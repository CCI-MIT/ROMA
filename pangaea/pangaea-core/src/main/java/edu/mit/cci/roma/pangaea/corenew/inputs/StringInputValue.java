package edu.mit.cci.roma.pangaea.corenew.inputs;

import edu.mit.cci.roma.pangaea.core.VensimException;
import edu.mit.cci.roma.pangaea.core.VensimHelper;

public class StringInputValue extends BaseInputValue<String> {

	private final String value;


	public StringInputValue(String inputName, String value) {
		super(inputName);
		this.value = value;
	}

	@Override
	public void setValue(String vensimName, VensimHelper vensimHelper) throws VensimException {
		vensimHelper.setVariable(vensimName, value);
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "StringInputValue [value=" + value + "]";
	}

}
