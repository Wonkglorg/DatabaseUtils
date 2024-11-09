package com.wonkglorg.interfaces.functional.checked;

import java.util.function.BiFunction;

/**
 * Represents a function that accepts two arguments and produces a result.
 * This is the two-arity specialization of {@link CheckedFunction}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object)}.
 *
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <R> the type of the result of the function
 * @see CheckedFunction
 * @since 1.8
 */
@SuppressWarnings("unused")
@FunctionalInterface
public interface CheckedBiFunction<T, U, R> extends BiFunction<T, U, R> {
    R applyChecked(T t, U u) throws Exception;

    @Override
    default R apply(T t, U u) {
        try {
            return applyChecked(t, u);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
