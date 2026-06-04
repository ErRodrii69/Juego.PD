@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo.
echo ╔═════════════════════════════════════════╗
echo ║  LOBOS DE LA ALDEA - EL PUEBLO DUERME   ║
echo ╚═════════════════════════════════════════╝
echo.

:: Verificar que estamos en el directorio correcto
if not exist "pueblo_duerme\src" (
    echo ERROR: No se encontró la carpeta pueblo_duerme\src
    echo Asegúrate de ejecutar este script desde la raíz del proyecto.
    pause
    exit /b 1
)

echo [1/3] Creando carpeta de compilación...
if not exist "pueblo_duerme\out" mkdir pueblo_duerme\out

echo [2/3] Compilando proyecto (esto puede tomar unos segundos)...

:: Usar PowerShell para obtener los archivos .java recursivamente
for /f "delims=" %%F in ('powershell -NoProfile -Command "Get-ChildItem -Path pueblo_duerme\src -Recurse -Filter *.java | ForEach-Object {$_.FullName}"') do (
    set "JAVA_FILES=!JAVA_FILES! %%F"
)

cd pueblo_duerme
javac -encoding UTF-8 -d out %JAVA_FILES%
if errorlevel 1 (
    echo.
    echo ERROR: La compilación falló. Verifica que Java está instalado.
    echo Instala Java desde: https://www.oracle.com/java/technologies/downloads/
    cd ..
    pause
    exit /b 1
)

echo [3/3] Iniciando juego...
echo.
cd ..
timeout /t 1 /nobreak
java -cp pueblo_duerme\out Main

if errorlevel 1 (
    echo.
    echo ERROR: No se pudo iniciar el juego.
    pause
    exit /b 1
)

pause
