import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;

public class urmInterpreterGUI {

    public static int ANCHO_VENTANA = 1700;
    public static int ALTO_VENTANA = 1000;

    JFrame frameVentana;

    ArrayList<JTextField> listadoRegistros = new ArrayList<>();
    JTextArea textAreaPrograma;
    JTextArea textAreaSalida;
    JButton botonInterpretar;
    JButton botonElegirFichero;
    JButton botonInterpretarPaso;

    JLabel labelResultado;

    final JFileChooser fc = new JFileChooser();


    urmInterpreter urmInterpreter;

    private void inicializarInterprete(){
        urmInterpreter = new urmInterpreter(textAreaPrograma.getText(), obtenerValoresRegistros());
        deshabilitarRegistros();
        deshabilitarInputPrograma();
    }

    private void deshabilitarRegistros(){
        for (JTextField textField : listadoRegistros){
            textField.setEnabled(false);
        }
    }

    private void deshabilitarBotonesInterpretacion(){
        botonInterpretar.setEnabled(false);
        botonInterpretarPaso.setEnabled(false);
    }
    private void habilitarBotonesInterpretacion(){
        botonInterpretar.setEnabled(true);
        botonInterpretarPaso.setEnabled(true);
    }

    private void deshabilitarInputPrograma(){
        textAreaPrograma.setEditable(false);
        botonElegirFichero.setEnabled(false);
    }

    private void habilitarInputPrograma(){
        textAreaPrograma.setEditable(true);
        botonElegirFichero.setEnabled(true);
    }

