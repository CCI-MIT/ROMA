package edu.mit.cci.roma.java;

import java.util.List;

import org.apache.log4j.Logger;

import edu.mit.cci.roma.api.Scenario;
import edu.mit.cci.roma.api.Simulation;
import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.excel.ExcelRunnerStrategy;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.server.DefaultServerSimulation;
import edu.mit.cci.roma.server.RunStrategy;
import edu.mit.cci.roma.util.SimulationValidationException;
import edu.mit.cci.roma.util.U;

public class JavaRunnerStrategy implements RunStrategy {

	private final DefaultServerSimulation sim;
	private static final Logger log = Logger.getLogger(ExcelRunnerStrategy.class);
	private final JavaSimulation javaSimulation;
	private final Long javaSimulationId;

	public JavaRunnerStrategy(DefaultServerSimulation simulation) throws SimulationValidationException {
		this.sim = simulation;
		sim.setRunStrategy(this);
		JavaRunnerValidation.validateUrl(sim.getUrl());

        javaSimulationId = Long.parseLong(sim.getUrl().substring(JavaSimulation.JAVA_URL.length()));
        javaSimulation = JavaSimulation.findJavaSimulation(javaSimulationId);
	}

	@Override
	public String run(String url, List<Tuple> params) throws SimulationException {
        
        if (javaSimulation == null) {
			throw new SimulationException("Can't find java simulation for id " + javaSimulationId);
        }
        
        
        return U.createStringRepresentation(javaSimulation.run(params));

	}

	@Override
	public void prePersistScenario(Scenario scenario) throws SimulationException {
		javaSimulation.prePersistScenario(scenario);
	}

	@Override
	public Simulation getResultSimulation(Simulation defaultServerSimulation) {
		return javaSimulation.getResultSimulation(defaultServerSimulation);
	}

}
