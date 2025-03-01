package com.wonkglorg.database.objs;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public record Tuple<K, V>(K first, V second) implements Serializable, Cloneable{
	
	public static <K, V> Tuple<K, V> of(K first, V second) {
		return new Tuple<>(first, second);
	}
	
	/**
	 * Swaps the first and second value
	 */
	public Tuple<V, K> swap() {
		return new Tuple<>(second, first);
	}
	
	/**
	 * Sets the new key for this tuple
	 *
	 * @param val2 the second value to set
	 */
	public Tuple<K, V> setSecond(V second) {
		return new Tuple<>(first, second);
	}
	
	/**
	 * Sets the new key for this tuple
	 *
	 * @param first the first value to set
	 */
	public Tuple<K, V> setFirst(K first) {
		return new Tuple<>(first, second);
	}
	
	@Override
	public String toString() {
		return "Tuple{" + "first=" + first + ", second=" + second + '}';
	}
	
	@Override
	public boolean equals(Object object) {
		if(this == object){
			return true;
		}
		if(!(object instanceof Tuple<?, ?> tuple)){
			return false;
		}
		return Objects.equals(first, tuple.first) && Objects.equals(second, tuple.second);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}
	
	/**
	 * Creates a copy of this object (in case values are mutable use
	 * {@link #deepCopy(Function, Function)} instead
	 *
	 * @throws CloneNotSupportedException
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Tuple<>(first, second);
	}
	
	/**
	 * Deep copies a tuple (in case values are mutable like lists
	 *
	 * @param keyCopyFunction key function
	 * @param valueCopyFunction value function
	 * @return the tuple copy
	 */
	public Tuple<K, V> deepCopy(Function<K, K> keyCopyFunction, Function<V, V> valueCopyFunction) {
		return new Tuple<>(keyCopyFunction.apply(first), valueCopyFunction.apply(second));
	}
	
	/**
	 * Converts a tuple to a map Entry
	 */
	public Map.Entry<K, V> toEntry() {
		return new AbstractMap.SimpleEntry<>(first, second);
	}
	
	/**
	 * @return if one or more values are null
	 */
	public boolean hasNulls() {
		return first == null || second == null;
	}
	
	/**
	 * @return if both values are null
	 */
	public boolean isEmpty() {
		return first == null && second == null;
	}
	
}
