package edu.mit.cci.roma.pangaea.corenew.inputs;


public abstract class BaseInputValue<T> implements InputValue<T> {
	private final String inputName;

	public BaseInputValue(String inputName) {
		this.inputName = inputName;
	}

	public String getInputName() {
		return inputName;
	}

}
