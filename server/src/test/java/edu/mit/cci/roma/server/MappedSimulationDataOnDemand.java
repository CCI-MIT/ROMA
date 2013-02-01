package edu.mit.cci.roma.server;


import org.springframework.roo.addon.dod.RooDataOnDemand;

@RooDataOnDemand(entity = MappedServerSimulation.class)
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

}
