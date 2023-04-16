import java.util.Arrays;

public class lifeSim {

    public enum ESTADO_INICIAL {
        ALEATORIO,
        ISLAS,
        CANIONES
    }


    // Matriz con los valores de la generación actual
    private float[][][] a;
    private float[] mediaPoblacionA;

    private float[][][] b;
    private float[] mediaPoblacionB;
    private float[][][] c;
    private float[] mediaPoblacionC;

    private int p= 0, q=1;


    // Nº de generación actual
    private int nGeneracionActual;

    // Nº total de generaciones
    private int numGeneraciones;


    // Necesario para el generador aleatorio
    private static long xGenerador_Randu = 1;

    public static void seed(long seed){
        xGenerador_Randu = seed;
    }

    public lifeSim(int numGeneraciones){
        this.numGeneraciones = numGeneraciones;
        iniciar();
    }



    private float generadorRandu(){
        xGenerador_Randu = (((long) Math.pow(2, 16)) + 3) * xGenerador_Randu % ((long) Math.pow(2, 31));
        return xGenerador_Randu / (float) Math.pow(2, 31);
    }

    private void iniciar(){

        int n_celulas = lifeSimGUI.TAMANIO_RETICULA / lifeSimGUI.FACTOR_CELULAS;

        // Inicializamos la generación actual
        this.a = new float[n_celulas][n_celulas][2];
        this.b = new float[n_celulas][n_celulas][2];
        this.c = new float[n_celulas][n_celulas][2];

        this.mediaPoblacionA = new float[numGeneraciones];
        this.mediaPoblacionB = new float[numGeneraciones];
        this.mediaPoblacionC = new float[numGeneraciones];

        // Creamos la primera generación
        inicializaAleatorio();
    }

    private void inicializaAleatorio(){
        int n = a[0].length;

        // Generamos los puntos aleatorios
        for (int i=0; i<n; i++){
            for (int j=0; j<n; j++) {

                this.a[i][j][p] = generadorRandu();
                this.b[i][j][p] = generadorRandu();
                this.c[i][j][p] = generadorRandu();
            }
        }
    }

    private float constrain(float v, float min, float max){
        if (v > max) return max;
        else if (v < min) return min;
        else return v;
    }

    private float calculaMedia(float[][][] v, int temp){

        float media = 0.0f;

        for (int i=0; i<v.length; i++){
            for (int j=0; j<v.length; j++) {
                media += v[i][j][temp];
            }
        };

        return media / (v.length * v.length);
    }

    /**
     *
     * @return
     */
    public void evolucionVonNeumann(){

        int n = this.a[0].length;

        float mediaA = 0.0f;
        float mediaB = 0.0f;
        float mediaC = 0.0f;

        for (int x = 0; x < n ; x ++) {
            for (int y = 0; y < n ; y ++) {
                float c_a = 0.0f;
                float c_b = 0.0f;
                float c_c = 0.0f;

                // Aplica
                for (int i = x - 1; i <= x + 1; i ++) {
                    for (int j = y - 1; j <= y + 1; j ++) {
                        c_a += a [( i + n ) % n ][( j + n ) % n ][ p ];
                        c_b += b [( i + n ) % n ][( j + n ) % n ][ p ];
                        c_c += c [( i + n ) % n ][( j + n ) % n ][ p ];
                    }
                }
                c_a /= 9.0;
                c_b /= 9.0;
                c_c /= 9.0;

                a[x][y][q] = constrain( c_a + c_a * ( c_b - c_c ), 0, 1);
                b[x][y][q] = constrain( c_b + c_b * ( c_c - c_a ), 0, 1);
                c[x][y][q] = constrain( c_c + c_c * ( c_a - c_b ), 0, 1);

                mediaA += a[x][y][0];
                mediaB += b[x][y][0];
                mediaC += c[x][y][0];
            }
        }

        // Calculamos la media para graficar
        if (nGeneracionActual < numGeneraciones){
            float n_2 = n*n;

            mediaPoblacionA[nGeneracionActual] = mediaA / n_2;
            mediaPoblacionB[nGeneracionActual] = mediaB / n_2;
            mediaPoblacionC[nGeneracionActual] = mediaC / n_2;
        }


        if (p == 0) {
            p = 1; q = 0;
        }
        else {
            p = 0; q = 1;
        }
    }

    public void evoluciona(){

        if (nGeneracionActual != 0){
            evolucionVonNeumann();
        }

        nGeneracionActual++;
    }


    public float[] getMediaPoblacionA() {
        return mediaPoblacionA;
    }

    public float[] getMediaPoblacionB() {
        return mediaPoblacionB;
    }

    public float[] getMediaPoblacionC() {
        return mediaPoblacionC;
    }

    public float[][][] getA() {
        return a;
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
