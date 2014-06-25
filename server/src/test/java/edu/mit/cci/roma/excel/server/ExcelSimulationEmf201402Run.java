package edu.mit.cci.roma.excel.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.DefaultScenario;
import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.server.DefaultServerSimulation;
import edu.mit.cci.roma.server.ServerTuple;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml")
@Transactional
public class ExcelSimulationEmf201402Run {
    @Test
    public void testReadExcelFile() throws Exception {
        DefaultSimulation sim = DefaultServerSimulation.findDefaultServerSimulation(38L);

        String[][] expected = {
        		{ "0,00", "0,00", "-0,05", "-0,07", "-0,09", "-0,11", "-0,14", "-0,18", "-0,22", "-0,26", "-0,33"},
        		{ "0,00", "0,00", "-0,00", "-0,01", "-0,02", "-0,03", "-0,05", "-0,06", "-0,07", "-0,08", "-0,09" }
        };

        List<Tuple> inputs = new ArrayList<Tuple>();

        for (Variable v : sim.getInputs()) {

            ServerTuple t = new ServerTuple(v);
            t.persist();
            
            System.out.println(v.getName());
            if (v.getName().equals("Input scenario")) {
                t.setValues(new String[] {"EMF27G1"});
            }
            inputs.add(t);
        }

        DefaultScenario scenario = (DefaultScenario) sim.run(inputs);
        Iterator<Variable> outputVariables = sim.getOutputs().iterator();
        while (outputVariables.hasNext()) {
        	Variable outVar = outputVariables.next();
        	Tuple t = scenario.getVariableValue(outVar);
        	System.out.println(outVar.getName() + "\t" + outVar.getLabels());
        	System.out.println(Arrays.toString(t.getValues()));
        	/*if (outVar.getName().equals("FUND output")) {
        		org.junit.Assert.assertArrayEquals(expected[0], t.getValues());
        	}*/
        }
        
        inputs = new ArrayList<Tuple>();

        for (Variable v : sim.getInputs()) {

            ServerTuple t = new ServerTuple(v);
            t.persist();
            if (v.getName().equals("Input scenario")) {
                t.setValues(new String[] {"550"});
            }
            inputs.add(t);
        }

        scenario = (DefaultScenario) sim.run(inputs);
        outputVariables = sim.getOutputs().iterator();
        while (outputVariables.hasNext()) {
        	Variable outVar = outputVariables.next();
        	Tuple t = scenario.getVariableValue(outVar);
        	if (outVar.getName().equals("FUND output")) {
        		org.junit.Assert.assertArrayEquals(expected[1], t.getValues());
        	}
        }
    }
}
