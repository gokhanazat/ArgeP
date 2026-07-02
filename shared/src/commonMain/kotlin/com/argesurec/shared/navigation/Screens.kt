package com.argesurec.shared.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.argesurec.shared.ui.home.HomeScreen
import com.argesurec.shared.ui.home.ProfileScreen
import com.argesurec.shared.ui.project.ProjectsScreen
import com.argesurec.shared.ui.home.ReportsScreen
import com.argesurec.shared.ui.team.TeamScreen
import com.argesurec.shared.ui.tasks.TasksScreen
import cafe.adriel.voyager.navigator.Navigator
import com.argesurec.shared.util.strings

object HomeTab : Tab {
    @Composable
    override fun Content() {
        Navigator(HomeScreen())
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = strings.home
            val icon = rememberVectorPainter(Icons.Default.Home)
            return remember(title, icon) {
                TabOptions(index = 0u, title = title, icon = icon)
            }
        }
}

object ProjectsTab : Tab {
    @Composable
    override fun Content() {
        Navigator(ProjectsScreen())
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = strings.projects
            val icon = rememberVectorPainter(Icons.Default.List)
            return remember(title, icon) {
                TabOptions(index = 1u, title = title, icon = icon)
            }
        }
}

object TasksTab : Tab {
    @Composable
    override fun Content() {
        Navigator(TasksScreen(null))
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = strings.tasks
            val icon = rememberVectorPainter(Icons.Default.Done)
            return remember(title, icon) {
                TabOptions(index = 2u, title = title, icon = icon)
            }
        }
}

object ReportsTab : Tab {
    @Composable
    override fun Content() {
        Navigator(ReportsScreen())
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = strings.reports
            val icon = rememberVectorPainter(Icons.Default.Info)
            return remember(title, icon) {
                TabOptions(index = 3u, title = title, icon = icon)
            }
        }
}

object TeamTab : Tab {
    @Composable
    override fun Content() {
        Navigator(TeamScreen(null))
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = strings.team
            val icon = rememberVectorPainter(Icons.Default.AccountBox)
            return remember(title, icon) {
                TabOptions(index = 4u, title = title, icon = icon)
            }
        }
}

object ProfileTab : Tab {
    @Composable
    override fun Content() {
        Navigator(ProfileScreen())
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = strings.profile
            val icon = rememberVectorPainter(Icons.Default.Person)
            return remember(title, icon) {
                TabOptions(index = 5u, title = title, icon = icon)
            }
        }
}
