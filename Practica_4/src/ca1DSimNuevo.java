import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

public class ca1DSimNuevo {

    private JFrame frameOpciones;
    private JFrame frameGraficas;


    private JComboBox<String> comboNumEstadosCelulas;
    private JSpinner spinnerRegla;

    private JComboBox<String> comboConfInicial;

    private JComboBox<String> comboCondFrontera;

    private JSpinner spinnerGeneraciones;

    private JSpinner spinnerNumCelulas;

    private JCheckBox checkBoxHamming;
    private JCheckBox checkBoxEntropiaEspacial;
    private JCheckBox checkBoxEntropiaTempCelula;
    private JSpinner spinnerCelulaObservada;


    final static String[] OPCIONES_INICIALIZACION_AUTOMATA = new String[]{"Aleatoria", "Célula Central Activa"};
    final static String[] OPCIONES_NUM_ESTADOS_AUTOMATA = new String[]{"2", "3", "4", "5"};

    final static String[] CONDICION_FRONTERA_AUTOMATA = new String[]{"Nula", "Cilíndrica"};

    public void anadirOpciones(JFrame frameOpciones){

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridy = 0;          // Fila que ocupara desde arriba izquierda
        gbc.gridx = 0;          // Columna que ocupará desde arriba izquierda
        gbc.gridheight = 1;     // Nº de filas que ocupará verticalmente
        gbc.weightx = 1.0;      // Porcentaje sobre el eje x (ancho) que ocupará
        gbc.weighty = 0.9;      // Porcentaje sobre el eje y (alto) que ocupará
        gbc.insets = new Insets(0,0,0,0);

        // Creamos el panel en el que irán las opciones
        JPanel panelOpciones = new JPanel();
        panelOpciones.setLayout(new GridLayout(1,2));


        // Separamos el panel izquierdo del derecho
        JPanel panelOpcionesIzquierdo = new JPanel();
        panelOpcionesIzquierdo.setLayout(new GridLayout(10, 1));
        panelOpcionesIzquierdo.setBackground(Color.BLUE);

        JPanel panelOpcionesDerecho = new JPanel();
        panelOpcionesDerecho.setLayout(new GridLayout(10, 1));
        panelOpcionesDerecho.setBackground(Color.GREEN);

        // Añadimos padding a los paneles
        EmptyBorder bordesPaneles = new EmptyBorder(0, 15, 20, 15);
        panelOpcionesIzquierdo.setBorder(bordesPaneles);
        panelOpcionesIzquierdo.setBorder(bordesPaneles);

        // Añadimos las opciones
        anadirBotonesOpciones(panelOpcionesIzquierdo, panelOpcionesDerecho);

        // Anadimos el panel de opciones al frame
        panelOpciones.add(panelOpcionesIzquierdo);
        panelOpciones.add(panelOpcionesDerecho);
        frameOpciones.add(panelOpciones, gbc);
    }

