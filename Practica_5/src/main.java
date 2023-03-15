import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

public class main {

    private int k = 2;
    private int r = 10;
    private int numGeneraciones = 1000;
    private int numCelulas = 1024;



    private int REGLA_ESCOGIDA = 531;



    // ----- Calculo de mejores reglas -----
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



        return null;
    }
    public String descifrar(){
        return null;
    }
    // --------------------------------------





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




    public static void main(String[] args) throws InstantiationException {

        int reglaMax = 1000;

        //calcularMejoresReglas(reglaMax);

        main m = new main();
    }
}
