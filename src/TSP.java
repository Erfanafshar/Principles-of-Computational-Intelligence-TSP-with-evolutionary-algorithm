import java.io.File;
import java.util.*;

public class TSP {
    private final static int NUMBER_OF_CHROMOSOME = 15000;
    private final static int NUMBER_OF_PARENT_SELECTION = 7500;
    private final static int NUMBER_OF_CHILDREN_CREATION = 15000;
    private final static double MUTATION_RATE = 0.1;
    private final static int TOURNAMENT_SIZE = 30;
    private int GENES_IN_CHROMOSOME;
    private final static int COLUMNS_OF_TABLE = 3;

    private double cities[][];

    private int parents[][];
    private int children[][];
    private double parentFitness[] = new double[NUMBER_OF_CHROMOSOME];
    private int selectedParentsIndex[] = new int[NUMBER_OF_PARENT_SELECTION];
    private double childrenFitness[] = new double[NUMBER_OF_CHILDREN_CREATION];

    private List<Integer> numbersList = new ArrayList<>();
    private Scanner scanner1;
    private Scanner scanner2;

    private int maximumIndex;

    TSP() {
        GENES_IN_CHROMOSOME = 0;

    }

    public void createScanner() {
        try {
            File file = new File("tsp_data.txt");
            scanner1 = new Scanner(file);
            scanner2 = new Scanner(file);
        } catch (Exception e) {
            System.out.println("file not found");
        }
    }

    public void countLines() {
        int cnt = 0;
        while (scanner1.hasNextLine()) {
            cnt++;
            scanner1.nextLine();
        }
        GENES_IN_CHROMOSOME = cnt;
    }

    public void setArrays() {
        cities = new double[GENES_IN_CHROMOSOME][COLUMNS_OF_TABLE];
        parents = new int[NUMBER_OF_CHROMOSOME][GENES_IN_CHROMOSOME];
        children = new int[NUMBER_OF_CHILDREN_CREATION][GENES_IN_CHROMOSOME];
    }

    public void setCities() {
        String spt[];
        int cnt = 0;
        while (scanner2.hasNextLine()) {
            spt = scanner2.nextLine().split(" ");
            for (int i = 0; i < COLUMNS_OF_TABLE; i++) {
                cities[cnt][i] = Double.valueOf(spt[i]);
            }
            cities[cnt][0]--;
            cnt++;
        }
    }

    private void setNumbersList() {
        for (int i = 0; i < GENES_IN_CHROMOSOME; i++) {
            numbersList.add(i);
        }
    }

    private void getRandomPermutation() {
        Collections.shuffle(numbersList, new Random());
    }

    public void createFirsPopulation() {
        setNumbersList();
        for (int i = 0; i < NUMBER_OF_CHROMOSOME; i++) {
            getRandomPermutation();
            for (int j = 0; j < GENES_IN_CHROMOSOME; j++) {
                parents[i][j] = numbersList.get(j);
            }
        }
    }

    public void evaluation(int version) {
        if (version == 0)
            evaluationCalculation(parents, parentFitness, NUMBER_OF_CHROMOSOME);
        if (version == 1) {
            evaluationCalculation(children, childrenFitness, NUMBER_OF_CHILDREN_CREATION);
            printResult();
        }
    }

    private void printResult() {
        double maxValue = Integer.MIN_VALUE;
        int maxIndex = -1;
        for (int i = 0; i < NUMBER_OF_CHILDREN_CREATION; i++) {
            if (childrenFitness[i] > maxValue) {
                maxValue = childrenFitness[i];
                maxIndex = i;
            }
        }
        System.out.println("total distance : " + 100000.0 / maxValue);
        maximumIndex = maxIndex;
    }

    private void evaluationCalculation(int evalArray[][], double fitArray[], int upper) {
        int val;
        for (int i = 0; i < upper; i++) {
            val = 0;
            for (int j = 0; j < GENES_IN_CHROMOSOME - 1; j++) {
                val += calculateDistance(evalArray[i][j], evalArray[i][j + 1]);
            }
            val += calculateDistance(evalArray[i][GENES_IN_CHROMOSOME - 1], evalArray[i][0]);
            fitArray[i] = (1.0 / val) * 100000;
        }
    }

    private double calculateDistance(int numCity1, int numCity2) {
        double distance = Math.pow((cities[numCity1][1] - cities[numCity2][1]), 2)
                + Math.pow((cities[numCity1][2] - cities[numCity2][2]), 2);
        return Math.sqrt(distance);
    }

    public void selection(int version) {
        switch (version) {
            case 0:
                rouletteWheel();
                break;
            case 1:
                sus();
                break;
            case 2:
                tournament();
                break;
        }
    }

    private void rouletteWheel() {
        double[] points = createRuler();
        double sumFitness = points[NUMBER_OF_CHROMOSOME - 1];
        double[] randomNumbers = setRandomNumbers1(sumFitness);
        setSelectedParentsIndex(points, randomNumbers);
    }

