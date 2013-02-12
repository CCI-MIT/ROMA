package edu.mit.cci.roma.server;

import edu.mit.cci.roma.impl.DefaultSimulation;
import org.springframework.roo.addon.dod.RooDataOnDemand;

@RooDataOnDemand(entity = DefaultSimulation.class)
public class DefaultSimulationDataOnDemand {
      public DefaultServerSimulation getNewTransientDefaultSimulation(int index) {
        DefaultServerSimulation obj = new DefaultServerSimulation();
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
