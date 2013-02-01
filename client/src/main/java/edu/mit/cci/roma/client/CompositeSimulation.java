package edu.mit.cci.roma.client;

import java.util.Set;

/**
 * Represents a roma that is composed of other simulations
 * @author jintrone
 *
 */
public interface CompositeSimulation extends Simulation {

    public Set<Simulation> getChildren();

    /**
     * An xml file that describes the composite roma
     * @param descriptor
     */
    public void setCompositeDescriptor(String descriptor);
    public String getCompositeDescriptor();

}