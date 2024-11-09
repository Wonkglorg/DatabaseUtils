package com.wonkglorg.interfaces.functional.throwing;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.BinaryOperator;

public interface ThrowingBinaryOperator<T, E extends Throwable> {
	T apply(T t, T u) throws E;

	/**
	 * Returns a {@link BinaryOperator} which returns the lesser of two elements
	 * according to the specified {@code Comparator}.
	 *
	 * @param <T> the type of the input arguments of the comparator
	 * @param comparator a {@code Comparator} for comparing the two values
	 * @return a {@code BinaryOperator} which returns the lesser of its operands,
	 * according to the supplied {@code Comparator}
	 * @throws NullPointerException if the argument is null
	 */
	static <T> ThrowingBinaryOperator<T, Throwable> minBy(Comparator<? super T> comparator) {
		Objects.requireNonNull(comparator);
		return (a, b) -> comparator.compare(a, b) <= 0 ? a : b;
	}

	/**
	 * Returns a {@link BinaryOperator} which returns the greater of two elements
	 * according to the specified {@code Comparator}.
	 *
	 * @param <T> the type of the input arguments of the comparator
	 * @param comparator a {@code Comparator} for comparing the two values
	 * @return a {@code BinaryOperator} which returns the greater of its operands,
	 * according to the supplied {@code Comparator}
	 * @throws NullPointerException if the argument is null
	 */
	static <T> ThrowingBinaryOperator<T, Throwable> maxBy(Comparator<? super T> comparator) {
		Objects.requireNonNull(comparator);
		return (a, b) -> comparator.compare(a, b) >= 0 ? a : b;
	}
}
