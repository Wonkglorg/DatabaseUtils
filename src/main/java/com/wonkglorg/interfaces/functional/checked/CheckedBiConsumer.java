package com.wonkglorg.interfaces.functional.checked;

import java.util.function.BiConsumer;

/**
 * Represents an operation that accepts two input arguments and returns no
 * result.  This is the two-arity specialization of {@link CheckedConsumer}.
 * Unlike most other functional interfaces, {@code BiConsumer} is expected
 * to operate via side-effects.
 * </p>
 * whose functional method is {@link #accept(Object, Object)}.
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @see CheckedConsumer
 * @since 1.8
 */
@SuppressWarnings("unused")
@FunctionalInterface
public interface CheckedBiConsumer<T, U> extends BiConsumer<T, U> {
    void acceptChecked(T t, U u) throws Exception;

    @Override
    default void accept(T t, U u) {
        try {
            acceptChecked(t, u);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
