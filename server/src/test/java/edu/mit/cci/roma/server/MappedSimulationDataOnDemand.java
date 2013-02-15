package edu.mit.cci.roma.server;


import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.dod.RooDataOnDemand;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Configurable
@Component
public class MappedSimulationDataOnDemand {

     public MappedServerSimulation getNewTransientMappedSimulation(int index) {
        MappedServerSimulation obj = new MappedServerSimulation();
        //obj.setRunStrategy(null);
        obj.setCreated(new java.util.GregorianCalendar(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR), java.util.Calendar.getInstance().get(java.util.Calendar.MONTH), java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH), java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY), java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE), java.util.Calendar.getInstance().get(java.util.Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime());
        obj.setSimulationVersion(new Integer(index).longValue());
        obj.setDescription(null);
        obj.setName(null);
        obj.setUrl(null);
        obj.setManyToOne(null);
        obj.setReplication(new Integer(index));
        obj.setSamplingFrequency(new Integer(index));
        obj.setIndexingVariable(null);
        obj.setExecutorSimulation(null);
        return obj;
    }



    private Random rnd = new java.security.SecureRandom();

    private List<MappedServerSimulation> data;

    public MappedServerSimulation getSpecificMappedSimulation(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        MappedServerSimulation obj = data.get(index);
        return MappedServerSimulation.findMappedSimulation(obj.getId());
    }

    public MappedServerSimulation getRandomMappedSimulation() {
        init();
        MappedServerSimulation obj = data.get(rnd.nextInt(data.size()));
        return MappedServerSimulation.findMappedSimulation(obj.getId());
    }

    public boolean modifyMappedSimulation(MappedServerSimulation obj) {
        return false;
    }

    public void init() {
        data = MappedServerSimulation.findMappedSimulationEntries(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'MappedSimulation' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }

        data = new java.util.ArrayList<MappedServerSimulation>();
        for (int i = 0; i < 10; i++) {
            MappedServerSimulation obj = getNewTransientMappedSimulation(i);
            obj.persist();
            obj.flush();
            data.add(obj);
        }
    }

}
