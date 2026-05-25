package ui;

import juego.EstadoJuego;
import juego.JuegoJuego;
import juego.Jugador;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import roles.Aldeano;
import roles.Bruja;
import roles.Cazador;
import roles.Lobo;
import roles.Vidente;

public class JuegoFrame extends JFrame {

    private static final String CARD_CONFIG = "config";
    private static final String CARD_REVEAL = "reveal";
    private static final String CARD_GAME = "game";

    private final JuegoJuego juego = new JuegoJuego();
    private final CardLayout screens = new CardLayout();
    private final JPanel screenPanel = new JPanel(screens);

    private final JSpinner jugadoresSpinner =
            new JSpinner(new SpinnerNumberModel(JuegoJuego.MIN_JUGADORES, JuegoJuego.MIN_JUGADORES,
                    JuegoJuego.MAX_JUGADORES, 1));
    private final JPanel nombresPanel = new JPanel();
    private final List<JTextField> camposJugadores = new ArrayList<>();
    private final JLabel repartoLabel = new JLabel();

    private final JLabel revealStepLabel = new JLabel();
    private final JLabel revealNameLabel = new JLabel();
    private final JLabel revealRoleLabel = new JLabel();
    private final JTextArea revealDescriptionArea = new JTextArea();
    private final AccentButton revealActionButton = new AccentButton("Mostrar mi rol", Theme.TEAL);
    private boolean rolVisible;

    private final JLabel headerTitleLabel = new JLabel("Lobos de la Aldea");
    private final JLabel headerStateLabel = new JLabel();
    private final JLabel headerInfoLabel = new JLabel();
    private final JLabel headerWinnerLabel = new JLabel();
    private final JPanel playersListPanel = new JPanel();
    private final JTextArea logArea = new JTextArea();
    private final JLabel actionTitleLabel = new JLabel();
    private final JLabel actionActorLabel = new JLabel();
    private final JTextArea actionDescriptionArea = new JTextArea();
    private final AccentButton primaryActionButton = new AccentButton("Continuar", Theme.GOLD);
    private final AccentButton secondaryActionButton = new AccentButton("Cancelar", Theme.CARD_ACCENT);

