# AppPos - Sistema de Punto de Venta para El Zarape Imports

## Resumen Ejecutivo

### Descripción
AppPos es una aplicación Android diseñada para automatizar la gestión de ventas, precios y facturación de **El Zarape Imports**, una empresa dedicada a la importación y venta de artesanías mexicanas en EE.UU. El sistema permite manejar múltiples tipos de clientes (minorista, mayorista, en línea) con precios diferenciados, reduciendo errores manuales y optimizando procesos.

### Problema Identificado
- **Gestión manual de precios**: Errores frecuentes en facturación debido a la falta de automatización.
- **Falta de integración digital**: Uso de Excel y métodos tradicionales, lo que ralentiza las operaciones.
- **Escalabilidad limitada**: Dificultad para gestionar el crecimiento del volumen de ventas.

### Solución
- **Automatización de precios**: Selección automática de precios según tipo de cliente.
- **Generación de facturas**: Exportación en PDF o impresión directa.
- **Base de datos centralizada**: Integración con Firebase para sincronización en tiempo real.
- **Interfaz intuitiva**: Diseño responsive y adaptable a tablets/smartphones.

### Arquitectura
Cliente Android → API REST → Firebase (Base de Datos/Almacenamiento) → Servidor Web (Versión Final)


---

## Tabla de Contenidos
1. [Requerimientos](#requerimientos)  
2. [Instalación](#instalación)  
3. [Configuración](#configuración)  
4. [Uso](#uso)  
5. [Contribución](#contribución)  
6. [Roadmap](#roadmap)  

---

## Requerimientos
### Infraestructura
- **Servidor de Base de Datos**: Firebase Realtime Database.
- **Almacenamiento**: Firebase Storage.
- **Servidor Web**: Para despliegue de la versión final (ej: Heroku, AWS).

### Paquetes Adicionales
- Firebase SDK (Firestore, Authentication, Storage).
- Bibliotecas de Android: Jetpack Compose, Material Design.
- JUnit para pruebas unitarias.

### Versiones
- **Java**: JDK 17+.
- **Android Studio**: Arctic Fox (2020.3.1) o superior.

---

## Instalación
### Ambiente de Desarrollo
1. Clona el repositorio:
   ```bash
   git clone https://github.com/JoseAguirreRios/AppPos.git
Configura Firebase:

Crea un proyecto en Firebase Console.

Descarga el archivo google-services.json y colócalo en app/.

Instala dependencias:

bash
Copy
./gradlew build
Ejecución de Pruebas
Pruebas unitarias:

bash
Copy
./gradlew test
Pruebas en dispositivo físico/emulador: Ejecuta la app desde Android Studio.

Implementación en Producción
Ambiente local: Compila un APK con ./gradlew assembleRelease.

Nube (Heroku):

Configura un servidor web estático para la API.

Despliega el APK en servicios como AppGallery o Google Play Store.

Configuración
Archivos Clave
google-services.json: Configuración de Firebase (no compartir públicamente).

build.gradle: Define dependencias y versiones de SDK.

Variables de Entorno
FIREBASE_API_KEY: Agregar en local.properties:

properties
Copy
FIREBASE_API_KEY=tu_api_key
Uso
Usuario Final
Iniciar Sesión: Ingresa con correo/contraseña o usa el modo demo.

Nueva Venta:

Selecciona tipo de cliente (minorista/mayorista/en línea).

Escanea/agrega productos.

Genera factura en PDF o imprime.

Historial: Consulta ventas anteriores filtradas por fecha o cliente.

Administrador
Gestión de Productos: Actualiza precios y stock desde Firebase Console.

Backups: Configura respaldos automáticos en Firebase.

Contribución
Clona el repositorio y crea un branch:

bash
Copy
git checkout -b feature/nueva-funcionalidad
Realiza tus cambios y envía un Pull Request a develop.

Espera revisión y merge por parte del mantenedor.

Roadmap
Próximas Funcionalidades (Beta → GA)
Beta:

Reportes gráficos de ventas.

Optimización de rendimiento.

GA:

Integración con plataformas de e-commerce (Shopify, WooCommerce).

Soporte para múltiples monedas e idiomas.

Futuras Versiones
Sistema de inventario avanzado.

Dashboard de analíticas en tiempo real.
   
