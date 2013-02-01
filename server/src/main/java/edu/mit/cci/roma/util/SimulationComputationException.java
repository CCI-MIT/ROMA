package edu.mit.cci.roma.util;

import edu.mit.cci.roma.model.SimulationException;

/**
 * User: jintrone
 * Date: 3/9/11
 * Time: 7:39 AM
 */
public class SimulationComputationException extends SimulationException {
    public SimulationComputationException(String s) {
        super(s);
    }

    public SimulationComputationException(Exception e) {
        super(e);
    }

    public SimulationComputationException(String message, Throwable t) {
        super(message, t);
    }
}
