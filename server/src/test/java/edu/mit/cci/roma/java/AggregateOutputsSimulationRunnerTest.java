package edu.mit.cci.roma.java;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cern.colt.Arrays;
import edu.mit.cci.roma.api.DataType;
import edu.mit.cci.roma.api.Scenario;
import edu.mit.cci.roma.api.SimulationCreationException;
import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.java.runners.AggregateOutputsSimulationRunner;
import edu.mit.cci.roma.java.runners.RegionalScalingSimulationRunner;
import edu.mit.cci.roma.server.CompositeServerSimulation;
import edu.mit.cci.roma.server.CompositeStepMapping;
import edu.mit.cci.roma.server.DefaultServerSimulation;
import edu.mit.cci.roma.server.DefaultServerVariable;
import edu.mit.cci.roma.server.ServerTuple;
import edu.mit.cci.roma.server.Step;
import flexjson.JSONDeserializer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml")
@Transactional
public class AggregateOutputsSimulationRunnerTest {
	private String[] outputsToAggregate = new String[] {"Emissions from energy", "Emissions from land use", "Other GHG emissions"};

	@SuppressWarnings({ "rawtypes", "unchecked" }) Map<String, String> enRoadsInputValues = (Map<String, String>) new JSONDeserializer()
	.deserialize("{\"734\":\"2\",\"735\":\"2.2\",\"736\":\"1\",\"737\":\"1\","
			+ "\"738\":\"0\",\"739\":\"2015\",\"740\":\"2100\",\"741\":\"0\","
			+ "\"742\":\"2015\",\"743\":\"2100\",\"744\":\"0\",\"745\":\"2015\","
			+ "\"746\":\"2100\",\"747\":\"0\",\"748\":\"2015\",\"749\":\"2100\","
			+ "\"750\":\"0\",\"751\":\"2020\",\"752\":\"0\",\"753\":\"2020\","
			+ "\"754\":\"0\",\"755\":\"2020\",\"756\":\"0\",\"757\":\"2020\","
			+ "\"758\":\"0\",\"759\":\"2015\",\"760\":\"3\",\"761\":\"0\",\"762\":\"0\","
			+ "\"763\":\"0\",\"764\":\"2015\",\"765\":\"2050\", \"region\":\"US\"}");
	
	//@Test
	public void test() throws SimulationException {
		DefaultServerSimulation lastEnroads = null;
		DefaultServerSimulation aggregated = null;
		for (DefaultServerSimulation sim : DefaultServerSimulation.findAllDefaultServerSimulations()) {
			System.out.println(sim.getId() + ", " + sim.getName() + "\t" + sim.getUrl());
			if (sim.getName().contains("EnROADS")) {
				lastEnroads = sim;
			}
			if (sim.getName().equals("Aggregated emissions EnRoads model")) {
				aggregated = sim;
			}
		}


		List<Tuple> simInputs = new ArrayList<Tuple>();

		for (Variable var : aggregated.getInputs()) {
			String value = enRoadsInputValues.get(var.getId().toString());
			if (value == null) {
				value = enRoadsInputValues.get(var.getExternalName());
			}
			Tuple t = new ServerTuple(var);
			t.setValues(new String[] { value });
			simInputs.add(t);
		}

		Scenario outScenario = lastEnroads.run(simInputs);

		System.out.println(outScenario);
		for (Tuple t: outScenario.getValues_()) {
			System.out.println(t.getVar().getName() + ": " + Arrays.toString(t.getValues()));
		}
		
		outScenario = aggregated.run(simInputs);
		System.out.println(outScenario);
		for (Tuple t: outScenario.getValues_()) {
			System.out.println(t.getVar().getName() + ": " + Arrays.toString(t.getValues()));
		}
		
	}
	