    private void cargarContenidoFichero(File file){

        if (file.getName().endsWith(".urm") || file.getName().endsWith(".txt")){

            try {

                StringBuilder programa = new StringBuilder();

                // Leemos el contenido del fichero
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line = bufferedReader.readLine();

                while (line != null){
                    programa.append(line).append("\n");
                    line = bufferedReader.readLine();
                }

                // Escribimos el programa en el text area
                textAreaPrograma.setText(programa.toString());

                // Cerramos el fichero
                bufferedReader.close();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }



    private void actualizarTrayectoria(){
        if (urmInterpreter != null){
            textAreaSalida.setText(String.join("\n", urmInterpreter.getTraza()));
            textAreaSalida.setCaretPosition(0);
        }
    }

    public int[] obtenerValoresRegistros(){

        int[] registros = new int[listadoRegistros.size()];

        for (int i=0; i<registros.length; i++){
            JTextField target = listadoRegistros.get(i);

            // Leemos el valor del JTextField
            if (!target.getText().isEmpty()){
                registros[i] = Integer.parseInt(target.getText());
            }
        }

        return registros;
    }

    public JPanel obtenerPanelBotonEntrada(){

        // Panel con el boton para elegir el fichero
        JPanel panelBotonEntradaPrograma = new JPanel();
        panelBotonEntradaPrograma.setLayout(new GridBagLayout());

        // Boton para elegir el fichero
        botonElegirFichero = new JButton("Leer de archivo");
        botonElegirFichero.setPreferredSize(new Dimension(250,60));
        botonElegirFichero.addActionListener(actionEvent -> {
            int returnVal = fc.showOpenDialog(frameVentana);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                cargarContenidoFichero(file);
            }
        });

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
        textAreaPrograma = new JTextArea();
        JScrollPane scrollTextAreaPrograma = new JScrollPane (textAreaPrograma,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textAreaPrograma.setFont(textAreaPrograma.getFont().deriveFont(24f));

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
        textFieldRegistro.setText("0");

        textFieldRegistro.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') ||
                        (c == KeyEvent.VK_BACK_SPACE) ||
                        (c == KeyEvent.VK_DELETE))) {
                    e.consume();
                }
            }
        });

        // Guardamos el textfield
        listadoRegistros.add(textFieldRegistro);

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
        panelBotonesSimulacion.setLayout(new GridLayout(3, 1));

        // Panel con el boton "Interpretar"
        JPanel panelBotonInterpretar = new JPanel();
        panelBotonInterpretar.setLayout(new GridBagLayout());

        botonInterpretar = new JButton("Interpretar");
        botonInterpretar.setPreferredSize(new Dimension(250,60));
        botonInterpretar.addActionListener(actionEvent -> {

            // No hay nada
            if (urmInterpreter == null){
                // Interpretamos toodo el programa
                inicializarInterprete();
                urmInterpreter.interpretarTodo();
                actualizarTrayectoria();
                labelResultado.setText("y = " + urmInterpreter.getResultado());

                deshabilitarBotonesInterpretacion();
            }

            // Hay algo
            else {

                // No se ha terminado de ejecutar
                if (!urmInterpreter.ejecucionTerminada()){
                    urmInterpreter.interpretarTodo();
                    actualizarTrayectoria();
                    labelResultado.setText("y = " + urmInterpreter.getResultado());

                    deshabilitarBotonesInterpretacion();
                }
            }
        });
        panelBotonInterpretar.add(botonInterpretar, new GridBagConstraints());

        // Panel con el boton "Interpretar un paso"
        JPanel panelBotonInterpretarPaso = new JPanel();
        panelBotonInterpretarPaso.setLayout(new GridBagLayout());
        panelBotonInterpretarPaso.setBorder(new EmptyBorder(0,0,60,0));

        botonInterpretarPaso = new JButton("Interpretar un paso");
        botonInterpretarPaso.setPreferredSize(new Dimension(250,60));
        botonInterpretarPaso.addActionListener(actionEvent -> {

            // Había algo
            if (urmInterpreter != null){

                // Continuamos una ejecución
                if (!urmInterpreter.ejecucionTerminada()){
                    urmInterpreter.interpretarSiguiente();
                    actualizarTrayectoria();
                }
            }

            // No había nada ejecutándose, creamos una nueva instancia
            else {
                inicializarInterprete();
                urmInterpreter.interpretarSiguiente();
                actualizarTrayectoria();
            }

            // Ejecucion acabada. Hay que volver a pulsar reset
            if (urmInterpreter.ejecucionTerminada()){
                labelResultado.setText("y =  " + urmInterpreter.getResultado());

                deshabilitarBotonesInterpretacion();
            }
        });
        panelBotonInterpretarPaso.add(botonInterpretarPaso, new GridBagConstraints());

        // Panel con el boton "Reset"
        JPanel panelBotonReset = new JPanel();
        panelBotonReset.setLayout(new GridBagLayout());
        panelBotonReset.setBorder(new EmptyBorder(0,0,60,0));

        JButton botonReset = new JButton("Reset");
        botonReset.setPreferredSize(new Dimension(250,60));
        botonReset.addActionListener(actionEvent -> {

            // Habilitamos los registros y los ponemos a 0
            for (JTextField textField : listadoRegistros) {
                textField.setEnabled(true);
                textField.setText("0");
            }

            // Habilitamos los botones para ejecutar una simulacion
            habilitarBotonesInterpretacion();

            // Permitimos introducir instrucciones para un nuevo programa
            habilitarInputPrograma();

            // Limpiamos la traza
            textAreaSalida.setText("");

            // Eliminamos la instancia del interprete
            urmInterpreter = null;

            // Limpiamos el resultado
            labelResultado.setText("");
        });
        panelBotonReset.add(botonReset, new GridBagConstraints());

        // Añadimos los botones al panel principal
        panelBotonesSimulacion.add(panelBotonInterpretar);
        panelBotonesSimulacion.add(panelBotonInterpretarPaso);
        panelBotonesSimulacion.add(panelBotonReset);

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
        panelSalida.setLayout(new GridBagLayout());

        // Establecemos los bordes
        //EmptyBorder border = new EmptyBorder(20, 10, 100, 10);
        //panelSalida.setBorder(border);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.weightx = 1;

        // Restricciones del JTextArea
        gbc.gridy = 0;
        gbc.weighty = 0.8;

        // JTextArea para escribir la traza
        JPanel panelTextAreaSalida = new JPanel();
        panelTextAreaSalida.setLayout(new GridLayout());
        textAreaSalida = new JTextArea();
        textAreaSalida.setEditable(false);
        JScrollPane scrollTextAreaPrograma = new JScrollPane (textAreaSalida,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollTextAreaPrograma.setPreferredSize(new Dimension(1,1));
        textAreaSalida.setFont(textAreaSalida.getFont().deriveFont(16f));

        // Añadimos el JTextArea al panel
        panelTextAreaSalida.add(scrollTextAreaPrograma);
        panelSalida.add(panelTextAreaSalida, gbc);

        // JLabel para el resultado
        JPanel panelLabelResultado = new JPanel();
        panelLabelResultado.setLayout(new GridBagLayout());
        labelResultado = new JLabel("");
        labelResultado.setFont(textAreaSalida.getFont().deriveFont(24f));

        // Restricciones del JLabel
        gbc.gridy = 1;
        gbc.weighty = 0.2;

        // Añadimois el JLabel al panel
        panelLabelResultado.add(labelResultado);
        panelSalida.add(panelLabelResultado, gbc);

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
        gbc.weightx = 0.3;

        // Añadimos la parte para introducir el URM programa (panel lateral izquierdo)
        anadirEntradaPrograma(gbc);

        // Posiciones/Dimensiones del panel para los registros
        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.weighty = 1;
        gbc.weightx = 0.25;

        // Añadimos el panel lateral derecho
        anadirRegistros(gbc);

        // Posiciones/Dimensiones del panel para los registros
        gbc.gridy = 0;
        gbc.gridx = 2;
        gbc.weighty = 1;
        gbc.weightx = 0.45;

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
