package juego;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Pueblo {

    private List<Jugador> jugadores;

    public Pueblo(List<Jugador> jugadores) {
        this.jugadores = new ArrayList<>(jugadores);
    }

    /** Devuelve solo los jugadores vivos. */
    public List<Jugador> getVivos() {
        return jugadores.stream()
                .filter(Jugador::isVivo)
                .collect(Collectors.toList());
    }

    /** Cuenta los lobos vivos. */
    public long contarLobosVivos() {
        return getVivos().stream()
                .filter(j -> j.getRol().esLobo())
                .count();
    }

    /** Cuenta los aldeanos/especiales vivos (no-lobos). */
    public long contarAldeanosVivos() {
        return getVivos().stream()
                .filter(j -> !j.getRol().esLobo())
                .count();
    }

    /** Muestra el estado público: jugadores vivos sin revelar roles. */
    public void mostrarEstadoPublico() {
        System.out.println("\n══ JUGADORES VIVOS ══");
        getVivos().forEach(j -> System.out.println("  • " + j.getNombre()));
        System.out.println("  Total vivos: " + getVivos().size()
                + "  (Lobos conocidos: ocultos)");
    }

    public List<Jugador> getTodos() { return jugadores; }
}
