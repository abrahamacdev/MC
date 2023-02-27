import java.io.IOException;

public class main {

    public static void main(String[] args) throws InstantiationException, IOException {

        int k = 3;
        int codigo = 792;
        int generaciones = 900;

        ca1DSim automata = new ca1DSim(k, codigo, generaciones);

        gui gui = new gui();
        gui.crearVentana();
    }
}
