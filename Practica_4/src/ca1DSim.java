import java.util.Arrays;

public class ca1DSim {

    private double k;                       // Estados por célula: 2,3,4,5 (nº de valores que podrán tener en cada tiempo t las células)
    private int r = 1;                // Vecinos

    private int codigoEnBase10 = 100;       // Codigo a usar para los colores en base 10
    private String codigoEnBaseK;           // Codigo a usar para los colores en base k
    private int[] tablaColores;             // Tabla para asignar colores a cada célula. Tamaño = (2*r+1) * k - 2r

    private int nGeneraciones = 800;        // Veces que evolucionarán las células (equivalente al alto de la pantalla)

    private int nGeneracionActual = 0;      // Generación actual (veces que han evolucionado - 1)

    private int nCelulas = 600;             // Cantidad células en cada generación (Equivalente al ancho de la pantalla)
    private int[] generacionActual;         // Valores de las celulas para la generacion actual

    private int[] hamming;                  // Curva distancia Hamming
    private int[] entropiaEspacial;         // Curva entropía espacial
    private int[] valoresCelulaObservada;   // Valores de la celula observada en cada paso
    private double entropiaCelulaObservada; // Valor final de la entropía de la célula observada
    private int celulaObservada;            // Indice de la célula observada

    private CONFIGURACION_INICIAL configuracionInicial; // Forma de inicializar el autómata

    public enum CONFIGURACION_INICIAL {
        ALEATORIA,
        CELULA_CENTRAL_ACTIVA
    }


    // Necesario para el generador aleatorio
    private long xGenerador_Randu = 1;


    public ca1DSim(int k, int r, int codigo, int generaciones, CONFIGURACION_INICIAL configuracionInicial) throws InstantiationException {
        inicializarAutomata(k, r, codigo, generaciones, configuracionInicial);
    }

    private double generadorRandu(){
        xGenerador_Randu = (((long) Math.pow(2, 16)) + 3) * xGenerador_Randu % ((long) Math.pow(2, 31));
        return xGenerador_Randu / Math.pow(2, 31);
    }

    private void inicializarAutomata(int k, int r, int codigo, int generaciones, CONFIGURACION_INICIAL configuracionInicial) throws InstantiationException {

        // 2 <= k <= 5
        if (k < 2 || k > 5) throw new InstantiationException("Valor no válido para k (2-5)");

        // generaciones >= 1
        if (generaciones < 1) throw new InstantiationException("Valor no válido de generaciones (> 0)");

        // codigo >= 0
        if (codigo < 0) throw new InstantiationException("Código no válido (>=0)");

        // Establecemos los parámetros
        this.nGeneraciones = generaciones;
        this.k = k;
        this.r = r;
        this.codigoEnBase10 = codigo;
        this.configuracionInicial = configuracionInicial;

        // Reseteamos variables
        this.nGeneracionActual = 0;
        this.generacionActual = new int[nCelulas];
        this.hamming = new int[generaciones];           // Inicializamos la curva de Hamming
        this.entropiaEspacial = new int[nCelulas];      // Inicializamos la curva de entropia espacial
        this.valoresCelulaObservada = new int[generaciones];  // Inicializamos los valores de la celula observada

        // Inicializamos las células
        inicializaCelulas();

        // Inicializamos la tabla de colores
        if (tablaColores == null || tablaColores.length == 0) inicializarTablaColores();

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
        if (this.configuracionInicial == CONFIGURACION_INICIAL.ALEATORIA){
            for (int i=0; i<nCelulas; i++) generacionActual[i] = (int) (generadorRandu() * k);
        }

        // Célula central activa
        else {
            generacionActual[nCelulas/2 - 1] = (int) (k-1);
        }
    }

    public void evoluciona(){

        // En el paso 0 no evolucionaremos (por conveniencia).
        if (nGeneracionActual > 0){
            int[] temp = new int[generacionActual.length];

            for (int i=0; i<nCelulas; i++){

                int tempV;

                // Célula 0 (depende de: n-1, 0 y 1)
                if (i == 0){
                    tempV = generacionActual[nCelulas-1] + generacionActual[0] + generacionActual[1];
                }
                // Célula n-1 (depende de: n-2, n-1, 0)
                else if(i == nCelulas-1){
                    tempV = generacionActual[nCelulas-2] + generacionActual[nCelulas-1] + generacionActual[0];
                }

                // Célula i (depende de: i-1, i, i+1)
                else {
                    tempV = generacionActual[i-1] + generacionActual[i] + generacionActual[i+1];
                }

                // Obtenemos el valor correspondiente
                temp[i] = tablaColores[tempV];
            }

            // Actualizamos los valores de la curva de Hamming
            actualizaHamming(temp);

            // Actualizamos los valores de la curva de entropía espacial
            actualizaEntropiaEspacial(temp);

            // Registramos el valor de la célula observada
            valoresCelulaObservada[nGeneracionActual] = temp[celulaObservada];

            // Guardamos la generación nueva
            generacionActual = temp;
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

    public static double logConversion(double x){
        return(Math.log(x)/Math.log(2));
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
        for (int i=0; i<probabilidades.length;i++) entropiaEspacial[nGeneracionActual] += probabilidades[i] * logConversion(probabilidades[i]);
        entropiaEspacial[nGeneracionActual] = -entropiaEspacial[nGeneracionActual];
    }

    public void calculaEntropiaCelulaObservada(){

        double probabilidades[] = new double[(int) k];

        // Contamos cuantas celulas hay de cada valor para la celula observada
        for(int i=0; i<generacionActual.length; i++){
            int valor = generacionActual[i];
            probabilidades[valor]++;
        }

        // Obtenemos la probabilidad de cada uno
        for (int i=0; i<probabilidades.length; i++) probabilidades[i] /= generacionActual.length;

        // Formula de la entropia
        for (int i=0; i<probabilidades.length;i++) entropiaCelulaObservada += probabilidades[i] * logConversion(probabilidades[i]);
        entropiaCelulaObservada = -entropiaCelulaObservada;
    }

    public boolean haTerminado(){ return nGeneraciones == nGeneracionActual; }

    public void reset() throws InstantiationException {
        inicializarAutomata((int) k, r, codigoEnBase10, nGeneraciones, configuracionInicial);
    }





    public int[] getHamming() {
        return hamming;
    }

    public int[] getEntropiaEspacial() {
        return entropiaEspacial;
    }

    public double getEntropiaCelulaObservada() {
        return entropiaCelulaObservada;
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
