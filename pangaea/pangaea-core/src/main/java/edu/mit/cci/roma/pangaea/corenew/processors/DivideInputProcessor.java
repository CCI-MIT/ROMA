package edu.mit.cci.roma.pangaea.corenew.processors;

import java.util.Map;

public class DivideInputProcessor implements InputProcessor {
	private String inputName;
	private float valueToDivideBy;
	
	public void init(String internalName, String externalName, String configuration) {
		this.inputName = externalName;
		valueToDivideBy = Float.parseFloat(configuration.trim());
		if (valueToDivideBy == 0) {
			throw new RuntimeException("Can't divide by 0");
		}
		
	}

	public Map<String, String> processInputValues(Map<String, String> inputValues) {
		if (inputValues.containsKey(inputName)) {
			float currentVal = Float.parseFloat(inputValues.get(inputName));
			inputValues.put(inputName, String.valueOf(currentVal / valueToDivideBy));
		}
		return inputValues;
	}


}
