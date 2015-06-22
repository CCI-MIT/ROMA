package edu.mit.cci.roma.pangaea.corenew.processors.outputs;

import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;

import edu.mit.cci.roma.pangaea.core.VensimException;
import edu.mit.cci.roma.pangaea.core.VensimHelper;

public interface OutputProcessor {

	public void init(String configuration);
	public void processOutputs(VensimHelper vensimRun, VensimHelper baselineVensimRun, Stack<Pair<float[], float[]>> executionStack) throws VensimException;
	

}
