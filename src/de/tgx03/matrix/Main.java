package de.tgx03.matrix;

public class Main {

    public static void main(String[] args) {
        ResidueIntegerMatrix matrix = new ResidueIntegerMatrix(4, 10, 4, 4, 2, 0, 4, 3, 3, 3, 4, 1, 0, 4, 0, 4, 1, 3);
        ResidueIntegerMatrix.SolvedMatrix result = matrix.gaussWithSteps();
        System.out.println(result.steps());
        System.out.println(result.matrix().toString());
    }
}
