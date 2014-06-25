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

public class EnRoadsROMAAssetsGeneratorTest {
	
	private File destDir = new File("/tmp/pangaeaEnroads");
    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Test
	public void generateRomaAssets() throws IOException, VensimException, PangaeaException {
		destDir.mkdirs();
		
		// output simulation info
		File simFile = new File(destDir, "pangaeaEnroads_sim.csv");
		File outputsFile = new File(destDir, "pangaeaEnroads_outputs.csv");
		File inputsFile = new File(destDir, "pangaeaEnroads_inputs.csv");
		

		long nextId = new Date().getTime()/1000;
		CSVWriter simCsv = new CSVWriter(new FileWriter(simFile));
		CSVWriter outputsCsv = new CSVWriter(new FileWriter(outputsFile));
		CSVWriter inputsCsv = new CSVWriter(new FileWriter(inputsFile));

		outputsCsv.writeNext(new String[] {"description","id","internalname","name","profile","vartype","units","labels","external","varcontext","indexingid","defaultval","id","max","metadata","min","categories"});
		inputsCsv.writeNext(new String[] {"description","id","internalname","name","profile","vartype","units","labels","external","varcontext","indexingid","defaultval","id","max","metadata","min","categories"});
		simCsv.writeNext(new String[] {"description","id","name","url","state","creation","compositeString","configured","type"});

		String modelName = "EnROADS " + new Date();
		simCsv.writeNext(new String[] {modelName,String.valueOf(nextId),modelName,"","PUBLIC",format.format(new Date()),"NULL","NULL","none"});
		
		VensimModelDefinition modelDefinition = PangaeaPropsUtils.getModelForName("enroads");
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
		
		
		for (String output: modelDefinition.getOutputs()) {
			Map<Integer, String[]> attributes = vensim.getVariableAttributes(output);
			long outputId = nextId++;
			String[] maxes = attributes.get(Vensim.ATTRIB_MAX);
			String[] mins = attributes.get(Vensim.ATTRIB_MIN);
			
			outputsCsv.writeNext(new String[] {
					output, 
					String.valueOf(outputId),
					output,
					output,
					"java.lang.Double",
					"RANGE",
					output,
					attributes.get(Vensim.ATTRIB_UNITS)[0],
					"",
					"INDEXED",
					String.valueOf(indexId),
					"",
					String.valueOf(outputId),
					maxes != null && maxes.length > 0 ? maxes[0] : "NULL",
					String.valueOf(outputId),
					mins != null && mins.length > 0 ? mins[0] : "NULL",
					"NULL"});
		}
		
		for (String input: modelDefinition.getInputs()) {
			Map<Integer, String[]> attributes = vensim.getVariableAttributes(input);
			long inputId = nextId++;
			String[] maxes = attributes.get(Vensim.ATTRIB_MAX);
			String[] mins = attributes.get(Vensim.ATTRIB_MIN);
			
			System.out.println(vensim.getVariableInfo(input));
			inputsCsv.writeNext(new String[] {
					input, 
					String.valueOf(inputId),
					input,
					input,
					"java.lang.Double",
					"RANGE",
					input,
					attributes.get(Vensim.ATTRIB_UNITS)[0],
					"",
					"INDEXED",
					String.valueOf(indexId),
					"",
					String.valueOf(inputId),
					maxes != null && maxes.length > 0 ? maxes[0] : "NULL",
					String.valueOf(inputId),
					mins != null && mins.length > 0 ? mins[0] : "NULL",
					"NULL"});
		}
		
		
		
		for (CSVWriter writer: new CSVWriter[] {simCsv, outputsCsv, inputsCsv}) {
			writer.flush();
			writer.close();
		}
	}

}
