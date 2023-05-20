import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class urmInterpreterGUI {

    public static int ANCHO_VENTANA = 1400;
    public static int ALTO_VENTANA = 1000;

    JFrame frameVentana;

    ArrayList<JTextField> listadoRegistros = new ArrayList<>();

    public JPanel obtenerPanelBotonEntrada(){

        // Panel con el boton para elegir el fichero
        JPanel panelBotonEntradaPrograma = new JPanel();
        panelBotonEntradaPrograma.setLayout(new GridBagLayout());

        // Boton para elegir el fichero
        JButton botonElegirFichero = new JButton("Leer de archivo");
        botonElegirFichero.setPreferredSize(new Dimension(250,60));

        // Añadimos el boton al panel
        panelBotonEntradaPrograma.add(botonElegirFichero, new GridBagConstraints());

        return panelBotonEntradaPrograma;
    }

    public void anadirEntradaPrograma(GridBagConstraints gbcEntradaPrograma){

        JPanel panelEntradaPrograma = new JPanel();
        panelEntradaPrograma.setLayout(new GridBagLayout());

        // Establecemos los bordes
        EmptyBorder border = new EmptyBorder(20, 70, 0, 30);
        panelEntradaPrograma.setBorder(border);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;

        // JTextArea para escribir el programa
        JTextArea areaTextoPrograma = new JTextArea();
        JScrollPane scrollTextAreaPrograma = new JScrollPane (areaTextoPrograma,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        areaTextoPrograma.setFont(areaTextoPrograma.getFont().deriveFont(24f));

        // Restricciones para el JTextArea
        gbc.gridy = 0;
        gbc.weighty = 0.85;
        gbc.weightx = 1;

        // Añadimos el JTextArea al panel
        panelEntradaPrograma.add(scrollTextAreaPrograma, gbc);

        // Restricciones para boton con seleccion de archivo
        gbc.gridy = 1;
        gbc.weighty = 0.15;
        gbc.weightx = 1;

        // Añadimos el panel con el boton para elegir el archivo
        JPanel panelArchivoPrograma = obtenerPanelBotonEntrada();
        panelEntradaPrograma.add(panelArchivoPrograma, gbc);

        // Añadimos toodo al frame principal
        frameVentana.add(panelEntradaPrograma, gbcEntradaPrograma);
    }

    public JPanel obtenerRegistro(int numero){

        JPanel panelRegistro = new JPanel();
        panelRegistro.setLayout(new GridLayout(2,1));
        panelRegistro.setBorder(new EmptyBorder(10,10,20,10));

        JLabel labelRegistro = new JLabel("R" + (numero+1));
        labelRegistro.setBorder(new EmptyBorder(30,0,0,0));
        JTextField textFieldRegistro = new JTextField(10);

        // Añadimos el label y el input
        panelRegistro.add(labelRegistro);
        panelRegistro.add(textFieldRegistro);

        return panelRegistro;
    }

    public JPanel obtenerPanelRegistros(){

        JPanel panelBotonesRegistro = new JPanel();
        panelBotonesRegistro.setLayout(new GridLayout(4, 2));
        panelBotonesRegistro.setBorder(new EmptyBorder(0,0,50,0));

        for (int i=0; i<8; i++){
            panelBotonesRegistro.add(obtenerRegistro(i));
        }

        return panelBotonesRegistro;
    }

    public JPanel obtenerPanelBotonesSimulacion(){

        JPanel panelBotonesSimulacion = new JPanel();
        panelBotonesSimulacion.setLayout(new GridLayout(2, 1));

        // Panel con el boton "Interpretar"
        JPanel panelBotonInterpretar = new JPanel();
        panelBotonInterpretar.setLayout(new GridBagLayout());

        JButton botonInterpretar = new JButton("Interpretar");
        botonInterpretar.setPreferredSize(new Dimension(250,60));
        panelBotonInterpretar.add(botonInterpretar, new GridBagConstraints());

        // Panel con el boton "Interpretar un paso"
        JPanel panelBotonInterpretarPaso = new JPanel();
        panelBotonInterpretarPaso.setLayout(new GridBagLayout());
        panelBotonInterpretarPaso.setBorder(new EmptyBorder(0,0,60,0));

        JButton botonInterpretarPaso = new JButton("Interpretar un paso");
        botonInterpretarPaso.setPreferredSize(new Dimension(250,60));
        panelBotonInterpretarPaso.add(botonInterpretarPaso, new GridBagConstraints());

        // Añadimos los botones al panel principal
        panelBotonesSimulacion.add(panelBotonInterpretar);
        panelBotonesSimulacion.add(panelBotonInterpretarPaso);

        return panelBotonesSimulacion;
    }

    public void anadirRegistros(GridBagConstraints gbcEntradaPrograma){

        JPanel panelRegistros = new JPanel();
        panelRegistros.setLayout(new GridBagLayout());

        // Añadimos los bordes al panel
        EmptyBorder border = new EmptyBorder(30, 25, 0, 25);
        panelRegistros.setBorder(border);

        // Restricciones del panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;

        // Restricciones para los registros
        gbc.gridy = 0;
        gbc.weighty = 0.3;
        gbc.weightx = 1;

        // Añadimos el panel con los registros
        JPanel panelBotonesRegistro = obtenerPanelRegistros();
        panelRegistros.add(panelBotonesRegistro, gbc);

        // Restricciones para botones de accion
        gbc.gridy = 1;
        gbc.weighty = 0.7;
        gbc.weightx = 1;

        // Añadimos el panel con los botones para simular
        JPanel panelBotonesSimulacion = obtenerPanelBotonesSimulacion();
        panelRegistros.add(panelBotonesSimulacion, gbc);

        // Añadimos toodo al frame principal
        frameVentana.add(panelRegistros, gbcEntradaPrograma);
    }

    public void anadirSalida(GridBagConstraints gbcEntradaPrograma){

        JPanel panelSalida = new JPanel();
        panelSalida.setLayout(new GridLayout(1,1));

        // TODO Colorear panel
        panelSalida.setBackground(Color.ORANGE);

        /*GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;

        // JTextArea para escribir el programa
        JTextArea areaTextoPrograma = new JTextArea();

        // Restricciones para el JTextArea
        gbc.gridy = 0;
        gbc.weighty = 0.75;
        gbc.weightx = 1;

        // Añadimos el JTextArea al panel
        panelEntradaPrograma.add(areaTextoPrograma, gbc);

        // Restricciones para boton con seleccion de archivo
        gbc.gridy = 1;
        gbc.weighty = 0.25;
        gbc.weightx = 1;

        // Añadimos el panel con el boton para elegir el archivo
        anadirPanelArchivoPrograma(panelEntradaPrograma, gbc);*/

        // Añadimos toodo al frame principal
        frameVentana.add(panelSalida, gbcEntradaPrograma);
    }

    public void crearVentana(){

        // Frame donde meter la reticula
        frameVentana = new JFrame("Intérprete URM");

        // Establecemos el layout con los tamaños de cada parte
        frameVentana.getContentPane().setLayout(new GridBagLayout());

        // Dimensiones y posiciones de las distintas partes
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;

        // Posiciones/Dimensiones del panel para la entrada del programa
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weighty = 1;
        gbc.weightx = 0.35;

        // Añadimos la parte para introducir el URM programa (panel lateral izquierdo)
        anadirEntradaPrograma(gbc);

        // Posiciones/Dimensiones del panel para los registros
        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.weighty = 1;
        gbc.weightx = 0.35;

        // Añadimos el panel lateral derecho
        anadirRegistros(gbc);

        // Posiciones/Dimensiones del panel para los registros
        gbc.gridy = 0;
        gbc.gridx = 2;
        gbc.weighty = 1;
        gbc.weightx = 0.3;

        // Añadimos el panel lateral derecho
        anadirSalida(gbc);

        // EMpaquetamos toodo y lo mostramos
        frameVentana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameVentana.setMinimumSize(new Dimension(ANCHO_VENTANA, ALTO_VENTANA));
        frameVentana.setResizable(false);
        frameVentana.setLocationRelativeTo(null);
        frameVentana.pack();
        frameVentana.setVisible(true);
    }

    public static void main(String[] args) {

        urmInterpreterGUI urmInterpreterGUI = new urmInterpreterGUI();
        urmInterpreterGUI.crearVentana();
    }

}
