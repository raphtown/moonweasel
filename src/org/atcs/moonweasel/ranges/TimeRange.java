package org.atcs.moonweasel.ranges;

import java.util.Iterator;

import org.atcs.moonweasel.Timed;

public class TimeRange<E extends Timed> implements Range<E> {
	private Iterator<E> iterator;
	private long start;
	private long end;
	private E current;
	
	public TimeRange(long start, long end, Iterator<E> iterator) {
		this.iterator = iterator;
		this.start = start;
		this.end = end;
		this.current = findNextElement();
	}

	private E findNextElement() {
		E element;
		while (iterator.hasNext()) {
			element = iterator.next();
			if (start <= element.getTime() && element.getTime() < end) {
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
