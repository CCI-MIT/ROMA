package edu.mit.cci.roma.model;

import edu.mit.cci.roma.impl.DefaultSimulation;
import org.springframework.roo.addon.dod.RooDataOnDemand;

@RooDataOnDemand(entity = DefaultSimulation.class)
public class DefaultSimulationDataOnDemand {
      public DefaultSimulation getNewTransientDefaultSimulation(int index) {
        DefaultSimulation obj = new DefaultSimulation();
        obj.setInputs(null);
        obj.setOutputs(null);
        obj.setCreated(null);
        obj.setSimulationVersion(new Integer(index).longValue());
        obj.setDescription(null);
        obj.setName(null);
        obj.setUrl(null);
        return obj;
    }

}
