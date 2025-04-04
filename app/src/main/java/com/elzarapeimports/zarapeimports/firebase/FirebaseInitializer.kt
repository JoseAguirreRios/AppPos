package com.elzarapeimports.zarapeimports.firebase

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

/**
 * Inicializa Firebase automáticamente cuando se lanza la aplicación.
 * ContentProvider se carga antes de cualquier Activity, incluso antes de Application.onCreate().
 */
class FirebaseInitializer : ContentProvider() {
    
    companion object {
        private const val TAG = "FirebaseInitializer"
    }
    
    override fun onCreate(): Boolean {
        // Inicializar Firebase
        try {
            context?.let {
                FirebaseApp.initializeApp(it)
                Log.i(TAG, "Firebase inicializado correctamente")
                
                // Opcionalmente, cargar datos iniciales
                cargarDatosIniciales()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al inicializar Firebase: ${e.message}")
        }
        
        return false // No need to provide a ContentProvider implementation
    }
    
    private fun cargarDatosIniciales() {
        // Aquí podríamos agregar lógica para precargar datos necesarios
        // Por ejemplo, cargar productos básicos o configuraciones
        // Esto podría hacerse en un ViewModel o Repository más adelante
    }
    
    // Métodos obligatorios de ContentProvider que no utilizaremos
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null
    
    override fun getType(uri: Uri): String? = null
    
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0
} 