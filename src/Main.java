public class Main {
    private final static int PARENT_SELECTION_VERSION = 2; //// 0 to 2
    private final static int MUTATION_VERSION = 2; //// 0 to 2
    private final static int REMAINING_SELECTION_VERSION = 0; //// 0 to 1
    private final static int NUMBER_OF_GENERATION = 600;

    public static void main(String[] args) {
        TSP tsp = new TSP();

        tsp.createScanner();
        tsp.countLines();
        tsp.setArrays();
        tsp.setCities();
        tsp.createFirsPopulation();
        tsp.evaluation(0);

        int iterator = 0;
        while (iterator != NUMBER_OF_GENERATION) {
            tsp.selection(PARENT_SELECTION_VERSION);
            tsp.crossover();
            tsp.mutation(MUTATION_VERSION);
            tsp.evaluation(1);
            tsp.remainingSelection(REMAINING_SELECTION_VERSION, iterator);
            iterator++;
        }
        tsp.drawResult();
    }
}

