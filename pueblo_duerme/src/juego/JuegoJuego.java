package juego;

import roles.*;

import java.util.*;

public class JuegoJuego {

    private Pueblo  pueblo;
    private int     rondaActual = 0;
    private boolean esNoche     = true;

    // Jugadores que morirán al resolver la noche
    private final Set<Jugador> pendientesDeMorir = new HashSet<>();
    // Jugadores salvados por la Bruja esta noche
    private final Set<Jugador> salvadosEstaNoche = new HashSet<>();

    private final Scanner scanner = new Scanner(System.in);

    // ══════════════════════════════════════════════
    //  INICIO
    // ══════════════════════════════════════════════

    public void iniciarJuego() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   LOBOS DE LA ALDEA – El Pueblo      ║");
        System.out.println("║              Duerme                  ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        List<Jugador> jugadores = pedirJugadores();
        asignarRoles(jugadores);
        pueblo = new Pueblo(jugadores);

        mostrarRolesPrivados(jugadores);

        System.out.println("\n¡El juego comienza! El pueblo se duerme...\n");
        bucleJuego();
    }

    private List<Jugador> pedirJugadores() {
        List<Jugador> lista = new ArrayList<>();
        System.out.print("¿Cuántos jugadores? (mínimo 4): ");
        int n = leerEntero(4, 20);

        for (int i = 1; i <= n; i++) {
            System.out.print("Nombre del jugador " + i + ": ");
            String nombre = scanner.nextLine().trim();
            // Rol temporal; se asigna en asignarRoles()
            lista.add(new Jugador(nombre, new Aldeano()));
        }
        return lista;
    }

    /**
     * Asigna roles al azar respetando una distribución razonable:
     *  - 1-2 Lobos según el número de jugadores
     *  - 1 Vidente (siempre)
     *  - 1 Bruja   (si hay ≥ 6 jugadores)
     *  - 1 Cazador (si hay ≥ 7 jugadores)
     *  - Resto: Aldeanos
     */
    private void asignarRoles(List<Jugador> jugadores) {
        int n = jugadores.size();
        List<Rol> roles = new ArrayList<>();

        int numLobos = n <= 5 ? 1 : (n <= 8 ? 2 : 3);
        for (int i = 0; i < numLobos; i++) roles.add(new Lobo());

        roles.add(new Vidente());
        if (n >= 6) roles.add(new Bruja());
        if (n >= 7) roles.add(new Cazador());

        while (roles.size() < n) roles.add(new Aldeano());

        Collections.shuffle(roles);

        // Asignar mediante reflexión-style: recrear Jugadores con rol
        for (int i = 0; i < n; i++) {
            Jugador j = jugadores.get(i);
            // Hack limpio: reemplazamos el rol usando un nuevo Jugador con mismo nombre
            jugadores.set(i, new Jugador(j.getNombre(), roles.get(i)));
        }
    }

    private void mostrarRolesPrivados(List<Jugador> jugadores) {
        System.out.println("\n══ ROLES ASIGNADOS (solo tú debes ver esto) ══");
        jugadores.forEach(j ->
            System.out.println("  " + j.getNombre() + " → " + j.getRol().getNombreRol()
                    + "  (" + j.getRol().getDescripcion() + ")")
        );
        System.out.println("══════════════════════════════════════════════");
        pausa("Pulsa ENTER para continuar...");
    }

    // ══════════════════════════════════════════════
    //  BUCLE PRINCIPAL
    // ══════════════════════════════════════════════

    private void bucleJuego() {
        while (comprobarGanador() == null) {
            rondaActual++;
            esNoche = true;
            System.out.println("\n\n══════════════  RONDA " + rondaActual + "  ══════════════");

            faseNoche();
            resolverMuertes();
            if (comprobarGanador() != null) break;

            cambiarFase();
            faseDia();
            resolverMuertes();
            cambiarFase();

            mostrarResumenRonda();
        }

        anunciarGanador(comprobarGanador());
    }

    // ══════════════════════════════════════════════
    //  FASE NOCHE
    // ══════════════════════════════════════════════