    private double[] createRuler() {
        double[] points = new double[NUMBER_OF_CHROMOSOME];
        double sumFitness = 0.0;
        for (int i = 0; i < NUMBER_OF_CHROMOSOME; i++) {
            sumFitness += parentFitness[i];
            points[i] = sumFitness;
        }
        return points;
    }

    private double[] setRandomNumbers1(double sumFitness) {
        double[] randomNumbers = new double[NUMBER_OF_PARENT_SELECTION];
        for (int i = 0; i < NUMBER_OF_PARENT_SELECTION; i++) {
            Random random = new Random();
            randomNumbers[i] = random.nextDouble() * sumFitness;
        }
        return randomNumbers;
    }


    private void setSelectedParentsIndex(double[] points, double[] randomNumbers) {
        for (int i = 0; i < NUMBER_OF_PARENT_SELECTION; i++) {
            binarySearch(points, randomNumbers[i], i);
        }
    }

    private void binarySearch(double[] points, double randomNumber, int k) {
        int fIndex = 0;
        int eIndex = NUMBER_OF_CHROMOSOME - 1;
        int mIndex = (eIndex + fIndex) / 2;
        double mPoint = points[mIndex];
        while (true) {
            if (randomNumber < mPoint) {
                eIndex = mIndex;
            } else {
                fIndex = mIndex;
            }
            mIndex = (fIndex + eIndex) / 2;
            mPoint = points[mIndex];
            if (mIndex == fIndex) {
                selectedParentsIndex[k] = mIndex + 1;
                break;
            }
        }
    }

    private void sus() {
        double[] points = createRuler();
        double sumFitness = points[NUMBER_OF_CHROMOSOME - 1];
        double[] randomNumbers = setRandomNumbers2(sumFitness);
        setSelectedParentsIndex(points, randomNumbers);
    }

    private double[] setRandomNumbers2(double sumFitness) {
        double[] randomNumbers = new double[NUMBER_OF_PARENT_SELECTION];
        Random random = new Random(System.currentTimeMillis());
        double step = sumFitness / NUMBER_OF_PARENT_SELECTION;
        randomNumbers[0] = random.nextDouble() * step;
        for (int i = 1; i < NUMBER_OF_PARENT_SELECTION; i++) {
            randomNumbers[i] = randomNumbers[i - 1] + step;
        }
        return randomNumbers;
    }

    private void tournament() {
        for (int i = 0; i < NUMBER_OF_PARENT_SELECTION; i++) {
            Random rand = new Random();
            int[] tournamentParentIndex = rand.ints(TOURNAMENT_SIZE * 2, 0, NUMBER_OF_CHROMOSOME)
                    .distinct().limit(TOURNAMENT_SIZE).toArray();
            tournamentSelection(tournamentParentIndex, i);
        }
    }


