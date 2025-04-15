package com.elzarapeimports.zarapeimports.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

/**
 * Categoría de productos
 */
data class Categoria(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val descripcion: String = "",
    val activa: Boolean = true
)

/**
 * Representa un proveedor de productos
 */
data class Proveedor(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val contacto: String = "",
    val telefono: String = "",
    val email: String = "",
    val direccion: String = "",
    val rfc: String = "",
    val notas: String = "",
    val activo: Boolean = true
)

/**
 * Representa un movimiento de inventario
 */
enum class TipoMovimiento {
    ENTRADA,
    SALIDA,
    AJUSTE,
    VENTA,
    DEVOLUCION
}

/**
 * Representa un movimiento en el inventario
 */
data class MovimientoInventario(
    val id: String = UUID.randomUUID().toString(),
    val producto: Producto,
    val cantidad: Int,
    val tipoMovimiento: TipoMovimiento,
    val fechaHora: Instant = Clock.System.now(),
    val precio: Double? = null,
    val comentario: String = "",
    val usuario: String = "",
    val documentoReferencia: String = "" // Número de factura, pedido, etc.
) {
    fun formatoFecha(): String {
        val dateTime = fechaHora.toLocalDateTime(TimeZone.currentSystemDefault())
        return String.format("%02d/%02d/%d %02d:%02d", 
            dateTime.date.dayOfMonth,
            dateTime.date.monthNumber,
            dateTime.date.year,
            dateTime.hour,
            dateTime.minute
        )
    }
    
    fun formatoPrecio(): String = precio?.let {
        NumberFormat.getCurrencyInstance(Locale("es", "MX")).format(it)
    } ?: "-"
}

/**
 * Clase para gestionar el inventario
 */
class InventarioManager {
    private val productos = mutableListOf<Producto>()
    private val categorias = mutableListOf<Categoria>()
    private val proveedores = mutableListOf<Proveedor>()
    private val movimientos = mutableListOf<MovimientoInventario>()
    
    // Métodos para gestionar productos
    fun agregarProducto(producto: Producto) {
        productos.add(producto)
    }
    
    fun actualizarProducto(producto: Producto) {
        val index = productos.indexOfFirst { it.id == producto.id }
        if (index >= 0) {
            productos[index] = producto
        }
    }
    
    fun eliminarProducto(id: String) {
        productos.removeIf { it.id == id }
    }
    
    fun buscarProducto(codigo: String): Producto? {
        return productos.find { it.codigo == codigo }
    }
    
    fun buscarProductoPorId(id: String): Producto? {
        return productos.find { it.id == id }
    }
    
    fun listarProductos(): List<Producto> {
        return productos.toList()
    }
    
    fun listarProductosPorCategoria(categoria: String): List<Producto> {
        return productos.filter { it.categoria == categoria }
    }
    
    fun productosConBajoStock(minimo: Int = 5): List<Producto> {
        return productos.filter { it.existencias <= minimo }
    }
    
    // Métodos para gestionar categorías
    fun agregarCategoria(categoria: Categoria) {
        categorias.add(categoria)
    }
    
    fun actualizarCategoria(categoria: Categoria) {
        val index = categorias.indexOfFirst { it.id == categoria.id }
        if (index >= 0) {
            categorias[index] = categoria
        }
    }
    
    fun eliminarCategoria(id: String) {
        categorias.removeIf { it.id == id }
    }
    
    fun listarCategorias(): List<Categoria> {
        return categorias.filter { it.activa }
    }
    
    // Métodos para gestionar proveedores
    fun agregarProveedor(proveedor: Proveedor) {
        proveedores.add(proveedor)
    }
    
    fun actualizarProveedor(proveedor: Proveedor) {
        val index = proveedores.indexOfFirst { it.id == proveedor.id }
        if (index >= 0) {
            proveedores[index] = proveedor
        }
    }
    
    fun eliminarProveedor(id: String) {
        proveedores.removeIf { it.id == id }
    }
    
    fun listarProveedores(): List<Proveedor> {
        return proveedores.filter { it.activo }
    }
    
    // Métodos para gestionar movimientos de inventario
    fun registrarMovimiento(movimiento: MovimientoInventario) {
        movimientos.add(movimiento)
        
        // Actualizar existencias del producto
        val producto = buscarProductoPorId(movimiento.producto.id)
        producto?.let {
            val nuevasExistencias = when (movimiento.tipoMovimiento) {
                TipoMovimiento.ENTRADA, TipoMovimiento.DEVOLUCION -> it.existencias + movimiento.cantidad
                TipoMovimiento.SALIDA, TipoMovimiento.VENTA -> it.existencias - movimiento.cantidad
                TipoMovimiento.AJUSTE -> movimiento.cantidad // Ajuste directo
            }
            
            // Crear producto actualizado
            val productoActualizado = it.copy(existencias = nuevasExistencias)
            actualizarProducto(productoActualizado)
        }
    }
    