    public void faseNoche() {
        pendientesDeMorir.clear();
        salvadosEstaNoche.clear();

        System.out.println("\n🌙 NOCHE — el pueblo duerme...\n");
        pueblo.mostrarEstadoPublico();

        accionLobos();
        accionVidente();
        accionBruja();
    }

    private void accionLobos() {
        List<Jugador> lobos = getLobosVivos();
        if (lobos.isEmpty()) return;

        System.out.println("\n[LOBOS despiertan]");
        List<Jugador> objetivos = getAldeanosVivos();

        System.out.println("  Jugadores disponibles para atacar:");
        mostrarLista(objetivos);

        System.out.print("  Los lobos eligen víctima (número): ");
        int idx = leerEntero(1, objetivos.size()) - 1;
        Jugador victima = objetivos.get(idx);

        // Todos los lobos ejecutan la acción sobre la víctima elegida
        for (Jugador lobo : lobos) {
            lobo.ejecutarAccionNocturna(this, victima);
        }
    }

    private void accionVidente() {
        Optional<Jugador> vidente = pueblo.getVivos().stream()
                .filter(j -> j.getRol() instanceof Vidente)
                .findFirst();

        if (vidente.isEmpty()) return;

        System.out.println("\n[VIDENTE despierta]");
        List<Jugador> otros = getVivosExcepto(vidente.get());

        System.out.println("  Jugadores disponibles para investigar:");
        mostrarLista(otros);

        System.out.print("  " + vidente.get().getNombre() + ", elige jugador (número): ");
        int idx = leerEntero(1, otros.size()) - 1;
        vidente.get().ejecutarAccionNocturna(this, otros.get(idx));
    }

    private void accionBruja() {
        Optional<Jugador> brujaPj = pueblo.getVivos().stream()
                .filter(j -> j.getRol() instanceof Bruja)
                .findFirst();

        if (brujaPj.isEmpty()) return;

        Bruja bruja = (Bruja) brujaPj.get().getRol();
        System.out.println("\n[BRUJA despierta]");

        // Cura
        if (bruja.tieneCura() && !pendientesDeMorir.isEmpty()) {
            System.out.println("  Esta noche van a morir: ");
            pendientesDeMorir.forEach(j -> System.out.println("    - " + j.getNombre()));
            System.out.print("  ¿Quieres usar la poción de CURA? (s/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
                List<Jugador> moribundos = new ArrayList<>(pendientesDeMorir);
                mostrarLista(moribundos);
                System.out.print("  Elige a quién salvar (número): ");
                int idx = leerEntero(1, moribundos.size()) - 1;
                bruja.usarCura(moribundos.get(idx), this);
            }
        }

        // Veneno
        if (bruja.tieneVeneno()) {
            System.out.print("  ¿Quieres usar la poción de VENENO? (s/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
                List<Jugador> vivos = getVivosExcepto(brujaPj.get());
                mostrarLista(vivos);
                System.out.print("  Elige a quién envenenar (número): ");
                int idx = leerEntero(1, vivos.size()) - 1;
                bruja.usarVeneno(vivos.get(idx), this);
            }
        }
    }

    // ══════════════════════════════════════════════
    //  FASE DÍA
    // ══════════════════════════════════════════════

    public void faseDia() {
        System.out.println("\n☀️  DÍA — el pueblo despierta.\n");
        pueblo.mostrarEstadoPublico();

        votacion();
    }

    private void votacion() {
        List<Jugador> vivos = pueblo.getVivos();
        Map<Jugador, Integer> votos = new HashMap<>();
        vivos.forEach(j -> votos.put(j, 0));

        System.out.println("\n── VOTACIÓN ──");
        for (Jugador votante : vivos) {
            List<Jugador> candidatos = getVivosExcepto(votante);
            System.out.println("\n  Turno de " + votante.getNombre() + ":");
            mostrarLista(candidatos);
            System.out.print("  Vota contra (número): ");
            int idx = leerEntero(1, candidatos.size()) - 1;
            Jugador objetivo = candidatos.get(idx);
            votante.votar(objetivo);
            votos.merge(objetivo, 1, Integer::sum);
        }

        // Jugador con más votos es linchado
        Jugador linchado = votos.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (linchado != null) {
            System.out.println("\n  ¡El pueblo vota linchar a " + linchado.getNombre() + "!");
            System.out.println("  Su rol era: " + linchado.getRol().getNombreRol());
            marcarParaMorir(linchado);
        }
    }

