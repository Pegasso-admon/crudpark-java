# CrudPark - Java Desktop App

## Objetivo
Aplicacion operativa de escritorio para gestion de ingresos y salidas de vehiculos.

## Requisitos
- Java Development Kit (JDK) 11 o superior
- Apache Maven
- Base de datos PostgreSQL

## Instalacion y Ejecucion
1. Clona este repositorio (si aplica, o si ya lo tienes, entra al directorio).
2. Configura las credenciales de la base de datos en `src/main/resources/db.properties`.
3. Asegurate de que la base de datos PostgreSQL este corriendo y tenga la estructura de tablas necesaria.
4. Para construir el proyecto:
   ```bash
   mvn clean install
   ```
5. Para ejecutar la aplicacion (desde el directorio `crudpark-java`):
   ```bash
   mvn exec:java -Dexec.mainClass="com.crudpark.MainApp"
   ```

## Estructura de Proyecto
```
crudpark-java/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/crudpark/
    │   │       ├── ui/             # Clases de la interfaz de usuario (Swing)
    │   │       ├── dao/            # Clases de acceso a datos (JDBC)
    │   │       ├── model/          # Clases de modelo (POJOs)
    │   │       ├── service/        # Logica de negocio
    │   │       ├── util/           # Utilidades (DbConnection, QRCodeGenerator, TicketPrinter)
    │   │       └── MainApp.java    # Clase principal
    │   └── resources/
    │       └── db.properties       # Configuracion de la base de datos
    └── test/ (opcional, se puede recrear con mvn)
```

## Creditos
- [Tu Nombre 1]
- [Tu Nombre 2]
- [Tu Nombre 3]
- Enlace al registro del equipo: [https://teams.crudzaso.com](https://teams.crudzaso.com)
