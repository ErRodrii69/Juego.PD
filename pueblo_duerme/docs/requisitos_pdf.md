# Requisitos del PDF

Resumen operativo del enunciado `UT4_Proyecto3.pdf` usado para mantener el proyecto alineado.

## Clases obligatorias

- `Jugador`: nombre, rol, estado vivo, accion nocturna, voto y muerte.
- `Rol`: clase abstracta con nombre, descripcion, accion nocturna y `esLobo()`.
- `Pueblo`: lista de jugadores, vivos, conteos por bando y estado publico sin roles.
- `JuegoJuego`: control de ronda, fase noche/dia, muertes, ganador y resumen.

## Roles

- `Lobo`: marca una victima por la noche.
- `Aldeano`: no actua por la noche y usa el voto.
- `Vidente`: consulta el rol de un jugador.
- `Bruja`: rol integrado en el reparto con una cura y un veneno.
- `Cazador`: rol integrado en el reparto que dispara al morir.

## Reglas implementadas

- Minimo de 4 jugadores.
- Fase de noche con lobos y roles especiales.
- Fase de dia con anuncios oficiales y votacion.
- Un eliminado deja de actuar y votar.
- Los roles no se revelan voluntariamente.
- Victoria de lobos si lobos vivos >= aldeanos vivos.
- Victoria de aldeanos si no quedan lobos vivos.
