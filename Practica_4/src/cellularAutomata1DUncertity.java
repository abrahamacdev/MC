import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class cellularAutomata1DUncertity {

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

    private JSpinner spinnerNumVecinos;

    // Nucleo del automata
    private ca1DSim simulador;


    final static String[] OPCIONES_INICIALIZACION_AUTOMATA = new String[]{"Aleatoria", "Célula Central Activa"};
    final static String[] OPCIONES_NUM_ESTADOS_AUTOMATA = new String[]{"2", "3", "4", "5"};
    final static String[] CONDICION_FRONTERA_AUTOMATA = new String[]{"Nula", "Cilíndrica"};

    private final Color[][] colores = new Color[][] {
            {Color.white, Color.black},
            {Color.white, Color.lightGray, Color.black},
            {Color.red, Color.green, Color.blue, Color.yellow},
            {Color.red, Color.green, Color.blue, Color.yellow, Color.orange},
    };



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
        panelOpciones.setLayout(new GridBagLayout());

        GridBagConstraints gbcPanelOpciones = new GridBagConstraints();
        gbcPanelOpciones.fill = GridBagConstraints.BOTH;
        gbcPanelOpciones.anchor = GridBagConstraints.FIRST_LINE_START;
        gbcPanelOpciones.gridy = 0;          // Fila que ocupara desde arriba izquierda
        gbcPanelOpciones.gridx = 0;          // Columna que ocupará desde arriba izquierda
        gbcPanelOpciones.gridheight = 1;     // Nº de celdas en la columna
        gbcPanelOpciones.gridwidth = 1;      // Nº de celdas en la fila
        gbcPanelOpciones.weightx = 0.45;      // Porcentaje sobre el eje x (ancho) que ocupará
        gbcPanelOpciones.weighty = 0.5;      // Porcentaje sobre el eje y (alto) que ocupará
        gbcPanelOpciones.insets = new Insets(5,5,5,5);


        // Panel Izquierdo
        JPanel panelOpcionesIzquierdo = new JPanel();
        panelOpcionesIzquierdo.setLayout(new GridLayout(10, 1));
        panelOpciones.add(panelOpcionesIzquierdo, gbcPanelOpciones);

        // Separacion
        JPanel separadorOpciones = new JPanel();

        gbcPanelOpciones.gridx = 1;                 // Columna que ocupará desde arriba izquierda
        gbcPanelOpciones.weightx = 0.1;      // Porcentaje sobre el eje x (ancho) que ocupará

        panelOpciones.add(separadorOpciones, gbcPanelOpciones);

        // Panel Derecho
        JPanel panelOpcionesDerecho = new JPanel();
        panelOpcionesDerecho.setLayout(new GridLayout(10, 1));

        gbcPanelOpciones.gridx = 3;                 // Columna que ocupará desde arriba izquierda
        gbcPanelOpciones.weightx = 0.45;      // Porcentaje sobre el eje x (ancho) que ocupará
        panelOpciones.add(panelOpcionesDerecho, gbcPanelOpciones);

        // Añadimos padding a los paneles
        EmptyBorder bordesPaneles = new EmptyBorder(0, 15, 20, 15);
        panelOpcionesIzquierdo.setBorder(bordesPaneles);
        panelOpcionesIzquierdo.setBorder(bordesPaneles);

        // Añadimos las opciones
        anadirBotonesOpciones(panelOpcionesIzquierdo, panelOpcionesDerecho);

        // Anadimos el panel de opciones al frame
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
        spinnerRegla.setValue(54);
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
        spinnerGeneraciones.setValue(300);

        // Nº células
        JLabel textoNumCelulas = new JLabel("Número de Células:");
        textoNumCelulas.setBorder(new EmptyBorder(15, 0, 0, 0));
        spinnerNumCelulas = new JSpinner();
        spinnerNumCelulas.setValue(400);

        // Checkbox Dist. Hamming
        checkBoxHamming = new JCheckBox("Calcular distancia de Hamming");
        checkBoxHamming.setBorder(new EmptyBorder(15, 4, 0, 0));

        // Checkbox Entropía Espacial
        checkBoxEntropiaEspacial = new JCheckBox("Calcular entropía espacial");

        // Checkbox Entropía Temporal Célula
        checkBoxEntropiaTempCelula = new JCheckBox("Calcular entropía temporal (célula individual)");

        // Spinner célula observada
        JLabel textoCelulaObservada = new JLabel("Célula");
        textoCelulaObservada.setBorder(new EmptyBorder(5, 0, 0, 0));
        spinnerCelulaObservada = new JSpinner();
        spinnerCelulaObservada.setValue(((int)spinnerNumCelulas.getValue()) / 2);

        // Spinner regla
        JLabel textoVecinos = new JLabel("Radio de Vecinos");
        textoVecinos.setBorder(new EmptyBorder(5, 0, 0, 0));
        spinnerNumVecinos = new JSpinner();
        spinnerNumVecinos.setName("vec");
        spinnerNumVecinos.setValue(1);

        // Sólo número naturales en los spinners
        ChangeListener listenerSpinners = changeEvent -> {

            JSpinner afectado = ((JSpinner) changeEvent.getSource());
            int valor = (int) afectado.getValue();
            String nombre = afectado.getName();

            // Para el número de vecinos
            if (nombre != null && nombre.equals("vec")){

                // Evitamos valores menores a 1
                if (valor < 1){
                    afectado.setValue(1);
                }

                // Evitamos valores iguales o mayores que la mitad del número de células
                else if (valor >= (int) spinnerNumCelulas.getValue() / 2){
                    afectado.setValue(((int) spinnerNumCelulas.getValue() / 2) - 1);
                }
            }

            else {
                // Evitamos que se puedan introducir números negativos
                if (valor < 0){
                    afectado.setValue(0);
                }
            }

        };

        // Añadimos los listeners a los spinners
        spinnerRegla.addChangeListener(listenerSpinners);
        spinnerCelulaObservada.addChangeListener(listenerSpinners);
        spinnerGeneraciones.addChangeListener(listenerSpinners);
        spinnerNumCelulas.addChangeListener(listenerSpinners);
        spinnerNumVecinos.addChangeListener(listenerSpinners);


        // Añadimos algunas opciones al panel izquierdo
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


        // Añadimos al panel derecho el resto
        panelOpcionesDerecho.add(textoNumCelulas);
        panelOpcionesDerecho.add(spinnerNumCelulas);

        panelOpcionesDerecho.add(checkBoxHamming);
        panelOpcionesDerecho.add(checkBoxEntropiaEspacial);
        panelOpcionesDerecho.add(checkBoxEntropiaTempCelula);

        panelOpcionesDerecho.add(textoCelulaObservada);
        panelOpcionesDerecho.add(spinnerCelulaObservada);

        panelOpcionesDerecho.add(textoVecinos);
        panelOpcionesDerecho.add(spinnerNumVecinos);
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
        //panelBotonSimular.setBackground(Color.RED);

        // Boton que permitira realizar la simulación
        JButton botonSimular = new JButton("Simular");
        botonSimular.addActionListener(actionEvent -> ejecutarSimulacion());

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

    public void anadirGraficaImagen(){

        JPanel graficaImagen = new JPanel(){
            @Override
            public void paint(Graphics graphics) {

                int anchoPanel = getWidth();
                int altoPanel = getHeight();

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

                ArrayList<int[]> historial = simulador.getHistorialGeneraciones();

                // Pintamos el automata
                for (int j=0; j<nGeneraciones; j++){

                    int[] celulasGenActual = historial.get(j);
                    int nGenActual = j;
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
                }

                graphics.drawImage(bufferedImage, 0, 0, this);
            }
        };

        // Añadimos la imagen del automata
        frameGraficas.add(graficaImagen);
    }

    public void anadirGraficaDistHamming(){

        JPanel panelControladorHamming = new JPanel();
        panelControladorHamming.setLayout(new GridBagLayout());

        JPanel graficaDstHamming = new JPanel(){
            @Override
            public void paint(Graphics graphics) {

                int ancho = getWidth();
                int alto = getHeight();

                BufferedImage bufferedImage = new BufferedImage(ancho, alto,BufferedImage.TYPE_INT_RGB);
                Graphics2D g = (Graphics2D) bufferedImage.getGraphics();

                // Pintamos un marco blanco como fondo
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, ancho, alto);

                // Color de la línea de la gráfica
                g.setColor(Color.RED);
                g.setStroke(new BasicStroke(2));

                int[] hamming = simulador.getHamming();

                int anchoLinea = ancho / hamming.length;



                // Pintamos la curva de la entropia espacial
                for (int i=0; i<hamming.length-1; i++){

                    int x0 = i * anchoLinea;
                    int x1 = (i+1) * anchoLinea;
                    int y0 = alto - hamming[i] - 1;
                    int y1 = alto - hamming[i + 1] - 1;


                    // Pintamos la linea
                    g.drawLine(x0, y0, x1, y1);
                }

                graphics.drawImage(bufferedImage, 0, 0, this);
            }
        };


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridy = 0;          // Fila que ocupara desde arriba izquierda
        gbc.gridx = 0;          // Columna que ocupará desde arriba izquierda
        gbc.gridheight = 1;     // Nº de filas que ocupará verticalmente
        gbc.weightx = 1.0;      // Porcentaje sobre el eje x (ancho) que ocupará
        gbc.weighty = 0.9;      // Porcentaje sobre el eje y (alto) que ocupará
        gbc.insets = new Insets(0,0,0,0);


        panelControladorHamming.add(graficaDstHamming, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.1;


        JPanel panelControladorTextoHamming = new JPanel();
        panelControladorTextoHamming.setBackground(Color.WHITE);
        panelControladorTextoHamming.setLayout(new GridLayout(1, 1));

        JLabel textoHamming = new JLabel("Curva Distancia Hamming");
        textoHamming.setHorizontalAlignment(SwingConstants.CENTER);

        panelControladorTextoHamming.add(textoHamming);
        panelControladorHamming.add(panelControladorTextoHamming, gbc);


        // Añadimos la imagen del automata
        frameGraficas.add(panelControladorHamming);
    }

    public void anadirGraficaEntropiaEspacial(){

        JPanel panelControladorEntropiaEspacial = new JPanel();
        panelControladorEntropiaEspacial.setLayout(new GridBagLayout());

        JPanel graficaEntropiaEspacial = new JPanel(){
            @Override
            public void paint(Graphics graphics) {

                int ancho = getWidth();
                int alto = getHeight();

                BufferedImage bufferedImage = new BufferedImage(ancho, alto,BufferedImage.TYPE_INT_RGB);
                Graphics2D g = (Graphics2D) bufferedImage.getGraphics();

                // Pintamos un marco blanco como fondo
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, ancho, alto);

                // Color de la línea de la gráfica
                g.setColor(Color.BLUE);
                g.setStroke(new BasicStroke(2));

                double[] entropiaEspacial = simulador.getEntropiaEspacial();

                int anchoLinea = ancho / entropiaEspacial.length;

                // Pintamos la curva de la entropia espacial
                for (int i=0; i<entropiaEspacial.length-1; i++){

                    int x0 = i * anchoLinea;
                    int x1 = (i+1) * anchoLinea;
                    int y0 = (int) (alto - entropiaEspacial[i] * alto);
                    int y1 = (int) (alto - entropiaEspacial[i + 1] * alto);

                    // Evitamos que la linea se esconda
                    if (y0 > alto) y0 = alto-1;
                    else if (y0 < 1) y0 = 2;
                    if (y1 > alto) y1 = alto-1;
                    else if (y1 < 1) y1 = 2;

                    //System.out.println("X0: " + x0 + ", X1: " + x1 + ", Y0: " + y0 + ", Y1: " + y1);

                    // Pintamos la linea
                    g.drawLine(x0, y0, x1, y1);
                }

                graphics.drawImage(bufferedImage, 0, 0, this);
            }
        };
        graficaEntropiaEspacial.setBorder(new EmptyBorder(0, 10,0 ,10 ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridy = 0;          // Fila que ocupara desde arriba izquierda
        gbc.gridx = 0;          // Columna que ocupará desde arriba izquierda
        gbc.gridheight = 1;     // Nº de filas que ocupará verticalmente
        gbc.weightx = 1.0;      // Porcentaje sobre el eje x (ancho) que ocupará
        gbc.weighty = 0.9;      // Porcentaje sobre el eje y (alto) que ocupará
        gbc.insets = new Insets(0,0,0,0);


        panelControladorEntropiaEspacial.add(graficaEntropiaEspacial, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.1;


        JPanel panelTextoEntropiaEspacial = new JPanel();
        panelTextoEntropiaEspacial.setBackground(Color.WHITE);
        panelTextoEntropiaEspacial.setLayout(new GridLayout(1, 1));

        JLabel textoEntropiaEspacial = new JLabel("Curva Entropía Espacial");
        textoEntropiaEspacial.setHorizontalAlignment(SwingConstants.CENTER);

        panelTextoEntropiaEspacial.add(textoEntropiaEspacial);
        panelControladorEntropiaEspacial.add(panelTextoEntropiaEspacial, gbc);


        // Añadimos la imagen del automata
        frameGraficas.add(panelControladorEntropiaEspacial);

    }

    public void anadirGraficaEntropiaTemporalCelula(){

        JPanel panelControladorEntropiaTemporal = new JPanel();
        panelControladorEntropiaTemporal.setLayout(new GridBagLayout());


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridy = 0;          // Fila que ocupara desde arriba izquierda
        gbc.gridx = 0;          // Columna que ocupará desde arriba izquierda
        gbc.gridheight = 1;     // Nº de filas que ocupará verticalmente
        gbc.weightx = 1.0;      // Porcentaje sobre el eje x (ancho) que ocupará
        gbc.weighty = 1.0;      // Porcentaje sobre el eje y (alto) que ocupará

        JPanel panelLabelTextoEntropiaTemporal = new JPanel();
        panelLabelTextoEntropiaTemporal.setBackground(Color.WHITE);
        panelLabelTextoEntropiaTemporal.setLayout(new GridBagLayout());

        JLabel textoCurvaEntropiaTemporal = new JLabel(String.format("%.4f", (float) simulador.getEntropiaCelulaObservada()));
        textoCurvaEntropiaTemporal.setHorizontalAlignment(SwingConstants.CENTER);
        textoCurvaEntropiaTemporal.setFont(new Font("serif", Font.BOLD, 24));
        textoCurvaEntropiaTemporal.setForeground(Color.GREEN);

        // Añadimos el panel con el valor de la entropia temporal
        panelLabelTextoEntropiaTemporal.add(textoCurvaEntropiaTemporal, gbc);
        gbc.weighty = 0.9;
        panelControladorEntropiaTemporal.add(panelLabelTextoEntropiaTemporal, gbc);

        // Añadimos el panel con el texto de que se muestra aquí
        JPanel panelLabelEntropiaTemporal = new JPanel();
        panelLabelEntropiaTemporal.setBackground(Color.WHITE);
        panelLabelEntropiaTemporal.setLayout(new GridBagLayout());

        // Label del panel
        JLabel labelEntropiaTemporal = new JLabel("Entropía Temporal Célula Observada");
        labelEntropiaTemporal.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.weighty = 1.0;
        panelLabelEntropiaTemporal.add(labelEntropiaTemporal, gbc);
        gbc.weighty = 0.1;
        gbc.gridy = 1;
        panelControladorEntropiaTemporal.add(panelLabelEntropiaTemporal, gbc);

        // Añadimos la imagen del automata
        frameGraficas.add(panelControladorEntropiaTemporal, gbc);
    }

    private void anadirGraficas(){

        int anchoFrame = 1600;
        int altoFrame = 640;

        // Destruimos los resultados anteriores
        if (frameGraficas != null){
            frameGraficas.setVisible(false);
            frameGraficas.dispose();
        }

        frameGraficas = new JFrame("Resultados");

        // Añadimos todas las columnas necesarias
        int columnas = 1;
        if (simulador.isCalcularHamming()) columnas++;
        if (simulador.isCalcularEntropiaEspacial()) columnas++;
        if (simulador.isCalcularEntropiaTemporalCelula()) columnas++;

        // Creamos el grid principal del frame
        frameGraficas.getContentPane().setLayout(new GridLayout(1, columnas));

        // Añadimos las graficas
        anadirGraficaImagen();
        if (simulador.isCalcularHamming()) anadirGraficaDistHamming();
        if (simulador.isCalcularEntropiaEspacial()) anadirGraficaEntropiaEspacial();
        if (simulador.isCalcularEntropiaTemporalCelula()) anadirGraficaEntropiaTemporalCelula();

        // Añadimos algunas opciones
        frameGraficas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameGraficas.setPreferredSize(new Dimension(anchoFrame, altoFrame));
        frameGraficas.setMinimumSize(new Dimension(anchoFrame, altoFrame));
        frameGraficas.setResizable(true);
        frameGraficas.setLocationRelativeTo(null);
        frameGraficas.pack();
        frameGraficas.setVisible(true);
    }

    private void ejecutarSimulacion(){

        int numEstados = comboNumEstadosCelulas.getSelectedIndex() + 2;
        int regla = (int) spinnerRegla.getValue();
        int radioVecinos = (int) spinnerNumVecinos.getValue();
        ca1DSim.CONFIGURACION_INICIALIZACION_AUTOMATA configuracionInicial = comboConfInicial.getSelectedIndex() == 0 ? ca1DSim.CONFIGURACION_INICIALIZACION_AUTOMATA.ALEATORIA : ca1DSim.CONFIGURACION_INICIALIZACION_AUTOMATA.CELULA_CENTRAL_ACTIVA;
        ca1DSim.CONFIGURACION_CONDICION_FRONTERA configuracionCondicionFrontera = comboCondFrontera.getSelectedIndex() == 0 ? ca1DSim.CONFIGURACION_CONDICION_FRONTERA.NULA : ca1DSim.CONFIGURACION_CONDICION_FRONTERA.CILINDRICA;
        int numGeneraciones = (int) spinnerGeneraciones.getValue();
        int numCelulas = (int) spinnerNumCelulas.getValue();
        boolean dstHamming = checkBoxHamming.isSelected();
        boolean entropiaEspacial = checkBoxEntropiaEspacial.isSelected();
        boolean entropiaTemporalCelula = checkBoxEntropiaTempCelula.isSelected();
        int indxCelulaObservada = (int) spinnerCelulaObservada.getValue();

        try {
            simulador = new ca1DSim(numEstados, radioVecinos, regla, configuracionInicial, configuracionCondicionFrontera, numGeneraciones, numCelulas, dstHamming, entropiaEspacial, entropiaTemporalCelula, indxCelulaObservada);

            // Realizamos la simulacion
            while (!simulador.haTerminado()) simulador.evoluciona();

            // Mostramos el resultado
            anadirGraficas();

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }


    public void crearVentana() throws IOException {

        int anchoFrame = 550;
        int altoFrame = 600;

        frameOpciones = new JFrame("Opciones");

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
        new cellularAutomata1DUncertity().crearVentana();
    }
}
