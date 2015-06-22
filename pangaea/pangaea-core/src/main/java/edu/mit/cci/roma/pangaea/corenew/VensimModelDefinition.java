package edu.mit.cci.roma.pangaea.corenew;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import edu.mit.cci.roma.pangaea.core.VensimException;
import edu.mit.cci.roma.pangaea.corenew.config.VensimModelConfig;
import edu.mit.cci.roma.pangaea.corenew.config.VensimModelInputConfig;
import edu.mit.cci.roma.pangaea.corenew.config.VensimModelOutputConfig;

public class VensimModelDefinition {
	private String name;
	private String path;
	private List<String> outputs = new ArrayList<String>();
	private List<String> inputs = new ArrayList<String>();
	private Set<Float> indexVals = new TreeSet<Float>();
	private final VensimModelConfig config; 
	private static final Logger _log = Logger.getLogger(VensimModelDefinition.class);
	
	
	public VensimModelDefinition(String name, String path, String configFile) throws Exception {
		super();
		this.name = name;
		this.path = path;
		if (configFile != null) {
			URL configUrl = VensimModelConfig.class.getClassLoader().getResource(configFile);
			if (configUrl == null) {
				String message = String.format("Can't find configUrl for model %s, model path: %s, config file path: ", name, path, configFile); 
				_log.error(message);
				throw new VensimException(message);
			}
			Serializer serializer = new Persister();
			config = serializer.read(VensimModelConfig.class, new File(configUrl.getFile()));
		}
		else {
			config = null;
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Collection<VensimModelOutputConfig> getOutputs() {
		if (config == null) return new ArrayList<VensimModelOutputConfig>();
		return config.getOutputs();
	}
	public Set<Float> getIndexVals() {
		return config.getIndexVals();
	}
	public Collection<VensimModelInputConfig> getInputs() {
		if (config == null) return new ArrayList<VensimModelInputConfig>();
		return config.getInputs();
	}
	
	public VensimModelConfig getConfig() {
		return config;
	}
	
	

}
