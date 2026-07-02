package com.argesurec.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import com.argesurec.shared.navigation.*
import com.argesurec.shared.ui.theme.ArgepColors
import com.argesurec.shared.util.strings

@Composable
fun ExecutiveSidebar(
    onLogout: () -> Unit,
    onUpgradeClick: () -> Unit
) {
    val s = strings
    val tabNavigator = LocalTabNavigator.current

    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(ArgepColors.Navy900)
            .padding(vertical = 32.dp, horizontal = 20.dp)
    ) {
        // LOGO AREA
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                color = ArgepColors.ChartBlue,
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("A", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                s.appName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                ),
                color = ArgepColors.White
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // NAVIGATION ITEMS
        SidebarItem(s.dashboard, Icons.Default.Home, tabNavigator.current == HomeTab) { tabNavigator.current = HomeTab }
        SidebarItem(s.projects, Icons.Default.List, tabNavigator.current == ProjectsTab) { tabNavigator.current = ProjectsTab }
        SidebarItem(s.tasks, Icons.Default.CheckCircle, tabNavigator.current == TasksTab) { tabNavigator.current = TasksTab }
        SidebarItem(s.reports, Icons.Default.Assessment, tabNavigator.current == ReportsTab) { tabNavigator.current = ReportsTab }
        SidebarItem(s.team, Icons.Default.Person, tabNavigator.current == TeamTab) { tabNavigator.current = TeamTab }
        SidebarItem(s.profile, Icons.Default.Settings, tabNavigator.current == ProfileTab) { tabNavigator.current = ProfileTab }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Premium Upgrade Card (Sleek)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onUpgradeClick() },
            color = ArgepColors.Navy800,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = ArgepColors.PremiumGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PREMIUM", style = MaterialTheme.typography.labelSmall, color = ArgepColors.PremiumGold, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(s.removeLimits, style = MaterialTheme.typography.labelMedium, color = ArgepColors.Navy300)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Logout Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onLogout() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = s.logout,
                tint = ArgepColors.Slate400,
                modifier = Modifier.size(20.dp)
            )
            Text(
                s.logout,
                style = MaterialTheme.typography.titleMedium,
                color = ArgepColors.Slate400
            )
        }
    }
}

@Composable
private fun SidebarItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = if (isActive) ArgepColors.ChartBlue.copy(alpha = 0.15f) else Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (isActive) ArgepColors.ChartBlue else ArgepColors.Slate400
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isActive) ArgepColors.White else ArgepColors.Slate400
            )
        }
    }
}
