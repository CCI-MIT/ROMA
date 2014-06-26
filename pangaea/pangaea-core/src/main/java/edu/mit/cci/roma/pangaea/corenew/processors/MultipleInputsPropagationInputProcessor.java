package edu.mit.cci.roma.pangaea.corenew.processors;

import java.util.HashMap;
import java.util.Map;

public class MultipleInputsPropagationInputProcessor implements InputProcessor {
	private final static String SEPARATOR = ";";
	private String[] inputsToPopulate;
	private String inputName; 
	
	public void init(String inputName, String configuration) {
		inputsToPopulate = configuration.split(SEPARATOR);
		this.inputName = inputName;
	}

	@Override
	public Map<String, String> processInputValues(Map<String, String> inputValues) {
		if (inputValues.containsKey(inputName)) {
			// value for specified input exists, remove it and instead put the same value for inputsToPopulate
			String currentVal = inputValues.get(inputName);
			Map<String, String> ret = new HashMap<String, String>(inputValues);
			ret.remove(inputName);
			for (String inputToPopulate: inputsToPopulate) {
				ret.put(inputToPopulate, currentVal);
			}
			return ret;
		}
		return inputValues;
	}

}