    public void anadirBotonesOpciones(JPanel panelOpcionesIzquierdo, JPanel panelOpcionesDerecho){

        // Nº estados por celula
        JLabel textoEstados = new JLabel("Nº de estados por celula:");
        textoEstados.setBorder(new EmptyBorder(15, 0, 0, 0));
        comboNumEstadosCelulas = new JComboBox<>(OPCIONES_NUM_ESTADOS_AUTOMATA);

        // Función de transicion (regla)
        JLabel textoRegla = new JLabel("Regla (base 10):");
        textoRegla.setBorder(new EmptyBorder(15, 0, 0, 0));
        spinnerRegla = new JSpinner();
        spinnerRegla.setValue(700);
        spinnerRegla.setPreferredSize(new Dimension(spinnerRegla.getPreferredSize().width, 20));

        // Configuracion inicial
        JLabel textoInicializacion = new JLabel("Configuracion inicial:");
        textoInicializacion.setBorder(new EmptyBorder(15, 0, 0, 0));
        comboConfInicial = new JComboBox<>(OPCIONES_INICIALIZACION_AUTOMATA);

        // Condidición de frontera
        JLabel textoCondicionFrontera = new JLabel("Condicion Frontera:");
        textoCondicionFrontera.setBorder(new EmptyBorder(15, 0, 0, 0));
        comboCondFrontera = new JComboBox<>(CONDICION_FRONTERA_AUTOMATA);

        // Nº generaciones
        JLabel textoGeneraciones = new JLabel("Número de Generaciones:");
        textoGeneraciones.setBorder(new EmptyBorder(15, 0, 0, 0));
        spinnerGeneraciones = new JSpinner();
        spinnerGeneraciones.setValue(600);

        // Nº células
        JLabel textoNumCelulas = new JLabel("Número de Células:");
        textoNumCelulas.setBorder(new EmptyBorder(15, 0, 0, 0));
        spinnerNumCelulas = new JSpinner();
        spinnerNumCelulas.setValue(500);

        // Checkbox Dist. Hamming
        checkBoxHamming = new JCheckBox("Calcular distancia de Hamming");

        // Checkbox Entropía Espacial
        checkBoxEntropiaEspacial = new JCheckBox("Calcular entropía espacial");

        // Checkbox Entropía Temporal Célula
        checkBoxEntropiaTempCelula = new JCheckBox("Calcular entropía temporal Célula");

        // Spinner célula observada
        spinnerNumCelulas = new JSpinner();
        spinnerNumCelulas.setValue(0);



        panelOpcionesIzquierdo.add(textoEstados);
        panelOpcionesIzquierdo.add(comboNumEstadosCelulas);

        panelOpcionesIzquierdo.add(textoRegla);
        panelOpcionesIzquierdo.add(spinnerRegla);

        panelOpcionesIzquierdo.add(textoInicializacion);
        panelOpcionesIzquierdo.add(comboConfInicial);

        panelOpcionesIzquierdo.add(textoCondicionFrontera);
        panelOpcionesIzquierdo.add(comboCondFrontera);

        panelOpcionesIzquierdo.add(textoGeneraciones);
        panelOpcionesIzquierdo.add(spinnerGeneraciones);


    }

    public void anadirBotonSimular(JFrame frameOpciones){

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridx = 0;          // Columna que ocupará desde arriba izquierda
        gbc.weightx = 1.0;      // Porcentaje sobre el eje x (ancho) que ocupará
        gbc.weighty = 0.1;      // Porcentaje sobre el eje y (alto) que ocupará
        gbc.gridy = 1;          // Fila que ocupara desde arriba izquierda
        gbc.gridheight = 1;     // Nº de filas que ocupará verticalmente
        gbc.insets = new Insets(0,0,0,0);

        // Añadimos el panel con el boton de simular
        JPanel panelBotonSimular = new JPanel();
        panelBotonSimular.setLayout(new GridLayout(3,3));
        panelBotonSimular.setBackground(Color.RED);

        // Boton que permitira realizar la simulación
        JButton botonSimular = new JButton("Simular");

        // Añadimos el boton al panel
        panelBotonSimular.add(new JLabel(""));
        panelBotonSimular.add(new JLabel(""));
        panelBotonSimular.add(new JLabel(""));
        panelBotonSimular.add(new JLabel(""));
        panelBotonSimular.add(botonSimular);
        panelBotonSimular.add(new JLabel(""));
        panelBotonSimular.add(new JLabel(""));
        panelBotonSimular.add(new JLabel(""));


        // Anadimos el panel del boton simular al frame de opciones
        frameOpciones.add(panelBotonSimular, gbc);
    }

    public void crearVentana() throws IOException {

        int anchoFrame = 600;
        int altoFrame = 750;

        frameOpciones = new JFrame("GUI Opciones");

        // Creamos el grid principal del frame
        frameOpciones.getContentPane().setLayout(new GridBagLayout());

        // Anadimos las opciones
        anadirOpciones(frameOpciones);

        // Anadimos el boton simular
        anadirBotonSimular(frameOpciones);

        // Añadimos algunas opciones
        frameOpciones.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameOpciones.setPreferredSize(new Dimension(anchoFrame, altoFrame));
        frameOpciones.setMinimumSize(new Dimension(anchoFrame, altoFrame));
        frameOpciones.setResizable(false);
        frameOpciones.setLocationRelativeTo(null);
        frameOpciones.pack();
        frameOpciones.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        new ca1DSimNuevo().crearVentana();
    }
}
