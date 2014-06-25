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

public class VensimModelDefinition {
	private String name;
	private String path;
	private List<String> outputs = new ArrayList<String>();
	private List<String> inputs = new ArrayList<String>();
	private Set<Float> indexVals = new TreeSet<Float>();
	
	
	public VensimModelDefinition(String name, String path, String configFile) throws Exception {
		super();
		this.name = name;
		this.path = path;
		if (configFile != null) {

			URL configUrl = VensimModelConfig.class.getClassLoader().getResource(configFile);
			System.out.println(VensimModelConfig.class.getClassLoader().getResource("."));
			

			Serializer serializer = new Persister();
			final VensimModelConfig config = serializer.read(VensimModelConfig.class, new File(configUrl.getFile()));
			System.out.println(config);
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
	public List<String> getOutputs() {
		return outputs;
	}
	public void setOutputs(List<String> outputs) {
		this.outputs = outputs;
	}
	public void addIndexVal(String singleIndexVal) {
		indexVals.add(Float.parseFloat(singleIndexVal));
		
	}
	public Set<Float> getIndexVals() {
		return indexVals;
	}
	public Collection<String> getInputs() {
		return inputs;
	}
	
	

}
