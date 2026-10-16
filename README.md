UVGqueue 
Desarrolladores: 
- Andrea Ramazzini
- Sebastian Tunchez
- Ana Sofía del Alguila
- Daniel Chou Jo

## Descripción

Un programa que ayuda a gestionar el tiempo de estudiantes y personal, al mostrar los tiempos de fila de los diferentes restaurantes de la universidad.


## Información base de datos

Se usó el servicio de AIVEN gratis: 1GB RAM, 1GB Storage, 1 CPU
Version: MySQL 8.4.11
Maintenance window: Saturdays after 09:15:24 UTC
Tabla de información:
Host        |   mysql-154b58c1-uvgqueue.f.aivencloud.com
Puerto      |   17652
Nombre base |   uvgqueue
Usuario     |   avnadmin
Contraseña  |   (git secrets)
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
DB_PASSWORD |   (git secrets)
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


## Prueba de conexión desde la terminal

Compilar:
javac -encoding UTF-8 -cp "lib/*" -d bin UVGqueue/database/Databaseconnection.java UVGqueue/database/PruebaConexion.java

Ejecutar:
java -cp "bin;lib/*" database.PruebaConexion

Resultado esperado:
Conexión exitosa
Base: uvgqueue
Restaurantes: (cantidad de restaurantes)


## Cómo compilar 

$fuentes = (Get-ChildItem .\UVGqueue -Recurse -Filter *.java).FullName
javac -cp "lib/*" -d bin $fuentes


## Cómo ejecutar

java -cp "bin;lib/*" Main