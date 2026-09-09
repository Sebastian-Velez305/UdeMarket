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
 * 1. MODELO DE DATOS
 * Definición simple del objeto de dominio para gastos.
 */
data class Gasto(
    val id: Int,
    val concepto: String,
    val monto: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen() {
    // --- ESTADO LOCAL REACTIVO ---
    // mutableStateListOf es la forma Senior de manejar listas dinámicas en Compose,
    // ya que notifica cambios individuales (añadir/eliminar) sin reasignar toda la lista.
    val listaGastos = remember { mutableStateListOf<Gasto>() }
    
    // Estados para los campos de entrada de datos
    var conceptoText by remember { mutableStateOf("") }
    var montoText by remember { mutableStateOf("") }
    
    // Cálculo derivado: se actualiza automáticamente cada vez que muta la lista.
    val totalGastado = listaGastos.sumOf { it.monto }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "PRESUPUESTO SENA", 
                        style = MaterialTheme.typography.titleLarge.copy(
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
            // --- SECCIÓN DE ENTRADA (FORMULARIO) ---
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
                        label = { Text("¿En qué gastaste?") },
                        placeholder = { Text("Ej. Fotocopias, Almuerzo") },
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
                            // VALIDACIÓN: Evitamos entradas vacías o valores inválidos.
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
                        Text("AGREGAR GASTO", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- LISTA DE GASTOS (HISTORIAL) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "DETALLE DEL DÍA",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${listaGastos.size} registros",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(listaGastos, key = { it.id }) { gasto ->
                    GastoItemRow(
                        gasto = gasto,
                        onDelete = { listaGastos.remove(gasto) }
                    )
                }
            }

            // --- SECCIÓN DE TOTALES Y ACCIONES GLOBALES ---
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            
            Surface(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("TOTAL ACUMULADO", style = MaterialTheme.typography.labelSmall)
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
                            containerColor = MaterialTheme.colorScheme.errorContainer,
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
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Payments, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(gasto.concepto, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(formatToCurrency(gasto.monto), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
            }
        }
    }
}

/**
 * FORMATEO DE MONEDA: Utiliza la configuración local para asegurar que los pesos colombianos
 * se muestren correctamente ($ 2.500,00).
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
