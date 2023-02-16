public class randomGenerator {

    private long xGenerador26_1_a = 1;   // x_n-1 del algoritmo 26.1a
    private long xGenerador26_1_b = 1;   // x_n-1 del algoritmo 26.1b
    private long xGenerador26_2 = 1;     // x_n-1 del algoritmo 26.2

    private long xGenerador26_3 = 1;     // x_n-1 del algoritmo 26.3


    //  Variables necesarias para el generador de las diapositivas 26_42
    private long wGenerador26_42 = 1;
    private long xGenerador26_42 = 1;
    private long yGenerador26_42 = 1;

    private long xGenerador_FishmanMoore = 1;

    private long xGenerador_Randu = 1;


    public randomGenerator(){}

    public randomGenerator(long seed){
        xGenerador26_1_a = seed;
        xGenerador26_1_b = seed;
        xGenerador26_2 = seed;
        xGenerador26_3 = seed;

        xGenerador26_42 = seed;
        wGenerador26_42 = seed;
        yGenerador26_42 = seed;

        xGenerador_FishmanMoore = seed;
        xGenerador_Randu = seed;
    }


    public double generador26_1_a(){
        xGenerador26_1_a = 5*xGenerador26_1_a % ((long) Math.pow(2, 5));
        return xGenerador26_1_a / Math.pow(2, 5);
    }

    public double generador26_1_b(){
        xGenerador26_1_b = 7*xGenerador26_1_b % ((long) Math.pow(2, 5));
        return xGenerador26_1_b / Math.pow(2, 5);
    }

    public double generador26_2(){
        xGenerador26_2 = 3*xGenerador26_2 % 31L;
        return xGenerador26_2 / 31.0;
    }

    public double generador26_3(){
        xGenerador26_3 = ((long) Math.pow(7, 5)) * xGenerador26_3 % (((long) Math.pow(2, 31)) - 1);
        return xGenerador26_3 / (Math.pow(2, 31) - 1);
    }

    public double generador26_42(){
        wGenerador26_42 = 157 * wGenerador26_42 % 32363;
        xGenerador26_42 = 146 * xGenerador26_42 % 31727;
        yGenerador26_42 = 142 * yGenerador26_42 % 31657;

        return Math.abs(wGenerador26_42 - xGenerador26_42 + yGenerador26_42) % 32362.0 / 32362.0;
    }

    public double generadorFishmanMoore(){
        xGenerador_FishmanMoore = 69621 * xGenerador_FishmanMoore % (((long) Math.pow(2, 31)) - 1);
        return xGenerador_FishmanMoore / (Math.pow(2, 31) - 1);
    }

    public double generadorRandu(){
        xGenerador_Randu = (((long) Math.pow(2, 16)) + 3) * xGenerador_Randu % ((long) Math.pow(2, 31));
        return xGenerador_Randu / Math.pow(2, 31);
    }

    public static void testGeneradores(){

        randomGenerator r = new randomGenerator();
        double v;

        for (int i=0; i<8; i++) r.generador26_1_a();
        v = r.generador26_1_a() * Math.pow(2, 5);
        assert v == 5;

        for (int i=0; i<4; i++) r.generador26_1_b();
        v = r.generador26_1_b() * Math.pow(2, 5);
        assert v == 7;

        for (int i=0; i<16; i++) r.generador26_2();
        v = r.generador26_2() * 31.0;
        assert v == 22;

        for (int i=0; i<9999; i++) r.generador26_3();
        v = r.generador26_3() * (Math.pow(2, 31) - 1);
        assert v == 1043618065;

        //for (int i=0; i<1001; i++) r.generador26_42();
        //assert r.generador26_3() * (Math.pow(2, 31) - 1) == 1043618065;

        //for (int i=0; i<1001; i++) r.generador26_3();
        //assert r.generador26_3() * (Math.pow(2, 31) - 1) == 1043618065;
    }
}
