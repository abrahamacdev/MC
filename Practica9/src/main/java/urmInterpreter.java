import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class urmInterpreter {

    // Dispondremos de 8 registros
    private int[] registros = new int[8];

    private int indxInstActual = 0; // Siguiente instrucción a ejecutar
    private String[] instrucciones; // Almacena las instrucciones a ejecutar
    private ArrayList<String> traza = new ArrayList<>();    // Almacena la traza del programa

    public urmInterpreter (String programa){
        iniciar(programa);
    }


    private void iniciar(String programa){

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

    public void interpretarSiguiente(){

        // El programa acaba cuando instActual = |programa| + 1
        if (indxInstActual < instrucciones.length){

            String actual = instrucciones[indxInstActual];
            String instruccion = actual.substring(0,1);
            String[] argumentos = actual.substring(1)
                    .replaceAll("\\(", "")
                    .replaceAll("\\)", "")
                    .split(",");

            System.out.println("Ejecutando " + instruccion + " con params: " + Arrays.toString(argumentos));
            ejecutarInstruccion(instruccion, argumentos);
            System.out.println("Resultado => " + configuracionActual());
        }
    }

    public void interpretarTodo(){
        while (!ejecucionTerminada()){
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




    public boolean ejecucionTerminada(){
        return indxInstActual >= instrucciones.length;
    }
}