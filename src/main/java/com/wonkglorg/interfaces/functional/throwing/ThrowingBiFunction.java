package com.wonkglorg.interfaces.functional.throwing;

import java.util.Objects;

/**
 * Represents a function that accepts two arguments and produces a result.
 * This is the two-arity specialization of {@link ThrowingFunction}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object)}.
 *
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <R> the type of the result of the function
 * @see ThrowingFunction
 * @since 1.8
 */
@SuppressWarnings("unused")
@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, E extends Throwable> {
	R apply(T t, U u) throws E;

	default <V> ThrowingBiFunction<T, U, V, E> andThen(
			ThrowingFunction<? super R, ? extends V, E> after) {
		Objects.requireNonNull(after);
		return (T t, U u) -> after.apply(apply(t, u));
	}
}
