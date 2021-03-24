package de.tgx03.matrix;

import java.io.Serializable;

/**
 * A class representing a residue class integer and methods to add, multiply, subtract and divide
 * multiple residue classes
 */
public class ResidueClassInteger extends Number implements Comparable<ResidueClassInteger>, Serializable, Cloneable {

    public final long value;
    public final long residue;

    /**
     * Creates a new residue class integer
     * @param value The value of this integer, must be lower than the residue
     * @param residue The residue class ring this integer belongs to
     */
    public ResidueClassInteger(long value, long residue) {
        if (value >= residue || value < 0) {
            throw new IllegalArgumentException("Value must be lower than residue and bigger than 0");
        }
        this.value = value;
        this.residue = residue;
    }

    /**
     * Adds to residue integers together and returns the correct result in this class
     * @param summand The other integer to add
     * @return The calculated result
     */
    public ResidueClassInteger add(ResidueClassInteger summand) {
        if (summand.residue != this.residue) {
            throw new IllegalArgumentException("Residues don't match");
        }
        long result = (this.value + summand.value) % this.residue;
        return new ResidueClassInteger(result, this.residue);
    }

    /**
     * Multiplies 2 residue integers and returns the result
     * @param factor The second factor
     * @return The product of both integers
     */
    public ResidueClassInteger multiply(ResidueClassInteger factor) {
        if (factor.residue != this.residue) {
            throw new IllegalArgumentException("Residues don't match");
        }
        long result = (this.value * factor.value) % this.residue;
        return new ResidueClassInteger(result, this.residue);
    }

    /**
     * Subtracts a residue integer from this one
     * @param subtrahend The subtrahend
     * @return The calculated result
     */
    public ResidueClassInteger subtract(ResidueClassInteger subtrahend) {
        if (subtrahend.residue != this.residue) {
            throw new IllegalArgumentException("Residues don't match");
        }
        long result = this.value - subtrahend.value;
        while (result < 0) {
            result = result + this.residue;
        }
        return new ResidueClassInteger(result, this.residue);
    }

    /**
     * "Divides" this integer by another integer
     * To achieve this, the multiplicative inverse of the given divisor is calculated
     * so that divisor² = 1 in this residue class
     * @param divisor The divisor to use
     * @return The calculated result
     */
    public ResidueClassInteger divide(ResidueClassInteger divisor) {
        if (divisor.residue != this.residue) {
            throw new IllegalArgumentException("Residues don't match");
        }
        long inverse = 1;
        while ((inverse * divisor.value) % this.residue != 1) {
            if (inverse >= this.residue) {
                throw new RuntimeException("Unknown error");
            }
            inverse++;
        }
        long result = this.value * divisor.value;
        return new ResidueClassInteger(result, this.residue);
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public int compareTo(ResidueClassInteger o) {
        if (o.residue != this.residue) {
            throw new IllegalArgumentException("Cannot compare Residue Integers of different residues");
        }
        long result = o.value - this.value;
        if (result >= Integer.MIN_VALUE && result <= Integer.MAX_VALUE) {
            return (int) result;
        } else if (result > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ResidueClassInteger cast) {
            return cast.value == this.value && cast.residue == this.residue;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public ResidueClassInteger clone() {
        return new ResidueClassInteger(this.value, this.residue);
    }

    public int hashCode() {
        return Long.hashCode(this.value);
    }
}
