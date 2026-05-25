package roles;

import juego.JuegoJuego;
import juego.Jugador;

public class Aldeano extends Rol {

    public Aldeano() {
        super("Aldeano", "No actua por la noche. Su fuerza esta en el voto.");
    }

    @Override
    public void accionNocturna(Jugador actor, Jugador objetivo, JuegoJuego juego) {
        // El aldeano duerme durante la noche.
    }
}
