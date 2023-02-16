import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class main {

    public static String RUTA_BASE = FileSystems.getDefault().getPath(".").toAbsolutePath().getParent().toString();

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

    public static void crearVentana() throws IOException{

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

    public static void panelLateralDerecho(JFrame frame) {

        JPanel panelBotones = new JPanel();
        panelBotones.setBorder(new EmptyBorder(0, 40, 0, 40));
        //panelBotones.setBackground(Color.RED);
        panelBotones.setMinimumSize(new Dimension(300, 700));
        panelBotones.setPreferredSize(new Dimension(300, 700));
        panelBotones.setMaximumSize(new Dimension(300, 700));

        GridLayout gridBotones = new GridLayout(16, 3);
        panelBotones.setLayout(gridBotones);

        JButton parametros = new JButton("Parámetros");
        parametros.setName("Parámetros");
        parametros.addActionListener(buttonListener);

        JButton curva = new JButton("Curva");
        curva.setName("Curva");
        curva.addActionListener(buttonListener);

        JButton computar = new JButton("Computar");
        computar.setName("Computar");
        computar.addActionListener(buttonListener);

        JButton detener = new JButton("Detener");
        detener.setName("Detener");
        detener.addActionListener(buttonListener);

        panelBotones.add(new JLabel(""));
        panelBotones.add(parametros);
        panelBotones.add(new JLabel(""));
        panelBotones.add(curva);
        panelBotones.add(new JLabel(""));
        panelBotones.add(computar);
        panelBotones.add(new JLabel(""));
        panelBotones.add(new JLabel(""));
        panelBotones.add(detener);
        //panelBotones.setBorder(new EmptyBorder(80, 80, 80, 80));

        frame.add(panelBotones, BorderLayout.EAST);
    }

    public static void panelLateralIzquierdo(JFrame frame) throws IOException {

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

    public static void anadirGrafica(JPanel panelGrafica){

        JPanel grafica = new JPanel(){

            @Override
            public void paint(Graphics graphics) {

                Random r = new Random();
                int ancho      = panelGrafica.getWidth();
                int alto       = panelGrafica.getHeight();
                int num_lineas = 10000;
                BufferedImage bufferedImage = new BufferedImage(ancho,alto,BufferedImage.TYPE_INT_RGB);
                Graphics g = bufferedImage.getGraphics();
                int x, y;

                for(int i = 0; i<num_lineas; i++) {
                    x=r.nextInt(ancho);
                    y=r.nextInt(alto);
                    g.drawOval(x, y, 1, 1);   // o tambien...
                }

                graphics.drawImage(bufferedImage, 0, 0, this);
            }
        };


        // Actualizamos el tamañoe
        panelGrafica.add(grafica);
    }

    public static void crearMenu(JFrame frame, ActionListener listener) {

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

        crearVentana();

    }
}