package edu.mit.cci.roma.pangaea.corenew.processors.inputs;

import java.util.HashMap;
import java.util.Map;

import edu.mit.cci.roma.pangaea.corenew.inputs.InputValue;
import edu.mit.cci.roma.pangaea.corenew.inputs.StringInputValue;

public class MultipleInputsPropagationInputProcessor implements InputProcessor {
	private final static String SEPARATOR = ";";
	private String[] inputsToPopulate;
	private String inputName; 
	
	public void init(String internalName, String externalName, String configuration) {
		inputsToPopulate = configuration.split(SEPARATOR);
		this.inputName = externalName;
	}

	@Override
	public Map<String, InputValue> processInputValues(Map<String, InputValue> inputValues) {
		if (inputValues.containsKey(inputName)) {
			// value for specified input exists, remove it and instead put the same value for inputsToPopulate
			InputValue inputValue = inputValues.get(inputName);
			if (inputValue instanceof StringInputValue) {
				String val = ((StringInputValue) inputValue).getValue();
				Map<String, InputValue> ret = new HashMap<String, InputValue>(inputValues);
				ret.remove(inputName);
				for (String inputToPopulate: inputsToPopulate) {
					ret.put(inputToPopulate, new StringInputValue(inputToPopulate, val));
				}
				return ret;
			}
		}
		return inputValues;
	}

}
