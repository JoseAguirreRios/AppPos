package com.elzarapeimports.zarapeimports.screens.inventario

import androidx.compose.foundation.background
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

/**
 * Pantalla para gestionar las categorías de productos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasScreen(
    onBack: () -> Unit
) {
    // Usamos el manager de inventario con datos de ejemplo para la demostración
    val inventarioManager = remember { DatosInventarioEjemplo.crearInventarioEjemplo() }
    var categorias by remember { mutableStateOf(inventarioManager.listarCategorias()) }
    var categoriaEnEdicion by remember { mutableStateOf<Categoria?>(null) }
    var mostrarDialogoEliminar by remember { mutableStateOf<Categoria?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Categorías") },
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
                onClick = { 
                    categoriaEnEdicion = Categoria(nombre = "", descripcion = "")
                },
                containerColor = SarapeVerde
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Nueva Categoría",
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Cabecera informativa
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SarapeVerde.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Categorías de Productos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SarapeVerde
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Las categorías le permiten organizar sus productos para una mejor gestión y navegación en su tienda.",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Categorías activas: ${categorias.size}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
            
            // Lista de categorías
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        text = "Listado de Categorías",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = SarapeAzul,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    // Cabecera de la tabla
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SarapeAzul.copy(alpha = 0.1f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nombre",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(0.4f)
                        )
                        Text(
                            text = "Descripción",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(0.4f)
                        )
                        Text(
                            text = "Acciones",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(0.2f),
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    // Lista de categorías
                    LazyColumn {
                        items(categorias) { categoria ->
                            CategoriaListItem(
                                categoria = categoria,
                                onEditar = { categoriaEnEdicion = categoria },
                                onEliminar = { mostrarDialogoEliminar = categoria }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
        
        // Dialog para editar o crear nueva categoría
        categoriaEnEdicion?.let { categoria ->
            EditarCategoriaDialog(
                categoria = categoria,
                onDismiss = { categoriaEnEdicion = null },
                onGuardar = { categoriaActualizada ->
                    if (categoria.id == categoriaActualizada.id) {
                        // Actualizar categoría existente
                        inventarioManager.actualizarCategoria(categoriaActualizada)
                    } else {
                        // Agregar nueva categoría
                        inventarioManager.agregarCategoria(categoriaActualizada)
                    }
                    categorias = inventarioManager.listarCategorias()
                    categoriaEnEdicion = null
                }
            )
        }
        
        // Dialog de confirmación para eliminar
        mostrarDialogoEliminar?.let { categoria ->
            AlertDialog(
                onDismissRequest = { mostrarDialogoEliminar = null },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Está seguro que desea eliminar la categoría '${categoria.nombre}'? Esta acción no se puede deshacer.") },
                confirmButton = {
                    Button(
                        onClick = {
                            inventarioManager.eliminarCategoria(categoria.id)
                            categorias = inventarioManager.listarCategorias()
                            mostrarDialogoEliminar = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SarapeRojo)
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { mostrarDialogoEliminar = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun CategoriaListItem(
    categoria: Categoria,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = categoria.nombre,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = categoria.descripcion,
            color = Color.Gray,
            modifier = Modifier.weight(0.4f)
        )
        Row(
            modifier = Modifier.weight(0.2f),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onEditar) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar",
                    tint = SarapeAzul
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar",
                    tint = SarapeRojo
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarCategoriaDialog(
    categoria: Categoria,
    onDismiss: () -> Unit,
    onGuardar: (Categoria) -> Unit
) {
    var nombre by remember { mutableStateOf(categoria.nombre) }
    var descripcion by remember { mutableStateOf(categoria.descripcion) }
    var nombreError by remember { mutableStateOf<String?>(null) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título del dialog
                Text(
                    text = if (categoria.id == categoria.id) "Editar Categoría" else "Nueva Categoría",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = SarapeAzul,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Campo para nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { 
                        nombre = it
                        nombreError = if (it.isBlank()) "El nombre es obligatorio" else null
                    },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nombreError != null,
                    supportingText = { nombreError?.let { Text(it) } },
                    leadingIcon = {
                        Icon(Icons.Filled.Label, contentDescription = null)
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Campo para descripción
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Filled.Description, contentDescription = null)
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (nombre.isBlank()) {
                                nombreError = "El nombre es obligatorio"
                                return@Button
                            }
                            
                            onGuardar(categoria.copy(nombre = nombre, descripcion = descripcion))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SarapeVerde)
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
} 