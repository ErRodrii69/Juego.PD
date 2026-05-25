package juego;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import roles.Aldeano;
import roles.Bruja;
import roles.Cazador;
import roles.Lobo;
import roles.Rol;
import roles.Vidente;

public class JuegoJuego {

    public static final int MIN_JUGADORES = 4;
    public static final int MAX_JUGADORES = 12;

    private final Random random = new Random();
    private final Set<Jugador> pendientesDeMorir = new LinkedHashSet<>();
    private final Set<Jugador> salvadosEstaNoche = new HashSet<>();
    private final List<String> registroPublico = new ArrayList<>();
    private final Map<Jugador, Integer> votosDia = new HashMap<>();

    private Pueblo pueblo;
    private int rondaActual;
    private boolean esNoche = true;
    private EstadoJuego estado = EstadoJuego.CONFIGURACION;
    private int indiceRevelacion;
    private int indiceVotacion;
    private String ganador;
    private String ultimoMensajePrivado = "";

    public void prepararPartida(List<String> nombresOriginales) {
        Objects.requireNonNull(nombresOriginales, "La lista de nombres no puede ser nula.");

        List<String> nombres = normalizarNombres(nombresOriginales);
        validarNombres(nombres);

        List<Jugador> jugadores = new ArrayList<>();
        for (String nombre : nombres) {
            jugadores.add(new Jugador(nombre, new Aldeano()));
        }

        asignarRoles(jugadores);
        pueblo = new Pueblo(jugadores);
        rondaActual = 0;
        esNoche = true;
        estado = EstadoJuego.REVELANDO_ROLES;
        indiceRevelacion = 0;
        indiceVotacion = 0;
        ganador = null;
        ultimoMensajePrivado = "";
        pendientesDeMorir.clear();
        salvadosEstaNoche.clear();
        registroPublico.clear();
        votosDia.clear();
    }

    public void iniciarJuego() {
        asegurarPartidaPreparada();
        if (estado != EstadoJuego.LISTA_PARA_INICIAR) {
            throw new IllegalStateException("La partida aun no esta lista para comenzar.");
        }

        registroPublico.clear();
        registroPublico.add("La partida comienza. El sistema sera quien anuncie cada resultado oficial.");
        faseNoche();
    }

    public void faseNoche() {
        asegurarPartidaPreparada();

        rondaActual++;
        esNoche = true;
        estado = EstadoJuego.NOCHE_LOBOS;
        ultimoMensajePrivado = "";
        pendientesDeMorir.clear();
        salvadosEstaNoche.clear();
        votosDia.clear();
        indiceVotacion = 0;

        registroPublico.add("Ronda " + rondaActual + ": cae la noche sobre la aldea.");
        avanzarAccionesNocturnasAutomaticamente();
    }

    public void faseDia() {
        asegurarPartidaPreparada();

        esNoche = false;
        estado = EstadoJuego.DIA_RESOLUCION;
        registroPublico.add("Ronda " + rondaActual + ": amanece.");

        List<Jugador> muertos = resolverMuertes();
        if (muertos.isEmpty()) {
            registroPublico.add("Nadie ha muerto durante la noche.");
        }

        ganador = comprobarGanador();
        if (ganador != null) {
            estado = EstadoJuego.FIN;
            registroPublico.add("La partida termina al amanecer.");
        }
    }

    public List<Jugador> resolverMuertes() {
        List<Jugador> muertosResueltos = new ArrayList<>();
        Deque<Jugador> cola = new ArrayDeque<>(pendientesDeMorir);
        Set<Jugador> procesados = new HashSet<>();

        pendientesDeMorir.clear();

        while (!cola.isEmpty()) {
            Jugador objetivo = cola.removeFirst();
            if (objetivo == null || !objetivo.isVivo() || !procesados.add(objetivo)) {
                continue;
            }

            objetivo.morir();
            muertosResueltos.add(objetivo);
            registroPublico.add(objetivo.getNombre() + " ha muerto. Su rol era " + objetivo.getRol().getNombreRol() + ".");

            if (objetivo.getRol() instanceof Cazador cazador) {
                Jugador represalia = cazador.elegirObjetivoAlMorir(objetivo, this);
                if (represalia != null && represalia.isVivo()) {
                    registroPublico.add("El cazador dispara en su ultimo aliento contra " + represalia.getNombre() + ".");
                    cola.addLast(represalia);
                }
            }
        }

        salvadosEstaNoche.clear();
        return muertosResueltos;
    }

