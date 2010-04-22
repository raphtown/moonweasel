package org.atcs.moonweasel.entities;

import java.util.TreeMap;

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
		ent.spawn();
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
		while (thoughts.containsKey(System.currentTimeMillis() + ms)) { 
			ms += 1;
		}
		this.thoughts.put(System.currentTimeMillis() + ms, entity);
	}

	public void update(long t) {
		offset = t - System.currentTimeMillis();
		
		while (thoughts.size() > 0
				&& thoughts.firstKey() < System.currentTimeMillis()) {
			thoughts.remove(thoughts.firstKey()).think();
		}
	}
}