package edu.mit.cci.roma.server;

import edu.mit.cci.roma.impl.DefaultSimulation;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.dod.RooDataOnDemand;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RooDataOnDemand(entity = DefaultSimulation.class)
@Configurable
@Component
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



    private Random rnd = new java.security.SecureRandom();

    private List<DefaultServerSimulation> data;

    public DefaultSimulation getSpecificDefaultSimulation(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        DefaultSimulation obj = data.get(index);
        return DefaultServerSimulation.findDefaultServerSimulation(obj.getId());
    }

    public DefaultServerSimulation getRandomDefaultSimulation() {
        init();
        DefaultSimulation obj = data.get(rnd.nextInt(data.size()));
        return DefaultServerSimulation.findDefaultServerSimulation(obj.getId());
    }

    public boolean modifyDefaultSimulation(DefaultSimulation obj) {
        return false;
    }

    public void init() {
        data = DefaultServerSimulation.findDefaultServerSimulationEntries(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'DefaultSimulation' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }

        data = new ArrayList<DefaultServerSimulation>();
        for (int i = 0; i < 10; i++) {
            DefaultServerSimulation obj = getNewTransientDefaultSimulation(i);
            obj.persist();
            obj.flush();
            data.add(obj);
        }
    }

}
