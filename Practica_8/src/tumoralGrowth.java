import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;

public class tumoralGrowth {


    // Tensor con los valores de la generación nueva/vieja (alterna)
    private byte[][] generaciones;

    // Ayuda a ir alternando generacion nueva/vieja en el tensor de arriba (#generaciones)
    // p sera la generación inicial -> actualizar matriz con q


    // Nº de generación actual
    private int nGeneracionActual;

    // Nº total de generaciones
    private int numGeneraciones;

    private double Ps, Pm, Pp;
    private int PH = 0, NP;

    private int[] historialCelulas = null;
    private int cuentaCelulas = 0;

    private static int N_CELULAS = tumoralGrowthGUI.TAMANIO_RETICULA / tumoralGrowthGUI.FACTOR_CELULAS;

    public static class ParametrosEscenario {

        public double Ps, Pm, Pp;
        public int NP;

        public HashSet<Point> celulasIniciales = new HashSet<>();

        public static ParametrosEscenario[] ESCENARIOS_INICIALES = getEscenariosIniciales();

        public ParametrosEscenario(double ps, double pm, double pp, int NP) {
            this.Ps = ps;
            this.Pm = pm;
            this.Pp = pp;
            this.NP = NP;
        }

        public ParametrosEscenario(double ps, double pm, double pp, int NP, HashSet<Point> celulasIniciales) {
            this.Ps = ps;
            this.Pm = pm;
            this.Pp = pp;
            this.NP = NP;
            this.celulasIniciales = celulasIniciales;
        }

        private static ParametrosEscenario[] getEscenariosIniciales(){

            int n_celulas = tumoralGrowthGUI.TAMANIO_RETICULA / tumoralGrowthGUI.FACTOR_CELULAS;
            int centro = (n_celulas-1) / 2;

            ParametrosEscenario escenarioA = new ParametrosEscenario(1.0, 0.2, 0.25, 1);
            escenarioA.celulasIniciales.add(new Point(centro, centro));
            escenarioA.celulasIniciales.add(new Point(centro+1,  centro));
            escenarioA.celulasIniciales.add(new Point(centro+2,  centro));
            escenarioA.celulasIniciales.add(new Point(centro-1,  centro));

            ParametrosEscenario escenarioB = new ParametrosEscenario(1.0, 0.8, 0.25, 1);
            escenarioB.celulasIniciales.add(new Point(centro, centro));
            escenarioB.celulasIniciales.add(new Point(centro+5, centro-5));

            ParametrosEscenario escenarioC = new ParametrosEscenario(1.0, 0.2, 0.25, 2);
            escenarioC.celulasIniciales.add(new Point(centro, centro));

            ParametrosEscenario escenarioD = new ParametrosEscenario(1.0, 0.8, 0.25, 2);
            escenarioD.celulasIniciales.add(new Point(centro, centro));

            return new ParametrosEscenario[]{escenarioA, escenarioB, escenarioC, escenarioD};
        }

        @Override
        public String toString() {
            String msg = "Ps=" + Ps + ", Pm=" + Pm + ", Pp=" + Pp + ", NP=" + NP + "\n";
            msg += celulasIniciales.toString();
            return msg;
        }
    }

    // Necesario para el generador aleatorio
    private static long xGenerador_Randu = 1;

    public static void seed(long seed){
        xGenerador_Randu = seed;
    }

    public tumoralGrowth(int numGeneraciones, int escenario){

        ParametrosEscenario parametrosEscenario = ParametrosEscenario.ESCENARIOS_INICIALES[escenario];
        init(numGeneraciones, parametrosEscenario);
    }

    public tumoralGrowth(int numGeneraciones, ParametrosEscenario parametrosEscenario){
        init(numGeneraciones, parametrosEscenario);
    }

