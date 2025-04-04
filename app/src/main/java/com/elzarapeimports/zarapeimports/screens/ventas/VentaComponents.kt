package com.elzarapeimports.zarapeimports.screens.ventas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.elzarapeimports.zarapeimports.model.*
import com.elzarapeimports.zarapeimports.ui.theme.*

/**
 * Elemento de venta individual
 */
@Composable
fun ElementoVentaItem(
    elementoVenta: ElementoVenta,
    onCantidadCambiada: (Int) -> Unit,
    onDescuentoCambiado: (Double) -> Unit,
    onEliminar: () -> Unit
) {
    var showEditarCantidad by remember { mutableStateOf(false) }
    var showEditarDescuento by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Información del producto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = elementoVenta.producto.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    
                    Text(
                        text = "Código: ${elementoVenta.producto.codigo}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    if (elementoVenta.descuento > 0) {
                        Text(
                            text = "Descuento: ${(elementoVenta.descuento * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = SarapeNaranja
                        )
                    }
                }
                
                IconButton(
                    onClick = onEliminar,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar",
                        tint = SarapeRojo
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Cantidad y precio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        onClick = {
                            if (elementoVenta.cantidad > 1) {
                                onCantidadCambiada(elementoVenta.cantidad - 1)
                            }
                        },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = SarapeRojo.copy(alpha = 0.1f),
                            contentColor = SarapeRojo
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Reducir cantidad",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White, shape = RoundedCornerShape(4.dp))
                            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(4.dp))
                            .clickable { showEditarCantidad = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${elementoVenta.cantidad}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    FilledIconButton(
                        onClick = { onCantidadCambiada(elementoVenta.cantidad + 1) },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = SarapeVerde.copy(alpha = 0.1f),
                            contentColor = SarapeVerde
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Aumentar cantidad",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$${String.format("%.2f", elementoVenta.subtotal())}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = SarapeBrown
                    )
                    
                    Row {
                        Text(
                            text = "$${String.format("%.2f", elementoVenta.producto.precio)} c/u",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar descuento",
                            tint = SarapeAzul,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { showEditarDescuento = true }
                        )
                    }
                }
            }
        }
    }
    
    // Diálogos para editar cantidad y descuento
    if (showEditarCantidad) {
        var cantidadText by remember { mutableStateOf(elementoVenta.cantidad.toString()) }
        
        AlertDialog(
            onDismissRequest = { showEditarCantidad = false },
            title = { Text("Editar cantidad") },
            text = {
                OutlinedTextField(
                    value = cantidadText,
                    onValueChange = { 
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            cantidadText = it
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    label = { Text("Cantidad") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cantidad = cantidadText.toIntOrNull() ?: 1
                        if (cantidad > 0) {
                            onCantidadCambiada(cantidad)
                        }
                        showEditarCantidad = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditarCantidad = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    if (showEditarDescuento) {
        var descuentoText by remember { mutableStateOf((elementoVenta.descuento * 100).toInt().toString()) }
        
        AlertDialog(
            onDismissRequest = { showEditarDescuento = false },
            title = { Text("Editar descuento") },
            text = {
                Column {
                    Text("Ingrese el porcentaje de descuento (0-100)")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = descuentoText,
                        onValueChange = { 
                            if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.toIntOrNull() ?: 0 <= 100)) {
                                descuentoText = it
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        label = { Text("Descuento %") },
                        trailingIcon = { Text("%") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val descuento = (descuentoText.toDoubleOrNull() ?: 0.0) / 100.0
                        onDescuentoCambiado(descuento)
                        showEditarDescuento = false
                    }
                ) {
                    Text("Aplicar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditarDescuento = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Diálogo para seleccionar un producto
 */
@Composable
fun SeleccionProductoDialog(
    onProductoSeleccionado: (Producto) -> Unit,
    onDismiss: () -> Unit
) {
    val productos = remember { DatosEjemplo.productosEjemplo }
    var busqueda by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SarapeBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Seleccionar Producto",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    placeholder = { Text("Buscar por nombre o código") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                
                // Lista de productos
                val productosFiltrados = productos.filter {
                    busqueda.isEmpty() || 
                    it.nombre.contains(busqueda, ignoreCase = true) ||
                    it.codigo.contains(busqueda, ignoreCase = true)
                }
                
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(productosFiltrados) { producto ->
                        ProductoItem(
                            producto = producto,
                            onClick = { onProductoSeleccionado(producto) }
                        )
                        
                        Divider()
                    }
                    
                    if (productosFiltrados.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No se encontraron productos",
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}

/**
 * Item de producto para la lista de selección
 */
@Composable
fun ProductoItem(
    producto: Producto,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = producto.nombre,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Código: ${producto.codigo} | ${producto.categoria}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            
            if (producto.existencias <= 5) {
                Text(
                    text = "Existencias: ${producto.existencias} (¡Bajo inventario!)",
                    fontSize = 12.sp,
                    color = SarapeRojo
                )
            } else {
                Text(
                    text = "Existencias: ${producto.existencias}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = producto.formatoPrecio(),
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = producto.formatoPrecioConImpuesto(),
                fontSize = 12.sp,
                color = SarapeAzul
            )
        }
    }
}

/**
 * Diálogo para escanear códigos de barras/QR
 */
@Composable
fun EscaneoDialog(
    onCodigoDetectado: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var codigoManual by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SarapeBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Escanear Código",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Vista simulada de la cámara
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFF222222), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QrCodeScanner,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(64.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Simulador de Escaneo",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Por favor, ingresa un código manualmente",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                }
                
                // Campo para introducir el código manualmente
                OutlinedTextField(
                    value = codigoManual,
                    onValueChange = { codigoManual = it },
                    label = { Text("Código de producto") },
                    placeholder = { Text("Ejemplo: 0001") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text("Cancelar")
                    }
                    
                    Button(
                        onClick = {
                            if (codigoManual.isNotEmpty()) {
                                onCodigoDetectado(codigoManual)
                            } else {
                                // Para pruebas, enviamos un código predeterminado
                                onCodigoDetectado("0001")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Text("Aceptar")
                    }
                }
                
                // Códigos de prueba
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SarapeAzul.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "Códigos de prueba:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DatosEjemplo.productosEjemplo.take(5).forEach { producto ->
                                Button(
                                    onClick = { onCodigoDetectado(producto.codigo) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SarapeAzul
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text(
                                        text = producto.codigo,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Diálogo para seleccionar un cliente
 */
@Composable
fun SeleccionClienteDialog(
    onClienteSeleccionado: (Cliente) -> Unit,
    onDismiss: () -> Unit
) {
    val clientes = remember { DatosEjemplo.clientesEjemplo }
    var busqueda by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SarapeBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Seleccionar Cliente",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    placeholder = { Text("Buscar por nombre o RFC") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                
                // Lista de clientes
                val clientesFiltrados = clientes.filter {
                    busqueda.isEmpty() || 
                    it.nombre.contains(busqueda, ignoreCase = true) ||
                    it.rfc.contains(busqueda, ignoreCase = true)
                }
                
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(clientesFiltrados) { cliente ->
                        ClienteItem(
                            cliente = cliente,
                            onClick = { onClienteSeleccionado(cliente) }
                        )
                        
                        Divider()
                    }
                    
                    if (clientesFiltrados.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No se encontraron clientes",
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}

/**
 * Item de cliente para la lista de selección
 */
@Composable
fun ClienteItem(
    cliente: Cliente,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
            tint = SarapeAzul,
            modifier = Modifier.padding(end = 16.dp)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cliente.nombre,
                fontWeight = FontWeight.Bold
            )
            
            if (cliente.rfc.isNotEmpty()) {
                Text(
                    text = "RFC: ${cliente.rfc}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            if (cliente.telefono.isNotEmpty()) {
                Text(
                    text = "Tel: ${cliente.telefono}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

/**
 * Diálogo para procesar el pago
 */
@Composable
fun PagoDialog(
    venta: Venta,
    onPago: (MetodoPago, String) -> Unit,
    onDismiss: () -> Unit
) {
    var metodoPagoSeleccionado by remember { mutableStateOf(MetodoPago.EFECTIVO) }
    var referenciaPago by remember { mutableStateOf("") }
    var mostrarDialogoExito by remember { mutableStateOf(false) }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SarapeBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Procesar Pago",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Totales
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SarapeRojo.copy(alpha = 0.1f)
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
                            Text(text = venta.formatoSubtotal())
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Impuestos:")
                            Text(text = venta.formatoImpuestos())
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
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = venta.formatoTotal(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
                
                // Método de pago
                Text(
                    text = "Método de Pago",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    MetodoPago.values().forEach { metodo ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { metodoPagoSeleccionado = metodo }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = metodoPagoSeleccionado == metodo,
                                onClick = { metodoPagoSeleccionado = metodo }
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Icon(
                                imageVector = when (metodo) {
                                    MetodoPago.EFECTIVO -> Icons.Filled.Payments
                                    MetodoPago.TARJETA_DEBITO -> Icons.Filled.CreditCard
                                    MetodoPago.TARJETA_CREDITO -> Icons.Filled.Payment
                                    MetodoPago.TRANSFERENCIA -> Icons.Filled.AccountBalance
                                    MetodoPago.OTRO -> Icons.Filled.MoreHoriz
                                },
                                contentDescription = null,
                                tint = SarapeAzul
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = when (metodo) {
                                    MetodoPago.EFECTIVO -> "Efectivo"
                                    MetodoPago.TARJETA_DEBITO -> "Tarjeta de débito"
                                    MetodoPago.TARJETA_CREDITO -> "Tarjeta de crédito"
                                    MetodoPago.TRANSFERENCIA -> "Transferencia bancaria"
                                    MetodoPago.OTRO -> "Otro método"
                                }
                            )
                        }
                    }
                }
                
                // Campo de referencia (visible solo para ciertos métodos de pago)
                if (metodoPagoSeleccionado != MetodoPago.EFECTIVO) {
                    OutlinedTextField(
                        value = referenciaPago,
                        onValueChange = { referenciaPago = it },
                        label = { Text("Referencia de pago") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }
                
                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text("Cancelar")
                    }
                    
                    Button(
                        onClick = {
                            mostrarDialogoExito = true
                            onPago(metodoPagoSeleccionado, referenciaPago)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SarapeVerde
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Finalizar")
                    }
                }
            }
        }
    }
    
    // Diálogo de éxito
    if (mostrarDialogoExito) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Venta Completada") },
            text = { 
                Text("La venta ha sido procesada correctamente y se ha generado el ticket.")
            },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoExito = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SarapeVerde
                    )
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
} 