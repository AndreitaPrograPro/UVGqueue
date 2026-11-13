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
- Búsqueda y un filtro único con opciones de estado, menor espera y mayor espera.
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
            
## Información de la base de datos
La aplicación usa MySQL alojado en Aiven. Las credenciales no se guardan en el
repositorio; cada integrante debe configurar localmente estas variables de entorno:

| Variable | Descripción |
|---|---|
| `DB_HOST` | Dirección del servidor |
| `DB_PORT` | Puerto del servidor |
| `DB_NAME` | Nombre de la base |
| `DB_USER` | Usuario de conexión |
| `DB_PASSWORD` | Contraseña de conexión |
| `DB_SSL_CA` | URI local de `cartificates/ca.pem` |

Los valores reales deben compartirse por un medio privado y nunca agregarse a Git.


## Acceder/editar base de datos
Se usó DBeaver para la creación y manejo del SQL.
1. Click: Database -> New Database Connection -> MySQL -> Next -> Controller for SQL 8+ -> Next
2. Copiar Server Host, Port, Database, User Name, Password.
3. Abrir pestaña SSL en el + verde arriba a la derecha de esta pestaña.
4. Agregar la ruta local del certificado `cartificates/ca.pem`.
5. Click: Probar conexión y descargar drivers de ser necesario.
6. Click: Finalizar

## Cómo compilar y ejecutar
mvn clean compile javafx:run
