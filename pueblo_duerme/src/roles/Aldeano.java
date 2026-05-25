package roles;

import juego.Jugador;
import juego.JuegoJuego;

public class Aldeano extends Rol {

    public Aldeano() {
        super("Aldeano", "No tiene habilidades especiales. Su fuerza está en el voto.");
    }

    @Override
    public void accionNocturna(Jugador actor, Jugador objetivo, JuegoJuego juego) {
        // El aldeano duerme por la noche, no hace nada
    }
}
