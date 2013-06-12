package edu.mit.cci.roma.server;

import edu.mit.cci.roma.impl.DefaultScenario;
import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.impl.DefaultVariable;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.jaxb.JaxbCollection;
import edu.mit.cci.roma.jaxb.JaxbReference;
import edu.mit.cci.roma.util.ConcreteSerializableCollection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 6/12/13
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml")
public class DefaultSimulationSerializationTest {

    @Test
    public void testDeserialization() throws JAXBException {
        DefaultServerScenario scenario = DefaultServerScenario.findDefaultScenario(8532l);
        JAXBContext jaxbContext = JAXBContext.newInstance(DefaultVariable.class,Tuple.class,DefaultSimulation.class,DefaultServerSimulation.class,DefaultScenario.class,JaxbCollection.class,JaxbCollection.class,JaxbReference.class, ConcreteSerializableCollection.class);
        jaxbContext.createMarshaller().marshal(scenario,System.out);

    }
}
