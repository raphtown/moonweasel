package org.atcs.moonweasel.ranges;

import java.util.Iterator;

public class Range<E> implements Iterator<E> {
	private Iterator<E> iterator;
	
	public Range(Iterator<E> iterator) {
		this.iterator = iterator;
	}
	
	public E next() {
		return iterator.next();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public final void remove() {
		throw new UnsupportedOperationException("Ranges do not implement remove.");
	}
}
