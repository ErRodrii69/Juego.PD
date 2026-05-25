package juego;

import roles.Rol;

public class Jugador {

    private String nombre;
    private Rol rol;
    private boolean vivo = true;

    public Jugador(String nombre, Rol rol) {
        this.nombre = nombre;
        this.rol    = rol;
    }

    /** Delega la acción nocturna en el Rol correspondiente. */
    public void ejecutarAccionNocturna(JuegoJuego juego, Jugador objetivo) {
        if (vivo) {
            rol.accionNocturna(this, objetivo, juego);
        }
    }

    /** Registra el voto de este jugador (la lógica real está en JuegoJuego). */
    public void votar(Jugador objetivo) {
        if (vivo) {
            System.out.println("  " + nombre + " vota contra " + objetivo.getNombre());
        }
    }

    /** Marca al jugador como muerto. */
    public void morir() {
        this.vivo = false;
    }

    // ── Getters ──────────────────────────────────────────────
    public String getNombre() { return nombre; }
    public Rol    getRol()    { return rol; }
    public boolean isVivo()   { return vivo; }

    @Override
    public String toString() {
        return nombre + (vivo ? "" : " (muerto)");
    }
}
