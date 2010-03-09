package org.atcs.moonweasel;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.atcs.moonweasel.ranges.Range;
import org.atcs.moonweasel.ranges.TypeRange;

public abstract class Manager<T extends Identifiable> implements Iterable<T> {
	private Map<Integer, T> elements;
	
	protected Manager() {
		this.elements = new TreeMap<Integer, T>();
	}
	
	public void add(T element) {
		this.elements.put(element.getID(), element);
	}
	
	@SuppressWarnings("unchecked")
	public <E extends T> E create(String type) {
		Class<E> clazz = (Class<E>)getClass(type);
		E element;
		
		try {
			Constructor<E> constructor = clazz.getDeclaredConstructor(new Class<?>[0]);
			constructor.setAccessible(true);
			element = constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Unable to create entity of type " + type, e);
		}
		
		elements.put(element.getID(), element);
		return element;
	}
	
	@SuppressWarnings("unchecked")
	public <E extends T> E get(int id) {
		return (E)elements.get(id);
	}
	
	public <E extends T> Range<E> getAllOfType(Class<E> clazz) {
		return new TypeRange<E>(clazz, elements.values().iterator());
	}
	
	protected abstract Class<? extends T> getClass(String type);
	
	public Iterator<T> iterator() {
		return Collections.unmodifiableCollection(elements.values()).iterator();
	}
	
	public abstract void update(long t);
}