    public String comprobarGanador() {
        if (pueblo == null) {
            return null;
        }

        long lobos = pueblo.contarLobosVivos();
        long aldeanos = pueblo.contarAldeanosVivos();

        if (lobos == 0) {
            return "Aldeanos";
        }
        if (lobos >= aldeanos) {
            return "Lobos";
        }
        return null;
    }

    public void cambiarFase() {
        esNoche = !esNoche;
    }

    public void mostrarResumenRonda() {
        if (pueblo == null) {
            return;
        }

        registroPublico.add(
                "Fin de ronda " + rondaActual + ". Siguen en pie " + pueblo.getVivos().size() + " jugadores.");
    }

    public void marcarParaMorir(Jugador jugador) {
        if (jugador != null && jugador.isVivo() && !salvadosEstaNoche.contains(jugador)) {
            pendientesDeMorir.add(jugador);
        }
    }

    public void salvarDeNoche(Jugador jugador) {
        if (jugador == null) {
            return;
        }
        pendientesDeMorir.remove(jugador);
        salvadosEstaNoche.add(jugador);
    }

    public void registrarVisionPrivada(Jugador actor, Jugador objetivo) {
        ultimoMensajePrivado = actor.getNombre()
                + " descubre que "
                + objetivo.getNombre()
                + " es "
                + objetivo.getRol().getNombreRol()
                + ".";
    }

    public String resolverAccionNocturnaActual(Jugador objetivo) {
        asegurarPartidaPreparada();

        switch (estado) {
            case NOCHE_LOBOS -> resolverAccionLobos(objetivo);
            case NOCHE_VIDENTE -> resolverAccionVidente(objetivo);
            case NOCHE_BRUJA_CURA -> resolverAccionBrujaCura(objetivo);
            case NOCHE_BRUJA_VENENO -> resolverAccionBrujaVeneno(objetivo);
            default -> throw new IllegalStateException("No hay una accion nocturna pendiente.");
        }

        avanzarAccionesNocturnasAutomaticamente();
        return ultimoMensajePrivado;
    }

    public void iniciarVotacion() {
        asegurarPartidaPreparada();
        if (estado != EstadoJuego.DIA_RESOLUCION) {
            throw new IllegalStateException("La votacion no puede comenzar en este momento.");
        }

        List<Jugador> vivos = pueblo.getVivos();
        if (vivos.size() < 2) {
            mostrarResumenRonda();
            ganador = comprobarGanador();
            estado = ganador == null ? EstadoJuego.DIA_RESULTADO : EstadoJuego.FIN;
            return;
        }

        votosDia.clear();
        for (Jugador jugador : vivos) {
            votosDia.put(jugador, 0);
        }

        indiceVotacion = 0;
        estado = EstadoJuego.DIA_VOTACION;
        ultimoMensajePrivado = "";
        registroPublico.add("Comienza la votacion del dia.");
    }

    public void registrarVotoActual(Jugador objetivo) {
        asegurarPartidaPreparada();
        if (estado != EstadoJuego.DIA_VOTACION) {
            throw new IllegalStateException("No hay una votacion activa.");
        }

        Jugador votante = getVotanteActual();
        List<Jugador> candidatos = getObjetivosDisponiblesActuales();
        if (!candidatos.contains(objetivo)) {
            throw new IllegalArgumentException("El objetivo del voto no es valido.");
        }

        votante.votar(objetivo);
        votosDia.merge(objetivo, 1, Integer::sum);
        ultimoMensajePrivado = votante.getNombre() + " ha emitido su voto.";
        indiceVotacion++;

        if (indiceVotacion >= pueblo.getVivos().size()) {
            resolverVotacion();
        }
    }

    public void avanzarJuegoPublico() {
        asegurarPartidaPreparada();

        switch (estado) {
            case DIA_RESOLUCION -> {
                ganador = comprobarGanador();
                if (ganador == null) {
                    iniciarVotacion();
                } else {
                    estado = EstadoJuego.FIN;
                    registroPublico.add("La victoria ya esta decidida.");
                }
            }
            case DIA_RESULTADO -> {
                ganador = comprobarGanador();
                if (ganador == null) {
                    faseNoche();
                } else {
                    estado = EstadoJuego.FIN;
                }
            }
            default -> throw new IllegalStateException("No hay un avance publico disponible ahora.");
        }
    }

