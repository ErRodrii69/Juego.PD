package juego;

import java.util.ArrayList;
import java.util.List;

public class Pueblo {

    private final List<Jugador> jugadores;

    public Pueblo(List<Jugador> jugadores) {
        this.jugadores = new ArrayList<>(jugadores);
    }

    public List<Jugador> getVivos() {
        return jugadores.stream()
                .filter(Jugador::isVivo)
                .toList();
    }

    public long contarLobosVivos() {
        return getVivos().stream()
                .filter(jugador -> jugador.getRol().esLobo())
                .count();
    }

    public long contarAldeanosVivos() {
        return getVivos().stream()
                .filter(jugador -> !jugador.getRol().esLobo())
                .count();
    }

    public void mostrarEstadoPublico() {
        System.out.println(describirEstadoPublico());
    }

    public String describirEstadoPublico() {
        StringBuilder builder = new StringBuilder();
        builder.append("Jugadores vivos:\n");
        for (Jugador jugador : getVivos()) {
            builder.append(" - ").append(jugador.getNombre()).append('\n');
        }
        builder.append("Total vivos: ").append(getVivos().size());
        return builder.toString();
    }

    public List<Jugador> getTodos() {
        return List.copyOf(jugadores);
    }
}
