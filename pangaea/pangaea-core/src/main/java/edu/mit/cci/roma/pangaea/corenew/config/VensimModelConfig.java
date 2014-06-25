package edu.mit.cci.roma.pangaea.corenew.config;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;


public class VensimModelConfig {

	@ElementList(type=VensimModelInputConfig.class)
	private List<VensimModelInputConfig> inputs;

	@ElementList(type=VensimModelOutputConfig.class)
	private List<VensimModelOutputConfig> outputs;
	
	@Element
	private String outputIndex;
	
	private Set<Float> indexVals;

	public Collection<VensimModelInputConfig> getInputs() {
		return inputs;
	}

	public Collection<VensimModelOutputConfig> getOutputs() {
		return outputs;
	}

	public Set<Float> getIndexVals() {
		if (indexVals == null) {
			indexVals = new TreeSet<Float>();
			for (String singleVal: outputIndex.split(";")) {
				indexVals.add(Float.parseFloat(singleVal));
			}
		}
		return indexVals;
	}
	
}
