package edu.mit.cci.roma.pangaea.corenew.processors.outputs;

import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;

import edu.mit.cci.roma.pangaea.core.VensimException;
import edu.mit.cci.roma.pangaea.core.VensimHelper;

/**
 * Retrieves variable from vensim and puts it onto execution stack
 * @author janusz
 *
 */
public class GetVariableOutputProcessor implements OutputProcessor {
	private final static String BASELINE_VAR_PREFIX = "From baseline: ";
	private String varName;
	private boolean fromBaseline;

	@Override
	public void init(String configuration) {
		configuration = configuration.trim();
		if (configuration.toLowerCase().startsWith(BASELINE_VAR_PREFIX.toLowerCase())) {
			varName = configuration.substring(BASELINE_VAR_PREFIX.length());
			fromBaseline = true;
		}
		else {
			varName = configuration;
		}
		
	}

	@Override
	public void processOutputs(VensimHelper vensimRun, VensimHelper baselineVensimRun, Stack<Pair<float[], float[]>> executionStack) throws VensimException {
		if (fromBaseline) {
			executionStack.push(baselineVensimRun.getVariableIndexed(varName));
		}
		else {
			executionStack.push(vensimRun.getVariableIndexed(varName));
		}
	}
	
	

}
