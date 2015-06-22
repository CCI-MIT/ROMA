package edu.mit.cci.roma.java;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.cci.roma.api.DataType;
import edu.mit.cci.roma.api.SimulationCreationException;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.java.runners.AggregateOutputsSimulationRunner;
import edu.mit.cci.roma.java.runners.AggregateScenariosSimulationRunner;
import edu.mit.cci.roma.java.runners.RegionalScalingSimulationRunner;
import edu.mit.cci.roma.server.CompositeServerSimulation;
import edu.mit.cci.roma.server.CompositeStepMapping;
import edu.mit.cci.roma.server.DefaultServerSimulation;
import edu.mit.cci.roma.server.DefaultServerVariable;
import edu.mit.cci.roma.server.Step;

public class RegionalModelTestUtils {
	
	private final static String REGIONAL_SIMULATION_NAME_FORMAT_STR = "Regional %s";

	public static DefaultServerSimulation findSimulation(String name, boolean required) {
		DefaultServerSimulation toReturn = null;
		for (DefaultServerSimulation sim : DefaultServerSimulation.findAllDefaultServerSimulations()) {
			if (sim.getName().equals(name)) {
				if (toReturn == null || toReturn.getId() < sim.getId());
				toReturn = sim;
			}
		}
		if (toReturn == null && required) {
			throw new RuntimeException(String.format("Simulation [%s] not found", name));
		}
		return toReturn;
		
	}

	public static DefaultServerSimulation findSimulation(String name) {
		return findSimulation(name, true);
	}

	public static DefaultServerSimulation findOrCreateRegionalSimulation(String baseSimulationName,
			Map<String, String[]> outputsToAggregate) throws SimulationCreationException {
		DefaultServerSimulation regionalSimulation = findSimulation(getRegionalSimulationNameForBaseName(baseSimulationName), false);
		if (regionalSimulation == null) {
			regionalSimulation = createRegionalSimulation(baseSimulationName, outputsToAggregate);
		}
		return regionalSimulation;
	}

	public static String getRegionalSimulationNameForBaseName(String baseSimulationName) {
		return String.format(REGIONAL_SIMULATION_NAME_FORMAT_STR, baseSimulationName);
	}

	private static DefaultServerSimulation createRegionalSimulation(String baseSimulationName,
			Map<String, String[]> outputsToAggregate) throws SimulationCreationException {

		DefaultServerSimulation baseSimulation = findSimulation(baseSimulationName);

		String aggregatingOutputsSimulationName = "Partial aggregating outputs simulation for " + baseSimulationName;
		String regionalScalingSimulationName = "Partial regional scaling simulation for " + baseSimulationName;

		DefaultServerSimulation aggregatingSimulation = findOrCreateOutputsAggregatingSimulation(
				aggregatingOutputsSimulationName, baseSimulation, outputsToAggregate);
		DefaultServerSimulation regionalScalingSimulation = findOrCreateRegionalScalingSimulation(
				regionalScalingSimulationName, aggregatingSimulation);

		return createAggregateOutputsSimulation(getRegionalSimulationNameForBaseName(baseSimulationName),
				baseSimulation, aggregatingSimulation, regionalScalingSimulation, outputsToAggregate);

	}

	private static DefaultServerSimulation findOrCreateOutputsAggregatingSimulation(
			String aggregatingOutputsSimulationName, DefaultServerSimulation baseSimulation,
			Map<String, String[]> outputsToAggregate) {
		DefaultServerSimulation aggregatingSimulation = findSimulation(aggregatingOutputsSimulationName, false);
		if (aggregatingSimulation == null) {
			aggregatingSimulation = createOutputsAggregatingSimulation(baseSimulation,
					aggregatingOutputsSimulationName, outputsToAggregate);
		}

		return aggregatingSimulation;
	}

