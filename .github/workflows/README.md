# Flujos de Trabajo GitHub Actions

Este directorio contiene los flujos de trabajo de GitHub Actions configurados para este proyecto.

## Flujos de Trabajo Disponibles

### 1. Android CI (`android-build.yml`)

Este flujo de trabajo se activa cuando:
- Se hace push a la rama `master`
- Se crea un pull request hacia la rama `master`

Realiza las siguientes tareas:
- Configura el entorno con JDK 17
- Compila el proyecto con Gradle
- Ejecuta pruebas unitarias
- Realiza verificación de lint
- Sube el APK de depuración como artefacto

### 2. Security Scan (`security-scan.yml`)

Este flujo de trabajo se activa cuando:
- Se hace push a la rama `master`
- Se crea un pull request hacia la rama `master`
- Automáticamente cada domingo a medianoche

Realiza un escaneo de seguridad de las dependencias utilizando OWASP Dependency Check y sube el informe como artefacto.

## Cómo Usar

No se requiere configuración adicional. Los flujos de trabajo se ejecutarán automáticamente cuando se cumplan las condiciones especificadas.

## Notas Técnicas

- Todas las acciones se han actualizado a sus versiones más recientes (v4)
- Se han añadido permisos específicos para garantizar la correcta ejecución
- Se utiliza JDK 17 para satisfacer los requisitos del Android Gradle Plugin 8.9.1

## Personalización

Para personalizar estos flujos de trabajo:
1. Edita los archivos `.yml` correspondientes
2. Ajusta los eventos de activación, pasos o configuraciones según tus necesidades 