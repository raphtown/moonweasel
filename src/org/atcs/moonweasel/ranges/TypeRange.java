package org.atcs.moonweasel.ranges;

import java.util.Iterator;

public class TypeRange<E> extends Range<E> {
	private Class<?> clazz;
	private E current;
	
	public TypeRange(Class<?> clazz, Iterator<E> iterator) {
		super(iterator);
		
		this.clazz = clazz;
		this.current = findNextElement();
	}

	private E findNextElement() {
		E element;
		while (super.hasNext()) {
			element = super.next();
			if (clazz.isAssignableFrom(element.getClass())) {
				return element;
			}
		}
		
		return null;
	}
	
	@Override
	public boolean hasNext() {
		return current != null;
	}
	
	@Override
	public E next() {
		E next = current;
		current = findNextElement();
		return next;
	}
}
