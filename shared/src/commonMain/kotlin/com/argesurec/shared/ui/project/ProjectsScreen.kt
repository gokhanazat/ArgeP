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

class ProjectsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<ProjectsViewModel>()
        val state by viewModel.state.collectAsState()
        
        var selectedFilter by remember { mutableStateOf("Tümü") }
        val filters = listOf("Tümü", "Kuluçka", "Geliştirme", "Ticarileşme")
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
            topBar = {
                TopAppBar(
                    title = { Text("Projeler", style = MaterialTheme.typography.titleLarge) },
                    actions = {
                        FilterChips(filters, selectedFilter) { selectedFilter = it }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { showCreateDialog = true },
                            shape = RoundedCornerShape(7.dp),
                            modifier = Modifier.padding(end = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ArgepColors.Navy700)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Yeni Proje", fontSize = 13.sp)
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
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = ArgepColors.Slate50
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                when (val uiState = state) {
                    is UiState.Loading -> LoadingScreen("Projeler yükleniyor...")
                    is UiState.Error -> ErrorScreen(uiState.message, onRetry = { viewModel.loadProjects(force = true) })
                    is UiState.Success -> {
                        val allProjects = uiState.data.projects
                        val filteredProjects = if (selectedFilter == "Tümü") {
                            allProjects
                        } else {
                            allProjects.filter { projectWithTeam ->
                                val project = projectWithTeam.toProject()
                                val enumName = when (project.phase) {
                                    com.argesurec.shared.model.ProjectPhase.INCUBATION -> "Kuluçka"
                                    com.argesurec.shared.model.ProjectPhase.DEVELOPMENT -> "Geliştirme"
                                    com.argesurec.shared.model.ProjectPhase.COMMERCIALIZATION -> "Ticarileşme"
                                }
                                enumName == selectedFilter
                            }
                        }

                        if (filteredProjects.isEmpty()) {
                            EmptyState("Aranan kriterlerde proje bulunamadı.")
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredProjects) { project ->
                                    PremiumProjectCard(project) {
                                        project.id?.let { id ->
                                            navigator.push(ProjectDetailScreen(id))
                                        }
                                    }
                                }
                                
                                // Yeni Proje Ekle Kartı (Dashed)
                                item {
                                    NewProjectPlaceholderCard(onClick = { showCreateDialog = true })
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
fun FilterChips(filters: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(modifier = Modifier.padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        filters.forEach { filter ->
            val isActive = selected == filter
            Surface(
                modifier = Modifier.clickable { onSelect(filter) },
                color = if (isActive) ArgepColors.Navy700 else ArgepColors.White,
                shape = RoundedCornerShape(20.dp),
                border = if (!isActive) androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.Slate300) else null
            ) {
                Text(
                    text = filter,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = if (isActive) Color.White else ArgepColors.Slate600
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
        com.argesurec.shared.model.ProjectPhase.INCUBATION -> "Kuluçka"
        com.argesurec.shared.model.ProjectPhase.DEVELOPMENT -> "Geliştirme"
        com.argesurec.shared.model.ProjectPhase.COMMERCIALIZATION -> "Ticarileşme"
        else -> "Belirsiz"
    }

    val phaseColor = when (project.phase) {
        com.argesurec.shared.model.ProjectPhase.INCUBATION -> ArgepColors.Phase1
        com.argesurec.shared.model.ProjectPhase.DEVELOPMENT -> ArgepColors.Phase2
        com.argesurec.shared.model.ProjectPhase.COMMERCIALIZATION -> ArgepColors.Phase3
        else -> ArgepColors.Navy500
    }
    
    val phaseBg = when (project.phase) {
        com.argesurec.shared.model.ProjectPhase.INCUBATION -> ArgepColors.Phase1Light
        com.argesurec.shared.model.ProjectPhase.DEVELOPMENT -> ArgepColors.Phase2Light
        com.argesurec.shared.model.ProjectPhase.COMMERCIALIZATION -> ArgepColors.Phase3Light
        else -> ArgepColors.Navy50
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.Slate200)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(project.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp), color = ArgepColors.Navy900)
                    Text(project.description ?: "Açıklama yok", style = MaterialTheme.typography.bodySmall, color = ArgepColors.Slate500, maxLines = 1)
                }
                Surface(color = phaseBg, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        phaseName.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                        color = phaseColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Progress Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("İlerleme", style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate600)
                    Text("%0", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = ArgepColors.Navy900)
                }
                LinearProgressIndicator(
                    progress = 0f,
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = phaseColor,
                    trackColor = phaseBg
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Budget Summary
            Row(modifier = Modifier.fillMaxWidth().background(ArgepColors.Slate50, RoundedCornerShape(8.dp)).padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("BÜTÇE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold), color = ArgepColors.Slate400)
                    Text("${project.budgetTotal ?: 0.0} ₺", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = ArgepColors.Navy800)
                }
                
                // Team Avatars
                Row(horizontalArrangement = Arrangement.spacedBy((-8).dp), verticalAlignment = Alignment.CenterVertically) {
                    projectWithTeam.members.take(3).forEach { member ->
                        Surface(
                            modifier = Modifier.size(24.dp),
                            shape = CircleShape,
                            color = ArgepColors.Slate200,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    (member.profile?.fullName ?: "?").take(1).uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = ArgepColors.Slate600
                                )
                            }
                        }
                    }
                    if (projectWithTeam.members.size > 3) {
                        Surface(
                            modifier = Modifier.size(24.dp),
                            shape = CircleShape,
                            color = ArgepColors.Navy700,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "+${projectWithTeam.members.size - 3}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("HARCANAN", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold), color = ArgepColors.Slate400)
                    Text("${project.budgetSpent ?: 0.0} ₺", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = ArgepColors.Navy800)
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
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = ArgepColors.Slate50,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, ArgepColors.Slate200)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                color = ArgepColors.White,
                shape = CircleShape,
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = ArgepColors.Navy700, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Yeni Proje Oluştur", style = MaterialTheme.typography.titleMedium, color = ArgepColors.Navy900, fontWeight = FontWeight.Bold)
            Text("Yeni bir Ar-Ge süreci başlatın", style = MaterialTheme.typography.labelSmall, color = ArgepColors.Slate500)
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
                        "Yeni Proje Başlat",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = ArgepColors.Navy900
                    )
                    Text("Projeye dair temel bilgileri girin", style = MaterialTheme.typography.bodySmall, color = ArgepColors.Slate500)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Proje Adı", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            placeholder = { Text("Örn: Akıllı Tarım Kiti", fontSize = 14.sp) }
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Proje Amacı ve Açıklama", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(10.dp)
                        )
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = budgetTotal, 
                                onValueChange = { if(it.all { c -> c.isDigit() || c == '.' }) budgetTotal = it }, 
                                label = { Text("Toplam Bütçe (₺)", fontSize = 11.sp) }, 
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = budgetSpent, 
                                onValueChange = { if(it.all { c -> c.isDigit() || c == '.' }) budgetSpent = it }, 
                                label = { Text("Mevcut Harcama (₺)", fontSize = 11.sp) }, 
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = startDate, 
                                onValueChange = { startDate = it }, 
                                label = { Text("Başlangıç Tarihi", fontSize = 11.sp) }, 
                                placeholder = { Text("YYYY-MM-DD", fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = endDate, 
                                onValueChange = { endDate = it }, 
                                label = { Text("Hedef Bitiş Tarihi", fontSize = 11.sp) }, 
                                placeholder = { Text("YYYY-MM-DD", fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Text("PROJE FAZI", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = ArgepColors.Navy700)
                        
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
                                                com.argesurec.shared.model.ProjectPhase.INCUBATION -> "Kuluçka"
                                                com.argesurec.shared.model.ProjectPhase.DEVELOPMENT -> "Geliştirme"
                                                com.argesurec.shared.model.ProjectPhase.COMMERCIALIZATION -> "Ticarileşme"
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
                            Text("Vazgeç", color = ArgepColors.Slate500)
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
                            Text("Projeyi Oluştur", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    )
}
