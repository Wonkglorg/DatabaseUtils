package com.wonkglorg.interfaces.functional.checked;

import com.wonkglorg.interfaces.functional.TriConsumer;

@SuppressWarnings("unused")
@FunctionalInterface
public interface CheckedTriConsumer<T, U, V> extends TriConsumer<T, U, V> {
    void acceptChecked(T t, U u, V v) throws Exception;

    @Override
    default void accept(T t, U u, V v) {
        try {
            acceptChecked(t, u, v);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
