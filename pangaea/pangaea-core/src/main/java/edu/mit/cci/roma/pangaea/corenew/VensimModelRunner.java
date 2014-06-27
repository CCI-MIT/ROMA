package edu.mit.cci.roma.pangaea.corenew;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import edu.mit.cci.roma.pangaea.core.PangaeaConnection;
import edu.mit.cci.roma.pangaea.core.PangaeaException;
import edu.mit.cci.roma.pangaea.core.VensimException;
import edu.mit.cci.roma.pangaea.core.VensimHelper;
import edu.mit.cci.roma.pangaea.corenew.config.VensimModelInputConfig;
import edu.mit.cci.roma.pangaea.corenew.config.VensimModelOutputConfig;

public class VensimModelRunner {

    private static Logger log = Logger.getLogger(PangaeaConnection.class);
    private final static int VENSIM_CONTEXT_CREATION_MAX_FAILURE_COUNT = 10;
    
    
	private VensimModelDefinition definition; 
    private VensimHelper vensim;
    private VensimHelper baselineVensim;
	
	public VensimModelRunner(VensimModelDefinition definition) throws PangaeaException {
		this.definition = definition;
		
        String libName = PangaeaPropsUtils.getVensimLibName();
        String modelPath = definition.getPath();
        
		for (int i = 0; i < VENSIM_CONTEXT_CREATION_MAX_FAILURE_COUNT && vensim == null; i++) {
			try {
				log.info("creating new vensim helper\n\tdll lib: " + libName + "\n\tmodel path: " + modelPath);
				baselineVensim = new VensimHelper(libName, modelPath);
				vensim = new VensimHelper(libName, modelPath);
			} catch (Throwable e) {
				log.error("An exception was thrown when initializing Vensim, try: " + i, e);
			}
		}
		if (vensim == null) {
			throw new PangaeaException("Can't initialize vensim");
		}
	}
	
	public VensimModelResults runTheModel(Map<String, String> inputs) throws PangaeaException {
		
		try {
			baselineVensim.run();
			// process inputs
			for (VensimModelInputConfig inputConfig: definition.getInputs()) {
				inputs = inputConfig.processInputValues(inputs);
			}
			for (Map.Entry<String, String> entry: inputs.entrySet()) {
				vensim.setVariable(entry.getKey(), entry.getValue());
			}
			vensim.run();
			
			VensimModelResults vensimModelResults = new VensimModelResults(definition, vensim);
			for (VensimModelOutputConfig output: definition.getOutputs()) {
				vensimModelResults.addOutput(output, output.computeOutputValues(vensim, baselineVensim));
			}
			return vensimModelResults;
		}
		catch (VensimException e) {
			throw new PangaeaException("Exception thrown when trying to run the model", e);
		}
		
		
	}
	
	public VensimHelper getVensim() {
		return vensim;
	}
	

}
