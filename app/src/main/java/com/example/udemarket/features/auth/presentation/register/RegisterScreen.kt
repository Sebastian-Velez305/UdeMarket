package com.example.udemarket.features.auth.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.udemarket.ui.theme.UdeMarketTheme

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val deepPurple = Color(0xFF0F001A)
    val neonPurple = MaterialTheme.colorScheme.primary

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color.Black, deepPurple, Color.Black)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Header Estilizado
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(neonPurple.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                    .border(1.dp, neonPurple.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "U",
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = neonPurple,
                        fontWeight = FontWeight.Black
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Crea tu cuenta",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            )
            
            Text(
                text = "Únete al Marketplace de la UdeA",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Formulario Glassmorphism
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(32.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Nombre Completo
                    RegisterField(
                        value = uiState.name,
                        onValueChange = viewModel::onNameChanged,
                        label = "Nombre Completo",
                        icon = Icons.Default.Person,
                        neonPurple = neonPurple
                    )

                    // Celular
                    RegisterField(
                        value = uiState.phone,
                        onValueChange = viewModel::onPhoneChanged,
                        label = "Número de Celular",
                        icon = Icons.Default.Phone,
                        neonPurple = neonPurple,
                        keyboardType = KeyboardType.Phone
                    )

                    // Correo Institucional
                    RegisterField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChanged,
                        label = "Correo Institucional",
                        icon = Icons.Default.Email,
                        neonPurple = neonPurple,
                        keyboardType = KeyboardType.Email,
                        isError = uiState.isEmailError,
                        errorMessage = uiState.emailErrorMessage
                    )

                    // Carrera (Opcional)
                    RegisterField(
                        value = uiState.career,
                        onValueChange = viewModel::onCareerChanged,
                        label = "Carrera (Opcional)",
                        icon = Icons.Default.School,
                        neonPurple = neonPurple
                    )

                    // Contraseña
                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = neonPurple) },
                        trailingIcon = {
                            val icon = if (uiState.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = viewModel::togglePasswordVisibility) {
                                Icon(icon, null, tint = Color.White.copy(alpha = 0.6f))
                            }
                        },
                        visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = neonPurple,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedLabelColor = neonPurple,
                            cursorColor = neonPurple,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón de Registro
                    Button(
                        onClick = viewModel::register,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        enabled = uiState.isRegisterEnabled && !uiState.isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = neonPurple,
                            contentColor = Color.Black
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                "REGISTRARME",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer
            TextButton(onClick = onNavigateBack) {
                Row {
                    Text("¿Ya tienes cuenta? ", color = Color.White.copy(alpha = 0.6f))
                    Text("Inicia sesión", color = neonPurple, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun RegisterField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    neonPurple: Color,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = neonPurple) },
        isError = isError,
        supportingText = {
            if (isError) {
                Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = neonPurple,
            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
            focusedLabelColor = neonPurple,
            cursorColor = neonPurple,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
fun RegisterScreenPreview() {
    UdeMarketTheme {
        RegisterScreen()
    }
}
