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
import kotlinx.datetime.Clock

// Enumeración para los modos de finalización de venta
enum class ModalidadFinalizacion {
    NORMAL,
    COMO_PRESUPUESTO
}

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
    var mostrarDialogoCliente by remember { mutableStateOf(false) }
    var mostrarDialogoPago by remember { mutableStateOf(false) }
    
    val ticketGenerator = rememberTicketGenerator()
    
    // Función para finalizar la venta
    fun finalizarVenta(venta: Venta, modalidad: ModalidadFinalizacion) {
        venta.fechaHora = Clock.System.now()
        venta.completada = true
        
        when (modalidad) {
            ModalidadFinalizacion.NORMAL -> {
                // Asignar número de factura al completar la venta
                venta.numeroFactura = DatosEjemplo.generarNumeroFactura()
                venta.facturada = true
                
                // Aquí iría la lógica para guardar la venta en base de datos
                // Por ahora, podemos agregar la venta a nuestro historial de ejemplo para ver los resultados
                DatosEjemplo.historialVentas.add(0, venta)
            }
            ModalidadFinalizacion.COMO_PRESUPUESTO -> {
                venta.esCotizacion = true
                // Lógica para guardar como presupuesto...
            }
        }
        
        // Limpiar la venta actual y volver al menú
        ventaActual = Venta()
        onBack()
    }
    
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
                            // Crear una nueva instancia de venta
                            ventaActual = Venta(
                                id = ventaActual.id,
                                elementos = nuevosElementos,
                                cliente = ventaActual.cliente,
                                metodoPago = ventaActual.metodoPago,
                                fechaHora = ventaActual.fechaHora,
                                comentarios = ventaActual.comentarios,
                                completada = ventaActual.completada,
                                facturada = ventaActual.facturada,
                                referenciaPago = ventaActual.referenciaPago,
                                numeroFactura = ventaActual.numeroFactura,
                                esCotizacion = ventaActual.esCotizacion
                            )
                        },
                        onDescuentoCambiado = { nuevoDescuento ->
                            val index = ventaActual.elementos.indexOf(elementoVenta)
                            val nuevosElementos = ventaActual.elementos.toMutableList()
                            nuevosElementos[index] = elementoVenta.copy(descuento = nuevoDescuento)
                            // Crear una nueva instancia de venta
                            ventaActual = Venta(
                                id = ventaActual.id,
                                elementos = nuevosElementos,
                                cliente = ventaActual.cliente,
                                metodoPago = ventaActual.metodoPago,
                                fechaHora = ventaActual.fechaHora,
                                comentarios = ventaActual.comentarios,
                                completada = ventaActual.completada,
                                facturada = ventaActual.facturada,
                                referenciaPago = ventaActual.referenciaPago,
                                numeroFactura = ventaActual.numeroFactura,
                                esCotizacion = ventaActual.esCotizacion
                            )
                        },
                        onEliminar = {
                            val nuevosElementos = ventaActual.elementos.toMutableList()
                            nuevosElementos.remove(elementoVenta)
                            // Crear una nueva instancia de venta
                            ventaActual = Venta(
                                id = ventaActual.id,
                                elementos = nuevosElementos,
                                cliente = ventaActual.cliente,
                                metodoPago = ventaActual.metodoPago,
                                fechaHora = ventaActual.fechaHora,
                                comentarios = ventaActual.comentarios,
                                completada = ventaActual.completada,
                                facturada = ventaActual.facturada,
                                referenciaPago = ventaActual.referenciaPago,
                                numeroFactura = ventaActual.numeroFactura,
                                esCotizacion = ventaActual.esCotizacion
                            )
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
                    val nuevosElementos = ventaActual.elementos.toMutableList()
                    
                    if (elementoExistente != null) {
                        // Actualizar cantidad
                        val index = ventaActual.elementos.indexOf(elementoExistente)
                        nuevosElementos[index] = elementoExistente.copy(cantidad = elementoExistente.cantidad + 1)
                    } else {
                        // Agregar nuevo elemento
                        nuevosElementos.add(ElementoVenta(producto = producto))
                    }
                    
                    // Crear una nueva instancia de venta
                    ventaActual = Venta(
                        id = ventaActual.id,
                        elementos = nuevosElementos,
                        cliente = ventaActual.cliente,
                        metodoPago = ventaActual.metodoPago,
                        fechaHora = ventaActual.fechaHora,
                        comentarios = ventaActual.comentarios,
                        completada = ventaActual.completada,
                        facturada = ventaActual.facturada,
                        referenciaPago = ventaActual.referenciaPago,
                        numeroFactura = ventaActual.numeroFactura,
                        esCotizacion = ventaActual.esCotizacion
                    )
                    
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
                        val nuevosElementos = ventaActual.elementos.toMutableList()
                        
                        if (elementoExistente != null) {
                            // Actualizar cantidad
                            val index = ventaActual.elementos.indexOf(elementoExistente)
                            nuevosElementos[index] = elementoExistente.copy(cantidad = elementoExistente.cantidad + 1)
                        } else {
                            // Agregar nuevo elemento
                            nuevosElementos.add(ElementoVenta(producto = productoEncontrado))
                        }
                        
                        // Crear una nueva instancia de venta
                        ventaActual = Venta(
                            id = ventaActual.id,
                            elementos = nuevosElementos,
                            cliente = ventaActual.cliente,
                            metodoPago = ventaActual.metodoPago,
                            fechaHora = ventaActual.fechaHora,
                            comentarios = ventaActual.comentarios,
                            completada = ventaActual.completada,
                            facturada = ventaActual.facturada,
                            referenciaPago = ventaActual.referenciaPago,
                            numeroFactura = ventaActual.numeroFactura,
                            esCotizacion = ventaActual.esCotizacion
                        )
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
            DialogoPago(
                venta = ventaActual,
                onPago = { metodoPago, referencia ->
                    ventaActual.metodoPago = metodoPago
                    ventaActual.referenciaPago = referencia
                    // Finalizar la venta
                    finalizarVenta(ventaActual, ModalidadFinalizacion.NORMAL)
                },
                onDismiss = { mostrarDialogoPago = false }
            )
        }
    }
}

// Diálogo de pago completo
@Composable
fun DialogoPago(
    venta: Venta,
    onPago: (MetodoPago, String) -> Unit,
    onDismiss: () -> Unit
) {
    var metodoPagoSeleccionado by remember { mutableStateOf(MetodoPago.EFECTIVO) }
    var referencia by remember { mutableStateOf("") }
    var showReferencia by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Procesar pago",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Total a cobrar: ${venta.formatoTotal()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = SarapeRojo
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Seleccione método de pago:")
                
                RadioGroupOpcionesPago(
                    options = MetodoPago.values().map { it.descripcion },
                    selectedOption = metodoPagoSeleccionado.descripcion,
                    onOptionSelected = { descripcion ->
                        metodoPagoSeleccionado = MetodoPago.values().find { it.descripcion == descripcion }!!
                        showReferencia = metodoPagoSeleccionado != MetodoPago.EFECTIVO
                    }
                )
                
                if (showReferencia) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = referencia,
                        onValueChange = { referencia = it },
                        label = { 
                            Text(
                                when (metodoPagoSeleccionado) {
                                    MetodoPago.TARJETA_CREDITO, MetodoPago.TARJETA_DEBITO -> "Últimos 4 dígitos"
                                    MetodoPago.TRANSFERENCIA -> "Referencia de transferencia"
                                    else -> "Referencia"
                                }
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onPago(metodoPagoSeleccionado, referencia)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SarapeRojo
                )
            ) {
                Text("Completar pago")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}