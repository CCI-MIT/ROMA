package edu.mit.cci.roma.pangaea.corenew;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import edu.mit.cci.roma.pangaea.core.PangaeaException;
import edu.mit.cci.roma.pangaea.corenew.config.VensimModelOutputConfig;

public class VensimModelResults {
	private final static String VALUE_SEP = ";";
	private final static String VAR_SEP = "&";
	private final static String INDEX_OUTPUT_NAME = "Year";
	
	private final VensimModelDefinition definition;
	private final Map<String, float[]> outputs = new HashMap<String, float[]>();
	

	public VensimModelResults(VensimModelDefinition definition) {
		this.definition = definition;
	}
	
	public void addOutput(VensimModelOutputConfig output, Pair<float[], float[]> value) throws PangaeaException {
		Set<Float> indexVals = definition.getIndexVals();
		float[] valuesIndexed = new float[indexVals.size()];
		int valuesFilled = 0;
		for (int i=0; i < value.getLeft().length; i++) {
			if (indexVals.contains(value.getLeft()[i])) {
				valuesIndexed[valuesFilled++] = value.getRight()[i];
			}
		}
		if (valuesFilled != indexVals.size()) {
			throw new PangaeaException("Not enough values in returned variable (not all number from index has corresponding value) [" + output.getName() + "], values:\n" + Arrays.toString(value.getRight()));
		}
		outputs.put(output.getName(), valuesIndexed);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		String varSep = "";
		for (Entry<String, float[]> entry: outputs.entrySet()) {
			sb.append(varSep);
			sb.append(entry.getKey());
			sb.append("=");
			String sep = "";
			for (float val: entry.getValue()) {
				sb.append(sep);
				sb.append(val);
				sep = VALUE_SEP;
			}
			
			varSep = VAR_SEP;
		}
		if (definition.getIndexVals() != null && definition.getIndexVals().size() > 0) {
			sb.append(varSep);
			sb.append(INDEX_OUTPUT_NAME);
			sb.append("=");
			String sep = "";
			for (Object indexSingleVal: definition.getIndexVals()) {
				sb.append(sep);
				sb.append(indexSingleVal);
				sep = VALUE_SEP;
			}
		}
		
		return sb.toString();
	}

}
