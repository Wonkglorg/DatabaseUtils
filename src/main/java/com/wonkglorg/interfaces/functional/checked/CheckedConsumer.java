package com.wonkglorg.interfaces.functional.checked;

import java.util.function.Consumer;

/**
 * Represents an operation that accepts a single input argument and returns no
 * result. Unlike most other functional interfaces, {@code CheckedConsumer} is expected
 * to operate via side-effects.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(Object)}.
 *
 * @param <T> the type of the input to the operation
 * @since 1.8
 */
@SuppressWarnings("unused")
@FunctionalInterface
public interface CheckedConsumer<T> extends Consumer<T> {
    void acceptChecked(T t) throws Exception;

    @Override
    default void accept(T t) {
        try {
            acceptChecked(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
