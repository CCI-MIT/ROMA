package edu.mit.cci.roma.excel.server;

import edu.mit.cci.roma.excel.ExcelSimulation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.test.RooIntegrationTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Component
@Configurable
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml")
@Transactional
public class ExcelSimulationIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }



       @Autowired
       private ExcelSimulationDataOnDemand dod;

       @Test
       public void testCountExcelSimulations() {
           org.junit.Assert.assertNotNull("Data on demand for 'ExcelSimulation' failed to initialize correctly", dod.getRandomExcelSimulation());
           long count = ExcelSimulation.countExcelSimulations();
           org.junit.Assert.assertTrue("Counter for 'ExcelSimulation' incorrectly reported there were no entries", count > 0);
       }

       @Test
       public void testFindExcelSimulation() {
           ExcelSimulation obj = dod.getRandomExcelSimulation();
           org.junit.Assert.assertNotNull("Data on demand for 'ExcelSimulation' failed to initialize correctly", obj);
           java.lang.Long id = obj.getId();
           org.junit.Assert.assertNotNull("Data on demand for 'ExcelSimulation' failed to provide an identifier", id);
           obj = ExcelSimulation.findExcelSimulation(id);
           org.junit.Assert.assertNotNull("Find method for 'ExcelSimulation' illegally returned null for id '" + id + "'", obj);
           org.junit.Assert.assertEquals("Find method for 'ExcelSimulation' returned the incorrect identifier", id, obj.getId());
       }

       @Test
       public void testFindAllExcelSimulations() {
           org.junit.Assert.assertNotNull("Data on demand for 'ExcelSimulation' failed to initialize correctly", dod.getRandomExcelSimulation());
           long count = ExcelSimulation.countExcelSimulations();
           org.junit.Assert.assertTrue("Too expensive to perform a find all test for 'ExcelSimulation', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
           java.util.List<ExcelSimulation> result = ExcelSimulation.findAllExcelSimulations();
           org.junit.Assert.assertNotNull("Find all method for 'ExcelSimulation' illegally returned null", result);
           org.junit.Assert.assertTrue("Find all method for 'ExcelSimulation' failed to return any data", result.size() > 0);
       }

       @Test
       public void testFindExcelSimulationEntries() {
           org.junit.Assert.assertNotNull("Data on demand for 'ExcelSimulation' failed to initialize correctly", dod.getRandomExcelSimulation());
           long count = ExcelSimulation.countExcelSimulations();
           if (count > 20) count = 20;
           java.util.List<ExcelSimulation> result = ExcelSimulation.findExcelSimulationEntries(0, (int) count);
           org.junit.Assert.assertNotNull("Find entries method for 'ExcelSimulation' illegally returned null", result);
           org.junit.Assert.assertEquals("Find entries method for 'ExcelSimulation' returned an incorrect number of entries", count, result.size());
       }

       @Test
       public void testFlush() {
           ExcelSimulation obj = dod.getRandomExcelSimulation();
           org.junit.Assert.assertNotNull("Data on demand for 'ExcelSimulation' failed to initialize correctly", obj);
           java.lang.Long id = obj.getId();
           org.junit.Assert.assertNotNull("Data on demand for 'ExcelSimulation' failed to provide an identifier", id);
           obj = ExcelSimulation.findExcelSimulation(id);
           org.junit.Assert.assertNotNull("Find method for 'ExcelSimulation' illegally returned null for id '" + id + "'", obj);
           boolean modified =  dod.modifyExcelSimulation(obj);
           java.lang.Integer currentVersion = obj.getVersion();
           obj.flush();
           org.junit.Assert.assertTrue("Version for 'ExcelSimulation' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
       }

       @Test
       public void testMerge() {
           ExcelSimulation obj = dod.getRandomExcelSimulation();
           org.junit.Assert.assertNotNull("Data on demand for 'ExcelSimulation' failed to initialize correctly", obj);
           java.lang.Long id = obj.getId();
           org.junit.Assert.assertNotNull("Data on demand for 'ExcelSimulation' failed to provide an identifier", id);
           obj = ExcelSimulation.findExcelSimulation(id);
           boolean modified =  dod.modifyExcelSimulation(obj);
           java.lang.Integer currentVersion = obj.getVersion();
           ExcelSimulation merged = (ExcelSimulation) obj.merge();
           obj.flush();
           org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
           org.junit.Assert.assertTrue("Version for 'ExcelSimulation' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
       }

       @Test
       public void testPersist() {
           org.junit.Assert.assertNotNull("Data on demand for 'ExcelSimulation' failed to initialize correctly", dod.getRandomExcelSimulation());
           ExcelSimulation obj = dod.getNewTransientExcelSimulation(Integer.MAX_VALUE);
           org.junit.Assert.assertNotNull("Data on demand for 'ExcelSimulation' failed to provide a new transient entity", obj);
           org.junit.Assert.assertNull("Expected 'ExcelSimulation' identifier to be null", obj.getId());
           obj.persist();
           obj.flush();
           org.junit.Assert.assertNotNull("Expected 'ExcelSimulation' identifier to no longer be null", obj.getId());
       }

       @Test
       public void testRemove() {
           ExcelSimulation obj = dod.getRandomExcelSimulation();
           org.junit.Assert.assertNotNull("Data on demand for 'ExcelSimulation' failed to initialize correctly", obj);
           java.lang.Long id = obj.getId();
           org.junit.Assert.assertNotNull("Data on demand for 'ExcelSimulation' failed to provide an identifier", id);
           obj = ExcelSimulation.findExcelSimulation(id);
           obj.remove();
           obj.flush();
           org.junit.Assert.assertNull("Failed to remove 'ExcelSimulation' with identifier '" + id + "'", ExcelSimulation.findExcelSimulation(id));
       }

}
