import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class gui {

    private JPanel padreGrafica;
    private JPanel grafica;

    private JPanel panelOpciones;
    private JFrame principal;

    private volatile ca1DSim simulador;

    private final Color[][] colores = new Color[][] {
            {Color.white, Color.black},
            {Color.white, Color.lightGray, Color.black},
            {Color.red, Color.green, Color.blue, Color.yellow},
            {Color.red, Color.green, Color.blue, Color.yellow, Color.orange},
    };


    public static ActionListener buttonListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            JFrame frame = new JFrame("");
            frame.setLocationRelativeTo(null);

            JComponent item = (JComponent) actionEvent.getSource();

            JPanel panel = new JPanel(){

                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(250, 300);
                }
            };
            panel.setLayout(new BorderLayout());

            JLabel label = new JLabel(item.getName());
            label.setHorizontalAlignment(SwingConstants.CENTER);

            panel.add(label, BorderLayout.CENTER);
            frame.add(panel, BorderLayout.CENTER);

            frame.setMinimumSize(new Dimension(250, 300));
            frame.setResizable(false);
            frame.setVisible(true);
        }
    };

    public void crearVentana() throws IOException {

        int anchoFrame = 900;
        int altoFrame = 700;

        principal = new JFrame("GUI");

        // Añadimos el menu al frame
        //crearMenu(frame, buttonListener);

        // Creamos el grid principal del frame
        BorderLayout borderLayout = new BorderLayout(1, 4);
        principal.getContentPane().setLayout(borderLayout);

        // Panel derecho con los desplegables
        panelLateralDerecho();

        // Panel izquierdo con los puntos
        panelLateralIzquierdo();

        principal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        principal.setMinimumSize(new Dimension(anchoFrame, altoFrame));
        principal.setResizable(true);
        principal.setLocationRelativeTo(null);
        principal.pack();
        principal.setVisible(true);
    }

    private void panelLateralDerecho() {

        String[] opcionesInicializacion = new String[]{"Aleatoria", "Célula Central Activa"};
        String[] opcionesEstados = new String[]{"2", "3", "4", "5"};

        panelOpciones = new JPanel();
        panelOpciones.setBorder(new EmptyBorder(0, 40, 0, 40));
        //panelBotones.setBackground(Color.RED);
        panelOpciones.setMinimumSize(new Dimension(350, 700));
        panelOpciones.setPreferredSize(new Dimension(350, 700));
        panelOpciones.setMaximumSize(new Dimension(350, 700));

        GridLayout gridBotones = new GridLayout(16, 3);
        panelOpciones.setLayout(gridBotones);

        // Configuracion inicial
        JLabel textoInicializacion = new JLabel("Configuracion inicial:");
        textoInicializacion.setBorder(new EmptyBorder(20, 0, 0, 0));
        JComboBox<String> listadoInicializacion = new JComboBox<>(opcionesInicializacion);

        // Nº estados por celula
        JLabel textoEstados = new JLabel("Nº de estados por celula:");
        textoInicializacion.setBorder(new EmptyBorder(20, 0, 0, 0));
        JComboBox<String> listadoNumEstados = new JComboBox<>(opcionesEstados);

        // Nº generaciones
        JLabel textoGeneraciones = new JLabel("Generaciones:");
        textoInicializacion.setBorder(new EmptyBorder(20, 0, 0, 0));
        JSpinner spinnerGeneraciones = new JSpinner();
        spinnerGeneraciones.setValue(600);
        spinnerGeneraciones.setPreferredSize(new Dimension(spinnerGeneraciones.getPreferredSize().width, 20));


        // Etiqueta del selector de cantidad
        JLabel textoRegla = new JLabel("Regla (base 10):");
        textoRegla.setBorder(new CompoundBorder(textoRegla.getBorder(), new EmptyBorder(20, 0, 0, 0)));
        JSpinner spinnerRegla = new JSpinner();
        spinnerRegla.setValue(700);
        spinnerRegla.setPreferredSize(new Dimension(spinnerRegla.getPreferredSize().width, 20));

        // Nos suscribimos a cambios en el JSpinner
        ChangeListener spinnersListeners = changeEvent -> {
            // Evitamos que se puedan introducir números negativos
            if ((int) spinnerRegla.getValue() < 0){
                spinnerRegla.setValue(0);
            }
        };


        // Añadimos el listener a los spinners
        spinnerRegla.addChangeListener(spinnersListeners);
        spinnerGeneraciones.addChangeListener(spinnersListeners);

        JButton botonGenerar = new JButton("Simular");
        botonGenerar.addActionListener(actionEvent -> {

            // Configuracion inicial
            ca1DSim.CONFIGURACION_INICIAL confInicial = listadoInicializacion.getSelectedIndex() == 0 ? ca1DSim.CONFIGURACION_INICIAL.ALEATORIA : ca1DSim.CONFIGURACION_INICIAL.CELULA_CENTRAL_ACTIVA;

            // Estados por celula
            int k = Integer.parseInt((String) Objects.requireNonNull(listadoNumEstados.getSelectedItem()));

            // Num generaciones
            int generaciones = (int) spinnerGeneraciones.getValue();

            // Regla/codigo
            int regla = (int) spinnerRegla.getValue();

            // Ejecutamos la simulacion
            realizarSimulacion(confInicial, k, generaciones, regla);
        });


        panelOpciones.add(new JLabel(""));
        panelOpciones.add(textoInicializacion);         // Configuracion inicial
        panelOpciones.add(listadoInicializacion);
        panelOpciones.add(textoEstados);                // Numero de estados por celula
        panelOpciones.add(listadoNumEstados);
        panelOpciones.add(textoGeneraciones);           // Generaciones
        panelOpciones.add(spinnerGeneraciones);
        panelOpciones.add(textoRegla);                  // Regla
        panelOpciones.add(spinnerRegla);
        panelOpciones.add(new JLabel(""));
        panelOpciones.add(botonGenerar);                // Boton simular

        principal.add(panelOpciones, BorderLayout.EAST);
    }

    private void panelLateralIzquierdo() throws IOException {

        padreGrafica = new JPanel();
        padreGrafica.setLayout(new GridBagLayout());
        padreGrafica.setMinimumSize(new Dimension(600, 800));
        padreGrafica.setPreferredSize(new Dimension(600, 800));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.insets = new Insets(0,0,0,0);

        // Panel que mostrará los puntos
        JPanel panelGrafica = new JPanel();
        panelGrafica.setLayout(new GridLayout(1,1));
        panelGrafica.setBackground(Color.BLUE);

        // Restricciones del panel de la grafica
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;


        // Añadimos la grafica al panel
        anadirGrafica(panelGrafica);

        // Añadimos el panel de la grafica
        padreGrafica.add(panelGrafica, gbc);

        // Lo añadimos al frame principal
        principal.add(padreGrafica, BorderLayout.CENTER);
    }

    private void anadirGrafica(JPanel panelGrafica){

        grafica = new JPanel(){

            @Override
            public void paint(Graphics graphics) {

                // Comprobamos que haya objeto simulador
                if (simulador != null){

                    int anchoPanel = panelGrafica.getWidth();
                    int altoPanel = panelGrafica.getHeight();

                    int nCelulas = simulador.getnCelulas();
                    int nGeneraciones = simulador.getnGeneraciones();

                    int anchoCelula = anchoPanel / nCelulas;
                    int altoCelula = altoPanel / nGeneraciones;
                    int k = (int) simulador.getK();

                    BufferedImage bufferedImage = new BufferedImage(anchoPanel,altoPanel,BufferedImage.TYPE_INT_RGB);
                    Graphics g = bufferedImage.getGraphics();

                    // Pintamos un marco blanco como fondo
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, anchoPanel, altoPanel);

                    // Pinta una sola generacion
                    Runnable pintaGeneracion = () -> {

                        int[] celulasGenActual = simulador.getGeneracionActual();
                        int nGenActual = simulador.getNGeneracionActual();
                        int filaColores = k - 2;

                        // Pintamos una generacion/fila
                        for (int i = 0; i < nCelulas; i++) {
                            int valor = celulasGenActual[i];

                            // Establecemos el color que le toca
                            g.setColor(colores[filaColores][valor]);

                            // Comienzo de dibujo del cuadrado
                            int x = anchoCelula * i;
                            int y = altoCelula * nGenActual;

                            // Pintamos el cuadrado
                            g.fillRect(x, y, altoCelula, anchoCelula);
                        }
                    };

                    // Pintamos todas las generaciones
                    while (!simulador.haTerminado()){
                        simulador.evoluciona();
                        pintaGeneracion.run();
                    }

                    graphics.drawImage(bufferedImage, 0, 0, this);
                }
            }
        };
        grafica.setBackground(Color.BLUE);

        // Actualizamos el tamañoe
        panelGrafica.add(grafica);
    }

    private void realizarSimulacion(ca1DSim.CONFIGURACION_INICIAL confInicial, int k, int generaciones, int regla){

        try {
            simulador = new ca1DSim(k, regla, generaciones, confInicial);

            // Ajustamos el tamaño del frame
            //redimensionarFrame();

            // Repintamos la gráfica
            grafica.invalidate();
            grafica.repaint();

        }catch (Exception e){
            JOptionPane.showMessageDialog(principal, "Ocurrio un error al ejecutar la simulacion:\n'" + e.getMessage() + "'", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*private void crearMenu(JFrame frame, ActionListener listener) {

        JMenu opA, opB, opC, opAcerca;
        JMenuItem subA, subB, subC, subAcerca;

        JMenuBar mb = new JMenuBar();

        opA = new JMenu("Opcion A");
        subA = new JMenuItem("Opción 1");
        subA.setName("Opción 1");
        subA.addActionListener(listener);
        opA.add(subA);

        opB = new JMenu("Opcion B");
        subB = new JMenuItem("Opción 2");
        subB.setName("Opción 2");
        subB.addActionListener(listener);
        opB.add(subB);

        opC = new JMenu("Opcion C");
        subC = new JMenuItem("Opción 3");
        subC.setName("Opción 3");
        subC.addActionListener(listener);
        opC.add(subC);

        opAcerca = new JMenu("Acerca de");
        subAcerca = new JMenuItem("Opción 4");
        subAcerca.setName("Opción 4");
        subAcerca.addActionListener(listener);
        opAcerca.add(subAcerca);

        mb.add(opA);
        mb.add(opB);
        mb.add(opC);
        mb.add(opAcerca);

        frame.setJMenuBar(mb);
    }*/
}
