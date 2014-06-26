package edu.mit.cci.roma.pangaea.core;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.vensim.Vensim;

import edu.mit.cci.roma.pangaea.corenew.PangaeaPropsUtils;
import edu.mit.cci.roma.pangaea.corenew.VensimModelResults;
import edu.mit.cci.roma.pangaea.corenew.VensimModelRunner;


public class EnRoadsTest {
	
	@Test
	public void calculateBaseline() throws PangaeaException, VensimException {
		VensimModelRunner runner = new VensimModelRunner(PangaeaPropsUtils.getModelForName("enroads"));
		Map<String, String> params = new HashMap<String, String>();
		params.put("Global population scenario", "3");
		params.put("Source subsidy CCS", "1");
		params.put("Emissions price time to achieve initial target", "2020");
		params.put("Breakthrough commercialization time new tech", "2015.0");
		VensimModelResults results = runner.runTheModel(params);
		//System.out.println("Reference supply: " + Arrays.toString(results.getVensim().getVariable("Energy supply capacity[EBio]")));
		System.out.println(Arrays.toString(results.getVensim().getVariable("Temperature change from 1990[Deterministic]")));
		System.out.println(results.getVensim().getVariableInfo("Energy supply capacity"));
		System.out.println(results.toString());
		
		
		
		
	}
	
	//@Test
	public void saveVarables() throws VensimException, PangaeaException, IOException {
		VensimHelper helper = new VensimHelper(PangaeaPropsUtils.getVensimLibName(), PangaeaPropsUtils.getModelForName("enroads").getPath());
		
		//helper.setVariable("Population scenario", 3);
		FileWriter fw = new FileWriter("/tmp/enroads_vars.txt");
		for (String var: helper.getVariables()) {
			fw.append(var);
			fw.append("\n");
			fw.append(helper.getVariableInfo(var));
			fw.append("\n");
		}
		
		fw.flush();
		fw.close();
		
		//float[] tmp = helper.getVariable("Temp change from preindust[2C]");
		String varName = "Absolute adjustments to costs";
		System.out.println(helper.getVariableInfo(varName));
		float[] tmp = helper.getVariable(varName + "[[FCoal]]");
		String[] unitAttr = null;
		if (varName.indexOf("[") > 0) {
			unitAttr = helper.getVariableAttributes(varName.substring(0, varName.indexOf("["))).get(Vensim.ATTRIB_UNITS);
			helper.getVariable(varName);
		}
		
		System.out.println(Arrays.toString(tmp));
		
		System.out.println(helper.getVariableInfo("Source subsidy renewables"));
	}
	
}
