package edu.mit.cci.roma.pangaea.core;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
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
	public static void loadProperties() throws TestingUtilsException, VensimException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		TestingUtils.loadPropertiesToSystem(EnRoadsTest.class.getClassLoader().getResource("test.properties").getFile());
		Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
		fieldSysPath.setAccessible( true );
		fieldSysPath.set( null, null );
		
        String libName = System.getProperty(DLL_LIBNAME_PARAM);
        String modelPath = System.getProperty(MODEL_PATH_PARAM);

        System.out.println(System.getenv());
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
	            System.out.println(Arrays.toString(vensimHelper.getVariable(varName)));
	        }
	        
	       System.out.println("end...");
	}
	
	
	
}
