package edu.mit.cci.roma.pangaea.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.junit.Test;

import com.vensim.Vensim;

import au.com.bytecode.opencsv.CSVWriter;
import edu.mit.cci.roma.pangaea.corenew.PangaeaPropsUtils;
import edu.mit.cci.roma.pangaea.corenew.VensimModelDefinition;
import edu.mit.cci.roma.pangaea.corenew.config.BaseVensimVariableInfo;
import edu.mit.cci.roma.pangaea.corenew.config.VensimModelInputConfig;
import edu.mit.cci.roma.pangaea.corenew.config.VensimModelOutputConfig;

public class ModelROMAAssetsGeneratorTest {
	
	private File destDir = new File("/tmp/pangaeaEnroads");
    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	

	@Test
	public void generateRomaAssetsTest() throws IOException, VensimException, PangaeaException {
		generateRomaAssets("enroads");
		generateRomaAssets("clearn_v75");
	
	}
	
	public void generateRomaAssets(String modelName) throws IOException, VensimException, PangaeaException {
		destDir.mkdirs();
		
		// output simulation info
		File simFile = new File(destDir, modelName + "_sim.csv");
		File outputsFile = new File(destDir, modelName + "_outputs.csv");
		File inputsFile = new File(destDir, modelName + "_inputs.csv");
		

		long nextId = new Date().getTime()/1000;
		CSVWriter simCsv = new CSVWriter(new FileWriter(simFile), ',', '\"', '\\');
		CSVWriter outputsCsv = new CSVWriter(new FileWriter(outputsFile), ',', '\"', '\\');
		CSVWriter inputsCsv = new CSVWriter(new FileWriter(inputsFile), ',', '\"', '\\');

		outputsCsv.writeNext(new String[] {"description","id","internalname","name","profile","vartype","units","labels","external","varcontext","indexingid","defaultval","id","max","metadata","min","categories"});
		inputsCsv.writeNext(new String[] {"description","id","internalname","name","profile","vartype","units","labels","external","varcontext","indexingid","defaultval","id","max","metadata","min","categories"});
		simCsv.writeNext(new String[] {"description","id","name","url","state","creation","compositeString","configured","type"});

		
		VensimModelDefinition modelDefinition = PangaeaPropsUtils.getModelForName(modelName);
		simCsv.writeNext(new String[] {modelDefinition.getConfig().getDescription(),String.valueOf(nextId),modelDefinition.getConfig().getName(),"","PUBLIC",format.format(new Date()),"NULL","NULL","none"});
		
		VensimHelper vensim = new VensimHelper(PangaeaPropsUtils.getVensimLibName(), PangaeaPropsUtils.getModelForName("enroads").getPath());
		
		long indexId = nextId++;

		outputsCsv.writeNext(new String[] {"Year",
				String.valueOf(indexId),
				"Year",
				"Year",
				"java.lang.Integer",
				"RANGE",
				"Year",
				"Year",
				"",
				"INDEX",
				"NULL",
				"",
				String.valueOf(indexId),
				"2100.0",
				String.valueOf(indexId),
				"2000.0",
				"NULL"});
		
		
		for (VensimModelOutputConfig output: modelDefinition.getOutputs()) {
			appendVariableDefinition(outputsCsv, vensim, output, nextId++, indexId);
		}
		
		for (VensimModelInputConfig input: modelDefinition.getInputs()) {
			appendVariableDefinition(inputsCsv, vensim, input, nextId++, 0);
		}
		
		
		
		for (CSVWriter writer: new CSVWriter[] {simCsv, outputsCsv, inputsCsv}) {
			writer.flush();
			writer.close();
		}
	}
	
	private void appendVariableDefinition(CSVWriter writer, VensimHelper vensim, BaseVensimVariableInfo variableInfo, long varId, long indexId) {
		if (variableInfo.isInternalOnly()) {
			return;
		}
		Map<Integer, String[]> attributes = vensim.getVariableAttributes(variableInfo.getVensimContextVariable() != null ? 
				variableInfo.getVensimContextVariable() : variableInfo.getName());
		//"description","id","internalname","name","profile","vartype","units","labels","external","varcontext","indexingid","defaultval","id","max","metadata","min","categories"
		String description = variableInfo.getDescription();
		String internalName = variableInfo.getName(); //variableInfo.getVensimContextVariable() == null ? variableInfo.getName() : variableInfo.getVensimContextVariable();
		String name = variableInfo.getName();
		String profile = variableInfo.getProfile();
		String vartype = variableInfo.getVarType();
		String units = variableInfo.getUnit();
		String labels = variableInfo.getLabel();
		String min = variableInfo.getMin();
		String max = variableInfo.getMax();
		String defaultVal = variableInfo.getDefaultVal();
		
		if (description == null) {
			if (!attributes.isEmpty()) {
				String[] comment = attributes.get(Vensim.ATTRIB_COMMENT);
				if (comment.length > 0) description = comment[0];
			}
			else description = variableInfo.getName();
		}
		if (profile == null) {
			if (!attributes.isEmpty()) {
				String[] increment = attributes.get(Vensim.ATTRIB_INCREMENT);
				if (increment.length > 0) {
					float incrementVal = Float.parseFloat(increment[0]);
					if (incrementVal >= 1) profile = "java.lang.Integer";
				}
				if (profile == null) profile = "java.lang.Double";
			}
		}
		
		if (vartype == null) {
			vartype = "RANGE";
		}
		
		if (units == null) {
			if (!attributes.isEmpty()) {
				String[] unitsAttr = attributes.get(Vensim.ATTRIB_UNITS);
				if (unitsAttr.length > 0) {
					units = unitsAttr[0];
				}
			}
			if (units == null) {
				units = variableInfo.getName();
			}
		}
		
		if (labels == null) {
			labels = variableInfo.getName();
		}
		
		if (min == null) {
			if (!attributes.isEmpty()) {
				String[] minAttr = attributes.get(Vensim.ATTRIB_MIN);
				if (minAttr.length > 0) min = minAttr[0];
			}
		}
		

		if (max == null) {
			if (!attributes.isEmpty()) {
				String[] maxAttr = attributes.get(Vensim.ATTRIB_MAX);
				if (maxAttr.length > 0) max = maxAttr[0];
			}
		}
		if (defaultVal == null && min != null && max != null) {
			try {
				float minF = Float.parseFloat(min);
				float maxF = Float.parseFloat(max);
				defaultVal = String.valueOf(minF + (maxF-minF)/2);
			}
			catch (Exception e) {
				// ignore
			}
		}
		
		writer.writeNext(new String[] {
				description, 
				String.valueOf(varId),
				internalName,
				name,
				profile,
				vartype,
				units,
				labels,
				name,
				"INDEXED",
				String.valueOf(indexId),
				defaultVal,
				String.valueOf(varId),
				max,
				String.valueOf(varId),
				min,
				String.valueOf(variableInfo.getCategories())});
		
	}

}
