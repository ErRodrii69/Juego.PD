package roles;

import juego.Jugador;
import juego.JuegoJuego;

import java.util.List;
import java.util.Random;

public class Cazador extends Rol {

    public Cazador() {
        super("Cazador", "Al morir, arrastra consigo a otro jugador aleatorio.");
    }

    @Override
    public void accionNocturna(Jugador actor, Jugador objetivo, JuegoJuego juego) {
        // El cazador no actúa por la noche de forma activa
    }

    /**
     * Se llama automáticamente cuando el Cazador muere.
     * Elige un jugador vivo al azar y lo elimina también.
     */
    public void alMorir(Jugador cazador, JuegoJuego juego) {
        List<Jugador> vivos = juego.getPueblo().getVivos();
        vivos.remove(cazador);

        if (!vivos.isEmpty()) {
            Jugador victima = vivos.get(new Random().nextInt(vivos.size()));
            System.out.println("  [CAZADOR] " + cazador.getNombre()
                    + " cae, pero se lleva a " + victima.getNombre() + " con él!");
            victima.morir();
        }
    }
}