	//@Test
	@Transactional
    @Rollback(false)
    public void createAggregateOutputsSimulation() throws SimulationCreationException {
		
		DefaultServerSimulation lastEnroads = null;
		
		for (DefaultServerSimulation sim : DefaultServerSimulation.findAllDefaultServerSimulations()) {
			if (sim.getName().contains("EnROADS"))
				lastEnroads = sim;
		}
		DefaultServerSimulation aggregateOutputsSimulation = createOutputsAggregatingSimulation(lastEnroads);
		DefaultServerSimulation regionalScalingSimulation = createRegionalScalingSimulation(aggregateOutputsSimulation);
		
		CompositeServerSimulation compositeSim = new CompositeServerSimulation();
		
		
		compositeSim.setName("Aggregated emissions EnRoads model");
		compositeSim.setSimulationVersion(1l);
		compositeSim.setCreated(new Date());

		compositeSim.setInputs(new HashSet<Variable>());
		compositeSim.getInputs().addAll(lastEnroads.getInputs());
		Variable regionVariable = regionalScalingSimulation.findVariableWithExternalName(RegionalScalingSimulationRunner.REGION_PARAM, true);
		compositeSim.getInputs().add(regionalScalingSimulation.findVariableWithExternalName(RegionalScalingSimulationRunner.REGION_PARAM, true));
		
		compositeSim.setOutputs(new HashSet<Variable>());
		compositeSim.getOutputs().addAll(regionalScalingSimulation.getOutputs());

		
		Step enroadsSimStep = new Step(1, lastEnroads);
		Step aggregationStep = new Step(2, aggregateOutputsSimulation);
		Step regionalScalingStep = new Step(3, regionalScalingSimulation);

		compositeSim.getSteps().add(enroadsSimStep);
		compositeSim.getSteps().add(aggregationStep);
		compositeSim.getSteps().add(regionalScalingStep);
		
        CompositeStepMapping mapping01 = new CompositeStepMapping(compositeSim, null, enroadsSimStep);
        
        // add mapping for first step, so all inputs entered by user are passed to enroads
        for (Variable v : compositeSim.getInputs()) {
        	if (v != regionVariable)
        		mapping01.addLink(v, v);
        }

        CompositeStepMapping mapping03 = new CompositeStepMapping(compositeSim, null, regionalScalingStep);
        mapping03.addLink(regionVariable, regionVariable);
        
        CompositeStepMapping mapping12 = new CompositeStepMapping(compositeSim, enroadsSimStep, aggregationStep);
        for (int i = 0; i < outputsToAggregate.length; i++) {
        	Variable vOut = lastEnroads.findVariableWithExternalName(outputsToAggregate[i], false);
        	Variable vIn = aggregateOutputsSimulation.findVariableWithExternalName(outputsToAggregate[i], true);
        	mapping12.addLink(vIn, vOut);
        }
        
        CompositeStepMapping mapping13 = new CompositeStepMapping(compositeSim, enroadsSimStep, regionalScalingStep);
        for (Variable input: regionalScalingSimulation.getInputs()) {
        	if (input.getIndexingVariable() != null) {
        		mapping13.addLink(input.getIndexingVariable(), input.getIndexingVariable());
        	}
        }

        CompositeStepMapping mapping23 = new CompositeStepMapping(compositeSim, aggregationStep, regionalScalingStep);
        for (Variable v : aggregateOutputsSimulation.getOutputs()) {
        	mapping23.addLink(v, regionalScalingSimulation.findVariableWithExternalName(v.getExternalName(), true));
        }

        CompositeStepMapping mapping3out = new CompositeStepMapping(compositeSim, regionalScalingStep, null);
        for (Variable v : regionalScalingSimulation.getOutputs()) {
        	mapping3out.addLink(v, regionalScalingSimulation.findVariableWithExternalName(v.getExternalName(), true));
        }
        
        compositeSim.persist();
        System.out.println(compositeSim);
	}
	
