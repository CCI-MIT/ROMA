package edu.mit.cci.roma.server.util;

import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.server.DefaultServerSimulation;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import java.util.List;

/**
 * User: jintrone
 * Date: 12/2/11
 * Time: 12:16 PM
 */


public class CL {




    public static void main(String[] args) {

        String[] configs = new String[] {
         "classpath:/META-INF/spring/applicationContext.xml"
        };
        new ClassPathXmlApplicationContext(configs);
        List<? extends DefaultSimulation> existing = DefaultServerSimulation.findAllDefaultServerSimulations();
        for (DefaultSimulation sim : existing) {
           System.err.println(sim.getName());
        }
    }
}
