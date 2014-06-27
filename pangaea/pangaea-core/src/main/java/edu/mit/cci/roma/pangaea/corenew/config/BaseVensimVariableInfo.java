package edu.mit.cci.roma.pangaea.corenew.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class BaseVensimVariableInfo {
	
	@Attribute
	private String name;
	
	@Attribute(required=false)
	private String min;
	
	@Attribute(required=false)
	private String max;
	
	@Element(required=false)
	private String description;
	
	@Attribute(required=false)
	private String label;
	
	@Attribute(required=false)
	private String internalName;

	@Attribute(required=false)
	private String profile;

	@Attribute(required=false)
	private String varType;

	@Attribute(required=false)
	private String unit;

	@Attribute(required=false)
	private String defaultVal;
	
	@Attribute(required=false)
	private String vensimContextVariable; 
	
	@Attribute(required=false)
	private boolean internalOnly;

	@Attribute(required=false)
	private String categories;
	

	public String getName() {
		return name;
	}

	public String getMin() {
		return min;
	}

	public String getMax() {
		return max;
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}

	public String getInternalName() {
		return internalName;
	}

	public String getProfile() {
		return profile;
	}

	public String getVarType() {
		return varType;
	}

	public String getUnit() {
		return unit;
	}

	public String getDefaultVal() {
		return defaultVal;
	}

	public String getVensimContextVariable() {
		return vensimContextVariable;
	}
	
	public boolean isInternalOnly() {
		return internalOnly;
	}

	public String getCategories() {
		return categories;
	}

}
