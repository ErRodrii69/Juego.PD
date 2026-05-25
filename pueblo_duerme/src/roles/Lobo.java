package roles;

import juego.JuegoJuego;
import juego.Jugador;

public class Lobo extends Rol {

    public Lobo() {
        super("Lobo", "Elige a un jugador para eliminar durante la noche.");
    }

    @Override
    public void accionNocturna(Jugador actor, Jugador objetivo, JuegoJuego juego) {
        if (objetivo != null && objetivo.isVivo()) {
            juego.marcarParaMorir(objetivo);
        }
    }

    @Override
    public boolean esLobo() {
        return true;
    }
}
