import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.Arrays;

public class ca1DSim {

    private double k;                       // Estados por célula: 2,3,4,5 (nº de valores que podrán tener en cada tiempo t las células)
    private int r = 1;                      // Vecinos

    private int codigoEnBase10 = 100;       // Codigo a usar para los colores en base 10
    private String codigoEnBaseK;           // Codigo a usar para los colores en base k
    private int[] tablaColores;             // Tabla para asignar colores a cada célula. Tamaño = (2*r+1) * k - 2r

    private int nGeneraciones = 800;        // Veces que evolucionarán las células (equivalente al alto de la pantalla)

    private int nGeneracionActual = 0;      // Generación actual (veces que han evolucionado - 1)

    private int nCelulas = 600;             // Cantidad células en cada generación (Equivalente al ancho de la pantalla)
    private int[] generacionActual;         // Valores de las celulas para la generacion actual


    private boolean calcularHamming;
    private int[] hamming;                  // Curva distancia Hamming

    private boolean calcularEntropiaEspacial;
    private double[] entropiaEspacial;         // Curva entropía espacial
    private int[] valoresCelulaObservada;   // Valores de la celula observada en cada paso

    private boolean calcularEntropiaTemporalCelula;
    private double entropiaCelulaObservada; // Valor final de la entropía de la célula observada
    private int celulaObservada;            // Indice de la célula observada

    private CONFIGURACION_INICIALIZACION_AUTOMATA configuracionInicial; // Forma de inicializar el autómata

    private CONFIGURACION_CONDICION_FRONTERA condicionFrontera;

    public enum CONFIGURACION_INICIALIZACION_AUTOMATA {
        ALEATORIA,
        CELULA_CENTRAL_ACTIVA
    }

    public enum CONFIGURACION_CONDICION_FRONTERA {
        NULA,
        CILINDRICA
    }


    // Necesario para el generador aleatorio
    private long xGenerador_Randu = 1;




    public ca1DSim(int k, int r, int codigo, int numGeneraciones, int[] generacionInicial) throws InstantiationException {

        this(k, r, codigo, CONFIGURACION_INICIALIZACION_AUTOMATA.CELULA_CENTRAL_ACTIVA,
                CONFIGURACION_CONDICION_FRONTERA.CILINDRICA, numGeneraciones, generacionInicial.length, true,
                true, true, (generacionInicial.length - 1) / 2,
                true, generacionInicial);
    }

    public ca1DSim(int k, int r, int codigo, int numGeneraciones, int numCelulas) throws InstantiationException {
        this(k, r, codigo, numGeneraciones, numCelulas,  (numCelulas-1) / 2);
    }

    public ca1DSim(int k, int r, int codigo, int numGeneraciones, int numCelulas, int indxCelula) throws InstantiationException {
        this(k, r, codigo, numGeneraciones, numCelulas, true, true, true, indxCelula);
    }

    public ca1DSim(int k, int r, int codigo, int numGeneraciones, int numCelulas, boolean calcularHamming,
                   boolean calcularEntropiaEspacial, boolean calcularEntropiaTemporalCelula, int indxCelula) throws InstantiationException {
        this(k, r, codigo, CONFIGURACION_INICIALIZACION_AUTOMATA.ALEATORIA, CONFIGURACION_CONDICION_FRONTERA.CILINDRICA,
                numGeneraciones, numCelulas, calcularHamming, calcularEntropiaEspacial, calcularEntropiaTemporalCelula, indxCelula);
    }

    public ca1DSim(int k, int r, int codigo, CONFIGURACION_INICIALIZACION_AUTOMATA configuracionInicial,
                   CONFIGURACION_CONDICION_FRONTERA condicionFrontera, int numGeneraciones,
                   int numCelulas, boolean calcularHamming, boolean calcularEntropiaEspacial,
                   boolean calcularEntropiaTemporalCelula, int indxCelula) throws InstantiationException {

        this(k, r, codigo, configuracionInicial, condicionFrontera, numGeneraciones, numCelulas,
                calcularHamming, calcularEntropiaEspacial, calcularEntropiaTemporalCelula, indxCelula,
                false, null);
    }

    public ca1DSim(int k, int r, int codigo, CONFIGURACION_INICIALIZACION_AUTOMATA configuracionInicial,
                   CONFIGURACION_CONDICION_FRONTERA condicionFrontera, int numGeneraciones,
                   int numCelulas, boolean calcularHamming, boolean calcularEntropiaEspacial,
                   boolean calcularEntropiaTemporalCelula, int indxCelula, boolean inicializarGeneracionActual,
                   int[] generacionActual) throws InstantiationException {

        inicializarAutomata(k, r, codigo, configuracionInicial, condicionFrontera, numGeneraciones, numCelulas,
                calcularHamming, calcularEntropiaEspacial, calcularEntropiaTemporalCelula, indxCelula,
                inicializarGeneracionActual, generacionActual);
    }



