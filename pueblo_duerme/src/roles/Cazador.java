package roles;

import juego.JuegoJuego;
import juego.Jugador;
import java.util.List;
import java.util.Random;

public class Cazador extends Rol {

    private final Random random = new Random();

    public Cazador() {
        super("Cazador", "Si muere, derriba a otro jugador vivo al azar.");
    }

    @Override
    public void accionNocturna(Jugador actor, Jugador objetivo, JuegoJuego juego) {
        // El cazador no tiene accion nocturna propia.
    }

    public Jugador elegirObjetivoAlMorir(Jugador cazador, JuegoJuego juego) {
        List<Jugador> vivos = juego.getPueblo().getVivos();
        if (vivos.isEmpty()) {
            return null;
        }
        return vivos.get(random.nextInt(vivos.size()));
    }
}
