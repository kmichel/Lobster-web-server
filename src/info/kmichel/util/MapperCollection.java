package info.kmichel.util;

import java.util.Collection;
import java.util.Iterator;

public abstract class MapperCollection<K, V> implements Collection<K> {

	private final Collection<V> destination;

	public MapperCollection(final Collection<V> destination) {
		this.destination = destination;
	}

	protected abstract V map(final K key);

	public boolean add(final K key) {
		return destination.add(map(key));
	}

	public boolean addAll(Collection<? extends K> keys) {
		boolean changed = false;
		for (final K key : keys) {
			changed |= add(key);
		}
		return changed;
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean contains(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	public Iterator<K> iterator() {
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return destination.size();
	}

	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	public <K> K[] toArray(K[] a) {
		throw new UnsupportedOperationException();
	}

}
