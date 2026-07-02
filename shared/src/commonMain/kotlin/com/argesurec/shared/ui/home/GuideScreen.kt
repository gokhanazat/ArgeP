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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.argesurec.shared.ui.theme.ArgepColors
import com.argesurec.shared.util.strings

class GuideScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val s = strings

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(s.userGuide, style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            containerColor = ArgepColors.Slate50
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = s.guideSubtitle,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArgepColors.Navy900,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(getGuideSteps(s)) { step ->
                    GuideStepCard(step)
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { navigator.pop() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ArgepColors.Navy900),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(s.gotIt, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    @Composable
    private fun GuideStepCard(step: GuideStep) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = step.color.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(step.icon, null, tint = step.color, modifier = Modifier.size(24.dp))
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArgepColors.Navy900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ArgepColors.Slate600
                    )
                }
            }
        }
    }

    private data class GuideStep(
        val title: String,
        val description: String,
        val icon: ImageVector,
        val color: Color
    )

    private fun getGuideSteps(s: com.argesurec.shared.util.AppStrings) = listOf(
        GuideStep(s.guideStep1Title, s.guideStep1Desc, Icons.Default.Business, ArgepColors.ChartBlue),
        GuideStep(s.guideStep2Title, s.guideStep2Desc, Icons.Default.RocketLaunch, ArgepColors.ChartEmerald),
        GuideStep(s.guideStep3Title, s.guideStep3Desc, Icons.Default.Timeline, ArgepColors.ChartAmber),
        GuideStep(s.guideStep4Title, s.guideStep4Desc, Icons.Default.CheckCircle, ArgepColors.Info),
        GuideStep(s.guideStep5Title, s.guideStep5Desc, Icons.Default.AutoGraph, ArgepColors.ChartRose)
    )
}
