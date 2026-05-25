package juego;

public enum EstadoJuego {
    CONFIGURACION,
    REVELANDO_ROLES,
    LISTA_PARA_INICIAR,
    NOCHE_LOBOS,
    NOCHE_VIDENTE,
    NOCHE_BRUJA_CURA,
    NOCHE_BRUJA_VENENO,
    DIA_RESOLUCION,
    DIA_VOTACION,
    DIA_RESULTADO,
    FIN;

    public boolean esAccionPrivada() {
        return this == NOCHE_LOBOS
                || this == NOCHE_VIDENTE
                || this == NOCHE_BRUJA_CURA
                || this == NOCHE_BRUJA_VENENO;
    }
}
