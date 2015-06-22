package edu.mit.cci.roma.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import edu.mit.cci.roma.api.DataType;
import edu.mit.cci.roma.api.Simulation;
import edu.mit.cci.roma.api.SimulationCreationException;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.excel.ExcelSimulation;
import edu.mit.cci.roma.excel.ExcelVariable;
import edu.mit.cci.roma.server.DefaultServerSimulation;
import edu.mit.cci.roma.server.DefaultServerVariable;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/spring/applicationContext.xml", "classpath:/webmvc-test-config.xml"})
public class CreateSimulations201402 {
	

    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Test
    @Transactional
    @Rollback(false)
    public void createAll() throws IOException, ParseException, SimulationCreationException {
    	DefaultServerSimulation sim = (DefaultServerSimulation) createBaseSim("./target/test-classes/2014_may_models/emf_combined/emf_20140520", 1, 11, true);
    }
    
    public Simulation createBaseSim(String path, int inputArity, int outputArity, boolean isExcel) throws IOException, ParseException {

        DefaultServerSimulation sim = (DefaultServerSimulation) readSimulation(path);
        ExcelSimulation esim = null;
        if (isExcel) {
            esim = new ExcelSimulation();
            esim.setSimulation((DefaultServerSimulation) sim);
            esim.setCreation(new Date());
            esim.setFile(IOUtils.toByteArray(new FileInputStream(path + ".xlsx")));
        }
        addVars(sim, path, true, inputArity, isExcel, esim);
        addVars(sim, path, false, outputArity, isExcel, esim);
        
        if (isExcel) {
            esim.persist();
            sim.setUrl(ExcelSimulation.EXCEL_URL + esim.getId());
            sim.persist();
        }
        
        
        return sim;
    }
    
    public static void addVars(Simulation sim, String basename, boolean inputs, int arity, boolean isExcel, ExcelSimulation esim) throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        CSVReader reader = new CSVReader(basename + (inputs ? "_inputs.csv" : "_outputs.csv"));
        Map<String, Variable> varmap = new HashMap<String, Variable>();
        List<Variable> varlist = new ArrayList<Variable>();

        for (Map<String, String> line : reader) {

            DefaultServerVariable v = new DefaultServerVariable(get(line, "name"),
                    get(line, "description"),
                    arity, inferPrecision(get(line, "profile")), //0 for year, otherwise 2
                    getDouble(line, "min"),
                    getDouble(line, "max"));
            v.setUnits(get(line, "units"));
            v.setLabels(get(line, "labels"));//labels for graphs
            v.setDefaultValue(get(line, "defaultval")); //Default value if there's not dataset
            v.setExternalName(get(line, "internalname")); //Doens't need to set
            v.setOptions(parseCategories(get(line, "categories"))); //ignore
            v.setDataType(inferDataType(get(line, "vartype"))); //RANGE
            //v.setVarContext(get(line, "varcontext"));

            v.persist();
            map.put(v.getId_() + "", get(line, "metadata"));
            varlist.add(v);
            varmap.put(get(line, "metadata"), v);
        }

        int i = 0;
        for (Map<String, String> line : reader) {
            Variable v = varlist.get(i);
            if (get(line, "indexingid") != null) {
                Variable indexing = varmap.get(get(line, "indexingid"));
                v.setIndexingVariable(indexing);
                ((DefaultServerVariable) v).persist();


            }
            if (inputs) {
            	if (sim.getInputs() == null) {
            		sim.setInputs(new HashSet<Variable>());
            	}
                sim.getInputs().add(v);
                
                if (isExcel) {
                	if (esim.getInputs() == null) {
                		esim.setInputs(new HashSet<ExcelVariable>());
                	}
                	esim.getInputs().add(new ExcelVariable(esim, (DefaultServerVariable) v, get(line, "sheet"), get(line, "rowsrange")));
                }

            }
            else {
            	if (sim.getOutputs() == null) {
            		sim.setOutputs(new HashSet<Variable>());
            	}
                sim.getOutputs().add(v);
                
                if (isExcel) {
                	if (esim.getOutputs() == null) {
                		esim.setOutputs(new HashSet<ExcelVariable>());
                	}
                	esim.getOutputs().add(new ExcelVariable(esim, (DefaultServerVariable) v, get(line, "sheet"), get(line, "rowsrange")));
                }

            }

            i++;
        }
        ((DefaultServerSimulation) sim).persist();
        //dumpMapping(map, VAR_MAP);
    }
    
    
    
    public static Simulation readSimulation(String basename) throws IOException, ParseException {

        Map<String, String> map = new HashMap<String, String>();
        CSVReader reader = new CSVReader(basename + "_sim.csv");
        Map<String, String> line = reader.readLine(1);

        DefaultServerSimulation sim = new DefaultServerSimulation();
        sim.setName(get(line, "name"));
        sim.setDescription(get(line, "description"));
        sim.setCreated(new Date());
        sim.setUrl(get(line, "url"));
        sim.setSimulationVersion(1l);
        sim.persist();
        return sim;
    }
    

    public static String get(Map<String, String> line, String key) {
        String result = line.get(key);
        return result==null || "NULL".equals(result) || result.isEmpty() ? null : result;
    }
    

    public static int inferPrecision(String profile) {
        if (profile.contains("Integer")) {
            return 0;
        } else return 4;
    }

    public static Double getDouble(Map<String, String> line, String key) {
        String result = get(line, key);
        return result == null ? null : Double.parseDouble(result);
    }
    
    public static String[] parseCategories(String cat) {
        if (cat == null) return null;
        else {
            return cat.split(";");
        }
    }


    public static DataType inferDataType(String vartype) {
        if ("RANGE".equals(vartype)) {
            return DataType.NUM;
        } else if ("CATEGORICAL".equals(vartype)) {
            return DataType.CAT;
        } else if ("FREE".equals(vartype)) {
            return DataType.TXT;
        }
        return DataType.NUM;
    }


}