    private void init(int numGeneraciones, ParametrosEscenario parametrosEscenario){

        // Inicializamos los parámetros del simulador
        this.numGeneraciones = numGeneraciones;
        this.Ps = parametrosEscenario.Ps;
        this.Pm = parametrosEscenario.Pm;
        this.Pp = parametrosEscenario.Pp;
        this.NP = parametrosEscenario.NP;
        this.historialCelulas = new int[numGeneraciones];

        // Inicializamos la generación actual
        this.generaciones = new byte[N_CELULAS][N_CELULAS];

        // Colocamos las primeras células vivas en base a las plantillas
        inicializaCancerEscenario(parametrosEscenario);
    }

    private float generadorRandu(){
        xGenerador_Randu = (((long) Math.pow(2, 16)) + 3) * xGenerador_Randu % ((long) Math.pow(2, 31));
        return xGenerador_Randu / (float) Math.pow(2, 31);
    }

    private void inicializaCancerEscenario(ParametrosEscenario parametrosEscenario){

        int cuentaCelulas = 0;
        for (Point punto : parametrosEscenario.celulasIniciales){
            cuentaCelulas++;
            this.generaciones[punto.x][punto.y] = 1;
        }

        this.historialCelulas[nGeneracionActual] = cuentaCelulas;
    }

    /**
     *
     * @return
     */
    public void evolucionVonNeumann(){

        // COpiamos el estado del anterior
        byte[][] temp = new byte[N_CELULAS][N_CELULAS];
        /*for (int i=0; i<N_CELULAS; i++){
            temp[i] = Arrays.copyOf(generaciones[i], N_CELULAS);
        }*/
        boolean fijarseViejo = true;

        for (int x = 0; x < N_CELULAS ; x ++) {
            for (int y = 0; y < N_CELULAS ; y ++) {

                boolean celulaViva = temp[x][y] == 1;

                if (fijarseViejo) celulaViva = generaciones[x][y] == 1;

                // Comprobamos si la célula está viva
                if (celulaViva){

                    double rr = Math.random();

                    // La célula muere
                    /*if (rr >= Ps){
                        cuentaCelulas--;
                        generaciones[q][x][y] = 0;
                    }*/

                    // La célula sobrevive
                    if (rr < Ps){
                        double rrp = Math.random();

                        // Aumentamos el conteo de proliferación (PH+1)
                        if (rrp < Pp){
                            PH++;

                            // Intentaremos proliferar
                            if (PH >= NP) {
                                boolean prolifero = intentarProliferacion(x, y, N_CELULAS, rr, temp, fijarseViejo);

                                // No ha proliferado
                                if (!prolifero) ramaMigracion(x, y, N_CELULAS, rr, temp, fijarseViejo);

                                // TODO ¿?
                                else PH = 0;
                            }
                            // No podemos "intentar proliferar"
                            else ramaMigracion(x, y, N_CELULAS, rr, temp, fijarseViejo);
                        }

                        // No podemos aumentar el conteo de proliferacion (PH+1)
                        else ramaMigracion(x, y, N_CELULAS, rr, temp, fijarseViejo);
                    }
                }
            }
        }

        int contadorTemp = 0;
        int contadorP = 0;
        for (int x = 0; x < N_CELULAS ; x ++) {
            for (int y = 0; y < N_CELULAS ; y ++) {
                if (temp[x][y] == 1) contadorTemp++;
                if (generaciones[x][y] == 1) contadorP++;
            }
        }

        generaciones = temp;
    }

    /**
     * Parte de la estructura de decisiones donde aparece rrm < Pm
     * @param x
     * @param y
     * @param n
     */
    private void ramaMigracion(int x, int y, int n, double rr, byte[][] temp, boolean fijarseViejo){

        double rrm = Math.random();

        // Intentamos que migre
        if (rrm < Pm){
            intentaMigrar(x, y, n, rr, temp, fijarseViejo);
        }

        // Dejamos el valor que tuviese (quiescent)
        else temp[x][y] = generaciones[x][y];
    }

