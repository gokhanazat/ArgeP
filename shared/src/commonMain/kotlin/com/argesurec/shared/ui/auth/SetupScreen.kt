package com.argesurec.shared.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
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
import com.argesurec.shared.ui.theme.ArgepColors
import com.argesurec.shared.util.strings
import com.argesurec.shared.viewmodel.OrganizationViewModel
import org.koin.compose.viewmodel.koinViewModel

class SetupScreen : Screen {
    @Composable
    override fun Content() {
        val s = strings
        val viewModel = koinViewModel<OrganizationViewModel>()
        val state by viewModel.state.collectAsState()
        
        var businessName by remember { mutableStateOf("") }

        LaunchedEffect(state.isSuccess) {
            if (state.isSuccess) {
                // Success case: The navigation will be handled by MainScreen observing the profile orgId
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ArgepColors.Navy900, ArgepColors.Navy700)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = ArgepColors.White.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Business,
                            null,
                            tint = ArgepColors.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = s.organizationSetup,
                    style = MaterialTheme.typography.headlineMedium,
                    color = ArgepColors.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = s.setupInstructions,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ArgepColors.Navy300,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ArgepColors.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = businessName,
                            onValueChange = { businessName = it },
                            label = { Text(s.businessName) },
                            placeholder = { Text(s.businessNamePlaceholder) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            enabled = !state.isLoading
                        )

                        if (state.error != null) {
                            Text(
                                text = state.error ?: "",
                                color = ArgepColors.Error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }

                        Button(
                            onClick = { viewModel.createOrganization(businessName) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ArgepColors.Navy900,
                                contentColor = ArgepColors.White
                            ),
                            enabled = !state.isLoading && !state.isSuccess && businessName.isNotBlank()
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = ArgepColors.White,
                                    strokeWidth = 2.dp
                                )
                            } else if (state.isSuccess) {
                                Icon(Icons.Default.CheckCircle, null, tint = ArgepColors.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(s.successRedirecting)
                            } else {
                                Text(s.createBusiness, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                TextButton(
                    onClick = { /* Join org flow could be here */ },
                    enabled = !state.isLoading
                ) {
                    Text(s.joiningOrganization, color = ArgepColors.Navy300)
                }
            }
        }
    }
}
