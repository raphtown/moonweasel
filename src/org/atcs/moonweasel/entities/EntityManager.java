package org.atcs.moonweasel.entities;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.atcs.moonweasel.Manager;
import org.atcs.moonweasel.Moonweasel;
import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.ranges.SphericalRange;
import org.atcs.moonweasel.ranges.TypeRange;
import org.atcs.moonweasel.util.Vector;

public class EntityManager extends Manager<Entity> {
	private final static EntityManager ENTITY_MANAGER;

	static {
		ENTITY_MANAGER = new EntityManager();
	}

	public static EntityManager getEntityManager() {
		return ENTITY_MANAGER;
	}

	private final TreeMap<Long, Entity> thoughts;
	private long offset;

	private EntityManager() {
		this.thoughts = new TreeMap<Long, Entity>();
	}

	@Override
	public <E extends Entity> E create(String type) {
		E ent = super.create(type);
		return ent;
	}

	protected Class<? extends Entity> getClass(String type) {
		return Moonweasel.getEntityClassByName(type);
	}

	public SphericalRange<ModelEntity> getAllInSphere(Vector center, float radius) {
		return new SphericalRange<ModelEntity>(center, radius, getAllOfType(ModelEntity.class));
	}

	public TypeRange<Ship> getAllShipsInSphere(Vector center, float radius) {
		return new TypeRange<Ship>(Ship.class, getAllInSphere(center, radius));
	}

	public long getTime() {
		return System.currentTimeMillis() + offset;
	}

	public void registerThink(Entity entity, int ms) {
		 long time = System.currentTimeMillis() + ms;
		 int offset = 0;
		 while (this.thoughts.containsKey(time + offset))
			 	offset++;
		 time = time + offset;
		 this.thoughts.put(time, entity);
	}

	public void update(long t) {
		offset = t - System.currentTimeMillis();

		while (thoughts.size() > 0
				&& thoughts.firstKey() < System.currentTimeMillis()) {
			thoughts.remove(thoughts.firstKey()).think();
		}
		
		Iterator<Entry<Integer, Entity>> iter = elements.entrySet().iterator();
		Entry<Integer, Entity> entry;
		while (iter.hasNext()) {
			entry = iter.next();
			if (entry.getValue().isDestroyed()) {
				iter.remove();
			}
		}
	}

	public int getNextID() {
		return Entity.getNextIDWithoutChanging();
	}

	public void setNextID(int nextID)
	{
		Entity.setNextID(nextID);
	}
}