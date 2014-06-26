package edu.mit.cci.roma.pangaea.corenew.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;
import org.simpleframework.xml.ElementList;

import edu.mit.cci.roma.pangaea.core.VensimException;
import edu.mit.cci.roma.pangaea.core.VensimHelper;
import edu.mit.cci.roma.pangaea.corenew.PangaeaPropsUtils;
import edu.mit.cci.roma.pangaea.corenew.processors.OutputProcessor;


public class VensimModelOutputConfig extends BaseVensimVariableInfo {
	
	@ElementList(required=true, inline=true, entry="processorConfig")
	private List<VensimModelOutputProcessorConfig> processorConfig;
	
	private List<OutputProcessor> processors;
	
	public Pair<float[], float[]> computeOutputValues(VensimHelper vensimRun, VensimHelper baselineVensimRun) throws VensimException {
		Stack<Pair<float[], float[]>> executionStack = new Stack<Pair<float[],float[]>>();
		
		for (OutputProcessor processor: getProcessors()) {
			processor.processOutputs(vensimRun, baselineVensimRun, executionStack);
		}
		
		return executionStack.pop();
	}
	
	
	private List<OutputProcessor> getProcessors() {
		if (processors == null) {
			processors = new ArrayList<OutputProcessor>();
			for (VensimModelOutputProcessorConfig singleProcessorConfig: processorConfig) {
				OutputProcessor processor = PangaeaPropsUtils.getOutputProcessorForName(singleProcessorConfig.getName());
				processor.init(singleProcessorConfig.getConfiguration());
				processors.add(processor);
			}
			
		}
		return processors;
	}
}
