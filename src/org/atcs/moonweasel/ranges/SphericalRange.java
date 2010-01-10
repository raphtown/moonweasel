package org.atcs.moonweasel.ranges;

import java.util.Iterator;

import org.atcs.moonweasel.Positional;
import org.atcs.moonweasel.util.Vector;

public class SphericalRange<E> extends TypeRange<E> {
	private Vector center;
	private float radius;
	private E current;
	
	public SphericalRange(Vector center, float radius, Iterator<E> iterator) {
		super(Positional.class, iterator);
		
		this.center = center;
		this.radius = radius;
		this.current = findNextElement();
	}

	private E findNextElement() {
		E element;
		while (super.hasNext()) {
			element = super.next();
			if (center.subtract(((Positional)element).getPosition()).length() < radius) {
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
