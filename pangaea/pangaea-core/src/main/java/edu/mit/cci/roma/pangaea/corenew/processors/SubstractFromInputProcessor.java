package edu.mit.cci.roma.pangaea.corenew.processors;

import java.util.Map;

public class SubstractFromInputProcessor implements InputProcessor {
	private String inputName;
	private float valueToSubstract;
	
	public void init(String inputName, String configuration) {
		this.inputName = inputName;
		valueToSubstract = Float.parseFloat(configuration.trim());
		
	}

	public Map<String, String> processInputValues(Map<String, String> inputValues) {
		if (inputValues.containsKey(inputName)) {
			float currentVal = Float.parseFloat(inputValues.get(inputName));
			inputValues.put(inputName, String.valueOf(currentVal - valueToSubstract));
		}
		return inputValues;
	}


}
