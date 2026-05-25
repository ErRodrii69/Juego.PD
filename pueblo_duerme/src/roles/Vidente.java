package roles;

import juego.JuegoJuego;
import juego.Jugador;

public class Vidente extends Rol {

    public Vidente() {
        super("Vidente", "Puede descubrir el rol de un jugador vivo cada noche.");
    }

    @Override
    public void accionNocturna(Jugador actor, Jugador objetivo, JuegoJuego juego) {
        if (objetivo != null && objetivo.isVivo()) {
            juego.registrarVisionPrivada(actor, objetivo);
        }
    }
}