	private static DefaultServerSimulation createOutputsAggregatingSimulation(
			DefaultServerSimulation simulationToAggregate, String aggregatedSimulationName,
			Map<String, String[]> outputsToAggregate) {


		DefaultServerSimulation aggregateOutputsSimulation = new DefaultServerSimulation();
		aggregateOutputsSimulation.setOutputs(new HashSet<Variable>());
		aggregateOutputsSimulation.setCreated(new Date());
		aggregateOutputsSimulation.setDescription(aggregatedSimulationName);
		aggregateOutputsSimulation.setName(aggregatedSimulationName);
		
		// find enroads outputs that we want to aggregate
		Set<Variable> toAggregate = new HashSet<Variable>();
		for (Map.Entry<String, String[]> outputToAggregateEntry: outputsToAggregate.entrySet()) {
			Variable inputVar = null;
			for (String outputToAggregate: outputToAggregateEntry.getValue()) {
				inputVar = simulationToAggregate.findVariableWithExternalName(outputToAggregate, false);
				toAggregate.add(inputVar);
				
			}
			DefaultServerVariable outVariable = new DefaultServerVariable(toAggregate.iterator().next());
			outVariable.setName(outputToAggregateEntry.getKey());
			outVariable.setExternalName(outputToAggregateEntry.getKey());
			outVariable.setLabels(outputToAggregateEntry.getKey());
			aggregateOutputsSimulation.getOutputs().add(outVariable);

		}

		aggregateOutputsSimulation.setInputs(toAggregate);
		aggregateOutputsSimulation.setSimulationVersion(1L);

		JavaSimulation aggregatingJavaSimulation = new JavaSimulation();
		aggregatingJavaSimulation.setCreation(new Date());
		aggregatingJavaSimulation.setSimulation(aggregateOutputsSimulation);
		aggregatingJavaSimulation.setRunnerClass(AggregateOutputsSimulationRunner.class.getName());
		aggregatingJavaSimulation.setVersion(1);
		aggregatingJavaSimulation.setConfiguration(outputsToAggregate);

		aggregatingJavaSimulation.persist();

		aggregateOutputsSimulation.setUrl(JavaSimulation.JAVA_URL + aggregatingJavaSimulation.getId());
		aggregateOutputsSimulation.persist();
		return aggregateOutputsSimulation;
	}

	private static DefaultServerSimulation findOrCreateRegionalScalingSimulation(String regionalScalingSimulationName,
			DefaultServerSimulation baseSimulation) {
		DefaultServerSimulation regionalScalingSimulation = findSimulation(regionalScalingSimulationName, false);
		if (regionalScalingSimulation == null) {
			regionalScalingSimulation = createRegionalScalingSimulation(baseSimulation, regionalScalingSimulationName);
		}
		return regionalScalingSimulation;
	}

	private static DefaultServerSimulation createRegionalScalingSimulation(DefaultServerSimulation baseSimulation,
			String regionalSimulationName) {

		DefaultServerSimulation regionalScalingSimulation = new DefaultServerSimulation();

		regionalScalingSimulation.setCreated(new Date());
		regionalScalingSimulation.setDescription(regionalSimulationName);
		regionalScalingSimulation.setName(regionalSimulationName);

		regionalScalingSimulation.setInputs(new HashSet<Variable>(baseSimulation.getOutputs()));
		DefaultServerVariable regionVariable = new DefaultServerVariable();
		regionVariable.setArity(1);
		regionVariable.setDataType(DataType.TXT);
		regionVariable.setName(RegionalScalingSimulationRunner.REGION_PARAM);
		regionVariable.setExternalName(RegionalScalingSimulationRunner.REGION_PARAM);
		regionVariable.setName(RegionalScalingSimulationRunner.REGION_PARAM);
		regionalScalingSimulation.getInputs().add(regionVariable);

		regionalScalingSimulation.setOutputs(new HashSet<Variable>(baseSimulation.getOutputs()));

		List<Variable> idexingVariables = new ArrayList<Variable>();
		for (Variable input : regionalScalingSimulation.getInputs()) {
			if (input.getIndexingVariable() != null) {
				idexingVariables.add(input.getIndexingVariable());
			}
		}
		regionalScalingSimulation.getInputs().addAll(idexingVariables);
		regionalScalingSimulation.getOutputs().addAll(idexingVariables);


		regionalScalingSimulation.setSimulationVersion(1L);

		JavaSimulation regionalScalingJavaSimulation = new JavaSimulation();
		regionalScalingJavaSimulation.setCreation(new Date());
		regionalScalingJavaSimulation.setSimulation(regionalScalingSimulation);
		regionalScalingJavaSimulation.setRunnerClass(RegionalScalingSimulationRunner.class.getName());
		regionalScalingJavaSimulation.setVersion(1);
		;

		regionalScalingJavaSimulation.persist();

		regionalScalingSimulation.setUrl(JavaSimulation.JAVA_URL + regionalScalingJavaSimulation.getId());
		regionalScalingSimulation.persist();
		return regionalScalingSimulation;
	}

