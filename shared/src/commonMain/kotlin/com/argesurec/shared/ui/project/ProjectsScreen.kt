package com.argesurec.shared.ui.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.argesurec.shared.model.*
import com.argesurec.shared.ui.components.EmptyState
import com.argesurec.shared.ui.components.ErrorScreen
import com.argesurec.shared.ui.components.LoadingScreen
import com.argesurec.shared.ui.theme.ArgepColors
import com.argesurec.shared.util.UiState
import com.argesurec.shared.viewmodel.ProjectsViewModel
import com.argesurec.shared.util.strings

class ProjectsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val s = strings
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<ProjectsViewModel>()
        val state by viewModel.state.collectAsState()
        
        val allLabel = s.all
        var selectedFilter by remember { mutableStateOf(allLabel) }
        val filters = listOf(allLabel, s.incubation, s.development, s.commercialization)
        var showCreateDialog by remember { mutableStateOf(false) }
        
        val snackbarHostState = remember { SnackbarHostState() }
        val actionMessage by viewModel.actionMessage.collectAsState()

        LaunchedEffect(actionMessage) {
            actionMessage?.let {
                snackbarHostState.showSnackbar(it)
                viewModel.clearActionMessage()
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = ArgepColors.Slate50,
            contentWindowInsets = WindowInsets(0.dp), // Disable default safe insets to allow edge-to-edge
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = ArgepColors.ChartBlue,
                    contentColor = ArgepColors.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text(s.new_short) }
                )
            }
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
                                Text(s.projects.uppercase(), style = MaterialTheme.typography.labelSmall, color = ArgepColors.Navy300, letterSpacing = 1.sp)
                                Text(s.projectManagement, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = ArgepColors.White)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                            FilterChips(filters, selectedFilter) { selectedFilter = it }
                        }
                    }
                }

                when (val uiState = state) {
                    is UiState.Loading -> LoadingScreen(s.loadingProjects)
                    is UiState.Error -> ErrorScreen(uiState.message, onRetry = { viewModel.loadProjects(force = true) })
                    is UiState.Success -> {
                        val allProjects = uiState.data.projects
                        val filteredProjects = if (selectedFilter == s.all) {
                            allProjects
                        } else {
                            allProjects.filter { projectWithTeam ->
                                val project = projectWithTeam.toProject()
                                val enumName = when (project.phase) {
                                    com.argesurec.shared.model.ProjectPhase.INCUBATION -> s.incubation
                                    com.argesurec.shared.model.ProjectPhase.DEVELOPMENT -> s.development
                                    com.argesurec.shared.model.ProjectPhase.COMMERCIALIZATION -> s.commercialization
                                }
                                enumName == selectedFilter
                            }
                        }

                        if (filteredProjects.isEmpty()) {
                            EmptyState(s.noProjectsFound)
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 340.dp),
                                contentPadding = PaddingValues(32.dp),
                                horizontalArrangement = Arrangement.spacedBy(24.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredProjects) { project ->
                                    PremiumProjectCard(project) {
                                        project.id?.let { id ->
                                            navigator.push(ProjectDetailScreen(id))
                                        }
                                    }
                                }
                                
                                item {
                                    NewProjectPlaceholderCard(onClick = { showCreateDialog = true })
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (showCreateDialog) {
            CreateProjectDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, desc, phase, total, spent, start, end ->
                    viewModel.createProject(name, desc, phase, total, spent, start, end)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun FilterChips(filters: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(modifier = Modifier.padding(horizontal = 0.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        filters.forEach { filter ->
            val isActive = selected == filter
            Surface(
                modifier = Modifier.clickable { onSelect(filter) },
                color = if (isActive) ArgepColors.ChartBlue else Color.Transparent,
                shape = RoundedCornerShape(10.dp),
                border = if (!isActive) androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.Navy700) else null
            ) {
                Text(
                    text = filter,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                        color = if (isActive) Color.White else ArgepColors.Navy300
                    )
                )
            }
        }
    }
}

@Composable
fun PremiumProjectCard(projectWithTeam: ProjectWithTeam, onClick: () -> Unit) {
    val project = projectWithTeam.toProject()
    val phaseName = when (project.phase) {
        com.argesurec.shared.model.ProjectPhase.INCUBATION -> strings.incubation
        com.argesurec.shared.model.ProjectPhase.DEVELOPMENT -> strings.development
        com.argesurec.shared.model.ProjectPhase.COMMERCIALIZATION -> strings.commercialization
        else -> strings.unknown
    }

    val phaseColor = when (project.phase) {
        com.argesurec.shared.model.ProjectPhase.INCUBATION -> ArgepColors.ChartAmber
        com.argesurec.shared.model.ProjectPhase.DEVELOPMENT -> ArgepColors.ChartBlue
        com.argesurec.shared.model.ProjectPhase.COMMERCIALIZATION -> ArgepColors.ChartEmerald
        else -> ArgepColors.Navy500
    }
    
    val phaseBg = phaseColor.copy(alpha = 0.1f)

    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(project.name, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = ArgepColors.Navy900)
                    Text(project.description ?: strings.noDescription, style = MaterialTheme.typography.bodyMedium, color = ArgepColors.Slate500, maxLines = 1)
                }
                Surface(color = phaseBg, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        phaseName.uppercase(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                        color = phaseColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Modern Progress Section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.SpaceBetween, 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.progress, 
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), 
                        color = ArgepColors.Navy700
                    )
                    Text(
                        text = "45%", 
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold), 
                        color = ArgepColors.Navy900
                    )
                }
                LinearProgressIndicator(
                    progress = 0.45f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = phaseColor,
                    trackColor = phaseBg,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Stats Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(strings.budget, style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate400)
                    Text("${project.budgetTotal ?: 0.0} ${strings.currencySymbol}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = ArgepColors.Navy800)
                }
                
                // Team Avatars
                Row(horizontalArrangement = Arrangement.spacedBy((-10).dp), verticalAlignment = Alignment.CenterVertically) {
                    projectWithTeam.members.take(3).forEach { member ->
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = CircleShape,
                            color = ArgepColors.Slate100,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    (member.profile?.fullName ?: "?").take(1).uppercase(),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ArgepColors.Navy700
                                )
                            }
                        }
                    }
                    if (projectWithTeam.members.size > 3) {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = CircleShape,
                            color = ArgepColors.Navy900,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "+${projectWithTeam.members.size - 3}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewProjectPlaceholderCard(onClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable { onClick() },
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, ArgepColors.Slate200)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                color = ArgepColors.Slate100,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = ArgepColors.Navy700, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(strings.newProject, style = MaterialTheme.typography.titleMedium, color = ArgepColors.Navy900, fontWeight = FontWeight.ExtraBold)
            Text(strings.startArgeProcess, style = MaterialTheme.typography.bodySmall, color = ArgepColors.Slate500)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?, com.argesurec.shared.model.ProjectPhase, Double, Double, String?, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var budgetTotal by remember { mutableStateOf("") }
    var budgetSpent by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var selectedPhase by remember { mutableStateOf(com.argesurec.shared.model.ProjectPhase.DEVELOPMENT) }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(0.95f),
        content = {
            Surface(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                color = ArgepColors.White
            ) {
                Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                    Text(
                        strings.startNewProject,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = ArgepColors.Navy900
                    )
                    Text(strings.enterProjectDetails, style = MaterialTheme.typography.bodySmall, color = ArgepColors.Slate500)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(strings.projectName, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            placeholder = { Text(strings.projectNamePlaceholder, fontSize = 14.sp) }
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text(strings.projectDescription, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(10.dp)
                        )
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = budgetTotal, 
                                onValueChange = { if(it.all { c -> c.isDigit() || c == '.' }) budgetTotal = it }, 
                                label = { Text(strings.totalBudget, fontSize = 11.sp) }, 
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = budgetSpent, 
                                onValueChange = { if(it.all { c -> c.isDigit() || c == '.' }) budgetSpent = it }, 
                                label = { Text(strings.currentSpending, fontSize = 11.sp) }, 
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = startDate, 
                                onValueChange = { startDate = it }, 
                                label = { Text(strings.startDate, fontSize = 11.sp) }, 
                                placeholder = { Text("YYYY-MM-DD", fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = endDate, 
                                onValueChange = { endDate = it }, 
                                label = { Text(strings.endDate, fontSize = 11.sp) }, 
                                placeholder = { Text("YYYY-MM-DD", fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Text(strings.projectPhase, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = ArgepColors.Navy700)
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            com.argesurec.shared.model.ProjectPhase.entries.forEach { phase ->
                                val isSelected = selectedPhase == phase
                                Surface(
                                    modifier = Modifier.weight(1f).clickable { selectedPhase = phase },
                                    color = if (isSelected) ArgepColors.Navy700 else ArgepColors.Slate50,
                                    shape = RoundedCornerShape(8.dp),
                                    border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.Slate200) else null
                                ) {
                                    Box(modifier = Modifier.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            when(phase) {
                                                com.argesurec.shared.model.ProjectPhase.INCUBATION -> strings.incubation
                                                com.argesurec.shared.model.ProjectPhase.DEVELOPMENT -> strings.development
                                                com.argesurec.shared.model.ProjectPhase.COMMERCIALIZATION -> strings.commercialization
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else ArgepColors.Slate600
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(strings.cancel, color = ArgepColors.Slate500)
                        }
                        Button(
                            onClick = { 
                                onCreate(
                                    name, 
                                    description, 
                                    selectedPhase, 
                                    budgetTotal.toDoubleOrNull() ?: 0.0, 
                                    budgetSpent.toDoubleOrNull() ?: 0.0,
                                    if(startDate.isNotBlank()) startDate else null,
                                    if(endDate.isNotBlank()) endDate else null
                                ) 
                            },
                            modifier = Modifier.weight(1.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = ArgepColors.Navy700),
                            shape = RoundedCornerShape(10.dp),
                            enabled = name.isNotBlank()
                        ) {
                            Text(strings.createProjectButton, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    )
}
