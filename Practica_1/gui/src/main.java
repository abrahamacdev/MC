import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class main {

    static String RUTA_BASE = FileSystems.getDefault().getPath(".").toAbsolutePath().getParent().toString();

    static ActionListener buttonListener = new ActionListener() {
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

        frame.add(panelBotones, BorderLayout.EAST);

        JPanel panelImagen = new JPanel();
        panelImagen.setLayout(new GridLayout());
        panelImagen.setBackground(Color.BLUE);
        panelImagen.setMinimumSize(new Dimension(600, 700));
        panelImagen.setPreferredSize(new Dimension(600, 700));
        panelBotones.setBorder(new EmptyBorder(80, 80, 80, 80));

        // Cargamos la imagen y la mostramos
        BufferedImage imagen = ImageIO.read(new File(RUTA_BASE + "/espacio.jpg"));
        Image imagenRecortada = imagen.getScaledInstance(600, 500, Image.SCALE_DEFAULT);

        JLabel picLabel = new JLabel(new ImageIcon(imagenRecortada));
        picLabel.setMinimumSize(new Dimension(600, 500));
        picLabel.setPreferredSize(new Dimension(600, 500));
        picLabel.setMaximumSize(new Dimension(600, 500));

        panelImagen.add(picLabel);
        frame.add(panelImagen, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(anchoFrame, altoFrame));
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
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