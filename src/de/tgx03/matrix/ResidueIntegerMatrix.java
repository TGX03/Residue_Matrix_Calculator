package de.tgx03.matrix;

import java.io.Serializable;

public class ResidueIntegerMatrix implements Cloneable, Serializable {

    private final ResidueClassInteger[][] matrix;
    private final int x;
    private final int y;

    public ResidueIntegerMatrix(int width, ResidueClassInteger... ints) {
        this.x = width;
        this.y = ints.length / width;
        if (ints.length % width != 0) {
            throw new IllegalArgumentException("Not enough values to fill the matrix");
        }
        matrix = new ResidueClassInteger[this.y][this.x];
        int current = 0;
        for (int y = 0; y < this.y; y++) {
            for (int x = 0; x < this.x; x++) {
                matrix[y][x] = ints[current];
                current++;
            }
        }
    }

    public ResidueIntegerMatrix(int width, long residue, long... values) {
        this.x = width;
        this.y = values.length / width;
        if (values.length % width != 0) {
            throw new IllegalArgumentException("Not enough values to fill the matrix");
        }
        matrix = new ResidueClassInteger[this.y][this.x];
        int current = 0;
        for (int y = 0; y < this.y; y++) {
            for (int x = 0; x < this.x; x++) {
                matrix[y][x] = new ResidueClassInteger(values[current], residue);
                current++;
            }
        }
    }

    private ResidueIntegerMatrix(int x, int y) {
        this.matrix = new ResidueClassInteger[y][x];
        this.x = x;
        this.y = y;
    }

    public SolvedMatrix gaussWithSteps() {
        ResidueIntegerMatrix clone = this.clone();
        StringBuilder steps = new StringBuilder();
        int offset = 0;
        int column = 0;

        while (column + offset < clone.y && column < clone.x) {

            // Set non-zero value to diagonal position
            if (clone.matrix[column][column + offset].value == 0L) {
                boolean done = false;
                for (int line = column; line < clone.y && !done; line++) {
                    if (clone.matrix[line][column + offset].value != 0L) {
                        steps.append("Swapping lines ").append(column).append(" and ").append(line).append(":");
                        clone.swapLines(line, column);
                        steps.append(System.lineSeparator()).append(clone);
                        done = true;
                    }
                }
                if (!done) {
                    steps.append("Didn't find a pivot").append(System.lineSeparator());
                    offset++;
                    break;
                }
            }

            // Set current column below the pivot to zero
            for (int line = column + 1; line < clone.y; line++) {
                ResidueClassInteger factor = clone.findFactor(clone.matrix[line][column + offset], clone.matrix[column][column + offset]);
                steps.append("Adding ").append(factor.value).append(" times line ").append(column).append(" to line ").append(line).append(System.lineSeparator());
                clone.addLines(column, line, factor);
                steps.append(clone).append(System.lineSeparator());
            }
            column++;
        }
        return new SolvedMatrix(steps.toString(), clone);
    }

    private void swapLines(int i, int j) {
        if (i == j || i >= this.y || j >= this.y) {
            throw new IllegalArgumentException("Invalid line numbers");
        }
        ResidueClassInteger[] first = matrix[i];
        matrix[i] = matrix[j];
        matrix[j] = first;
    }

    private void addLines(int source, int target, ResidueClassInteger factor) {
        for (int i = 0; i < this.x; i++) {
            matrix[target][i] = matrix[target][i].add(matrix[source][i].multiply(factor));
        }
    }

    /**
     * Calculates how often a value needs to be added to a fixed value to make it 0
     *
     * @param fixed    The value to be added to
     * @param toFactor The value which's multiple shall be added to the fixed to make it 0
     * @return How often it needs to be added
     */
    private ResidueClassInteger findFactor(ResidueClassInteger fixed, ResidueClassInteger toFactor) {
        long current = 0;
        final long residue = fixed.residue;
        while (((current * toFactor.value) + fixed.value) % residue != 0) {
            current++;
        }
        return new ResidueClassInteger(current, residue);
    }

    @Override
    public ResidueIntegerMatrix clone() {
        ResidueIntegerMatrix clone = new ResidueIntegerMatrix(this.x, this.y);
        for (int x = 0; x < this.x; x++) {
            System.arraycopy(this.matrix[x], 0, clone.matrix[x], 0, this.y);
        }
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(this.x * this.y * 2);
        for (int y = 0; y < this.y; y++) {
            builder.append("[");
            for (int x = 0; x < this.x; x++) {
                if (x != 0) {
                    builder.append(";");
                }
                builder.append(matrix[y][x]);
            }
            builder.append("]").append(System.lineSeparator());
        }
        return builder.toString();
    }

    public static record SolvedMatrix(String steps, ResidueIntegerMatrix matrix) {
    }
}
