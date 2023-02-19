import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Random;

public class randomGenerator {

    public enum ALGORITMOS {
        _26_1_a,
        _26_1_b,
        _26_2,
        _26_3,
        _26_42,
        FISHMAN_MOORE,
        RANDU
    };

    private main.ALGORITMOS algoritmo = main.ALGORITMOS._26_1_a;
    private JPanel grafica = null;      // Gráfica con los puntos
    private AbstractMap.SimpleEntry[] puntos;

    private int numeroPuntos = 1000;

    private long xGenerador26_1_a = 1;   // x_n-1 del algoritmo 26.1a
    private long xGenerador26_1_b = 1;   // x_n-1 del algoritmo 26.1b
    private long xGenerador26_2 = 1;     // x_n-1 del algoritmo 26.2

    private long xGenerador26_3 = 1;     // x_n-1 del algoritmo 26.3


    //  Variables necesarias para el generador de las diapositivas 26_42
    private long wGenerador26_42 = 1;
    private long xGenerador26_42 = 1;
    private long yGenerador26_42 = 1;

    private long xGenerador_FishmanMoore = 1;

    private long xGenerador_Randu = 1;


    public randomGenerator(){}

    public randomGenerator(long seed){
        xGenerador26_1_a = seed;
        xGenerador26_1_b = seed;
        xGenerador26_2 = seed;
        xGenerador26_3 = seed;

        xGenerador26_42 = seed;
        wGenerador26_42 = seed;
        yGenerador26_42 = seed;

        xGenerador_FishmanMoore = seed;
        xGenerador_Randu = seed;
    }


    public double generador26_1_a(){
        xGenerador26_1_a = 5*xGenerador26_1_a % ((long) Math.pow(2, 5));
        return xGenerador26_1_a / Math.pow(2, 5);
    }

    public double generador26_1_b(){
        xGenerador26_1_b = 7*xGenerador26_1_b % ((long) Math.pow(2, 5));
        return xGenerador26_1_b / Math.pow(2, 5);
    }

    public double generador26_2(){
        xGenerador26_2 = 3*xGenerador26_2 % 31L;
        return xGenerador26_2 / 31.0;
    }

    public double generador26_3(){
        xGenerador26_3 = ((long) Math.pow(7, 5)) * xGenerador26_3 % (((long) Math.pow(2, 31)) - 1);
        return xGenerador26_3 / (Math.pow(2, 31) - 1);
    }

    public double generador26_42(){
        wGenerador26_42 = 157 * wGenerador26_42 % 32363;
        xGenerador26_42 = 146 * xGenerador26_42 % 31727;
        yGenerador26_42 = 142 * yGenerador26_42 % 31657;

        return Math.abs(wGenerador26_42 - xGenerador26_42 + yGenerador26_42) % 32362.0 / 32362.0;
    }

    public double generadorFishmanMoore(){
        xGenerador_FishmanMoore = 69621 * xGenerador_FishmanMoore % (((long) Math.pow(2, 31)) - 1);
        return xGenerador_FishmanMoore / (Math.pow(2, 31) - 1);
    }

    public double generadorRandu(){
        xGenerador_Randu = (((long) Math.pow(2, 16)) + 3) * xGenerador_Randu % ((long) Math.pow(2, 31));
        return xGenerador_Randu / Math.pow(2, 31);
    }

    public double generar(){
        switch (algoritmo){

            case _26_1_a: {
                return generador26_1_a();
            }

            case _26_1_b: {
                return generador26_1_b();
            }

            case _26_2: {
                return generador26_2();
            }

            case _26_3: {
                return generador26_3();
            }

            case _26_42: {
                return generador26_42();
            }

            case FISHMAN_MOORE: {
                return generadorFishmanMoore();
            }

            case RANDU: {
                return generadorRandu();
            }
        }

        return 0.0;
    }

    AbstractMap.SimpleEntry<Integer, Integer> generarPunto(int ancho, int alto){

        int x = (int) (generar() * ancho);
        int y = (int) (generar() * alto);

        return new AbstractMap.SimpleEntry<>(x, y);
    }

    public void estableceAlgoritmo (String s){

        switch (s){

            case "26.1a":
                algoritmo = main.ALGORITMOS._26_1_a;
                break;

            case "26.1b":
                algoritmo = main.ALGORITMOS._26_1_b;
                break;

            case "26.2":
                algoritmo = main.ALGORITMOS._26_2;
                break;

            case "26.3":
                algoritmo = main.ALGORITMOS._26_3;
                break;

            case "Combinado 26-42":
                algoritmo = main.ALGORITMOS._26_42;
                break;

            case "Fishman y Moore":
                algoritmo = main.ALGORITMOS.FISHMAN_MOORE;
                break;

            case "RANDU":
                algoritmo = main.ALGORITMOS.RANDU;
                break;
        }
    }




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

        JFrame frame = new JFrame("GUI");

        // Añadimos el menu al frame
        crearMenu(frame, buttonListener);

        // Creamos el grid principal del frame
        BorderLayout borderLayout = new BorderLayout(1, 4);
        frame.getContentPane().setLayout(borderLayout);

        // Panel derecho con los desplegables
        panelLateralDerecho(frame);