	private static CompositeServerSimulation createAggregateOutputsSimulation(String compositeSimulationName,
			DefaultServerSimulation baseSimulation, DefaultServerSimulation aggregateOutputsSimulation,
			DefaultServerSimulation regionalScalingSimulation, Map<String, String[]> namesOfAggregatedOutputs)
			throws SimulationCreationException {

		CompositeServerSimulation compositeSim = new CompositeServerSimulation();

		compositeSim.setName(compositeSimulationName);
		compositeSim.setSimulationVersion(1l);
		compositeSim.setCreated(new Date());

		compositeSim.setInputs(new HashSet<Variable>());
		compositeSim.getInputs().addAll(baseSimulation.getInputs());
		Variable regionVariable = regionalScalingSimulation.findVariableWithExternalName(
				RegionalScalingSimulationRunner.REGION_PARAM, true);
		compositeSim.getInputs().add(
				regionalScalingSimulation.findVariableWithExternalName(RegionalScalingSimulationRunner.REGION_PARAM,
						true));

		compositeSim.setOutputs(new HashSet<Variable>());
		compositeSim.getOutputs().addAll(regionalScalingSimulation.getOutputs());

		Step enroadsSimStep = new Step(1, baseSimulation);
		Step aggregationStep = new Step(2, aggregateOutputsSimulation);
		Step regionalScalingStep = new Step(3, regionalScalingSimulation);

		compositeSim.getSteps().add(enroadsSimStep);
		compositeSim.getSteps().add(aggregationStep);
		compositeSim.getSteps().add(regionalScalingStep);

		CompositeStepMapping mapping01 = new CompositeStepMapping(compositeSim, null, enroadsSimStep);

		// add mapping for first step, so all inputs entered by user are passed
		// to enroads
		for (Variable v : compositeSim.getInputs()) {
			if (v != regionVariable)
				mapping01.addLink(v, v);
		}

		CompositeStepMapping mapping03 = new CompositeStepMapping(compositeSim, null, regionalScalingStep);
		mapping03.addLink(regionVariable, regionVariable);

		// map all of the inputs that should be aggregated from source simulation
		CompositeStepMapping mapping12 = new CompositeStepMapping(compositeSim, enroadsSimStep, aggregationStep);
		for (Map.Entry<String, String[]> aggregatedOptuputsInfo: namesOfAggregatedOutputs.entrySet()) {
			for (String aggregatedName: aggregatedOptuputsInfo.getValue()) {
				Variable vOut = baseSimulation.findVariableWithExternalName(aggregatedName, false);
				Variable vIn = aggregateOutputsSimulation.findVariableWithExternalName(aggregatedName, true);
				mapping12.addLink(vIn, vOut);
			}
		}

		CompositeStepMapping mapping13 = new CompositeStepMapping(compositeSim, enroadsSimStep, regionalScalingStep);
		for (Variable input : regionalScalingSimulation.getInputs()) {
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

		return compositeSim;
	}
	
	public static DefaultServerSimulation findOrCreateScenarioAggregatingSimulation(String simulationName) {
		DefaultServerSimulation sim = findSimulation(simulationName, false);
		if (sim == null) {
			sim = createScenarioAggregatingSimulation(simulationName);
		}
		return sim;
	}

	private static DefaultServerSimulation createScenarioAggregatingSimulation(String simulationName) {

		DefaultServerSimulation scenariosAggregatingSimulation = new DefaultServerSimulation();

		scenariosAggregatingSimulation.setCreated(new Date());
		scenariosAggregatingSimulation.setDescription(simulationName);
		scenariosAggregatingSimulation.setName(simulationName);

		scenariosAggregatingSimulation.setInputs(new HashSet<Variable>());
		DefaultServerVariable scenariosVariable = new DefaultServerVariable();
		scenariosVariable.setArity(1);
		scenariosVariable.setDataType(DataType.TXT);
		scenariosVariable.setName(AggregateScenariosSimulationRunner.SCENARIO_IDS_PARAM);
		scenariosVariable.setExternalName(AggregateScenariosSimulationRunner.SCENARIO_IDS_PARAM);
		scenariosAggregatingSimulation.getInputs().add(scenariosVariable);

		scenariosAggregatingSimulation.setOutputs(new HashSet<Variable>());

		scenariosAggregatingSimulation.setSimulationVersion(1L);

		JavaSimulation scenariosAggregatingJavaSimulation = new JavaSimulation();
		scenariosAggregatingJavaSimulation.setCreation(new Date());
		scenariosAggregatingJavaSimulation.setSimulation(scenariosAggregatingSimulation);
		scenariosAggregatingJavaSimulation.setRunnerClass(AggregateScenariosSimulationRunner.class.getName());
		scenariosAggregatingJavaSimulation.setVersion(1);

		scenariosAggregatingJavaSimulation.persist();

		scenariosAggregatingSimulation.setUrl(JavaSimulation.JAVA_URL + scenariosAggregatingJavaSimulation.getId());
		scenariosAggregatingSimulation.persist();
		return scenariosAggregatingSimulation;
	}

}
