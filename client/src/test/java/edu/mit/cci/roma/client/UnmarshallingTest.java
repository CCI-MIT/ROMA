package edu.mit.cci.roma.client;

import edu.mit.cci.roma.api.Simulation;
import edu.mit.cci.roma.client.model.impl.ClientSimulation;

import edu.mit.cci.roma.impl.DefaultScenario;
import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.impl.DefaultVariable;
import edu.mit.cci.roma.jaxb.JaxbUtils;
import edu.mit.cci.roma.util.ConcreteSerializableCollection;
import edu.mit.cci.roma.jaxb.JaxbCollection;
import edu.mit.cci.roma.jaxb.JaxbReference;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author: jintrone
 * @date: May 17, 2010
 */
public class UnmarshallingTest {


    private static Logger log = Logger.getLogger(UnmarshallingTest.class);

    @Test
    public void testUnmarshall() throws FileNotFoundException, JAXBException {


        System.setProperty(JaxbUtils.RESOLVER_FACTORY_PROPERTY,"edu.mit.cci.roma.client.comm.MockResolver");

        JAXBContext context = JAXBContext.newInstance(DefaultSimulation.class, DefaultVariable.class, DefaultScenario.class,
                ConcreteSerializableCollection.class, JaxbCollection.class, JaxbReference.class);
        System.setProperty("jaxb.debug", "true");


        Unmarshaller um = context.createUnmarshaller();
        InputStream stream = ClientSimulation.class.getResourceAsStream("/marshalledelements/simple/DefaultSimulations.xml");
        Object o = um.unmarshal(stream);

        Assert.assertNotNull(o);
        Assert.assertTrue(o instanceof ConcreteSerializableCollection);
        Simulation sim = (edu.mit.cci.roma.api.Simulation) ((ConcreteSerializableCollection)o).bucket.toArray()[0];
        Assert.assertTrue(sim.getInputs().size() ==1);
        Assert.assertTrue("fooey".equals(sim.getInputs().toArray()[0]));

    }



   


}
