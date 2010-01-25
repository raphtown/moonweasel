package org.atcs.moonweasel.ranges;

import java.util.Iterator;

public class TypeRange<E> implements Range<E> {
	private Iterator<? super E> iterator;
	private Class<?> clazz;
	private E current;
	
	public TypeRange(Class<E> clazz, Iterator<? super E> iterator) {
		this.iterator = iterator;
		this.clazz = clazz;
		this.current = findNextElement();
	}

	@SuppressWarnings("unchecked")
	private E findNextElement() {
		Object element;
		while (iterator.hasNext()) {
			element = iterator.next();
			if (clazz.isAssignableFrom(element.getClass())) {
				return (E)element;
			}
		}
		
		return null;
	}
	
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
