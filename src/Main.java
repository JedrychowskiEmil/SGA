import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        double a = -0.2;
        double b = 40;
        double c = 550;
        int numOfGenerations = 3;
        int populationSize = 50;
        double mutationProbability = 0.01;
        double crossingProbability = 0.8;

        //Wyniki zapisywane w SGA.txt


        StringBuilder returnFile = new StringBuilder();
        //Ile razy ma się program wykonać
        for (int howManyTimes = 0; howManyTimes < 40; howManyTimes++) {
            SGA sga = new SGA(a, b, c, numOfGenerations, populationSize, mutationProbability, crossingProbability);
            returnFile.append(sga.core());
            returnFile.append("\n");
        }
        try (FileWriter fileWriter = new FileWriter("SGA.txt")) {

            fileWriter.write(returnFile.toString());


        } catch (IOException e) {
            System.out.println("Error in Main/FileWriter/returnFile");
        }


        double biggest = Integer.MIN_VALUE;
        int forXValue = -1;
        for (int i = 0; i < 256; i++) {
            final double quadraticFunction = (a * Math.pow(i, 2)) + (b * i) + c;
            if (quadraticFunction > biggest){
                biggest = quadraticFunction;
                forXValue = i;
            }
        }
        System.out.println(biggest + " is a maximum possible value of function for value of x=" + forXValue);
        System.out.println("Results in file SGA.txt");
    }
}