    public boolean hayRolPendiente() {
        return pueblo != null
                && estado == EstadoJuego.REVELANDO_ROLES
                && indiceRevelacion < pueblo.getTodos().size();
    }

    public Jugador getJugadorPendienteDeRevelar() {
        if (!hayRolPendiente()) {
            return null;
        }
        return pueblo.getTodos().get(indiceRevelacion);
    }

    public void avanzarRevelacion() {
        if (!hayRolPendiente()) {
            return;
        }

        indiceRevelacion++;
        if (indiceRevelacion >= pueblo.getTodos().size()) {
            estado = EstadoJuego.LISTA_PARA_INICIAR;
        }
    }

    public EstadoJuego getEstado() {
        return estado;
    }

    public Pueblo getPueblo() {
        return pueblo;
    }

    public List<Jugador> getJugadores() {
        if (pueblo == null) {
            return List.of();
        }
        return pueblo.getTodos();
    }

    public List<String> getRegistroPublico() {
        return List.copyOf(registroPublico);
    }

    public int getRondaActual() {
        return rondaActual;
    }

    public boolean isEsNoche() {
        return esNoche;
    }

    public String getGanador() {
        return ganador;
    }

    public String getUltimoMensajePrivado() {
        return ultimoMensajePrivado;
    }

    public String getResumenPartida() {
        if (pueblo == null) {
            return "";
        }

        StringBuilder resumen = new StringBuilder();
        resumen.append("Ganador: ").append(ganador == null ? "Sin decidir" : ganador).append("\n\n");
        for (Jugador jugador : getJugadores()) {
            resumen.append(jugador.getNombre())
                    .append(" - ")
                    .append(jugador.getRol().getNombreRol())
                    .append(jugador.isVivo() ? " (vivo)" : " (eliminado)")
                    .append('\n');
        }
        return resumen.toString().trim();
    }

    public String getTituloEstado() {
        return switch (estado) {
            case CONFIGURACION -> "Configurar partida";
            case REVELANDO_ROLES -> "Reparto privado de roles";
            case LISTA_PARA_INICIAR -> "Todo listo para empezar";
            case NOCHE_LOBOS, NOCHE_VIDENTE, NOCHE_BRUJA_CURA, NOCHE_BRUJA_VENENO ->
                    "Noche " + rondaActual;
            case DIA_RESOLUCION, DIA_VOTACION, DIA_RESULTADO -> "Dia " + rondaActual;
            case FIN -> "Partida terminada";
        };
    }

    public String getDescripcionEstado() {
        return switch (estado) {
            case CONFIGURACION ->
                    "Introduce a los jugadores y el sistema asignara roles al azar.";
            case REVELANDO_ROLES ->
                    "Cada jugador debe consultar su carta en privado y pasar el dispositivo.";
            case LISTA_PARA_INICIAR ->
                    "Todos conocen su rol. El juego puede arrancar en la fase de noche.";
            case NOCHE_LOBOS ->
                    "Accion privada pendiente: los lobos deben marcar una victima.";
            case NOCHE_VIDENTE ->
                    "Accion privada pendiente: la vidente puede inspeccionar a un jugador.";
            case NOCHE_BRUJA_CURA ->
                    "Accion privada pendiente: la bruja puede decidir si usa su cura.";
            case NOCHE_BRUJA_VENENO ->
                    "Accion privada pendiente: la bruja puede decidir si usa su veneno.";
            case DIA_RESOLUCION ->
                    "Revisa los anuncios oficiales del amanecer y prepara la votacion.";
            case DIA_VOTACION ->
                    "La aldea debate y emite sus votos sin revelar roles voluntariamente.";
            case DIA_RESULTADO ->
                    "La ronda ha terminado. Puedes continuar a la siguiente noche.";
            case FIN ->
                    "El juego ya tiene un ganador. El panel muestra todos los roles.";
        };
    }

    public String getEtiquetaPrivadaActual() {
        return switch (estado) {
            case NOCHE_LOBOS -> getLobosVivos().size() > 1 ? "el grupo de Lobos" : getNombrePrimerLobo();
            case NOCHE_VIDENTE -> getJugadorPorRol(Vidente.class).map(Jugador::getNombre).orElse("la Vidente");
            case NOCHE_BRUJA_CURA, NOCHE_BRUJA_VENENO ->
                    getJugadorPorRol(Bruja.class).map(Jugador::getNombre).orElse("la Bruja");
            case DIA_VOTACION -> {
                Jugador votante = getVotanteActual();
                yield votante == null ? "la aldea" : votante.getNombre();
            }
            default -> "el director de partida";
        };
    }

