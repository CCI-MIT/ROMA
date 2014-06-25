package edu.mit.cci.roma.pangaea.corenew;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.mit.cci.roma.pangaea.core.PangaeaException;

public class PangaeaPropsUtils {
	private static PangaeaPropsUtils instance = new PangaeaPropsUtils();
	
	private final static String PANGAEA_PROPERTIES = "pangaea.properties";
	
	private final static String VENSIM_LIB_PATH = "vensim.lib.path";
	private final static String VENSIM_MODEL_PREFIX = "vensim.model.";
	private static final String MODEL_CONFIG = "model.config.";
	/*
	private final static String VENSIM_MODEL_OUTPUT_PREFIX = "model.output.";
	private final static String VENSIM_MODEL_INPUT_PREFIX = "model.input.";
	private final static String VENSIM_MODEL_OUTINDEX_PREFIX = "model.outindex.";
	*/
	private final static String JAVA_LIBRARY_PATH = "java.library.path";


	private Properties pangaeaProperties = new Properties();
	private String vensimLibName;
	private Map<String, VensimModelDefinition> vensimModels = new HashMap<String, VensimModelDefinition>();
	
	private PangaeaPropsUtils() {
		URL pangaeaPropertiesUrl = PangaeaPropsUtils.class.getClassLoader().getResource(PANGAEA_PROPERTIES);
		if (pangaeaPropertiesUrl == null) {
			throw new RuntimeException("Can't find pangaea.properties, location: [" +  PangaeaPropsUtils.class.getClassLoader().getResource(".") + "]");
		}
		File pangaeaPropertiesFile = new File(pangaeaPropertiesUrl.getFile());
		if (! pangaeaPropertiesFile.exists()) {
			throw new RuntimeException("Can't find pangaea.properties, location: [" + pangaeaPropertiesFile.getAbsolutePath() + "]");
		}
		
		try {
			FileInputStream propertiesIs = new FileInputStream(PangaeaPropsUtils.class.getClassLoader().getResource(PANGAEA_PROPERTIES).getFile());
			pangaeaProperties.load(propertiesIs);
			propertiesIs.close();
		}
		catch (IOException e) {
			throw new RuntimeException("Can't read " + PANGAEA_PROPERTIES, e);
		}
		
		if (! pangaeaProperties.containsKey(VENSIM_LIB_PATH)) {
			throw new RuntimeException("Can't find required property " + VENSIM_LIB_PATH + " in " + pangaeaPropertiesFile.getAbsolutePath());
		}
		
		
		File vensimLibFile = new File(pangaeaProperties.getProperty(VENSIM_LIB_PATH));
		if (! vensimLibFile.exists()) {
			throw new RuntimeException("Can't find vensim lib file " + vensimLibFile.getAbsolutePath() + " (defined in " + pangaeaPropertiesFile.getAbsolutePath());
		}
		String vensimLibPath = vensimLibFile.getAbsolutePath();
		vensimLibName = vensimLibPath.substring(vensimLibPath.lastIndexOf(File.separator)+1);
		
		vensimLibName = vensimLibName.replace("lib", "");
		vensimLibName = vensimLibName.replace(".so", "");
		
		System.setProperty(JAVA_LIBRARY_PATH, vensimLibFile.getParentFile().getAbsolutePath());
		
		// force rereading of java.library.path

		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		}
		catch (Exception e) {
			throw new RuntimeException("Can't set java library path to " + vensimLibFile.getAbsolutePath());
		}
		
		
		// load vensim
		System.loadLibrary(vensimLibName);
		
		// create models map 
		for (Object property: pangaeaProperties.keySet()) {
			String propertyStr = property.toString();
			if (propertyStr.startsWith(VENSIM_MODEL_PREFIX)) {
				String modelName = propertyStr.substring(VENSIM_MODEL_PREFIX.length());
				String modelPath = pangaeaProperties.getProperty(propertyStr);
				String configFile = pangaeaProperties.getProperty(MODEL_CONFIG + modelName);
				
				File modelFile = new File(modelPath);
				if (! modelFile.exists()) {
					throw new RuntimeException("Can't find model file: " + modelFile.getAbsolutePath() + " as defined in " + pangaeaPropertiesFile.getAbsolutePath());
				}
				
				try {
					vensimModels.put(modelName, new VensimModelDefinition(modelName, modelFile.getAbsolutePath(), configFile));
				} catch (Exception e) {
					throw new RuntimeException("Can't initialize vensim model definition: " + modelName);
				}
			}
		}
		

		// read models outputs 
		/*
		for (Object property: pangaeaProperties.keySet()) {
			String propertyStr = property.toString();
			if (propertyStr.startsWith(VENSIM_MODEL_OUTPUT_PREFIX)) {
				
				String[] modelAndOutputName = propertyStr.substring(VENSIM_MODEL_OUTPUT_PREFIX.length()).split("\\.");
				VensimModelDefinition modelDef = vensimModels.get(modelAndOutputName[0]);
				if (modelDef == null) {
					throw new RuntimeException("Can't find model " + modelAndOutputName[0] + " for which output was defined " + pangaeaPropertiesFile.getAbsolutePath());
				}
				modelDef.getOutputs().add(pangaeaProperties.getProperty(propertyStr));
			}
			else if (propertyStr.startsWith(VENSIM_MODEL_INPUT_PREFIX)) {
				String[] modelAndInputName = propertyStr.substring(VENSIM_MODEL_INPUT_PREFIX.length()).split("\\.");
				VensimModelDefinition modelDef = vensimModels.get(modelAndInputName[0]);
				if (modelDef == null) {
					throw new RuntimeException("Can't find model " + modelAndInputName[0] + " for which output was defined " + pangaeaPropertiesFile.getAbsolutePath());
				}
				modelDef.getInputs().add(pangaeaProperties.getProperty(propertyStr));
			}
			else if (propertyStr.startsWith(VENSIM_MODEL_OUTINDEX_PREFIX)) {
				String modelName = propertyStr.substring(VENSIM_MODEL_OUTINDEX_PREFIX.length());
				VensimModelDefinition modelDef = vensimModels.get(modelName);
				
				for (String singleIndexVal: pangaeaProperties.getProperty(propertyStr).split(",")) {
					modelDef.addIndexVal(singleIndexVal);
				}
				
			}
		}*/
	}
	
	
	private VensimModelDefinition getModelFileForName(String name) throws PangaeaException {
		if (vensimModels.containsKey(name)) {
			return vensimModels.get(name);
		}
		
		throw new PangaeaException("Can't find model for name: " + name);
	}
	
	public static VensimModelDefinition getModelForName(String name) throws PangaeaException {
		return instance.getModelFileForName(name);
	}
	
	public static String getVensimLibName() {
		return instance.vensimLibName;
	}
}
