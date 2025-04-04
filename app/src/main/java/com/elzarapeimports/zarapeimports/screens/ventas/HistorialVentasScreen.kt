package com.elzarapeimports.zarapeimports.screens.ventas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elzarapeimports.zarapeimports.model.DatosEjemplo
import com.elzarapeimports.zarapeimports.model.ElementoVenta
import com.elzarapeimports.zarapeimports.model.Venta
import com.elzarapeimports.zarapeimports.ui.theme.SarapeAzul
import com.elzarapeimports.zarapeimports.ui.theme.SarapeBackground
import com.elzarapeimports.zarapeimports.ui.theme.SarapeBrown
import com.elzarapeimports.zarapeimports.ui.theme.SarapeNaranja
import com.elzarapeimports.zarapeimports.ui.theme.SarapeRojo
import com.elzarapeimports.zarapeimports.ui.theme.SarapeVerde
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone
import java.util.*
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.MoreHoriz

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialVentasScreen(
    onBack: () -> Unit,
    onEditarVenta: (Venta) -> Unit = {}
) {
    var ventasFiltradas by remember { mutableStateOf<List<Venta>>(DatosEjemplo.historialVentas) }
    var busqueda by remember { mutableStateOf("") }
    var filtroFecha by remember { mutableStateOf("") }
    var ventaSeleccionada by remember { mutableStateOf<Venta?>(null) }
    var showDetalleDialog by remember { mutableStateOf(false) }
    var mostrarVentaDetalle by remember { mutableStateOf(false) }
    
    // Función para actualizar los filtros
    fun actualizarFiltros(nuevaBusqueda: String = busqueda, nuevoFiltroFecha: String = filtroFecha) {
        busqueda = nuevaBusqueda
        filtroFecha = nuevoFiltroFecha
        ventasFiltradas = filtrarVentas(busqueda, filtroFecha)
    }
    
    // Agrupar ventas por fecha para la visualización
    val ventasAgrupadas = ventasFiltradas.groupBy { venta ->
        venta.formatoFechaCorta()
    }
    
    if (mostrarVentaDetalle && ventaSeleccionada != null) {
        // Pantalla de detalle de venta
        DetalleVentaScreen(
            venta = ventaSeleccionada!!,
            onBack = { mostrarVentaDetalle = false }
        )
    } else {
        // Pantalla principal de historial
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Historial de Ventas") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
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
                    .padding(horizontal = 16.dp)
            ) {
                // Barra de búsqueda y filtros
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { actualizarFiltros(nuevaBusqueda = it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    placeholder = { Text("Buscar por cliente o número de factura") },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Buscar")
                    },
                    trailingIcon = {
                        if (busqueda.isNotEmpty()) {
                            IconButton(onClick = { actualizarFiltros(nuevaBusqueda = "") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Limpiar")
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SarapeNaranja,
                        unfocusedBorderColor = SarapeBrown.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                
                // Chips para filtrar por fecha
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filtroFecha == "hoy",
                        onClick = { actualizarFiltros(nuevoFiltroFecha = if (filtroFecha == "hoy") "" else "hoy") },
                        label = { Text("Hoy") },
                        leadingIcon = {
                            if (filtroFecha == "hoy") {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        }
                    )
                    
                    FilterChip(
                        selected = filtroFecha == "semana",
                        onClick = { actualizarFiltros(nuevoFiltroFecha = if (filtroFecha == "semana") "" else "semana") },
                        label = { Text("Esta semana") },
                        leadingIcon = {
                            if (filtroFecha == "semana") {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        }
                    )
                    
                    FilterChip(
                        selected = filtroFecha == "mes",
                        onClick = { actualizarFiltros(nuevoFiltroFecha = if (filtroFecha == "mes") "" else "mes") },
                        label = { Text("Este mes") },
                        leadingIcon = {
                            if (filtroFecha == "mes") {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        }
                    )
                }
                
                // Lista de ventas agrupadas por fecha
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ventasAgrupadas.forEach { (fecha, ventas) ->
                        item {
                            FechaHeader(fecha)
                        }
                        
                        items(ventas) { venta ->
                            VentaCard(
                                venta = venta,
                                onClick = {
                                    ventaSeleccionada = venta
                                    mostrarVentaDetalle = true
                                }
                            )
                        }
                    }
                    
                    if (ventasFiltradas.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No se encontraron ventas con los filtros actuales",
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleVentaScreen(
    venta: Venta,
    onBack: () -> Unit
) {
    var mostrarDialogoEmail by remember { mutableStateOf(false) }
    var emailDestino by remember { mutableStateOf("") }
    var mostrarMensajeCopia by remember { mutableStateOf(false) }
    var mostrarMensajeEmail by remember { mutableStateOf(false) }
    var mostrarCarrito by remember { mutableStateOf(false) }
    
    if (mostrarCarrito) {
        // Mostramos la pantalla de carrito
        CarritoScreen(
            venta = venta,
            onBack = { mostrarCarrito = false }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Volver",
                                modifier = Modifier
                                    .clickable { onBack() }
                                    .padding(end = 16.dp)
                            )
                            Text(
                                text = "ORDEN#: ${venta.numeroFactura.removePrefix("#")}",
                                color = Color(0xFF00A899), // Color turquesa similar al de la imagen
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                // Cliente y datos principales
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    val clienteNombre = venta.cliente?.nombre ?: "Cliente no especificado"
                    val clienteInfo = venta.cliente?.rfc ?: ""
                    
                    Text(
                        text = clienteNombre,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003366) // Color azul oscuro
                    )
                    
                    Text(
                        text = "$clienteNombre, ${venta.cliente?.direccion ?: "Sin dirección"}",
                        color = Color.Gray
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Orden#: ${venta.numeroFactura.removePrefix("#")}",
                            color = Color.Gray
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Cuenta: ${venta.cliente?.id?.takeLast(4) ?: "----"}",
                            color = Color.Gray
                        )
                    }
                    
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fecha de envío: ",
                            color = Color.Gray
                        )
                        Text(
                            text = venta.formatoFechaCorta(),
                            color = Color(0xFF003366),
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        // Icono de editar notas
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar notas",
                            tint = Color(0xFFFF6666),
                            modifier = Modifier
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .border(
                                    width = 2.dp,
                                    color = Color(0xFF003366),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(4.dp)
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Notas: ",
                            color = Color.Gray
                        )
                        Text(
                            text = venta.comentarios,
                            color = Color.Gray
                        )
                    }
                }
                
                Divider(color = Color.LightGray, thickness = 1.dp)
                
                // Sección de opciones
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    // Opción de Carrito
                    OpcionDetalle(
                        icono = Icons.Filled.ShoppingCart,
                        texto = "Carrito",
                        contador = venta.elementos.size,
                        colorIcono = Color(0xFF003366),
                        onClick = {
                            mostrarCarrito = true
                        }
                    )
                    
                    Divider(color = Color.LightGray, thickness = 1.dp)
                    
                    // Opción de Firma
                    OpcionDetalle(
                        icono = Icons.Filled.Create,
                        texto = "Firma",
                        colorIcono = Color(0xFFFF6666),
                        onClick = {
                            // Aquí iría la lógica para capturar firma
                        }
                    )
                    
                    Divider(color = Color.LightGray, thickness = 1.dp)
                    
                    // Opción de Imprimir
                    OpcionDetalle(
                        icono = Icons.Filled.Print,
                        texto = "Imprimir",
                        colorIcono = Color(0xFF003366),
                        onClick = {
                            // Imprimir no disponible temporalmente
                        }
                    )
                    
                    Divider(color = Color.LightGray, thickness = 1.dp)
                    
                    // Opción de Email
                    OpcionDetalle(
                        icono = Icons.Filled.Email,
                        texto = "eMail",
                        colorIcono = Color(0xFF003366),
                        onClick = {
                            mostrarDialogoEmail = true
                        }
                    )
                    
                    Divider(color = Color.LightGray, thickness = 1.dp)
                    
                    // Opción de Descuento
                    OpcionDetalle(
                        icono = Icons.Filled.LocalOffer,
                        texto = "Descuento",
                        colorIcono = Color(0xFF003366),
                        onClick = {
                            // Aquí iría la lógica para aplicar descuentos
                        }
                    )
                    
                    Divider(color = Color.LightGray, thickness = 1.dp)
                    
                    // Opción de Copiar
                    OpcionDetalle(
                        icono = Icons.Filled.ContentCopy,
                        texto = "Copiar",
                        colorIcono = Color(0xFF003366),
                        onClick = {
                            // Duplicar la venta
                            duplicarVenta(venta)
                            mostrarMensajeCopia = true
                        }
                    )
                    
                    Divider(color = Color.LightGray, thickness = 1.dp)
                    
                    // Opción de Documentos
                    OpcionDetalle(
                        icono = Icons.Filled.Folder,
                        texto = "Documentos",
                        colorIcono = Color(0xFF003366),
                        onClick = {
                            // Aquí iría la lógica para ver documentos asociados
                        }
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Total en la parte inferior
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = Color(0xFF003366)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL ${venta.formatoTotal()}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
    
    // Diálogo para enviar email
    if (mostrarDialogoEmail) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEmail = false },
            title = { Text("Enviar factura por email") },
            text = {
                Column {
                    Text("Ingrese el correo electrónico para enviar la factura en formato PDF")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = emailDestino,
                        onValueChange = { emailDestino = it },
                        label = { Text("Correo electrónico") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Generar PDF y simular envío
                        if (emailDestino.isNotEmpty() && emailValido(emailDestino)) {
                            generarYEnviarPdf(venta, emailDestino)
                            mostrarDialogoEmail = false
                            mostrarMensajeEmail = true
                        }
                    },
                    enabled = emailDestino.isNotEmpty() && emailValido(emailDestino)
                ) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEmail = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Mensaje de confirmación de email enviado
    if (mostrarMensajeEmail) {
        AlertDialog(
            onDismissRequest = { mostrarMensajeEmail = false },
            title = { Text("Email enviado") },
            text = { 
                Text("La factura ha sido enviada correctamente a $emailDestino")
            },
            confirmButton = {
                Button(onClick = { mostrarMensajeEmail = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
    
    // Diálogo confirmación de copia
    if (mostrarMensajeCopia) {
        AlertDialog(
            onDismissRequest = { mostrarMensajeCopia = false },
            title = { Text("Venta duplicada") },
            text = { 
                Text("La venta se ha duplicado correctamente y se ha añadido al historial como borrador.")
            },
            confirmButton = {
                Button(onClick = { mostrarMensajeCopia = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
fun OpcionDetalle(
    icono: ImageVector,
    texto: String,
    contador: Int? = null,
    colorIcono: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (contador != null && contador > 0) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icono,
                    contentDescription = texto,
                    tint = colorIcono,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .offset(x = 8.dp, y = (-8).dp)
                        .size(20.dp)
                        .background(Color(0xFFFF6666), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contador.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            Icon(
                imageVector = icono,
                contentDescription = texto,
                tint = colorIcono,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = texto,
            fontSize = 16.sp,
            color = Color(0xFF003366)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

@Composable
fun FechaHeader(fecha: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = fecha,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = SarapeBrown
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentaCard(
    venta: Venta,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lado izquierdo: número de factura e información básica
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = venta.numeroFactura,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SarapeRojo
                )
                
                Text(
                    text = venta.cliente?.nombre ?: "Cliente no especificado",
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = venta.formatoHora(),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // Lado derecho: total y método de pago
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$${String.format("%.2f", venta.subtotal())}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SarapeBrown
                )
                
                Text(
                    text = venta.metodoPago.descripcion,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                
                // Icono según el método de pago
                Icon(
                    imageVector = when (venta.metodoPago) {
                        com.elzarapeimports.zarapeimports.model.MetodoPago.EFECTIVO -> Icons.Filled.AttachMoney
                        com.elzarapeimports.zarapeimports.model.MetodoPago.TARJETA_CREDITO -> Icons.Filled.CreditCard
                        com.elzarapeimports.zarapeimports.model.MetodoPago.TARJETA_DEBITO -> Icons.Filled.CreditCard
                        com.elzarapeimports.zarapeimports.model.MetodoPago.TRANSFERENCIA -> Icons.Filled.AccountBalance
                        else -> Icons.Filled.Payment
                    },
                    contentDescription = null,
                    tint = when (venta.metodoPago) {
                        com.elzarapeimports.zarapeimports.model.MetodoPago.EFECTIVO -> SarapeVerde
                        com.elzarapeimports.zarapeimports.model.MetodoPago.TARJETA_CREDITO -> SarapeAzul
                        com.elzarapeimports.zarapeimports.model.MetodoPago.TARJETA_DEBITO -> SarapeAzul
                        com.elzarapeimports.zarapeimports.model.MetodoPago.TRANSFERENCIA -> SarapeRojo
                        else -> SarapeNaranja
                    },
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// Función para filtrar ventas según los criterios
private fun filtrarVentas(busqueda: String, filtroFecha: String): List<Venta> {
    var resultado = DatosEjemplo.historialVentas.toList()
    
    // Filtrar por búsqueda
    if (busqueda.isNotEmpty()) {
        resultado = resultado.filter {
            (it.cliente?.nombre?.contains(busqueda, ignoreCase = true) ?: false) ||
            it.numeroFactura.contains(busqueda, ignoreCase = true)
        }
    }
    
    // Filtrar por fecha
    val ahora = Clock.System.now()
    val zonaHoraria = TimeZone.currentSystemDefault()
    val fechaActual = ahora.toLocalDateTime(zonaHoraria).date
    
    resultado = when (filtroFecha) {
        "hoy" -> resultado.filter {
            val fechaVenta = it.fechaHora.toLocalDateTime(zonaHoraria).date
            fechaVenta == fechaActual
        }
        "semana" -> resultado.filter {
            val fechaVenta = it.fechaHora.toLocalDateTime(zonaHoraria).date
            val diasDiferencia = fechaActual.dayOfYear - fechaVenta.dayOfYear
            diasDiferencia in 0..6
        }
        "mes" -> resultado.filter {
            val fechaVenta = it.fechaHora.toLocalDateTime(zonaHoraria).date
            fechaVenta.year == fechaActual.year && fechaVenta.month == fechaActual.month
        }
        else -> resultado
    }
    
    // Ordenar por fecha (más reciente primero)
    return resultado.sortedByDescending { it.fechaHora }
}

// Función para duplicar una venta
private fun duplicarVenta(ventaOriginal: Venta) {
    // Crear una nueva venta basada en la original pero con nueva ID y fecha
    val nuevaVenta = Venta(
        id = generarNuevoId(),
        elementos = ventaOriginal.elementos,
        cliente = ventaOriginal.cliente,
        metodoPago = ventaOriginal.metodoPago,
        fechaHora = Clock.System.now(),
        comentarios = ventaOriginal.comentarios + " (Copia de ${ventaOriginal.numeroFactura})",
        completada = false, // Nueva venta como borrador
        facturada = false,
        referenciaPago = ventaOriginal.referenciaPago,
        numeroFactura = generarNuevoNumeroFactura(),
        esCotizacion = false
    )
    
    // Añadir la nueva venta al historial
    DatosEjemplo.historialVentas.add(nuevaVenta)
}

// Generar un nuevo ID para la venta duplicada
private fun generarNuevoId(): String {
    return "v${System.currentTimeMillis()}"
}

// Generar un nuevo número de factura
private fun generarNuevoNumeroFactura(): String {
    // Encontrar el número más alto y sumar 1
    val ultimoNumero = DatosEjemplo.historialVentas
        .mapNotNull { venta -> 
            val num = venta.numeroFactura.removePrefix("#")
            num.toIntOrNull() 
        }
        .maxOrNull() ?: 0
    
    return "#${String.format("%05d", ultimoNumero + 1)}"
}

// Validar formato de email
private fun emailValido(email: String): Boolean {
    val pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    return email.matches(pattern.toRegex())
}

// Función para generar PDF y simular el envío
private fun generarYEnviarPdf(venta: Venta, email: String) {
    // En una aplicación real, aquí se generaría el PDF usando una librería como iText
    // y se implementaría el envío por email usando Intent o alguna API de email
    
    // Para nuestro simulador, solo registramos en log que se ha enviado
    println("PDF de la venta ${venta.numeroFactura} generado y enviado a $email")
    
    // Aquí se podría almacenar el PDF en el almacenamiento o en una base de datos
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    venta: Venta,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            modifier = Modifier
                                .clickable { onBack() }
                                .padding(end = 16.dp)
                        )
                        Text(
                            text = "Productos en Carrito",
                            fontWeight = FontWeight.Bold
                        )
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
            if (venta.elementos.isEmpty()) {
                // Mostrar mensaje si no hay productos
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "El carrito está vacío",
                            color = Color.Gray,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // Lista de productos
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    item {
                        Text(
                            text = "Productos en esta orden:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    items(venta.elementos) { elemento ->
                        ProductoCarritoItem(elemento)
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color.LightGray
                        )
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
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = venta.formatoTotal(),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoCarritoItem(elemento: ElementoVenta) {
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
                        text = elemento.producto.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    
                    Text(
                        text = "Código: ${elemento.producto.codigo}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    Text(
                        text = "Cantidad: ${elemento.cantidad}",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    
                    if (elemento.descuento > 0) {
                        Text(
                            text = "Descuento: ${(elemento.descuento * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = SarapeNaranja
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$${String.format("%.2f", elemento.subtotal())}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = SarapeBrown
                    )
                    
                    Text(
                        text = "$${String.format("%.2f", elemento.producto.precio)} c/u",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
} 