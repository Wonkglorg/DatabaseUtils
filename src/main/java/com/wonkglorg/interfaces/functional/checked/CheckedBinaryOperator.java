package com.wonkglorg.interfaces.functional.checked;

import java.util.function.BinaryOperator;

public interface CheckedBinaryOperator<T> extends BinaryOperator<T> {
	T applyChecked(T t, T u) throws Exception;

	@Override
	default T apply(T t, T u) {
		try {
			return applyChecked(t, u);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
