import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Locale;

public class lifeSimGUI {

    JFrame frameReticula;
    JFrame frameOpciones;

    volatile JPanel panelReticula;
    JPanel panelOpciones;

    public static final int TAMANIO_RETICULA = 600;
    private static final int ALTO_OPCIONES = TAMANIO_RETICULA;
    private static final int ANCHO_OPCIONES = 350;

    private static int ALTO_BARRA_VENTANA = -1;

    // Cada conjunto de X*X celdas se corresponderán con la evolución de una sola célula
    public static int FACTOR_CELULAS = 3;   // 3

    public static final double DESPLAZAMIENTO_GRAFICA = 0.3;

    private volatile lifeSim sim;

    private volatile Timer timer;

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
                for (int i=0; i<n; i++){
                    for (int j = 0; j < n; j++) {

                        int estadoCelula = sim.getGeneracionActual()[i][j];

                        // Inexistente o célula muerta
                        if (estadoCelula == 0 || estadoCelula == -1){
                            g.setColor(Color.BLACK);
                        }
                        // Célula viva
                        else if (estadoCelula == 1){
                            g.setColor(Color.GREEN);
                        }
                        // Planeador
                        else g.setColor(Color.BLUE);

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

    private void anadePanelOpciones(){

        // Frame donde meter la reticula
        frameOpciones = new JFrame("Opciones");
        frameOpciones.getContentPane().setLayout(new GridLayout(1,1));
        frameOpciones.setMinimumSize(new Dimension(ANCHO_OPCIONES, ALTO_OPCIONES));

        // Panel donde meteremos la gráfica
        panelOpciones = new JPanel();
        panelOpciones.setMinimumSize(new Dimension(ANCHO_OPCIONES, ALTO_OPCIONES));
        panelOpciones.setBackground(Color.RED);
        frameOpciones.add(panelOpciones);


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

        // Mostramos la reticula
        frameOpciones.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameOpciones.setResizable(false);
        //frameReticula.setLocationRelativeTo(null);
        frameOpciones.setLocation(dxOpciones, dyOpciones);
        frameOpciones.pack();
        frameOpciones.setVisible(true);
    }

    private void evolucionar(){

        // Cada 100ms pintará una nueva generación
        timer = new Timer(10, actionEvent -> {
            if (!sim.haTerminadoEvolucion()){
                sim.evoluciona();
                panelReticula.repaint();
            }
            else timer.stop();
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

        // Generamos el simulador
        // TODO Eliminar
            sim = new lifeSim(lifeSim.ESTADO_INICIAL.CANIONES, 1000);

        // Dibujamos
        // TODO ELiminar
        evolucionar();
    }

    public void comprobacionesPrevias(){
        String OS = System.getProperty("os.name", "unknown").toLowerCase(Locale.ROOT);
        ALTO_BARRA_VENTANA = OS.equals("win") ? 37 : 28;
    }

    public static void main(String[] args) {
        new lifeSimGUI().crearVentana();
    }
}
