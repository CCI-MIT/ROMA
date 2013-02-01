package edu.mit.cci.roma.server;

import edu.mit.cci.roma.impl.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.dod.RooDataOnDemand;

@RooDataOnDemand(entity = Tuple.class)
public class TupleDataOnDemand {

    @Autowired
    private VariableDataOnDemand variableDataOnDemand;

      public Tuple getNewTransientTuple(int index) {
        Tuple obj = new Tuple();

        obj.setVar(variableDataOnDemand.getRandomDefaultVariable());

          return obj;
    }
}
