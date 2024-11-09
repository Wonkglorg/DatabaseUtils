package com.wonkglorg.interfaces.functional.throwing;

import com.wonkglorg.interfaces.functional.TrinaryOperator;

import java.util.Comparator;
import java.util.Objects;

public interface ThrowingTrinaryOperator<T, E extends Throwable> {

	T apply(T t, T u, T v) throws E;

	/**
	 * Returns a {@link TrinaryOperator} which returns the least of elements
	 * according to the specified {@code Comparator}.
	 *
	 * @param <T> the type of the input arguments of the comparator
	 * @param comparator a {@code Comparator} for comparing the two values
	 * @return a {@code BinaryOperator} which returns the lesser of its operands,
	 * according to the supplied {@code Comparator}
	 * @throws NullPointerException if the argument is null
	 */
	static <T> ThrowingTrinaryOperator<T, Throwable> minBy(Comparator<? super T> comparator) {
		Objects.requireNonNull(comparator);
		return (a, b, c) -> {
			T smaller = comparator.compare(a, b) <= 0 ? a : b;
			return comparator.compare(smaller, c) <= 0 ? smaller : c;
		};
	}

	/**
	 * Returns a {@link TrinaryOperator} which returns the greatest of elements
	 * according to the specified {@code Comparator}.
	 *
	 * @param <T> the type of the input arguments of the comparator
	 * @param comparator a {@code Comparator} for comparing the two values
	 * @return a {@code BinaryOperator} which returns the greater of its operands,
	 * according to the supplied {@code Comparator}
	 * @throws NullPointerException if the argument is null
	 */
	static <T> ThrowingTrinaryOperator<T, Throwable> maxBy(Comparator<? super T> comparator) {
		Objects.requireNonNull(comparator);
		return (a, b, c) -> {
			T bigger = comparator.compare(a, b) >= 0 ? a : b;
			return comparator.compare(bigger, c) >= 0 ? bigger : c;
		};
	}
}
