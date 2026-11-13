$ErrorActionPreference = "Stop"

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "No se encontró Java. Instala JDK 21 o una versión más reciente." -ForegroundColor Red
    exit 1
}

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "No se encontro Maven. Instala Apache Maven y vuelve a abrir la terminal." -ForegroundColor Yellow
    Write-Host "Después ejecuta: mvn clean compile javafx:run"
    exit 1
}

mvn clean compile javafx:run
