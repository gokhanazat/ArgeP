package com.argesurec.shared.ui.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.argesurec.shared.model.Task
import com.argesurec.shared.model.TaskPriority
import com.argesurec.shared.model.TaskStatus
import com.argesurec.shared.ui.theme.ArgepColors
import com.argesurec.shared.util.UiState
import com.argesurec.shared.viewmodel.TaskViewModel
import com.argesurec.shared.viewmodel.TeamViewModel
import com.argesurec.shared.util.AppStrings
import com.argesurec.shared.util.strings

class TaskDetailScreen(private val taskId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<TaskViewModel>()
        val teamViewModel = koinViewModel<TeamViewModel>()
        
        val state by viewModel.detailState.collectAsState()
        val teamState by teamViewModel.state.collectAsState()

        val task = (state as? UiState.Success)?.data
        var showAssignDropdown by remember { mutableStateOf(false) }

        LaunchedEffect(taskId) {
            viewModel.loadTaskDetail(taskId)
        }

        LaunchedEffect(task?.milestoneId) {
            val milestoneId = task?.milestoneId ?: return@LaunchedEffect
            val proId = viewModel.getProjectIdForTask(milestoneId)
            proId?.let { teamViewModel.loadTeamForProject(it) }
        }

        Column(modifier = Modifier.fillMaxSize().background(ArgepColors.Slate50)) {
            // Executive Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ArgepColors.Navy900,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                            Column {
                                Text(strings.taskDetail, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = ArgepColors.White)
                                Text("PRO-102 · ${strings.taskIdLabel}: ${taskId.take(8)}", style = MaterialTheme.typography.labelSmall, color = ArgepColors.White.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }

            if (task == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                    CircularProgressIndicator(color = ArgepColors.ChartAmber) 
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Main Content Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ArgepColors.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.Slate100)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                SelectionPriorityBadge(task.priority)
                                Surface(color = ArgepColors.Slate100, shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        strings.softwareLabel, 
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), 
                                        style = MaterialTheme.typography.labelSmall, 
                                        fontWeight = FontWeight.Bold,
                                        color = ArgepColors.Slate600
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(task.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = ArgepColors.Navy900)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(task.description ?: strings.noTaskDescription, style = MaterialTheme.typography.bodyLarge, color = ArgepColors.Slate700, lineHeight = 24.sp)
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(strings.updateStatus, style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate400, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                StatusButton(strings.todo, task.status == TaskStatus.TODO, ArgepColors.ChartAmber) {
                                    viewModel.updateTaskStatus(task.id!!, task.milestoneId, TaskStatus.TODO)
                                }
                                StatusButton(strings.inProgress, task.status == TaskStatus.IN_PROGRESS, ArgepColors.ChartBlue) {
                                    viewModel.updateTaskStatus(task.id!!, task.milestoneId, TaskStatus.IN_PROGRESS)
                                }
                                StatusButton(strings.done, task.status == TaskStatus.DONE, ArgepColors.ChartEmerald) {
                                    viewModel.updateTaskStatus(task.id!!, task.milestoneId, TaskStatus.DONE)
                                }
                            }
                        }
                    }

                    // Meta Info Card
                    TaskInfoCard(strings.taskDetailsLabel) {
                        TaskDetailRow(strings.assignedTo, {
                            val assignedMember = (teamState as? UiState.Success)?.data?.members?.find { it.userId == task.assignedTo }
                            
                            Box {
                                Row(
                                    modifier = Modifier.clickable { showAssignDropdown = true },
                                    verticalAlignment = Alignment.CenterVertically, 
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(modifier = Modifier.size(28.dp), color = ArgepColors.Navy100, shape = CircleShape) {
                                        Box(contentAlignment = Alignment.Center) { 
                                            Text(assignedMember?.profile?.fullName?.take(1) ?: "?", color = ArgepColors.Navy900, fontSize = 12.sp, fontWeight = FontWeight.Bold) 
                                        }
                                    }
                                    Text(assignedMember?.profile?.fullName ?: strings.unassigned, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ArgepColors.Navy900)
                                    Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(20.dp), tint = ArgepColors.Slate500)
                                }

                                DropdownMenu(
                                    expanded = showAssignDropdown,
                                    onDismissRequest = { showAssignDropdown = false },
                                    modifier = Modifier.background(ArgepColors.White)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(strings.removeAssignment, color = ArgepColors.Error) },
                                        onClick = {
                                            viewModel.updateTaskAssignment(task.id!!, null)
                                            showAssignDropdown = false
                                        }
                                    )
                                    if (teamState is UiState.Success) {
                                        (teamState as UiState.Success).data.members.forEach { member ->
                                            DropdownMenuItem(
                                                text = { Text(member.profile?.fullName ?: strings.unnamed) },
                                                onClick = {
                                                    viewModel.updateTaskAssignment(task.id!!, member.userId)
                                                    showAssignDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        })
                        TaskDetailRow(strings.endDate, { Text("24 June 2025", style = MaterialTheme.typography.bodyMedium, color = ArgepColors.Navy900) })
                        TaskDetailRow(strings.createdAt, { Text(task.createdAt?.take(10) ?: "-", style = MaterialTheme.typography.bodyMedium, color = ArgepColors.Navy900) })
                    }
                }
            }
        }
    }
}

@Composable
fun TaskInfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ArgepColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.Slate100)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ArgepColors.Navy900)
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
fun TaskDetailRow(label: String, valueContent: @Composable () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate400, modifier = Modifier.width(120.dp), fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        valueContent()
    }
}

@Composable
fun RowScope.StatusButton(label: String, isActive: Boolean, activeColor: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.height(44.dp).weight(1f).clickable { onClick() },
        color = if (isActive) activeColor else ArgepColors.White,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isActive) activeColor else ArgepColors.Slate200)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                label, 
                style = MaterialTheme.typography.labelSmall, 
                fontWeight = FontWeight.Bold,
                color = if (isActive) (if (activeColor == ArgepColors.ChartAmber) ArgepColors.Navy900 else ArgepColors.White) else ArgepColors.Slate600,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun SelectionPriorityBadge(priority: TaskPriority) {
    val (color, bgColor) = when (priority) {
        TaskPriority.HIGH -> ArgepColors.Error to ArgepColors.Error.copy(alpha = 0.1f)
        TaskPriority.MEDIUM -> ArgepColors.ChartAmber to ArgepColors.ChartAmber.copy(alpha = 0.1f)
        TaskPriority.LOW -> ArgepColors.ChartEmerald to ArgepColors.ChartEmerald.copy(alpha = 0.1f)
    }
    Surface(color = bgColor, shape = RoundedCornerShape(8.dp)) {
        Text(
            priority.name, 
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), 
            style = MaterialTheme.typography.labelSmall, 
            color = color,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
