package com.elzarapeimports.zarapeimports.screens.ventas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.elzarapeimports.zarapeimports.model.*
import com.elzarapeimports.zarapeimports.ui.theme.*
import com.elzarapeimports.zarapeimports.util.rememberTicketGenerator

/**
 * Pantalla para crear una nueva venta
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaVentaScreen(
    onBack: () -> Unit
) {
    var ventaActual by remember { mutableStateOf(Venta()) }
    var mostrarDialogoProducto by remember { mutableStateOf(false) }
    var mostrarDialogoEscaneo by remember { mutableStateOf(false) }
    var mostrarDialogoPago by remember { mutableStateOf(false) }
    var mostrarDialogoCliente by remember { mutableStateOf(false) }
    
    val ticketGenerator = rememberTicketGenerator()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Venta") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SarapeBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Título y botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Venta ${ventaActual.id.substring(0, 8)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SarapeRojo
                )
                
                Row {
                    IconButton(
                        onClick = { mostrarDialogoEscaneo = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(SarapeRojo.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QrCodeScanner,
                            contentDescription = "Escanear código",
                            tint = SarapeRojo
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = { mostrarDialogoProducto = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(SarapeVerde.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Agregar producto",
                            tint = SarapeVerde
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = { mostrarDialogoCliente = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(SarapeAzul.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Seleccionar cliente",
                            tint = SarapeAzul
                        )
                    }
                }
            }
            
            // Cliente seleccionado
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SarapeBackground
                ),
                shape = RoundedCornerShape(8.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = SarapeAzul
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = ventaActual.cliente?.nombre ?: "Cliente no seleccionado",
                            fontWeight = FontWeight.Bold
                        )
                        if (ventaActual.cliente != null) {
                            Text(
                                text = ventaActual.cliente?.rfc ?: "",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    
                    if (ventaActual.cliente != null) {
                        IconButton(onClick = { ventaActual = ventaActual.copy(cliente = null) }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Quitar cliente",
                                tint = SarapeRojo
                            )
                        }
                    }
                }
            }
            
            // Lista de productos
            Text(
                text = "Productos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                items(ventaActual.elementos) { elementoVenta ->
                    ElementoVentaItem(
                        elementoVenta = elementoVenta,
                        onCantidadCambiada = { nuevaCantidad ->
                            val index = ventaActual.elementos.indexOf(elementoVenta)
                            val nuevosElementos = ventaActual.elementos.toMutableList()
                            nuevosElementos[index] = elementoVenta.copy(cantidad = nuevaCantidad)
                            ventaActual = ventaActual.copy(elementos = nuevosElementos)
                        },
                        onDescuentoCambiado = { nuevoDescuento ->
                            val index = ventaActual.elementos.indexOf(elementoVenta)
                            val nuevosElementos = ventaActual.elementos.toMutableList()
                            nuevosElementos[index] = elementoVenta.copy(descuento = nuevoDescuento)
                            ventaActual = ventaActual.copy(elementos = nuevosElementos)
                        },
                        onEliminar = {
                            val nuevosElementos = ventaActual.elementos.toMutableList()
                            nuevosElementos.remove(elementoVenta)
                            ventaActual = ventaActual.copy(elementos = nuevosElementos)
                        }
                    )
                    
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.LightGray
                    )
                }
                
                if (ventaActual.elementos.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay productos agregados",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            
            // Resumen de totales
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SarapeBackground
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Subtotal:")
                        Text(text = ventaActual.formatoSubtotal())
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Impuestos:")
                        Text(text = ventaActual.formatoImpuestos())
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "TOTAL:",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = ventaActual.formatoTotal(),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Botón de cobrar
            Button(
                onClick = { mostrarDialogoPago = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SarapeRojo
                ),
                enabled = ventaActual.elementos.isNotEmpty()
            ) {
                Icon(
                    imageVector = Icons.Filled.Payment,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "COBRAR ${ventaActual.formatoTotal()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Diálogos
        if (mostrarDialogoProducto) {
            SeleccionProductoDialog(
                onProductoSeleccionado = { producto ->
                    // Revisar si el producto ya está en la lista
                    val elementoExistente = ventaActual.elementos.find { it.producto.id == producto.id }
                    if (elementoExistente != null) {
                        // Actualizar cantidad
                        val index = ventaActual.elementos.indexOf(elementoExistente)
                        val nuevosElementos = ventaActual.elementos.toMutableList()
                        nuevosElementos[index] = elementoExistente.copy(cantidad = elementoExistente.cantidad + 1)
                        ventaActual = ventaActual.copy(elementos = nuevosElementos)
                    } else {
                        // Agregar nuevo elemento
                        val nuevosElementos = ventaActual.elementos.toMutableList()
                        nuevosElementos.add(ElementoVenta(producto = producto))
                        ventaActual = ventaActual.copy(elementos = nuevosElementos)
                    }
                    mostrarDialogoProducto = false
                },
                onDismiss = { mostrarDialogoProducto = false }
            )
        }
        
        if (mostrarDialogoEscaneo) {
            EscaneoDialog(
                onCodigoDetectado = { codigo ->
                    // Buscar el producto por código
                    val productoEncontrado = DatosEjemplo.productosEjemplo.find { it.codigo == codigo }
                    if (productoEncontrado != null) {
                        // Revisar si el producto ya está en la lista
                        val elementoExistente = ventaActual.elementos.find { it.producto.id == productoEncontrado.id }
                        if (elementoExistente != null) {
                            // Actualizar cantidad
                            val index = ventaActual.elementos.indexOf(elementoExistente)
                            val nuevosElementos = ventaActual.elementos.toMutableList()
                            nuevosElementos[index] = elementoExistente.copy(cantidad = elementoExistente.cantidad + 1)
                            ventaActual = ventaActual.copy(elementos = nuevosElementos)
                        } else {
                            // Agregar nuevo elemento
                            val nuevosElementos = ventaActual.elementos.toMutableList()
                            nuevosElementos.add(ElementoVenta(producto = productoEncontrado))
                            ventaActual = ventaActual.copy(elementos = nuevosElementos)
                        }
                    }
                    mostrarDialogoEscaneo = false
                },
                onDismiss = { mostrarDialogoEscaneo = false }
            )
        }
        
        if (mostrarDialogoCliente) {
            SeleccionClienteDialog(
                onClienteSeleccionado = { cliente ->
                    ventaActual = ventaActual.copy(cliente = cliente)
                    mostrarDialogoCliente = false
                },
                onDismiss = { mostrarDialogoCliente = false }
            )
        }
        
        if (mostrarDialogoPago) {
            PagoDialog(
                venta = ventaActual,
                onPago = { metodoPago, referencia ->
                    // Finalizar la venta
                    val ventaFinalizada = ventaActual.copy(
                        metodoPago = metodoPago,
                        referenciaPago = referencia,
                        completada = true,
                        fechaHora = kotlinx.datetime.Clock.System.now()
                    )
                    
                    // Generar ticket
                    val rutaTicket = ticketGenerator.generarTicketPDF(ventaFinalizada)
                    
                    // Resetear la venta actual
                    ventaActual = Venta()
                    
                    mostrarDialogoPago = false
                    onBack()
                },
                onDismiss = { mostrarDialogoPago = false }
            )
        }
    }
}