package com.elzarapeimports.zarapeimports.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Comprueba si un permiso específico está concedido
 */
fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * Comprueba si el permiso de cámara está concedido
 */
fun Context.hasCameraPermission(): Boolean {
    return hasPermission(Manifest.permission.CAMERA)
}

/**
 * Comprueba si el permiso de almacenamiento está concedido
 */
fun Context.hasStoragePermission(): Boolean {
    return hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
} 