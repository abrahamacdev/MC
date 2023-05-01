import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class lifeSimGUI {

    JFrame frameReticula;
    JFrame frameOpciones;

    volatile JPanel panelReticula;

    volatile JPanel panelSeleccionReticula;

    volatile JPanel panelGraficaPoblacion;

    JPanel panelOpciones;

    JSpinner spinnerGeneraciones;
    JButton botonSimular;

    JSpinner spinnerPs;
    JSpinner spinnerPm;
    JSpinner spinnerPp;
    JSpinner spinnerNP;

    JComboBox<String> comboBoxEscenarios;

    private boolean mostrandoGrillaSeleccion = false;



    public static final int TAMANIO_RETICULA = 900; // 600
    private static final int ALTO_OPCIONES = TAMANIO_RETICULA;
    private static final int ANCHO_OPCIONES = 350;

    private static int ALTO_BARRA_VENTANA = -1;

    // Cada conjunto de X*X celdas se corresponderán con la evolución de una sola célula
    public static int FACTOR_CELULAS = 10;   // 4

    public static final double DESPLAZAMIENTO_GRAFICA = 0.3;


    private static final int GENERACIONES_INICIALES = 600;

    private volatile lifeSim sim = new lifeSim(GENERACIONES_INICIALES, 0);

    private volatile Timer timer;

    private HashSet<Point> setPuntosReticula = new HashSet<>();

    private volatile boolean simulando = false;



    private void pintarReticula(Graphics graphics){

        //int anchoPanel = panelReticula.getWidth();
        //int altoPanel = panelReticula.getHeight();

        int n = TAMANIO_RETICULA / FACTOR_CELULAS;

        BufferedImage bufferedImage = new BufferedImage(TAMANIO_RETICULA,TAMANIO_RETICULA,BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();

        // Pintamos el fondo
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, TAMANIO_RETICULA, TAMANIO_RETICULA);

        // Pintamos
        if (sim != null){

            try {

                byte[][] gen = sim.getActualGen();

                for (int i=0; i<n; i++){
                    for (int j = 0; j < n; j++) {

                        byte v = gen[i][j];

                        // Establecemos el color
                        if (v == 1){
                            g.setColor(Color.black);
                        }else {
                            g.setColor(Color.white);
                        }

                        // Pintamos el cuadrado
                        g.fillRect(i*FACTOR_CELULAS, j*FACTOR_CELULAS, i + FACTOR_CELULAS, j +FACTOR_CELULAS);
                        //g.drawOval(i*FACTOR_CELULAS, j*FACTOR_CELULAS, FACTOR_CELULAS, FACTOR_CELULAS);
                        //g.drawRect(i*FACTOR_CELULAS, j*FACTOR_CELULAS, FACTOR_CELULAS, FACTOR_CELULAS);
                    }
                }
            }catch (Exception e){
                System.out.println(sim.getnGeneracionActual());
            }

        }

        graphics.drawImage(bufferedImage, 0, 0, panelReticula);
    }

    private void pintarSeleccionReticula(Graphics graphics){

        int n = TAMANIO_RETICULA / FACTOR_CELULAS;

        BufferedImage bufferedImage = new BufferedImage(TAMANIO_RETICULA,TAMANIO_RETICULA,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();

        int anchoPanel = panelSeleccionReticula.getWidth();
        int altoPanel = panelSeleccionReticula.getHeight();

        // Pintamos el fondo
        g.setColor(Color.white);
        g.fillRect(0, 0, TAMANIO_RETICULA, TAMANIO_RETICULA);

        // Cambiamos el color para pintar
        g.setColor(Color.black);

        // Pintamos los cuadrados elegidos
        for (Point punto : setPuntosReticula){

            int x1 = punto.x * FACTOR_CELULAS;
            int y1 = punto.y * FACTOR_CELULAS;

            g.fillRect(x1, y1, FACTOR_CELULAS, FACTOR_CELULAS);
        }

        // Pintamos las líneas de la cuadrícula
        int startX = 0;
        int startY = 0;
        int endX = altoPanel;
        int endY = anchoPanel;
        int x = startX;
        int y = startY;
        while (x <= endX){
            g.drawLine(x, startY, x, endY);
            g.drawLine(startX, y, endX, y);

            x += FACTOR_CELULAS;
            y += FACTOR_CELULAS;
        }

        graphics.drawImage(bufferedImage, 0, 0, panelSeleccionReticula);
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

                /*float[] historicoA = sim.getMediaPoblacionA();
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

                 */

            }catch (Exception e){}
        }

        graphics.drawImage(bufferedImage, 0, 0, panelGraficaPoblacion);
    }

    private void cambiaEstadoReticula(boolean ponerGrillaSeleccion){

        // Ponemos la grilla para elegir las células activas
        if (ponerGrillaSeleccion){

            // Eliminamos la reticula para meter la selección reticula
            frameReticula.getContentPane().removeAll();

            // Panel para mostrar la retícula con lineas para elegir los puntos iniciales
            panelSeleccionReticula = new JPanel(){
                @Override
                public void paint(Graphics g) {
                    pintarSeleccionReticula(g);
                }
            };
            panelSeleccionReticula.setMinimumSize(new Dimension(TAMANIO_RETICULA+10, TAMANIO_RETICULA));
            panelSeleccionReticula.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {

                    if (!simulando && comboBoxEscenarios.getSelectedIndex() > 3){
                        Point localizacionMouse = mouseEvent.getPoint();
                        Point puntoElegido = new Point();

                        puntoElegido.x = localizacionMouse.x / FACTOR_CELULAS;
                        puntoElegido.y = localizacionMouse.y / FACTOR_CELULAS;

                        // Desmarcamos el punto
                        if (setPuntosReticula.contains(puntoElegido)){
                            setPuntosReticula.remove(puntoElegido);
                        }
                        // Lo marcamos
                        else {
                            // GUardamos el punto
                            setPuntosReticula.add(puntoElegido);
                        }
                        panelSeleccionReticula.repaint();
                    }
                }
                @Override
                public void mousePressed(MouseEvent mouseEvent) {}

                @Override
                public void mouseReleased(MouseEvent mouseEvent) {}

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {}

                @Override
                public void mouseExited(MouseEvent mouseEvent) {}
            });
            frameReticula.add(panelSeleccionReticula);
        }

        // Mostramos la grilla con las posiciones iniciales
        else {
            // Eliminamos la seleccion reticula para meter la reticula
            frameReticula.getContentPane().removeAll();
            panelReticula = new JPanel(){
                @Override
                public void paint(Graphics g) {
                    pintarReticula(g);
                }
            };
            frameReticula.add(panelReticula);
        }
        frameReticula.revalidate();
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
        frameReticula.add(panelReticula);

        // Panel donde dejaremos elegir los puntos iniciales
        /*panelSeleccionReticula = new JPanel(){
            @Override
            public void paint(Graphics g) {
                pintarSeleccionReticula(g);
            }
        };
        panelSeleccionReticula.setMinimumSize(new Dimension(TAMANIO_RETICULA+10, TAMANIO_RETICULA));
        panelSeleccionReticula.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

                if (!simulando && comboBoxEscenarios.getSelectedIndex() > 3){
                    Point localizacionMouse = mouseEvent.getPoint();
                    Point puntoElegido = new Point();

                    puntoElegido.x = localizacionMouse.x / FACTOR_CELULAS;
                    puntoElegido.y = localizacionMouse.y / FACTOR_CELULAS;

                    // Desmarcamos el punto
                    if (setPuntosReticula.contains(puntoElegido)){
                        setPuntosReticula.remove(puntoElegido);
                    }
                    // Lo marcamos
                    else {
                        // GUardamos el punto
                        setPuntosReticula.add(puntoElegido);
                    }
                    panelSeleccionReticula.repaint();
                }
            }
            @Override
            public void mousePressed(MouseEvent mouseEvent) {}

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {}

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {}

            @Override
            public void mouseExited(MouseEvent mouseEvent) {}
        });
        frameReticula.add(panelSeleccionReticula);*/


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
        frameReticula.setResizable(true);
        //frameReticula.setLocationRelativeTo(null);
        frameReticula.setLocation(dx-offsetX, dy);
        frameReticula.pack();
        frameReticula.setVisible(true);
    }

    private void gestionarClickReticula(){

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
        panelBotones.setLayout(new GridLayout(18, 1));

        EmptyBorder bordeIzquierdoJLabels = new EmptyBorder(10, 0, 0, 0);

        // Para margen
        panelBotones.add(new JLabel(""));

        JLabel labelGeneraciones = new JLabel("Numero de generaciones");
        labelGeneraciones.setBorder(bordeIzquierdoJLabels);
        spinnerGeneraciones = new JSpinner();
        spinnerGeneraciones.setValue(GENERACIONES_INICIALES);
        spinnerGeneraciones.addChangeListener((event) -> {
            if ((Integer) spinnerGeneraciones.getValue() <= 0) spinnerGeneraciones.setValue(1);
        });

        // Añadimos el label y el spinner de las generaciones
        panelBotones.add(labelGeneraciones);
        panelBotones.add(spinnerGeneraciones);

        JLabel labelEscenario = new JLabel("Escenario Inicial");
        String[] opcionesEscenarios = new String[]{"A", "B", "C", "D", "Personalizado"};
        comboBoxEscenarios = new JComboBox<>(opcionesEscenarios);
        comboBoxEscenarios.addItemListener(itemEvent -> {

            // Es un escenario => parámetros por defecto y no se puede editar
            if (comboBoxEscenarios.getSelectedIndex() < 4){

                // Desactivamos los spinners
                desactivarSpinnersParametros();

                // Ponemos los parametros en oscuro
                lifeSim.ParametrosEscenario parametrosEscenario = lifeSim.ParametrosEscenario.ESCENARIOS_INICIALES[comboBoxEscenarios.getSelectedIndex()];
                spinnerPs.setValue(parametrosEscenario.Ps);
                spinnerPm.setValue(parametrosEscenario.Pm);
                spinnerPp.setValue(parametrosEscenario.Pp);
                spinnerNP.setValue(parametrosEscenario.NP);

                // Ponemos la grilla normal
                cambiaEstadoReticula(false);

                // Eliminamos los puntos que se hayan podido seleccionar de la retícula
                setPuntosReticula.clear();

                // Creamos un objeto sim para obtener las posiciones de los primeros puntos
                sim = new lifeSim((int)spinnerGeneraciones.getValue(), comboBoxEscenarios.getSelectedIndex());
                panelReticula.repaint();
            }
            else {

                // Activamos los spinners
                activarSpinnersParametros();

                // Ponemos la grilla para elegir
                cambiaEstadoReticula(true);
            }
        });

        // Añadimos el label y el selector de escenario
        panelBotones.add(labelEscenario);
        panelBotones.add(comboBoxEscenarios);

        // Change listener para sólo permitir valores [0,1]
        ChangeListener changeListenerParamsDecimales = changeEvent -> {

            JSpinner objetivo = (JSpinner) changeEvent.getSource();
            Double valor = (Double) objetivo.getValue();

            if (valor < 0) objetivo.setValue(0.0);
            else if (valor > 1) objetivo.setValue(1.0);
        };

        JLabel labelPs = new JLabel("Parámetro Ps");
        labelPs.setBorder(new EmptyBorder(10, 0, 0, 0));
        SpinnerNumberModel plantillaSpinner = new SpinnerNumberModel(0.5d, 0.0d, 1.0d, 0.01d);
        spinnerPs = new JSpinner(plantillaSpinner);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinnerPs, "#,##0.00");
        spinnerPs.setEditor(editor);
        spinnerPs.setValue(1.0);
        spinnerPs.addChangeListener(changeListenerParamsDecimales);

        // Añadimos el label y spinner del parámetro Ps
        panelBotones.add(labelPs);
        panelBotones.add(spinnerPs);

        JLabel labelPm = new JLabel("Parámetro Pm");
        labelPm.setBorder(new EmptyBorder(10, 0, 0, 0));
        // Modifica el spinner para valores con 2 decimales
        SpinnerNumberModel plantillaSpinner2 = new SpinnerNumberModel(0.5d, 0.0d, 1.0d, 0.01d);
        spinnerPm = new JSpinner(plantillaSpinner2);
        JSpinner.NumberEditor editor2 = new JSpinner.NumberEditor(spinnerPm, "#,##0.00");
        spinnerPm.setEditor(editor2);
        spinnerPm.setValue(0.2);
        spinnerPm.addChangeListener(changeListenerParamsDecimales);

        // Añadimos el label y spinner del parámetro Pm
        panelBotones.add(labelPm);
        panelBotones.add(spinnerPm);

        JLabel labelPp = new JLabel("Parámetro Pp");
        labelPp.setBorder(new EmptyBorder(10, 0, 0, 0));
        SpinnerNumberModel plantillaSpinner3 = new SpinnerNumberModel(0.5d, 0.0d, 1.0d, 0.01d);
        spinnerPp = new JSpinner(plantillaSpinner3);
        JSpinner.NumberEditor editor3 = new JSpinner.NumberEditor(spinnerPp, "#,##0.00");
        spinnerPp.setEditor(editor3);
        spinnerPp.setValue(0.25);
        spinnerPp.addChangeListener(changeListenerParamsDecimales);

        // Añadimos el label y spinner del parámetro Pp
        panelBotones.add(labelPp);
        panelBotones.add(spinnerPp);

        JLabel labelNP = new JLabel("Parámetro NP");
        labelNP.setBorder(new EmptyBorder(10, 0, 0, 0));
        spinnerNP = new JSpinner();
        spinnerNP.setValue(1);
        spinnerNP.addChangeListener(changeEvent -> {
            if ((Integer) spinnerNP.getValue() < 1) spinnerNP.setValue(1);
        });

        // Añadimos el label y spinner del parámetro Pp
        panelBotones.add(labelNP);
        panelBotones.add(spinnerNP);

        // Añadimos el boton para comenzar la simulacion
        botonSimular = new JButton("Simular");
        botonSimular.addActionListener(e -> {

            if (!simulando){

                // Es un escenario personalizado
                if (comboBoxEscenarios.getSelectedIndex() > 3){
                    double Ps = (double) spinnerPs.getValue();
                    double Pm = (double) spinnerPm.getValue();
                    double Pp = (double) spinnerPp.getValue();
                    int NP = (int) spinnerNP.getValue();
                    lifeSim.ParametrosEscenario parametrosEscenario = new lifeSim.ParametrosEscenario(Ps, Pm, Pp, NP, setPuntosReticula);
                    sim = new lifeSim((int) spinnerGeneraciones.getValue(), parametrosEscenario);
                }
                // Es un escenario preestablecido
                else {
                    sim = new lifeSim((int) spinnerGeneraciones.getValue(), comboBoxEscenarios.getSelectedIndex());
                }

                // Cambiamos la retícula para mostrar la simulación
                cambiaEstadoReticula(false);

                simulando = true;

                // Comenzamos el proceso de evolucionado
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


        // Desactivamos los parametros
        desactivarSpinnersParametros();

        // Añadimos el panel al principal
        panelOpciones.add(panelBotones);
    }

    private void activarSpinnersParametros(){
        spinnerPs.setEnabled(true);
        spinnerPm.setEnabled(true);
        spinnerPp.setEnabled(true);
        spinnerNP.setEnabled(true);
    }

    private void desactivarSpinnersParametros(){
        spinnerPs.setEnabled(false);
        spinnerPm.setEnabled(false);
        spinnerPp.setEnabled(false);
        spinnerNP.setEnabled(false);
    }


    private void anadePanelOpciones(){

        // Frame donde meter la reticula
        frameOpciones = new JFrame("Opciones");
        frameOpciones.getContentPane().setLayout(new GridLayout(1,1));
        frameOpciones.setMinimumSize(new Dimension(ANCHO_OPCIONES, ALTO_OPCIONES));

        // Panel donde meteremos la gráfica
        panelOpciones = new JPanel();
        panelOpciones.setLayout(new GridLayout(1, 1));
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
        ALTO_BARRA_VENTANA = OS.equals("win") ? 37 : OS.contains("nux") ? 37 : 28;
    }

    public static void main(String[] args) {
        new lifeSimGUI().crearVentana();

        /*lifeSim s = new lifeSim(10, true, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 1, 1);
        while (!s.haTerminadoEvolucion())s.evoluciona();*/
    }
}
