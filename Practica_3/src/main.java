public class main {

    public static void main(String[] args) throws InstantiationException {

        int k = 3;
        int codigo = 792;
        int generaciones = 800;

        ca1DSim automata = new ca1DSim(k, codigo, generaciones);

        while (!automata.haTerminado()){
            automata.evoluciona();
        }
        System.out.println("Evolucionado. Actual " + automata.getNGeneracionActual());
    }
}
