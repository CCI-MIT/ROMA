package edu.mit.cci.roma.java.runners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.cci.roma.api.DataType;
import edu.mit.cci.roma.api.Scenario;
import edu.mit.cci.roma.api.Simulation;
import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.java.JavaSimulationRunner;

public class AggregateOutputsSimulationRunner implements JavaSimulationRunner {
	private Map<String, List<String>> outputsToAggregate = new HashMap<String, List<String>>();

	@Override
	public void init(Map properties) throws SimulationException {
		for (Object key : properties.keySet()) {
			if (outputsToAggregate.get(key) instanceof List<?>) {
				throw new SimulationException(String.format(
						"Invalid value for key %s, should be array of strings but got %s", key,
						outputsToAggregate.get(key).getClass()));
			}
			outputsToAggregate.put(key.toString(), (List<String>) properties.get(key));
		}

		if (outputsToAggregate.isEmpty()) {
			throw new SimulationException("No configuration given, don't know what to aggregate");
		}
	}

	@Override
	public Map<Variable, Object[]> run(List<Tuple> params, Set<Variable> outputs) throws SimulationException {
		Map<Variable, Object[]> result = new HashMap<Variable, Object[]>();
		Map<String, Tuple> inputsByName = getMapOfTuplesByVarName(params);

		for (Variable output : outputs) {
			if (!outputsToAggregate.containsKey(output.getName())) {
				continue;
			}

			if (output.getDataType() != DataType.NUM) {
				throw new SimulationException("Don't know how to aggregate non numeric variables");
			}

			List<String> variablesToAggregate = outputsToAggregate.get(output.getName());

			Double[] aggregatedValues = null;
			for (String nameOfVarToAggregate : variablesToAggregate) {
				if (!inputsByName.containsKey(nameOfVarToAggregate)) {
					throw new SimulationException(String.format("Can't find variable that should be aggregated %s",
							nameOfVarToAggregate));
				}

				String[] aggregatedInputValues = inputsByName.get(nameOfVarToAggregate).getValues();
				if (aggregatedValues == null) {
					aggregatedValues = new Double[aggregatedInputValues.length];
					for (int i = 0; i < aggregatedValues.length; i++) {
						aggregatedValues[i] = 0.;
					}
				}

				if (aggregatedValues.length != aggregatedInputValues.length) {
					throw new SimulationException(String.format(
							"Values array size is incompatible, expected %d, but got %d (for input %s)",
							aggregatedValues.length, aggregatedInputValues.length, nameOfVarToAggregate));
				}

				for (int i = 0; i < aggregatedInputValues.length; i++) {
					if (aggregatedInputValues[i] == null) continue;
					aggregatedValues[i] += Double.parseDouble(aggregatedInputValues[i]);
				}
			}

			result.put(output, aggregatedValues);

		}
		
		return result;

	}

	private Map<String, Tuple> getMapOfTuplesByVarName(List<Tuple> tuples) {
		Map<String, Tuple> result = new HashMap<String, Tuple>();

		for (Tuple tuple : tuples) {
			result.put(tuple.getVar().getExternalName(), tuple);
		}
		return result;
	}

	@Override
	public void prePersistScenario(Scenario scenario) throws SimulationException {

	}

	@Override
	public Simulation getResultSimulation(Simulation defaultServerSimulation) {
		return defaultServerSimulation;
	}
}
