package edu.mit.cci.roma.pangaea.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.cci.roma.pangaea.core.VensimHelper;

public class ModelInfo {
	private Map<String, VariableInfo> variables = new HashMap<String, VariableInfo>();

	public ModelInfo(VensimHelper vensimHelper) {

	    for (String varName: vensimHelper.getVariables()) {
	    	variables.put(varName, new VariableInfo(varName, vensimHelper));
	    }
	    
	    for (Map.Entry<String, VariableInfo> entry: variables.entrySet()) {
	    	entry.getValue().populateDependencies(variables);
	    }
	}
	
	public List<VariableInfo> getInputs() {
		List<VariableInfo> ret = new ArrayList<VariableInfo>();
		
		for (VariableInfo variable: variables.values()) {
			if (variable.getUses().isEmpty()) {
				ret.add(variable);
			}
		}
		
		return ret;
	}

}
