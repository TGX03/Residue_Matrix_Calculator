package de.tgx03.matrix;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A class representing a vector which operates inside a residue class
 */
public class ResidueIntegerVector implements Cloneable, Serializable {

    private final ResidueClassInteger[] vector;

    /**
     * Creates a new vector from a given set of residue integers
     *
     * @param values The values this vector holds
     */
    public ResidueIntegerVector(ResidueClassInteger... values) {
        vector = new ResidueClassInteger[values.length];
        System.arraycopy(values, 0, vector, 0, values.length);
    }

    /**
     * Creates a new vector from a given residue and a given set of values
     *
     * @param residue The residue class this vector is in
     * @param values  The values of this vector
     */
    public ResidueIntegerVector(long residue, long... values) {
        vector = new ResidueClassInteger[values.length];
        for (int i = 0; i < vector.length; i++) {
            ResidueClassInteger current = new ResidueClassInteger(values[i], residue);
            vector[i] = current;
        }
    }

    /**
     * Initializes a new empty vector with a given size
     *
     * @param size The size of the new vector
     */
    private ResidueIntegerVector(int size) {
        vector = new ResidueClassInteger[size];
    }

    /**
     * Returns the value at a specified position
     *
     * @param position The position to return
     * @return The value at that position
     */
    public long getValue(int position) {
        return vector[position].value;
    }

    /**
     * Returns the residue class integer at a specified position
     *
     * @param position The position to return
     * @return The value at that position
     */
    public ResidueClassInteger get(int position) {
        return vector[position];
    }

    /**
     * Returns how many entries this vector holds
     *
     * @return The size of this vector
     */
    public int size() {
        return vector.length;
    }

    /**
     * Adds another vector to this vector and returns the result
     *
     * @param vector The other vector to add to
     * @return The resulting vector
     */
    public ResidueIntegerVector add(ResidueIntegerVector vector) {
        if (this.vector.length != vector.vector.length) {
            throw new IllegalArgumentException("Vectors must have the same size to add them");
        }
        ResidueIntegerVector result = new ResidueIntegerVector(this.vector.length);
        for (int i = 0; i < this.vector.length; i++) {
            result.vector[i] = this.vector[i].add(vector.vector[i]);
        }
        return result;
    }

    /**
     * Multiplies this vector with another vector and returns the result
     * The vector given as argument represents the second vector,
     * while the vector the arguments gets given to acts as the first vector
     *
     * @param vector The second vector to multiply with
     * @return The resulting vector
     */
    public ResidueIntegerVector multiply(ResidueIntegerVector vector) {
        if (this.vector.length != vector.vector.length) {
            throw new IllegalArgumentException("Vectors must have the same size to add them");
        }

        ResidueClassInteger[] first = new ResidueClassInteger[this.vector.length];
        ResidueClassInteger[] second = new ResidueClassInteger[vector.vector.length];
        System.arraycopy(this.vector, 1, first, 0, this.vector.length - 1);
        System.arraycopy(vector.vector, 1, second, 0, this.vector.length - 1);
        first[first.length - 1] = this.vector[0];
        second[second.length - 1] = vector.vector[0];

        ResidueIntegerVector result = new ResidueIntegerVector(this.vector.length);

        for (int i = 0; i < first.length; i++) {
            ResidueClassInteger minuend;
            ResidueClassInteger subtrahend;
            if (i == first.length - 1) {
                minuend = first[i].multiply(second[0]);
                subtrahend = first[0].multiply(second[i]);
            } else {
                minuend = first[i].multiply(second[i + 1]);
                subtrahend = first[i + 1].multiply(second[i]);
            }
            result.vector[i] = minuend.subtract(subtrahend);
        }
        return result;
    }

    /**
     * Multiplies this vector with the given value
     *
     * @param value The factor to apply to this vector
     * @return The resulting vector
     */
    public ResidueIntegerVector multiply(ResidueClassInteger value) {
        ResidueIntegerVector result = new ResidueIntegerVector(this.vector.length);
        for (int i = 0; i < this.vector.length; i++) {
            result.vector[i] = this.vector[i].multiply(value);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(this.vector.length * 2);
        builder.append("{");
        for (int i = 0; i < this.vector.length; i++) {
            if (i != 0) {
                builder.append(";");
            }
            builder.append(this.vector[i]);
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.vector);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ResidueIntegerVector vector) {
            return Arrays.equals(this.vector, vector.vector);
        }
        return false;
    }

    @Override
    public ResidueIntegerVector clone() {
        ResidueIntegerVector clone = new ResidueIntegerVector(this.vector.length);
        System.arraycopy(this.vector, 0, clone.vector, 0, this.vector.length);
        return clone;
    }
}
