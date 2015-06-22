package edu.mit.cci.roma.pangaea.corenew.processors.inputs;

import java.util.Map;

import edu.mit.cci.roma.pangaea.corenew.inputs.InputValue;

public interface InputProcessor {
	public void init(String inputName, String externalName, String configuration);
	public Map<String, InputValue> processInputValues(Map<String, InputValue> inputValues);

}
