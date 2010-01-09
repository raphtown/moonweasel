package org.atcs.moonweasel;

import java.util.Map;
import java.util.TreeMap;

public abstract class Manager<T extends Identifiable> {
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
			element = clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		elements.put(element.getID(), element);
		return element;
	}
	
	protected abstract Class<? extends T> getClass(String type);
	
	public abstract void update();
}