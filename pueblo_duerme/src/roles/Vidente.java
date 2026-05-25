package roles;

import juego.Jugador;
import juego.JuegoJuego;

public class Vidente extends Rol {

    public Vidente() {
        super("Vidente", "Puede revelar el rol secreto de un jugador cada noche.");
    }

    @Override
    public void accionNocturna(Jugador actor, Jugador objetivo, JuegoJuego juego) {
        if (objetivo != null && objetivo.isVivo()) {
            System.out.println("  [NOCHE - VIDENTE] " + actor.getNombre()
                    + " ve que " + objetivo.getNombre()
                    + " es: " + objetivo.getRol().getNombreRol());
        }
    }
}
