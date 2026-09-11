package io.github.qishr.cascara.lang.ct.util;

public class NumericParameter {
    private Number number;
    private boolean isRelative;
    private boolean isPercentage;

    public NumericParameter(Number number, boolean isRelative, boolean isPercentage) {
        this.number = number;
        this.isRelative = isRelative;
        this.isPercentage = isPercentage;
    }

    public Number number() {
        return number;
    }

    public double asDouble() {
        return number.doubleValue();
    }

    public int asInteger() {
        return number.intValue();
    }

    public boolean isPercentage() {
        return isPercentage;
    }

    public boolean isRelative() {
        return isRelative;
    }

    public boolean isInteger() {
        return number instanceof Integer;
    }

    public boolean isDouble() {
        return number instanceof Double;
    }
}
