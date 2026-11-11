## FastQueue
Menos espera. Más tiempo.

FastQueue es una aplicación de escritorio que permite consultar tiempos de espera,
comparar restaurantes, guardar favoritos y compartir reportes de fila en tiempo
real. Su objetivo es ayudar a las personas a decidir dónde comer antes de perder
tiempo esperando.
# Equipo
- Andrea Ramazzini
- Sebastián Tunchez
- Ana Sofía del Águila
- Daniel Chou Jo
# Funcionalidades
- Inicio de sesión y registro con contraseñas protegidas mediante PBKDF2.
- Restaurantes con estado y horario de atención.
- Reportes colaborativos de personas y minutos de espera.
- Promedios calculados usando reportes recientes.
- Sugerencias Quick, priorizando filas cortas y favoritos.
- Favoritos, verificaciones de cierre e historial de reportes.
- Detalle de restaurantes con gráfica de tiempos.
- Búsqueda y filtros por estado.
- Interfaz JavaFX con menú lateral retráctil.
# Tecnologías
- Java 21 o superior.
- JavaFX 21.
- Maven 3.9 o superior.
- MariaDB JDBC.
- MySQL alojado en Aiven.
- CSS para estilos JavaFX.
JavaFX y el controlador de MariaDB se descargan automáticamente mediante Maven;
no es necesario agregar archivos JAR manualmente.
## Estructura
FastQueue/
├── pom.xml
├── run-fastqueue.ps1
├── cartificates/
│   └── ca.pem
└── src/
    └── main/
        ├── java/
        │   ├── controller/
        │   ├── dao/
        │   ├── database/
        │   ├── model/
        │   ├── view/
        │   ├── Main.java
        │   └── FastQueueApp.java
        └── resources/
            ├── database/
            ├── images/
            └── styles/

