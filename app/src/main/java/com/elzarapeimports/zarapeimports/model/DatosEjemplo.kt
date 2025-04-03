package com.elzarapeimports.zarapeimports.model

/**
 * Proporciona datos de ejemplo para el desarrollo y pruebas
 */
object DatosEjemplo {
    
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
} 