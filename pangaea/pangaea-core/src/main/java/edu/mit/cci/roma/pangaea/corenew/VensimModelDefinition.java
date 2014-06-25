package edu.mit.cci.roma.pangaea.corenew;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

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
	
	
	public VensimModelDefinition(String name, String path, String configFile) throws Exception {
		super();
		this.name = name;
		this.path = path;
		if (configFile != null) {
			URL configUrl = VensimModelConfig.class.getClassLoader().getResource(configFile);

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
		return config.getOutputs();
	}
	public Set<Float> getIndexVals() {
		return config.getIndexVals();
	}
	public Collection<VensimModelInputConfig> getInputs() {
		return config.getInputs();
	}
	
	

}