    fun listarMovimientos(): List<MovimientoInventario> {
        return movimientos.toList()
    }
    
    fun listarMovimientosPorProducto(idProducto: String): List<MovimientoInventario> {
        return movimientos.filter { it.producto.id == idProducto }
    }
    
    fun listarMovimientosPorTipo(tipo: TipoMovimiento): List<MovimientoInventario> {
        return movimientos.filter { it.tipoMovimiento == tipo }
    }
}

/**
 * Datos de ejemplo para el inventario
 */
object DatosInventarioEjemplo {
    private val categoriasEjemplo = listOf(
        Categoria(nombre = "Sarapes", descripcion = "Sarapes tradicionales de diferentes tamaños"),
        Categoria(nombre = "Rebosos", descripcion = "Rebosos artesanales"),
        Categoria(nombre = "Infantil", descripcion = "Productos para niños"),
        Categoria(nombre = "Decoración", descripcion = "Artículos decorativos para el hogar"),
        Categoria(nombre = "Accesorios", descripcion = "Accesorios personales"),
        Categoria(nombre = "Ropa", descripcion = "Ropa tradicional mexicana"),
        Categoria(nombre = "Comedor", descripcion = "Artículos para comedor y cocina")
    )
    
    private val proveedoresEjemplo = listOf(
        Proveedor(
            nombre = "Artesanías del Sur",
            contacto = "Pedro Martínez",
            telefono = "9511234567",
            email = "contacto@artesaniassur.com",
            direccion = "Av. Oaxaca 123, Oaxaca",
            rfc = "ASU160429JK2"
        ),
        Proveedor(
            nombre = "Textiles Mexicanos",
            contacto = "Luisa Ramírez",
            telefono = "2221987654",
            email = "ventas@textilesmx.com",
            direccion = "Calle Juárez 45, Puebla",
            rfc = "TME050810XY3"
        ),
        Proveedor(
            nombre = "Artesanos Unidos",
            contacto = "Miguel Ángel López",
            telefono = "3335678901",
            email = "info@artesanosunidos.mx",
            direccion = "Av. Hidalgo 78, Guadalajara",
            rfc = "AUN090215AB7"
        )
    )
    
    // Método para inicializar un InventarioManager con datos de ejemplo
    fun crearInventarioEjemplo(): InventarioManager {
        val inventario = InventarioManager()
        
        // Agregar categorías
        categoriasEjemplo.forEach { inventario.agregarCategoria(it) }
        
        // Agregar proveedores
        proveedoresEjemplo.forEach { inventario.agregarProveedor(it) }
        
        // Agregar productos (usando los productos de DatosEjemplo)
        DatosEjemplo.productosEjemplo.forEach { inventario.agregarProducto(it) }
        
        // Agregar algunos movimientos de ejemplo
        if (DatosEjemplo.productosEjemplo.isNotEmpty()) {
            val producto1 = DatosEjemplo.productosEjemplo[0]
            
            // Registrar un movimiento de entrada
            inventario.registrarMovimiento(
                MovimientoInventario(
                    producto = producto1,
                    cantidad = 10,
                    tipoMovimiento = TipoMovimiento.ENTRADA,
                    precio = 650.0,
                    comentario = "Compra inicial",
                    documentoReferencia = "FAC-1234"
                )
            )
            
            // Registrar otro movimiento si hay suficientes productos
            if (DatosEjemplo.productosEjemplo.size > 3) {
                val producto2 = DatosEjemplo.productosEjemplo[3]
                
                inventario.registrarMovimiento(
                    MovimientoInventario(
                        producto = producto2,
                        cantidad = 5,
                        tipoMovimiento = TipoMovimiento.ENTRADA,
                        precio = 900.0,
                        comentario = "Compra inicial",
                        documentoReferencia = "FAC-1234"
                    )
                )
                
                // Simular una venta
                inventario.registrarMovimiento(
                    MovimientoInventario(
                        producto = producto1,
                        cantidad = 2,
                        tipoMovimiento = TipoMovimiento.VENTA,
                        comentario = "Venta",
                        documentoReferencia = "#00001"
                    )
                )
            }
        }
        
        return inventario
    }
} 