package com.argesurec.shared.ui.team

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.viewmodel.koinViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.argesurec.shared.model.UserProfile
import com.argesurec.shared.model.TeamMemberWithProfile
import com.argesurec.shared.ui.components.EmptyState
import com.argesurec.shared.ui.components.ErrorScreen
import com.argesurec.shared.ui.components.LoadingScreen
import com.argesurec.shared.ui.project.PhaseBadge
import com.argesurec.shared.ui.theme.ArgepColors
import com.argesurec.shared.util.ProjectRole
import com.argesurec.shared.util.UiState
import com.argesurec.shared.viewmodel.TeamViewModel
import com.argesurec.shared.util.strings

class TeamScreen(private val projectId: String?) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val s = strings
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<TeamViewModel>()
        val state by viewModel.state.collectAsState()
        val isActionLoading by viewModel.isActionLoading.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }
        val actionMessage by viewModel.actionMessage.collectAsState()

        var searchQuery by remember { mutableStateOf("") }
        var showInviteDialog by remember { mutableStateOf(false) }
        var memberToDelete by remember { mutableStateOf<TeamMemberWithProfile?>(null) }
        var memberToEditRole by remember { mutableStateOf<TeamMemberWithProfile?>(null) }

        LaunchedEffect(projectId) {
            if (projectId != null) {
                viewModel.loadTeamForProject(projectId)
            } else {
                viewModel.loadTeam()
            }
        }

        LaunchedEffect(actionMessage) {
            actionMessage?.let {
                snackbarHostState.showSnackbar(it)
                viewModel.clearActionMessage()
            }
        }

        if (showInviteDialog) {
            InviteMemberDialog(
                onDismiss = { showInviteDialog = false },
                onInvite = { email, role ->
                    projectId?.let { viewModel.inviteMember(email, role, it) }
                    showInviteDialog = false
                }
            )
        }

        if (memberToDelete != null) {
            DeleteMemberDialog(
                memberName = memberToDelete?.profile?.fullName ?: s.unnamed,
                onDismiss = { memberToDelete = null },
                onConfirm = {
                    viewModel.removeMember(memberToDelete!!.userId)
                    memberToDelete = null
                }
            )
        }

        if (memberToEditRole != null) {
            EditRoleDialog(
                member = memberToEditRole!!,
                onDismiss = { memberToEditRole = null },
                onConfirm = { newRole ->
                    projectId?.let { viewModel.updateMemberRole(memberToEditRole!!.id, it, newRole) }
                    memberToEditRole = null
                }
            )
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = ArgepColors.Slate50,
            contentWindowInsets = WindowInsets(0.dp)
        ) { padding ->
            Column(modifier = Modifier.padding(bottom = padding.calculateBottomPadding()).fillMaxSize()) {
                // PROFESSIONAL HEADER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(ArgepColors.Navy900, ArgepColors.ChartBlue.copy(alpha = 0.8f))
                            ),
                            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                        )
                ) {
                    Column(modifier = Modifier.statusBarsPadding().padding(bottom = 32.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(s.team.uppercase(), style = MaterialTheme.typography.labelSmall, color = ArgepColors.Navy300, letterSpacing = 1.sp)
                                Text(s.teamManagement, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = ArgepColors.White)
                            }
                            
                            if (projectId != null) {
                                Button(
                                    onClick = { if (!isActionLoading) showInviteDialog = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ArgepColors.ChartBlue),
                                    enabled = !isActionLoading
                                ) {
                                    if (isActionLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                    } else {
                                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(s.addNewMember)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Search Bar In Header Area
                        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Surface(
                                color = ArgepColors.Navy800,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(0.6f)
                            ) {
                                TextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text(s.searchMember, color = ArgepColors.Navy300) },
                                    leadingIcon = { Icon(Icons.Default.Search, null, tint = ArgepColors.Navy300) },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                when (val uiState = state) {
                    is UiState.Loading -> LoadingScreen(s.loadingTeam)
                    is UiState.Error -> ErrorScreen(uiState.message, onRetry = { viewModel.loadTeam() })
                    is UiState.Success -> {
                        val members = if (searchQuery.isEmpty()) {
                            uiState.data.members
                        } else {
                            uiState.data.members.filter { it.profile?.fullName?.contains(searchQuery, true) == true }
                        }
                        
                        Column(modifier = Modifier.fillMaxSize().padding(32.dp)) {
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(16.dp),
                                shadowElevation = 2.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    // Modern Table Header
                                    Row(
                                        modifier = Modifier.fillMaxWidth().background(ArgepColors.Slate50).padding(horizontal = 24.dp, vertical = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(s.memberPersonnel.uppercase(), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = ArgepColors.Slate500, modifier = Modifier.weight(2f))
                                        Text(s.role.uppercase(), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = ArgepColors.Slate500, modifier = Modifier.weight(1f))
                                        Text(s.joined.uppercase(), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = ArgepColors.Slate500, modifier = Modifier.weight(1f))
                                        Text(s.actions.uppercase(), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = ArgepColors.Slate500, modifier = Modifier.width(100.dp), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                                    }
                                    
                                    if (members.isEmpty()) {
                                        Box(modifier = Modifier.fillMaxWidth().padding(64.dp), contentAlignment = Alignment.Center) {
                                            Text(s.noResultsFound, color = ArgepColors.Slate400, style = MaterialTheme.typography.bodyLarge)
                                        }
                                    } else {
                                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                            items(members) { member ->
                                                TeamMemberTableRow(
                                                    member = member,
                                                    onEdit = { memberToEditRole = member },
                                                    onDelete = { memberToDelete = member }
                                                )
                                                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = ArgepColors.Slate100)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamMemberTableRow(
    member: TeamMemberWithProfile,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Name & Profile
        Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = ArgepColors.Slate100) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        member.profile?.fullName?.take(1)?.uppercase() ?: "?", 
                        color = ArgepColors.Navy900, 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            Column {
                Text(member.profile?.fullName ?: strings.unnamed, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = ArgepColors.Navy900)
                Text(member.profile?.department ?: strings.argeDepartment, style = MaterialTheme.typography.bodySmall, color = ArgepColors.Slate500)
            }
        }

        // Role Badge
        Box(modifier = Modifier.weight(1f)) {
            val roleEnum = try { ProjectRole.valueOf(member.role ?: "GOZLEMCI") } catch (e: Exception) { ProjectRole.GOZLEMCI }
            val roleColor = when (roleEnum) {
                ProjectRole.PROJE_MUDURU -> ArgepColors.ChartBlue
                ProjectRole.TEKNIK_LIDER -> ArgepColors.ChartAmber
                ProjectRole.ARGE_UZMANI -> Color(0xFF8B5CF6)
                ProjectRole.TEST_MUHENDISI -> ArgepColors.ChartEmerald
                ProjectRole.MALI_UZMAN -> ArgepColors.ChartRose
                else -> ArgepColors.Slate500
            }
            Surface(color = roleColor.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                Text(
                    member.role?.replace("_", " ") ?: "Üye", 
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), 
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), 
                    color = roleColor
                )
            }
        }

        // Joined Date
        val joinedDate = member.joinedAt.split("T").firstOrNull() ?: strings.unknown
        Text(joinedDate, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = ArgepColors.Slate600)

        // Actions
        Row(modifier = Modifier.width(100.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onEdit, modifier = Modifier.size(40.dp)) { 
                Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp), tint = ArgepColors.Slate400) 
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) { 
                Icon(Icons.Default.DeleteOutline, null, modifier = Modifier.size(18.dp), tint = ArgepColors.ChartRose.copy(alpha = 0.8f)) 
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteMemberDialog(
    onDismiss: () -> Unit,
    onInvite: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(ProjectRole.ARGE_UZMANI) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.addMemberToTeam, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(strings.enterEmailAndAssignRole, style = MaterialTheme.typography.bodySmall, color = ArgepColors.Slate500)
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(strings.emailAddress) },
                    placeholder = { Text(strings.emailPlaceholder) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
                    )
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedRole.name.replace("_", " "),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(strings.projectRole) },
                        modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                        shape = RoundedCornerShape(8.dp),
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        ProjectRole.entries.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role.name.replace("_", " ")) },
                                onClick = {
                                    selectedRole = role
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (email.isNotEmpty()) onInvite(email, selectedRole.name) },
                colors = ButtonDefaults.buttonColors(containerColor = ArgepColors.Navy700),
                shape = RoundedCornerShape(10.dp),
                enabled = email.isNotEmpty()
            ) {
                Text(strings.add)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = ArgepColors.Slate500)
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun DeleteMemberDialog(
    memberName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.removeMember, fontWeight = FontWeight.Bold) },
        text = { Text(strings.removeMemberConfirm(memberName), style = MaterialTheme.typography.bodyMedium, color = ArgepColors.Slate600) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = ArgepColors.Error),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(strings.removeMember)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = ArgepColors.Slate500)
            }
        },
        containerColor = Color.White
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRoleDialog(
    member: TeamMemberWithProfile,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedRole by remember { mutableStateOf(try { ProjectRole.valueOf(member.role ?: "ARGE_UZMANI") } catch (e: Exception) { ProjectRole.ARGE_UZMANI }) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.editRole, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(strings.selectNewRoleFor(member.profile?.fullName ?: strings.unnamed), style = MaterialTheme.typography.bodySmall, color = ArgepColors.Slate500)
                
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedRole.name.replace("_", " "),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(strings.newRole) },
                        modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                        shape = RoundedCornerShape(10.dp),
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ArgepColors.Navy200,
                            unfocusedBorderColor = ArgepColors.Slate200
                        )
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.7f).background(Color.White)
                    ) {
                        ProjectRole.entries.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role.name.replace("_", " "), style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    selectedRole = role
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedRole.name) },
                colors = ButtonDefaults.buttonColors(containerColor = ArgepColors.Navy700),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(strings.updateRole)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = ArgepColors.Slate500)
            }
        },
        containerColor = Color.White
    )
}
