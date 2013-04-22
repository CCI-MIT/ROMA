package edu.mit.cci.roma.server;

import edu.mit.cci.roma.impl.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Configurable
@Component
public class TupleDataOnDemand {

    @Autowired
    private VariableDataOnDemand variableDataOnDemand;

      public ServerTuple getNewTransientTuple(int index) {
        ServerTuple obj = new ServerTuple();

        obj.setVar(variableDataOnDemand.getRandomDefaultVariable());

          return obj;
    }


    private Random rnd = new java.security.SecureRandom();

    private List<ServerTuple> data;

    @Autowired
    private VariableDataOnDemand defaultVariableDataOnDemand;

    public ServerTuple getSpecificTuple(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        Tuple obj = data.get(index);
        return ServerTuple.findTuple(obj.getId());
    }

    public ServerTuple getRandomTuple() {
        init();
        Tuple obj = data.get(rnd.nextInt(data.size()));
        return ServerTuple.findTuple(obj.getId());
    }

    public boolean modifyTuple(Tuple obj) {
        return false;
    }

    public void init() {
        data = ServerTuple.findTupleEntries(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'Tuple' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }

        data = new java.util.ArrayList<ServerTuple>();
        for (int i = 0; i < 10; i++) {
            ServerTuple obj = getNewTransientTuple(i);
            obj.persist();
            obj.flush();
            data.add(obj);
        }
    }
}
