package edu.mit.cci.roma.server;

import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.util.U;

import java.util.List;

/**
 * User: jintrone
 * Date: 2/28/11
 * Time: 9:57 AM
 */
public interface RunStrategy {

    public String run(String url, List<Tuple> params) throws SimulationException;


    public static class Post implements RunStrategy {

        @Override
        public String run(String url, List<Tuple> params) throws SimulationException {

            try {
                return U.executePost(url, U.prepareInput(params, true));
            } catch (Exception e) {
                throw new SimulationException(e);
            }
        }
    }




}
