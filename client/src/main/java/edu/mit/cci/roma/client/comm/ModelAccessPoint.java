package edu.mit.cci.roma.client.comm;

import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.client.MetaData;
import edu.mit.cci.roma.client.Scenario;
import edu.mit.cci.roma.client.Simulation;
import org.apache.commons.lang.StringUtils;

import java.net.InetAddress;

/**
* User: jintrone
* Date: 3/16/11
* Time: 4:12 PM
*/
public enum ModelAccessPoint implements RestAccessPoint {
    RUN_MODEL_URL("/defaultsimulations/%s/run"),
    GET_SIMULATION("/defaultsimulations"),
    GET_SCENARIO("/defaultscenarios"),
    GET_VARIABLE("/variables"),
    EDIT_SCENARIO_URL("/rest/scenariostate");

    String url;

    ModelAccessPoint(String s) {
       this.url = s;
    }

    public String create(String context, String... params) {
        StringBuffer buf = new StringBuffer(!context.startsWith("http://")?"http://":"");
        buf.append(context);

        int start = StringUtils.countMatches(url, "%");

        if (start > -1) {
            buf.append(String.format(url,params));

        } else {
            buf.append(url);
        }
        for (int i =start;i<params.length;i++) {
            buf.append("/");
            buf.append(params[i]);
        }
        return buf.toString();
    }

    public static ModelAccessPoint forClass(Class clazz) {
       if (Simulation.class.isAssignableFrom(clazz) || edu.mit.cci.roma.api.Simulation.class.isAssignableFrom(clazz)) {
            return GET_SIMULATION;
        } else if (MetaData.class.isAssignableFrom(clazz) || Variable.class.isAssignableFrom(clazz)) {
            return GET_VARIABLE;
        } else if (Scenario.class.isAssignableFrom(clazz) || edu.mit.cci.roma.api.Scenario.class.isAssignableFrom(clazz)) {
           return GET_SCENARIO;
        }
        return null;
    }




}
