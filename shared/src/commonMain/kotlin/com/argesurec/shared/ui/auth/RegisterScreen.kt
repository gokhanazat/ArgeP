package com.argesurec.shared.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.argesurec.shared.viewmodel.AuthViewModel
import com.argesurec.shared.ui.theme.ArgepColors
import com.argesurec.shared.util.strings

class RegisterScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<AuthViewModel>()
        val state by viewModel.state.collectAsState()

        var fullName by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Column(modifier = Modifier.fillMaxSize().background(ArgepColors.White).padding(32.dp)) {
            Surface(
                onClick = { navigator.pop() },
                color = ArgepColors.Slate50,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = strings.back,
                    tint = ArgepColors.Navy900,
                    modifier = Modifier.padding(8.dp).size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                strings.register,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = ArgepColors.Navy900
            )
            Text(
                strings.registerInstructions,
                style = MaterialTheme.typography.bodyMedium,
                color = ArgepColors.Slate500
            )

            Spacer(modifier = Modifier.height(40.dp))

            ModernInputField(
                label = strings.fullNameLabel.uppercase(),
                value = fullName,
                placeholder = strings.fullNamePlaceholder,
                onValueChange = { fullName = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            ModernInputField(
                label = strings.email.uppercase(),
                value = email,
                placeholder = strings.emailExample,
                onValueChange = { email = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            ModernInputField(
                label = strings.password.uppercase(),
                value = password,
                placeholder = strings.passwordDots,
                isPassword = true,
                onValueChange = { password = it }
            )

            Spacer(modifier = Modifier.height(40.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ArgepColors.Navy900)
                }
            } else {
                Button(
                    onClick = { viewModel.signUp(email, password, fullName) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ArgepColors.Navy900),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(strings.register, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            state.error?.let {
                Text(
                    text = it,
                    color = ArgepColors.Error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