        // Panel izquierdo con los puntos
        panelLateralIzquierdo(frame);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(anchoFrame, altoFrame));
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }

    public void panelLateralDerecho(JFrame frame) {

        String[] opcionesListado = new String[]{"26.1a", "26.1b", "26.2", "26.3", "Combinado 26-42", "Fishman y Moore", "RANDU"};

        JPanel panelOpciones = new JPanel();
        panelOpciones.setBorder(new EmptyBorder(0, 40, 0, 40));
        //panelBotones.setBackground(Color.RED);
        panelOpciones.setMinimumSize(new Dimension(350, 700));
        panelOpciones.setPreferredSize(new Dimension(350, 700));
        panelOpciones.setMaximumSize(new Dimension(350, 700));

        GridLayout gridBotones = new GridLayout(16, 3);
        panelOpciones.setLayout(gridBotones);

        // Etiqueta del listado
        JLabel textoGenerador = new JLabel("Generador de numeros aleatorios:");
        //textoGenerador.setName("Parámetros");
        //textoGenerador.addActionListener(buttonListener);

        // Listado de seleccion
        JComboBox<String> listadoGeneradores = new JComboBox<>(opcionesListado);

        // Etiqueta del selector de cantidad
        JLabel textoCantidad = new JLabel("Numero de digitos aleatorios:");
        textoCantidad.setBorder(new CompoundBorder(textoCantidad.getBorder(), new EmptyBorder(20, 0, 0, 0)));
        //textoGenerador.setName("Parámetros");
        //textoGenerador.addActionListener(buttonListener);

        // Listado de seleccion
        JSpinner spinnerCantidad = new JSpinner();
        spinnerCantidad.setPreferredSize(new Dimension(spinnerCantidad.getPreferredSize().width, 20));
        spinnerCantidad.setValue(1000);    // Valor por defecto

        // Nos suscribimos a cambios en el JSpinner
        spinnerCantidad.addChangeListener(changeEvent -> {
            // EVitamos que se puedan introducir números negativos
            if ((int) spinnerCantidad.getValue() < 0){
                spinnerCantidad.setValue(1000);
            }
        });

        JButton botonGenerar = new JButton("Generar");
        botonGenerar.addActionListener(actionEvent -> {

            // Establecemos el algoritmo
            estableceAlgoritmo(listadoGeneradores.getSelectedItem().toString());

            // Establecemos el número de puntos
            numeroPuntos = (int) spinnerCantidad.getValue();

            // Repintamos la gráfica
            grafica.invalidate();
            grafica.repaint();
        });


        panelOpciones.add(new JLabel(""));
        panelOpciones.add(textoGenerador);       // Label del listado
        panelOpciones.add(listadoGeneradores);   // Listado
        panelOpciones.add(textoCantidad);        // Label del spinner
        panelOpciones.add(spinnerCantidad);      // Spinner
        panelOpciones.add(new JLabel(""));
        panelOpciones.add(botonGenerar);        // Boton generar
        /*panelBotones.add(computar);
        panelBotones.add(new JLabel(""));
        panelBotones.add(new JLabel(""));
        panelBotones.add(detener);*/
        //panelBotones.setBorder(new EmptyBorder(80, 80, 80, 80));

        frame.add(panelOpciones, BorderLayout.EAST);
    }

    public void panelLateralIzquierdo(JFrame frame) throws IOException {

        JPanel panelPadre = new JPanel();
        panelPadre.setBackground(Color.ORANGE);
        panelPadre.setLayout(new GridBagLayout());
        panelPadre.setMinimumSize(new Dimension(600, 700));
        panelPadre.setPreferredSize(new Dimension(600, 700));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Panel que mostrará los puntos
        JPanel panelGrafica = new JPanel();
        panelGrafica.setLayout(new GridLayout(1,1));
        panelGrafica.setBackground(Color.BLUE);

        // Restricciones del panel de la grafica
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.7;


        // Añadimos la grafica al panel
        anadirGrafica(panelGrafica);

        // Añadimos el panel de la grafica
        panelPadre.add(panelGrafica, gbc);


        // Panel que contendrá todos los números
        JPanel panelNumeros = new JPanel();
        panelNumeros.setBackground(Color.RED);

        // Restricciones del panel de la grafica
        gbc.gridy = 1;
        gbc.weighty = 0.3;
        gbc.insets = new Insets(0, 10, 10, 10);

        // Añadimos el panel de los numeros
        panelPadre.add(panelNumeros, gbc);

        // Lo añadimos al frame principal
        frame.add(panelPadre, BorderLayout.CENTER);
    }

    public void anadirGrafica(JPanel panelGrafica){

        grafica = new JPanel(){

            @Override
            public void paint(Graphics graphics) {

                int ancho      = panelGrafica.getWidth();
                int alto       = panelGrafica.getHeight();
                BufferedImage bufferedImage = new BufferedImage(ancho,alto,BufferedImage.TYPE_INT_RGB);
                Graphics g = bufferedImage.getGraphics();

                // Inicializamos el vector de puntos
                puntos = new AbstractMap.SimpleEntry[numeroPuntos];

                // Pintamos un marco blanco como fondo
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, ancho, alto);

                // Establecemos el color de los puntos
                g.setColor(Color.BLUE);

                // Generamos los puntos aleatorios
                for(int i = 0; i<numeroPuntos; i++) {
                    AbstractMap.SimpleEntry<Integer, Integer> punto = generarPunto(ancho, alto);
                    puntos[i] = punto;
                    g.fillOval(punto.getKey(), punto.getValue(), 3, 3);
                }

                graphics.drawImage(bufferedImage, 0, 0, this);
            }
        };

        // Actualizamos el tamañoe
        panelGrafica.add(grafica);
    }

    public void crearMenu(JFrame frame, ActionListener listener) {

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
    }

    public static void main(String[] args) throws Exception {

        randomGenerator r = new randomGenerator();

        r.crearVentana();

    }
}
