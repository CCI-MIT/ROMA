package edu.mit.cci.roma.server;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Configurable;

@Embeddable
public class RegionYearId implements Serializable {
	
	String region;
	int year;

	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
	@Override
	public int hashCode() {
		return region.hashCode() * year;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof RegionYearId)) {
			return false;
		}
		RegionYearId compared = (RegionYearId) obj;
		
		return compared.year == year && compared.region.equals(region);	
	}
	
}