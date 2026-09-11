package com.example.udemarket.features.chat.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.udemarket.data.model.Message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    viewModel: ChatDetailViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val deepPurple = Color(0xFF0F001A)
    val neonPurple = MaterialTheme.colorScheme.primary
    
    // Estado de la lista para controlar el scroll
    val listState = rememberLazyListState()

    // EFECTO SENIOR: Autoscroll al final cuando llegan mensajes nuevos o se abre el chat
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color.Black, deepPurple, Color.Black)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CHAT", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Lista de mensajes optimizada
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.messages) { message ->
                        MessageBubble(
                            message = message,
                            isMine = message.senderId == viewModel.currentUserId,
                            neonPurple = neonPurple
                        )
                    }
                }

                // Barra de entrada con manejo de teclado (imePadding)
                ChatInputBar(
                    text = uiState.newMessageText,
                    onTextChange = viewModel::onMessageTextChanged,
                    onSend = viewModel::sendMessage,
                    neonPurple = neonPurple
                )
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    isMine: Boolean,
    neonPurple: Color
) {
    val bubbleColor = if (isMine) neonPurple else Color.White.copy(alpha = 0.1f)
    val textColor = if (isMine) Color.Black else Color.White
    val alignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
    val shape = if (isMine) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Surface(
            color = bubbleColor,
            shape = shape,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                color = textColor,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    neonPurple: Color
) {
    Surface(
        color = Color.White.copy(alpha = 0.05f),
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding() // Asegura que el teclado no tape el campo de texto
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje...", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                maxLines = 4
            )
            
            FloatingActionButton(
                onClick = onSend,
                containerColor = neonPurple,
                contentColor = Color.Black,
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
            }
        }
    }
}
