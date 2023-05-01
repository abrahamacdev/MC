import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public class lifeSimGUI {

    JFrame frameReticula;
    JFrame frameOpciones;

    volatile JPanel panelReticula;

    volatile JPanel panelGraficaPoblacion;

    JPanel panelOpciones;

    JSpinner spinnerGeneraciones;
    JButton botonSimular;


    private volatile boolean simulando = false;

    public static final int TAMANIO_RETICULA = 600;
    private static final int ALTO_OPCIONES = TAMANIO_RETICULA;
    private static final int ANCHO_OPCIONES = 350;

    private static int ALTO_BARRA_VENTANA = -1;

    // Cada conjunto de X*X celdas se corresponderán con la evolución de una sola célula
    public static int FACTOR_CELULAS = 4;   // 3

    public static final double DESPLAZAMIENTO_GRAFICA = 0.3;

    private volatile lifeSim sim;

    private volatile Timer timer;

    private Color coloresCurvas[] = new Color[]{Color.RED, Color.BLUE, Color.GREEN};


    private Color value2Color(float c){
        float minHue = 120f/255; //corresponds to green
        float maxHue = 0; //corresponds to red
        float hue = c*maxHue + (1-c)*minHue;
        return new Color(Color.HSBtoRGB(hue, 1, 0.5f));
    }

    private float normaliza(float v, float min, float max){
        float norm = (v - min) / (max - min);

        return Math.max(norm, min);
    }

    private void pintaGeneracion(float[] media, Color color, int i, Graphics2D g,
                                 int maxHeight, int maxWidth, int minHeight, int minWidth,
                                 int maxGeneraciones){

        float rangoMinValores = 0.2f;
        float rangoMaxValores = 0.5f;

        g.setColor(color);

        int nGenActual = i;
        int pobActual =  (int) (normaliza(media[i], rangoMinValores, rangoMaxValores) * maxHeight);

        int nGenSiguiente = i+1;
        int pobSiguiente = (int) (normaliza(media[i+1], rangoMinValores, rangoMaxValores) * maxHeight);

        int xActual = nGenActual * maxWidth / maxGeneraciones;
        int yActual = maxHeight - pobActual;

        int xSiguiente = nGenSiguiente * maxWidth / maxGeneraciones;
        int ySiguiente = maxHeight - pobSiguiente;

        g.drawLine(xActual + minWidth, yActual+minHeight, xSiguiente+minWidth, ySiguiente+minHeight);
    }

    private void pintarReticula(Graphics graphics){

        //int anchoPanel = panelReticula.getWidth();
        //int altoPanel = panelReticula.getHeight();

        int n = TAMANIO_RETICULA / FACTOR_CELULAS;

        BufferedImage bufferedImage = new BufferedImage(TAMANIO_RETICULA,TAMANIO_RETICULA,BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();

        // Pintamos el fondo
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, TAMANIO_RETICULA, TAMANIO_RETICULA);

        // Pintamos
        if (sim != null){

            try {

                float a[][][] = sim.getA();

                for (int i=0; i<n; i++){
                    for (int j = 0; j < n; j++) {

                        // Establecemos el color
                        g.setColor(value2Color(a[i][j][sim.getQ()]));

                        // Pintamos el cuadrado
                        g.fillRect(i*FACTOR_CELULAS, j*FACTOR_CELULAS, i + FACTOR_CELULAS, j +FACTOR_CELULAS);
                    }
                }
            }catch (Exception e){
                System.out.println(sim.getnGeneracionActual());
            }

        }

        graphics.drawImage(bufferedImage, 0, 0, panelReticula);
    }

    private void pintarPoblacion(Graphics graphics){

        BufferedImage bufferedImage = new BufferedImage(panelGraficaPoblacion.getWidth(), panelGraficaPoblacion.getHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();

        int minWidth = 5;
        int minHeight = 5;

        int maxHeight = panelGraficaPoblacion.getHeight();
        int maxWidth = panelGraficaPoblacion.getWidth();

        // Pintamos el fondo
        g.setColor(Color.white);
        g.fillRect(0, 0, maxWidth, maxHeight);

        // Evitamos que quede muy pegado
        maxHeight -= minHeight;
        maxWidth -= minWidth;

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));

        // Pintamos
        if (sim != null && sim.getnGeneracionActual() > 0){

            try {

                // Suponemos que el número de células activas nunca va a ser superior a 1/6 del total
                // de posibles células activas (nos ayuda a hacer zoom en la gráfica).
                int maxPoblacionActiva =  1;
                int maxGeneraciones = sim.getNumGeneraciones();

                float[] historicoA = sim.getMediaPoblacionA();
                float[] historicoB = sim.getMediaPoblacionB();
                float[] historicoC = sim.getMediaPoblacionC();

                for (int i=0; i<sim.getnGeneracionActual(); i++){

                    // Grafica A
                    pintaGeneracion(historicoA, coloresCurvas[0], i, g, maxHeight, maxWidth,
                            minHeight, minWidth, maxGeneraciones);

                    // Grafica B
                    pintaGeneracion(historicoB, coloresCurvas[1], i, g, maxHeight, maxWidth,
                            minHeight, minWidth, maxGeneraciones);

                    // Grafica C
                    pintaGeneracion(historicoC, coloresCurvas[2], i, g, maxHeight, maxWidth,
                            minHeight, minWidth, maxGeneraciones);
                }

            }catch (Exception e){}
        }

        graphics.drawImage(bufferedImage, 0, 0, panelGraficaPoblacion);
    }

    private void anadeReticula(){

        // Frame donde meter la reticula
        frameReticula = new JFrame("Reticula");
        frameReticula.getContentPane().setLayout(new GridLayout(1,1));
        frameReticula.setMinimumSize(new Dimension(TAMANIO_RETICULA, TAMANIO_RETICULA + ALTO_BARRA_VENTANA));

        // Panel donde meteremos la gráfica
        panelReticula = new JPanel(){
            @Override
            public void paint(Graphics g) {
                pintarReticula(g);
            }
        };
        panelReticula.setBackground(Color.BLUE);
        frameReticula.add(panelReticula);


        // Colocamos el frame de la gráfica bien
        Dimension windowSize = frameReticula.getSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();

        // Un poquito más a la izquierda del centro
        int dx = centerPoint.x - windowSize.width / 2;
        int dy = centerPoint.y - windowSize.height / 2;
        int offsetX = (int) (frameReticula.getWidth() * DESPLAZAMIENTO_GRAFICA);

        // Mostramos la reticula
        frameReticula.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameReticula.setResizable(false);
        //frameReticula.setLocationRelativeTo(null);
        frameReticula.setLocation(dx-offsetX, dy);
        frameReticula.pack();
        frameReticula.setVisible(true);
    }

    private void anadirGraficaPoblacion(){

        JPanel panelTemp = new JPanel();
        panelTemp.setBorder(new EmptyBorder(20, 0,0 ,0));
        panelTemp.setLayout(new GridLayout(1,1));

        panelGraficaPoblacion = new JPanel(){
            @Override
            public void paint(Graphics g) {
                pintarPoblacion(g);
            }
        };

        panelTemp.add(panelGraficaPoblacion);
        panelOpciones.add(panelTemp);
    }

    private AbstractMap.SimpleEntry<Integer, Integer> posicionarPanelOpciones(){
        // Calculamos el cachito de la gráfica que esta "por la derecha del centro"
        Dimension windowSize = frameReticula.getSize();
        int offsetXReticula = (int) (frameReticula.getWidth() * DESPLAZAMIENTO_GRAFICA);
        int porLaDerecha = windowSize.width / 2 - offsetXReticula;

        // Calculamos la posiciones del frame para estar pegado a la derecha de la gráfica
        windowSize = frameOpciones.getSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();
        int dxOpciones = centerPoint.x + porLaDerecha;
        int dyOpciones = centerPoint.y - windowSize.height / 2;

        return new AbstractMap.SimpleEntry<>(dxOpciones, dyOpciones);
    }

    private void anadirBotonesOpciones(){

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(8, 1));

        // Para margen
        panelBotones.add(new JLabel(""));

        JLabel labelGeneraciones = new JLabel("Numero de generaciones");
        labelGeneraciones.setBorder(new EmptyBorder(10, 0, 0, 0));
        spinnerGeneraciones = new JSpinner();
        spinnerGeneraciones.setValue(600);
        spinnerGeneraciones.addChangeListener((event) -> {
            if ((Integer) spinnerGeneraciones.getValue() <= 0) spinnerGeneraciones.setValue(1);
        });

        // Añadimos el label y el spinner de las generaciones
        panelBotones.add(labelGeneraciones);
        panelBotones.add(spinnerGeneraciones);


        // Añadimos el label y el selector del estado inicial
        /*JLabel labelEstadoInicial = new JLabel("Estado inicial");
        String[] opcionesEstadoInicial = new String[]{"Aleatorio", "Islas", "Cañones Planeadores"};
        comboBoxEstadoInicial = new JComboBox(opcionesEstadoInicial);
        panelBotones.add(labelEstadoInicial);
        panelBotones.add(comboBoxEstadoInicial);*/

        // Añadimos el boton para comenzar la simulacion
        botonSimular = new JButton("Simular");
        botonSimular.addActionListener(e -> {

            if (!simulando){
                int nGeneraciones = (int) spinnerGeneraciones.getValue();
                //lifeSim.ESTADO_INICIAL estadoInicial = lifeSim.ESTADO_INICIAL.values()[comboBoxEstadoInicial.getSelectedIndex()];

                //sim = new lifeSim(estadoInicial, nGeneraciones);
                sim = new lifeSim(nGeneraciones);

                simulando = true;

                evolucionar();
            }
        });

        // Para margen
        panelBotones.add(new JLabel(""));
        panelBotones.add(botonSimular);

        // Añadimos el boton para comenzar la simulacion
        JButton botonReset = new JButton("Reset");
        botonReset.addActionListener(e -> {
            simulando = false;

            timer.stop();

            sim = null;
            simulando = false;
            panelReticula.repaint();
            panelGraficaPoblacion.repaint();
        });

        // Para margen
        panelBotones.add(new JLabel(""));
        panelBotones.add(botonReset);

        panelOpciones.add(panelBotones);

    }
    private void anadePanelOpciones(){

        // Frame donde meter la reticula
        frameOpciones = new JFrame("Opciones");
        frameOpciones.getContentPane().setLayout(new GridLayout(1,1));
        frameOpciones.setMinimumSize(new Dimension(ANCHO_OPCIONES, ALTO_OPCIONES));

        // Panel donde meteremos la gráfica
        panelOpciones = new JPanel();
        panelOpciones.setLayout(new GridLayout(2, 1));
        panelOpciones.setBorder(new EmptyBorder(10, 20, 10 ,20));
        panelOpciones.setMinimumSize(new Dimension(ANCHO_OPCIONES, ALTO_OPCIONES));
        frameOpciones.add(panelOpciones);

        // Calculamos la posicion del panel
        AbstractMap.SimpleEntry<Integer, Integer> posicionPanel = posicionarPanelOpciones();

        // Añadimos los botones para las opciones
        anadirBotonesOpciones();

        // Añadimos la gráfica con la evolución de la población
        anadirGraficaPoblacion();

        // Mostramos la reticula
        frameOpciones.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameOpciones.setResizable(false);
        //frameReticula.setLocationRelativeTo(null);
        frameOpciones.setLocation(posicionPanel.getKey(), posicionPanel.getValue());
        frameOpciones.pack();
        frameOpciones.setVisible(true);
    }

    private void evolucionar(){

        // Cada 100ms pintará una nueva generación
        timer = new Timer(10, actionEvent -> {
            if (!sim.haTerminadoEvolucion() && simulando){
                sim.evoluciona();
                panelReticula.repaint();
                panelGraficaPoblacion.repaint();
            }
            // Dejamos simulación final
            else if (sim.haTerminadoEvolucion()){
                timer.stop();

                // Reiniciamos la retícula
                sim = null;
                simulando = false;
            }
            // Simulacion parada. Eliminamos toodo
            else {

                // Habilitamos de nuevo el boton para simular
                timer.stop();

                // Reiniciamos la retícula
                sim = null;
                simulando = false;
                panelReticula.repaint();
                panelGraficaPoblacion.repaint();
            }
        });
        timer.start();
    }

    public void crearVentana() {

        // Establece el margen superior para los Jframes
        comprobacionesPrevias();

        // Mostramos la retícula
        anadeReticula();

        // Mostramos las opciones
        anadePanelOpciones();
    }

    public void comprobacionesPrevias(){
        String OS = System.getProperty("os.name", "unknown").toLowerCase(Locale.ROOT);
        ALTO_BARRA_VENTANA = OS.equals("win") ? 37 : 28;
    }

    public static void main(String[] args) {
        new lifeSimGUI().crearVentana();
    }
}