    private double generadorRandu(){
        xGenerador_Randu = (((long) Math.pow(2, 16)) + 3) * xGenerador_Randu % ((long) Math.pow(2, 31));
        return xGenerador_Randu / Math.pow(2, 31);
    }



    private void inicializarAutomata(int k, int r, int regla, CONFIGURACION_INICIALIZACION_AUTOMATA configuracionInicial,
                                     CONFIGURACION_CONDICION_FRONTERA condicionFrontera, int numGeneraciones,
                                     int numCelulas, boolean calcularHamming, boolean calcularEntropiaEspacial,
                                     boolean calcularEntropiaTemporalCelula, int indxCelula, boolean inicializarGeneracionActual, int[] generacionActual) throws InstantiationException {

        // 2 <= k <= 5
        if (k < 2 || k > 5) throw new InstantiationException("Valor no válido para k (2-5)");

        // generaciones >= 1
        if (numGeneraciones < 1) throw new InstantiationException("Valor no válido de generaciones (> 0)");

        // codigo/regla >= 0
        if (r < 0) throw new InstantiationException("Código/Regla no válido (>=0)");

        // Establecemos los parámetros
        if (calcularEntropiaTemporalCelula){
            if (indxCelula < numCelulas) this.celulaObservada = indxCelula;
            else if (indxCelula < 0) this.celulaObservada = 0;
            else this.celulaObservada = numCelulas-1;
        }

        // Inicializamos los valores de las variables
        this.nCelulas = numCelulas;
        this.calcularHamming = calcularHamming;
        this.calcularEntropiaEspacial = calcularEntropiaEspacial;
        this.calcularEntropiaTemporalCelula = calcularEntropiaTemporalCelula;
        this.nGeneraciones = numGeneraciones;
        this.k = k;
        this.r = r;
        this.codigoEnBase10 = regla;
        this.configuracionInicial = configuracionInicial;
        this.condicionFrontera = condicionFrontera;
        this.nGeneracionActual = 0;
        this.hamming = new int[nGeneraciones];           // Inicializamos la curva de Hamming
        this.entropiaEspacial = new double[nGeneraciones];      // Inicializamos la curva de entropia espacial
        this.valoresCelulaObservada = new int[nGeneraciones];  // Inicializamos los valores de la celula observada

        // Inicializamos la tabla de colores
        if (tablaColores == null || tablaColores.length == 0) inicializarTablaColores();

        // Inicializamos el automata con los datos que nos pasan
        if (inicializarGeneracionActual) this.generacionActual = generacionActual;
        else {

            this.generacionActual = new int[nCelulas];

            // Inicializamos las células
            inicializaCelulas();
        }

        // Registramos los primeros valores
        actualizaHamming(generacionActual);
        actualizaEntropiaEspacial(generacionActual);
        valoresCelulaObservada[nGeneracionActual] = generacionActual[celulaObservada];
    }

    private void inicializarTablaColores(){

        int tamanioTabla = (2*r+1) * ((int) k) - 2 * r;

        tablaColores = new int[tamanioTabla];         // Por defecto, JAVA inicializa todos a 0

        // Transformamos el codigo de base 10 a base k
        codigoEnBaseK = Integer.toString(codigoEnBase10, (int) k);
        codigoEnBaseK = new StringBuilder(codigoEnBaseK).reverse().toString();

        // Rellenamos la tabla con los valores de las posiciones menos significativas
        // a las posiciones más significativas
        int i=0;
        while(i<tamanioTabla && i<codigoEnBaseK.length()){

            // Rellenamos la tabla con el color que le toque
            tablaColores[i] = Character.getNumericValue(codigoEnBaseK.charAt(i));

            i++;
        }
    }

    private void inicializaCelulas(){

        // Inicialización aleatoria
        if (this.configuracionInicial == CONFIGURACION_INICIALIZACION_AUTOMATA.ALEATORIA){
            for (int i=0; i<nCelulas; i++) generacionActual[i] = (int) (generadorRandu() * k);
        }

        // Célula central activa
        else {
            generacionActual[(nCelulas-1) / 2 ] = (int) (k-1);
        }
    }

    private int calculaValorCelula(int indxCelula){

        int tempV = generacionActual[indxCelula];

        int porDerecha = r;
        int porIzquierda = r;

        // Cogemos los vecinos de la derecha (hasta acabar o llegar al último)
        for (int i=indxCelula+1; i<nCelulas && porDerecha>0; i++){
            porDerecha--;
            tempV += generacionActual[i];
        }

        // Cogemos los vecinos de la izquierda (hasta acabar o llegar al primero)
        for (int i=indxCelula-1; i>=0 && porIzquierda>0; i--){
            porIzquierda--;
            tempV += generacionActual[i];
        }

        // Si la condicion de frontera es cilíndrica, recorreremos lo que nos haga falta
        if (condicionFrontera == CONFIGURACION_CONDICION_FRONTERA.CILINDRICA){
            // Todavía faltaban celulas por la derecha, cogemos del principio
            if (porDerecha > 0){
                for (int i=0; porDerecha>0; i++){
                    porDerecha--;
                    tempV += generacionActual[i];
                }
            }

            // Todavía faltaban celulas por la izquierda, cogemos del final
            if (porIzquierda > 0){
                for (int i=nCelulas-1; porIzquierda>0; i--){
                    porIzquierda--;
                    tempV += generacionActual[i];
                }
            }
        }

        return tempV;
    }

