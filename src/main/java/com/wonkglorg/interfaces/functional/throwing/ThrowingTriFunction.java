package com.wonkglorg.interfaces.functional.throwing;

import java.util.Objects;

@SuppressWarnings("unused")
@FunctionalInterface
public interface ThrowingTriFunction<T, U, V, R, E extends Throwable> {
	R apply(T t, U u, V v) throws E;

	default <W> ThrowingTriFunction<T, U, V, W, E> andThen(
			ThrowingBiFunction<? super R, ? super V, ? extends W, E> after) {
		Objects.requireNonNull(after);
		return (t, u, v) -> after.apply(apply(t, u, v), v);
	}
}
