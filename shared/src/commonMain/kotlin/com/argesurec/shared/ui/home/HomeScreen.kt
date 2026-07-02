package com.argesurec.shared.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.json.jsonPrimitive
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.viewmodel.koinViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.argesurec.shared.ui.components.*
import com.argesurec.shared.ui.project.TaskDetailScreen
import com.argesurec.shared.ui.theme.ArgepColors
import com.argesurec.shared.util.UiState
import com.argesurec.shared.viewmodel.AuthViewModel
import com.argesurec.shared.viewmodel.ProjectsViewModel
import com.argesurec.shared.viewmodel.ProjectsData
import com.argesurec.shared.ui.subscription.PaywallScreen
import com.argesurec.shared.viewmodel.TaskViewModel
import com.argesurec.shared.viewmodel.TaskData
import com.argesurec.shared.util.isWeb
import com.argesurec.shared.util.strings

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val s = strings
        val navigator = LocalNavigator.currentOrThrow
        val authViewModel = koinViewModel<AuthViewModel>()
        val taskViewModel = koinViewModel<TaskViewModel>()
        val projectsViewModel = koinViewModel<ProjectsViewModel>()
        
        val authState by authViewModel.state.collectAsState()
        val taskUiState by taskViewModel.state.collectAsState()
        val projectsUiState by projectsViewModel.state.collectAsState()

        val userName = authState.currentUser?.userMetadata?.get("full_name")?.jsonPrimitive?.content ?: s.user

        LaunchedEffect(Unit) {
            taskViewModel.loadAssignedTasks()
            projectsViewModel.loadProjects()
        }

        if (isWeb) {
            ExecutiveDashboard(
                userName = userName,
                taskUiState = taskUiState,
                projectsUiState = projectsUiState,
                onTaskClick = { id -> navigator.push(TaskDetailScreen(id)) }
            )
        } else {
            MobileDashboard(
                userName = userName,
                taskUiState = taskUiState,
                projectsUiState = projectsUiState
            )
        }
    }
}

