import java.util.ArrayList;
import java.util.Arrays;

public class urmInterpreter {

    // Dispondremos de 8 registros
    private int[] registros;
    private int[] registrosIniciales;

    private boolean valido = true;

    private int indxInstActual = 0; // Siguiente instrucción a ejecutar
    private String[] instrucciones; // Almacena las instrucciones a ejecutar
    private ArrayList<String> traza = new ArrayList<>();    // Almacena la traza del programa

    public urmInterpreter (String programa, int[] registros){
        iniciar(programa, registros);
    }



    private void iniciar(String programa, int[] registros){

        this.registros = registros;
        this.registrosIniciales = Arrays.copyOf(registros, registros.length);

        // Tiene que haber al menos una instruccion
        if (programa.length() > 0){
            // Quitamos espacios
            String programaSinEspacios = programa.replaceAll(" ", "");

            // Separamos las instrucciones por salto de línea
            instrucciones = programaSinEspacios.split("\n");

            // Añadimos a la traza la siguiente instruccion a ejecutar
            traza.add(configuracionActual());
        }
    }

    public void reset(){
        indxInstActual = 0;
        traza.clear();
        registros = Arrays.copyOf(registrosIniciales, registrosIniciales.length);;
    }

    public void interpretarSiguiente(){

        // El programa acaba cuando instActual = |programa| + 1
        if (indxInstActual < instrucciones.length){

            String actual = instrucciones[indxInstActual];
            String instruccion = actual.substring(0,1);
            String[] argumentos = actual.substring(1)
                    .replaceAll("\\(", "")
                    .replaceAll("\\)", "")
                    .split(",");

            ejecutarInstruccion(instruccion, argumentos);
        }
    }

    public void interpretarTodo(){
        while (!ejecucionTerminada()){
            System.out.println("Instruccion actual " + indxInstActual);
            interpretarSiguiente();
        }
    }

    private void ejecutarInstruccion(String instruccion, String[] params){

        // Ejecutamos la instruccion que toque
        switch (instruccion){

            case "S":
                registros[Integer.parseInt(params[0])-1]++;
                indxInstActual++;
                break;

            case "Z":
                registros[Integer.parseInt(params[0])-1] = 0;
                indxInstActual++;
                break;

            case "J":
                // Rm == Rn => i
                if (registros[Integer.parseInt(params[0])-1] == registros[Integer.parseInt(params[1])-1]){
                    indxInstActual = Integer.parseInt(params[2]);
                }
                // Rm != Rn => actual+1
                else indxInstActual++;

                break;

            case "T":
                registros[Integer.parseInt(params[1])-1] = registros[Integer.parseInt(params[0])-1];
                indxInstActual++;
                break;
        }

        // Añadimos a la traza la siguiente instruccion a ejecutar
        traza.add(configuracionActual());
    }

    private String configuracionActual(){
        StringBuilder conf = new StringBuilder("<" + indxInstActual + ", <");

        // Añadimos el estado
        for (int i=0; i< registros.length; i++){

            // Añadimos el contenido del i-ésimo registro
            conf.append("R" + (i+1) + "=" + registros[i]);

            if (i != registros.length-1){
                conf.append(", ");
            }
        }
        conf.append(">>");
        return conf.toString();
    }




    public ArrayList<String> getTraza() {
        return traza;
    }

    public boolean ejecucionTerminada(){
        return indxInstActual >= instrucciones.length;
    }

    public int getResultado(){
        if (ejecucionTerminada()) return registros[0];
        return -1;
    }

    public boolean isValido() {
        return valido;
    }

    public void setValido(boolean valido) {
        this.valido = valido;
    }
}