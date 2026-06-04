# Lobos de la Aldea - El Pueblo Duerme

Proyecto Java Swing basado en el enunciado `UT4_Proyecto3.pdf`.

## Estructura

```text
pueblo_duerme/
  src/
    Main.java
    juego/    Control de partida, pueblo, jugadores y estados.
    roles/    Jerarquia de roles del juego.
    ui/       Interfaz grafica Swing.
  docs/       Resumen de requisitos del PDF.
```

Las carpetas `out/` y `build/` son salida de compilacion y no forman parte del codigo fuente.

## Ejecutar

### ⚡ Forma fácil (Recomendado)

**Windows:**
- Haz doble clic en `EJECUTAR_JUEGO.bat`
- El juego se compilará e iniciará automáticamente

**PowerShell:**
- Ejecuta: `.\EJECUTAR_JUEGO.ps1`

### Manual

Desde la raíz del repositorio:

```powershell
javac -encoding UTF-8 -d pueblo_duerme\out (Get-ChildItem pueblo_duerme\src -Recurse -Filter *.java).FullName
java -cp pueblo_duerme\out Main
```

**Requisito:** Java JDK instalado ([descargar](https://www.oracle.com/java/technologies/downloads/))

## Archivos de ayuda

- `LEEME_PRIMERO.txt` - Instrucciones detalladas para ejecutar el juego
- `EJECUTAR_JUEGO.bat` - Script para compilar y ejecutar (haz doble clic)
- `EJECUTAR_JUEGO.ps1` - Alternativa en PowerShell

## Notas

- El juego empieza con configuracion de jugadores y reparto privado de roles.
- Los roles de jugadores vivos permanecen ocultos.
- Las muertes y resultados oficiales los anuncia el sistema.
- La logica principal se mantiene en clases orientadas a objetos como pide el PDF.
- Los archivos compilados se generan en `pueblo_duerme/out/` automáticamente.
