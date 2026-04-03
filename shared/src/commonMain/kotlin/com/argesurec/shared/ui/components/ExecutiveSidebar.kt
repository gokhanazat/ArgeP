package com.argesurec.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import com.argesurec.shared.navigation.*
import com.argesurec.shared.ui.theme.ArgepColors

@Composable
fun ExecutiveSidebar(
    onLogout: () -> Unit
) {
    val tabNavigator = LocalTabNavigator.current

    Column(
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight()
            .background(ArgepColors.ExecutivePrimary)
            .padding(24.dp)
    ) {
        Text("Argep", style = MaterialTheme.typography.headlineSmall, color = ArgepColors.White)
        Spacer(modifier = Modifier.height(48.dp))
        
        SidebarItem("Dashboard", HomeTab, tabNavigator.current == HomeTab) { tabNavigator.current = HomeTab }
        SidebarItem("Projeler", ProjectsTab, tabNavigator.current == ProjectsTab) { tabNavigator.current = ProjectsTab }
        SidebarItem("Raporlar", ReportsTab, tabNavigator.current == ReportsTab) { tabNavigator.current = ReportsTab }
        SidebarItem("Ekip", TeamTab, tabNavigator.current == TeamTab) { tabNavigator.current = TeamTab }
        SidebarItem("Profil", ProfileTab, tabNavigator.current == ProfileTab) { tabNavigator.current = ProfileTab }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Logout Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogout() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Çıkış",
                tint = ArgepColors.White.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
            Text(
                "Çıkış Yap",
                style = MaterialTheme.typography.titleMedium,
                color = ArgepColors.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun SidebarItem(
    label: String,
    tab: Tab,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        color = if (isActive) ArgepColors.ExecutiveSecondary.copy(alpha = 0.2f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            label,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.titleMedium,
            color = if (isActive) ArgepColors.White else ArgepColors.White.copy(alpha = 0.7f)
        )
    }
}
