package edu.mit.cci.roma.java;

import edu.mit.cci.roma.util.SimulationValidationException;

public class JavaRunnerValidation {

	public static void validateUrl(String url) throws SimulationValidationException {
		if (!url.startsWith(JavaSimulation.JAVA_URL)) {
			throw new SimulationValidationException(String.format("Url for built-in java models should begin with %s",
					JavaSimulation.JAVA_URL));
		}
	}

}
