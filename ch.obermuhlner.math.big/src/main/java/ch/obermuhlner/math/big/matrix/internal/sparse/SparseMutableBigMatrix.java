package ch.obermuhlner.math.big.matrix.internal.sparse;

import ch.obermuhlner.math.big.matrix.BigMatrix;
import ch.obermuhlner.math.big.matrix.MutableBigMatrix;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.BiFunction;

public class SparseMutableBigMatrix extends AbstractSparseBigMatrix implements MutableBigMatrix {
    public SparseMutableBigMatrix(int rows, int columns, BigDecimal... values) {
        super(rows, columns, values);
    }

    public SparseMutableBigMatrix(int rows, int columns, BiFunction<Integer, Integer, BigDecimal> valueFunction) {
        super(rows, columns, valueFunction);
    }

    @Override
    protected SparseMutableBigMatrix createBigMatrix(int rows, int columns) {
        return new SparseMutableBigMatrix(rows, columns);
    }

    @Override
    public void set(int row, int column, BigDecimal value) {
        internalSet(row, column, value);
    }

    @Override
    public void fill(BigDecimal value) {
        data.clear();
        defaultValue = value;
    }

    @Override
    public MutableBigMatrix add(BigMatrix other, MathContext mathContext) {
        return (MutableBigMatrix) super.add(other, mathContext);
    }

    @Override
    public MutableBigMatrix subtract(BigMatrix other, MathContext mathContext) {
        return (MutableBigMatrix) super.subtract(other, mathContext);
    }

    @Override
    public MutableBigMatrix multiply(BigDecimal value, MathContext mathContext) {
        return (MutableBigMatrix) super.multiply(value, mathContext);
    }

    @Override
    public MutableBigMatrix multiply(BigMatrix other, MathContext mathContext) {
        return (MutableBigMatrix) super.multiply(other, mathContext);
    }
}