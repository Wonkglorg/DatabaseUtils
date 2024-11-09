package com.wonkglorg.interfaces.functional.throwing;

import java.util.Objects;

@SuppressWarnings("unused")
@FunctionalInterface
public interface ThrowingTriConsumer<T, U, V, E extends Throwable> {
	void accept(T t, U u, V v) throws E;


	default ThrowingTriConsumer<T, U, V, E> andThen(
			ThrowingTriConsumer<? super T, ? super U, ? super V, E> after) {
		Objects.requireNonNull(after);
		return (l, r, v) -> {
			accept(l, r, v);
			after.accept(l, r, v);
		};
	}
}
