UVGqueue 
Desarrolladores: 
- Andrea Ramazzini
- Sebastian Tunchez
- Ana Sofía del Alguila
- Daniel Chou Jo

## Descripción
Un programa que ayuda a gestionar el tiempo de estudiantes y personal, al mostrar los tiempos de fila de los diferentes restaurantes de la universidad.

## Cómo compilar

$fuentes = (Get-ChildItem .\UVGqueue -Recurse -Filter *.java).FullName
javac -cp "lib/*" -d bin $fuentes

## Cómo ejecutar

java -cp "bin;lib/*" Main