package com.elzarapeimports.zarapeimports.screens.inventario

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.elzarapeimports.zarapeimports.model.*
import com.elzarapeimports.zarapeimports.ui.theme.*

/**
 * Pantalla para gestionar los movimientos de inventario
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientosInventarioScreen(
    onBack: () -> Unit
) {
    // Usamos el manager de inventario con datos de ejemplo para la demostración
    val inventarioManager = remember { DatosInventarioEjemplo.crearInventarioEjemplo() }
    var movimientos by remember { mutableStateOf(inventarioManager.listarMovimientos()) }
    var tipoMovimientoSeleccionado by remember { mutableStateOf<TipoMovimiento?>(null) }
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    var mostrarDialogoNuevoMovimiento by remember { mutableStateOf(false) }
    var expandedTipoMenu by remember { mutableStateOf(false) }
    
    fun actualizarListaMovimientos() {
        movimientos = when {
            tipoMovimientoSeleccionado != null && productoSeleccionado != null -> {
                inventarioManager.listarMovimientos().filter { 
                    it.tipoMovimiento == tipoMovimientoSeleccionado && it.producto.id == productoSeleccionado!!.id
                }
            }
            tipoMovimientoSeleccionado != null -> {
                inventarioManager.listarMovimientosPorTipo(tipoMovimientoSeleccionado!!)
            }
            productoSeleccionado != null -> {
                inventarioManager.listarMovimientosPorProducto(productoSeleccionado!!.id)
            }
            else -> inventarioManager.listarMovimientos()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movimientos de Inventario") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SarapeBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogoNuevoMovimiento = true },
                containerColor = SarapeVerde
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Nuevo Movimiento",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Filtros de movimientos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Filtro por tipo de movimiento
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = tipoMovimientoSeleccionado?.name ?: "Todos los tipos",
                        onValueChange = { },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Tipo") },
                        trailingIcon = {
                            IconButton(onClick = { expandedTipoMenu = true }) {
                                Icon(Icons.Filled.ArrowDropDown, 
                                     contentDescription = "Seleccionar tipo")
                            }
                        }
                    )
                    
                    DropdownMenu(
                        expanded = expandedTipoMenu,
                        onDismissRequest = { expandedTipoMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todos") },
                            onClick = {
                                tipoMovimientoSeleccionado = null
                                actualizarListaMovimientos()
                                expandedTipoMenu = false
                            }
                        )
                        TipoMovimiento.values().forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo.name) },
                                onClick = {
                                    tipoMovimientoSeleccionado = tipo
                                    actualizarListaMovimientos()
                                    expandedTipoMenu = false
                                }
                            )
                        }
                    }
                }
                
                // Filtro por producto (simulado)
                OutlinedButton(
                    onClick = { 
                        // En una implementación real, aquí se abriría un diálogo para seleccionar producto
                        productoSeleccionado = if (productoSeleccionado == null) {
                            inventarioManager.listarProductos().firstOrNull()
                        } else {
                            null
                        }
                        actualizarListaMovimientos()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.FilterList, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (productoSeleccionado != null) {
                            "Filtro: ${productoSeleccionado!!.nombre}"
                        } else {
                            "Filtrar por Producto"
                        }
                    )
                }
            }
            
            // Lista de movimientos
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = SarapeBackground
                ),
                shape = RoundedCornerShape(8.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Encabezado de la tabla
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray.copy(alpha = 0.3f))
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fecha",
                            modifier = Modifier.weight(0.2f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Producto",
                            modifier = Modifier.weight(0.3f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Tipo",
                            modifier = Modifier.weight(0.15f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Cantidad",
                            modifier = Modifier.weight(0.15f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Precio",
                            modifier = Modifier.weight(0.2f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    // Lista de movimientos
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(movimientos) { movimiento ->
                            MovimientoListItem(movimiento = movimiento)
                            Divider()
                        }
                    }
                }
            }
            
            if (movimientos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron movimientos",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
    
    // Diálogo para crear nuevo movimiento
    if (mostrarDialogoNuevoMovimiento) {
        NuevoMovimientoDialog(
            onDismiss = { mostrarDialogoNuevoMovimiento = false },
            onGuardar = { nuevoMovimiento ->
                inventarioManager.registrarMovimiento(nuevoMovimiento)
                actualizarListaMovimientos()
                mostrarDialogoNuevoMovimiento = false
            },
            productos = inventarioManager.listarProductos()
        )
    }
}

@Composable
fun MovimientoListItem(
    movimiento: MovimientoInventario
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Fecha
        Text(
            text = movimiento.formatoFecha(),
            modifier = Modifier.weight(0.2f),
            fontSize = 12.sp
        )
        
        // Producto
        Text(
            text = movimiento.producto.nombre,
            modifier = Modifier.weight(0.3f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        
        // Tipo de movimiento
        Box(
            modifier = Modifier
                .weight(0.15f)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            val (backgroundColor, textColor) = when (movimiento.tipoMovimiento) {
                TipoMovimiento.ENTRADA -> Pair(SarapeVerde.copy(alpha = 0.2f), SarapeVerde)
                TipoMovimiento.SALIDA -> Pair(SarapeRojo.copy(alpha = 0.2f), SarapeRojo)
                TipoMovimiento.AJUSTE -> Pair(SarapeMorado.copy(alpha = 0.2f), SarapeMorado)
                TipoMovimiento.VENTA -> Pair(SarapeAzul.copy(alpha = 0.2f), SarapeAzul)
                TipoMovimiento.DEVOLUCION -> Pair(SarapeAmarillo.copy(alpha = 0.2f), SarapeAmarillo)
            }
            
            val textoTipo = when (movimiento.tipoMovimiento) {
                TipoMovimiento.ENTRADA -> "Entrada"
                TipoMovimiento.SALIDA -> "Salida"
                TipoMovimiento.AJUSTE -> "Ajuste"
                TipoMovimiento.VENTA -> "Venta"
                TipoMovimiento.DEVOLUCION -> "Devolución"
            }
            
            Text(
                text = textoTipo,
                modifier = Modifier
                    .background(backgroundColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
        
        // Cantidad
        Text(
            text = movimiento.cantidad.toString(),
            modifier = Modifier.weight(0.15f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        
        // Precio
        Text(
            text = movimiento.formatoPrecio(),
            modifier = Modifier.weight(0.2f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoMovimientoDialog(
    onDismiss: () -> Unit,
    onGuardar: (MovimientoInventario) -> Unit,
    productos: List<Producto>
) {
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    var tipoMovimiento by remember { mutableStateOf(TipoMovimiento.ENTRADA) }
    var cantidad by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var comentario by remember { mutableStateOf("") }
    var referencia by remember { mutableStateOf("") }
    
    // Errores del formulario
    var productoError by remember { mutableStateOf<String?>(null) }
    var cantidadError by remember { mutableStateOf<String?>(null) }
    var precioError by remember { mutableStateOf<String?>(null) }
    
    // Expandido del menú de productos
    var expandedProductoMenu by remember { mutableStateOf(false) }
    
    // Expandido del menú de tipos de movimiento
    var expandedTipoMenu by remember { mutableStateOf(false) }
    
    fun validarFormulario(): Boolean {
        var esValido = true
        
        // Validar producto
        if (productoSeleccionado == null) {
            productoError = "Seleccione un producto"
            esValido = false
        } else {
            productoError = null
        }
        
        // Validar cantidad
        try {
            val cantidadNum = cantidad.toInt()
            if (cantidadNum <= 0) {
                cantidadError = "La cantidad debe ser mayor a 0"
                esValido = false
            } else {
                cantidadError = null
            }
        } catch (e: NumberFormatException) {
            cantidadError = "Ingrese un número válido"
            esValido = false
        }
        
        // Validar precio (solo para entradas)
        if (tipoMovimiento == TipoMovimiento.ENTRADA || tipoMovimiento == TipoMovimiento.DEVOLUCION) {
            if (precio.isNotBlank()) {
                try {
                    val precioNum = precio.toDouble()
                    if (precioNum < 0) {
                        precioError = "El precio no puede ser negativo"
                        esValido = false
                    } else {
                        precioError = null
                    }
                } catch (e: NumberFormatException) {
                    precioError = "Ingrese un precio válido"
                    esValido = false
                }
            }
        }
        
        return esValido
    }
    
    fun guardarMovimiento() {
        if (!validarFormulario()) {
            return
        }
        
        val precioFinal = if (tipoMovimiento == TipoMovimiento.ENTRADA || tipoMovimiento == TipoMovimiento.DEVOLUCION) {
            precio.toDoubleOrNull()
        } else {
            null
        }
        
        val nuevoMovimiento = MovimientoInventario(
            producto = productoSeleccionado!!,
            cantidad = cantidad.toInt(),
            tipoMovimiento = tipoMovimiento,
            precio = precioFinal,
            comentario = comentario,
            documentoReferencia = referencia
        )
        
        onGuardar(nuevoMovimiento)
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Nuevo Movimiento de Inventario",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SarapeAzul
                )
                
                // Producto
                Box {
                    OutlinedTextField(
                        value = productoSeleccionado?.nombre ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Producto") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = productoError != null,
                        supportingText = { productoError?.let { Text(it) } },
                        trailingIcon = {
                            IconButton(onClick = { expandedProductoMenu = true }) {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Seleccionar producto")
                            }
                        }
                    )
                    
                    DropdownMenu(
                        expanded = expandedProductoMenu,
                        onDismissRequest = { expandedProductoMenu = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        productos.forEach { producto ->
                            DropdownMenuItem(
                                text = {
                                    Text("${producto.codigo} - ${producto.nombre}")
                                },
                                onClick = {
                                    productoSeleccionado = producto
                                    expandedProductoMenu = false
                                }
                            )
                        }
                    }
                }
                
                // Tipo de movimiento
                Box {
                    OutlinedTextField(
                        value = when (tipoMovimiento) {
                            TipoMovimiento.ENTRADA -> "Entrada"
                            TipoMovimiento.SALIDA -> "Salida"
                            TipoMovimiento.AJUSTE -> "Ajuste"
                            TipoMovimiento.VENTA -> "Venta"
                            TipoMovimiento.DEVOLUCION -> "Devolución"
                        },
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Tipo de Movimiento") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { expandedTipoMenu = true }) {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Seleccionar tipo")
                            }
                        }
                    )
                    
                    DropdownMenu(
                        expanded = expandedTipoMenu,
                        onDismissRequest = { expandedTipoMenu = false }
                    ) {
                        TipoMovimiento.values().forEach { tipo ->
                            val textoTipo = when (tipo) {
                                TipoMovimiento.ENTRADA -> "Entrada"
                                TipoMovimiento.SALIDA -> "Salida"
                                TipoMovimiento.AJUSTE -> "Ajuste"
                                TipoMovimiento.VENTA -> "Venta"
                                TipoMovimiento.DEVOLUCION -> "Devolución"
                            }
                            
                            DropdownMenuItem(
                                text = { Text(textoTipo) },
                                onClick = {
                                    tipoMovimiento = tipo
                                    expandedTipoMenu = false
                                }
                            )
                        }
                    }
                }
                
                // Cantidad
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = { Text("Cantidad") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = cantidadError != null,
                    supportingText = { cantidadError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )
                
                // Precio (solo para entradas)
                if (tipoMovimiento == TipoMovimiento.ENTRADA || tipoMovimiento == TipoMovimiento.DEVOLUCION) {
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = precioError != null,
                        supportingText = { precioError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Decimal
                        )
                    )
                }
                
                // Comentario
                OutlinedTextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    label = { Text("Comentario (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Referencia
                OutlinedTextField(
                    value = referencia,
                    onValueChange = { referencia = it },
                    label = { Text("Referencia (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Número de factura, pedido, etc.") }
                )
                
                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { guardarMovimiento() },
                        colors = ButtonDefaults.buttonColors(containerColor = SarapeVerde)
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
} 