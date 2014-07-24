package edu.mit.cci.roma.pangaea.corenew.processors;

import java.util.Map;

public interface InputProcessor {
	public void init(String inputName, String externalName, String configuration);
	public Map<String, String> processInputValues(Map<String, String> inputValues);

}
