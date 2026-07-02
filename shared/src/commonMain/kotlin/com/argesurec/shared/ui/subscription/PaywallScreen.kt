package com.argesurec.shared.ui.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.argesurec.shared.ui.theme.ArgepColors
import com.argesurec.shared.util.strings
import com.argesurec.shared.viewmodel.SubscriptionViewModel
import org.koin.compose.viewmodel.koinViewModel

class PaywallScreen : Screen {
    @Composable
    override fun Content() {
        val s = strings
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<SubscriptionViewModel>()
        val state by viewModel.state.collectAsState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ArgepColors.Navy900, ArgepColors.PremiumNavy)
                    )
                )
        ) {
            // Background Decorative Elements
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .offset(x = (-100).dp, y = (-100).dp)
                    .background(ArgepColors.PremiumGold.copy(alpha = 0.05f), RoundedCornerShape(150.dp))
            )

            // Close Button
            IconButton(
                onClick = { navigator.pop() },
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White.copy(alpha = 0.7f))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                // Premium Header Icon (Enhanced)
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = ArgepColors.PremiumGold.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(2.dp, ArgepColors.PremiumGold)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = ArgepColors.PremiumGold,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = s.upgradeToPremium,
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = ArgepColors.White,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                )

                Text(
                    text = s.premiumTagline,
                    style = MaterialTheme.typography.bodyLarge,
                    color = ArgepColors.PremiumGold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Feature Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = ArgepColors.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.White.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        PremiumFeatureRow(s.unlimitedProjects, s.unlimitedProjectsDesc)
                        PremiumFeatureRow(s.unlimitedTeam, s.unlimitedTeamDesc)
                        PremiumFeatureRow(s.advancedAiAnalysis, s.advancedAiAnalysisDesc)
                        PremiumFeatureRow(s.customReporting, s.customReportingDesc)
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Packages
                if (state.isLoading) {
                    CircularProgressIndicator(color = ArgepColors.PremiumGold)
                } else {
                    state.packages.forEach { pkg ->
                        PremiumPackageCard(
                            title = pkg.title,
                            price = pkg.priceString,
                            description = pkg.description,
                            onClick = { viewModel.purchase(pkg, Unit) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    if (state.packages.isEmpty()) {
                        PremiumPackageCard(
                            title = s.premiumMonthlyTitle,
                            price = "${s.currencySymbol}299.99 / ${s.monthly}",
                            description = s.premiumMonthlyDesc,
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PremiumPackageCard(
                            title = s.premiumYearlyTitle,
                            price = "${s.currencySymbol}2.999 / ${s.yearly}",
                            description = s.premiumYearlyDesc,
                            isBestValue = true,
                            onClick = {}
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                TextButton(onClick = { viewModel.restore() }) {
                    Text(s.restorePurchases, color = ArgepColors.White.copy(alpha = 0.5f), style = MaterialTheme.typography.labelMedium)
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun PremiumFeatureRow(title: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(24.dp),
            color = ArgepColors.PremiumGold,
            shape = RoundedCornerShape(6.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = ArgepColors.Navy900)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, color = ArgepColors.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(description, color = ArgepColors.White.copy(alpha = 0.6f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun PremiumPackageCard(
    title: String,
    price: String,
    description: String,
    isBestValue: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isBestValue) ArgepColors.PremiumGold else ArgepColors.White.copy(alpha = 0.08f),
        border = if (!isBestValue) androidx.compose.foundation.BorderStroke(1.dp, ArgepColors.White.copy(alpha = 0.15f)) else null
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title, 
                    fontWeight = FontWeight.Bold, 
                    color = if (isBestValue) ArgepColors.Navy900 else ArgepColors.White, 
                    fontSize = 18.sp
                )
                if (isBestValue) {
                    Surface(
                        color = ArgepColors.Navy900,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            strings.bestValue,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = ArgepColors.PremiumGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Text(
                price, 
                color = if (isBestValue) ArgepColors.Navy900 else ArgepColors.PremiumGold, 
                fontWeight = FontWeight.Black, 
                fontSize = 28.sp, 
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                description, 
                color = if (isBestValue) ArgepColors.Navy900.copy(alpha = 0.7f) else ArgepColors.White.copy(alpha = 0.6f), 
                style = MaterialTheme.typography.bodyMedium, 
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
