package edu.mit.cci.roma.util;

import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.DefaultVariable;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * User: jintrone
 * Date: 3/18/11
 * Time: 1:26 AM
 */
public class VariableJAXBAdapter  extends XmlAdapter<DefaultVariable, Variable> {

    @Override
    public Variable unmarshal(DefaultVariable defaultSimulation) throws Exception {
        return defaultSimulation;
    }

    @Override
    public DefaultVariable marshal(Variable v) throws Exception {
        return (DefaultVariable) v;
    }


     public DefaultVariable marshal(String v) throws Exception {
        return  null;
    }


}
