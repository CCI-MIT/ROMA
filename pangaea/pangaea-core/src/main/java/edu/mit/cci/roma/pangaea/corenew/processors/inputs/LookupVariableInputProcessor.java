package edu.mit.cci.roma.pangaea.corenew.processors.inputs;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.cci.roma.pangaea.corenew.inputs.InputValue;
import edu.mit.cci.roma.pangaea.corenew.inputs.LookupInputValue;
import edu.mit.cci.roma.pangaea.corenew.inputs.StringInputValue;

public class LookupVariableInputProcessor implements InputProcessor {
	private String inputName;
	private String externalName;

	@Override
	public void init(String inputName, String externalName, String configuration) {
		this.inputName = inputName;
		this.externalName = externalName;

	}

	@Override
	public Map<String, InputValue> processInputValues(Map<String, InputValue> inputValues) {
		LookupInputValue inputValue = new LookupInputValue(inputName);
		boolean lookupFound = false;

		Map<String, InputValue> ret = new HashMap<String, InputValue>();
		for (Map.Entry<String, InputValue> entry: inputValues.entrySet()) {
			boolean addToRet = true;
			
			if (entry.getValue() instanceof StringInputValue) {
				final Pattern patternToMatchAgainst = Pattern.compile(externalName + "\\[(\\d+)\\]");
				Matcher m = patternToMatchAgainst.matcher(entry.getKey());
				if (m.find()) {
					Integer idx = Integer.parseInt(m.group(1));
					String value = ((StringInputValue) entry.getValue()).getValue();
					inputValue.addValue(idx, value);
					addToRet = false;
					lookupFound = true;
				}
			}
			if (addToRet) {
				ret.put(entry.getKey(), entry.getValue());
			}
		}
		if (lookupFound) {
			ret.put(externalName, inputValue);
			return ret;
		}
		
		return inputValues;
	}

}
