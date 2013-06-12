package edu.mit.cci.roma.server;

import edu.mit.cci.roma.api.DataType;
import edu.mit.cci.roma.api.Scenario;
import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.api.TupleStatus;
import edu.mit.cci.roma.api.Variable;

import edu.mit.cci.roma.impl.Tuple;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml")
@Transactional
public class MappedSimulationTest {


    SimulationMockFactory mock = new SimulationMockFactory();

    @Test
    public void testOneToOne_SubSelect() {

        DefaultServerSimulation sim = mock.getScalarSimulation(0,13,0);


         MappedServerSimulation msim = mock.getMappedSimulation(0,sim,123,10,null);
        Set<Variable> inputs = msim.getInputs();
        Set<Variable> outputs = msim.getOutputs();

        Assert.assertEquals(1, inputs.size());
        Assert.assertEquals(1,outputs.size());

        Assert.assertEquals(123,  inputs.iterator().next().getArity().intValue());
        Assert.assertEquals(13,  outputs.iterator().next().getArity().intValue());
    }

    @Test
    public void testOneToOne_OneToOne_Index() {

        DefaultServerSimulation sim = mock.getScalarSimulation(0,13,0);
        DefaultServerVariable idx = new DefaultServerVariable();
        idx.setArity(1);
        idx.setDataType(DataType.NUM);
        idx.setName("Index");
        idx.persist();
        sim.getInputs().add(idx);


         MappedServerSimulation msim = mock.getMappedSimulation(0,sim,123,1,null);
        msim.updateIndexingVariable(idx);
        Set<Variable> inputs = msim.getInputs();
        Set<Variable> outputs = msim.getOutputs();

        Assert.assertEquals(2,inputs.size());
        Assert.assertEquals(1,outputs.size());


        Assert.assertEquals(123,  inputs.iterator().next().getArity().intValue());
        Assert.assertEquals(123,  inputs.iterator().next().getArity().intValue());

        Variable output = outputs.iterator().next();
        Assert.assertEquals(123, output.getArity().intValue());
        Variable v = output.getIndexingVariable();
        Assert.assertEquals(v.getName(),idx.getName());
        Assert.assertEquals(123,v.getArity().intValue());
    }

    @Test
    public void testRun_oneToOne() throws SimulationException {
       DefaultServerSimulation sim = mock.getScalarSimulation(10,9,0);
        DefaultServerVariable idx = new DefaultServerVariable("Index","Test",1,0,0d,20d);
        idx.persist();
        sim.getInputs().add(idx);


         MappedServerSimulation msim = mock.getMappedSimulation(0,sim,123,1,null);
        msim.updateIndexingVariable(idx);
        Set<Variable> inputs = msim.getInputs();
        List<Tuple> params = new ArrayList<Tuple>();

        for (Variable v:inputs) {
            String[] x = new String[v.getArity()];
            Arrays.fill(x,"10");
            Tuple t = new Tuple(v);
            t.setValues(x);
            params.add(t);
        }

        Scenario s  = msim.run(params);
        for (Variable v:msim.getInputs()) {
            Tuple t = s.getVariableValue(v);
            Assert.assertNotNull(t);
            Assert.assertEquals(v.getArity().intValue(),t.getValues().length);
            Assert.assertEquals("10",t.getValues()[0]);
        }

        for (Variable v:msim.getOutputs()) {
            Tuple t = s.getVariableValue(v);
            Assert.assertNotNull(t);
            Assert.assertEquals(v.getArity().intValue(),t.getValues().length);
            Assert.assertEquals("9",t.getValues()[0]);


        }



    }


     @Test
    public void testRun_oneToOne_Err() throws SimulationException {
       DefaultServerSimulation sim = mock.getScalarSimulation(0,12,0);
        DefaultServerVariable idx = new DefaultServerVariable("Index","Test",1,0,0d,20d);
        idx.persist();
        sim.getInputs().add(idx);


         MappedServerSimulation msim = mock.getMappedSimulation(0,sim,123,1,null);
        msim.updateIndexingVariable(idx);
        Set<Variable> inputs = msim.getInputs();
        List<Tuple> params = new ArrayList<Tuple>();

        for (Variable v:inputs) {
            String[] x = new String[v.getArity()];
            Arrays.fill(x,"21");
            Tuple t = new Tuple(v);
            t.setValues(x);
            params.add(t);
        }

        Scenario s  = msim.run(params);
        for (Variable v:msim.getInputs()) {
            Tuple t = s.getVariableValue(v);
            Assert.assertNotNull(t);
            Assert.assertEquals(v.getArity().intValue(),t.getValues().length);
            Assert.assertNull(t.getValues()[0]);
            Assert.assertEquals(TupleStatus.ERR_OOB,t.getStatus(0));
        }

        for (Variable v:msim.getOutputs()) {
            Tuple t = s.getVariableValue(v);
            Assert.assertNotNull(t);
            Assert.assertEquals(v.getArity().intValue(),t.getValues().length);
            Assert.assertNull(t.getValues()[0]);
            Assert.assertEquals(TupleStatus.ERR_OOB,t.getStatus(0));


        }



    }

