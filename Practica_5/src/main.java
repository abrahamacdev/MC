import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

public class main {

    private int k = 2;
    private int r = 1;
    private int numGeneraciones = 1000;
    private int numCelulas = 1024;

    public static final int REGLA_ESCOGIDA = 531;

    public static final int TAM_CLAVE = 512;    // Nº de bits que compondrán la clave


    private JFrame framePrincipal;

    // Descifrado
    private JTextArea textAreaClaveDescifrado;
    private JTextArea textAreaCriptograma;
    private JTextArea textAreaTextoDescifrado;

    // Cifrado
    private JTextArea textAreaClaveCifrado;
    private JTextArea textAreaTextoACifrar;
    private JTextArea textAreaTextoCifrado;




    // ----- Estudio de reglas -----
    private void guardarMejoresReglas(ArrayList<Integer> mejoresReglas){

        try {

            BufferedWriter buffer = new BufferedWriter(new FileWriter("reglas.txt"));

            for (int regla : mejoresReglas){
                buffer.write(String.valueOf(regla));
                buffer.newLine();
            }

            buffer.flush();
            buffer.close();

        } catch (Exception e){
            System.out.println(e);
        }
    }

    private double media(int[] arr){
        double sum = 0;

        for (int i : arr) sum+=i;

        return sum / arr.length;
    }

    private double media(double[] arr){
        double sum = 0;

        for (double i : arr) sum+=i;

        return sum / arr.length;
    }

    private boolean reglaValida(ca1DSim simulador){

        double mediaHamming = media(simulador.getHamming());
        double mediaEntropiaEspacial = media(simulador.getEntropiaEspacial());
        double entropiaTemporal = simulador.getEntropiaCelulaObservada();

        return mediaHamming > 300 && mediaEntropiaEspacial > 0.8 && entropiaTemporal > 0.8;
    }

    public void calcularMejoresReglas(int reglaMax) throws InstantiationException {

        ArrayList<Integer> reglasPasanRestricciones = new ArrayList<>();

        // Probamos reglas desde (0,reglaMax)
        for (int i=0; i<reglaMax; i++){

            if ((i+1) % 100 == 0) System.out.println("Regla " + (i+1));

            ca1DSim simulador = new ca1DSim(k, r, i, numGeneraciones, numCelulas);

            // Evolucionamos el autómata
            while (!simulador.haTerminado()) simulador.evoluciona();

            // Si la regla pasa las restricciones, la guardamos
            if (reglaValida(simulador)) reglasPasanRestricciones.add(i);
        }

        // Guardamos el listado con las mejores reglas
        guardarMejoresReglas(reglasPasanRestricciones);
    }

    public void testearRegla(int regla) throws InstantiationException {

        ca1DSim simulador = new ca1DSim(k, r, regla, numGeneraciones, numCelulas);

        // Evolucionamos el autómata
        while (!simulador.haTerminado()) simulador.evoluciona();

        System.out.println(media(simulador.getHamming()));
        System.out.println(media(simulador.getEntropiaEspacial()));
        System.out.println(simulador.getEntropiaCelulaObservada());
    }
    // -------------------------------------



    // ----- Cifrado -----
    public String cifrar(String clave, String texto){

        int[] bitsClave = string2bits(clave);       // Bits que forman la clave en bruto
        int[] bitsClaveFinal = new int[TAM_CLAVE];  // Bits que forman la clave que se usará finalmente para cifrar
        int[] bitsTexto = string2bits(texto);       // Bits que forman el texto a cifrar

        int[] bitsMensajeCifrado = new int[bitsTexto.length];   // Bits que forman el texto cifrado

        // Si la clave es muy larga, cogemos los primeros 512 bits, si es muy corta, lo que se pueda
        if (bitsClave.length > TAM_CLAVE) bitsClaveFinal = Arrays.copyOf(bitsClave, 512);
        else System.arraycopy(bitsClave, 0, bitsClaveFinal, 0, bitsClave.length);

        ca1DSim simulador = null;

        try {
            simulador = new ca1DSim(k, r, REGLA_ESCOGIDA, bitsTexto.length, bitsClaveFinal);
        } catch (Exception e) {
            System.out.println("No se ha podido realizar el cifrado");
        }

        // Ciframos el mensaje
        return ejecutarSimulador(bitsTexto, bitsMensajeCifrado, simulador);
    }
    public String descifrar(String clave, String criptograma){

        int[] bitsClave = string2bits(clave);           // Bits que forman la clave en bruto
        int[] bitsClaveFinal = new int[TAM_CLAVE];      // Bits que forman la clave que se usará finalmente para descifrar
        int[] bitsCriptograma = string2bits(criptograma);     // Bits que forman el criptograma

        int[] bitsMensajeDescifrado = new int[bitsCriptograma.length];   // Bits que forman el texto descifrado

        // Si la clave es muy larga, cogemos los primeros 512 bits, si es muy corta, lo que se pueda
        if (bitsClave.length > TAM_CLAVE) bitsClaveFinal = Arrays.copyOf(bitsClave, 512);
        else System.arraycopy(bitsClave, 0, bitsClaveFinal, 0, bitsClave.length);

        ca1DSim simulador = null;

        try {
            simulador = new ca1DSim(k, r, REGLA_ESCOGIDA, bitsCriptograma.length, bitsClaveFinal);
        } catch (Exception e) {
            System.out.println("No se ha podido realizar el descifrado");
        }

        // Ciframos el mensaje
        return ejecutarSimulador(bitsCriptograma, bitsMensajeDescifrado, simulador);
    }

