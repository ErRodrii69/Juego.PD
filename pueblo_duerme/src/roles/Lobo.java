package roles;

import juego.Jugador;
import juego.JuegoJuego;

public class Lobo extends Rol {

    public Lobo() {
        super("Lobo", "Elige a un jugador para eliminar durante la noche.");
    }

    @Override
    public void accionNocturna(Jugador actor, Jugador objetivo, JuegoJuego juego) {
        if (objetivo != null && objetivo.isVivo()) {
            juego.marcarParaMorir(objetivo);
            System.out.println("  [NOCHE] " + actor.getNombre() + " (Lobo) ataca a " + objetivo.getNombre());
        }
    }

    @Override
    public boolean esLobo() {
        return true;
    }
}
