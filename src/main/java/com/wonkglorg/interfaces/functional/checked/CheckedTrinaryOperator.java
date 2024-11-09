package com.wonkglorg.interfaces.functional.checked;

import com.wonkglorg.interfaces.functional.TrinaryOperator;

public interface CheckedTrinaryOperator<T> extends TrinaryOperator<T> {
	T applyChecked(T t, T u, T v) throws Exception;

	@Override
	default T apply(T t, T u, T v) {
		try {
			return applyChecked(t, u, v);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
