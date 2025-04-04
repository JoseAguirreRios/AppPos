package com.elzarapeimports.zarapeimports

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elzarapeimports.zarapeimports.model.Venta
import com.elzarapeimports.zarapeimports.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZarapeImportsTheme {
                ZarapeApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZarapeApp() {
    var selectedTab by remember { mutableStateOf(0) }
    var showConfiguration by remember { mutableStateOf(false) }
    var showAccount by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Usamos el logo vectorial que hemos creado
                        Image(
                            painter = painterResource(id = R.drawable.logo_zarape_imports),
                            contentDescription = "Zarape Imports Logo",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Zarape Imports",
                            color = SarapeBrown,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SarapeBackground
                ),
                actions = {
                    IconButton(onClick = { showAccount = true }) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Cuenta")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SarapeBackground,
                contentColor = SarapeBrown
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Ventas") },
                    label = { Text("Ventas") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SarapeRojo,
                        selectedTextColor = SarapeRojo
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Filled.List, contentDescription = "Inventario") },
                    label = { Text("Inventario") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SarapeAzul,
                        selectedTextColor = SarapeAzul
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Filled.Group, contentDescription = "Clientes") },
                    label = { Text("Clientes") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SarapeVerde,
                        selectedTextColor = SarapeVerde
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Filled.InsertChart, contentDescription = "Reportes") },
                    label = { Text("Reportes") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SarapeMorado,
                        selectedTextColor = SarapeMorado
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SarapeBackground)
        ) {
            when {
                showConfiguration -> ConfiguracionScreen(onBack = { showConfiguration = false })
                showAccount -> CuentaScreen(onBack = { showAccount = false })
                else -> {
                    when (selectedTab) {
                        0 -> VentasScreen()
                        1 -> InventarioScreen()
                        2 -> ClientesScreen()
                        3 -> ReportesScreen()
                        else -> VentasScreen()
                    }
                }
            }
            
            // Botón de configuración flotante
            if (!showConfiguration && !showAccount) {
                FloatingActionButton(
                    onClick = { showConfiguration = true },
                    containerColor = SarapeNaranja,
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = "Configuración")
                }
            }
        }
    }
}

@Composable
fun VentasScreen() {
    var mostrarNuevaVenta by remember { mutableStateOf(false) }
    var mostrarHistorial by remember { mutableStateOf(false) }
    var ventaParaEditar by remember { mutableStateOf<Venta?>(null) }
    
    when {
        ventaParaEditar != null -> {
            com.elzarapeimports.zarapeimports.screens.ventas.EditarVentaScreen(
                venta = ventaParaEditar!!,
                onBack = { ventaParaEditar = null }
            )
        }
        mostrarNuevaVenta -> {
            com.elzarapeimports.zarapeimports.screens.ventas.NuevaVentaScreen(
                onBack = { mostrarNuevaVenta = false }
            )
        }
        mostrarHistorial -> {
            com.elzarapeimports.zarapeimports.screens.ventas.HistorialVentasScreen(
                onBack = { mostrarHistorial = false },
                onEditarVenta = { venta ->
                    mostrarHistorial = false
                    ventaParaEditar = venta
                }
            )
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Gestión de Ventas",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = SarapeRojo
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tarjetas de menú de colores para las diferentes funciones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MenuCard(
                        title = "Nueva Venta",
                        icon = Icons.Filled.Add,
                        color = SarapeRojo,
                        onClick = { mostrarNuevaVenta = true },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    MenuCard(
                        title = "Historial",
                        icon = Icons.Filled.List,
                        color = SarapeNaranja,
                        onClick = { mostrarHistorial = true },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MenuCard(
                        title = "Devoluciones",
                        icon = Icons.Filled.Undo,
                        color = SarapeAzul,
                        onClick = { /* TODO */ },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    MenuCard(
                        title = "Cotizaciones",
                        icon = Icons.Filled.Description,
                        color = SarapeMorado,
                        onClick = { /* TODO */ },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun InventarioScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Inventario",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = SarapeAzul
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tarjetas de menú de colores para las diferentes funciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MenuCard(
                title = "Productos",
                icon = Icons.Filled.List,
                color = SarapeAzul,
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            MenuCard(
                title = "Categorías",
                icon = Icons.Filled.Label,
                color = SarapeVerde,
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MenuCard(
                title = "Entradas",
                icon = Icons.Filled.Input,
                color = SarapeTurquesa,
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            MenuCard(
                title = "Proveedores",
                icon = Icons.Filled.Business,
                color = SarapeAmarillo,
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ClientesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Gestión de Clientes",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = SarapeVerde
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tarjetas de menú de colores para las diferentes funciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MenuCard(
                title = "Directorio",
                icon = Icons.Filled.Contacts,
                color = SarapeVerde,
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            MenuCard(
                title = "Nuevo Cliente",
                icon = Icons.Filled.PersonAdd,
                color = SarapeTurquesa,
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MenuCard(
                title = "Créditos",
                icon = Icons.Filled.CreditCard,
                color = SarapeAmarillo,
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            MenuCard(
                title = "Fidelización",
                icon = Icons.Filled.Star,
                color = SarapeNaranja,
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ReportesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Reportes",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = SarapeMorado
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tarjetas de menú de colores para las diferentes funciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MenuCard(
                title = "Ventas",
                icon = Icons.Filled.BarChart,
                color = SarapeMorado,
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            MenuCard(
                title = "Inventario",
                icon = Icons.Filled.InsertChart,
                color = SarapeRojo,
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MenuCard(
                title = "Clientes",
                icon = Icons.Filled.PieChart,
                color = SarapeAzul,
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            MenuCard(
                title = "Finanzas",
                icon = Icons.Filled.MonetizationOn,
                color = SarapeVerde,
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ConfiguracionScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = SarapeBrown)
            }
            Text(
                text = "Configuración",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SarapeNaranja
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Tarjetas de menú para configuración
        MenuCard(
            title = "Preferencias de Usuario",
            icon = Icons.Filled.Person,
            color = SarapeNaranja,
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        MenuCard(
            title = "Configuración de Impresora",
            icon = Icons.Filled.Print,
            color = SarapeNaranja,
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        MenuCard(
            title = "Parámetros del Sistema",
            icon = Icons.Filled.Settings,
            color = SarapeNaranja,
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        MenuCard(
            title = "Respaldo de Datos",
            icon = Icons.Filled.Backup,
            color = SarapeNaranja,
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CuentaScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = SarapeBrown)
            }
            Text(
                text = "Mi Cuenta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SarapeMorado
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Avatar y nombre de usuario
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Avatar",
                tint = SarapeMorado,
                modifier = Modifier.size(100.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Usuario Administrador",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "admin@zarapeimports.com",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Tarjetas de menú para la cuenta
        MenuCard(
            title = "Editar Perfil",
            icon = Icons.Filled.Edit,
            color = SarapeMorado,
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        MenuCard(
            title = "Cambiar Contraseña",
            icon = Icons.Filled.Lock,
            color = SarapeMorado,
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        MenuCard(
            title = "Notificaciones",
            icon = Icons.Filled.Notifications,
            color = SarapeMorado,
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        MenuCard(
            title = "Cerrar Sesión",
            icon = Icons.Filled.ExitToApp,
            color = SarapeRojo,
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ZarapeAppPreview() {
    ZarapeImportsTheme {
        ZarapeApp()
    }
}