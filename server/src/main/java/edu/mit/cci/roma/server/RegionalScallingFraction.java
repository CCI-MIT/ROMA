package edu.mit.cci.roma.server;

import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * User: jintrone Date: 2/14/11 Time: 1:21 PM
 */
@Entity
@Configurable
public class RegionalScallingFraction implements Comparable<RegionalScallingFraction> {

	@EmbeddedId
	private RegionYearId id;

	private double fraction;

	@PersistenceContext
	transient EntityManager entityManager;

	public double getFraction() {
		return fraction;
	}

	public void setFraction(double fraction) {
		this.fraction = fraction;
	}

	public static final EntityManager entityManager() {
		EntityManager em = new RegionalScallingFraction().entityManager;
		if (em == null)
			throw new IllegalStateException(
					"Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
		return em;
	}

	public RegionYearId getId() {
		return id;
	}

	public void setId(RegionYearId id) {
		this.id = id;
	}

	public static List<RegionalScallingFraction> findByRegion(String region) {
		return entityManager()
				.createQuery("select o from RegionalScallingFraction o WHERE region = :region",
						RegionalScallingFraction.class).setParameter("region", region).getResultList();
	}

	@Override
	public int compareTo(RegionalScallingFraction o) {
		int compareResult = id.getRegion().compareTo(o.getId().getRegion());
		if (compareResult == 0) {
			compareResult = id.year - o.id.year;
		}
		return compareResult;
	}

}
