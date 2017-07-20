package ch.obermuhlner.math.big.stream;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import ch.obermuhlner.math.big.BigDecimalMath;

/**
 * Provides constructor methods for streams of {@link BigDecimal} elements. 
 */
public class BigDecimalStream {

    /**
     * Returns a sequential ordered {@code Stream<BigDecimal>} from {@code startInclusive}
     * (inclusive) to {@code endExclusive} (exclusive) by an incremental step of {@link BigDecimal#ONE}.
     *
     * @apiNote
     * <p>An equivalent sequence of increasing values can be produced
     * sequentially using a {@code for} loop as follows:
     * <pre>{@code
     *     for (BigDecimal i = startInclusive; i.compareTo(endExclusive) &lt; 0; i = i.add(BigDecimal.ONE, mathContext)) { ... }
     * }</pre>
     *
     * @param startInclusive the (inclusive) initial value
     * @param endExclusive the exclusive upper bound
     * @param mathContext the {@link MathContext} used for all mathematical operations
     * @return a sequential {@code Stream<BigDecimal>}
     */
    public static Stream<BigDecimal> range(BigDecimal startInclusive, BigDecimal endExclusive, MathContext mathContext) {
    	BigDecimal step = BigDecimal.ONE;
    	if (endExclusive.subtract(startInclusive).signum() < 0) {
    		step = step.negate();
    	}
    	return range(startInclusive, endExclusive, step, mathContext);
    }

    /**
     * Returns a sequential ordered {@code Stream<BigDecimal>} from {@code startInclusive}
     * (inclusive) to {@code endInclusive} (inclusive) by an incremental step of {@link BigDecimal#ONE}.
     *
     * @apiNote
     * <p>An equivalent sequence of increasing values can be produced
     * sequentially using a {@code for} loop as follows:
     * <pre>{@code
     *     for (BigDecimal i = startInclusive; i.compareTo(endExclusive) &lt;= 0; i = i.add(BigDecimal.ONE, mathContext)) { ... }
     * }</pre>
     *
     * @param startInclusive the (inclusive) initial value
     * @param endInclusive the inclusive upper bound
     * @param mathContext the {@link MathContext} used for all mathematical operations
     * @return a sequential {@code Stream<BigDecimal>}
     */
    public static Stream<BigDecimal> rangeClosed(BigDecimal startInclusive, BigDecimal endInclusive, MathContext mathContext) {
    	BigDecimal step = BigDecimal.ONE;
    	if (endInclusive.subtract(startInclusive).signum() < 0) {
    		step = step.negate();
    	}
    	return rangeClosed(startInclusive, endInclusive, step, mathContext);
    }

    /**
     * Returns a sequential ordered {@code Stream<BigDecimal>} from {@code startInclusive}
     * (inclusive) to {@code endExclusive} (exclusive) by an incremental {@code step}.
     *
     * @apiNote
     * <p>An equivalent sequence of increasing values can be produced
     * sequentially using a {@code for} loop as follows:
     * <pre>{@code
     *     for (BigDecimal i = startInclusive; i.compareTo(endExclusive) &lt; 0; i = i.add(step, mathContext)) { ... }
     * }</pre>
     *
     * @param startInclusive the (inclusive) initial value
     * @param endExclusive the exclusive upper bound
     * @param step the step between elements
     * @param mathContext the {@link MathContext} used for all mathematical operations
     * @return a sequential {@code Stream<BigDecimal>}
     */
    public static Stream<BigDecimal> range(BigDecimal startInclusive, BigDecimal endExclusive, BigDecimal step, MathContext mathContext) {
    	if (step.signum() == 0) {
    		throw new IllegalArgumentException("invalid step: 0");
    	}
		if (endExclusive.subtract(startInclusive).signum() != step.signum()) {
			return Stream.empty();
		}
    	return StreamSupport.stream(new BigDecimalSpliterator(startInclusive, endExclusive, false, step, mathContext), false);
    }
    
    /**
     * Returns a sequential ordered {@code Stream<BigDecimal>} from {@code startInclusive}
     * (inclusive) to {@code endInclusive} (inclusive) by an incremental {@code step}.
     *
     * @apiNote
     * <p>An equivalent sequence of increasing values can be produced
     * sequentially using a {@code for} loop as follows:
     * <pre>{@code
     *     for (BigDecimal i = startInclusive; i.compareTo(endInclusive) &lt;= 0; i = i.add(step, mathContext)) { ... }
     * }</pre>
     *
     * @param startInclusive the (inclusive) initial value
     * @param endInclusive the inclusive upper bound
     * @param step the step between elements
     * @param mathContext the {@link MathContext} used for all mathematical operations
     * @return a sequential {@code Stream<BigDecimal>}
     */
    public static Stream<BigDecimal> rangeClosed(BigDecimal startInclusive, BigDecimal endInclusive, BigDecimal step, MathContext mathContext) {
    	if (step.signum() == 0) {
    		throw new IllegalArgumentException("invalid step: 0");
    	}
		if (endInclusive.subtract(startInclusive).signum() == -step.signum()) {
			return Stream.empty();
		}
    	return StreamSupport.stream(new BigDecimalSpliterator(startInclusive, endInclusive, true, step, mathContext), false);
    }
    
    private static class BigDecimalSpliterator extends AbstractSpliterator<BigDecimal> {

		private BigDecimal value;
		private BigDecimal step;
		private long count;
		private MathContext mathContext;

		public BigDecimalSpliterator(BigDecimal startInclusive, BigDecimal step, long count, MathContext mathContext) {
    		super(count,
    				Spliterator.SIZED | Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.ORDERED);
			
    		this.value = startInclusive;
			this.step = step;
			this.count = count;
			this.mathContext = mathContext;
		}
    	
		public BigDecimalSpliterator(BigDecimal startInclusive, BigDecimal end, boolean inclusive, BigDecimal step, MathContext mathContext) {
			this(startInclusive, step, estimatedCount(startInclusive, end, inclusive, step, mathContext), mathContext);
		}
		
		private static long estimatedCount(BigDecimal startInclusive, BigDecimal end, boolean inclusive, BigDecimal step, MathContext mathContext) {
			BigDecimal count = end.subtract(startInclusive).divide(step, mathContext);
    		long result = count.longValue();
    		if (BigDecimalMath.fractionalPart(count).signum() != 0) {
    			result++;
    		} else {
    			if (inclusive) {
    				result++;
    			}
    		}
    		return result;
		}

		@Override
		public boolean tryAdvance(Consumer<? super BigDecimal> action) {
			if (count == 0) {
				return false;
			}
			
			action.accept(value);
			value = value.add(step, mathContext);
			count--;
			return true;
		}
		
		@Override
		public Spliterator<BigDecimal> trySplit() {
			long firstHalfCount = count / 2;
			
			if (firstHalfCount == 0) {
				return null;
			}
			
			long secondHalfCount = count - firstHalfCount;
			
			count = firstHalfCount;
			BigDecimal startSecondHalf = value.add(step.multiply(new BigDecimal(firstHalfCount), mathContext));
			
			return new BigDecimalSpliterator(startSecondHalf, step, secondHalfCount, mathContext);
		}
    }
}