    public void evoluciona(){

        // En el paso 0 no evolucionaremos (por conveniencia).
        if (nGeneracionActual > 0){
            int[] temp = new int[generacionActual.length];

            for (int i=0; i<nCelulas; i++){

                // Obtenemos el valor correspondiente
                temp[i] = tablaColores[calculaValorCelula(i)];
            }

            // Actualizamos los valores de la curva de Hamming
            if (calcularHamming) actualizaHamming(temp);

            // Actualizamos los valores de la curva de entropía espacial
            if (calcularEntropiaEspacial) actualizaEntropiaEspacial(temp);

            // Registramos el valor de la célula observada
            if (calcularEntropiaTemporalCelula) valoresCelulaObservada[nGeneracionActual] = temp[celulaObservada];

            // Guardamos la generación nueva
            generacionActual = temp;

            // Calculamos la entropia de la celula observada en caso de tener que hacerlo
            if (isCalcularEntropiaTemporalCelula() && nGeneracionActual == nGeneraciones-1) calculaEntropiaCelulaObservada();

        }

        // Guardamos la evolución
        nGeneracionActual++;
    }

    public void actualizaHamming(int[] temp){

        int hamming = 0;

        // Contamos los dígitos que difieren
        for (int i=0; i<temp.length; i++){
            if (temp[i] != generacionActual[i]) hamming++;
        }

        this.hamming[nGeneracionActual] = hamming;
    }

    public double logConversion(double x){
        return(Math.log(x)/Math.log(k));
    }

    public void actualizaEntropiaEspacial(int[] generacion){

        double probabilidades[] = new double[(int) k];

        // Contamos cuantas celulas hay de cada valor en la generacion actual
        for(int i=0; i<generacion.length; i++){
            int valor = generacion[i];
            probabilidades[valor]++;
        }

        // Obtenemos la probabilidad de cada uno
        for (int i=0; i<probabilidades.length; i++) probabilidades[i] /= generacion.length;

        // Formula de la entropia
        for (int i=0; i<probabilidades.length;i++) entropiaEspacial[nGeneracionActual] -= probabilidades[i] * logConversion(probabilidades[i]);

        entropiaEspacial[nGeneracionActual] = Math.abs(entropiaEspacial[nGeneracionActual]);

    }

    public void calculaEntropiaCelulaObservada(){

        double probabilidades[] = new double[(int) k];
        entropiaCelulaObservada = 0.0;

        // Contamos cuantas celulas hay de cada valor para la celula observada
        for(int i=0; i<nGeneraciones; i++){
            int valor = valoresCelulaObservada[i];
            probabilidades[valor]++;
        }

        // Obtenemos la probabilidad de cada uno
        for (int i=0; i<probabilidades.length; i++) probabilidades[i] /= nGeneraciones;

        // Formula de la entropia
        for (int i=0; i<probabilidades.length;i++) {
            if (probabilidades[i] != 0.0){
                double temp = probabilidades[i] * logConversion(probabilidades[i]);
                entropiaCelulaObservada += temp;
            }
        }
        entropiaCelulaObservada = -entropiaCelulaObservada;

        if (entropiaCelulaObservada == -0.0) entropiaCelulaObservada = 0.0;
    }

    public boolean haTerminado(){ return nGeneraciones == nGeneracionActual; }





    public void setCodigo(int codigoEnBase10) {
        this.codigoEnBase10 = codigoEnBase10;
    }







    public boolean isCalcularHamming() {
        return calcularHamming;
    }

    public boolean isCalcularEntropiaEspacial() {
        return calcularEntropiaEspacial;
    }

    public boolean isCalcularEntropiaTemporalCelula() {
        return calcularEntropiaTemporalCelula;
    }

    public int[] getHamming() {
        return hamming;
    }

    public double[] getEntropiaEspacial() {
        return entropiaEspacial;
    }

    public double getEntropiaCelulaObservada() {
        return entropiaCelulaObservada;
    }

    public int getCelulaObsertada(){
        return generacionActual[celulaObservada];
    }

    public int[] getGeneracionActual(){ return this.generacionActual; }

    public int getNGeneracionActual(){ return nGeneracionActual; }

    public int getnGeneraciones() {
        return nGeneraciones;
    }

    public double getK(){
        return k;
    }

    public int getnCelulas() {
        return nCelulas;
    }
}