    public String getTituloDialogoPrivado() {
        return switch (estado) {
            case NOCHE_LOBOS -> "Ataque nocturno";
            case NOCHE_VIDENTE -> "Vision secreta";
            case NOCHE_BRUJA_CURA -> "Pocion de cura";
            case NOCHE_BRUJA_VENENO -> "Pocion de veneno";
            case DIA_VOTACION -> "Turno de voto";
            default -> "Accion";
        };
    }

    public String getDescripcionDialogoPrivado() {
        return switch (estado) {
            case NOCHE_LOBOS -> "Selecciona a quien atacan los lobos esta noche.";
            case NOCHE_VIDENTE -> "Selecciona a quien desea investigar la vidente.";
            case NOCHE_BRUJA_CURA -> pendientesDeMorir.isEmpty()
                    ? "No hay ningun objetivo herido esta noche."
                    : "La bruja puede salvar a uno de los heridos.";
            case NOCHE_BRUJA_VENENO -> "La bruja puede envenenar a un jugador vivo.";
            case DIA_VOTACION -> "Selecciona el objetivo del voto actual.";
            default -> "Sigue las instrucciones del juego.";
        };
    }

    public List<Jugador> getObjetivosDisponiblesActuales() {
        return switch (estado) {
            case NOCHE_LOBOS -> getAldeanosVivos();
            case NOCHE_VIDENTE -> getJugadorPorRol(Vidente.class)
                    .map(this::getVivosExcepto)
                    .orElse(List.of());
            case NOCHE_BRUJA_CURA -> new ArrayList<>(pendientesDeMorir);
            case NOCHE_BRUJA_VENENO -> getJugadorPorRol(Bruja.class)
                    .map(this::getVivosExcepto)
                    .orElse(List.of());
            case DIA_VOTACION -> {
                Jugador votante = getVotanteActual();
                yield votante == null ? List.of() : getVivosExcepto(votante);
            }
            default -> List.of();
        };
    }

    public boolean accionActualEsOpcional() {
        return estado == EstadoJuego.NOCHE_BRUJA_CURA || estado == EstadoJuego.NOCHE_BRUJA_VENENO;
    }

    public Jugador getVotanteActual() {
        if (estado != EstadoJuego.DIA_VOTACION || pueblo == null) {
            return null;
        }

        List<Jugador> vivos = pueblo.getVivos();
        if (indiceVotacion < 0 || indiceVotacion >= vivos.size()) {
            return null;
        }
        return vivos.get(indiceVotacion);
    }

    public int getVotosPendientes() {
        if (estado != EstadoJuego.DIA_VOTACION || pueblo == null) {
            return 0;
        }
        return Math.max(0, pueblo.getVivos().size() - indiceVotacion);
    }

    public int getTotalVivos() {
        return pueblo == null ? 0 : pueblo.getVivos().size();
    }

    public static List<String> describirReparto(int totalJugadores) {
        if (totalJugadores < MIN_JUGADORES || totalJugadores > MAX_JUGADORES) {
            return List.of();
        }

        List<String> reparto = new ArrayList<>();
        int lobos = calcularCantidadLobos(totalJugadores);
        int usados = lobos + 1;
        reparto.add(lobos + (lobos == 1 ? " Lobo" : " Lobos"));
        reparto.add("1 Vidente");

        if (totalJugadores >= 6) {
            reparto.add("1 Bruja");
            usados++;
        }
        if (totalJugadores >= 7) {
            reparto.add("1 Cazador");
            usados++;
        }

        int aldeanos = totalJugadores - usados;
        reparto.add(aldeanos + (aldeanos == 1 ? " Aldeano" : " Aldeanos"));
        return reparto;
    }

    private void resolverAccionLobos(Jugador objetivo) {
        validarObjetivo(objetivo);
        for (Jugador lobo : getLobosVivos()) {
            lobo.ejecutarAccionNocturna(this, objetivo);
        }
        ultimoMensajePrivado = "Los lobos han marcado a " + objetivo.getNombre() + ".";
        avanzarDespuesDeLobos();
    }

