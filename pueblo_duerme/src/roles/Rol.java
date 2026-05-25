package roles;

import juego.JuegoJuego;
import juego.Jugador;

public abstract class Rol {
    protected final String nombreRol;
    protected final String descripcion;

    public Rol(String nombreRol, String descripcion) {
        this.nombreRol = nombreRol;
        this.descripcion = descripcion;
    }

    public abstract void accionNocturna(Jugador actor, Jugador objetivo, JuegoJuego juego);

    public boolean esLobo() {
        return false;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return nombreRol;
    }
}
