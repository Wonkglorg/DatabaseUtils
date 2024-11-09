package com.wonkglorg.interfaces.functional.checked;

import java.util.function.Function;

/**
 * Represents a function that accepts one argument and produces a result.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object)}.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @since 1.8
 */
@SuppressWarnings("unused")
@FunctionalInterface
public interface CheckedFunction<T, R> extends Function<T, R> {
    R applyChecked(T t) throws Exception;

    @Override
    default R apply(T t) {
        try {
            return applyChecked(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
