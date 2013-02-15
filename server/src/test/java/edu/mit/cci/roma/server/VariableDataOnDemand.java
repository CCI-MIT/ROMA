package edu.mit.cci.roma.server;

import edu.mit.cci.roma.impl.DefaultVariable;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.dod.RooDataOnDemand;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Configurable
@Component
public class VariableDataOnDemand {


    private Random rnd = new java.security.SecureRandom();

    private List<DefaultServerVariable> data;

    public DefaultServerVariable getNewTransientDefaultVariable(int index) {
        DefaultServerVariable obj = new DefaultServerVariable();
        obj.setName(null);
        obj.setDescription(null);
        obj.setArity(new Integer(index));
        obj.setDataType(null);
        obj.setPrecision_(null);
        obj.setMax_(null);
        obj.setMin_(null);
        obj.setExternalName(null);
        obj.set_optionsRaw("_optionsRaw_" + index);
        obj.setIndexingVariable(null);
        return obj;
    }

    public DefaultVariable getSpecificDefaultVariable(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        DefaultVariable obj = data.get(index);
        return DefaultServerVariable.findDefaultVariable(obj.getId());
    }

    public DefaultServerVariable getRandomDefaultVariable() {
        init();
        DefaultServerVariable obj = data.get(rnd.nextInt(data.size()));
        return DefaultServerVariable.findDefaultVariable(obj.getId());
    }

    public boolean modifyDefaultVariable(DefaultVariable obj) {
        return false;
    }

    public void init() {
        data = DefaultServerVariable.findDefaultVariableEntries(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'DefaultVariable' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }

        data = new java.util.ArrayList<DefaultServerVariable>();
        for (int i = 0; i < 10; i++) {
            DefaultServerVariable obj = getNewTransientDefaultVariable(i);
            obj.persist();
            obj.flush();
            data.add(obj);
        }
    }
}
