package com.argesurec.shared.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.viewmodel.koinViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.argesurec.shared.model.Task
import com.argesurec.shared.model.TaskPriority
import com.argesurec.shared.model.TaskStatus
import com.argesurec.shared.ui.components.EmptyState
import com.argesurec.shared.ui.components.ErrorScreen
import com.argesurec.shared.ui.components.LoadingScreen
import com.argesurec.shared.ui.theme.ArgepColors
import com.argesurec.shared.util.UiState
import com.argesurec.shared.viewmodel.TaskViewModel
import com.argesurec.shared.viewmodel.TeamViewModel
import com.argesurec.shared.ui.project.AddTaskDialog
import com.argesurec.shared.ui.project.TaskDetailScreen
import com.argesurec.shared.util.strings

class TasksScreen(private val milestoneId: String? = null) : Screen {
    @Composable
    override fun Content() {
        val s = strings
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<TaskViewModel>()
        val teamViewModel = koinViewModel<TeamViewModel>()
        
        val state by viewModel.state.collectAsState()
        val teamState by teamViewModel.state.collectAsState()
        var showAddTaskDialog by remember { mutableStateOf(false) }

        LaunchedEffect(milestoneId) {
            if (milestoneId != null) {
                viewModel.loadTasks(milestoneId)
                val proId = viewModel.getProjectIdForTask(milestoneId)
                proId?.let { teamViewModel.loadTeamForProject(it) }
            } else {
                viewModel.loadAllTasks()
            }
        }

        if (showAddTaskDialog && milestoneId != null) {
            val teamMembers = (teamState as? UiState.Success)?.data?.members ?: emptyList()
            AddTaskDialog(
                teamMembers = teamMembers,
                onDismiss = { showAddTaskDialog = false },
                onConfirm = { title, description, priority, assignedTo ->
                    viewModel.createTask(milestoneId, title, description, priority, assignedTo)
                    showAddTaskDialog = false
                }
            )
        }

        Column(modifier = Modifier.fillMaxSize().background(ArgepColors.Slate50)) {
            // Executive Header
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (milestoneId != null) {
                                Surface(
                                    onClick = { navigator.pop() },
                                    color = ArgepColors.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null,
                                        tint = ArgepColors.White,
                                        modifier = Modifier.padding(8.dp).size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                            
                            Column {
                                Text(
                                    if (milestoneId != null) s.kanbanBoard else s.allTasks, 
                                    style = MaterialTheme.typography.titleLarge, 
                                    fontWeight = FontWeight.Bold, 
                                    color = ArgepColors.White
                                )
                                Text(
                                    if (milestoneId != null) s.milestoneTasks else s.organizationWideTasks, 
                                    style = MaterialTheme.typography.labelSmall, 
                                    color = ArgepColors.White.copy(alpha = 0.6f)
                                )
                            }
                        }
                        
                        if (milestoneId != null) {
                            Surface(
                                onClick = { showAddTaskDialog = true },
                                color = ArgepColors.ChartAmber,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp), tint = ArgepColors.Navy900)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(s.addTask, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ArgepColors.Navy900)
                                }
                            }
                        }
                    }
                }
            }

            when (val uiState = state) {
                is UiState.Loading -> LoadingScreen(s.loadingTasks)
                is UiState.Error -> ErrorScreen(uiState.message, onRetry = { 
                    if (milestoneId != null) viewModel.loadTasks(milestoneId) else viewModel.loadAllTasks() 
                })
                is UiState.Success -> {
                    val tasks = if (milestoneId != null) uiState.data.tasks else uiState.data.allTasks
                    
                    if (tasks.isEmpty()) {
                        EmptyState(s.noTasksFound)
                    } else {
                        KanbanBoard(tasks, onTaskClick = { task ->
                            navigator.push(TaskDetailScreen(task.id!!))
                        }, onAddTaskClick = { 
                            if (milestoneId != null) showAddTaskDialog = true 
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun KanbanBoard(tasks: List<Task>, onTaskClick: (Task) -> Unit, onAddTaskClick: () -> Unit) {
    val s = strings
    val statuses = listOf(s.todo, s.inProgress, s.done)
    
    Row(
        modifier = Modifier.fillMaxSize().horizontalScroll(rememberScrollState()).padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.width(4.dp))
        statuses.forEach { status ->
            val targetEnum = when (status) {
                s.todo -> TaskStatus.TODO
                s.inProgress -> TaskStatus.IN_PROGRESS
                s.done -> TaskStatus.DONE
                else -> TaskStatus.TODO
            }
            KanbanColumn(
                title = status,
                tasks = tasks.filter { it.status == targetEnum },
                onTaskClick = onTaskClick,
                onAddTaskClick = onAddTaskClick,
                modifier = Modifier.width(280.dp)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
    }
}

@Composable
fun KanbanColumn(
    title: String,
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onAddTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val s = strings
    val (accentColor, _) = when (title) {
        s.todo -> ArgepColors.ChartAmber to ArgepColors.ChartAmber.copy(alpha = 0.05f)
        s.inProgress -> ArgepColors.ChartBlue to ArgepColors.ChartBlue.copy(alpha = 0.05f)
        s.done -> ArgepColors.ChartEmerald to ArgepColors.ChartEmerald.copy(alpha = 0.05f)
        else -> ArgepColors.Slate500 to ArgepColors.Slate100
    }

    Column(modifier = modifier) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = ArgepColors.White,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.Slate100)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(androidx.compose.foundation.shape.CircleShape).background(accentColor))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ArgepColors.Navy900)
                    }
                    Surface(color = ArgepColors.Slate50, shape = RoundedCornerShape(8.dp)) {
                        Text(
                            text = tasks.size.toString(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ArgepColors.Slate700
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    tasks.forEach { task ->
                        KanbanTaskCard(task) { onTaskClick(task) }
                    }
                    
                    if (title == s.todo) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(48.dp).clickable { onAddTaskClick() },
                            color = Color.Transparent,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.Slate200)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("+ ${s.addTask}", style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate400, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KanbanTaskCard(task: Task, onClick: () -> Unit) {
    val (priorityColor, priorityBg) = when (task.priority) {
        TaskPriority.HIGH -> ArgepColors.Error to ArgepColors.Error.copy(alpha = 0.1f)
        TaskPriority.MEDIUM -> ArgepColors.ChartAmber to ArgepColors.ChartAmber.copy(alpha = 0.1f)
        TaskPriority.LOW -> ArgepColors.ChartEmerald to ArgepColors.ChartEmerald.copy(alpha = 0.1f)
    }

    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        color = ArgepColors.White,
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.Slate50)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = priorityBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        task.priority.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = priorityColor
                    )
                }
                Text(task.createdAt?.take(10) ?: "", style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate400, fontSize = 10.sp)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(task.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ArgepColors.Navy900)
            
            if (task.description != null) {
                Text(task.description, style = MaterialTheme.typography.bodySmall, color = ArgepColors.Slate500, maxLines = 2, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(24.dp), shape = androidx.compose.foundation.shape.CircleShape, color = ArgepColors.Navy100) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(task.assignedTo?.take(1) ?: "?", color = ArgepColors.Navy900, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(task.assignedTo ?: strings.unassigned, style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate500)
            }
        }
    }
}
