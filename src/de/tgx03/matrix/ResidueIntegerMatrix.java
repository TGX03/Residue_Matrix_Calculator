package de.tgx03.matrix;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * A class representing a matrix consisting of residue classes
 */
public class ResidueIntegerMatrix implements Cloneable, Serializable {

    private final ResidueClassInteger[][] matrix;
    private final int x;
    private final int y;

    /**
     * Initializes a new matrix with a given width and a given set of entries
     * The height gets calculated automatically
     * Throws an error if the matrix cannot be completely filled
     *
     * @param width The width of this matrix
     * @param ints  All the entries of this matrix
     */
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

    /**
     * Initializes a new matrix rom a given set of integers, a given residue and a given width
     * The height gets calculated automatically
     * Throws an error if not enough entries to fill the matrix are provided
     *
     * @param width   The width of this matrix
     * @param residue The residue class of this matrix
     * @param values  All the values this matrix shall hold
     */
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

    /**
     * Creates a new empty matrix with given dimensions
     *
     * @param x The width of the matrix
     * @param y The height of the matrix
     */
    private ResidueIntegerMatrix(int x, int y) {
        this.matrix = new ResidueClassInteger[y][x];
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the value stored at a specific place in this matrix
     *
     * @param x The x coordinate in this matrix
     * @param y The y coordinate in this matrix
     * @return The integer at that position
     */
    public long getValue(int x, int y) {
        return this.matrix[y][x].value;
    }

    /**
     * Returns the Residue Class at a given position in this matrix
     *
     * @param x The x coordinate in this matrix
     * @param y The y coordinate in this matrix
     * @return The residue integer at that position
     */
    public ResidueClassInteger get(int x, int y) {
        return this.matrix[y][x];
    }

    /**
     * Executes the Gauss algorithm and returns a new matrix after the algorithm and the steps
     * needed to create it
     *
     * @return A gaussed matrix and the steps taken to get it
     */
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

    /**
     * Uses the gauss algorithm to rearrange this matrix in steps and returns a new matrix of it
     *
     * @return The solved matrix
     */
    public ResidueIntegerMatrix solve() {
        ResidueIntegerMatrix clone = this.clone();
        int offset = 0;
        int column = 0;

        while (column + offset < clone.y && column < clone.x) {

            // Set non-zero value to diagonal position
            if (clone.matrix[column][column + offset].value == 0L) {
                boolean done = false;
                for (int line = column; line < clone.y && !done; line++) {
                    if (clone.matrix[line][column + offset].value != 0L) {
                        clone.swapLines(line, column);
                        done = true;
                    }
                }
                if (!done) {
                    offset++;
                    break;
                }
            }

            // Set current column below the pivot to zero
            for (int line = column + 1; line < clone.y; line++) {
                ResidueClassInteger factor = clone.findFactor(clone.matrix[line][column + offset], clone.matrix[column][column + offset]);
                clone.addLines(column, line, factor);
            }
            column++;
        }
        return clone;
    }

    /**
     * Multiplies this matrix with a given vector and returns the resulting vector
     *
     * @param vector The vector to multiply this matrix with
     * @return The resulting vector
     */
    public ResidueIntegerVector multiply(ResidueIntegerVector vector) {
        if (vector.size() != this.x) {
            throw new IllegalArgumentException("Vector must have the same number of entries as this matrix hast horizontal entries");
        }
        ResidueClassInteger[] result = new ResidueClassInteger[this.y];
        final long residue = this.matrix[0][0].residue;
        for (int line = 0; line < this.y; line++) {
            ResidueClassInteger current = new ResidueClassInteger(0, residue);
            for (int column = 0; column < this.matrix[line].length; column++) {
                current = current.add(this.matrix[line][column]);
            }
            result[line] = current;
        }
        return new ResidueIntegerVector(result);
    }

    /**
     * Multiplies this matrix with another matrix
     * with this matrix being the left matrix
     * and the matrix given as argument being the right matrix
     * For this both matrices must be square and have the same dimensions
     *
     * @param matrix The second matrix to multiply this matrix with
     * @return The resulting matrix
     */
    public ResidueIntegerMatrix multiply(ResidueIntegerMatrix matrix) {
        if (this.x != matrix.y) {
            throw new IllegalArgumentException("First matrix must have as many columns as the second matrix has lines");
        }
        ResidueIntegerMatrix result = new ResidueIntegerMatrix(matrix.x, this.y);
        if (this.y >= matrix.x) {
            IntStream.range(0, this.y).parallel().forEach(y -> {
                for (int x = 0; x < matrix.x; x++) {
                    result.matrix[y][x] = multiplyPosition(x, y, matrix);
                }
            });
        } else {
            IntStream.range(0, this.x).parallel().forEach(x -> {
                for (int y = 0; y < matrix.y; y++) {
                    result.matrix[y][x] = multiplyPosition(x, y, matrix);
                }
            });
        }
        return result;
    }

    /**
     * Swaps two lines in this matrix
     *
     * @param i The first line to be swapped
     * @param j The second line to be swapped with the first
     */
    private void swapLines(int i, int j) {
        if (i == j || i >= this.y || j >= this.y) {
            throw new IllegalArgumentException("Invalid line numbers");
        }
        ResidueClassInteger[] first = matrix[i];
        matrix[i] = matrix[j];
        matrix[j] = first;
    }

    /**
     * Adds a multiple of one line to another line
     *
     * @param source The line that a multiple of should be added to another line
     * @param target The line the source line shall be added to
     * @param factor How often the source line shall be added to the target
     */
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

    /**
     * Calculates the value which results during matrix multiplication
     * at a specific point in the matrix
     *
     * @param x      The x coordinate in the resulting matrix
     * @param y      The y coordinate in the resulting matrix
     * @param matrix The other matrix
     * @return The value calculated at that position
     */
    private ResidueClassInteger multiplyPosition(int x, int y, ResidueIntegerMatrix matrix) {
        ResidueClassInteger result = new ResidueClassInteger(0, this.matrix[0][0].residue);
        for (int i = 0; i < this.x; i++) {
            result = result.add(this.matrix[y][i].multiply(matrix.matrix[i][x]));
        }
        return result;
    }

    @Override
    public ResidueIntegerMatrix clone() {
        ResidueIntegerMatrix clone = new ResidueIntegerMatrix(this.x, this.y);
        for (int y = 0; y < this.y; y++) {
            System.arraycopy(this.matrix[y], 0, clone.matrix[y], 0, this.x);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResidueIntegerMatrix that = (ResidueIntegerMatrix) o;
        return x == that.x && y == that.y && Arrays.deepEquals(matrix, that.matrix);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(matrix);
    }

    /**
     * A record holding a solved matrix and the steps taken to solve it
     */
    public static record SolvedMatrix(String steps, ResidueIntegerMatrix matrix) {
    }
}
