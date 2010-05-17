package org.atcs.moonweasel.ranges;

import java.util.Iterator;

public abstract class CustomRange<E> implements Range<E> {
	private Iterator<? super E> iterator;
	private E current;
	
	public CustomRange(Iterator<? super E> iterator) {
		this.iterator = iterator;
		this.current = findNextElement();
	}

	@SuppressWarnings("unchecked")
	private E findNextElement() {
		E element;
		while (iterator.hasNext()) {
			element = (E)iterator.next();
			if (filter(element)) {
				return element;
			}
		}
		
		return null;
	}
	
	protected abstract boolean filter(E element);
	
	@Override
	public boolean hasNext() {
		return current != null;
	}
	
	@Override
	public Range<E> iterator() {
		return this;
	}
	
	@Override
	public E next() {
		E next = current;
		current = findNextElement();
		return next;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Ranges do not implement remove.");
	}
}
