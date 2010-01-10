package org.atcs.moonweasel.entities;

import java.util.TreeMap;

import org.atcs.moonweasel.Manager;
import org.atcs.moonweasel.Moonweasel;

public class EntityManager extends Manager<Entity> {
	private final static EntityManager ENTITY_MANAGER;

	static {
		ENTITY_MANAGER = new EntityManager();
	}

	public static EntityManager getEntityManager() {
		return ENTITY_MANAGER;
	}

	private final TreeMap<Long, Entity> thoughts;

	public EntityManager() {
		this.thoughts = new TreeMap<Long, Entity>();
	}

	protected Class<? extends Entity> getClass(String type) {
		return Moonweasel.ENTITY_MAP.get(type);
	}

	public void registerThink(Entity entity, int ms) {
		this.thoughts.put(System.currentTimeMillis() + ms, entity);
	}

	public void update() {
		while (thoughts.size() > 0
				&& thoughts.firstKey() < System.currentTimeMillis()) {
			thoughts.remove(thoughts.firstKey()).think();
		}
	}
}