    private String ejecutarSimulador(int[] bitsCriptograma, int[] bitsMensajeDescifrado, ca1DSim simulador) {

        if (simulador != null) {

            int i=0;
            while (!simulador.haTerminado()){

                simulador.evoluciona();

                // XOR entre el valor de la célula central y el correspondiente bit del mensaje
                int celulaCentral = simulador.getCelulaObsertada();
                bitsMensajeDescifrado[i] = bitsCriptograma[i] ^ celulaCentral;

                i++;
            }
        }

        return bits2string(bitsMensajeDescifrado);
    }

    private int[] string2bits(String s){

        byte[] bytes = s.getBytes();
        BitSet b = BitSet.valueOf(bytes);

        int numBits = bytes.length * 8;

        // Inicializamos el vector con los bits
        int[] bits = new int[numBits];

        // Buscamos los bits que estén a 1
        int bitActual = 0;
        int diff = 0;
        for (int i=0; i<numBits; i++){

            // Pasamos al siguiente bloque de 8 bits
            if (i != 0 && i % 8 == 0){
                bitActual++;
                diff = 0;
            }

            // Hay un 1 en el índice i del bitset
            if (b.get(i)) {
                int posPrimerBit = (bitActual+1) * 8 - 1;
                bits[posPrimerBit - diff] = 1;
            }

            diff++;
        }

        return bits;
    }

    private String bits2string(int[] bits){

        byte[] bytes = new byte[bits.length / 8];

        int byteActual = 0;
        byte acumulador = 0;
        byte potenciaActual = 7;
        for (int i=0; i<bits.length + 1; i++){

            // Pasamos a procesar el siguiente byte, guardamos el actual
            if (i != 0 && i % 8 == 0){

                bytes[byteActual] = acumulador;
                byteActual++;
                acumulador = 0;
                potenciaActual = 7;
            }

            // El bit esta a 1, sumamos al acumulador
            if (i < bits.length && bits[i] == 1){
                acumulador += (byte) Math.pow(2, potenciaActual);
            }

            potenciaActual--;
        }


        return new String(bytes, StandardCharsets.UTF_8);
    }
    // --------------------------------------



    // ----- GUI -----

