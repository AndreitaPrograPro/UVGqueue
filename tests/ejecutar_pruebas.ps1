$ErrorActionPreference = 'Stop'
Push-Location (Join-Path $PSScriptRoot '..')
try {
    New-Item -ItemType Directory -Force 'target/menu-tests' | Out-Null
    $fuentes = @(
        'src/main/java/model/ElementoMenu.java',
        'src/main/java/model/MenuRestaurante.java',
        'src/main/java/service/FiltroMenu.java',
        'src/main/java/database/Databaseconnection.java',
        'src/main/java/dao/MenuDAO.java',
        'tests/MenuFeatureTest.java'
    )
    javac --release 21 -encoding UTF-8 -d target/menu-tests $fuentes
    if ($LASTEXITCODE -ne 0) { throw 'La compilación de las pruebas falló.' }
    java -cp target/menu-tests MenuFeatureTest
    if ($LASTEXITCODE -ne 0) { throw 'Las pruebas fallaron.' }
} finally {
    Pop-Location
}
