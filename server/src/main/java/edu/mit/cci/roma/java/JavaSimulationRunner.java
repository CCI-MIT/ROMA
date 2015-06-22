package edu.mit.cci.roma.java;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.cci.roma.api.Scenario;
import edu.mit.cci.roma.api.Simulation;
import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.Tuple;

public interface JavaSimulationRunner {

	void init(Map properties) throws SimulationException;

	Map<Variable, Object[]> run(List<Tuple> params, Set<Variable> set) throws SimulationException;

	void prePersistScenario(Scenario scenario) throws SimulationException;

	Simulation getResultSimulation(Simulation defaultServerSimulation);

}
