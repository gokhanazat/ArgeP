package com.argesurec.shared.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import org.jetbrains.compose.resources.painterResource
import argesurec.shared.generated.resources.*
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.viewmodel.koinViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.argesurec.shared.viewmodel.AuthViewModel
import com.argesurec.shared.ui.theme.ArgepColors
import com.argesurec.shared.util.isWeb
import com.argesurec.shared.util.strings

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<AuthViewModel>()
        val state by viewModel.state.collectAsState()

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var rememberMe by remember { mutableStateOf(false) }

        if (isWeb) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ArgepColors.ExecutiveBackground),
                contentAlignment = Alignment.Center
            ) {
                WebAuthCard(
                    email = email,
                    password = password,
                    rememberMe = rememberMe,
                    isLoading = state.isLoading,
                    error = state.error,
                    onEmailChange = { email = it },
                    onPasswordChange = { password = it },
                    onRememberMeChange = { rememberMe = it },
                    onLoginClick = { viewModel.signIn(email, password) },
                    onRegisterClick = { navigator.push(RegisterScreen()) }
                )
            }
        } else {
            MobileAuthContent(
                email = email,
                password = password,
                isLoading = state.isLoading,
                error = state.error,
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onLoginClick = { viewModel.signIn(email, password) },
                onRegisterClick = { navigator.push(RegisterScreen()) }
            )
        }
    }

    @Composable
    private fun WebAuthCard(
        email: String,
        password: String,
        rememberMe: Boolean,
        isLoading: Boolean,
        error: String?,
        onEmailChange: (String) -> Unit,
        onPasswordChange: (String) -> Unit,
        onRememberMeChange: (Boolean) -> Unit,
        onLoginClick: () -> Unit,
        onRegisterClick: () -> Unit
    ) {
        Surface(
            modifier = Modifier
                .width(1000.dp)
                .height(600.dp)
                .shadow(24.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Side: Hero Section
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                        .background(ArgepColors.ExecutivePrimary)
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .background(Color.White, RoundedCornerShape(110.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.app_logo),
                            contentDescription = strings.appName,
                            modifier = Modifier.size(150.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text(
                        strings.appName,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 32.sp
                        )
                    )
                    Text(
                        strings.managementSystem,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Light,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 24.sp,
                            letterSpacing = 2.sp
                        )
                    )
                }

                // Right Side: Form Section
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(60.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        strings.welcomeBack,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ArgepColors.ExecutivePrimary
                        )
                    )
                    Text(
                        strings.loginInstructions,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ArgepColors.Slate500
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    ModernInputField(
                        label = strings.email.uppercase(),
                        value = email,
                        placeholder = strings.emailExample,
                        onValueChange = onEmailChange
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ModernInputField(
                        label = strings.password.uppercase(),
                        value = password,
                        placeholder = strings.passwordDots,
                        isPassword = true,
                        onValueChange = onPasswordChange
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = onRememberMeChange,
                            colors = CheckboxDefaults.colors(checkedColor = ArgepColors.ExecutivePrimary)
                        )
                        Text(strings.rememberMe, style = MaterialTheme.typography.bodySmall, color = ArgepColors.Slate600)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        Button(
                            onClick = onLoginClick,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ArgepColors.ExecutivePrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(strings.login, style = MaterialTheme.typography.titleMedium, color = Color.White)
                        }
                    }

                    error?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Divider(color = ArgepColors.Slate100, thickness = 1.dp)
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    // Registration disabled for SaaS private mode

                }
            }
        }
    }

    @Composable
    private fun MobileAuthContent(
        email: String,
        password: String,
        isLoading: Boolean,
        error: String?,
        onEmailChange: (String) -> Unit,
        onPasswordChange: (String) -> Unit,
        onLoginClick: () -> Unit,
        onRegisterClick: () -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(ArgepColors.White).padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                color = ArgepColors.Navy900,
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(Res.drawable.app_logo),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                strings.welcomeBack,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = ArgepColors.Navy900
            )
            Text(
                strings.loginInstructions,
                style = MaterialTheme.typography.bodyMedium,
                color = ArgepColors.Slate500
            )

            Spacer(modifier = Modifier.height(40.dp))

            ModernInputField(
                label = strings.email.uppercase(),
                value = email,
                placeholder = strings.emailExample,
                onValueChange = onEmailChange
            )

            Spacer(modifier = Modifier.height(20.dp))

            ModernInputField(
                label = strings.password.uppercase(),
                value = password,
                placeholder = strings.passwordDots,
                isPassword = true,
                onValueChange = onPasswordChange
            )

            Spacer(modifier = Modifier.height(40.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ArgepColors.Navy900)
                }
            } else {
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ArgepColors.Navy900),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(strings.login, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            error?.let {
                Text(
                    text = it, 
                    color = ArgepColors.Error, 
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Registration disabled for SaaS private mode

        }
    }
}

@Composable
fun ModernInputField(
    label: String,
    value: String,
    placeholder: String,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            ),
            color = ArgepColors.Slate400
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = ArgepColors.Slate300, fontSize = 15.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = ArgepColors.Slate50,
                unfocusedContainerColor = ArgepColors.Slate50,
                disabledContainerColor = ArgepColors.Slate50,
                cursorColor = ArgepColors.Navy900,
                focusedIndicatorColor = ArgepColors.Navy700,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            singleLine = true
        )
    }
}