    private void anadirPanelDescifrado(){

        int margenSuperiorLabels = 50;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 0.9;
        gbc.weightx = 0.5;

        JPanel panelDescifrado = new JPanel();
        panelDescifrado.setBorder(new EmptyBorder(20, 30, 0, 30));
        //panelDescifrado.setBackground(Color.RED);
        panelDescifrado.setLayout(new GridLayout(7, 1));

        // Label clave y TextArea clave
        JLabel labelClaveDescifrado = new JLabel("Introduzca la clave de descifrado");
        labelClaveDescifrado.setBorder(new EmptyBorder(margenSuperiorLabels, 0, 0, 0));
        textAreaClaveDescifrado = new JTextArea();
        JScrollPane scrollTextAreaClaveDescifrado = new JScrollPane (textAreaClaveDescifrado,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        // Label texto y TextArea texto
        JLabel labelCriptograma = new JLabel("Introduzca el texto a descifrar");
        labelCriptograma.setBorder(new EmptyBorder(margenSuperiorLabels, 0, 0, 0));
        textAreaCriptograma = new JTextArea();
        JScrollPane scrollTextAreaCriptograma = new JScrollPane (textAreaCriptograma,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Boton descifrar
        JPanel panelBotonDescifrar = new JPanel();
        //panelBotonDescifrar.setBackground(Color.WHITE);
        panelBotonDescifrar.setLayout(new GridLayout(2,1));
        JButton botonDescifrar = new JButton("Descifrar");
        botonDescifrar.addActionListener(actionEvent -> {

            // Obtenemos clave y texto a cifrar
            String clave = textAreaClaveDescifrado.getText();
            String criptograma = textAreaCriptograma.getText();

            // Ciframos y mostramos
            String textoBruto = descifrar(clave, criptograma);
            textAreaTextoDescifrado.setText(textoBruto);
        });
        panelBotonDescifrar.add(new JLabel(""));
        panelBotonDescifrar.add(botonDescifrar);

        // TextArea Texto descifrado
        textAreaTextoDescifrado = new JTextArea();
        textAreaTextoDescifrado.setEditable(false);
        JScrollPane scrollTextAreaTextoDescifrado = new JScrollPane (textAreaTextoDescifrado,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Clave
        panelDescifrado.add(labelClaveDescifrado);
        panelDescifrado.add(scrollTextAreaClaveDescifrado);

        // Criptograma
        panelDescifrado.add(labelCriptograma);
        panelDescifrado.add(scrollTextAreaCriptograma);

        // BOton descifrar
        panelDescifrado.add(panelBotonDescifrar);

        // Texto descifrado
        panelDescifrado.add(new JLabel());
        panelDescifrado.add(scrollTextAreaTextoDescifrado);

        framePrincipal.add(panelDescifrado, gbc);

    }
    private void anadirPanelCifrado(){

        int margenSuperiorLabels = 50;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 0.9;
        gbc.weightx = 1;

        JPanel panelCifrado = new JPanel();
        panelCifrado.setBorder(new EmptyBorder(20, 30, 0, 30));
        //panelDescifrado.setBackground(Color.RED);
        panelCifrado.setLayout(new GridLayout(7, 1));

        // Label clave y TextArea clave
        JLabel labelClaveCifrado = new JLabel("Introduzca la clave de cifrado");
        labelClaveCifrado.setBorder(new EmptyBorder(margenSuperiorLabels, 0, 0, 0));
        textAreaClaveCifrado = new JTextArea();
        JScrollPane scrollTextAreaClaveCifrado = new JScrollPane (textAreaClaveCifrado,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        // Label texto y TextArea texto
        JLabel labelTextoACifrar = new JLabel("Introduzca el texto a cifrar");
        labelTextoACifrar.setBorder(new EmptyBorder(margenSuperiorLabels, 0, 0, 0));
        textAreaTextoACifrar = new JTextArea();
        JScrollPane scrollTextAreaTextoACifrar = new JScrollPane (textAreaTextoACifrar,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Boton descifrar
        JPanel panelBotonCifrar = new JPanel();
        //panelBotonDescifrar.setBackground(Color.WHITE);
        panelBotonCifrar.setLayout(new GridLayout(2,1));
        JButton botonCifrar = new JButton("Cifrar");
        botonCifrar.addActionListener(actionEvent -> {

            // Obtenemos clave y texto a cifrar
            String clave = textAreaClaveCifrado.getText();
            String textoACifrar = textAreaTextoACifrar.getText();

            // Ciframos y mostramos
            String criptograma = cifrar(clave, textoACifrar);
            textAreaTextoCifrado.setText(criptograma);
        });
        panelBotonCifrar.add(new JLabel(""));
        panelBotonCifrar.add(botonCifrar);

        // TextArea Texto cifrado
        textAreaTextoCifrado = new JTextArea();
        textAreaTextoCifrado.setEditable(false);
        JScrollPane scrollTextAreaTextoCifrado = new JScrollPane (textAreaTextoCifrado,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Clave
        panelCifrado.add(labelClaveCifrado);
        panelCifrado.add(scrollTextAreaClaveCifrado);

        // Texto a cifrar
        panelCifrado.add(labelTextoACifrar);
        panelCifrado.add(scrollTextAreaTextoACifrar);

        // Boton cifrar
        panelCifrado.add(panelBotonCifrar);

        // Texto cifrado
        panelCifrado.add(new JLabel());
        panelCifrado.add(scrollTextAreaTextoCifrado);

        framePrincipal.add(panelCifrado, gbc);
    }
    private void anadirPanelOpciones(){

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 0.2;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);

        JPanel panelOpciones = new JPanel();
        //panelOpciones.setBackground(Color.ORANGE);
        panelOpciones.setLayout(new GridLayout(3, 5));

        JButton botonLimpiar = new JButton("Limpiar");
        botonLimpiar.addActionListener(actionEvent -> {

            // Cifrado
            textAreaClaveCifrado.setText("");
            textAreaTextoACifrar.setText("");
            textAreaTextoCifrado.setText("");

            // Descifrado
            textAreaClaveDescifrado.setText("");
            textAreaCriptograma.setText("");
            textAreaTextoDescifrado.setText("");
        });

        panelOpciones.add(new JLabel());
        panelOpciones.add(new JLabel());
        panelOpciones.add(new JLabel());
        panelOpciones.add(new JLabel());
        panelOpciones.add(botonLimpiar);
        panelOpciones.add(new JLabel());
        panelOpciones.add(new JLabel());


        framePrincipal.add(panelOpciones, gbc);
    }

    public void crearVentana() {

        //int anchoFrame = 1000;
        //int altoFrame = 850;

        int anchoFrame = 550;
        int altoFrame = 500;

        framePrincipal = new JFrame("GUI");

        // Creamos el grid principal del frame
        framePrincipal.getContentPane().setLayout(new GridBagLayout());

        // Panel para cifrar
        anadirPanelCifrado();

        // Panel para descifrar
        //anadirPanelDescifrado();

        // Panel para limpiar
        anadirPanelOpciones();

        //
        framePrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        framePrincipal.setMinimumSize(new Dimension(anchoFrame, altoFrame));
        framePrincipal.setResizable(false);
        framePrincipal.setLocationRelativeTo(null);
        framePrincipal.pack();
        framePrincipal.setVisible(true);
    }
    // ---------------




    public static void main(String[] args) throws InstantiationException {

        int reglaMax = 1000;

        main m = new main();
        m.crearVentana();

        // Para filtrar las mejores reglas
        //m.calcularMejoresReglas(reglaMax);

    }
}
