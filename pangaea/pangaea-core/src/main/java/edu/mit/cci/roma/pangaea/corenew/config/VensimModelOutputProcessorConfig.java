package edu.mit.cci.roma.pangaea.corenew.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class VensimModelOutputProcessorConfig {

	@Attribute
	private String name;
	
	@Element(required=true)
	private String configuration;
	
	public String getName() {
		return name;
	}

	public String getConfiguration() {
		return configuration;
	}
}