        @Test
    public void testRun_oneToOne_subSelect() throws SimulationException {
       DefaultServerSimulation sim = mock.getScalarSimulation(0,9,0);
        DefaultServerVariable idx = new DefaultServerVariable("Index","Test",1,0,0d,20d);

         MappedServerSimulation msim = mock.getMappedSimulation(0,sim,123,1,null);
        msim.setSamplingFrequency(10);
        msim.updateIndexingVariable(idx);
        Set<Variable> inputs = msim.getInputs();
        List<Tuple> params = new ArrayList<Tuple>();

        for (Variable v:inputs) {
            String[] x = new String[v.getArity()];
            Arrays.fill(x,"10");
            Tuple t = new Tuple(v);
            t.setValues(x);
            params.add(t);
        }

        Scenario s  = msim.run(params);
        for (Variable v:msim.getInputs()) {
            Tuple t = s.getVariableValue(v);
            Assert.assertNotNull(t);
            Assert.assertEquals(v.getArity().intValue(),123);
            Assert.assertEquals(v.getArity().intValue(),t.getValues().length);
            Assert.assertEquals("10",t.getValues()[0]);
        }

        for (Variable v:msim.getOutputs()) {
            Tuple t = s.getVariableValue(v);
            Assert.assertNotNull(t);
            Assert.assertEquals(13,v.getArity().intValue());
            Assert.assertEquals(v.getArity().intValue(),t.getValues().length);
            Assert.assertEquals("9",t.getValues()[0]);
        }



    }

        @Test
    public void testRun_oneToOne_subSelect_reduce() throws SimulationException {
      DefaultServerSimulation sim = mock.getScalarSimulation(0,9,0);
       DefaultServerVariable idx = new DefaultServerVariable("Index","Test",1,0,0d,20d);
        idx.persist();
        sim.getInputs().add(idx);


         MappedServerSimulation msim = mock.getMappedSimulation(0,sim,123,1,null);
            msim.setSamplingFrequency(10);
            msim.setManyToOne(ManyToOneMapping.SUM);
        msim.updateIndexingVariable(idx);
        Set<Variable> inputs = msim.getInputs();
        List<Tuple> params = new ArrayList<Tuple>();

        for (Variable v:inputs) {
            String[] x = new String[v.getArity()];
            Arrays.fill(x,"10");
            Tuple t = new Tuple(v);
            t.setValues(x);
            params.add(t);
        }

        Scenario s  = msim.run(params);
        for (Variable v:msim.getInputs()) {
            Tuple t = s.getVariableValue(v);
            Assert.assertNotNull(t);
            Assert.assertEquals(v.getArity().intValue(),123);
            Assert.assertEquals(v.getArity().intValue(),t.getValues().length);
            Assert.assertEquals("10",t.getValues()[0]);
        }

        for (Variable v:msim.getOutputs()) {
            Tuple t = s.getVariableValue(v);
            Assert.assertNotNull(t);
            Assert.assertEquals(v.getArity().intValue(),1);
            Assert.assertEquals(v.getArity().intValue(),t.getValues().length);
            Assert.assertEquals("117",t.getValues()[0]);
        }



    }

     @Test
    public void testRun_oneToOne_subSelect_reduce_err() throws SimulationException {
      DefaultServerSimulation sim = mock.getScalarSimulation(0,21,0);
       DefaultServerVariable idx = new DefaultServerVariable("Index","Test",1,0,0d,20d);
        idx.persist();
        sim.getInputs().add(idx);


         MappedServerSimulation msim = mock.getMappedSimulation(0,sim,123,1,null);
            msim.setSamplingFrequency(10);
            msim.setManyToOne(ManyToOneMapping.SUM);
        msim.updateIndexingVariable(idx);
        Set<Variable> inputs = msim.getInputs();
        List<Tuple> params = new ArrayList<Tuple>();

        for (Variable v:inputs) {
            String[] x = new String[v.getArity()];
            Arrays.fill(x,"21");
            Tuple t = new Tuple(v);
            t.setValues(x);
            params.add(t);
        }

        Scenario s  = msim.run(params);
        for (Variable v:msim.getInputs()) {
            Tuple t = s.getVariableValue(v);
            Assert.assertNotNull(t);
            Assert.assertEquals(v.getArity().intValue(),123);
            Assert.assertEquals(v.getArity().intValue(),t.getValues().length);
            Assert.assertNull(t.getValues()[0]);
            Assert.assertEquals(TupleStatus.ERR_OOB,t.getStatus(4));
        }

        for (Variable v:msim.getOutputs()) {
            Tuple t = s.getVariableValue(v);
            Assert.assertNotNull(t);
            Assert.assertEquals(v.getArity().intValue(),1);
            Assert.assertEquals(v.getArity().intValue(),t.getValues().length);
            Assert.assertNull(t.getValues()[0]);
            Assert.assertEquals(TupleStatus.ERR_CALC,t.getStatus(0));
        }



    }



}
