package com.wonkglorg.interfaces.functional.throwing;

import java.util.Objects;

/**
 * Represents an operation that accepts two input arguments and returns no
 * result.  This is the two-arity specialization of {@link ThrowingConsumer}.
 * Unlike most other functional interfaces, {@code BiConsumer} is expected
 * to operate via side-effects.
 * </p>
 * whose functional method is {@link #accept(Object, Object)}.
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @see ThrowingConsumer
 * @since 1.8
 */
@SuppressWarnings("unused")
@FunctionalInterface
public interface ThrowingBiConsumer<T, U, E extends Throwable> {
	void accept(T t, U u) throws E;

	default ThrowingBiConsumer<T, U, E> andThen(ThrowingBiConsumer<? super T, ? super U, E> after) {
		Objects.requireNonNull(after);
		return (l, r) -> {
			accept(l, r);
			after.accept(l, r);
		};
	}
}
