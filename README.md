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
            
## Información base de datos
Se usó el servicio de AIVEN gratis: 1GB RAM, 1GB Storage, 1 CPU
Version: MySQL 8.4.11
Maintenance window: Saturdays after 09:15:24 UTC
Tabla de información:
Host        |   mysql-154b58c1-uvgqueue.f.aivencloud.com
Puerto      |   17652
Nombre base |   uvgqueue
Usuario     |   avnadmin
Contraseña  |   AVNS_eX9Ad_u3Z7hlyeCPOcv
Certificado |   C:\...\UVGqueue\cartificates\ca.pem


## Cómo conectar la base de datos
Windows(tested):
Agregar variables de usuario(Edit environment variables for your account/Editar las variables de entorno de esta cuenta)
Variable    |   Valor
==========================================================
DB_HOST     |   mysql-154b58c1-uvgqueue.f.aivencloud.com
DB_PORT     |   17652
DB_NAME     |   uvgqueue
DB_USER     |   avnadmin
DB_PASSWORD |   AVNS_eX9Ad_u3Z7hlyeCPOcv
DB_SSL_CA   |   C:\...\UVGqueue\cartificates\ca.pem
==========================================================


## Acceder/editar base de datos
Se usó DBeaver para la creación y manejo del SQL.
1. Click: Database -> New Database Connection -> MySQL -> Next -> Controller for SQL 8+ -> Next
2. Copiar Server Host, Port, Database, User Name, Password.
3. Abrir pestaña SSL en el + verde arriba a la derecha de esta pestaña.
4. Agregar la ruta del certificado en CA Certificate: C:\...\UVGqueue\cartificates\ca.pem
5. Click: Probar conexión y descargar drivers de ser necesario.
6. Click: Finalizar

##Cómo compilar y ejecutar
mvn clean compile javafx:run
