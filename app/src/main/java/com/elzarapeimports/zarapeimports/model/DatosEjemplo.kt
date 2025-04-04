package com.elzarapeimports.zarapeimports.model

import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.hours

/**
 * Proporciona datos de ejemplo para el desarrollo y pruebas
 */
object DatosEjemplo {
    
    // Contador para generar números de factura secuenciales
    private val contadorFactura = AtomicInteger(1)
    
    val productosEjemplo = listOf(
        Producto(
            codigo = "0001",
            nombre = "Sarape Tradicional",
            descripcion = "Sarape tradicional mexicano en colores vivos",
            precio = 850.0,
            existencias = 25,
            categoria = "Sarapes",
            impuesto = 0.16
        ),
        Producto(
            codigo = "0002",
            nombre = "Reboso Artesanal",
            descripcion = "Reboso artesanal tejido a mano",
            precio = 950.0,
            existencias = 15,
            categoria = "Rebosos",
            impuesto = 0.16
        ),
        Producto(
            codigo = "0003",
            nombre = "Zarape Infantil",
            descripcion = "Zarape para niños con diseños coloridos",
            precio = 450.0,
            existencias = 30,
            categoria = "Infantil",
            impuesto = 0.16
        ),
        Producto(
            codigo = "0004",
            nombre = "Tapete Decorativo",
            descripcion = "Tapete decorativo con motivos tradicionales",
            precio = 1200.0,
            existencias = 10,
            categoria = "Decoración",
            impuesto = 0.16
        ),
        Producto(
            codigo = "0005",
            nombre = "Camino de Mesa",
            descripcion = "Camino de mesa tejido a mano con patrones típicos",
            precio = 680.0,
            existencias = 18,
            categoria = "Decoración",
            impuesto = 0.16
        ),
        Producto(
            codigo = "0006",
            nombre = "Cojín Sarape",
            descripcion = "Cojín decorativo con tela de sarape",
            precio = 320.0,
            existencias = 40,
            categoria = "Decoración",
            impuesto = 0.16
        ),
        Producto(
            codigo = "0007",
            nombre = "Bolsa Artesanal",
            descripcion = "Bolsa para dama con diseño de sarape",
            precio = 520.0,
            existencias = 22,
            categoria = "Accesorios",
            impuesto = 0.16
        ),
        Producto(
            codigo = "0008",
            nombre = "Poncho Mexicano",
            descripcion = "Poncho tradicional para caballero",
            precio = 1150.0,
            existencias = 12,
            categoria = "Ropa",
            impuesto = 0.16
        ),
        Producto(
            codigo = "0009",
            nombre = "Manteles Individuales",
            descripcion = "Set de 4 manteles individuales con motivos étnicos",
            precio = 420.0,
            existencias = 15,
            categoria = "Comedor",
            impuesto = 0.16
        ),
        Producto(
            codigo = "0010",
            nombre = "Sombrero Mexicano",
            descripcion = "Sombrero tradicional con detalles bordados",
            precio = 750.0,
            existencias = 8,
            categoria = "Accesorios",
            impuesto = 0.16
        )
    )
    
    val clientesEjemplo = listOf(
        Cliente(
            nombre = "María Fernández",
            rfc = "FEMA901210ABC",
            direccion = "Av. Revolución 123, CDMX",
            telefono = "5555123456",
            email = "maria@ejemplo.com",
            notas = "Cliente frecuente"
        ),
        Cliente(
            nombre = "Juan Pérez",
            rfc = "PEPJ851115XYZ",
            direccion = "Calle Madero 45, Puebla",
            telefono = "2222567890",
            email = "juan@ejemplo.com"
        ),
        Cliente(
            nombre = "Ana López",
            rfc = "LOAA780620DEF",
            direccion = "Av. Juárez 67, Guadalajara",
            telefono = "3333456789",
            email = "ana@ejemplo.com",
            notas = "Prefiere envíos a domicilio"
        ),
        Cliente(
            nombre = "Roberto González",
            rfc = "GORB900825GHI",
            direccion = "Calzada Independencia 890, Monterrey",
            telefono = "8181234567",
            email = "roberto@ejemplo.com"
        ),
        Cliente(
            nombre = "Sofía Ramírez",
            rfc = "RASO870304JKL",
            direccion = "Calle 5 de Mayo 42, Oaxaca",
            telefono = "9511234567",
            email = "sofia@ejemplo.com",
            notas = "Compras para hotel"
        )
    )
    
    // Función para obtener una venta de ejemplo
    fun crearVentaEjemplo(): Venta {
        val venta = Venta()
        
        // Agregamos algunos productos aleatorios
        venta.elementos.add(ElementoVenta(productosEjemplo[0], 2))
        venta.elementos.add(ElementoVenta(productosEjemplo[3], 1, 0.1)) // Con 10% de descuento
        venta.elementos.add(ElementoVenta(productosEjemplo[6], 3))
        
        // Asignamos un cliente aleatorio
        venta.cliente = clientesEjemplo[2]
        
        return venta
    }
    
    // Generar un nuevo número de factura
    fun generarNumeroFactura(): String {
        return String.format("#%05d", contadorFactura.getAndIncrement())
    }
    
    // Historial de ventas de ejemplo
    val historialVentas = mutableListOf<Venta>().apply {
        // Crear algunas ventas pasadas para el historial
        val ahora = Clock.System.now()
        val timezone = TimeZone.currentSystemDefault()
        
        // Venta de hoy hace 2 horas
        add(Venta(
            elementos = mutableListOf(
                ElementoVenta(productosEjemplo[0], 1),
                ElementoVenta(productosEjemplo[4], 2)
            ),
            cliente = clientesEjemplo[0],
            metodoPago = MetodoPago.EFECTIVO,
            fechaHora = Clock.System.now() - 2.hours,
            completada = true,
            facturada = true,
            numeroFactura = "#00001"
        ))
        
        // Venta de ayer
        val ayer = LocalDateTime(
            ahora.toLocalDateTime(timezone).date.year,
            ahora.toLocalDateTime(timezone).date.monthNumber,
            ahora.toLocalDateTime(timezone).date.dayOfMonth - 1,
            14, 30, 0
        ).toInstant(timezone)
        
        add(Venta(
            elementos = mutableListOf(
                ElementoVenta(productosEjemplo[2], 1),
                ElementoVenta(productosEjemplo[7], 1)
            ),
            cliente = clientesEjemplo[1],
            metodoPago = MetodoPago.TARJETA_CREDITO,
            fechaHora = ayer,
            completada = true,
            facturada = true,
            referenciaPago = "REF-4578",
            numeroFactura = "#00002"
        ))
        
        // Venta de hace 3 días
        val hace3Dias = LocalDateTime(
            ahora.toLocalDateTime(timezone).date.year,
            ahora.toLocalDateTime(timezone).date.monthNumber,
            ahora.toLocalDateTime(timezone).date.dayOfMonth - 3,
            9, 15, 0
        ).toInstant(timezone)
        
        add(Venta(
            elementos = mutableListOf(
                ElementoVenta(productosEjemplo[3], 1),
                ElementoVenta(productosEjemplo[5], 3),
                ElementoVenta(productosEjemplo[8], 2)
            ),
            cliente = clientesEjemplo[4],
            metodoPago = MetodoPago.TRANSFERENCIA,
            fechaHora = hace3Dias,
            completada = true,
            facturada = true,
            referenciaPago = "TRF-9876",
            numeroFactura = "#00003"
        ))
        
        // Actualizar el contador para que comience después de los números ya usados
        contadorFactura.set(4)
    }
} 