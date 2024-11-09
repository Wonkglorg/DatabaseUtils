package com.wonkglorg.interfaces.functional.throwing;

import java.util.function.Supplier;

@SuppressWarnings("unused")
@FunctionalInterface
public interface ThrowingProducer<T> extends Supplier<T> {
	T getChecked() throws Exception;

	@Override
	default T get() {
		try {
			return getChecked();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
