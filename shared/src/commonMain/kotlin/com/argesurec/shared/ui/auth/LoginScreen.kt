package com.argesurec.shared.ui.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

private val WebLoginDarkNavy = Color(0xFF1B2D4D)
private val WebLoginGold = Color(0xFFE1A730)
private val WebCardBtn = Color(0xFF0F172A)
private val WebInputBg = Color(0xFFF8FAFC)

private class CurvedHeroShape(private val isWide: Boolean) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            if (isWide) {
                moveTo(0f, 0f)
                lineTo(size.width * 0.38f, 0f)
                cubicTo(
                    size.width * 0.53f, size.height * 0.28f,
                    size.width * 0.53f, size.height * 0.70f,
                    size.width * 0.36f, size.height
                )
                lineTo(0f, size.height)
                close()
            } else {
                moveTo(0f, 0f)
                lineTo(0f, size.height * 0.30f)
                cubicTo(
                    size.width * 0.28f, size.height * 0.42f,
                    size.width * 0.72f, size.height * 0.42f,
                    size.width, size.height * 0.30f
                )
                lineTo(size.width, 0f)
                close()
            }
        }
        return Outline.Generic(path)
    }
}

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
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WebLoginDarkNavy)
            ) {
                val isWide = maxWidth >= 850.dp

                // 1. Hero Image with Curved Cut on the left
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CurvedHeroShape(isWide))
                ) {
                    Image(
                        painter = painterResource(Res.drawable.login_hero),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.CenterStart
                    )
                }

                // 2. Gold Border along the Curve
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val borderPath = Path().apply {
                        if (isWide) {
                            moveTo(size.width * 0.38f, 0f)
                            cubicTo(
                                size.width * 0.53f, size.height * 0.28f,
                                size.width * 0.53f, size.height * 0.70f,
                                size.width * 0.36f, size.height
                            )
                        } else {
                            moveTo(0f, size.height * 0.30f)
                            cubicTo(
                                size.width * 0.28f, size.height * 0.42f,
                                size.width * 0.72f, size.height * 0.42f,
                                size.width, size.height * 0.30f
                            )
                        }
                    }
                    drawPath(
                        path = borderPath,
                        color = WebLoginGold,
                        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // 3. Right pane with centered login card and logo
                if (isWide) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1.15f))

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            WebLoginForm(
                                email = email,
                                password = password,
                                rememberMe = rememberMe,
                                isLoading = state.isLoading,
                                error = state.error,
                                onEmailChange = { email = it },
                                onPasswordChange = { password = it },
                                onRememberMeChange = { rememberMe = it },
                                onLoginClick = { viewModel.signIn(email, password) }
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp, vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(140.dp))
                        WebLoginForm(
                            email = email,
                            password = password,
                            rememberMe = rememberMe,
                            isLoading = state.isLoading,
                            error = state.error,
                            onEmailChange = { email = it },
                            onPasswordChange = { password = it },
                            onRememberMeChange = { rememberMe = it },
                            onLoginClick = { viewModel.signIn(email, password) }
                        )
                    }
                }
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
    private fun WebLoginForm(
        email: String,
        password: String,
        rememberMe: Boolean,
        isLoading: Boolean,
        error: String?,
        onEmailChange: (String) -> Unit,
        onPasswordChange: (String) -> Unit,
        onRememberMeChange: (Boolean) -> Unit,
        onLoginClick: () -> Unit
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.width(380.dp).padding(vertical = 24.dp)
        ) {
            // Logo badge (White rounded box)
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(22.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    Image(
                        painter = painterResource(Res.drawable.app_logo),
                        contentDescription = strings.appName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // App title: Argep
            Text(
                text = "Argep",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 28.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle: Araştırma Yönetim Paneli
            Text(
                text = strings.researchManagementPanel,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 18.sp
                )
            )

            Spacer(modifier = Modifier.height(26.dp))

            // White Form Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = strings.welcomeBack,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            fontSize = 22.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.loginInstructions,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = strings.email.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = Color(0xFF64748B)
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        placeholder = {
                            Text(
                                strings.emailExample,
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = WebInputBg,
                            unfocusedContainerColor = WebInputBg,
                            disabledContainerColor = WebInputBg,
                            focusedBorderColor = Color(0xFFCBD5E1),
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = WebCardBtn,
                            focusedTextColor = WebCardBtn,
                            unfocusedTextColor = WebCardBtn
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = strings.password.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = Color(0xFF64748B)
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        placeholder = {
                            Text(
                                strings.passwordDots,
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = WebInputBg,
                            unfocusedContainerColor = WebInputBg,
                            disabledContainerColor = WebInputBg,
                            focusedBorderColor = Color(0xFFCBD5E1),
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = WebCardBtn,
                            focusedTextColor = WebCardBtn,
                            unfocusedTextColor = WebCardBtn
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = onRememberMeChange,
                            colors = CheckboxDefaults.colors(
                                checkedColor = WebCardBtn,
                                uncheckedColor = Color(0xFF94A3B8),
                                checkmarkColor = Color.White
                            ),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.rememberMe,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF475569),
                                fontSize = 13.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = WebCardBtn,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = onLoginClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WebCardBtn
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = strings.login,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            )
                        }
                    }

                    error?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = it,
                            color = Color(0xFFDC2626),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
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
