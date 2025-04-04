package com.elzarapeimports.zarapeimports.firebase

import android.net.Uri
import android.util.Log
import com.elzarapeimports.zarapeimports.model.Cliente
import com.elzarapeimports.zarapeimports.model.Producto
import com.elzarapeimports.zarapeimports.model.Venta
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import java.util.UUID

/**
 * Clase que gestiona todas las operaciones relacionadas con Firebase
 */
object FirebaseManager {
    private const val TAG = "FirebaseManager"
    
    // Referencias a los servicios de Firebase
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore
    private val storage: FirebaseStorage = Firebase.storage

    // Colecciones en Firestore
    private const val USUARIOS_COLLECTION = "usuarios"
    private const val PRODUCTOS_COLLECTION = "productos"
    private const val CLIENTES_COLLECTION = "clientes"
    private const val VENTAS_COLLECTION = "ventas"
    
    // Storage
    private const val IMAGENES_PRODUCTOS_PATH = "imagenes_productos"
    private const val TICKETS_PATH = "tickets"
    
    /**
     * AUTENTICACIÓN
     */
    
    // Usuario actual
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    
    // Iniciar sesión
    suspend fun iniciarSesion(email: String, password: String): Boolean {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user != null
        } catch (e: Exception) {
            Log.e(TAG, "Error al iniciar sesión: ${e.message}")
            false
        }
    }
    
    // Cerrar sesión
    fun cerrarSesion() {
        auth.signOut()
    }
    
    /**
     * PRODUCTOS
     */
    
    // Obtener todos los productos
    suspend fun obtenerProductos(): List<Producto> {
        return try {
            val querySnapshot = db.collection(PRODUCTOS_COLLECTION).get().await()
            querySnapshot.documents.mapNotNull { document ->
                convertirDocumentoAProducto(document)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener productos: ${e.message}")
            emptyList()
        }
    }
    
    // Obtener producto por código
    suspend fun obtenerProductoPorCodigo(codigo: String): Producto? {
        return try {
            val querySnapshot = db.collection(PRODUCTOS_COLLECTION)
                .whereEqualTo("codigo", codigo)
                .get()
                .await()
            
            if (querySnapshot.documents.isNotEmpty()) {
                convertirDocumentoAProducto(querySnapshot.documents[0])
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener producto por código: ${e.message}")
            null
        }
    }
    
    // Guardar producto
    suspend fun guardarProducto(producto: Producto): Boolean {
        return try {
            val productoMap = mapOf(
                "id" to producto.id,
                "codigo" to producto.codigo,
                "nombre" to producto.nombre,
                "descripcion" to producto.descripcion,
                "precio" to producto.precio,
                "existencias" to producto.existencias,
                "categoria" to producto.categoria,
                "fechaCreacion" to producto.fechaCreacion.toString(),
                "impuesto" to producto.impuesto,
                "imagenUrl" to producto.imagenUrl
            )
            
            db.collection(PRODUCTOS_COLLECTION)
                .document(producto.id)
                .set(productoMap)
                .await()
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar producto: ${e.message}")
            false
        }
    }
    
    // Actualizar existencias de un producto
    suspend fun actualizarExistencias(productoId: String, nuevaCantidad: Int): Boolean {
        return try {
            db.collection(PRODUCTOS_COLLECTION)
                .document(productoId)
                .update("existencias", nuevaCantidad)
                .await()
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar existencias: ${e.message}")
            false
        }
    }
    
    // Subir imagen de producto
    suspend fun subirImagenProducto(productoId: String, imagenUri: Uri): String? {
        return try {
            val storageRef = storage.reference
                .child("$IMAGENES_PRODUCTOS_PATH/$productoId.jpg")
            
            val uploadTask = storageRef.putFile(imagenUri).await()
            val downloadUrl = storageRef.downloadUrl.await()
            
            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e(TAG, "Error al subir imagen: ${e.message}")
            null
        }
    }
    
    /**
     * CLIENTES
     */
    
    // Obtener todos los clientes
    suspend fun obtenerClientes(): List<Cliente> {
        return try {
            val querySnapshot = db.collection(CLIENTES_COLLECTION).get().await()
            querySnapshot.documents.mapNotNull { document ->
                convertirDocumentoACliente(document)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener clientes: ${e.message}")
            emptyList()
        }
    }
    
    // Buscar clientes por nombre
    suspend fun buscarClientesPorNombre(nombre: String): List<Cliente> {
        return try {
            val nombreLower = nombre.lowercase()
            val querySnapshot = db.collection(CLIENTES_COLLECTION)
                .whereGreaterThanOrEqualTo("nombreLower", nombreLower)
                .whereLessThanOrEqualTo("nombreLower", nombreLower + '\uf8ff')
                .get()
                .await()
            
            querySnapshot.documents.mapNotNull { document ->
                convertirDocumentoACliente(document)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al buscar clientes: ${e.message}")
            emptyList()
        }
    }
    
    // Guardar cliente
    suspend fun guardarCliente(cliente: Cliente): Boolean {
        return try {
            val clienteMap = mapOf(
                "id" to cliente.id,
                "nombre" to cliente.nombre,
                "nombreLower" to cliente.nombre.lowercase(), // Para búsquedas insensibles a mayúsculas
                "rfc" to cliente.rfc,
                "direccion" to cliente.direccion,
                "telefono" to cliente.telefono,
                "email" to cliente.email,
                "notas" to cliente.notas
            )
            
            db.collection(CLIENTES_COLLECTION)
                .document(cliente.id)
                .set(clienteMap)
                .await()
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar cliente: ${e.message}")
            false
        }
    }
    
    /**
     * VENTAS
     */
    
    // Obtener historial de ventas
    suspend fun obtenerVentas(): List<Venta> {
        return try {
            val querySnapshot = db.collection(VENTAS_COLLECTION)
                .orderBy("fechaHora")
                .get()
                .await()
            
            querySnapshot.documents.mapNotNull { document ->
                convertirDocumentoAVenta(document)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener ventas: ${e.message}")
            emptyList()
        }
    }
    
    // Guardar venta
    suspend fun guardarVenta(venta: Venta): Boolean {
        return try {
            // Primero convertimos la venta a un mapa para Firestore
            val ventaMap = mapOf(
                "id" to venta.id,
                "fechaHora" to venta.fechaHora.toString(),
                "metodoPago" to venta.metodoPago.name,
                "clienteId" to venta.cliente?.id,
                "comentarios" to venta.comentarios,
                "completada" to venta.completada,
                "facturada" to venta.facturada,
                "referenciaPago" to venta.referenciaPago,
                "numeroFactura" to venta.numeroFactura,
                "esCotizacion" to venta.esCotizacion,
                "elementosVenta" to venta.elementos.map { elemento ->
                    mapOf(
                        "productoId" to elemento.producto.id,
                        "productoCodigo" to elemento.producto.codigo,
                        "productoNombre" to elemento.producto.nombre,
                        "productoPrecio" to elemento.producto.precio,
                        "productoImpuesto" to elemento.producto.impuesto,
                        "cantidad" to elemento.cantidad,
                        "descuento" to elemento.descuento
                    )
                }
            )
            
            // Guardamos la venta en Firestore
            db.collection(VENTAS_COLLECTION)
                .document(venta.id)
                .set(ventaMap)
                .await()
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar venta: ${e.message}")
            false
        }
    }
    
    // Subir ticket/factura en PDF
    suspend fun subirTicketPdf(ventaId: String, pdfUri: Uri): String? {
        return try {
            val storageRef = storage.reference
                .child("$TICKETS_PATH/$ventaId.pdf")
            
            val uploadTask = storageRef.putFile(pdfUri).await()
            val downloadUrl = storageRef.downloadUrl.await()
            
            // Actualizamos la venta con la URL del ticket
            db.collection(VENTAS_COLLECTION)
                .document(ventaId)
                .update("ticketUrl", downloadUrl.toString())
                .await()
            
            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e(TAG, "Error al subir ticket PDF: ${e.message}")
            null
        }
    }
    
    /**
     * Funciones auxiliares para convertir documentos de Firestore a objetos del modelo
     */
    
    private fun convertirDocumentoAProducto(document: DocumentSnapshot): Producto? {
        return try {
            val data = document.data ?: return null
            
            Producto(
                id = document.id,
                codigo = data["codigo"] as? String ?: "",
                nombre = data["nombre"] as? String ?: "",
                descripcion = data["descripcion"] as? String ?: "",
                precio = (data["precio"] as? Number)?.toDouble() ?: 0.0,
                existencias = (data["existencias"] as? Number)?.toInt() ?: 0,
                categoria = data["categoria"] as? String ?: "",
                fechaCreacion = (data["fechaCreacion"] as? String)?.toInstant() ?: Clock.System.now(),
                impuesto = (data["impuesto"] as? Number)?.toDouble() ?: 0.16,
                imagenUrl = data["imagenUrl"] as? String
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error al convertir documento a producto: ${e.message}")
            null
        }
    }
    
    private fun convertirDocumentoACliente(document: DocumentSnapshot): Cliente? {
        return try {
            val data = document.data ?: return null
            
            Cliente(
                id = document.id,
                nombre = data["nombre"] as? String ?: "",
                rfc = data["rfc"] as? String ?: "",
                direccion = data["direccion"] as? String ?: "",
                telefono = data["telefono"] as? String ?: "",
                email = data["email"] as? String ?: "",
                notas = data["notas"] as? String ?: ""
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error al convertir documento a cliente: ${e.message}")
            null
        }
    }
    
    private suspend fun convertirDocumentoAVenta(document: DocumentSnapshot): Venta? {
        return try {
            val data = document.data ?: return null
            
            // Obtener cliente si existe
            val clienteId = data["clienteId"] as? String
            val cliente = if (clienteId != null) {
                val clienteDoc = db.collection(CLIENTES_COLLECTION).document(clienteId).get().await()
                convertirDocumentoACliente(clienteDoc)
            } else null
            
            val venta = Venta(
                id = document.id,
                fechaHora = (data["fechaHora"] as? String)?.toInstant() ?: Clock.System.now(),
                metodoPago = try {
                    val metodoPagoStr = data["metodoPago"] as? String ?: ""
                    com.elzarapeimports.zarapeimports.model.MetodoPago.valueOf(metodoPagoStr)
                } catch (e: Exception) {
                    com.elzarapeimports.zarapeimports.model.MetodoPago.EFECTIVO
                },
                cliente = cliente,
                comentarios = data["comentarios"] as? String ?: "",
                completada = data["completada"] as? Boolean ?: false,
                facturada = data["facturada"] as? Boolean ?: false,
                referenciaPago = data["referenciaPago"] as? String ?: "",
                numeroFactura = data["numeroFactura"] as? String ?: "",
                esCotizacion = data["esCotizacion"] as? Boolean ?: false
            )
            
            // Obtener elementos de la venta
            val elementosVenta = data["elementosVenta"] as? List<Map<String, Any>> ?: emptyList()
            
            for (elementoMap in elementosVenta) {
                val productoId = elementoMap["productoId"] as? String ?: continue
                val productoCodigo = elementoMap["productoCodigo"] as? String ?: ""
                val productoNombre = elementoMap["productoNombre"] as? String ?: ""
                val productoPrecio = (elementoMap["productoPrecio"] as? Number)?.toDouble() ?: 0.0
                val productoImpuesto = (elementoMap["productoImpuesto"] as? Number)?.toDouble() ?: 0.16
                val cantidad = (elementoMap["cantidad"] as? Number)?.toInt() ?: 1
                val descuento = (elementoMap["descuento"] as? Number)?.toDouble() ?: 0.0
                
                // Creamos un objeto Producto con la información almacenada en la venta
                val producto = Producto(
                    id = productoId,
                    codigo = productoCodigo,
                    nombre = productoNombre,
                    precio = productoPrecio,
                    existencias = 0, // No almacenamos existencias en la venta
                    impuesto = productoImpuesto
                )
                
                venta.elementos.add(
                    com.elzarapeimports.zarapeimports.model.ElementoVenta(
                        producto = producto,
                        cantidad = cantidad,
                        descuento = descuento
                    )
                )
            }
            
            venta
        } catch (e: Exception) {
            Log.e(TAG, "Error al convertir documento a venta: ${e.message}")
            null
        }
    }
} 