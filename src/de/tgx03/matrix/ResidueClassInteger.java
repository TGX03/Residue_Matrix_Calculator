package de.tgx03.matrix;

import java.io.Serializable;

public class ResidueClassInteger extends Number implements Comparable<ResidueClassInteger>, Serializable, Cloneable {

    public final long value;
    public final long residue;

    public ResidueClassInteger(long value, long residue) {
        if (value >= residue || value < 0) {
            throw new IllegalArgumentException("Value must be lower than residue and bigger than 0");
        }
        this.value = value;
        this.residue = residue;
    }

    public ResidueClassInteger add(ResidueClassInteger o) {
        if (o.residue != this.residue) {
            throw new IllegalArgumentException("Residues don't match");
        }
        long result = (this.value + o.value) % this.residue;
        return new ResidueClassInteger(result, this.residue);
    }

    public ResidueClassInteger multiply(ResidueClassInteger o) {
        if (o.residue != this.residue) {
            throw new IllegalArgumentException("Residues don't match");
        }
        long result = (this.value * o.value) % this.residue;
        return new ResidueClassInteger(result, this.residue);
    }

    public ResidueClassInteger subtract(ResidueClassInteger o) {
        if (o.residue != this.residue) {
            throw new IllegalArgumentException("Residues don't match");
        }
        long result = this.value - o.value;
        while (result < 0) {
            result = result + this.residue;
        }
        return new ResidueClassInteger(result, this.residue);
    }

    public ResidueClassInteger divide(ResidueClassInteger o) {
        if (o.residue != this.residue) {
            throw new IllegalArgumentException("Residues don't match");
        }
        long inverse = 1;
        while ((inverse * o.value) % this.residue != 1) {
            if (inverse >= this.residue) {
                throw new RuntimeException("Unknown error");
            }
            inverse++;
        }
        long result = this.value * o.value;
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