@Composable
fun ExecutiveDashboard(
    userName: String,
    taskUiState: UiState<TaskData>,
    projectsUiState: UiState<ProjectsData>,
    onTaskClick: (String) -> Unit
) {
    val s = strings
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ArgepColors.ExecutiveBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // PROFESSIONAL NAVY HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(ArgepColors.Navy900, ArgepColors.ChartBlue.copy(alpha = 0.8f))
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(horizontal = 48.dp, vertical = 64.dp)
        ) {
            Column {
                Text(
                    text = s.executiveDashboard.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                    color = ArgepColors.Navy300
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = s.ecosystemPerformance,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ArgepColors.White
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ExecutiveButton(s.export, onClick = {})
                        ExecutiveButton(s.newInitiative, onClick = {})
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 48.dp)) {
            Spacer(modifier = Modifier.height(-32.dp)) // Pull stats up into the header slightly

            // Stat Cards Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                val activePrj = (projectsUiState as? UiState.Success<ProjectsData>)?.data?.activeProjectsCount ?: 0
                val pendingTsk = (taskUiState as? UiState.Success<TaskData>)?.data?.pendingTasksCount ?: 0
                val completedTsk = (taskUiState as? UiState.Success<TaskData>)?.data?.completedTasksCount ?: 0

                ExecutiveStatCard(s.activeProjects, activePrj.toString(), "↑ 12%", Icons.Default.CheckCircle, ArgepColors.ChartBlue, ArgepColors.ChartBlueBg, modifier = Modifier.weight(1f))
                ExecutiveStatCard(s.pendingTasks, pendingTsk.toString(), s.critical, Icons.Default.DateRange, ArgepColors.ChartAmber, ArgepColors.ChartAmberBg, modifier = Modifier.weight(1f))
                ExecutiveStatCard(s.completed, completedTsk.toString(), s.success_label, Icons.Default.Check, ArgepColors.ChartEmerald, ArgepColors.ChartEmeraldBg, modifier = Modifier.weight(1f))
                ExecutiveStatCard(s.atRisk, "03", "↓ 5%", Icons.Default.Warning, ArgepColors.ChartRose, ArgepColors.ChartRoseBg, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Content Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(48.dp)) {
                // Left: Tasks List
                Column(modifier = Modifier.weight(0.60f)) {
                    Text(
                        s.activeProjects,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    when (val uiState = projectsUiState) {
                        is UiState.Success<ProjectsData> -> {
                            uiState.data.projects.take(4).forEach { project ->
                                ExecutiveProjectRow(project.name, project.phase.name, 0.65f)
                            }
                        }
                        else -> Box(Modifier.fillMaxWidth().height(100.dp))
                    }
                }

                // Right: Activities
                Column(modifier = Modifier.weight(0.40f)) {
                    Text(
                        s.recentActivities,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    DashboardSectionCard(title = s.timeline) {
                        // Activity Timeline Logic
                        ActivityItem(s.newInitiative, "2 hours ago", ArgepColors.ChartBlue)
                        ActivityItem(s.success_label, "5 hours ago", ArgepColors.ChartEmerald)
                        ActivityItem(s.critical, "Yesterday", ArgepColors.ChartRose)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@Composable
fun ActivityItem(title: String, time: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(8.dp).background(color, RoundedCornerShape(4.dp)))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            Text(time, style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate500)
        }
    }
}

@Composable
fun ExecutiveNavItem(label: String, active: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        color = if (active) ArgepColors.ExecutiveSecondary.copy(alpha = 0.2f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            label,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.titleMedium,
            color = if (active) ArgepColors.White else ArgepColors.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun MobileDashboard(
    userName: String,
    taskUiState: UiState<TaskData>,
    projectsUiState: UiState<ProjectsData>
) {
    val s = strings
    val navigator = LocalNavigator.currentOrThrow
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ArgepColors.Slate50)
            .verticalScroll(rememberScrollState())
    ) {
        // Mobile Hero Header (Integrated with Top Bar)
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
                // Header Titles
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            s.ecosystemPerformance.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = ArgepColors.Navy300,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            s.dashboard,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = ArgepColors.White
                        )
                    }
                    IconButton(onClick = { navigator.push(PaywallScreen()) }) {
                        Icon(Icons.Default.Star, contentDescription = "Premium", tint = ArgepColors.PremiumGold)
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp).offset(y = (-16).dp)) {
                val activePrj = (projectsUiState as? UiState.Success<ProjectsData>)?.data?.activeProjectsCount ?: 0
                val pendingTsk = (taskUiState as? UiState.Success<TaskData>)?.data?.pendingTasksCount ?: 0
                val completedTsk = (taskUiState as? UiState.Success<TaskData>)?.data?.completedTasksCount ?: 0
                
                // Stat Cards Grid (2x2)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ExecutiveStatCard(s.activeProjects, activePrj.toString(), "↑ 12%", Icons.Default.CheckCircle, ArgepColors.ChartBlue, ArgepColors.ChartBlueBg, modifier = Modifier.weight(1f))
                        ExecutiveStatCard(s.pendingTasks, pendingTsk.toString(), s.critical, Icons.Default.DateRange, ArgepColors.ChartAmber, ArgepColors.ChartAmberBg, modifier = Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ExecutiveStatCard(s.completed, completedTsk.toString(), s.success_label, Icons.Default.Check, ArgepColors.ChartEmerald, ArgepColors.ChartEmeraldBg, modifier = Modifier.weight(1f))
                        ExecutiveStatCard(s.atRisk, "03", "↓ 5%", Icons.Default.Warning, ArgepColors.ChartRose, ArgepColors.ChartRoseBg, modifier = Modifier.weight(1f))
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    DashboardSectionCard(title = s.assignedTasks) {
                        when (val uiState = taskUiState) {
                            is UiState.Loading -> Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                            is UiState.Error -> Text(uiState.message, color = ArgepColors.Error)
                            is UiState.Success<TaskData> -> {
                                uiState.data.assignedTasks.take(5).forEach { task ->
                                    PremiumTaskRow(task, onClick = { navigator.push(TaskDetailScreen(task.id!!)) })
                                }
                            }
                        }
                    }

                    DashboardSectionCard(title = s.projectProgress) {
                        when (val uiState = projectsUiState) {
                            is UiState.Loading -> Box(Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(modifier = Modifier.size(24.dp)) }
                            is UiState.Error -> Text(uiState.message, color = ArgepColors.Error)
                            is UiState.Success<ProjectsData> -> {
                                uiState.data.projects.take(3).forEach { project ->
                                    ExecutiveProjectRow(project.name, project.phase.name, 0.45f)
                                }
                            }
                        }
                    }
            }
        }
    }
}


@Composable
fun DashboardSectionCard(
    title: String,
    badge: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = ArgepColors.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = ArgepColors.ExecutivePrimary)
                if (badge != null) {
                    Surface(color = ArgepColors.ChartBlueBg, shape = RoundedCornerShape(8.dp)) {
                        Text(badge, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = ArgepColors.ChartBlue)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}
