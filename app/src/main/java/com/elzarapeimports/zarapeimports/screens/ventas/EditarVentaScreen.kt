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
import com.elzarapeimports.zarapeimports.model.*
import com.elzarapeimports.zarapeimports.ui.theme.*
import kotlinx.datetime.Clock

/**
 * Pantalla para editar una venta existente
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarVentaScreen(
    venta: Venta,
    onBack: () -> Unit
) {
    var ventaActual by remember { mutableStateOf(venta) }
    var mostrarDialogoProducto by remember { mutableStateOf(false) }
    var mostrarDialogoCliente by remember { mutableStateOf(false) }
    var mostrarDialogoConfirmacion by remember { mutableStateOf(false) }
    var mostrarDialogoMetodoPago by remember { mutableStateOf(false) }
    var cambiosGuardados by remember { mutableStateOf(false) }
    
    // Verificar si hay cambios
    val hayModificaciones = ventaActual != venta
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Venta") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (hayModificaciones && !cambiosGuardados) {
                            mostrarDialogoConfirmacion = true
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SarapeBackground
                ),
                actions = {
                    if (hayModificaciones) {
                        IconButton(onClick = {
                            // Guardar los cambios
                            actualizarVentaEnHistorial(ventaActual)
                            cambiosGuardados = true
                            onBack()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Save,
                                contentDescription = "Guardar cambios",
                                tint = SarapeVerde
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Información de la factura
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Factura: ${ventaActual.numeroFactura}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SarapeRojo
                    )
                    Text(
                        text = "Fecha: ${ventaActual.formatoFecha()}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                
                Row {
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
                        IconButton(onClick = { 
                            // Crear nueva instancia de venta sin cliente
                            ventaActual = Venta(
                                id = ventaActual.id,
                                elementos = ventaActual.elementos,
                                cliente = null,
                                metodoPago = ventaActual.metodoPago,
                                fechaHora = ventaActual.fechaHora,
                                comentarios = ventaActual.comentarios,
                                completada = ventaActual.completada,
                                facturada = ventaActual.facturada,
                                referenciaPago = ventaActual.referenciaPago,
                                numeroFactura = ventaActual.numeroFactura,
                                esCotizacion = ventaActual.esCotizacion
                            )
                        }) {
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
                            // Creamos una venta completamente nueva con los nuevos elementos
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
                            // Creamos una venta completamente nueva con los nuevos elementos
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
                            // Creamos una venta completamente nueva con los nuevos elementos
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
            
            // Método de pago
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SarapeBackground
                ),
                shape = RoundedCornerShape(8.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Método de Pago",
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ventaActual.metodoPago.descripcion,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Button(
                            onClick = {
                                // Mostrar diálogo para cambiar método de pago
                                mostrarDialogoMetodoPago = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SarapeAzul
                            )
                        ) {
                            Text("Cambiar")
                        }
                    }
                    
                    if (ventaActual.referenciaPago.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Referencia: ${ventaActual.referenciaPago}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            // Botón de guardar
            Button(
                onClick = {
                    // Guardar los cambios
                    actualizarVentaEnHistorial(ventaActual)
                    cambiosGuardados = true
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SarapeVerde
                ),
                enabled = hayModificaciones && ventaActual.elementos.isNotEmpty()
            ) {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GUARDAR CAMBIOS",
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
                    
                    // Creamos una venta completamente nueva con los nuevos elementos
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
        
        if (mostrarDialogoCliente) {
            SeleccionClienteDialog(
                onClienteSeleccionado = { cliente ->
                    // Crear nueva instancia de venta con el cliente seleccionado
                    ventaActual = Venta(
                        id = ventaActual.id,
                        elementos = ventaActual.elementos,
                        cliente = cliente,
                        metodoPago = ventaActual.metodoPago,
                        fechaHora = ventaActual.fechaHora,
                        comentarios = ventaActual.comentarios,
                        completada = ventaActual.completada,
                        facturada = ventaActual.facturada,
                        referenciaPago = ventaActual.referenciaPago,
                        numeroFactura = ventaActual.numeroFactura,
                        esCotizacion = ventaActual.esCotizacion
                    )
                    mostrarDialogoCliente = false
                },
                onDismiss = { mostrarDialogoCliente = false }
            )
        }
        
        if (mostrarDialogoMetodoPago) {
            DialogoMetodoPago(
                metodoPagoActual = ventaActual.metodoPago,
                referenciaActual = ventaActual.referenciaPago,
                onConfirmar = { metodoPago, referencia ->
                    // Crear nueva instancia de venta con el método de pago actualizado
                    ventaActual = Venta(
                        id = ventaActual.id,
                        elementos = ventaActual.elementos,
                        cliente = ventaActual.cliente,
                        metodoPago = metodoPago,
                        fechaHora = ventaActual.fechaHora,
                        comentarios = ventaActual.comentarios,
                        completada = ventaActual.completada,
                        facturada = ventaActual.facturada,
                        referenciaPago = referencia,
                        numeroFactura = ventaActual.numeroFactura,
                        esCotizacion = ventaActual.esCotizacion
                    )
                    mostrarDialogoMetodoPago = false
                },
                onDismiss = { 
                    mostrarDialogoMetodoPago = false 
                }
            )
        }
        
        if (mostrarDialogoConfirmacion) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoConfirmacion = false },
                title = { Text("Cambios sin guardar") },
                text = { Text("¿Desea salir sin guardar los cambios?") },
                confirmButton = {
                    Button(
                        onClick = { onBack() }
                    ) {
                        Text("Salir sin guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoConfirmacion = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

// Función para actualizar una venta en el historial
private fun actualizarVentaEnHistorial(venta: Venta) {
    // Buscar la venta en el historial
    val indice = DatosEjemplo.historialVentas.indexOfFirst { it.id == venta.id }
    if (indice >= 0) {
        // Reemplazar la venta en el historial
        DatosEjemplo.historialVentas[indice] = venta
    }
}

// Diálogo para cambiar el método de pago
@Composable
fun DialogoMetodoPago(
    metodoPagoActual: MetodoPago,
    referenciaActual: String,
    onConfirmar: (MetodoPago, String) -> Unit,
    onDismiss: () -> Unit
) {
    var metodoPagoSeleccionado by remember { mutableStateOf(metodoPagoActual) }
    var referencia by remember { mutableStateOf(referenciaActual) }
    var showReferencia by remember { mutableStateOf(metodoPagoActual != MetodoPago.EFECTIVO) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Cambiar método de pago",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text("Seleccione el nuevo método de pago:")
                
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
                    onConfirmar(metodoPagoSeleccionado, referencia)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SarapeVerde
                )
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun RadioGroupOpcionesPago(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onOptionSelected(option) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = option == selectedOption,
                    onClick = { onOptionSelected(option) }
                )
                Text(text = option)
            }
        }
    }
} 