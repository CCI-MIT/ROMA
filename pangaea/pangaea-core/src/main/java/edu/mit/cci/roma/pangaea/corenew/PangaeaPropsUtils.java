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
import edu.mit.cci.roma.pangaea.corenew.processors.InputProcessor;
import edu.mit.cci.roma.pangaea.corenew.processors.OutputProcessor;

public class PangaeaPropsUtils {
	private static PangaeaPropsUtils instance = new PangaeaPropsUtils();
	
	private final static String PANGAEA_PROPERTIES = "pangaea.properties";
	
	private final static String VENSIM_LIB_PATH = "vensim.lib.path";
	private final static String VENSIM_MODEL_PREFIX = "vensim.model.";
	private static final String MODEL_CONFIG = "model.config.";
	private static final String INPUT_PROCESSORS = "model.input.processors";
	private static final String OUTPUT_PROCESSORS = "model.output.processors";
	/*
	private final static String VENSIM_MODEL_OUTPUT_PREFIX = "model.output.";
	private final static String VENSIM_MODEL_INPUT_PREFIX = "model.input.";
	private final static String VENSIM_MODEL_OUTINDEX_PREFIX = "model.outindex.";
	*/
	private final static String JAVA_LIBRARY_PATH = "java.library.path";


	private Properties pangaeaProperties = new Properties();
	private String vensimLibName;
	private final Map<String, VensimModelDefinition> vensimModels = new HashMap<String, VensimModelDefinition>();
	private final Map<String, Class<InputProcessor>> inputProcessors = new HashMap<String, Class<InputProcessor>>();
	private final Map<String, Class<OutputProcessor>> outputProcessors = new HashMap<String, Class<OutputProcessor>>();
	
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
					throw new RuntimeException("Can't initialize vensim model definition: " + modelName, e);
				}
			}
		}
		
		// get model input processors
		if (pangaeaProperties.containsKey(INPUT_PROCESSORS)) {
			for (String processorClassName: pangaeaProperties.getProperty(INPUT_PROCESSORS).split(";")) {
				Class processorClass;
				try {
					processorClass = Class.forName(processorClassName);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException("Unknown input processor class " + processorClassName, e);
				}
				if (!InputProcessor.class.isAssignableFrom(processorClass)) {
					throw new RuntimeException("Unknown InputProcessor " + processorClassName + " it has to extend " + InputProcessor.class.getName());
				}
				inputProcessors.put(processorClass.getSimpleName(), processorClass);
			}
			
		}
		
		// get model output processors
		if (pangaeaProperties.containsKey(OUTPUT_PROCESSORS)) {
			for (String processorClassName: pangaeaProperties.getProperty(OUTPUT_PROCESSORS).split(";")) {
				Class processorClass;
				try {
					processorClass = Class.forName(processorClassName);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException("Unknown ouptut processor class " + processorClassName, e);
				}
				if (!OutputProcessor.class.isAssignableFrom(processorClass)) {
					throw new RuntimeException("Unknown OutputProcessor " + processorClassName + " it has to extend " + OutputProcessor.class.getName());
				}
				outputProcessors.put(processorClass.getSimpleName(), processorClass);
			}
		}
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
	
	public static InputProcessor getInputProcessorForName(String name) {
		if (! instance.inputProcessors.containsKey(name)) {
			throw new RuntimeException("Uknonwn input processor requested " + name);
		}
		try {
			return instance.inputProcessors.get(name).newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static OutputProcessor getOutputProcessorForName(String name) {
		if (! instance.outputProcessors.containsKey(name)) {
			throw new RuntimeException("Uknonwn output processor requested " + name);
		}
		try {
			return instance.outputProcessors.get(name).newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