    /**
     * Parte de la estructura de deciciones donde se calcula la probabilidad de proliferar y, en caso de poderse,
     * se actualizan la celdas x,y y sus vecinas.
     * @param x
     * @param y
     * @param n
     * @return
     */
    private boolean intentarProliferacion(int x, int y, int n, double rr, byte[][] temp, boolean fijarseViejo){

        // Calculamos las posiciones de la fila/columna anterior/siguiente
        int xAnterior = (x-1+n)%n;
        int xSiguiente = (x+1+n)%n;

        int yAnterior = (y-1+n)%n;
        int ySiguiente = (y+1+n)%n;

        // Cogemos los valores de los vecinos
        byte valorFilaAnterior = temp[x][yAnterior];
        byte valorFilaSiguiente = temp[x][ySiguiente];

        byte valorColumnaAnterior = temp[xAnterior][y];
        byte valorColumnaSiguiente = temp[xSiguiente][y];


        if (fijarseViejo){
            valorFilaAnterior = generaciones[x][yAnterior];
            valorFilaSiguiente = generaciones[x][ySiguiente];
            valorColumnaAnterior = generaciones[xAnterior][y];
            valorColumnaSiguiente = generaciones[xAnterior][y];
        }

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
        if (proliferado){
            temp[direccionX][direccionY] = 1;
        }

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
    private void intentaMigrar(int x, int y, int n, double rr, byte[][] temp, boolean fijarseViejo){

        // Calculamos las posiciones de la fila/columna anterior/siguiente
        int xAnterior = (x-1+n)%n;
        int xSiguiente = (x+1+n)%n;

        int yAnterior = (y-1+n)%n;
        int ySiguiente = (y+1+n)%n;

        // Cogemos los valores de los vecinos
        byte valorFilaAnterior = temp[x][yAnterior];
        byte valorFilaSiguiente = temp[x][ySiguiente];

        byte valorColumnaAnterior = temp[xAnterior][y];
        byte valorColumnaSiguiente = temp[xSiguiente][y];

        if (fijarseViejo){
            valorFilaAnterior = generaciones[x][yAnterior];
            valorFilaSiguiente = generaciones[x][ySiguiente];
            valorColumnaAnterior = generaciones[xAnterior][y];
            valorColumnaSiguiente = generaciones[xAnterior][y];
        }

        // Calculamos las probabilidades
        double p1 = (float)(1-valorFilaAnterior) / (float)(4 - (valorFilaSiguiente + valorFilaAnterior + valorColumnaSiguiente + valorColumnaAnterior));
        double p2 = (float)(1-valorFilaSiguiente) / (float)(4 - (valorFilaSiguiente + valorFilaAnterior + valorColumnaSiguiente + valorColumnaAnterior));
        double p3 = (float)(1-valorColumnaAnterior) / (float)(4 - (valorFilaSiguiente + valorFilaAnterior + valorColumnaSiguiente + valorColumnaAnterior));

        int[] direccionesX = new int[]{ x, x, xAnterior, xSiguiente };
        int[] direccionesY = new int[]{ yAnterior, ySiguiente, y, y };

        double[] rangos = new double[]{0, p1, p1+p2, p1+p2+p3, 1};

        int direccionX = x;
        int direccionY = y;

        boolean migra = false;

        // Comprobamos si hay alguna posicion a la que proliferar
        for (int i=0; i<rangos.length-1; i++){
            if (rr >= rangos[i] && rr <= rangos[i+1]){
                migra = true;
                direccionX = direccionesX[i];
                direccionY = direccionesY[i];
                break;
            }
        }

        if (migra){

            // Migramos la célula en la dirección escogida
            temp[direccionX][direccionY] = 1;
        }
    }

    public void evoluciona(){

        if (nGeneracionActual != 0){
            evolucionVonNeumann();

            // Contamos las celulas de la generacion actual
            for (int i=0;i<N_CELULAS; i++){
                for (int j=0;j<N_CELULAS; j++) {
                    if (generaciones[i][j] == 1) {
                        historialCelulas[nGeneracionActual]++;
                    }
                }
            }
        }

        nGeneracionActual++;
    }


    public int[] getHistorialCelulas() {
        return historialCelulas;
    }

    public byte[][] getActualGen() {
        return generaciones;
    }

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
