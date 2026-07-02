package com.argesurec.shared.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import com.argesurec.shared.ui.theme.ArgepColors
import com.argesurec.shared.util.strings
import com.argesurec.shared.util.UiState
import com.argesurec.shared.viewmodel.TeamViewModel
import com.argesurec.shared.ui.team.TeamMemberTableRow
import com.argesurec.shared.ui.team.InviteMemberDialog
import com.argesurec.shared.ui.team.DeleteMemberDialog
import com.argesurec.shared.ui.team.EditRoleDialog
import com.argesurec.shared.util.ProjectRole
import com.argesurec.shared.model.TeamMemberWithProfile

class OrganizationTeamScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val s = strings
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<TeamViewModel>()
        val state by viewModel.state.collectAsState()
        val isActionLoading by viewModel.isActionLoading.collectAsState()
        val actionMessage by viewModel.actionMessage.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        var showInviteDialog by remember { mutableStateOf(false) }
        var memberToDelete by remember { mutableStateOf<TeamMemberWithProfile?>(null) }
        var memberToEditRole by remember { mutableStateOf<TeamMemberWithProfile?>(null) }

        LaunchedEffect(Unit) {
            viewModel.loadTeam()
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
                    viewModel.inviteMember(email, role)
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

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = ArgepColors.Slate50,
            topBar = {
                TopAppBar(
                    title = { Text(s.teamManagement, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showInviteDialog = true },
                    containerColor = ArgepColors.Navy900,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, null)
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp)) {
                when (val uiState = state) {
                    is UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    is UiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(uiState.message) }
                    is UiState.Success -> {
                        val members = uiState.data.members
                        
                        if (members.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(s.noResultsFound, color = ArgepColors.Slate400)
                            }
                        } else {
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(16.dp),
                                shadowElevation = 2.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                LazyColumn {
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
