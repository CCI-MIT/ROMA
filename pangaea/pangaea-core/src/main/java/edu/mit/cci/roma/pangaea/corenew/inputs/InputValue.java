package edu.mit.cci.roma.pangaea.corenew.inputs;

import edu.mit.cci.roma.pangaea.core.VensimException;
import edu.mit.cci.roma.pangaea.core.VensimHelper;

public interface InputValue<T> {
	public void setValue(String vensimName, VensimHelper vensimHelper) throws VensimException;

	public T getValue();

}