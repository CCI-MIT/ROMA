package edu.mit.cci.roma.server;

import edu.mit.cci.roma.impl.DefaultVariable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.test.RooIntegrationTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml")
@Transactional
public class VariableIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

    @Autowired
    private VariableDataOnDemand dod;

    @Test
    public void testCountVariables() {
        org.junit.Assert.assertNotNull("Data on demand for 'DefaultVariable' failed to initialize correctly", dod.getRandomDefaultVariable());
        long count = DefaultServerVariable.countDefaultVariables();
        org.junit.Assert.assertTrue("Counter for 'DefaultVariable' incorrectly reported there were no entries", count > 0);
    }

    @Test
    public void testFindVariable() {
        DefaultVariable obj = dod.getRandomDefaultVariable();
        org.junit.Assert.assertNotNull("Data on demand for 'DefaultVariable' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'DefaultVariable' failed to provide an identifier", id);
        obj = DefaultServerVariable.findDefaultVariable(id);
        org.junit.Assert.assertNotNull("Find method for 'DefaultVariable' illegally returned null for id '" + id + "'", obj);
        org.junit.Assert.assertEquals("Find method for 'DefaultVariable' returned the incorrect identifier", id, obj.getId());
    }

    @Test
    public void testFindAllVariables() {
        org.junit.Assert.assertNotNull("Data on demand for 'DefaultVariable' failed to initialize correctly", dod.getRandomDefaultVariable());
        long count = DefaultServerVariable.countDefaultVariables();
        org.junit.Assert.assertTrue("Too expensive to perform a find all test for 'DefaultVariable', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        java.util.List<DefaultVariable> result = DefaultServerVariable.findAllDefaultVariables();
        org.junit.Assert.assertNotNull("Find all method for 'DefaultVariable' illegally returned null", result);
        org.junit.Assert.assertTrue("Find all method for 'DefaultVariable' failed to return any data", result.size() > 0);
    }

    @Test
    public void testFindVariableEntries() {
        org.junit.Assert.assertNotNull("Data on demand for 'DefaultVariable' failed to initialize correctly", dod.getRandomDefaultVariable());
        long count = DefaultServerVariable.countDefaultVariables();
        if (count > 20) count = 20;
        java.util.List<DefaultVariable> result = DefaultServerVariable.findDefaultVariableEntries(0, (int) count);
        org.junit.Assert.assertNotNull("Find entries method for 'DefaultVariable' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'DefaultVariable' returned an incorrect number of entries", count, result.size());
    }

    @Test
    public void testFlush() {
        DefaultServerVariable obj = dod.getRandomDefaultVariable();
        org.junit.Assert.assertNotNull("Data on demand for 'DefaultVariable' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'DefaultVariable' failed to provide an identifier", id);
        obj = DefaultServerVariable.findDefaultVariable(id);
        org.junit.Assert.assertNotNull("Find method for 'DefaultVariable' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyDefaultVariable(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        obj.flush();
        org.junit.Assert.assertTrue("Version for 'DefaultVariable' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

    @Test
    public void testMerge() {
        DefaultServerVariable obj = dod.getRandomDefaultVariable();
        org.junit.Assert.assertNotNull("Data on demand for 'DefaultVariable' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'DefaultVariable' failed to provide an identifier", id);
        obj = DefaultServerVariable.findDefaultVariable(id);
        boolean modified =  dod.modifyDefaultVariable(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        DefaultVariable merged = (DefaultVariable) obj.merge();
        obj.flush();
        org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        org.junit.Assert.assertTrue("Version for 'DefaultVariable' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

    @Test
    public void testPersist() {
        org.junit.Assert.assertNotNull("Data on demand for 'DefaultVariable' failed to initialize correctly", dod.getRandomDefaultVariable());
        DefaultServerVariable obj = dod.getNewTransientDefaultVariable(Integer.MAX_VALUE);
        org.junit.Assert.assertNotNull("Data on demand for 'DefaultVariable' failed to provide a new transient entity", obj);
        org.junit.Assert.assertNull("Expected 'DefaultVariable' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        org.junit.Assert.assertNotNull("Expected 'DefaultVariable' identifier to no longer be null", obj.getId());
    }

    @Test
    public void testRemove() {
        DefaultServerVariable obj = dod.getRandomDefaultVariable();
        org.junit.Assert.assertNotNull("Data on demand for 'DefaultVariable' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'DefaultVariable' failed to provide an identifier", id);
        obj = DefaultServerVariable.findDefaultVariable(id);
        obj.remove();
        obj.flush();
        org.junit.Assert.assertNull("Failed to remove 'DefaultVariable' with identifier '" + id + "'", DefaultServerVariable.findDefaultVariable(id));
    }
}
