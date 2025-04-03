package com.elzarapeimports.zarapeimports.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.elzarapeimports.zarapeimports.model.Venta
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Clase que genera tickets de venta en formato PDF
 */
class TicketGenerator(private val context: Context) {
    
    /**
     * Genera un ticket de venta en formato PDF y lo guarda en el almacenamiento externo
     */
    fun generarTicketPDF(venta: Venta): String? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        
        // Configuración inicial
        paint.color = Color.BLACK
        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT
        
        // Dibuja el encabezado
        dibujarEncabezado(canvas, paint)
        
        // Dibuja los datos de la venta
        dibujarDatosVenta(canvas, paint, venta)
        
        // Dibuja los productos
        dibujarProductos(canvas, paint, venta)
        
        // Dibuja el resumen
        dibujarResumen(canvas, paint, venta)
        
        // Dibuja el pie de página
        dibujarPie(canvas, paint)
        
        pdfDocument.finishPage(page)
        
        // Guarda el PDF
        val nombreArchivo = "Ticket_${generarIdUnico()}.pdf"
        val rutaArchivo = "${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/$nombreArchivo"
        
        try {
            val file = File(rutaArchivo)
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            return rutaArchivo
        } catch (e: IOException) {
            Log.e("TicketGenerator", "Error al guardar PDF", e)
            pdfDocument.close()
            return null
        }
    }
    
    private fun dibujarEncabezado(canvas: Canvas, paint: Paint) {
        paint.textSize = 20f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("ZARAPE IMPORTS", 250f, 60f, paint)
        
        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Av. Revolución 1234, CDMX", 230f, 80f, paint)
        canvas.drawText("Tel: 55-1234-5678", 250f, 100f, paint)
        canvas.drawText("RFC: ZAIM123456ABC", 250f, 120f, paint)
        
        // Línea separadora
        paint.strokeWidth = 1f
        canvas.drawLine(50f, 140f, 545f, 140f, paint)
    }
    
    private fun dibujarDatosVenta(canvas: Canvas, paint: Paint, venta: Venta) {
        paint.textSize = 12f
        canvas.drawText("TICKET DE VENTA", 250f, 160f, paint)
        canvas.drawText("Fecha: ${venta.formatoFecha()}", 50f, 180f, paint)
        canvas.drawText("Folio: ${venta.id.substring(0, 8)}", 50f, 200f, paint)
        
        venta.cliente?.let { cliente ->
            canvas.drawText("Cliente: ${cliente.nombre}", 50f, 220f, paint)
            if (cliente.rfc.isNotEmpty()) {
                canvas.drawText("RFC: ${cliente.rfc}", 350f, 220f, paint)
            }
        } ?: canvas.drawText("Cliente: Público en general", 50f, 220f, paint)
        
        // Línea separadora
        paint.strokeWidth = 1f
        canvas.drawLine(50f, 240f, 545f, 240f, paint)
    }
    
    private fun dibujarProductos(canvas: Canvas, paint: Paint, venta: Venta) {
        // Encabezados de columnas
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("CANTIDAD", 50f, 260f, paint)
        canvas.drawText("DESCRIPCIÓN", 120f, 260f, paint)
        canvas.drawText("PRECIO UNIT.", 300f, 260f, paint)
        canvas.drawText("DESCUENTO", 380f, 260f, paint)
        canvas.drawText("IMPORTE", 470f, 260f, paint)
        
        // Línea separadora
        paint.strokeWidth = 0.5f
        canvas.drawLine(50f, 270f, 545f, 270f, paint)
        
        // Productos
        paint.typeface = Typeface.DEFAULT
        var yPos = 290f
        
        venta.elementos.forEach { elemento ->
            canvas.drawText(elemento.cantidad.toString(), 50f, yPos, paint)
            
            // Limita el texto de la descripción a 25 caracteres
            val descripcion = if (elemento.producto.nombre.length > 25) {
                "${elemento.producto.nombre.substring(0, 22)}..."
            } else {
                elemento.producto.nombre
            }
            canvas.drawText(descripcion, 120f, yPos, paint)
            
            canvas.drawText(elemento.producto.formatoPrecio(), 300f, yPos, paint)
            canvas.drawText(elemento.formatoDescuento(), 380f, yPos, paint)
            canvas.drawText(elemento.formatoSubtotal(), 470f, yPos, paint)
            
            yPos += 20f
        }
        
        // Línea separadora
        yPos += 10f
        canvas.drawLine(50f, yPos, 545f, yPos, paint)
    }
    
    private fun dibujarResumen(canvas: Canvas, paint: Paint, venta: Venta) {
        var yPos = 450f
        
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Subtotal:", 400f, yPos, paint)
        canvas.drawText(venta.formatoSubtotal(), 470f, yPos, paint)
        
        yPos += 20f
        canvas.drawText("IVA:", 400f, yPos, paint)
        canvas.drawText(venta.formatoImpuestos(), 470f, yPos, paint)
        
        yPos += 20f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("TOTAL:", 400f, yPos, paint)
        canvas.drawText(venta.formatoTotal(), 470f, yPos, paint)
        
        yPos += 40f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Forma de pago: ${formatoMetodoPago(venta.metodoPago)}", 50f, yPos, paint)
        
        if (venta.referenciaPago.isNotEmpty()) {
            yPos += 20f
            canvas.drawText("Referencia: ${venta.referenciaPago}", 50f, yPos, paint)
        }
    }
    
    private fun dibujarPie(canvas: Canvas, paint: Paint) {
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        
        canvas.drawText("*** GRACIAS POR SU COMPRA ***", 230f, 700f, paint)
        canvas.drawText("Este documento es un comprobante de pago, no fiscal.", 180f, 720f, paint)
        canvas.drawText("Conserve su ticket para cualquier aclaración.", 200f, 740f, paint)
    }
    
    private fun formatoMetodoPago(metodoPago: com.elzarapeimports.zarapeimports.model.MetodoPago): String {
        return when (metodoPago) {
            com.elzarapeimports.zarapeimports.model.MetodoPago.EFECTIVO -> "Efectivo"
            com.elzarapeimports.zarapeimports.model.MetodoPago.TARJETA_DEBITO -> "Tarjeta de Débito"
            com.elzarapeimports.zarapeimports.model.MetodoPago.TARJETA_CREDITO -> "Tarjeta de Crédito"
            com.elzarapeimports.zarapeimports.model.MetodoPago.TRANSFERENCIA -> "Transferencia Bancaria"
            com.elzarapeimports.zarapeimports.model.MetodoPago.OTRO -> "Otro método"
        }
    }
    
    private fun generarIdUnico(): String {
        val formato = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        return formato.format(Date())
    }
}

/**
 * Composable que proporciona una instancia de TicketGenerator
 */
@Composable
fun rememberTicketGenerator(): TicketGenerator {
    val context = LocalContext.current
    return remember { TicketGenerator(context) }
} 