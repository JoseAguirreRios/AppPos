package com.elzarapeimports.zarapeimports.screens.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elzarapeimports.zarapeimports.model.*
import com.elzarapeimports.zarapeimports.ui.theme.*

/**
 * Pantalla principal para gestionar el inventario
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(
    onBack: () -> Unit,
    onNuevoProducto: () -> Unit,
    onEditarProducto: (Producto) -> Unit,
    onGestionarCategorias: () -> Unit,
    onGestionarProveedores: () -> Unit,
    onVerMovimientos: () -> Unit
) {
    // Usamos el manager de inventario con datos de ejemplo para la demostración
    val inventarioManager = remember { DatosInventarioEjemplo.crearInventarioEjemplo() }
    var productos by remember { mutableStateOf(inventarioManager.listarProductos()) }
    var categoriaSeleccionada by remember { mutableStateOf<String?>(null) }
    var busqueda by remember { mutableStateOf("") }
    var mostrarBajoStock by remember { mutableStateOf(false) }
    
    fun actualizarListaProductos() {
        productos = when {
            mostrarBajoStock -> inventarioManager.productosConBajoStock()
            categoriaSeleccionada != null -> inventarioManager.listarProductosPorCategoria(categoriaSeleccionada!!)
            busqueda.isNotEmpty() -> inventarioManager.listarProductos().filter { 
                it.nombre.contains(busqueda, ignoreCase = true) || 
                it.codigo.contains(busqueda, ignoreCase = true) 
            }
            else -> inventarioManager.listarProductos()
        }
    }
    
    fun eliminarProducto(producto: Producto) {
        inventarioManager.eliminarProducto(producto.id)
        actualizarListaProductos()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario") },
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
                onClick = { onNuevoProducto() },
                containerColor = SarapeVerde
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Nuevo Producto",
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
            // Opciones de filtrado y búsqueda
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Campo de búsqueda
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { 
                        busqueda = it
                        actualizarListaProductos()
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Buscar producto") },
                    leadingIcon = { 
                        Icon(Icons.Filled.Search, contentDescription = "Buscar") 
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SarapeAzul,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Botón para filtrar por bajo stock
                FilterChip(
                    selected = mostrarBajoStock,
                    onClick = { 
                        mostrarBajoStock = !mostrarBajoStock
                        actualizarListaProductos()
                    },
                    label = { Text("Bajo stock") },
                    leadingIcon = if (mostrarBajoStock) {
                        { Icon(Icons.Filled.Warning, contentDescription = null, tint = SarapeRojo) }
                    } else null
                )
            }
            
            // Botones de gestión
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { onGestionarCategorias() },
                    colors = ButtonDefaults.buttonColors(containerColor = SarapeMorado)
                ) {
                    Icon(Icons.Filled.Category, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Categorías")
                }
                
                Button(
                    onClick = { onGestionarProveedores() },
                    colors = ButtonDefaults.buttonColors(containerColor = SarapeTurquesa)
                ) {
                    Icon(Icons.Filled.Business, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Proveedores")
                }
                
                Button(
                    onClick = { onVerMovimientos() },
                    colors = ButtonDefaults.buttonColors(containerColor = SarapeAmarillo)
                ) {
                    Icon(Icons.Filled.History, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Movimientos")
                }
            }
            
            // Lista de categorías para filtrar
            val categorias = listOf("Sarapes", "Rebosos", "Infantil", "Decoración", "Accesorios", "Ropa", "Comedor")
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = categoriaSeleccionada == null,
                        onClick = { 
                            categoriaSeleccionada = null
                            actualizarListaProductos()
                        },
                        label = { Text("Todos") }
                    )
                }
                
                items(categorias) { categoria ->
                    FilterChip(
                        selected = categoriaSeleccionada == categoria,
                        onClick = { 
                            categoriaSeleccionada = if (categoriaSeleccionada == categoria) null else categoria
                            actualizarListaProductos()
                        },
                        label = { Text(categoria) }
                    )
                }
            }
            
            // Lista de productos
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
                            .background(Color.LightGray.copy(alpha = 0.3f)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Código",
                            modifier = Modifier.weight(0.15f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Producto",
                            modifier = Modifier.weight(0.35f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Precio",
                            modifier = Modifier.weight(0.2f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Stock",
                            modifier = Modifier.weight(0.15f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Acción",
                            modifier = Modifier.weight(0.15f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    // Lista de productos
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(productos) { producto ->
                            ProductoListItem(
                                producto = producto,
                                onEdit = { onEditarProducto(producto) },
                                onDelete = { eliminarProducto(producto) }
                            )
                            Divider()
                        }
                    }
                }
            }
            
            if (productos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron productos",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ProductoListItem(
    producto: Producto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = producto.codigo,
            modifier = Modifier.weight(0.15f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier.weight(0.35f)
        ) {
            Text(
                text = producto.nombre,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = producto.categoria,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = producto.formatoPrecio(),
            modifier = Modifier.weight(0.2f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        
        // Stock con color según nivel
        Box(
            modifier = Modifier
                .weight(0.15f)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            val backgroundColor = when {
                producto.existencias <= 5 -> SarapeRojo.copy(alpha = 0.2f)
                producto.existencias <= 10 -> SarapeAmarillo.copy(alpha = 0.2f)
                else -> SarapeVerde.copy(alpha = 0.2f)
            }
            
            val textColor = when {
                producto.existencias <= 5 -> SarapeRojo
                producto.existencias <= 10 -> SarapeAmarillo
                else -> SarapeVerde
            }
            
            Text(
                text = producto.existencias.toString(),
                modifier = Modifier
                    .background(backgroundColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
        
        // Botones de acción
        Row(
            modifier = Modifier.weight(0.15f),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { onEdit() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar",
                    tint = SarapeAzul
                )
            }
            
            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar",
                    tint = SarapeRojo
                )
            }
        }
    }
    
    // Diálogo de confirmación para eliminar
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Está seguro que desea eliminar el producto '${producto.nombre}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SarapeRojo)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
} 