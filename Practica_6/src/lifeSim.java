import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class lifeSim {

    public enum ESTADO_INICIAL {
        ALEATORIO,
        ISLAS,
        CANIONES
    }

    public class planeador {

        public int direccion_x;
        public int direccion_y;
        public int centro;

        public planeador(int direccion_x, int direccion_y, int centro){
            this.direccion_x = direccion_x;
            this.direccion_y = direccion_y;
            this.centro = centro;
        }
    }

    private static final double PROB_CELULAS_ALEATORIAS = 0.1;
    private static final double PROB_NUM_ISLAS = 0.2;
    private static final int RADIO_ISLA = 5;

    private static final int NUM_CANIONES = 4;
    private AbstractMap.SimpleEntry<Integer, Integer>[] centrosPlaneadores = new AbstractMap.SimpleEntry[NUM_CANIONES];


    // Matriz con los valores de la generación actual
    private int[][] generacionActual;

    // Nº de generación actual
    private int nGeneracionActual;

    // Nº total de generaciones
    private int numGeneraciones;

    // Nº de bacterias vivas de la generación actual
    private int[] historicoPoblacionActiva = null;

    // Estado con el que inicializamos el simulador
    private ESTADO_INICIAL estadoInicial;

    // Necesario para el generador aleatorio
    private static long xGenerador_Randu = 1;

    public static void seed(long seed){
        xGenerador_Randu = seed;
    }


    public lifeSim(ESTADO_INICIAL estadoInicial, int numGeneraciones){
        this.numGeneraciones = numGeneraciones;
        this.estadoInicial = estadoInicial;
        this.historicoPoblacionActiva = new int[numGeneraciones];
        iniciar();
    }


    private double generadorRandu(){
        xGenerador_Randu = (((long) Math.pow(2, 16)) + 3) * xGenerador_Randu % ((long) Math.pow(2, 31));
        return xGenerador_Randu / Math.pow(2, 31);
    }

    private void iniciar(){

        int n_celulas = lifeSimGUI.TAMANIO_RETICULA / lifeSimGUI.FACTOR_CELULAS;

        // Inicializamos la generación actual
        this.generacionActual = new int[n_celulas][n_celulas];

        // Creamos la primera generación
        switch (estadoInicial){
            case ALEATORIO: inicializaAleatorio(); break;
            case ISLAS: inicializaIslas(); break;
            case CANIONES: inicializaCaniones(); break;
        }

        actualizaHistoricoPoblacion();
    }

    private void inicializaAleatorio(){

        // Generamos los puntos aleatorios
        for (int i=0; i<generacionActual.length; i++){
            for (int j=0; j<generacionActual.length; j++) {
                double rand = generadorRandu();

                if (rand <= PROB_CELULAS_ALEATORIAS) {
                    generacionActual[i][j] = 1;
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public int vecindadVonNeumann(int i, int j, int[][] generacionAnterior){

        int estadoAnterior = generacionAnterior[i][j];

        int vivas = 0;

        for (int k=i-1; k<=i+1; k++){
            for (int z=j-1; z<=j+1; z++) {

                // Comprobamos que no se sale de rango
                if (k >= 0 && z >= 0 && k < generacionActual.length && z < generacionActual.length){

                    // No contamos la celda i_j
                    if (k != i || j != z){

                        int valor = generacionAnterior[k][z];

                        // Evitamos procesar los planeadores
                        if (valor == 1){
                            vivas++;
                        }
                    }
                }
            }
        }

        // Por defecto está vacío
        int nuevoEstado = 0;


        if (estadoAnterior == 1){
            // Viva con menos de 2 vecinas vivas -> muere || sobrepoblacion -> muere
            if (vivas < 2 || vivas > 3) nuevoEstado = -1;

            // Viva y con 2 o 3 vecinas vivas -> vive
            else nuevoEstado = estadoAnterior;
        }

        // Punto vacío (murió una bacteria o no había ninguna) con 3 vecinas vivas -> nueva vida
        else if (vivas == 3) nuevoEstado = 1;



        return nuevoEstado;
    }

    private void evolucionAleatorio(){

        int[][] tempGen = new int[generacionActual.length][generacionActual.length];

        for (int i=0; i<tempGen.length; i++){
            for (int j=0; j<tempGen.length; j++){
                tempGen[i][j] = vecindadVonNeumann(i, j, generacionActual);
            }
        }

        // Guardamos la nueva generacion
        generacionActual = tempGen;
    }

    /**
     * Comprueba si está dentro del radio de una isla para marcarlo
     * @param centros
     * @param i
     * @param j
     * @return
     */
    private Map.Entry<Integer, Integer> centroDeLaIsla(ArrayList<Map.Entry<Integer, Integer>> centros, int i, int j, double radio){

        for (Map.Entry<Integer, Integer> centro : centros){
            int diffFilas = Math.abs(centro.getKey() - i);
            int diffColumnas = Math.abs(centro.getValue() - j);

            if (diffFilas + diffColumnas <= 2){
                return centro;
            }
        }
        return null;
    }

    private void inicializaIslas(){

        // Listado con los centros de las islas
        ArrayList<Map.Entry<Integer, Integer>> centrosIslas = new ArrayList<>();

        // Generamos los centros de las islas
        double num_centros = generacionActual.length * PROB_NUM_ISLAS;
        int max = generacionActual.length * generacionActual.length;
        for (int i=0; i<num_centros; i++){
            int fila = (int) (generadorRandu() * max) / generacionActual.length;
            int columna = (int) (generadorRandu() * max) / generacionActual.length;
            generacionActual[fila][columna] = 1;
            centrosIslas.add(new AbstractMap.SimpleEntry<>(fila, columna));
        }


        // TODO Modificar funcion #centroIslas
        // Rellenamos alrededor de los centros de las islas
        for (int i=0; i<generacionActual.length; i++){
            for (int j=0; j<generacionActual.length; j++) {
                double rand = generadorRandu();
                if (rand <= 0.5 && centroDeLaIsla(centrosIslas, i, j, RADIO_ISLA) != null) {
                    generacionActual[i][j] = 1;
                }
            }
        }

    }
    private void evolucionIslas(){

        int[][] tempGen = new int[generacionActual.length][generacionActual.length];

        for (int i=0; i<tempGen.length; i++){
            for (int j=0; j<tempGen.length; j++){
                tempGen[i][j] = vecindadVonNeumann(i, j, generacionActual);
            }
        }

        // Guardamos la nueva generacion
        generacionActual = tempGen;
    }

    private void pintaCanion(int col, int fila){

        // Primer cuadrado
        for (int i=0;i<2;i++){
            for (int j=0; j<2; j++){
                generacionActual[col+i][fila+j] = 1;
            }
        }
        col += 1;

        // Espacio de 8
        col += 9;

        // Pintamos la estructura rara de la derecha del cuadrado izquierdo
        int filaCentro = fila+1;
        int colCentro = col+3;

        int filaInicio = fila - 2;
        int colInicio = col;
        for (int i=0; i<7; i++){
            for (int j=0; j<7; j++){
                int filaTemp = filaInicio + j;
                int colTemp = colInicio + i;

                // Evitamos pintar el centro
                if (filaTemp != filaCentro || colTemp != colCentro){

                    // Dibujamos el exterior del círculo
                    double dst = Math.sqrt(Math.pow(filaTemp - filaCentro,2) + Math.pow(colTemp - colCentro,2));
                    if (dst >= 2.5 && dst <= 3.5) {
                        generacionActual[colTemp][filaTemp] = 1;
                    }
                }
            }
        }

        // Añadimos los cambios del círculo
        generacionActual[colCentro+1][filaCentro] = 1;
        generacionActual[colCentro+4][filaCentro] = 1;
        generacionActual[colCentro+1][filaCentro-3] = 0;
        generacionActual[colCentro+1][filaCentro+3] = 0;

        col += 10;

        // Pintamos la cosa rara que está a la izquierda del cuadrado izquierdo
        for (int i=0; i<2; i++){
            for (int j=0; j<3; j++){
                generacionActual[col+i][fila-j] = 1;
            }
        }
        col += 2;
        generacionActual[col][fila-3] = 1;
        generacionActual[col][fila+1] = 1;

        col += 2;
        generacionActual[col][fila-3] = 1;
        generacionActual[col][fila-4] = 1;
        generacionActual[col][fila+1] = 1;
        generacionActual[col][fila+2] = 1;

        // Pintamos el espaciado
        col += 10;
        fila -= 1;

        // Segundo cuadrado
        for (int i=0;i<2;i++){
            for (int j=0; j<2; j++){
                generacionActual[col+i][fila-j] = 1;
            }
        }
    }

    private void inicializaCaniones(){

        int n  =generacionActual.length;
        int fila = n / 2 - (n/16);

        int columna = 0;
        for (int i = 0; i< NUM_CANIONES; i++){
            pintaCanion(columna, fila);
            columna += 45;
        }
    }

    private void evolucionCaniones(){

        int[][] tempGen = new int[generacionActual.length][generacionActual.length];

        for (int i=0; i<tempGen.length; i++){
            for (int j=0; j<tempGen.length; j++){
                tempGen[i][j] = vecindadVonNeumann(i, j, generacionActual);
            }
        }

        // Guardamos la nueva generacion
        generacionActual = tempGen;
    }

    public void evoluciona(){

        if (nGeneracionActual != 0){

            // Creamos la primera generación
            switch (estadoInicial){
                case ALEATORIO: evolucionAleatorio(); break;
                case ISLAS: evolucionIslas(); break;
                case CANIONES: evolucionCaniones();
                    break;
            }

            actualizaHistoricoPoblacion();
        }

        nGeneracionActual++;
    }

    private void actualizaHistoricoPoblacion(){

        // Contamos las células vivas
        for (int i=0; i<generacionActual.length; i++){
            for (int j=0; j<generacionActual.length; j++) {
                if (generacionActual[i][j] == 1) historicoPoblacionActiva[nGeneracionActual]++;
            }
        }
    }


    public int getNumGeneraciones() {
        return numGeneraciones;
    }


    public int[] getHistoricoPoblacionActiva(){ return historicoPoblacionActiva; }

    public boolean haTerminadoEvolucion(){
        return nGeneracionActual == numGeneraciones;
    }


    public int[][] getGeneracionActual() {
        return generacionActual;
    }

    public int getnGeneracionActual() {
        return nGeneracionActual;
    }
}