    private void tournamentSelection(int[] tournamentParentIndex, int k) {
        int maxIndex = -1;
        double maxVal = Integer.MIN_VALUE;
        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            if (parentFitness[tournamentParentIndex[i]] > maxVal) {
                maxVal = parentFitness[tournamentParentIndex[i]];
                maxIndex = tournamentParentIndex[i];
            }
        }
        selectedParentsIndex[k] = maxIndex;
    }

    public void crossover() {
        for (int i = 0; i < (NUMBER_OF_CHILDREN_CREATION / 2); i++) {
            int[] randIndex = createTwoRandInteger(NUMBER_OF_PARENT_SELECTION);
            order1crossover(randIndex, 2 * i);
        }
    }

    private void order1crossover(int[] randIndex, int childrenIndex) {
        int[] parent1 = parents[selectedParentsIndex[randIndex[0]]];
        int[] parent2 = parents[selectedParentsIndex[randIndex[1]]];
        int[] child1 = new int[GENES_IN_CHROMOSOME];
        int[] child2 = new int[GENES_IN_CHROMOSOME];
        int[] randomIndex = createTwoRandInteger(GENES_IN_CHROMOSOME);

        order1ChildCreation(randomIndex, parent1, parent2, child1, childrenIndex);
        order1ChildCreation(randomIndex, parent2, parent1, child2, childrenIndex + 1);
    }

    private void order1ChildCreation(int[] randomIndex, int[] parent1, int[] parent2, int[] child, int childIndex) {
        System.arraycopy(parent1, randomIndex[0], child, randomIndex[0], randomIndex[1] - randomIndex[0]);
        int cIndex = randomIndex[1];
        int pIndex = randomIndex[1];
        while (cIndex != randomIndex[0]) {
            boolean found;
            while (true) {
                found = false;
                for (int i = randomIndex[0]; i < randomIndex[1]; i++) {
                    if (child[i] == parent2[pIndex]) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    child[cIndex] = parent2[pIndex];
                    pIndex++;
                    if (pIndex == GENES_IN_CHROMOSOME)
                        pIndex = 0;
                    break;
                } else {
                    pIndex++;
                    if (pIndex == GENES_IN_CHROMOSOME)
                        pIndex = 0;
                }
            }
            cIndex++;
            if (cIndex == GENES_IN_CHROMOSOME)
                cIndex = 0;
        }
        children[childIndex] = child;
    }

    private int[] createTwoRandInteger(int upperBound) {
        Random random = new Random();
        int[] randsIndex = new int[2];
        randsIndex[0] = random.nextInt(upperBound);
        randsIndex[1] = randsIndex[0];
        while (randsIndex[1] == randsIndex[0]) {
            randsIndex[1] = random.nextInt(upperBound);
        }
        Arrays.sort(randsIndex);
        return randsIndex;
    }

    public void mutation(int version) {
        switch (version) {
            case 0:
                insertionMutation();
                break;
            case 1:
                changeMutation();
                break;
            case 2:
                inverseMutation();
                break;
        }
    }

    private void insertionMutation() {
        for (int i = 0; i < NUMBER_OF_CHILDREN_CREATION * MUTATION_RATE; i++) {
            Random random = new Random();
            int childIndex = random.nextInt(NUMBER_OF_CHILDREN_CREATION);
            int[] child = children[childIndex];
            int[] randomIndex = createTwoRandInteger(GENES_IN_CHROMOSOME);
            int tmp = child[randomIndex[1]];
            System.arraycopy(child, randomIndex[0] + 1, child, randomIndex[0] + 1 + 1, randomIndex[1] - 1 - randomIndex[0]);
            child[randomIndex[0] + 1] = tmp;
        }
    }

    private void changeMutation() {
        for (int i = 0; i < NUMBER_OF_CHILDREN_CREATION * MUTATION_RATE; i++) {
            Random random = new Random();
            int childIndex = random.nextInt(NUMBER_OF_CHILDREN_CREATION);
            int[] child = children[childIndex];
            int[] randomIndex = createTwoRandInteger(GENES_IN_CHROMOSOME);
            swap(child, randomIndex[0], randomIndex[1]);
        }
    }

    private void inverseMutation() {
        for (int k = 0; k < NUMBER_OF_CHILDREN_CREATION * MUTATION_RATE; k++) {
            Random random = new Random();
            int childIndex = random.nextInt(NUMBER_OF_CHILDREN_CREATION);
            int[] child = children[childIndex];
            int[] randomIndex = createTwoRandInteger(GENES_IN_CHROMOSOME);
            int endIndex = randomIndex[0] + ((randomIndex[1] - randomIndex[0]) / 2);
            for (int i = randomIndex[0], j = 0; i < endIndex; i++, j++) {
                swap(child, i, randomIndex[1] - j);
            }
        }
    }

    private void swap(int[] child, int randIndex1, int randIndex2) {
        int tmp = child[randIndex2];
        child[randIndex2] = child[randIndex1];
        child[randIndex1] = tmp;
    }

    public void drawResult() {
        new Draw(cities, children[maximumIndex], GENES_IN_CHROMOSOME);
    }

    public void remainingSelection(int version, int iterator) {
        if (version == 0)
            chooseChildren();
        if (version == 1)
            chooseBest(iterator);
    }

    private void chooseChildren() {
        parents = children;
        parentFitness = childrenFitness;
    }

    private void chooseBest(int iterator) {
        int[][] newParents = new int[NUMBER_OF_CHROMOSOME][GENES_IN_CHROMOSOME];
        double newParentsFitness[] = new double[NUMBER_OF_CHROMOSOME];

        ArrayList<Integer> chooseIndex1 = new ArrayList<>();
        double maxVal1 = Integer.MIN_VALUE;
        int maxInx1 = -1;

        ArrayList<Integer> chooseIndex2 = new ArrayList<>();
        double maxVal2 = Integer.MIN_VALUE;
        int maxInx2 = -1;

        int whichChoose = 0;
        int k = 0;
        for (int i = 0; i < NUMBER_OF_CHROMOSOME; i++) {
            if (whichChoose != 2) {
                if (iterator == 0) {
                    maxVal1 = Integer.MIN_VALUE;
                    maxInx1 = -1;
                    for (int j = 0; j < NUMBER_OF_CHROMOSOME; j++) {
                        if (!chooseIndex1.contains(j)) {
                            if (parentFitness[j] > maxVal1) {
                                maxVal1 = parentFitness[j];
                                maxInx1 = j;
                            }
                        }
                    }
                } else {
                    maxVal1 = parentFitness[k];
                    maxInx1 = k;
                    k++;
                }
            }

            if (whichChoose != 1) {
                maxVal2 = Integer.MIN_VALUE;
                maxInx2 = -1;
                for (int j = 0; j < NUMBER_OF_CHILDREN_CREATION; j++) {
                    if (!chooseIndex2.contains(j)) {
                        if (childrenFitness[j] > maxVal2) {
                            maxVal2 = childrenFitness[j];
                            maxInx2 = j;
                        }
                    }
                }
            }

            if (maxVal1 > maxVal2) {
                whichChoose = 1;
                newParents[i] = parents[maxInx1];
                newParentsFitness[i] = parentFitness[maxInx1];
                chooseIndex1.add(maxInx1);
            } else {
                whichChoose = 2;
                newParents[i] = children[maxInx2];
                newParentsFitness[i] = childrenFitness[maxInx2];
                chooseIndex2.add(maxInx2);
            }

        }

        parents = newParents;
        parentFitness = newParentsFitness;
    }
}
