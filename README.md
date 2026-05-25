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

Desde la raiz del repositorio:

```powershell
javac -encoding UTF-8 -d pueblo_duerme\out (Get-ChildItem pueblo_duerme\src -Recurse -Filter *.java).FullName
java -cp pueblo_duerme\out Main
```

## Notas

- El juego empieza con configuracion de jugadores y reparto privado de roles.
- Los roles de jugadores vivos permanecen ocultos.
- Las muertes y resultados oficiales los anuncia el sistema.
- La logica principal se mantiene en clases orientadas a objetos como pide el PDF.