    private void resolverAccionVidente(Jugador objetivo) {
        validarObjetivo(objetivo);
        Jugador vidente = getJugadorPorRol(Vidente.class)
                .orElseThrow(() -> new IllegalStateException("No hay vidente viva."));
        vidente.ejecutarAccionNocturna(this, objetivo);
        avanzarDespuesDeVidente();
    }

    private void resolverAccionBrujaCura(Jugador objetivo) {
        Bruja bruja = getBrujaViva();
        if (objetivo == null) {
            ultimoMensajePrivado = "La bruja decide reservar la pocion de cura.";
        } else {
            if (!getObjetivosDisponiblesActuales().contains(objetivo)) {
                throw new IllegalArgumentException("No se puede curar a ese jugador.");
            }
            if (!bruja.usarCura(objetivo, this)) {
                throw new IllegalStateException("La bruja ya no puede usar la cura.");
            }
            ultimoMensajePrivado = "La bruja salva a " + objetivo.getNombre() + ".";
        }
        avanzarDespuesDeCura();
    }

    private void resolverAccionBrujaVeneno(Jugador objetivo) {
        Bruja bruja = getBrujaViva();
        if (objetivo == null) {
            ultimoMensajePrivado = "La bruja decide no usar su veneno.";
        } else {
            if (!getObjetivosDisponiblesActuales().contains(objetivo)) {
                throw new IllegalArgumentException("No se puede envenenar a ese jugador.");
            }
            if (!bruja.usarVeneno(objetivo, this)) {
                throw new IllegalStateException("La bruja ya no puede usar el veneno.");
            }
            ultimoMensajePrivado = "La bruja envenena a " + objetivo.getNombre() + ".";
        }
        faseDia();
    }

    private void resolverVotacion() {
        Collection<Map.Entry<Jugador, Integer>> entradas = votosDia.entrySet();
        int maximo = entradas.stream()
                .map(Map.Entry::getValue)
                .max(Comparator.naturalOrder())
                .orElse(0);

        List<Jugador> empatados = entradas.stream()
                .filter(entry -> entry.getValue() == maximo)
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparing(Jugador::getNombre))
                .toList();

        if (empatados.size() != 1) {
            registroPublico.add("La votacion termina en empate. Nadie es eliminado.");
        } else {
            Jugador linchado = empatados.get(0);
            registroPublico.add("La aldea decide eliminar a " + linchado.getNombre() + ".");
            marcarParaMorir(linchado);
            resolverMuertes();
        }

