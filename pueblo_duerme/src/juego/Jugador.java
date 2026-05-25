package juego;

import roles.Rol;

public class Jugador {

    private final String nombre;
    private final Rol rol;
    private boolean vivo = true;

    public Jugador(String nombre, Rol rol) {
        this.nombre = nombre;
        this.rol = rol;
    }

    public void ejecutarAccionNocturna(JuegoJuego juego, Jugador objetivo) {
        if (vivo) {
            rol.accionNocturna(this, objetivo, juego);
        }
    }

    public void votar(Jugador objetivo) {
        if (!vivo || objetivo == null || !objetivo.isVivo()) {
            throw new IllegalStateException("El voto no es valido.");
        }
    }

    public void morir() {
        vivo = false;
    }

    public String getNombre() {
        return nombre;
    }

    public Rol getRol() {
        return rol;
    }

    public boolean isVivo() {
        return vivo;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
