package edu.mit.cci.roma.server;

import edu.mit.cci.roma.impl.DefaultVariable;
import edu.mit.cci.roma.impl.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml")
@Transactional
public class TupleIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }



    @Autowired
    private TupleDataOnDemand dod;

    @Test
    public void testCountTuples() {
        org.junit.Assert.assertNotNull("Data on demand for 'Tuple' failed to initialize correctly", dod.getRandomTuple());
        long count = ServerTuple.countTuples();
        org.junit.Assert.assertTrue("Counter for 'Tuple' incorrectly reported there were no entries", count > 0);
    }

    @Test
    public void testFindTuple() {
        ServerTuple obj = dod.getRandomTuple();
        org.junit.Assert.assertNotNull("Data on demand for 'Tuple' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Tuple' failed to provide an identifier", id);
        obj = ServerTuple.findTuple(id);
        org.junit.Assert.assertNotNull("Find method for 'Tuple' illegally returned null for id '" + id + "'", obj);
        org.junit.Assert.assertEquals("Find method for 'Tuple' returned the incorrect identifier", id, obj.getId());
    }

    //@Test
    public void testFindAllTuples() {
        org.junit.Assert.assertNotNull("Data on demand for 'Tuple' failed to initialize correctly", dod.getRandomTuple());
        long count = ServerTuple.countTuples();
        org.junit.Assert.assertTrue("Too expensive to perform a find all test for 'Tuple', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        java.util.List<ServerTuple> result = ServerTuple.findAllTuples();
        org.junit.Assert.assertNotNull("Find all method for 'Tuple' illegally returned null", result);
        org.junit.Assert.assertTrue("Find all method for 'Tuple' failed to return any data", result.size() > 0);
    }

    @Test
    public void testFindTupleEntries() {
        org.junit.Assert.assertNotNull("Data on demand for 'Tuple' failed to initialize correctly", dod.getRandomTuple());
        long count = ServerTuple.countTuples();
        if (count > 20) count = 20;
        java.util.List<ServerTuple> result = ServerTuple.findTupleEntries(0, (int) count);
        org.junit.Assert.assertNotNull("Find entries method for 'Tuple' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'Tuple' returned an incorrect number of entries", count, result.size());
    }

    @Test
    public void testFlush() {
        ServerTuple obj = dod.getRandomTuple();
        org.junit.Assert.assertNotNull("Data on demand for 'Tuple' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Tuple' failed to provide an identifier", id);
        obj = ServerTuple.findTuple(id);
        org.junit.Assert.assertNotNull("Find method for 'Tuple' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyTuple(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        obj.flush();
        org.junit.Assert.assertTrue("Version for 'Tuple' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

    @Test
    public void testMerge() {
        ServerTuple obj = dod.getRandomTuple();
        org.junit.Assert.assertNotNull("Data on demand for 'Tuple' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Tuple' failed to provide an identifier", id);
        obj = ServerTuple.findTuple(id);
        boolean modified =  dod.modifyTuple(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        Tuple merged = (Tuple) obj.merge();
        obj.flush();
        org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        org.junit.Assert.assertTrue("Version for 'Tuple' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

    @Test
    public void testPersist() {
        org.junit.Assert.assertNotNull("Data on demand for 'Tuple' failed to initialize correctly", dod.getRandomTuple());
        ServerTuple obj = dod.getNewTransientTuple(Integer.MAX_VALUE);
        org.junit.Assert.assertNotNull("Data on demand for 'Tuple' failed to provide a new transient entity", obj);
        org.junit.Assert.assertNull("Expected 'Tuple' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        org.junit.Assert.assertNotNull("Expected 'Tuple' identifier to no longer be null", obj.getId());
    }

    @Test
    public void testRemove() {
         DefaultServerVariable v = new DefaultServerVariable();
        v.persist();
        ServerTuple obj = new ServerTuple(v);

        obj.persist();
        org.junit.Assert.assertNotNull("'Tuple' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("'Tuple' failed to provide an identifier", id);
        obj = ServerTuple.findTuple(id);
        obj.remove();
        obj.flush();
        org.junit.Assert.assertNull("Failed to remove 'Tuple' with identifier '" + id + "'", ServerTuple.findTuple(id));
    }
}