	private DefaultServerSimulation createOutputsAggregatingSimulation(DefaultServerSimulation enroads) {
		// find enroads outputs that we want to aggregate
		Set<Variable> toAggregate = new HashSet<Variable>();
		for (String outputToAggregate: outputsToAggregate) {
			toAggregate.add(enroads.findVariableWithExternalName(outputToAggregate, false));
			
		}
		
		// create output variable based on any of output variables from source simulation
		DefaultServerVariable outVariable = new DefaultServerVariable(toAggregate.iterator().next());
		outVariable.setName("GHG emissions");
		outVariable.setExternalName("GHG emissions");
		
		DefaultServerSimulation aggregateOutputsSimulation = new DefaultServerSimulation();
		aggregateOutputsSimulation.setCreated(new Date());
		aggregateOutputsSimulation.setDescription("GHG aggregator");
		aggregateOutputsSimulation.setName("GHG aggregator");
		
		aggregateOutputsSimulation.setOutputs(new HashSet<Variable>());
		aggregateOutputsSimulation.getOutputs().add(outVariable);

		
		aggregateOutputsSimulation.setInputs(toAggregate);
		aggregateOutputsSimulation.setSimulationVersion(1L);
		
		
		JavaSimulation aggregatingJavaSimulation = new JavaSimulation();
		aggregatingJavaSimulation.setCreation(new Date());
		aggregatingJavaSimulation.setSimulation(aggregateOutputsSimulation);
		aggregatingJavaSimulation.setRunnerClass(AggregateOutputsSimulationRunner.class.getName());
		aggregatingJavaSimulation.setVersion(1);;
		
		aggregatingJavaSimulation.persist();
		
		aggregateOutputsSimulation.setUrl(JavaSimulation.JAVA_URL + aggregatingJavaSimulation.getId());
		aggregateOutputsSimulation.persist();
		return aggregateOutputsSimulation;
	}
	

	private DefaultServerSimulation createRegionalScalingSimulation(DefaultServerSimulation sourceSimulation) {
		
		DefaultServerSimulation regionalScalingSimulation = new DefaultServerSimulation();
		regionalScalingSimulation.setCreated(new Date());
		regionalScalingSimulation.setDescription("Regional scaling simulation");
		regionalScalingSimulation.setName("Regional scaling simulation");

		regionalScalingSimulation.setInputs(new HashSet<Variable>(sourceSimulation.getOutputs()));
		DefaultServerVariable regionVariable = new DefaultServerVariable();
		regionVariable.setArity(1);
		regionVariable.setDataType(DataType.TXT);
		regionVariable.setName(RegionalScalingSimulationRunner.REGION_PARAM);
		regionVariable.setExternalName(RegionalScalingSimulationRunner.REGION_PARAM);
		regionVariable.setName(RegionalScalingSimulationRunner.REGION_PARAM);
		regionalScalingSimulation.getInputs().add(regionVariable);
		
		regionalScalingSimulation.setOutputs(new HashSet<Variable>(sourceSimulation.getOutputs()));

		for (Variable input: regionalScalingSimulation.getInputs()) {
			if (input.getIndexingVariable() != null) {
				regionalScalingSimulation.getInputs().add(input.getIndexingVariable());
				regionalScalingSimulation.getOutputs().add(input.getIndexingVariable());
			}
		}
		
		
		regionalScalingSimulation.setSimulationVersion(1L);
		
		
		JavaSimulation regionalScalingJavaSimulation = new JavaSimulation();
		regionalScalingJavaSimulation.setCreation(new Date());
		regionalScalingJavaSimulation.setSimulation(regionalScalingSimulation);
		regionalScalingJavaSimulation.setRunnerClass(RegionalScalingSimulationRunner.class.getName());
		regionalScalingJavaSimulation.setVersion(1);;
		
		regionalScalingJavaSimulation.persist();
		
		regionalScalingSimulation.setUrl(JavaSimulation.JAVA_URL + regionalScalingJavaSimulation.getId());
		regionalScalingSimulation.persist();
		return regionalScalingSimulation;
	}
}
