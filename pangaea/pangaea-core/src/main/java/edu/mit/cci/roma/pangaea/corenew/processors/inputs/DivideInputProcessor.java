package edu.mit.cci.roma.pangaea.corenew.processors.inputs;

import java.util.Map;

import edu.mit.cci.roma.pangaea.corenew.inputs.InputValue;
import edu.mit.cci.roma.pangaea.corenew.inputs.StringInputValue;

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

	public Map<String, InputValue> processInputValues(Map<String, InputValue> inputValues) {
		if (inputValues.containsKey(inputName)) {
			InputValue inputValue = inputValues.get(inputName);
			if (inputValue instanceof StringInputValue) {
				float currentVal = Float.parseFloat((((StringInputValue) inputValue).getValue()));
				inputValues.put(inputName, new StringInputValue(inputName, String.valueOf(currentVal / valueToDivideBy)));
			}
		}
		return inputValues;
	}


}