    // ══════════════════════════════════════════════
    //  RESOLUCIÓN DE MUERTES
    // ══════════════════════════════════════════════

    public void resolverMuertes() {
        if (pendientesDeMorir.isEmpty()) {
            System.out.println("\n  Esta noche nadie ha muerto.");
            return;
        }

        System.out.println("\n── MUERTES ──");
        for (Jugador j : pendientesDeMorir) {
            if (!j.isVivo()) continue; // ya estaba muerto (Cazador encadenado)

            System.out.println("  ✝ " + j.getNombre() + " ha muerto. Era: " + j.getRol().getNombreRol());
            j.morir();

            // Habilidad del Cazador al morir
            if (j.getRol() instanceof Cazador cazador) {
                cazador.alMorir(j, this);
            }
        }
        pendientesDeMorir.clear();
        salvadosEstaNoche.clear();
    }

    // ══════════════════════════════════════════════
    //  GANADOR / RESUMEN
    // ══════════════════════════════════════════════

    /** @return "Lobos", "Aldeanos" o null si la partida sigue. */
    public String comprobarGanador() {
        long lobos     = pueblo.contarLobosVivos();
        long aldeanos  = pueblo.contarAldeanosVivos();

        if (lobos == 0)          return "Aldeanos";
        if (lobos >= aldeanos)   return "Lobos";
        return null;
    }

    public void mostrarResumenRonda() {
        System.out.println("\n── FIN RONDA " + rondaActual + " ──");
        System.out.println("  Lobos vivos:    " + pueblo.contarLobosVivos());
        System.out.println("  Aldeanos vivos: " + pueblo.contarAldeanosVivos());
    }

    private void anunciarGanador(String ganador) {
        System.out.println("\n\n╔══════════════════════════════════════╗");
        System.out.println("║         FIN DE LA PARTIDA            ║");
        System.out.printf( "║  ¡¡¡ %s ganan !!!%s║%n",
                ganador, " ".repeat(Math.max(0, 22 - ganador.length())));
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("\nRoles de todos los jugadores:");
        pueblo.getTodos().forEach(j ->
            System.out.println("  " + j.getNombre() + " → " + j.getRol().getNombreRol()
                    + (j.isVivo() ? "" : " (eliminado)"))
        );
    }

    // ══════════════════════════════════════════════
    //  UTILIDADES PÚBLICAS (usadas por roles)
    // ══════════════════════════════════════════════

    public void marcarParaMorir(Jugador j) {
        if (!salvadosEstaNoche.contains(j)) {
            pendientesDeMorir.add(j);
        }
    }

    public void salvarDeNoche(Jugador j) {
        pendientesDeMorir.remove(j);
        salvadosEstaNoche.add(j);
    }

    public Pueblo getPueblo() { return pueblo; }

    // ══════════════════════════════════════════════
    //  HELPERS PRIVADOS
    // ══════════════════════════════════════════════

    public void cambiarFase() {
        esNoche = !esNoche;
    }

    private List<Jugador> getLobosVivos() {
        return pueblo.getVivos().stream()
                .filter(j -> j.getRol().esLobo())
                .toList();
    }

    private List<Jugador> getAldeanosVivos() {
        return pueblo.getVivos().stream()
                .filter(j -> !j.getRol().esLobo())
                .toList();
    }

    private List<Jugador> getVivosExcepto(Jugador excluido) {
        return pueblo.getVivos().stream()
                .filter(j -> !j.equals(excluido))
                .toList();
    }

    private void mostrarLista(List<Jugador> lista) {
        for (int i = 0; i < lista.size(); i++) {
            System.out.println("    " + (i + 1) + ". " + lista.get(i).getNombre());
        }
    }

    private int leerEntero(int min, int max) {
        while (true) {
            try {
                int v = Integer.parseInt(scanner.nextLine().trim());
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignored) {}
            System.out.print("  Introduce un número entre " + min + " y " + max + ": ");
        }
    }

    private void pausa(String msg) {
        System.out.print(msg);
        scanner.nextLine();
    }
}
