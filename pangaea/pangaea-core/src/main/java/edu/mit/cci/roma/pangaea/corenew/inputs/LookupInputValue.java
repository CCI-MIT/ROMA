package edu.mit.cci.roma.pangaea.corenew.inputs;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import edu.mit.cci.roma.pangaea.core.VensimException;
import edu.mit.cci.roma.pangaea.core.VensimHelper;

public class LookupInputValue extends BaseInputValue<Map<Integer, String>> {
	
	// we want to make sure that index values are set in proper order
	private final Map<Integer, String> values = new TreeMap<Integer, String>();

	public LookupInputValue(String inputName) {
		super(inputName);
	}


	public void addValue(Integer idx, String value) {
		values.put(idx, value);
	}

	@Override
	public void setValue(String vensimName, VensimHelper vensimHelper) throws VensimException {
		
		if (values.isEmpty()) {
			return;
		}
		
		StringBuilder value = new StringBuilder();
		boolean addComma = false;
		for (Map.Entry<Integer, String> entry : values.entrySet()) {
			if (addComma) {
				value.append(",");
			}
			addComma = true;
			value.append("(").append(entry.getKey()).append(",")
					.append(entry.getValue()).append(")");
		}
		vensimHelper.setLookup(vensimName, value.toString());

	}

	@Override
	public Map<Integer, String> getValue() {
		return Collections.unmodifiableMap(values);
	}

}
