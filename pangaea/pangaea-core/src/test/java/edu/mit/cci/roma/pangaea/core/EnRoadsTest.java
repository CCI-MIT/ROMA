package edu.mit.cci.roma.pangaea.core;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.mit.cci.testing.TestingUtils;
import edu.mit.cci.testing.TestingUtilsException;

public class EnRoadsTest {
    private static VensimHelper vensimHelper;

    public static final String DLL_LIBNAME_PARAM = "vensim_lib_name";
    public static final String MODEL_PATH_PARAM = "vensim_model_path";
    
	
	@BeforeClass
	public static void loadProperties() throws TestingUtilsException, VensimException {
		TestingUtils.loadPropertiesToSystem(EnRoadsTest.class.getClassLoader().getResource("test.properties").getFile());
		
        String libName = System.getProperty(DLL_LIBNAME_PARAM);
        String modelPath = System.getProperty(MODEL_PATH_PARAM);

        vensimHelper = new VensimHelper(libName, modelPath);
	}
	
	
	@Test
	public void variablesTest() throws VensimException, PangaeaException, IOException {
	    
	    FileWriter fw = new FileWriter("/tmp/enroads_vars.txt");
	    
	    fw.append("#### Variable names ####\n");
        for (String varName: vensimHelper.getVariables()) {
            fw.append(varName + "\n");
        }

        fw.flush();
        fw.close();

        fw = new FileWriter("/tmp/enroads_vars_def.txt");
        fw.append("#### Variable definitions ####\n");
	    for (String varName: vensimHelper.getVariables()) {
	        fw.append(vensimHelper.getVariableInfo(varName) + "\n");
	    }
	    
	    fw.flush();
	    fw.close();
	    
	    vensimHelper.run();
	       for (String varName: vensimHelper.getVariables()) {
	           System.out.println(varName);
	            System.out.println(vensimHelper.getVariable(varName));
	        }
	        
	       System.out.println("end...");
	}
	
	
	
}
