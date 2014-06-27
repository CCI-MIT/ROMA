package edu.mit.cci.roma.pangaea.core;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.vensim.Vensim;

import edu.mit.cci.roma.pangaea.corenew.PangaeaPropsUtils;
import edu.mit.cci.roma.pangaea.corenew.VensimModelDefinition;
import edu.mit.cci.roma.pangaea.corenew.VensimModelResults;
import edu.mit.cci.roma.pangaea.corenew.VensimModelRunner;
import edu.mit.cci.roma.pangaea.corenew.config.VensimModelInputConfig;


public class EnRoadsTest {
	
	@Test
	public void calculateBaseline() throws PangaeaException, VensimException {
		VensimModelDefinition def = PangaeaPropsUtils.getModelForName("enroads");
		VensimModelRunner runner = new VensimModelRunner(def);
		Map<String, String> params = new HashMap<String, String>();
		//params.put("GDP per capita rate global", "10");
		//params.put("GDP per capita change start year", "2050");
		runner.runTheModel(new HashMap<String, String>());
		for (VensimModelInputConfig vmic: def.getInputs()) {
			System.out.println(vmic.getName() + ": " + Arrays.toString(runner.getVensim().getVariable(vmic.getVensimContextVariable() == null ? vmic.getName() : vmic.getVensimContextVariable())));
			
		}
		
		
		VensimModelResults results = runner.runTheModel(params);
		//System.out.println("Reference supply: " + Arrays.toString(results.getVensim().getVariable("Energy supply capacity[EBio]")));
		//System.out.println(Arrays.toString(results.getVensim().getVariable("Temperature change from 1990[Deterministic]")));
		//System.out.println(results.getVensim().getVariableInfo("\"R&D success year\""));
		//System.out.println(Arrays.toString(results.getVensim().getVariable("\"R&D success year nuclear\"")));
		/*System.out.println(Arrays.toString(results.getVensim().getVariableIndexed("Global GDP per capita").getLeft()));
		System.out.println(Arrays.toString(results.getVensim().getVariableIndexed("Global GDP per capita").getRight()));
		System.out.println(Arrays.toString(results.getVensim().getVariable("GDP per capita rate global")));
		System.out.println(Arrays.toString(results.getVensim().getVariable("\"Choose GDP/capita rates by country/region\"")));
		System.out.println(Arrays.toString(results.getVensim().getVariable("Time to approach long term capital rate")));
		System.out.println(Arrays.toString(results.getVensim().getVariable("GDP per Capita Convergence Time")));
		System.out.println(Arrays.toString(results.getVensim().getVariable("GDP per Capita Long Run Duration from Start Year")));
		System.out.println(Arrays.toString(results.getVensim().getVariable("Switch for GDP Convergence")));
		System.out.println(Arrays.toString(results.getVensim().getVariable("Target GDP per Capita")));*/
		//System.out.println(results.toString());
		
		
		
		
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
