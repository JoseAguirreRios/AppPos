package com.elzarapeimports.zarapeimports.firebase

import android.util.Log
import com.elzarapeimports.zarapeimports.model.Cliente
import com.elzarapeimports.zarapeimports.model.DatosEjemplo
import com.elzarapeimports.zarapeimports.model.Producto
import com.elzarapeimports.zarapeimports.model.Venta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Gestiona la sincronización entre los datos de ejemplo locales y Firebase
 * Permitiendo una migración gradual de los datos locales a la nube
 */
object FirebaseDataSync {
    
    private const val TAG = "FirebaseDataSync"
    
    /**
     * Sincroniza todos los datos locales con Firebase
     */
    suspend fun sincronizarTodo() {
        withContext(Dispatchers.IO) {
            try {
                sincronizarProductos()
                sincronizarClientes()
                sincronizarVentas()
                Log.i(TAG, "Sincronización de todos los datos completada")
            } catch (e: Exception) {
                Log.e(TAG, "Error en la sincronización de datos: ${e.message}")
            }
        }
    }
    
    /**
     * Sincroniza los productos de ejemplo con Firebase
     */
    suspend fun sincronizarProductos() {
        withContext(Dispatchers.IO) {
            try {
                // Productos existentes en Firebase
                val productosFirebase = FirebaseManager.obtenerProductos()
                val codigosFirebase = productosFirebase.map { it.codigo }.toSet()
                
                // Productos locales que no están en Firebase
                val productosNuevos = DatosEjemplo.productosEjemplo.filter { 
                    !codigosFirebase.contains(it.codigo)
                }
                
                // Subir productos nuevos
                for (producto in productosNuevos) {
                    FirebaseManager.guardarProducto(producto)
                    Log.d(TAG, "Producto sincronizado: ${producto.nombre}")
                }
                
                Log.i(TAG, "Sincronización de productos completada. ${productosNuevos.size} productos nuevos.")
            } catch (e: Exception) {
                Log.e(TAG, "Error al sincronizar productos: ${e.message}")
            }
        }
    }
    
    /**
     * Sincroniza los clientes de ejemplo con Firebase
     */
    suspend fun sincronizarClientes() {
        withContext(Dispatchers.IO) {
            try {
                // Clientes existentes en Firebase
                val clientesFirebase = FirebaseManager.obtenerClientes()
                val nombresRfcFirebase = clientesFirebase.map { "${it.nombre}|${it.rfc}" }.toSet()
                
                // Clientes locales que no están en Firebase
                val clientesNuevos = DatosEjemplo.clientesEjemplo.filter { 
                    !nombresRfcFirebase.contains("${it.nombre}|${it.rfc}")
                }
                
                // Subir clientes nuevos
                for (cliente in clientesNuevos) {
                    FirebaseManager.guardarCliente(cliente)
                    Log.d(TAG, "Cliente sincronizado: ${cliente.nombre}")
                }
                
                Log.i(TAG, "Sincronización de clientes completada. ${clientesNuevos.size} clientes nuevos.")
            } catch (e: Exception) {
                Log.e(TAG, "Error al sincronizar clientes: ${e.message}")
            }
        }
    }
    
    /**
     * Sincroniza las ventas de ejemplo con Firebase
     */
    suspend fun sincronizarVentas() {
        withContext(Dispatchers.IO) {
            try {
                // Ventas existentes en Firebase
                val ventasFirebase = FirebaseManager.obtenerVentas()
                val idsVentasFirebase = ventasFirebase.map { it.id }.toSet()
                
                // Ventas locales que no están en Firebase
                val ventasNuevas = DatosEjemplo.historialVentas.filter { 
                    !idsVentasFirebase.contains(it.id)
                }
                
                // Subir ventas nuevas
                for (venta in ventasNuevas) {
                    FirebaseManager.guardarVenta(venta)
                    Log.d(TAG, "Venta sincronizada: ${venta.id} - ${venta.formatoFecha()}")
                }
                
                Log.i(TAG, "Sincronización de ventas completada. ${ventasNuevas.size} ventas nuevas.")
            } catch (e: Exception) {
                Log.e(TAG, "Error al sincronizar ventas: ${e.message}")
            }
        }
    }
    
    /**
     * Carga datos desde Firebase y actualiza los datos locales
     */
    suspend fun cargarDatosDesdeFirebase() {
        withContext(Dispatchers.IO) {
            try {
                // Cargar productos desde Firebase
                val productos = FirebaseManager.obtenerProductos()
                if (productos.isNotEmpty()) {
                    // En un escenario real, actualizaríamos una base de datos local (Room)
                    // Para este prototipo, solo lo registramos
                    Log.i(TAG, "Cargados ${productos.size} productos desde Firebase")
                }
                
                // Cargar clientes desde Firebase
                val clientes = FirebaseManager.obtenerClientes()
                if (clientes.isNotEmpty()) {
                    Log.i(TAG, "Cargados ${clientes.size} clientes desde Firebase")
                }
                
                // Cargar ventas desde Firebase
                val ventas = FirebaseManager.obtenerVentas()
                if (ventas.isNotEmpty()) {
                    Log.i(TAG, "Cargadas ${ventas.size} ventas desde Firebase")
                }
                
                Log.i(TAG, "Carga de datos desde Firebase completada")
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar datos desde Firebase: ${e.message}")
            }
        }
    }
} 