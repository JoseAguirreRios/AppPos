package com.elzarapeimports.zarapeimports.screens.inventario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elzarapeimports.zarapeimports.model.DatosInventarioEjemplo
import com.elzarapeimports.zarapeimports.model.Producto
import com.elzarapeimports.zarapeimports.ui.theme.*
import kotlinx.datetime.Clock
import java.util.UUID

/**
 * Pantalla para editar o crear un nuevo producto
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProductoScreen(
    productoExistente: Producto? = null,
    onBack: () -> Unit,
    onSave: (Producto) -> Unit
) {
    val esNuevoProducto = productoExistente == null
    val titulo = if (esNuevoProducto) "Nuevo Producto" else "Editar Producto"
    
    // Estado del formulario
    var codigo by remember { mutableStateOf(productoExistente?.codigo ?: "") }
    var nombre by remember { mutableStateOf(productoExistente?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(productoExistente?.descripcion ?: "") }
    var precio by remember { mutableStateOf(productoExistente?.precio?.toString() ?: "") }
    var existencias by remember { mutableStateOf(productoExistente?.existencias?.toString() ?: "") }
    var categoria by remember { mutableStateOf(productoExistente?.categoria ?: "Sarapes") }
    var impuesto by remember { mutableStateOf((productoExistente?.impuesto ?: 0.16) * 100) }
    
    // Errores del formulario
    var codigoError by remember { mutableStateOf<String?>(null) }
    var nombreError by remember { mutableStateOf<String?>(null) }
    var precioError by remember { mutableStateOf<String?>(null) }
    var existenciasError by remember { mutableStateOf<String?>(null) }
    
    // Categorías disponibles (en una aplicación real esto vendría de la base de datos)
    val categorias = listOf("Sarapes", "Rebosos", "Infantil", "Decoración", "Accesorios", "Ropa", "Comedor")
    var mostrarMenuCategorias by remember { mutableStateOf(false) }
    
    // Estado para la confirmación de guardar
    var mostrarDialogoGuardar by remember { mutableStateOf(false) }
    
    fun validarFormulario(): Boolean {
        var esValido = true
        
        // Validar código
        if (codigo.isBlank()) {
            codigoError = "El código es obligatorio"
            esValido = false
        } else {
            codigoError = null
        }
        
        // Validar nombre
        if (nombre.isBlank()) {
            nombreError = "El nombre es obligatorio"
            esValido = false
        } else {
            nombreError = null
        }
        
        // Validar precio
        try {
            val precioNum = precio.toDouble()
            if (precioNum <= 0) {
                precioError = "El precio debe ser mayor a 0"
                esValido = false
            } else {
                precioError = null
            }
        } catch (e: NumberFormatException) {
            precioError = "Ingrese un precio válido"
            esValido = false
        }
        
        // Validar existencias
        try {
            val existenciasNum = existencias.toInt()
            if (existenciasNum < 0) {
                existenciasError = "Las existencias no pueden ser negativas"
                esValido = false
            } else {
                existenciasError = null
            }
        } catch (e: NumberFormatException) {
            existenciasError = "Ingrese un número válido"
            esValido = false
        }
        
        return esValido
    }
    
    fun guardarProducto() {
        if (!validarFormulario()) {
            return
        }
        
        val producto = Producto(
            id = productoExistente?.id ?: UUID.randomUUID().toString(),
            codigo = codigo,
            nombre = nombre,
            descripcion = descripcion,
            precio = precio.toDouble(),
            existencias = existencias.toInt(),
            categoria = categoria,
            impuesto = impuesto / 100.0,
            fechaCreacion = productoExistente?.fechaCreacion ?: Clock.System.now(),
            imagenUrl = productoExistente?.imagenUrl
        )
        
        onSave(producto)
        mostrarDialogoGuardar = false
        onBack()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SarapeBackground
                ),
                actions = {
                    IconButton(
                        onClick = { 
                            if (validarFormulario()) {
                                mostrarDialogoGuardar = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = "Guardar",
                            tint = SarapeVerde
                        )
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
                .verticalScroll(rememberScrollState())
        ) {
            // Formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Sección Información Básica
                    Text(
                        text = "Información Básica",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SarapeAzul
                    )
                    
                    // Código
                    OutlinedTextField(
                        value = codigo,
                        onValueChange = { codigo = it },
                        label = { Text("Código") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = codigoError != null,
                        supportingText = { codigoError?.let { Text(it) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        leadingIcon = {
                            Icon(Icons.Filled.QrCode, contentDescription = null)
                        }
                    )
                    
                    // Nombre
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = nombreError != null,
                        supportingText = { nombreError?.let { Text(it) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        leadingIcon = {
                            Icon(Icons.Filled.ShoppingBag, contentDescription = null)
                        }
                    )
                    
                    // Descripción
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        leadingIcon = {
                            Icon(Icons.Filled.Description, contentDescription = null)
                        }
                    )
                    
                    // Categoría
                    Box {
                        OutlinedTextField(
                            value = categoria,
                            onValueChange = { },
                            label = { Text("Categoría") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { mostrarMenuCategorias = true }) {
                                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Seleccionar categoría")
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Filled.Category, contentDescription = null)
                            }
                        )
                        
                        DropdownMenu(
                            expanded = mostrarMenuCategorias,
                            onDismissRequest = { mostrarMenuCategorias = false }
                        ) {
                            categorias.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        categoria = cat
                                        mostrarMenuCategorias = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Divider()
                    
                    // Sección Precios e Inventario
                    Text(
                        text = "Precios e Inventario",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SarapeAzul
                    )
                    
                    // Precio
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = precioError != null,
                        supportingText = { precioError?.let { Text(it) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        leadingIcon = {
                            Icon(Icons.Filled.AttachMoney, contentDescription = null)
                        }
                    )
                    
                    // Existencias
                    OutlinedTextField(
                        value = existencias,
                        onValueChange = { existencias = it },
                        label = { Text("Existencias") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = existenciasError != null,
                        supportingText = { existenciasError?.let { Text(it) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        leadingIcon = {
                            Icon(Icons.Filled.Inventory, contentDescription = null)
                        }
                    )
                    
                    // Impuesto
                    Column {
                        Text(
                            text = "Impuesto: ${impuesto.toInt()}%",
                            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                        )
                        Slider(
                            value = impuesto.toFloat(),
                            onValueChange = { impuesto = it.toDouble() },
                            valueRange = 0f..25f,
                            steps = 25,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón guardar
            Button(
                onClick = { 
                    if (validarFormulario()) {
                        mostrarDialogoGuardar = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SarapeVerde
                )
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar Producto")
            }
            
            if (mostrarDialogoGuardar) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogoGuardar = false },
                    title = { Text("Confirmar") },
                    text = { Text("¿Desea guardar los cambios realizados?") },
                    confirmButton = {
                        Button(
                            onClick = { guardarProducto() },
                            colors = ButtonDefaults.buttonColors(containerColor = SarapeVerde)
                        ) {
                            Text("Guardar")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { mostrarDialogoGuardar = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
} 