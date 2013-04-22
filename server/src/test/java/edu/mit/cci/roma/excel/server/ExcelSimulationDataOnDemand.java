package edu.mit.cci.roma.excel.server;

import edu.mit.cci.roma.excel.ExcelSimulation;
import org.springframework.beans.factory.annotation.Configurable;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@Configurable
public class ExcelSimulationDataOnDemand {


    private Random rnd = new java.security.SecureRandom();

    private List<ExcelSimulation> data;

    public ExcelSimulation getNewTransientExcelSimulation(int index) {
        ExcelSimulation obj = new ExcelSimulation();
        obj.setCreation(new java.util.GregorianCalendar(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR), java.util.Calendar.getInstance().get(java.util.Calendar.MONTH), java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH), java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY), java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE), java.util.Calendar.getInstance().get(java.util.Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime());
        obj.setSimulation(null);
        obj.setFile(null);
        return obj;
    }

    public ExcelSimulation getSpecificExcelSimulation(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        ExcelSimulation obj = data.get(index);
        return ExcelSimulation.findExcelSimulation(obj.getId());
    }

    public ExcelSimulation getRandomExcelSimulation() {
        init();
        ExcelSimulation obj = data.get(rnd.nextInt(data.size()));
        return ExcelSimulation.findExcelSimulation(obj.getId());
    }

    public boolean modifyExcelSimulation(ExcelSimulation obj) {
        return false;
    }

    public void init() {
        data = ExcelSimulation.findExcelSimulationEntries(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'ExcelSimulation' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }

        data = new java.util.ArrayList<ExcelSimulation>();
        for (int i = 0; i < 10; i++) {
            ExcelSimulation obj = getNewTransientExcelSimulation(i);
            obj.persist();
            obj.flush();
            data.add(obj);
        }
    }
}