        mostrarResumenRonda();
        ganador = comprobarGanador();
        estado = ganador == null ? EstadoJuego.DIA_RESULTADO : EstadoJuego.FIN;
    }

    private void avanzarAccionesNocturnasAutomaticamente() {
        boolean seguir;
        do {
            seguir = false;

            if (estado == EstadoJuego.NOCHE_LOBOS && getLobosVivos().isEmpty()) {
                avanzarDespuesDeLobos();
                seguir = true;
            } else if (estado == EstadoJuego.NOCHE_VIDENTE && getJugadorPorRol(Vidente.class).isEmpty()) {
                avanzarDespuesDeVidente();
                seguir = true;
            } else if (estado == EstadoJuego.NOCHE_BRUJA_CURA && !brujaPuedeCurar()) {
                avanzarDespuesDeCura();
                seguir = true;
            } else if (estado == EstadoJuego.NOCHE_BRUJA_VENENO && !brujaPuedeEnvenenar()) {
                faseDia();
                seguir = true;
            }
        } while (seguir);
    }

    private void avanzarDespuesDeLobos() {
        if (getJugadorPorRol(Vidente.class).isPresent()) {
            estado = EstadoJuego.NOCHE_VIDENTE;
        } else if (getJugadorPorRol(Bruja.class).isPresent()) {
            estado = EstadoJuego.NOCHE_BRUJA_CURA;
        } else {
            faseDia();
        }
    }

    private void avanzarDespuesDeVidente() {
        if (getJugadorPorRol(Bruja.class).isPresent()) {
            estado = EstadoJuego.NOCHE_BRUJA_CURA;
        } else {
            faseDia();
        }
    }

    private void avanzarDespuesDeCura() {
        if (getJugadorPorRol(Bruja.class).isPresent()) {
            estado = EstadoJuego.NOCHE_BRUJA_VENENO;
        } else {
            faseDia();
        }
    }

    private boolean brujaPuedeCurar() {
        if (getJugadorPorRol(Bruja.class).isEmpty()) {
            return false;
        }
        return getBrujaViva().tieneCura() && !pendientesDeMorir.isEmpty();
    }

    private boolean brujaPuedeEnvenenar() {
        if (getJugadorPorRol(Bruja.class).isEmpty()) {
            return false;
        }
        Jugador jugadora = getJugadorPorRol(Bruja.class).orElseThrow();
        return getBrujaViva().tieneVeneno() && !getVivosExcepto(jugadora).isEmpty();
    }

    private Bruja getBrujaViva() {
        return (Bruja) getJugadorPorRol(Bruja.class)
                .map(Jugador::getRol)
                .orElseThrow(() -> new IllegalStateException("No hay bruja viva."));
    }

    private List<Jugador> getLobosVivos() {
        if (pueblo == null) {
            return List.of();
        }
        return pueblo.getVivos().stream()
                .filter(jugador -> jugador.getRol().esLobo())
                .toList();
    }

    private String getNombrePrimerLobo() {
        return getLobosVivos().stream()
                .findFirst()
                .map(Jugador::getNombre)
                .orElse("el Lobo");
    }

    private List<Jugador> getAldeanosVivos() {
        if (pueblo == null) {
            return List.of();
        }
        return pueblo.getVivos().stream()
                .filter(jugador -> !jugador.getRol().esLobo())
                .toList();
    }

    private List<Jugador> getVivosExcepto(Jugador excluido) {
        if (pueblo == null) {
            return List.of();
        }
        return pueblo.getVivos().stream()
                .filter(jugador -> !jugador.equals(excluido))
                .toList();
    }

    private <T extends Rol> java.util.Optional<Jugador> getJugadorPorRol(Class<T> tipoRol) {
        if (pueblo == null) {
            return java.util.Optional.empty();
        }
        return pueblo.getVivos().stream()
                .filter(jugador -> tipoRol.isInstance(jugador.getRol()))
                .findFirst();
    }

    private void validarObjetivo(Jugador objetivo) {
        if (!getObjetivosDisponiblesActuales().contains(objetivo)) {
            throw new IllegalArgumentException("El objetivo seleccionado no es valido.");
        }
    }

    private void asegurarPartidaPreparada() {
        if (pueblo == null) {
            throw new IllegalStateException("Todavia no hay una partida preparada.");
        }
    }

    private void asignarRoles(List<Jugador> jugadores) {
        int total = jugadores.size();
        List<Rol> roles = new ArrayList<>();

        int cantidadLobos = calcularCantidadLobos(total);
        for (int i = 0; i < cantidadLobos; i++) {
            roles.add(new Lobo());
        }

        roles.add(new Vidente());
        if (total >= 6) {
            roles.add(new Bruja());
        }
        if (total >= 7) {
            roles.add(new Cazador());
        }

        while (roles.size() < total) {
            roles.add(new Aldeano());
        }

        java.util.Collections.shuffle(roles, random);

        for (int i = 0; i < total; i++) {
            Jugador base = jugadores.get(i);
            jugadores.set(i, new Jugador(base.getNombre(), roles.get(i)));
        }
    }

    private static int calcularCantidadLobos(int totalJugadores) {
        if (totalJugadores <= 5) {
            return 1;
        }
        if (totalJugadores <= 8) {
            return 2;
        }
        return 3;
    }

    private List<String> normalizarNombres(List<String> nombresOriginales) {
        List<String> nombres = new ArrayList<>();
        for (int i = 0; i < nombresOriginales.size(); i++) {
            String nombre = nombresOriginales.get(i) == null ? "" : nombresOriginales.get(i).trim();
            nombres.add(nombre.isEmpty() ? "Jugador " + (i + 1) : nombre);
        }
        return nombres;
    }

    private void validarNombres(List<String> nombres) {
        if (nombres.size() < MIN_JUGADORES || nombres.size() > MAX_JUGADORES) {
            throw new IllegalArgumentException(
                    "La partida necesita entre " + MIN_JUGADORES + " y " + MAX_JUGADORES + " jugadores.");
        }

        Set<String> normalizados = new HashSet<>();
        for (String nombre : nombres) {
            String clave = nombre.toLowerCase(Locale.ROOT);
            if (!normalizados.add(clave)) {
                throw new IllegalArgumentException("No puede haber nombres repetidos.");
            }
        }
    }
}
