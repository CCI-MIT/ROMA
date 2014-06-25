package edu.mit.cci.roma.pangaea.corenew.config;

import java.util.List;

import org.simpleframework.xml.ElementList;

public class VensimModelConfig {

	@ElementList(type=VensimModelInputConfig.class)
	private List<VensimModelInputConfig> inputs;

	@ElementList(type=VensimModelOutputConfig.class)
	private List<VensimModelOutputConfig> outputs;
	
	
}
