package edu.mit.cci.roma.util;

import edu.mit.cci.roma.impl.DefaultScenario;
import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.impl.DefaultVariable;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: jintrone
 * Date: 3/14/11
 * Time: 9:17 AM
 */
@XmlRootElement(name="List")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConcreteSerializableCollection {

    @XmlElements({
            @XmlElement(name="Variable", type=DefaultVariable.class),
            @XmlElement(name="Simulation", type=DefaultSimulation.class),
            @XmlElement(name="Scenario", type= DefaultScenario.class)
    })
    public final ArrayList bucket = new ArrayList();

    public ConcreteSerializableCollection() {}

    public ConcreteSerializableCollection(Collection c) {
        bucket.addAll(c);
    }

    public void add(Object o) {
        if (o instanceof Collection) {
            bucket.addAll((Collection)o);
        } else {
            bucket.add(o);
        }
    }


}
