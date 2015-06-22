package edu.mit.cci.roma.pangaea.corenew.processors.inputs;

import java.util.Map;

import edu.mit.cci.roma.pangaea.corenew.inputs.InputValue;
import edu.mit.cci.roma.pangaea.corenew.inputs.StringInputValue;

public class SubstractFromInputProcessor implements InputProcessor {
	private String inputName;
	private float valueToSubstract;
	
	public void init(String internalName, String externalName, String configuration) {
		this.inputName = externalName;
		valueToSubstract = Float.parseFloat(configuration.trim());
		
	}

	public Map<String, InputValue> processInputValues(Map<String, InputValue> inputValues) {
		InputValue inputValue = inputValues.get(inputName);
		if (inputValue != null && inputValue instanceof StringInputValue) {
			
			float currentVal = Float.parseFloat(((StringInputValue) inputValue).getValue());
			inputValues.put(inputName, new StringInputValue(inputName, String.valueOf(currentVal - valueToSubstract)));
		}
		return inputValues;
	}


}