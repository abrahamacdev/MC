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

    private static final double PROB_CELULAS_ALEATORIAS = 0.3;
    private static final double PROB_NUM_ISLAS = 0.25;
    private static final int RADIO_ISLA = 2;

    private static final int NUM_PLANEADORES = 1;
    private static final int RADIO_X_PLANEADORES = 3;
    private static final int RADIO_Y_PLANEADORES = 1;
    private static final Map.Entry<Integer, Integer> DIRECCION_PLANEADORES = new AbstractMap.SimpleEntry<>(2, -3);


    // Matriz con los valores de la generación actual
    private int[][] generacionActual;

    // Nº de generación actual
    private int nGeneracionActual;

    // Nº total de generaciones
    private int numGeneraciones;

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
        iniciar();
    }


    private double generadorRandu(){
        xGenerador_Randu = (((long) Math.pow(2, 16)) + 3) * xGenerador_Randu % ((long) Math.pow(2, 31));
        return xGenerador_Randu / Math.pow(2, 31);
    }

    private void iniciar(){

        // Inicializamos la generación actual
        this.generacionActual = new int[lifeSimGUI.TAMANIO_RETICULA][lifeSimGUI.TAMANIO_RETICULA];

        // Creamos la primera generación
        switch (estadoInicial){
            case ALEATORIO: inicializaAleatorio(); break;
            case ISLAS: inicializaIslas(); break;
            case CANIONES: inicializaCaniones(); break;
        }
    }

    private void inicializaAleatorio(){

        // Generamos los puntos aleatorios
        for (int i=0; i<generacionActual.length; i++){
            for (int j=0; j<generacionActual.length; j++) {
                double rand = generadorRandu();
                if (rand <= PROB_CELULAS_ALEATORIAS) generacionActual[i][j] = 1;
            }
        }
    }

    /**
     *
     * @return
     */
    public int vecindadVonNeumann(int i, int j, int[][] generacionAnterior){

        int estadoAnterior = generacionAnterior[i][j];

        // Evitamos procesar los planeadores
        if (estadoAnterior != 2){
            int vivas = 0;

            for (int k=i-1; k<=i+1; k++){
                for (int z=j-1; z<=j+1; z++) {

                    // Comprobamos que no se sale de rango
                    if (k >= 0 && z >= 0 && k < lifeSimGUI.TAMANIO_RETICULA && z < lifeSimGUI.TAMANIO_RETICULA){

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

        return estadoAnterior;
    }

    private void evolucionAleatorio(){

        int[][] tempGen = new int[lifeSimGUI.TAMANIO_RETICULA][lifeSimGUI.TAMANIO_RETICULA];

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
    private Map.Entry<Integer, Integer> centroDeLaIsla(ArrayList<Map.Entry<Integer, Integer>> centros, int i, int j, int radio){

        for (Map.Entry<Integer, Integer> centro : centros){
            int diffFilas = centro.getKey() - i;
            int diffColumnas = centro.getKey() - j;
            int diff = diffFilas*diffFilas + diffColumnas*diffColumnas;
            if (diff <= radio) return centro;
        }
        return null;
    }

    private void inicializaIslas(){

        // Listado con los centros de las islas
        ArrayList<Map.Entry<Integer, Integer>> centrosIslas = new ArrayList<>();

        // Generamos los centros de las islas
        for (int i=0; i<generacionActual.length; i++){
            for (int j=0; j<generacionActual.length; j++) {
                double rand = generadorRandu();
                if (rand <= PROB_NUM_ISLAS) {
                    centrosIslas.add(new AbstractMap.SimpleEntry<>(i,j));
                    generacionActual[i][j] = 1;
                }
            }
        }

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

    }

    private Map.Entry<Integer, Integer> centroDelPlaneador(ArrayList<Map.Entry<Integer, Integer>> centros, int i, int j, int radioX, int radioY){

        for (Map.Entry<Integer, Integer> centro : centros){
            int diffFilas = centro.getKey() - i;
            int diffColumnas = centro.getKey() - j;
            if (diffFilas < radioX && diffColumnas < radioY){
                return centro;
            }
        }
        return null;
    }

    private void inicializaCaniones(){

        int n_cuad = lifeSimGUI.TAMANIO_RETICULA * lifeSimGUI.TAMANIO_RETICULA;
        int n = lifeSimGUI.TAMANIO_RETICULA;

        // Generamos los centros de los planeadores
        ArrayList<Map.Entry<Integer, Integer>> centros = new ArrayList<>(NUM_PLANEADORES);
        for (int i=0; i<NUM_PLANEADORES; i++){
            int centro = (int) (generadorRandu() * n_cuad);
            int fila = centro / n;
            int columna = centro % n;

            centros.add(new AbstractMap.SimpleEntry<>(fila, columna));

            generacionActual[fila][columna] = 2;
        }

        //  Rellenamos alrededor del planeador
        for (int i=0; i<generacionActual.length; i++){
            for (int j=0; j<generacionActual.length; j++) {
                Map.Entry<Integer, Integer> centro = centroDelPlaneador(centros, i, j, RADIO_X_PLANEADORES, RADIO_Y_PLANEADORES);

                if (centro != null){
                    generacionActual[i][j] = 2;
                }
            }
        }
    }
    private void evolucionCaniones(){

    }

    public void evoluciona(){

        if (nGeneracionActual != 0){

            // Creamos la primera generación
            switch (estadoInicial){
                case ALEATORIO: evolucionAleatorio(); break;
                case ISLAS: evolucionIslas(); break;
                case CANIONES: evolucionCaniones(); break;
            }
        }

        nGeneracionActual++;
    }

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
