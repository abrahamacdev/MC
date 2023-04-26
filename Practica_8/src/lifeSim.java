import java.util.Arrays;

public class lifeSim {


    // Tensor con los valores de la generación nueva/vieja (alterna)
    private byte[][][] generaciones;

    // Ayuda a ir alternando generacion nueva/vieja en el tensor de arriba (#generaciones)
    // p sera la generación inicial -> actualizar matriz con q
    private int p= 0, q=1;


    // Nº de generación actual
    private int nGeneracionActual;

    // Nº total de generaciones
    private int numGeneraciones;

    private double Ps, Pm, Pp;
    private int PH, NP;


    // Necesario para el generador aleatorio
    private static long xGenerador_Randu = 1;

    public static void seed(long seed){
        xGenerador_Randu = seed;
    }

    public lifeSim(int numGeneraciones, boolean escenarioPersonalizado, double Ps, double Pm, double Pp, int NP){
        this.numGeneraciones = numGeneraciones;
        this.Ps = Ps;
        this.Pm = Pm;
        this.Pp = Pp;
        this.PH = 0;
        this.NP = NP;

        iniciar(escenarioPersonalizado);
    }



    private float generadorRandu(){
        xGenerador_Randu = (((long) Math.pow(2, 16)) + 3) * xGenerador_Randu % ((long) Math.pow(2, 31));
        return xGenerador_Randu / (float) Math.pow(2, 31);
    }

    private void iniciar(boolean escenarioPersonalizado){

        int n_celulas = lifeSimGUI.TAMANIO_RETICULA / lifeSimGUI.FACTOR_CELULAS;

        // Inicializamos la generación actual
        this.generaciones = new byte[2][n_celulas][n_celulas];

        // Creamos la primera generación
        inicializaCancer(escenarioPersonalizado);
    }

    private void inicializaCancer(boolean escenarioPersonalizado){

        int n_celulas = lifeSimGUI.TAMANIO_RETICULA / lifeSimGUI.FACTOR_CELULAS;
        int centro = (n_celulas-1) / 2;

        // Pintar cuadrado
        if (escenarioPersonalizado){

            int[] x_s = new int[4];
            int[] y_s = new int[4];

            // Calculamos las posiciones de 4 células juntas formando un cuadrado en el centro de la retícula
            int indx = 0;
            for (int i=0; i<2; i++){
                for (int j=0; j<2; j++) {
                    x_s[indx] = centro + i;
                    y_s[indx] = centro + j;
                    indx++;
                }

            }

            // Situamos las 4 células en la retícula
            for (int i=0; i<4; i++){
                this.generaciones[p][x_s[i]][y_s[i]] = 1;
            }
        }

        // Inicializar con punto en el medio
        else {
            this.generaciones[p][centro][centro] = 1;
        }

    }

    /**
     *
     * @return
     */
    public void evolucionVonNeumann(){

        int n = lifeSimGUI.TAMANIO_RETICULA / lifeSimGUI.FACTOR_CELULAS;

        for (int x = 0; x < n ; x ++) {
            for (int y = 0; y < n ; y ++) {

                // Comprobamos si la célula está viva
                if (generaciones[p][x][y] == 1){

                    double rr = Math.random();

                    // La célula muere
                    if (rr >= Ps){
                        generaciones[q][x][y] = 0;
                    }

                    // La célula sobrevive
                    else {

                        double rrp = Math.random();

                        // Aumentamos el conteo de proliferación (PH+1)
                        if (rrp < Pp){
                            PH++;

                            // Intentaremos proliferar
                            if (PH >= NP) {
                                boolean prolifero = intentarProliferacion(x, y, n, rr);

                                // No ha proliferado
                                if (!prolifero) ramaMigracion(x, y, n, rr);

                                    // TODO ¿?
                                else PH = 0;
                            }
                            // No podemos "intentar proliferar"
                            else ramaMigracion(x, y, n, rr);
                        }

                        // No podemos aumentar el conteo de proliferacion (PH+1)
                        else ramaMigracion(x, y, n, rr);
                    }
                }
            }
        }

        // Alterna entre 0 y 1
        p ^= 1;
        q ^= 1;
    }

    /**
     * Parte de la estructura de decisiones donde aparece rrm < Pm
     * @param x
     * @param y
     * @param n
     */
    private void ramaMigracion(int x, int y, int n, double rr){

        double rrm = Math.random();

        if (rrm < Pm){
            intentaMigrar(x, y, n, rr);
        }
    }

