package com.example.udemarket.ui.features.gastos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.udemarket.ui.theme.UdeMarketTheme
import java.text.NumberFormat
import java.util.Locale

/**
 * MODELO DE DATOS: Gasto
 * Se utiliza una data class inmutable para representar cada registro.
 */
data class Gasto(
    val id: Int,
    val concepto: String,
    val monto: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen() {
    // --- ESTADO LOCAL (REACTIVE STATE) ---
    // mutableStateListOf permite que Compose rastree cambios en la lista (add/remove) de forma eficiente.
    val listaGastos = remember { mutableStateListOf<Gasto>() }
    
    // Estados para el manejo de los campos de texto
    var conceptoText by remember { mutableStateOf("") }
    var montoText by remember { mutableStateOf("") }
    
    // Cálculo derivado: el total se recalcula automáticamente cada vez que la lista muta.
    val totalGastado = listaGastos.sumOf { it.monto }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "GESTIÓN DE PRESUPUESTO",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- BLOQUE DE ENTRADA (FORMULARIO) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = conceptoText,
                        onValueChange = { conceptoText = it },
                        label = { Text("Concepto del gasto") },
                        placeholder = { Text("Ej. Almuerzo, Fotocopias") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = montoText,
                        onValueChange = { montoText = it },
                        label = { Text("Monto ($)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            val montoVal = montoText.toDoubleOrNull() ?: 0.0
                            // VALIDACIÓN: Se requiere concepto y un monto positivo.
                            if (conceptoText.isNotBlank() && montoVal > 0) {
                                val nuevoGasto = Gasto(
                                    id = (listaGastos.maxOfOrNull { it.id } ?: 0) + 1,
                                    concepto = conceptoText,
                                    monto = montoVal
                                )
                                listaGastos.add(nuevoGasto)
                                conceptoText = ""
                                montoText = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = conceptoText.isNotBlank() && montoText.isNotBlank()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("AGREGAR AL DÍA", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- LISTADO DE GASTOS (LazyColumn) ---
            Text(
                "HISTORIAL DE GASTOS",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                fontWeight = FontWeight.Bold
            )

            if (listaGastos.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("No hay gastos registrados hoy", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listaGastos, key = { it.id }) { gasto ->
                        GastoItemRow(
                            gasto = gasto,
                            onDelete = { listaGastos.remove(gasto) }
                        )
                    }
                }
            }

            // --- SECCIÓN DE TOTALES ---
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("TOTAL GASTADO", style = MaterialTheme.typography.labelSmall)
                        Text(
                            formatToCurrency(totalGastado),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    
                    IconButton(
                        onClick = { listaGastos.clear() },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reiniciar")
                    }
                }
            }
        }
    }
}

@Composable
fun GastoItemRow(gasto: Gasto, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Payments, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(gasto.concepto, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(formatToCurrency(gasto.monto), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
            }
        }
    }
}

/**
 * FORMATEO DE MONEDA: Técnica Senior para asegurar consistencia regional ($ CO).
 */
private fun formatToCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    return format.format(amount)
}

@Preview(showBackground = true)
@Composable
fun GastosScreenPreview() {
    UdeMarketTheme {
        GastosScreen()
    }
}
