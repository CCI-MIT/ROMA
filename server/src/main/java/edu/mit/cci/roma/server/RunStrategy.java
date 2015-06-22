package edu.mit.cci.roma.server;

import java.util.List;

import edu.mit.cci.roma.api.Scenario;
import edu.mit.cci.roma.api.Simulation;
import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.util.U;

/**
 * User: jintrone
 * Date: 2/28/11
 * Time: 9:57 AM
 */
public interface RunStrategy {

    public String run(String url, List<Tuple> params) throws SimulationException;
    public void prePersistScenario(Scenario scenario) throws SimulationException;


    public static class Post implements RunStrategy {

        @Override
        public String run(String url, List<Tuple> params) throws SimulationException {

            try {
                return U.executePost(url, U.prepareInput(params, true));
            } catch (Exception e) {
                throw new SimulationException(e);
            }
        }

		@Override
		public void prePersistScenario(Scenario scenario) throws SimulationException {
			
		}

		@Override
		public Simulation getResultSimulation(Simulation defaultServerSimulation) {
			return defaultServerSimulation;
		}
    }


	public Simulation getResultSimulation(Simulation defaultServerSimulation);




}
