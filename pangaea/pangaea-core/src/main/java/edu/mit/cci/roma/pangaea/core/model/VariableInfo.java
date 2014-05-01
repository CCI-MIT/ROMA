package edu.mit.cci.roma.pangaea.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vensim.Vensim;

import edu.mit.cci.roma.pangaea.core.VensimHelper;

public class VariableInfo {
	
	private String name;
	private Map<Integer, String[]> attributes;
	private List<VariableInfo> causes = new ArrayList<VariableInfo>();
	private List<VariableInfo> uses = new ArrayList<VariableInfo>();
	private List<VariableInfo> initcauses = new ArrayList<VariableInfo>();
	private List<VariableInfo> activeCauses = new ArrayList<VariableInfo>();
	
	public VariableInfo(String name, VensimHelper vensimHelper) {
		this.name = name;
		attributes = vensimHelper.getVariableAttributes(name);
	}
	
	public void populateDependencies(Map<String, VariableInfo> variables) { 
		processReferences(Vensim.ATTRIB_ACTIVECAUSES, activeCauses, variables);
		processReferences(Vensim.ATTRIB_CAUSES, causes, variables);
		processReferences(Vensim.ATTRIB_USES, uses, variables);
		processReferences(Vensim.ATTRIB_INITCAUSES, initcauses, variables);
	}
	
	private void processReferences(int attributeId, List<VariableInfo> targetList, Map<String, VariableInfo> variables) {
		for (String refVarName: attributes.get(attributeId)) {
			if (refVarName == name) {
				throw new RuntimeException("Varable references itself!");
			}
			VariableInfo refVariable = variables.get(refVarName);
			if (refVarName == null) {
				throw new RuntimeException("Can't find referenced variable");
			}
			
			targetList.add(refVariable);
		}
	}
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<Integer, String[]> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<Integer, String[]> attributes) {
		this.attributes = attributes;
	}
	public List<VariableInfo> getCauses() {
		return causes;
	}
	public void setCauses(List<VariableInfo> causes) {
		this.causes = causes;
	}
	public List<VariableInfo> getUses() {
		return uses;
	}
	public void setUses(List<VariableInfo> uses) {
		this.uses = uses;
	}
	public List<VariableInfo> getInitcauses() {
		return initcauses;
	}
	public void setInitcauses(List<VariableInfo> initcauses) {
		this.initcauses = initcauses;
	}
	public List<VariableInfo> getActiveCauses() {
		return activeCauses;
	}
	public void setActiveCauses(List<VariableInfo> activeCauses) {
		this.activeCauses = activeCauses;
	}

	@Override
	public String toString() {
		return "VariableInfo [name=" + name + "]";
	}
	
	
	
	

}
