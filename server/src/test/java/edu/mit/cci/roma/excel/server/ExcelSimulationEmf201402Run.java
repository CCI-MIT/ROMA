package edu.mit.cci.roma.excel.server;

import java.util.ArrayList;
import java.util.Arrays;
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
        DefaultSimulation sim = DefaultServerSimulation.findDefaultServerSimulation(32L);

        String[] expect = new String[]{"3,22","6,69","8,17","10,75","10,03", "7,36","6,61","5,48"};

        List<Tuple> inputs = new ArrayList<Tuple>();

        for (Variable v : sim.getInputs()) {

            ServerTuple t = new ServerTuple(v);
            t.persist();
            if (v.getName().equals("Input scenario")) {
                t.setValues(new String[] {"Scenario 2"});
            }
            else if (v.getName().equals("Input level")) {
                t.setValues(new String[] {"Overshoot"});
            }
            else {
                t.setValues(new String[] {"3,7"});
            }
            inputs.add(t);

        }

        DefaultScenario scenario = (DefaultScenario) sim.run(inputs);
        Tuple t = scenario.getVariableValue(sim.getOutputs().iterator().next());
        System.out.println(Arrays.toString(t.getValues()));
        System.out.println(Arrays.toString(t.getValues()));
        Assert.assertArrayEquals(expect, t.getValues());


    }
}
