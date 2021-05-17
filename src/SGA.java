

import java.util.concurrent.ThreadLocalRandom;

/*
*SGA - Simple Genetic Algorithm
* Tries to find biggest quadratic function of x from range with certain a,b,c
*
* */

public class SGA {

    //Arguments
    private double a; //x^2
    private double b; //x^1
    private double c; //x^0
    private double maxValue; //Max value of function for safety reasons f(x) can never be < 0 so just add this to be sure

    //Range of X
    private int start = 0;
    private int end = 255;

    //Main Data
    private int numOfGenerations; //
    private int populationSize; //population in each generation
    private double mutationProbability;
    private double crossingProbability;

    //Generated generation
    private String[][] generation;
    private int currentGeneration;
    private double[] worthOfUnit;


    public SGA(double a, double b, double c, int numOfGenerations, int populationSize, double mutationProbability, double crossingProbability) {
        this.a = a;
        this.b = b;
        this.c = c;

        this.numOfGenerations = numOfGenerations;
        this.populationSize = populationSize;
        this.mutationProbability = mutationProbability;
        this.crossingProbability = crossingProbability;

        this.generation = new String[numOfGenerations][populationSize];
        this.currentGeneration = 0;
        this.worthOfUnit = new double[populationSize];
    }

    public String core() {
        this.maxValue = letsPlaySafe();
        while (numOfGenerations-- > 0) {
            selection();
            makeRoulette();
            crossing();
            mutate();
            //   printGeneration(currentGeneration);
            currentGeneration++;
        }
        return findBiggest();
    }


    private double targetFun(int x) {
        return (a * Math.pow(x, 2)) + (b * x) + c;
    }

    //Finds biggest in generation, values are in binary in String format - swap to int and compare
    private String findBiggest() {
        double max = Integer.MIN_VALUE;
        int index = 0;
        for (int i = 0; i < populationSize; i++) {
            if (targetFun(Integer.parseInt(generation[currentGeneration - 1][i], 2)) > max) {
                max = targetFun(Integer.parseInt(generation[currentGeneration - 1][i], 2));
                index = i;
            }
        }

        // return max value with its "x"
        return max + " " + Integer.parseInt(generation[currentGeneration - 1][index], 2);
    }

    //Draw population of new generation from older generation,
    //if its 1st generation then its draw numbers from range (start, end)
    private void selection() {
        if (currentGeneration == 0) {

            for (int i = 0; i < populationSize; i++) {
                generation[currentGeneration][i] = String.format("%8s", Integer.toBinaryString((ThreadLocalRandom.current().nextInt(start, end + 1)))).replace(' ', '0');

            }

            makeRoulette();
            for (int i = 0; i < populationSize; i++) {
                generation[currentGeneration][i] = generation[currentGeneration][rouletteShot(Math.random())];
            }

        } else {
            for (int i = 0; i < populationSize; i++) {
                generation[currentGeneration][i] = generation[currentGeneration - 1][rouletteShot(Math.random())];
            }
        }
    }

    //Assign each unit his worth for this generation basted on how big they targetFun is compared to sum of targetFun whole generation
    private void makeRoulette() {
        double sum = 0;
        for (int i = 0; i < populationSize; i++) {
            worthOfUnit[i] = (targetFun(Integer.parseInt(generation[currentGeneration][i], 2)));
            worthOfUnit[i] -= maxValue;
            sum += worthOfUnit[i];
        }

        for (int i = 0; i < populationSize; i++) {
            worthOfUnit[i] /= sum;
        }
    }

    //In loop adds worth of unit to sum and checks if randomValue is <= to that value,
    //if not then add another worthOfUnit to sum till it find it and return index of unit it stopped on
    private int rouletteShot(double randomValue) {
        double sum = 0;

        for (int i = 0; i < populationSize; i++) {
            sum += worthOfUnit[i];
            if (randomValue <= sum) return i;
        }

        System.out.println("Error in rouletteShot");
        System.out.println("Random value =" + randomValue);
        System.out.println("Sum =" + sum);
        return -1;
    }

    //If randomly generated number is smaller or equal to mutation chance then it mutates gene.
    //Works for each unit in generation
    private void mutate() {
        for (int i = 0; i < populationSize; i++) {
            //if (Math.random() <= mutationProbability) // if you want it to draw chance of mutation both for unit and its genes (without it its only genes)
            for (int j = 0; j < generation[currentGeneration][i].length(); j++) {
                if (Math.random() <= mutationProbability) {
                    if (generation[currentGeneration][i].charAt(j) == 0) {
                        char[] tmp = generation[currentGeneration][i].toCharArray();
                        tmp[j] = '1';
                    } else {
                        char[] tmp = generation[currentGeneration][i].toCharArray();
                        tmp[j] = '0';
                    }
                }
            }
        }
    }

    //Draw units into pairs from pool of units that were not picked yet till everyone is draft,
    //then crossingProbability draw if they are supposed to get crossed
    private void crossing() {
        int separator = populationSize - 1; //separates units that already become a parent from the ona that did not
        while (separator > 0) {
            int first = ThreadLocalRandom.current().nextInt(0, separator + 1);
            int second;
            while ((second = ThreadLocalRandom.current().nextInt(0, separator + 1)) == first) ;

            //System.out.println(generation[currentGeneration][first]);
            //System.out.println(generation[currentGeneration][second]);
            if (Math.random() <= crossingProbability) {
                int cut = ThreadLocalRandom.current().nextInt(1, 8);
                String descendant1 = generation[currentGeneration][first].substring(0, cut) + generation[currentGeneration][second].substring(cut);
                String descendant2 = generation[currentGeneration][second].substring(0, cut) + generation[currentGeneration][first].substring(cut);

                generation[currentGeneration][first] = descendant1;
                generation[currentGeneration][second] = descendant2;
            }

            String temp = generation[currentGeneration][separator];
            generation[currentGeneration][separator] = generation[currentGeneration][first];
            generation[currentGeneration][first] = temp;

            String temp2 = generation[currentGeneration][separator - 1];
            generation[currentGeneration][separator - 1] = generation[currentGeneration][second];
            generation[currentGeneration][second] = temp2;

            separator -= 2;

        }
    }

    //Finds biggest value of fun using Sequential Search
    private double letsPlaySafe() {
        double min = 0;
        for (int i = start; i <= end; i++) {
            if (targetFun(i) < min) {
                min = targetFun(i);
            }
        }
        //System.out.println(min);
        if(min <= 0) {
            return min - 1; //for case a=0, b=0, c=0
        }else{
            return 0;
        }
    }

    private void printGeneration(int num) {
        for (int i = 0; i < populationSize; i++) {
            System.out.print(Integer.parseInt(generation[num][i], 2) + ", ");
        }
        System.out.println();
    }
}
