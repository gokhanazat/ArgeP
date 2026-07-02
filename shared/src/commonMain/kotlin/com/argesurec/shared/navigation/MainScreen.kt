package com.argesurec.shared.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.argesurec.shared.ui.components.ExecutiveSidebar
import com.argesurec.shared.ui.subscription.PaywallScreen
import com.argesurec.shared.ui.auth.SetupScreen
import com.argesurec.shared.util.isWeb
import com.argesurec.shared.viewmodel.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel

class MainScreen : Screen {
    @Composable
    override fun Content() {
        val authViewModel = koinViewModel<AuthViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        val authState by authViewModel.state.collectAsState()
        val userProfile = authState.userProfile
        
        // Show loading if logged in but profile not yet loaded
        if (authState.isLoggedIn && userProfile == null) {
            com.argesurec.shared.ui.components.LoadingScreen()
            return
        }

        // Show SetupScreen if logged in but no organization
        if (authState.isLoggedIn && userProfile?.orgId == null) {
            SetupScreen().Content()
            return
        }

        TabNavigator(HomeTab) {
            if (isWeb) {
                Row(modifier = Modifier.fillMaxSize()) {
                    ExecutiveSidebar(
                        onLogout = { authViewModel.signOut() },
                        onUpgradeClick = { navigator.push(PaywallScreen()) }
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        CurrentTab()
                    }
                }
            } else {
                Scaffold(
                    bottomBar = {
                        BottomNavBar()
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(bottom = padding.calculateBottomPadding())) {
                        CurrentTab()
                    }
                }
            }
        }
    }
}