    /**
     * Parte de la estructura de deciciones donde se calcula la probabilidad de proliferar y, en caso de poderse,
     * se actualizan la celdas x,y y sus vecinas.
     * @param x
     * @param y
     * @param n
     * @return
     */
    private boolean intentarProliferacion(int x, int y, int n, double rr){

        // Calculamos las posiciones de la fila/columna anterior/siguiente
        int xAnterior = (x-1+n)%n;
        int xSiguiente = (x+1+n)%n;

        int yAnterior = (y-1+n)%n;
        int ySiguiente = (y+1+n)%n;

        // Cogemos los valores de los vecinos
        byte valorFilaAnterior = generaciones[p][xAnterior][y];
        byte valorFilaSiguiente = generaciones[p][xSiguiente][y];

        byte valorColumnaAnterior = generaciones[p][x][yAnterior];
        byte valorColumnaSiguiente = generaciones[p][x][ySiguiente];

        // Calculamos las probabilidades
        double p1 = (float)(1-valorFilaAnterior) / (float)(4 - (valorFilaSiguiente + valorFilaAnterior + valorColumnaSiguiente + valorColumnaAnterior));
        double p2 = (float)(1-valorFilaSiguiente) / (float)(4 - (valorFilaSiguiente + valorFilaAnterior + valorColumnaSiguiente + valorColumnaAnterior));
        double p3 = (float)(1-valorColumnaAnterior) / (float)(4 - (valorFilaSiguiente + valorFilaAnterior + valorColumnaSiguiente + valorColumnaAnterior));

        int[] direccionesX = new int[]{ xAnterior, xSiguiente, x, x };
        int[] direccionesY = new int[]{ y, y, yAnterior, ySiguiente };

        double[] rangos = new double[]{0, p1, p1+p2, p1+p2+p3, 1};

        int direccionX = x;
        int direccionY = y;
        boolean proliferado = false;

        // Comprobamos si hay alguna posicion a la que proliferar
        for (int i=0; i<rangos.length-1; i++){
            if (rr >= rangos[i] && rr <= rangos[i+1]){
                proliferado = true;
                direccionX = direccionesX[i];
                direccionY = direccionesY[i];
                break;
            }
        }

        // Proliferamos la célula en la dirección escogida
        generaciones[q][direccionX][direccionY] = 1;

        // Si se ha proliferado
        return proliferado;
    }

    /**
     * Parte de la estructura de deciciones donde se calcula la probabilidad de MIGRAR y, en caso de poderse,
     * se actualiza la celda x,y.
     * @param x
     * @param y
     * @param n
     * @return
     */
    private void intentaMigrar(int x, int y, int n, double rr){

        // Calculamos las posiciones de la fila/columna anterior/siguiente
        int xAnterior = (x-1+n)%n;
        int xSiguiente = (x+1+n)%n;

        int yAnterior = (y-1+n)%n;
        int ySiguiente = (y+1+n)%n;

        // Cogemos los valores de los vecinos
        byte valorFilaAnterior = generaciones[p][xAnterior][y];
        byte valorFilaSiguiente = generaciones[p][xSiguiente][y];

        byte valorColumnaAnterior = generaciones[p][x][yAnterior];
        byte valorColumnaSiguiente = generaciones[p][x][ySiguiente];

        // Calculamos las probabilidades
        double p1 = (float)(1-valorFilaAnterior) / (float)(4 - (valorFilaSiguiente + valorFilaAnterior + valorColumnaSiguiente + valorColumnaAnterior));
        double p2 = (float)(1-valorFilaSiguiente) / (float)(4 - (valorFilaSiguiente + valorFilaAnterior + valorColumnaSiguiente + valorColumnaAnterior));
        double p3 = (float)(1-valorColumnaAnterior) / (float)(4 - (valorFilaSiguiente + valorFilaAnterior + valorColumnaSiguiente + valorColumnaAnterior));

        int[] direccionesX = new int[]{ xAnterior, xSiguiente, x, x };
        int[] direccionesY = new int[]{ y, y, yAnterior, ySiguiente };

        double[] rangos = new double[]{0, p1, p1+p2, p1+p2+p3, 1};

        int direccionX = x;
        int direccionY = y;

        // Comprobamos si hay alguna posicion a la que proliferar
        for (int i=0; i<rangos.length-1; i++){
            if (rr >= rangos[i] && rr <= rangos[i+1]){
                direccionX = direccionesX[i];
                direccionY = direccionesY[i];
            }
        }

        // Migramos la célula en la dirección escogida
        generaciones[q][direccionX][direccionY] = 1;
        generaciones[q][x][y] = 0;
    }

    public void evoluciona(){

        if (nGeneracionActual != 0){
            evolucionVonNeumann();
        }

        nGeneracionActual++;
    }

    public byte[][] getActualGen() {
        return generaciones[p];
    }

    public int getQ(){ return q; }

    public int getNumGeneraciones() {
        return numGeneraciones;
    }

    public boolean haTerminadoEvolucion(){
        return nGeneracionActual == numGeneraciones;
    }

    public int getnGeneracionActual() {
        return nGeneracionActual;
    }
}
