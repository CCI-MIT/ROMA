package edu.mit.cci.roma.java;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.server.DefaultServerSimulation;
import edu.mit.cci.roma.server.DefaultServerVariable;
import edu.mit.cci.roma.server.ServerTuple;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml")
@Transactional
public class OutputsTest {
	
	//@Test
	public void test() throws SimulationException {
		
		for (DefaultServerSimulation sim: DefaultServerSimulation.findAllDefaultServerSimulations()) {
			System.out.println(sim.getId() + ": " + sim.getOutputs().size());
			for (Variable v: sim.getOutputs()) {
				System.out.println("\t" + v.getExternalName() + "\t" + v.getLabels() + "\t" + v.getName());
			}
		}
			
	}
	
	@Test
	public void testEmf() throws Exception {
		DefaultServerSimulation sim = DefaultServerSimulation.findDefaultServerSimulation(42L);
		
		List<Tuple> inputs = new ArrayList<Tuple>();
		Tuple region = new ServerTuple(DefaultServerVariable.findDefaultVariable(814L));
		Tuple scenario = new ServerTuple(DefaultServerVariable.findDefaultVariable(366L));
		scenario.setValues(new String[] {"EMF27G7"});
		region.setValues(new String[] {"US"});
		inputs.add(region);
		inputs.add(scenario);
		
		sim.run(inputs);
	}
}
