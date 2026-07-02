package com.argesurec.shared.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.json.jsonPrimitive
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import com.argesurec.shared.ui.theme.ArgepColors
import com.argesurec.shared.util.strings
import com.argesurec.shared.viewmodel.AuthViewModel
import com.argesurec.shared.viewmodel.SettingsViewModel

class ProfileScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val s = strings
        val authViewModel = koinViewModel<AuthViewModel>()
        val settingsViewModel = koinViewModel<SettingsViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        
        val state by authViewModel.state.collectAsState()
        val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
        val user = state.currentUser
        val fullName = user?.userMetadata?.get("full_name")?.jsonPrimitive?.content ?: s.defaultUser

        var showEditDialog by remember { mutableStateOf(false) }
        var showSettingsDialog by remember { mutableStateOf(false) }
        var showAboutDialog by remember { mutableStateOf(false) }
        var editName by remember { mutableStateOf("") }
        var editDept by remember { mutableStateOf("") }

        val departments = listOf(
            s.deptSoftware,
            s.deptHardware,
            s.deptMechanical,
            s.deptEmbedded,
            s.deptProject,
            s.deptData,
            s.deptQuality
        )

        // Dialogs
        if (showEditDialog) {
            ProfileEditDialog(
                currentName = editName,
                currentDept = editDept,
                departments = departments,
                onDismiss = { showEditDialog = false },
                onSave = { name, dept ->
                    authViewModel.updateProfile(name, dept)
                    showEditDialog = false
                }
            )
        }

        if (showSettingsDialog) {
            SettingsDialog(
                isDarkMode = isDarkMode,
                onDarkModeChange = { settingsViewModel.toggleDarkMode() },
                onDismiss = { showSettingsDialog = false }
            )
        }

        if (showAboutDialog) {
            AboutDialog(onDismiss = { showAboutDialog = false })
        }

        Scaffold(
            containerColor = ArgepColors.Slate100,
            contentWindowInsets = WindowInsets(0.dp)
        ) { padding ->
            Column(modifier = Modifier.padding(bottom = padding.calculateBottomPadding()).fillMaxSize()) {
                // EXECUTIVE HEADER
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
                    Row(
                        modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 32.dp).padding(top = 12.dp, bottom = 40.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(s.profile.uppercase(), style = MaterialTheme.typography.labelSmall, color = ArgepColors.Navy300, letterSpacing = 1.sp)
                            Text(s.accountSettings, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = ArgepColors.White)
                        }
                        
                        IconButton(
                            onClick = { authViewModel.signOut() },
                            modifier = Modifier.background(ArgepColors.Error.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = ArgepColors.Error)
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                // User Profile Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        Surface(modifier = Modifier.size(80.dp), shape = CircleShape, color = ArgepColors.Navy100) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(fullName.take(1), color = ArgepColors.Navy900, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                        Column {
                            Text(fullName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text(user?.email ?: s.defaultEmail, style = MaterialTheme.typography.bodyLarge, color = ArgepColors.Slate500)
                            state.organization?.let { org ->
                                Text(org.name, style = MaterialTheme.typography.bodyMedium, color = ArgepColors.Navy700, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(color = ArgepColors.Phase3Light, shape = RoundedCornerShape(20.dp)) {
                                val roleText = when(state.userProfile?.role) {
                                    "owner" -> strings.owner
                                    "manager" -> strings.manager
                                    else -> s.activeMember
                                }
                                Text(roleText, modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = ArgepColors.Phase3, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Settings Sections
                SettingsSection(s.personalInfo) {
                    SettingsRow(Icons.Default.Person, s.profileInfo, s.profileInfoSubtitle) {
                        editName = fullName
                        editDept = "" 
                        showEditDialog = true
                    }
                    
                    // Admin/Owner Section
                    if (state.userProfile?.role == "owner" || state.userProfile?.role == "manager") {
                        SettingsRow(Icons.Default.Group, s.teamManagement, strings.manageOrganizationTeam) {
                            navigator.push(OrganizationTeamScreen())
                        }
                    }

                    SettingsRow(Icons.Default.Settings, s.appPreferences, s.appPreferencesSubtitle) {
                        showSettingsDialog = true
                    }
                    SettingsRow(Icons.Default.Info, s.about, s.aboutSubtitle) {
                        showAboutDialog = true
                    }
                    SettingsRow(Icons.Default.Help, s.userGuide, s.guideSubtitle) {
                        navigator.push(GuideScreen())
                    }
                }
            }
        }
    }
}
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title.uppercase(), style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate500, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(modifier = Modifier.size(40.dp), color = ArgepColors.Navy50, shape = RoundedCornerShape(8.dp)) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = ArgepColors.Navy700, modifier = Modifier.size(20.dp)) }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = ArgepColors.Slate500)
        }
        Icon(Icons.Default.KeyboardArrowRight, null, tint = ArgepColors.Slate300, modifier = Modifier.size(20.dp))
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = ArgepColors.Slate100)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditDialog(
    currentName: String,
    currentDept: String,
    departments: List<String>,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var dept by remember { mutableStateOf(currentDept) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.editProfile, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(strings.fullNameLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = dept,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(strings.department) },
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
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        departments.forEach { d ->
                            DropdownMenuItem(
                                text = { Text(d) },
                                onClick = {
                                    dept = d
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
                onClick = { onSave(name, dept) },
                colors = ButtonDefaults.buttonColors(containerColor = ArgepColors.Navy700),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(strings.save)
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
fun SettingsDialog(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.appPreferences, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(strings.darkMode, style = MaterialTheme.typography.titleMedium)
                        Text(strings.darkModeSubtitle, style = MaterialTheme.typography.bodySmall, color = ArgepColors.Slate500)
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = onDarkModeChange,
                        colors = SwitchDefaults.colors(checkedThumbColor = ArgepColors.Navy700)
                    )
                }
                
                HorizontalDivider(color = ArgepColors.Slate100)
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(strings.notifications, style = MaterialTheme.typography.titleMedium)
                        Text(strings.notificationsSubtitle, style = MaterialTheme.typography.bodySmall, color = ArgepColors.Slate500)
                    }
                    Switch(checked = true, onCheckedChange = {}, enabled = false)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(strings.close) }
        },
        containerColor = Color.White
    )
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.about, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(modifier = Modifier.size(64.dp), color = ArgepColors.Navy100, shape = RoundedCornerShape(12.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("AG", color = ArgepColors.Navy900, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                    }
                }
                Text(strings.appName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(strings.appVersion, style = MaterialTheme.typography.bodyMedium, color = ArgepColors.Slate500)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    strings.appDescription,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(strings.copyright, style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate400)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(strings.understood) }
        },
        containerColor = Color.White
    )
}
