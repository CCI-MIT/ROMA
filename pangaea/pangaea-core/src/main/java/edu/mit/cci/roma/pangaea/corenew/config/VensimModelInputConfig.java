package edu.mit.cci.roma.pangaea.corenew.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class VensimModelInputConfig {
	
	@Attribute
	private String name;
	
	@Element(required=false)
	private String min;
	
	@Element(required=false)
	private String max;
	
	@Element(required=false)
	private VensimModelInputType type;

	public String getName() {
		return name;
	}
	

}
