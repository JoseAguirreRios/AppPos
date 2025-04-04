package com.elzarapeimports.zarapeimports.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

/**
 * Representa un producto en el sistema
 */
data class Producto(
    val id: String = UUID.randomUUID().toString(),
    val codigo: String,
    val nombre: String,
    val descripcion: String = "",
    val precio: Double,
    val existencias: Int,
    val categoria: String = "",
    val fechaCreacion: Instant = Clock.System.now(),
    val impuesto: Double = 0.16, // Por defecto, 16% (IVA en México)
    val imagenUrl: String? = null
) {
    fun precioConImpuesto(): Double = precio * (1 + impuesto)
    
    fun formatoPrecio(): String = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        .format(precio)
        
    fun formatoPrecioConImpuesto(): String = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        .format(precioConImpuesto())
}

/**
 * Representa un elemento de una venta
 */
data class ElementoVenta(
    val producto: Producto,
    var cantidad: Int = 1,
    var descuento: Double = 0.0 // Porcentaje de descuento (0.0 - 1.0)
) {
    fun subtotal(): Double = producto.precio * cantidad * (1 - descuento)
    
    fun subtotalConImpuesto(): Double = producto.precioConImpuesto() * cantidad * (1 - descuento)
    
    fun impuestoTotal(): Double = subtotalConImpuesto() - subtotal()
    
    fun formatoSubtotal(): String = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        .format(subtotal())
        
    fun formatoDescuento(): String = if (descuento > 0) {
        "${(descuento * 100).toInt()}%"
    } else {
        "0%"
    }
}

/**
 * Tipos de pago soportados
 */
enum class MetodoPago(val descripcion: String) {
    EFECTIVO("Efectivo"),
    TARJETA_DEBITO("Tarjeta de débito"),
    TARJETA_CREDITO("Tarjeta de crédito"),
    TRANSFERENCIA("Transferencia"),
    OTRO("Otro método")
}

/**
 * Representa una venta completa
 */
data class Venta(
    val id: String = UUID.randomUUID().toString(),
    val elementos: MutableList<ElementoVenta> = mutableListOf(),
    var cliente: Cliente? = null,
    var metodoPago: MetodoPago = MetodoPago.EFECTIVO,
    var fechaHora: Instant = Clock.System.now(),
    var comentarios: String = "",
    var completada: Boolean = false,
    var facturada: Boolean = false,
    var referenciaPago: String = "",
    var numeroFactura: String = "",
    var esCotizacion: Boolean = false
) {
    fun subtotal(): Double = elementos.sumOf { it.subtotal() }
    
    fun impuestoTotal(): Double = elementos.sumOf { it.impuestoTotal() }
    
    fun total(): Double = elementos.sumOf { it.subtotalConImpuesto() }
    
    fun cantidadProductos(): Int = elementos.sumOf { it.cantidad }
    
    fun formatoTotal(): String = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        .format(total())
        
    fun formatoSubtotal(): String = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        .format(subtotal())
        
    fun formatoImpuestos(): String = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        .format(impuestoTotal())
    
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
    
    fun formatoFechaCorta(): String {
        val dateTime = fechaHora.toLocalDateTime(TimeZone.currentSystemDefault())
        return String.format("%02d/%02d/%d", 
            dateTime.date.dayOfMonth,
            dateTime.date.monthNumber,
            dateTime.date.year
        )
    }
    
    fun formatoHora(): String {
        val dateTime = fechaHora.toLocalDateTime(TimeZone.currentSystemDefault())
        return String.format("%02d:%02d", dateTime.hour, dateTime.minute)
    }
}

/**
 * Representa un cliente
 */
data class Cliente(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val rfc: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val email: String = "",
    val notas: String = ""
) 