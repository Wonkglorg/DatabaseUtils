package com.wonkglorg.interfaces.functional.checked;

import java.util.function.Supplier;

@SuppressWarnings("unused")
@FunctionalInterface
public interface CheckedProducer<T> extends Supplier<T> {
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
