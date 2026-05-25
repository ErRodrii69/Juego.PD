package roles;

import juego.JuegoJuego;
import juego.Jugador;

public class Bruja extends Rol {

    private boolean tieneCura = true;
    private boolean tieneVeneno = true;

    public Bruja() {
        super("Bruja", "Tiene una pocion de cura y una de veneno, ambas de uso unico.");
    }

    @Override
    public void accionNocturna(Jugador actor, Jugador objetivo, JuegoJuego juego) {
        // La bruja se resuelve desde la clase central del juego.
    }

    public boolean usarCura(Jugador objetivo, JuegoJuego juego) {
        if (tieneCura && objetivo != null) {
            juego.salvarDeNoche(objetivo);
            tieneCura = false;
            return true;
        }
        return false;
    }

    public boolean usarVeneno(Jugador objetivo, JuegoJuego juego) {
        if (tieneVeneno && objetivo != null && objetivo.isVivo()) {
            juego.marcarParaMorir(objetivo);
            tieneVeneno = false;
            return true;
        }
        return false;
    }

    public boolean tieneCura() {
        return tieneCura;
    }

    public boolean tieneVeneno() {
        return tieneVeneno;
    }
}
