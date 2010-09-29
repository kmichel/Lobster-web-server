package info.kmichel.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * MatchMap is a special map which uses assignability
 * ({@code Class.isAssignableFrom}) to compare keys, to get consistent results
 * keys are tested in topological order (a key is tested before all of its
 * superclasses or superinterfaces), retrieval is in O(n).
 * Functions for full-content retrieval work on exact keys, IE. keySet does not
 * return the (infinite) set of all key which would return a value (since all
 * subclasses of each registered key would return the same value).
 * Performances are not as good as a standard map, retrieval is in O(n) (which
 * can be improved if we split n for counting separately classes and interfaces
 * then exploit the stored class hiearchy to prune our search).
 */
public class MatchMap<V> implements Map<Class<?>, V> {

	private final List<Class<?>> index;
	private final Map<Class<?>, V> data;

	public MatchMap() {
		this.index = new LinkedList<Class<?>>();
		this.data = new HashMap<Class<?>, V>();
	}

	public V put(final Class<?> key, final V value) {
		final V previous = get(key);
		
		if (index.isEmpty()) {
			index.add(key);
		} else {
			// keys are inserted in topo-orde
			int i=0;
			for (final Class<?> matcher: index) {
				if (key.equals(matcher)) {
					break;
				} else if (matcher.isAssignableFrom(key)) {
					index.add(i, key);
					break;
				}
				i++;
			}
		}
		data.put(key, value);
		return previous;
	}

	public V get(final Object key) {
		if (Class.class.isAssignableFrom(key.getClass())) {
			return get(Class.class.cast(key));
		} else {
			return null;
		}
	}

	/**
	 * Returns the value associated with the lowest stored key.
	 * If there's more than one lowest key, throws an IllegalArgumentException.
	 */
	public V get(final Class<?> key) {
		Class<?> match = null;
		for (final Class<?> matcher: index) {
			if (matcher.isAssignableFrom(key)) {
				if (match == null) {
					match = matcher;
				} else {
					throw new IllegalArgumentException("Key is subclass of more than one stored key");
				}
			}
		}
		if (match == null) {
			return null;
		} else {
			return data.get(match);
		}
	}

	public void putAll(final Map<? extends Class<?>,? extends V> map) {
		for (final Map.Entry<? extends Class<?>, ? extends V> entry: map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public V remove(final Object key) {
		final V value = data.remove(key);
		if (value != null) {
			index.remove(key);
		}
		return value;
	}
	
	public void clear() {
		index.clear();
		data.clear();
	}

	public boolean containsKey(final Object key) {
		return get(key) != null;
	}

	public boolean containsValue(final Object value) {
		return data.containsValue(value);
	}

	public int size() {
		return index.size();
	}

	public boolean isEmpty() {
		return index.isEmpty();
	}

	public Set<Map.Entry<Class<?>, V>> entrySet() {
		return data.entrySet();
	}

	public Set<Class<?>> keySet() {
		return data.keySet();
	}

	public Collection<V> values() {
		return data.values();
	}

}
