package org.atcs.moonweasel.ranges;

import java.util.Iterator;

import org.atcs.moonweasel.Positional;
import org.atcs.moonweasel.util.Vector;

public class SphericalRange<E extends Positional> implements Range<E> {
	private Iterator<E> iterator;
	private Vector center;
	private float radius;
	private E current;
	
	public SphericalRange(Vector center, float radius, Iterator<E> iterator) {
		this.iterator = iterator;
		this.center = center;
		this.radius = radius;
		this.current = findNextElement();
	}

	private E findNextElement() {
		E element;
		while (iterator.hasNext()) {
			element = iterator.next();
			if (center.subtract(element.getPosition()).length() < radius) {
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
