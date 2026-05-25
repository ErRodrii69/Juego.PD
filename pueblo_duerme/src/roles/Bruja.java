package roles;

import juego.Jugador;
import juego.JuegoJuego;

public class Bruja extends Rol {

    private boolean tieneCura = true;
    private boolean tieneVeneno = true;

    public Bruja() {
        super("Bruja", "Posee una poción de cura y una de veneno, cada una de uso único.");
    }

    @Override
    public void accionNocturna(Jugador actor, Jugador objetivo, JuegoJuego juego) {
        // La lógica interactiva de cura/veneno se gestiona desde JuegoJuego
        // para mantener el control centralizado del flujo
    }

    public boolean usarCura(Jugador objetivo, JuegoJuego juego) {
        if (tieneCura && objetivo != null) {
            juego.salvarDeNoche(objetivo);
            tieneCura = false;
            System.out.println("  [NOCHE - BRUJA] Usa la poción de CURA sobre " + objetivo.getNombre());
            return true;
        }
        return false;
    }

    public boolean usarVeneno(Jugador objetivo, JuegoJuego juego) {
        if (tieneVeneno && objetivo != null && objetivo.isVivo()) {
            juego.marcarParaMorir(objetivo);
            tieneVeneno = false;
            System.out.println("  [NOCHE - BRUJA] Usa la poción de VENENO sobre " + objetivo.getNombre());
            return true;
        }
        return false;
    }

    public boolean tieneCura()    { return tieneCura; }
    public boolean tieneVeneno()  { return tieneVeneno; }
}
