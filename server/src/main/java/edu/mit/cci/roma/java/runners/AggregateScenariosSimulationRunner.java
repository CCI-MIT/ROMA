package edu.mit.cci.roma.java.runners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cern.colt.Arrays;
import edu.mit.cci.roma.api.DataType;
import edu.mit.cci.roma.api.Scenario;
import edu.mit.cci.roma.api.Simulation;
import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.java.JavaSimulationRunner;
import edu.mit.cci.roma.server.DefaultServerScenario;

public class AggregateScenariosSimulationRunner implements JavaSimulationRunner {
	public static final String SCENARIO_IDS_PARAM = "scenarioIds";
	private Simulation simulation;

	@Override
	public void init(Map properties) throws SimulationException {

	}

	@Override
	public Map<Variable, Object[]> run(List<Tuple> params, Set<Variable> outputs) throws SimulationException {
		if (params.size() != 1 || !params.get(0).getVar().getName().equals(SCENARIO_IDS_PARAM)) {
			throw new SimulationException(String.format(
					"Scenario aggregator accepts only one param (%s) - list of scenario ids to merge",
					SCENARIO_IDS_PARAM));
		}

		List<DefaultServerScenario> scenariosToCombine = new ArrayList<DefaultServerScenario>();
		simulation = null;
		for (String scenarioId : params.get(0).getValues()[0].split(",")) {
			try {
				DefaultServerScenario scenario = DefaultServerScenario.findDefaultScenario(Long.parseLong(scenarioId));
				if (scenario == null) {
					throw new SimulationException(String.format("Can't find scenario for id (%s)", scenarioId));
				}
				scenariosToCombine.add(scenario);
				simulation = scenario.getSimulation();
			} catch (NumberFormatException e) {
				throw new SimulationException(String.format("Unknown scenario id, should be an integer (%s)",
						scenarioId));
			}
		}

		if (scenariosToCombine.size() < 2) {
			throw new SimulationException(String.format("There is nothing to combine, requires at least 2 scenarios"));
		}

		boolean isEmf27 = simulation.getName().equals("Stanford EMF27");
		Variable emfScenarioInput = isEmf27 ? simulation.getInputs().iterator().next() : null;
		// validate that models are the same
		String scenarioUsed = null;
		for (Scenario s : scenariosToCombine) {
			if (!(s.getSimulation().getId().equals(simulation.getId()))) {
				throw new SimulationException(String.format(
						"Can't combine scenarios, incompatible models: %d != %d, scenarios: [%s]", s.getSimulation()
								.getId(), simulation.getId(), Arrays.toString(params.get(0).getValues())));
			} else if (isEmf27) {
				if (scenarioUsed != null && !scenarioUsed.equals(s.getVariableValue(emfScenarioInput).getValues()[0])) {
					throw new SimulationException(
							String.format(
									"Can't combine scenarios, incompatible scenarios, different input to emf makes scenarios incompatible, inputs %s, %s, scenarios: [%s]",
									scenarioUsed, s.getVariableValue(emfScenarioInput).getValues()[0],
									Arrays.toString(params.get(0).getValues())));

				} else {
					scenarioUsed = s.getVariableValue(emfScenarioInput).getValues()[0];
				}
			}
		}

		// find all indexing variables as they shouldn't be aggregated
		Set<Variable> indexingVariables = new HashSet<Variable>();
		for (Variable output : simulation.getOutputs()) {
			if (output.getIndexingVariable() != null) {
				indexingVariables.add(output.getIndexingVariable());
			}
		}

		Map<Variable, Double[]> variableValuesAggregated = new HashMap<Variable, Double[]>();
		for (Scenario scenario : scenariosToCombine) {
			for (Variable output : simulation.getOutputs()) {
				if (indexingVariables.contains(output))
					continue;

				if (output.getDataType() != DataType.NUM) {
					throw new SimulationException(String.format(
							"Can't aggregate outputs with type different than NUM, given: %s", output));
				}

				String[] variableValue = scenario.getVariableValue(output).getValues();
				Double[] values = variableValuesAggregated.get(output);
				if (values == null) {
					values = new Double[variableValue.length];
					for (int i = 0; i < values.length; i++) {
						values[i] = 0.;
					}
					variableValuesAggregated.put(output, values);
				}
				if (variableValue.length != values.length) {
					throw new SimulationException(String.format(
							"Number of values for variable %d is different in scenarios", output.getId()));
				}
				for (int i = 0; i < values.length; i++) {
					values[i] += Double.parseDouble(variableValue[i]);
				}
			}
		}

		// copy all of the values from the source
		Map<Variable, Object[]> result = new HashMap<Variable, Object[]>();
		for (Tuple t : scenariosToCombine.get(0).getValues_()) {
			if (!variableValuesAggregated.containsKey(t.getVar())) {
				result.put(t.getVar(), t.getValues());
			}
		}

		for (Map.Entry<Variable, Double[]> aggregatedVariableValue : variableValuesAggregated.entrySet()) {
			result.put(aggregatedVariableValue.getKey(), (Object[]) aggregatedVariableValue.getValue());
		}

		return result;
	}

	@Override
	public void prePersistScenario(Scenario scenario) throws SimulationException {
		if (simulation == null) {
			throw new SimulationException("Scenario aggregating simulation wasn't run");
		}
		scenario.setSimulation(simulation);

		// remove scenarioIds input
		Tuple valueToRemove = null;
		for (Tuple scenarioTuple : scenario.getValues_()) {
			if (scenarioTuple.getVar().getName().equals(SCENARIO_IDS_PARAM)) {
				valueToRemove = scenarioTuple;
			}
		}
		scenario.getValues_().remove(valueToRemove);
	}

	@Override
	public Simulation getResultSimulation(Simulation defaultServerSimulation) {
		return simulation;
	}
}