    public JuegoFrame() {
        super("Lobos de la Aldea");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 760));
        setSize(1360, 860);
        setLocationRelativeTo(null);

        setContentPane(buildRoot());
        rebuildPlayerFields((Integer) jugadoresSpinner.getValue());
        refreshRevealScreen();
        refreshGameScreen();
    }

    private JComponent buildRoot() {
        BackdropPanel root = new BackdropPanel();
        root.setLayout(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        screenPanel.setOpaque(false);
        screenPanel.add(buildConfigScreen(), CARD_CONFIG);
        screenPanel.add(buildRevealScreen(), CARD_REVEAL);
        screenPanel.add(buildGameScreen(), CARD_GAME);

        root.add(screenPanel, BorderLayout.CENTER);
        screens.show(screenPanel, CARD_CONFIG);
        return root;
    }

    private JComponent buildConfigScreen() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        RoundedPanel card = new RoundedPanel(Theme.CARD_SOFT, Theme.CARD_END, 34);
        card.setBorder(Theme.cardPadding());
        card.setLayout(new BorderLayout(0, 20));
        card.setPreferredSize(new Dimension(760, 680));

        JPanel heroPanel = new JPanel();
        heroPanel.setOpaque(false);
        heroPanel.setLayout(new BoxLayout(heroPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Lobos de la Aldea");
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Juego por turnos basado en el PDF, ahora con interfaz grafica.");
        subtitleLabel.setFont(Theme.BODY_FONT);
        subtitleLabel.setForeground(Theme.MUTED);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel noteLabel = new JLabel("La partida se juega desde una sola ventana y el sistema controla todos los anuncios oficiales.");
        noteLabel.setFont(Theme.SMALL_FONT);
        noteLabel.setForeground(Theme.MUTED);
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        heroPanel.add(titleLabel);
        heroPanel.add(Box.createVerticalStrut(8));
        heroPanel.add(subtitleLabel);
        heroPanel.add(Box.createVerticalStrut(4));
        heroPanel.add(noteLabel);

        JPanel topGrid = new JPanel(new GridLayout(1, 2, 16, 0));
        topGrid.setOpaque(false);
        topGrid.add(buildPlayerCountCard());
        topGrid.add(buildRolePreviewCard());

        nombresPanel.setOpaque(false);
        nombresPanel.setLayout(new BoxLayout(nombresPanel, BoxLayout.Y_AXIS));

        JScrollPane namesScrollPane = Theme.createScrollPane(nombresPanel);

        AccentButton createButton = new AccentButton("Crear partida", Theme.GOLD);
        createButton.setPreferredSize(new Dimension(210, 44));
        createButton.addActionListener(event -> createGameFromForm());

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(createButton);

        card.add(heroPanel, BorderLayout.NORTH);
        card.add(topGrid, BorderLayout.CENTER);

        RoundedPanel namesCard = new RoundedPanel(Theme.CARD_START, Theme.CARD_END, 28);
        namesCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        namesCard.setLayout(new BorderLayout(0, 12));

        JLabel namesTitle = new JLabel("Mesa de jugadores");
        namesTitle.setFont(Theme.SECTION_FONT);
        namesTitle.setForeground(Theme.TEXT);
        namesCard.add(namesTitle, BorderLayout.NORTH);
        namesCard.add(namesScrollPane, BorderLayout.CENTER);

        JPanel centerBlock = new JPanel(new BorderLayout(0, 16));
        centerBlock.setOpaque(false);
        centerBlock.add(topGrid, BorderLayout.NORTH);
        centerBlock.add(namesCard, BorderLayout.CENTER);

        card.add(centerBlock, BorderLayout.CENTER);
        card.add(buttonRow, BorderLayout.SOUTH);

        wrapper.add(card);
        return wrapper;
    }

    private JComponent buildRevealScreen() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        RoundedPanel card = new RoundedPanel(Theme.CARD_ACCENT, Theme.CARD_END, 36);
        card.setBorder(BorderFactory.createEmptyBorder(28, 30, 28, 30));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(640, 540));

        revealStepLabel.setFont(Theme.SMALL_FONT);
        revealStepLabel.setForeground(Theme.GOLD);
        revealStepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        revealNameLabel.setFont(Theme.SECTION_FONT);
        revealNameLabel.setForeground(Theme.TEXT);
        revealNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        revealRoleLabel.setFont(Theme.BIG_ROLE_FONT);
        revealRoleLabel.setForeground(Theme.MUTED);
        revealRoleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        revealRoleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        Theme.styleTextArea(revealDescriptionArea, true);
        revealDescriptionArea.setForeground(Theme.MUTED);
        revealDescriptionArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        revealDescriptionArea.setMaximumSize(new Dimension(520, 130));

        revealActionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        revealActionButton.setPreferredSize(new Dimension(250, 48));
        revealActionButton.setMaximumSize(new Dimension(250, 48));
        revealActionButton.addActionListener(event -> handleRevealAction());

        JLabel privacyLabel = new JLabel("Pasa el dispositivo al siguiente jugador antes de pulsar.");
        privacyLabel.setFont(Theme.SMALL_FONT);
        privacyLabel.setForeground(Theme.MUTED);
        privacyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(18));
        card.add(revealStepLabel);
        card.add(Box.createVerticalStrut(24));
        card.add(revealNameLabel);
        card.add(Box.createVerticalStrut(32));
        card.add(revealRoleLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(revealDescriptionArea);
        card.add(Box.createVerticalGlue());
        card.add(privacyLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(revealActionButton);

        wrapper.add(card);
        return wrapper;
    }

    private JComponent buildGameScreen() {
        JPanel gamePanel = new JPanel(new BorderLayout(18, 18));
        gamePanel.setOpaque(false);

        gamePanel.add(buildHeaderPanel(), BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new BorderLayout(18, 18));
        bodyPanel.setOpaque(false);
        bodyPanel.add(buildPlayersPanel(), BorderLayout.WEST);
        bodyPanel.add(buildLogPanel(), BorderLayout.CENTER);
        bodyPanel.add(buildActionPanel(), BorderLayout.EAST);

        gamePanel.add(bodyPanel, BorderLayout.CENTER);
        return gamePanel;
    }

    private JComponent buildHeaderPanel() {
        RoundedPanel header = new RoundedPanel(Theme.CARD_START, Theme.CARD_SOFT, 28);
        header.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        header.setLayout(new BorderLayout(16, 0));

        headerTitleLabel.setFont(new java.awt.Font("Georgia", java.awt.Font.BOLD, 28));
        headerTitleLabel.setForeground(Theme.TEXT);

        headerStateLabel.setFont(Theme.SECTION_FONT);
        headerStateLabel.setForeground(Theme.GOLD);

        headerInfoLabel.setFont(Theme.BODY_FONT);
        headerInfoLabel.setForeground(Theme.MUTED);

        headerWinnerLabel.setFont(Theme.BODY_FONT);
        headerWinnerLabel.setForeground(Theme.GREEN);
        headerWinnerLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel leftBlock = new JPanel();
        leftBlock.setOpaque(false);
        leftBlock.setLayout(new BoxLayout(leftBlock, BoxLayout.Y_AXIS));
        leftBlock.add(headerTitleLabel);
        leftBlock.add(Box.createVerticalStrut(6));
        leftBlock.add(headerStateLabel);
        leftBlock.add(Box.createVerticalStrut(4));
        leftBlock.add(headerInfoLabel);

        header.add(leftBlock, BorderLayout.WEST);
        header.add(headerWinnerLabel, BorderLayout.EAST);
        return header;
    }

    private JComponent buildPlayersPanel() {
        RoundedPanel panel = new RoundedPanel(Theme.CARD_START, Theme.CARD_END, 28);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panel.setLayout(new BorderLayout(0, 14));
        panel.setPreferredSize(new Dimension(290, 100));

        JLabel title = new JLabel("Aldea");
        title.setFont(Theme.SECTION_FONT);
        title.setForeground(Theme.TEXT);

        JLabel subtitle = new JLabel("Solo se revelan roles al morir o al final.");
        subtitle.setFont(Theme.SMALL_FONT);
        subtitle.setForeground(Theme.MUTED);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(title);
        top.add(Box.createVerticalStrut(4));
        top.add(subtitle);

        playersListPanel.setOpaque(false);
        playersListPanel.setLayout(new BoxLayout(playersListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = Theme.createScrollPane(playersListPanel);
        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildLogPanel() {
        RoundedPanel panel = new RoundedPanel(Theme.CARD_SOFT, Theme.CARD_END, 28);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panel.setLayout(new BorderLayout(0, 14));

        JLabel title = new JLabel("Cronica oficial");
        title.setFont(Theme.SECTION_FONT);
        title.setForeground(Theme.TEXT);

        JLabel subtitle = new JLabel("Este registro es publico y sigue las reglas del PDF.");
        subtitle.setFont(Theme.SMALL_FONT);
        subtitle.setForeground(Theme.MUTED);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(title);
        top.add(Box.createVerticalStrut(4));
        top.add(subtitle);

        Theme.styleTextArea(logArea, false);
        logArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = Theme.createScrollPane(logArea);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildActionPanel() {
        RoundedPanel panel = new RoundedPanel(Theme.CARD_ACCENT, Theme.CARD_END, 28);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panel.setLayout(new BorderLayout(0, 14));
        panel.setPreferredSize(new Dimension(330, 100));

        actionTitleLabel.setFont(Theme.SECTION_FONT);
        actionTitleLabel.setForeground(Theme.TEXT);

        actionActorLabel.setFont(Theme.SMALL_FONT);
        actionActorLabel.setForeground(Theme.GOLD);

        Theme.styleTextArea(actionDescriptionArea, false);
        actionDescriptionArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        primaryActionButton.setPreferredSize(new Dimension(240, 44));
        primaryActionButton.addActionListener(event -> handlePrimaryAction());

        secondaryActionButton.setPreferredSize(new Dimension(240, 40));
        secondaryActionButton.addActionListener(event -> returnToConfig());

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(actionTitleLabel);
        top.add(Box.createVerticalStrut(4));
        top.add(actionActorLabel);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        primaryActionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        secondaryActionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttons.add(primaryActionButton);
        buttons.add(Box.createVerticalStrut(10));
        buttons.add(secondaryActionButton);

        panel.add(top, BorderLayout.NORTH);
        panel.add(Theme.createScrollPane(actionDescriptionArea), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JComponent buildPlayerCountCard() {
        RoundedPanel card = new RoundedPanel(Theme.CARD_START, Theme.CARD_END, 24);
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Numero de jugadores");
        label.setFont(Theme.SECTION_FONT);
        label.setForeground(Theme.TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel help = new JLabel("El PDF exige un minimo de 4 jugadores.");
        help.setFont(Theme.SMALL_FONT);
        help.setForeground(Theme.MUTED);
        help.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) jugadoresSpinner.getEditor();
        Theme.styleTextField(editor.getTextField());
        jugadoresSpinner.setMaximumSize(new Dimension(180, 42));
        jugadoresSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        jugadoresSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                rebuildPlayerFields((Integer) jugadoresSpinner.getValue());
            }
        });

        card.add(label);
        card.add(Box.createVerticalStrut(4));
        card.add(help);
        card.add(Box.createVerticalStrut(18));
        card.add(jugadoresSpinner);
        card.add(Box.createVerticalGlue());
        return card;
    }

    private JComponent buildRolePreviewCard() {
        RoundedPanel card = new RoundedPanel(Theme.CARD_START, Theme.CARD_END, 24);
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.setLayout(new BorderLayout(0, 12));

        JLabel title = new JLabel("Reparto previsto");
        title.setFont(Theme.SECTION_FONT);
        title.setForeground(Theme.TEXT);

        repartoLabel.setFont(Theme.BODY_FONT);
        repartoLabel.setForeground(Theme.MUTED);
        repartoLabel.setVerticalAlignment(SwingConstants.TOP);

        card.add(title, BorderLayout.NORTH);
        card.add(repartoLabel, BorderLayout.CENTER);
        return card;
    }

    private void rebuildPlayerFields(int totalPlayers) {
        camposJugadores.clear();
        nombresPanel.removeAll();

        for (int i = 0; i < totalPlayers; i++) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            row.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

            JLabel label = new JLabel("Jugador " + (i + 1));
            label.setFont(Theme.BODY_FONT);
            label.setForeground(Theme.TEXT);
            label.setPreferredSize(new Dimension(110, 40));

            JTextField field = new JTextField("Jugador " + (i + 1));
            Theme.styleTextField(field);

            camposJugadores.add(field);
            row.add(label, BorderLayout.WEST);
            row.add(field, BorderLayout.CENTER);
            nombresPanel.add(row);
        }

        repartoLabel.setText(buildRolePreviewHtml(totalPlayers));
        nombresPanel.revalidate();
        nombresPanel.repaint();
    }

    private String buildRolePreviewHtml(int totalPlayers) {
        List<String> reparto = JuegoJuego.describirReparto(totalPlayers);
        StringBuilder html = new StringBuilder("<html><div style='font-family:Segoe UI; color:#ACB8CC;'>");
        html.append("El reparto sigue los roles obligatorios y las opciones del PDF.<br><br>");
        for (String rol : reparto) {
            html.append("- ").append(rol).append("<br>");
        }
        html.append("</div></html>");
        return html.toString();
    }

    private void createGameFromForm() {
        List<String> nombres = new ArrayList<>();
        for (JTextField campo : camposJugadores) {
            nombres.add(campo.getText());
        }

        try {
            juego.prepararPartida(nombres);
            rolVisible = false;
            refreshRevealScreen();
            screens.show(screenPanel, CARD_REVEAL);
        } catch (IllegalArgumentException exception) {
            showMessageDialog("No se puede crear la partida", exception.getMessage(), Theme.ROSE);
        }
    }

    private void handleRevealAction() {
        if (juego.getEstado() == EstadoJuego.LISTA_PARA_INICIAR) {
            juego.iniciarJuego();
            refreshGameScreen();
            screens.show(screenPanel, CARD_GAME);
            return;
        }

        if (!rolVisible) {
            rolVisible = true;
        } else {
            rolVisible = false;
            juego.avanzarRevelacion();
        }

        refreshRevealScreen();
    }

    private void refreshRevealScreen() {
        if (juego.getEstado() == EstadoJuego.LISTA_PARA_INICIAR) {
            revealStepLabel.setText("Todos los roles han sido repartidos");
            revealNameLabel.setText("La aldea esta preparada");
            revealRoleLabel.setText("Comenzar partida");
            revealRoleLabel.setForeground(Theme.GOLD);
            revealDescriptionArea.setText(
                    "La primera ronda empieza de noche. Durante la partida, la interfaz publica no mostrara roles de jugadores vivos.");
            revealActionButton.setText("Entrar en la noche 1");
            return;
        }

        Jugador jugador = juego.getJugadorPendienteDeRevelar();
        if (jugador == null) {
            revealStepLabel.setText("Sin jugadores");
            revealNameLabel.setText("");
            revealRoleLabel.setText("");
            revealDescriptionArea.setText("");
            revealActionButton.setText("Volver");
            return;
        }

        int indice = juego.getJugadores().indexOf(jugador) + 1;
        revealStepLabel.setText("Jugador " + indice + " de " + juego.getJugadores().size());
        revealNameLabel.setText(jugador.getNombre());

        if (!rolVisible) {
            revealRoleLabel.setText("Rol oculto");
            revealRoleLabel.setForeground(Theme.MUTED);
            revealDescriptionArea.setText(
                    "Solo " + jugador.getNombre() + " debe mirar esta pantalla. Cuando termine, pulsa para revelar su carta.");
            revealActionButton.setText("Mostrar mi rol");
        } else {
            revealRoleLabel.setText(jugador.getRol().getNombreRol());
            revealRoleLabel.setForeground(getRoleColor(jugador));
            revealDescriptionArea.setText(jugador.getRol().getDescripcion());
            revealActionButton.setText("Ocultar y pasar");
        }
    }

    private void handlePrimaryAction() {
        try {
            switch (juego.getEstado()) {
                case NOCHE_LOBOS, NOCHE_VIDENTE, NOCHE_BRUJA_CURA, NOCHE_BRUJA_VENENO -> handlePrivateNightAction();
                case DIA_RESOLUCION, DIA_RESULTADO -> {
                    juego.avanzarJuegoPublico();
                    refreshGameScreen();
                }
                case DIA_VOTACION -> handleVoteAction();
                case FIN -> returnToConfig();
                default -> { }
            }
        } catch (RuntimeException exception) {
            showMessageDialog("Accion no valida", exception.getMessage(), Theme.ROSE);
        }
    }

    private void handlePrivateNightAction() {
        SelectionResult result = openSelectionDialog(
                juego.getEtiquetaPrivadaActual(),
                juego.getTituloDialogoPrivado(),
                juego.getDescripcionDialogoPrivado(),
                juego.getObjetivosDisponiblesActuales(),
                juego.accionActualEsOpcional(),
                getConfirmLabel());

        if (!result.confirmed) {
            return;
        }

        String message = juego.resolverAccionNocturnaActual(result.target);
        if (!message.isBlank()) {
            showMessageDialog("Accion registrada", message, Theme.TEAL);
        }
        refreshGameScreen();
    }

    private void handleVoteAction() {
        Jugador votante = juego.getVotanteActual();
        if (votante == null) {
            return;
        }

        SelectionResult result = openSelectionDialog(
                votante.getNombre(),
                "Turno de voto",
                "Elige a quien desea acusar " + votante.getNombre() + ".",
                juego.getObjetivosDisponiblesActuales(),
                false,
                "Registrar voto");

        if (!result.confirmed || result.target == null) {
            return;
        }

        juego.registrarVotoActual(result.target);
        refreshGameScreen();
    }

    private String getConfirmLabel() {
        return switch (juego.getEstado()) {
            case NOCHE_LOBOS -> "Marcar victima";
            case NOCHE_VIDENTE -> "Inspeccionar";
            case NOCHE_BRUJA_CURA -> "Usar cura";
            case NOCHE_BRUJA_VENENO -> "Usar veneno";
            default -> "Confirmar";
        };
    }

    private void refreshGameScreen() {
        headerStateLabel.setText(juego.getTituloEstado());
        headerInfoLabel.setText("Ronda " + juego.getRondaActual() + "  |  Jugadores vivos: " + juego.getTotalVivos());
        headerWinnerLabel.setText(juego.getGanador() == null ? "" : "Ganador provisional: " + juego.getGanador());

        refreshPlayersList();
        refreshLogArea();
        refreshActionPanel();
    }

    private void refreshPlayersList() {
        playersListPanel.removeAll();

        for (Jugador jugador : juego.getJugadores()) {
            playersListPanel.add(createPlayerCard(jugador));
            playersListPanel.add(Box.createVerticalStrut(10));
        }

        playersListPanel.revalidate();
        playersListPanel.repaint();
    }

    private JComponent createPlayerCard(Jugador jugador) {
        Color start = jugador.isVivo() ? Theme.CARD_SOFT : new Color(57, 62, 79, 230);
        Color end = jugador.isVivo() ? Theme.CARD_END : new Color(39, 43, 56, 235);

        RoundedPanel card = new RoundedPanel(start, end, 22);
        card.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(jugador.getNombre());
        name.setFont(Theme.SECTION_FONT);
        name.setForeground(Theme.TEXT);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel status = new JLabel(jugador.isVivo() ? "Vivo" : "Eliminado");
        status.setFont(Theme.SMALL_FONT);
        status.setForeground(jugador.isVivo() ? Theme.GREEN : Theme.DEAD);
        status.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel role = new JLabel();
        role.setFont(Theme.BODY_FONT);
        role.setAlignmentX(Component.LEFT_ALIGNMENT);

        boolean revealRole = juego.getEstado() == EstadoJuego.FIN || !jugador.isVivo();
        if (revealRole) {
            role.setText(jugador.getRol().getNombreRol());
            role.setForeground(getRoleColor(jugador));
        } else {
            role.setText("Rol oculto");
            role.setForeground(Theme.MUTED);
        }

        card.add(name);
        card.add(Box.createVerticalStrut(4));
        card.add(status);
        card.add(Box.createVerticalStrut(10));
        card.add(role);
        return card;
    }

    private void refreshLogArea() {
        if (juego.getRegistroPublico().isEmpty()) {
            logArea.setText("Todavia no hay anuncios oficiales.");
        } else {
            logArea.setText(String.join("\n\n", juego.getRegistroPublico()));
        }
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void refreshActionPanel() {
        EstadoJuego estado = juego.getEstado();
        actionTitleLabel.setText(juego.getTituloEstado());

        switch (estado) {
            case CONFIGURACION -> {
                actionActorLabel.setText("Preparacion");
                actionDescriptionArea.setText("Crea una partida desde la pantalla inicial.");
                primaryActionButton.setEnabled(false);
                primaryActionButton.setText("Sin partida");
                secondaryActionButton.setVisible(true);
                secondaryActionButton.setText("Volver al inicio");
            }
            case REVELANDO_ROLES, LISTA_PARA_INICIAR -> {
                actionActorLabel.setText("Reparto privado");
                actionDescriptionArea.setText("La partida se esta preparando en la pantalla de roles.");
                primaryActionButton.setEnabled(false);
                primaryActionButton.setText("Pendiente");
                secondaryActionButton.setVisible(true);
                secondaryActionButton.setText("Volver al inicio");
            }
            case NOCHE_LOBOS, NOCHE_VIDENTE, NOCHE_BRUJA_CURA, NOCHE_BRUJA_VENENO -> {
                actionActorLabel.setText("Accion privada para " + juego.getEtiquetaPrivadaActual());
                actionDescriptionArea.setText(juego.getDescripcionEstado() + "\n\n" + juego.getDescripcionDialogoPrivado());
                primaryActionButton.setEnabled(true);
                primaryActionButton.setText("Abrir accion privada");
                secondaryActionButton.setVisible(true);
                secondaryActionButton.setText("Volver al inicio");
            }
            case DIA_RESOLUCION -> {
                actionActorLabel.setText("Amanecer");
                actionDescriptionArea.setText(juego.getDescripcionEstado());
                primaryActionButton.setEnabled(true);
                primaryActionButton.setText(juego.getGanador() == null ? "Abrir votacion" : "Ver final");
                secondaryActionButton.setVisible(true);
                secondaryActionButton.setText("Volver al inicio");
            }
            case DIA_VOTACION -> {
                actionActorLabel.setText("Turno actual: " + juego.getEtiquetaPrivadaActual());
                actionDescriptionArea.setText(
                        "Quedan " + juego.getVotosPendientes() + " votos por emitir.\n\nEl voto se registra sin mostrar roles.");
                primaryActionButton.setEnabled(true);
                primaryActionButton.setText("Registrar voto");
                secondaryActionButton.setVisible(true);
                secondaryActionButton.setText("Volver al inicio");
            }
            case DIA_RESULTADO -> {
                actionActorLabel.setText("Ronda cerrada");
                actionDescriptionArea.setText(juego.getDescripcionEstado());
                primaryActionButton.setEnabled(true);
                primaryActionButton.setText("Siguiente noche");
                secondaryActionButton.setVisible(true);
                secondaryActionButton.setText("Volver al inicio");
            }
            case FIN -> {
                actionActorLabel.setText(juego.getGanador() == null ? "Partida terminada" : "Vence el bando " + juego.getGanador());
                actionDescriptionArea.setText(juego.getResumenPartida());
                primaryActionButton.setEnabled(true);
                primaryActionButton.setText("Nueva partida");
                secondaryActionButton.setVisible(false);
            }
        }
    }

    private SelectionResult openSelectionDialog(
            String actor,
            String title,
            String description,
            List<Jugador> targets,
            boolean optional,
            String confirmLabel) {

        JDialog dialog = new JDialog(this, title, true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        RoundedPanel panel = new RoundedPanel(Theme.CARD_ACCENT, Theme.CARD_END, 28);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new BorderLayout(0, 16));

        JLabel actorLabel = new JLabel("Pasa el dispositivo a " + actor);
        actorLabel.setFont(Theme.SECTION_FONT);
        actorLabel.setForeground(Theme.GOLD);

        JTextArea descriptionArea = new JTextArea(description);
        Theme.styleTextArea(descriptionArea, true);
        descriptionArea.setForeground(Theme.TEXT);

        JComboBox<Jugador> comboBox = new JComboBox<>(targets.toArray(new Jugador[0]));
        comboBox.setFont(Theme.BODY_FONT);
        comboBox.setForeground(Theme.TEXT);
        comboBox.setBackground(Theme.INPUT);
        comboBox.setBorder(Theme.fieldBorder());
        comboBox.setEnabled(!targets.isEmpty());

        AccentButton confirmButton = new AccentButton(confirmLabel, Theme.GOLD);
        AccentButton cancelButton = new AccentButton(optional ? "Omitir" : "Cancelar", Theme.CARD_ACCENT);

        final boolean[] confirmed = {false};
        final Jugador[] selectedTarget = {null};

        confirmButton.setEnabled(optional || !targets.isEmpty());
        confirmButton.addActionListener(event -> {
            confirmed[0] = true;
            selectedTarget[0] = (Jugador) comboBox.getSelectedItem();
            dialog.dispose();
        });

        cancelButton.addActionListener(event -> {
            if (optional) {
                confirmed[0] = true;
            }
            dialog.dispose();
        });

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(actorLabel);
        center.add(Box.createVerticalStrut(12));
        center.add(descriptionArea);
        center.add(Box.createVerticalStrut(16));
        if (!targets.isEmpty()) {
            center.add(comboBox);
        } else {
            JLabel emptyLabel = new JLabel(optional
                    ? "No hay objetivos validos. Puedes continuar."
                    : "No hay objetivos disponibles.");
            emptyLabel.setFont(Theme.BODY_FONT);
            emptyLabel.setForeground(Theme.MUTED);
            center.add(emptyLabel);
        }

        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0));
        actions.setOpaque(false);
        actions.add(cancelButton);
        actions.add(confirmButton);

        panel.add(center, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth(), 470), dialog.getHeight());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return new SelectionResult(confirmed[0], selectedTarget[0]);
    }

    private void showMessageDialog(String title, String message, Color accentColor) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        RoundedPanel panel = new RoundedPanel(Theme.CARD_SOFT, Theme.CARD_END, 28);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new BorderLayout(0, 14));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.SECTION_FONT);
        titleLabel.setForeground(accentColor);

        JTextArea messageArea = new JTextArea(message);
        Theme.styleTextArea(messageArea, true);
        messageArea.setForeground(Theme.TEXT);

        AccentButton closeButton = new AccentButton("Cerrar", Theme.GOLD);
        closeButton.addActionListener(event -> dialog.dispose());

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(closeButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(messageArea, BorderLayout.CENTER);
        panel.add(buttonRow, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth(), 430), Math.max(dialog.getHeight(), 220));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void returnToConfig() {
        rolVisible = false;
        screens.show(screenPanel, CARD_CONFIG);
    }

    private Color getRoleColor(Jugador jugador) {
        if (jugador.getRol() instanceof Lobo) {
            return Theme.ROSE;
        }
        if (jugador.getRol() instanceof Vidente) {
            return Theme.TEAL;
        }
        if (jugador.getRol() instanceof Bruja) {
            return Theme.GOLD;
        }
        if (jugador.getRol() instanceof Cazador) {
            return Theme.GREEN;
        }
        if (jugador.getRol() instanceof Aldeano) {
            return Theme.TEXT;
        }
        return Theme.TEXT;
    }

    private static final class SelectionResult {
        private final boolean confirmed;
        private final Jugador target;

        private SelectionResult(boolean confirmed, Jugador target) {
            this.confirmed = confirmed;
            this.target = target;
        }
    }
}
