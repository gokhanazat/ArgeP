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
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.viewmodel.koinViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.argesurec.shared.model.*
import com.argesurec.shared.ui.components.EmptyState
import com.argesurec.shared.ui.components.ErrorScreen
import com.argesurec.shared.ui.components.LoadingScreen
import com.argesurec.shared.ui.theme.ArgepColors
import com.argesurec.shared.viewmodel.ExpenseViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.argesurec.shared.util.ProjectRole
import com.argesurec.shared.util.UiState
import com.argesurec.shared.viewmodel.MilestoneViewModel
import com.argesurec.shared.viewmodel.MilestoneData
import com.argesurec.shared.viewmodel.ProjectsViewModel
import com.argesurec.shared.viewmodel.ProjectsData
import com.argesurec.shared.viewmodel.TeamViewModel
import com.argesurec.shared.viewmodel.TeamData
import com.argesurec.shared.util.strings

private fun formatDate(date: String?): String {
    if (date.isNullOrBlank()) return "-"
    val datePart = date.split("T")[0]
    val parts = datePart.split("-")
    return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else datePart
}

class ProjectDetailScreen(private val projectId: String) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        val projectsViewModel = koinViewModel<ProjectsViewModel>()
        val milestoneViewModel = koinViewModel<com.argesurec.shared.viewmodel.MilestoneViewModel>()
        val teamViewModel = koinViewModel<com.argesurec.shared.viewmodel.TeamViewModel>()
        val projectFilesViewModel = koinViewModel<com.argesurec.shared.viewmodel.ProjectFilesViewModel>()
        val expenseViewModel = koinViewModel<ExpenseViewModel>()

        val projectsState by projectsViewModel.state.collectAsState()
        val milestoneState by milestoneViewModel.state.collectAsState()
        val teamState by teamViewModel.state.collectAsState()
        val filesState by projectFilesViewModel.state.collectAsState()
        val actionState by projectFilesViewModel.actionState.collectAsState()
        val expenseState by expenseViewModel.state.collectAsState()

        var activeTab by remember { mutableStateOf(0) }
        var showEditDialog by remember { mutableStateOf(false) }
        var showAddMilestoneDialog by remember { mutableStateOf(false) }
        var showAddExpenseDialog by remember { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }
        
        val projectsActionMessage by projectsViewModel.actionMessage.collectAsState()
        val teamActionMessage by teamViewModel.actionMessage.collectAsState()
        val expenseActionMessage by expenseViewModel.actionMessage.collectAsState()

        val filePicker = com.argesurec.shared.util.rememberFilePicker { file ->
            file?.let { projectFilesViewModel.uploadFile(projectId, it) }
        }

        LaunchedEffect(projectId) {
            projectsViewModel.loadProjects()
            milestoneViewModel.loadMilestones(projectId)
            teamViewModel.loadTeamForProject(projectId)
            projectFilesViewModel.loadFiles(projectId)
            expenseViewModel.loadExpenses(projectId)
        }

        LaunchedEffect(projectsActionMessage) {
            projectsActionMessage?.let {
                snackbarHostState.showSnackbar(it)
                projectsViewModel.clearActionMessage()
            }
        }

        LaunchedEffect(teamActionMessage) {
            teamActionMessage?.let {
                snackbarHostState.showSnackbar(it)
                teamViewModel.clearActionMessage()
            }
        }

        LaunchedEffect(expenseActionMessage) {
            expenseActionMessage?.let {
                snackbarHostState.showSnackbar(it)
                expenseViewModel.clearActionMessage()
            }
        }

        // State'ten ilgili projeyi bul
        val projectWithTeam = (projectsState as? UiState.Success<ProjectsData>)?.data?.projects?.find { it.id == projectId }
        val project = projectWithTeam?.toProject()

        Scaffold(
            topBar = {
                Surface(
                    color = ArgepColors.Navy900,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        // Back Button & Parent Breadcrumb
                        Row(
                            modifier = Modifier
                                .clickable { navigator.pop() }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                null,
                                modifier = Modifier.size(16.dp),
                                tint = ArgepColors.Slate400
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                strings.projects.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = ArgepColors.Slate400,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = project?.name ?: strings.loadingProjects,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                if (project != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        PhaseBadge(project.phase)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        StatusBadge(project.status ?: strings.active)
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Button(
                                    onClick = { showEditDialog = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White.copy(alpha = 0.1f),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(strings.edit, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))

                                when (activeTab) {
                                    0 -> Button(
                                        onClick = { showAddMilestoneDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = ArgepColors.ChartBlue),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(strings.addMilestone, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                    1 -> Button(
                                        onClick = { filePicker.launch() },
                                        colors = ButtonDefaults.buttonColors(containerColor = ArgepColors.ChartEmerald),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp), tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(strings.uploadFile, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    2 -> Button(
                                        onClick = { showAddExpenseDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = ArgepColors.ChartAmber),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(strings.addExpense, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = ArgepColors.Slate100
        ) { padding ->
            when (val pState = projectsState) {
                                is UiState.Loading -> if (project == null) LoadingScreen(strings.loadingProjects)
                                is UiState.Error -> ErrorScreen(pState.message, onRetry = { projectsViewModel.loadProjects(force = true) })
                                is UiState.Success -> {
                                    if (project == null) {
                                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(strings.noProjectsFound, color = ArgepColors.Error)
                                                Button(onClick = { navigator.pop() }, modifier = Modifier.padding(top = 16.dp)) {
                                                    Text(strings.back)
                                                }
                                            }
                                        }
                                    } else {
                        ProjectDetailContent(
                            padding = padding,
                            project = project!!,
                            milestoneState = milestoneState,
                            teamState = teamState,
                            filesState = filesState,
                            expenseState = expenseState,
                            actionState = actionState,
                            activeTab = activeTab,
                            onTabChange = { activeTab = it },
                            onMilestoneClick = { id -> navigator.push(MilestoneDetailScreen(id)) },
                            onDeleteFile = { path -> projectFilesViewModel.deleteFile(projectId, path) },
                            onUploadClick = { filePicker.launch() },
                            onAddExpenseClick = { showAddExpenseDialog = true },
                            onAddMilestoneClick = { showAddMilestoneDialog = true }
                        )

                        if (showAddExpenseDialog) {
                            AddExpenseDialog(
                                onDismiss = { showAddExpenseDialog = false },
                                onCreate = { amount, desc, cat -> 
                                    expenseViewModel.addExpense(projectId, amount, desc, cat)
                                    showAddExpenseDialog = false
                                }
                            )
                        }

                        if (showEditDialog) {
                            EditProjectDialog(
                                project = project!!,
                                onDismiss = { showEditDialog = false },
                                onUpdate = { updatedProject ->
                                    projectsViewModel.updateProject(updatedProject)
                                    showEditDialog = false
                                }
                            )
                        }

                        if (showAddMilestoneDialog) {
                            AddMilestoneDialog(
                                onDismiss = { showAddMilestoneDialog = false },
                                onCreate = { title, date ->
                                    milestoneViewModel.createMilestone(projectId, title, date)
                                    showAddMilestoneDialog = false
                                }
                            )
                        }
                    }
                }
            }
        }

        LaunchedEffect(actionState) {
            if (actionState is UiState.Success) {
                projectFilesViewModel.clearActionState()
            }
        }
    }
}

@Composable
fun ProjectDetailContent(
    padding: PaddingValues,
    project: Project,
    milestoneState: UiState<MilestoneData>,
    teamState: UiState<TeamData>,
    filesState: UiState<com.argesurec.shared.viewmodel.FileData>,
    expenseState: UiState<com.argesurec.shared.viewmodel.ExpenseData>,
    actionState: UiState<Unit>?,
    activeTab: Int,
    onTabChange: (Int) -> Unit,
    onMilestoneClick: (String) -> Unit,
    onDeleteFile: (String) -> Unit,
    onUploadClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onAddMilestoneClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    BoxWithConstraints(
        modifier = Modifier.padding(padding).fillMaxSize()
    ) {
        val isMobile = maxWidth < 800.dp
        
        if (isMobile) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val progress = if ((project.budgetTotal ?: 0.0) > 0) ((project.budgetSpent ?: 0.0) / (project.budgetTotal ?: 1.0)).toFloat() else 0f
                ProgressOverviewCard(project, progress.coerceIn(0f, 1f))
                
                // Modern Tab Bar
                Surface(
                    color = ArgepColors.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp
                ) {
                    TabRow(
                        selectedTabIndex = activeTab,
                        containerColor = Color.Transparent,
                        contentColor = ArgepColors.Navy900,
                        divider = {},
                        indicator = { tabPositions ->
                            Box(
                                Modifier
                                    .tabIndicatorOffset(tabPositions[activeTab])
                                    .fillMaxHeight()
                                    .padding(horizontal = 4.dp, vertical = 6.dp)
                                    .background(ArgepColors.Slate100, RoundedCornerShape(8.dp))
                            )
                        }
                    ) {
                        listOf(
                            Triple(strings.milestones, Icons.Default.DateRange, 0),
                            Triple(strings.files, Icons.Default.Info, 1),
                            Triple(strings.expenses, Icons.Default.ShoppingCart, 2)
                        ).forEach { (label, icon, index) ->
                            Tab(
                                selected = activeTab == index,
                                onClick = { onTabChange(index) },
                                text = { Text(label, fontSize = 11.sp, fontWeight = if(activeTab == index) FontWeight.Bold else FontWeight.Normal) },
                                icon = { Icon(icon, null, modifier = Modifier.size(16.dp)) }
                            )
                        }
                    }
                }

                when(activeTab) {
                    0 -> MilestonesCard(milestoneState, onMilestoneClick)
                    1 -> FilesCard(project, filesState, actionState, onDeleteFile, onUploadClick)
                    2 -> ExpensesCard(expenseState, onAddExpenseClick)
                }

                InfoPanel(project, teamState)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // LEFT COLUMN (Main Content)
                Column(modifier = Modifier.weight(1.6f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    val progress = if ((project.budgetTotal ?: 0.0) > 0) ((project.budgetSpent ?: 0.0) / (project.budgetTotal ?: 1.0)).toFloat() else 0f
                    ProgressOverviewCard(project, progress.coerceIn(0f, 1f))

                    // Modern Tab Bar
                    Surface(
                        color = ArgepColors.White,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 2.dp
                    ) {
                        TabRow(
                            selectedTabIndex = activeTab,
                            containerColor = Color.Transparent,
                            contentColor = ArgepColors.Navy900,
                            divider = {},
                            indicator = { tabPositions ->
                                Box(
                                    Modifier
                                        .tabIndicatorOffset(tabPositions[activeTab])
                                        .fillMaxHeight()
                                        .padding(horizontal = 4.dp, vertical = 6.dp)
                                        .background(ArgepColors.Slate100, RoundedCornerShape(8.dp))
                                )
                            }
                        ) {
                            listOf(
                                Triple(strings.milestones, Icons.Default.DateRange, 0),
                                Triple(strings.files, Icons.Default.Info, 1),
                                Triple(strings.expenses, Icons.Default.ShoppingCart, 2)
                            ).forEach { (label, icon, index) ->
                                Tab(
                                    selected = activeTab == index,
                                    onClick = { onTabChange(index) },
                                    text = { Text(label, fontSize = 13.sp, fontWeight = if(activeTab == index) FontWeight.Bold else FontWeight.Normal) },
                                    icon = { Icon(icon, null, modifier = Modifier.size(18.dp)) }
                                )
                            }
                        }
                    }

                    when(activeTab) {
                        0 -> MilestonesCard(milestoneState, onMilestoneClick)
                        1 -> FilesCard(project, filesState, actionState, onDeleteFile, onUploadClick)
                        2 -> ExpensesCard(expenseState, onAddExpenseClick)
                    }
                }

                // RIGHT COLUMN (Info Panel)
                Column(modifier = Modifier.weight(0.9f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    InfoPanel(project, teamState)
                }
            }
        }
    }
}

@Composable
fun MilestonesCard(
    milestoneState: UiState<MilestoneData>,
    onMilestoneClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ArgepColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.Slate100)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(strings.roadmap, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = ArgepColors.Navy900)
            Spacer(modifier = Modifier.height(20.dp))
            
            when (val mState = milestoneState) {
                is UiState.Loading -> Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = ArgepColors.ChartBlue) }
                is UiState.Error -> Text(mState.message, color = ArgepColors.Error)
                is UiState.Success -> {
                    val milestones = mState.data.milestones
                    if (milestones.isEmpty()) {
                        EmptyState(strings.noMilestones)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            milestones.forEach { milestone ->
                                MilestoneTimelineItem(milestone) {
                                    milestone.id?.let { onMilestoneClick(it) }
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
fun FilesCard(
    project: Project,
    filesState: UiState<com.argesurec.shared.viewmodel.FileData>,
    actionState: UiState<Unit>?,
    onDeleteFile: (String) -> Unit,
    onUploadClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ArgepColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.Slate100)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(strings.projectDocuments, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = ArgepColors.Navy900)
                if (actionState is UiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = ArgepColors.ChartBlue)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUploadClick() },
                color = ArgepColors.Slate50,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.Slate200)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = ArgepColors.ChartBlue, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(strings.uploadNewDocument, style = MaterialTheme.typography.titleSmall, color = ArgepColors.Navy900, fontWeight = FontWeight.Bold)
                    Text(strings.uploadInstructions, style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate500)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (val fState = filesState) {
                is UiState.Loading -> Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = ArgepColors.ChartBlue) }
                is UiState.Error -> Text(fState.message, color = ArgepColors.Error)
                is UiState.Success -> {
                    val filesData = fState.data
                    if (filesData.files.isEmpty()) {
                        EmptyState(strings.noDocumentsFound)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            filesData.files.forEach { file ->
                                ProjectFileItem(file) {
                                    onDeleteFile("projects/${project.id}/${file.name}")
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
fun InfoPanel(
    project: Project,
    teamState: UiState<TeamData>
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ArgepColors.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.Slate100)
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(strings.details, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = ArgepColors.Navy900)
                
                DetailRow(strings.currentStatus, { StatusBadge(project.status ?: strings.active) })
                DetailRow(strings.startDate, { Text(formatDate(project.startDate ?: project.createdAt), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold) })
                DetailRow(strings.totalBudget, { Text("${project.budgetTotal} ${strings.currencySymbol}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ArgepColors.ChartBlue) })
                DetailRow(strings.spent, { Text("${project.budgetSpent} ${strings.currencySymbol}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ArgepColors.ChartAmber) })

                HorizontalDivider(color = ArgepColors.Slate100)
                
                Text(
                    project.description ?: strings.noProjectDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ArgepColors.Slate600,
                    lineHeight = 20.sp
                )
            }
        }

        // Team Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ArgepColors.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.Slate100)
        ) {
            var showInviteDialog by remember { mutableStateOf(false) }
            val navigator = LocalNavigator.currentOrThrow
            val teamViewModel = koinViewModel<TeamViewModel>()

            if (showInviteDialog) {
                com.argesurec.shared.ui.team.InviteMemberDialog(
                    onDismiss = { showInviteDialog = false },
                    onInvite = { email, role ->
                        teamViewModel.inviteMember(email, role, project.id!!)
                        showInviteDialog = false
                    }
                )
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(strings.projectTeam, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = ArgepColors.Navy900)
                    IconButton(
                        onClick = { showInviteDialog = true },
                        modifier = Modifier.size(32.dp).background(ArgepColors.ChartBlue, CircleShape)
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                when (val tState = teamState) {
                    is UiState.Loading -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = ArgepColors.ChartBlue) }
                    is UiState.Error -> Text(tState.message, color = ArgepColors.Error)
                    is UiState.Success -> {
                        val members = tState.data.members
                        if (members.isEmpty()) {
                            EmptyState(strings.noTeamMembers)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                members.take(5).forEach { member ->
                                    TeamMemberAvatarRow(member.profile?.fullName ?: strings.unknown, member.role ?: strings.member)
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Button(
                                    onClick = { navigator.push(com.argesurec.shared.ui.team.TeamScreen(project.id)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = ArgepColors.Slate50, contentColor = ArgepColors.Navy900),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("${strings.manageTeam} (${members.size})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
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
fun ProjectFileItem(file: io.github.jan.supabase.storage.FileObject, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(ArgepColors.Slate50, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            Icons.Default.List,
            contentDescription = null,
            tint = ArgepColors.Navy600,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            val size = file.metadata?.get("size")?.toString()?.toLongOrNull() ?: 0L
            Text(file.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = ArgepColors.Navy900)
            Text("${size / 1024} KB", style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate500)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = { /* İndirme henüz eklenmedi */ }) {
                Icon(Icons.Default.Share, contentDescription = strings.download, tint = ArgepColors.Slate600)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = strings.delete, tint = ArgepColors.Error)
            }
        }
    }
}

@Composable
fun ProgressOverviewCard(project: com.argesurec.shared.model.Project, progress: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ArgepColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.Slate100)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(strings.overallProjectProgress, style = MaterialTheme.typography.labelMedium, color = ArgepColors.Slate500, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${(progress * 100).toInt()}% ${strings.completed}", style = MaterialTheme.typography.headlineSmall, color = ArgepColors.Navy900, fontWeight = FontWeight.ExtraBold)
                }
                
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp)) {
                    CircularProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxSize(),
                        color = ArgepColors.ChartEmerald,
                        trackColor = ArgepColors.Slate100,
                        strokeWidth = 6.dp
                    )
                    Text("${(progress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ArgepColors.ChartEmerald)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = ArgepColors.ChartEmerald,
                trackColor = ArgepColors.Slate100
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Column {
                    Text(strings.startDate.uppercase(), style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate400)
                    Text(formatDate(project.startDate ?: project.createdAt), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(strings.targetEndDate.uppercase(), style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate400)
                    Text(formatDate(project.endDate), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(strings.currentStatus.uppercase(), style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate400)
                    StatusBadge(project.status ?: strings.active)
                }
            }
        }
    }
}

@Composable
fun MilestoneTimelineItem(milestone: Milestone, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(ArgepColors.Phase3))
        Column(modifier = Modifier.weight(1f)) {
            Text(milestone.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Text(milestone.dueDate ?: strings.noDateSpecified, style = MaterialTheme.typography.bodySmall, color = ArgepColors.Slate500)
        }
        StatusBadge(milestone.status ?: strings.waiting)
    }
}

@Composable
fun InfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ArgepColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = ArgepColors.Slate100)
            content()
        }
    }
}

@Composable
fun DetailRow(label: String, valueContent: @Composable () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.Top) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate500, modifier = Modifier.width(110.dp))
        valueContent()
    }
}

@Composable
fun TeamMemberAvatarRow(name: String, role: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(modifier = Modifier.size(30.dp), shape = CircleShape, color = ArgepColors.Navy600) {
            Box(contentAlignment = Alignment.Center) {
                Text(name.take(1), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        Column {
            Text(name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(role, style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate500)
        }
    }
}

@Composable
fun PhaseBadge(phase: com.argesurec.shared.model.ProjectPhase?) {
    val phaseName = when (phase) {
        com.argesurec.shared.model.ProjectPhase.INCUBATION -> strings.incubation
        com.argesurec.shared.model.ProjectPhase.DEVELOPMENT -> strings.development
        com.argesurec.shared.model.ProjectPhase.COMMERCIALIZATION -> strings.commercialization
        else -> strings.unknown
    }

    val phaseColor = when (phase) {
        com.argesurec.shared.model.ProjectPhase.INCUBATION -> ArgepColors.Phase1
        com.argesurec.shared.model.ProjectPhase.DEVELOPMENT -> ArgepColors.Phase2
        com.argesurec.shared.model.ProjectPhase.COMMERCIALIZATION -> ArgepColors.Phase3
        else -> ArgepColors.Navy500
    }
    val phaseBg = when (phase) {
        com.argesurec.shared.model.ProjectPhase.INCUBATION -> ArgepColors.Phase1Light
        com.argesurec.shared.model.ProjectPhase.DEVELOPMENT -> ArgepColors.Phase2Light
        com.argesurec.shared.model.ProjectPhase.COMMERCIALIZATION -> ArgepColors.Phase3Light
        else -> ArgepColors.Navy50
    }
    Surface(color = phaseBg, shape = RoundedCornerShape(20.dp)) {
        Text(phaseName, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = phaseColor)
    }
}

@Composable
fun StatusBadge(status: String) {
    val (color, bgColor) = when (status) {
        strings.completed, strings.done -> ArgepColors.ChartEmerald to ArgepColors.ChartEmerald.copy(alpha = 0.1f)
        strings.waiting, strings.todo -> ArgepColors.Slate500 to ArgepColors.Slate100
        else -> ArgepColors.Error to ArgepColors.Error.copy(alpha = 0.1f)
    }
    Surface(color = bgColor, shape = RoundedCornerShape(8.dp)) {
        Text(
            status.uppercase(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun ExpensesCard(
    expenseState: UiState<com.argesurec.shared.viewmodel.ExpenseData>,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ArgepColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.Slate100)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(strings.financialTracking, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = ArgepColors.Navy900)
                    Text(strings.budgetAnalysis, style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate500)
                }
                
                Surface(
                    onClick = onAddClick,
                    color = ArgepColors.ChartAmber,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = ArgepColors.Navy900)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.addExpense, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ArgepColors.Navy900)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when(expenseState) {
                is UiState.Loading -> Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = ArgepColors.ChartAmber) }
                is UiState.Error -> Text("${strings.error}: ${expenseState.message}", color = ArgepColors.Error)
                is UiState.Success -> {
                    val data = expenseState.data
                    
                    if (data.expenses.isNotEmpty()) {
                        ExpenseLineChart(data.expenses)
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Text(strings.recentExpenses, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ArgepColors.Slate400, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            data.expenses.take(10).forEach { expense ->
                                ExpenseItem(expense)
                            }
                        }
                    } else {
                        EmptyState(strings.noExpensesFound)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseLineChart(expenses: List<com.argesurec.shared.model.Expense>) {
    val chartData = expenses.groupBy { it.expenseDate?.take(7) ?: "Bilinmiyor" }
        .mapValues { it.value.sumOf { exp -> exp.amount } }
        .entries.sortedBy { it.key }.map { it.value }

    if (chartData.size < 2) {
        Text(strings.moreRecordsForChart, style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate400)
        return
    }

    val maxAmount = chartData.maxOrNull()?.toFloat() ?: 1f

    Column {
        Text(strings.expenseTrend, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ArgepColors.Slate500)
        Spacer(modifier = Modifier.height(16.dp))
        Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            val width = size.width
            val height = size.height
            val spacing = width / (chartData.size - 1)
            
            val path = Path()
            chartData.forEachIndexed { index, amount ->
                val x = index.toFloat() * spacing
                val y = height - (amount.toFloat() / maxAmount * height)
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                
                drawCircle(color = ArgepColors.Phase3, radius = 4.dp.toPx(), center = androidx.compose.ui.geometry.Offset(x, y))
            }
            
            drawPath(
                path = path,
                color = ArgepColors.Phase3,
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}

@Composable
fun ExpenseItem(expense: com.argesurec.shared.model.Expense) {
    Surface(
        color = ArgepColors.Slate50,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (iconColor, bgColor) = when(expense.category) {
                strings.personnel -> ArgepColors.ChartBlue to ArgepColors.ChartBlue.copy(alpha = 0.1f)
                strings.software -> ArgepColors.ChartEmerald to ArgepColors.ChartEmerald.copy(alpha = 0.1f)
                strings.hardware -> ArgepColors.ChartAmber to ArgepColors.ChartAmber.copy(alpha = 0.1f)
                else -> ArgepColors.Slate500 to ArgepColors.Slate100
            }

            Surface(
                modifier = Modifier.size(40.dp),
                color = bgColor,
                shape = RoundedCornerShape(10.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when(expense.category) {
                            strings.personnel -> Icons.Default.Person
                            strings.software -> Icons.Default.Settings
                            strings.hardware -> Icons.Default.Build
                            else -> Icons.Default.Info
                        },
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = iconColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(expense.description ?: strings.noDescription, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ArgepColors.Navy900)
                Text("${expense.category} • ${expense.expenseDate?.take(10) ?: "-"}", style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate500)
            }
            
            Text("${strings.currencySymbol}${expense.amount.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = ArgepColors.Navy900)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onCreate: (Double, String, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val initialCategory = strings.software
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    val categories = listOf(strings.personnel, strings.software, strings.hardware, strings.service, strings.other)

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if(it.all { c -> c.isDigit() || c == '.' }) amount = it },
                    label = { Text("${strings.budget} (${strings.currencySymbol})") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(strings.descriptionLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(strings.categoryLabel, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ArgepColors.Navy700,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(amount.toDoubleOrNull() ?: 0.0, description, selectedCategory) },
                colors = ButtonDefaults.buttonColors(containerColor = ArgepColors.Navy700),
                enabled = amount.isNotBlank() && description.isNotBlank()
            ) {
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}

@Composable
fun EditProjectDialog(
    project: Project,
    onDismiss: () -> Unit,
    onUpdate: (Project) -> Unit
) {
    var name by remember { mutableStateOf(project.name) }
    var description by remember { mutableStateOf(project.description ?: "") }
    var budgetTotal by remember { mutableStateOf(project.budgetTotal?.toString() ?: "0.0") }
    var budgetSpent by remember { mutableStateOf(project.budgetSpent?.toString() ?: "0.0") }
    var selectedPhase by remember { mutableStateOf(project.phase ?: com.argesurec.shared.model.ProjectPhase.DEVELOPMENT) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.editProject) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(strings.projectName) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(strings.descriptionLabel) }, modifier = Modifier.fillMaxWidth())
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = budgetTotal, 
                        onValueChange = { if(it.all { c -> c.isDigit() || c == '.' }) budgetTotal = it }, 
                        label = { Text(strings.totalBudget) }, 
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = budgetSpent, 
                        onValueChange = { if(it.all { c -> c.isDigit() || c == '.' }) budgetSpent = it }, 
                        label = { Text(strings.currentSpending) }, 
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(strings.projectPhaseLabel, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = ArgepColors.Slate500)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    com.argesurec.shared.model.ProjectPhase.entries.forEach { phase ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically, 
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedPhase = phase }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = selectedPhase == phase, onClick = { selectedPhase = phase })
                            Text(
                                when(phase) {
                                    com.argesurec.shared.model.ProjectPhase.INCUBATION -> strings.incubation
                                    com.argesurec.shared.model.ProjectPhase.DEVELOPMENT -> strings.development
                                    com.argesurec.shared.model.ProjectPhase.COMMERCIALIZATION -> strings.commercialization
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selectedPhase == phase) ArgepColors.Navy900 else ArgepColors.Slate600
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { 
                onUpdate(project.copy(
                    name = name,
                    description = description,
                    phase = selectedPhase,
                    budgetTotal = budgetTotal.toDoubleOrNull() ?: 0.0,
                    budgetSpent = budgetSpent.toDoubleOrNull() ?: 0.0
                )) 
            }) {
                Text(strings.update)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}

@Composable
fun AddMilestoneDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.addMilestone) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(strings.milestoneTitleLabel) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("${strings.targetEndDate} (DD/MM/YYYY)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { 
                onCreate(title, dueDate.ifEmpty { null }) 
            }) {
                Text(strings.add)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}

@Composable
fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = ArgepColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = ArgepColors.Navy900)
        }
    }
}
