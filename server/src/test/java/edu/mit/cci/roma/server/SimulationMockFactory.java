package edu.mit.cci.roma.server;

import edu.mit.cci.roma.api.DataType;
import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.excel.ExcelRunnerStrategy;
import edu.mit.cci.roma.excel.ExcelSimulation;
import edu.mit.cci.roma.excel.ExcelVariable;

import edu.mit.cci.roma.impl.DefaultVariable;
import edu.mit.cci.roma.impl.DelegatingSim;

import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.util.U;
import org.junit.runner.RunWith;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: jintrone
 * Date: 2/28/11
 * Time: 7:54 AM
 */

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml")
@Component
public class SimulationMockFactory {

    VariableDataOnDemand vdod = new VariableDataOnDemand();


    DefaultSimulationDataOnDemand dod = new DefaultSimulationDataOnDemand();

    MappedSimulationDataOnDemand mdod = new MappedSimulationDataOnDemand();


    public  DefaultServerSimulation createNewDefaultSimulation() {
         DefaultServerSimulation simulation = new  DefaultServerSimulation();
        simulation.setSimulationVersion(1l);
        simulation.setName("Test");
        simulation.setDescription("Test description");
        simulation.persist();
        return simulation;
    }

    public  DefaultServerSimulation getScalarSimulation(int simId, final int output, int varId) {
         DefaultServerSimulation sim = createNewDefaultSimulation();

        DefaultVariable vin = vdod.getSpecificDefaultVariable(varId);
        vin.setArity(1);
        vin.setDataType(DataType.NUM);
        vin.setPrecision_(0);
        vin.setMin_(0d);
        vin.setMax_(10d);

        DefaultVariable vout = vdod.getSpecificDefaultVariable(varId + 1);
        vout.setArity(1);
        vout.setDataType(DataType.NUM);
        vout.setPrecision_(0);
        vout.setMin_(0d);
        vout.setMax_(10d);

        sim.getInputs().add(vin);
        sim.getOutputs().add(vout);

        return new DelegatingSim(sim) {

            {
                delegate.setRunStrategy(new RunStrategy() {

                    @Override
                    public String run(String url, List<Tuple> params) throws SimulationException {
                        Map<Variable, Object[]> data = new HashMap<Variable, Object[]>();
                        Variable v = getOutputs().iterator().next();
                        data.put(v, new String[]{output + ""});
                        return U.createStringRepresentation(data);
                    }
                });
            }
        };

    }

    public static  DefaultServerSimulation configurePassThruStrategy(DefaultServerSimulation sim) {
        return new DelegatingSim(sim) {

            {
                delegate.setRunStrategy(new RunStrategy() {

                    @Override
                    public String run(String url, List<Tuple> params) throws SimulationException {
                        Map<Variable, Object[]> data = new HashMap<Variable, Object[]>();
                        String[] outputvals = new String[params.size()];
                        int i = 0;
                        for (Tuple t:params) {
                            outputvals[i++] = t.getValues()[0];
                        }
                        for (Variable v : getOutputs()) {
                            String[] output = new String[v.getArity()];
                            for (i = 0;i<v.getArity();i++) {
                                output[i] = outputvals[i%outputvals.length];
                                data.put(v, output);
                            }
                        }
                        return U.createStringRepresentation(data);
                    }
                });
            }
        };
    }

    public  MappedServerSimulation getMappedSimulation(int simid,  DefaultServerSimulation embeddedscalar, int replication, int samplingFreq, ManyToOneMapping type) {
         MappedServerSimulation sim = mdod.getNewTransientMappedSimulation(simid);
        sim.setReplication(replication);
        sim.setExecutorSimulation(embeddedscalar);
        sim.setSamplingFrequency(samplingFreq);
        sim.setManyToOne(type);
        return sim;
    }

    public DefaultVariable getVariable(int arity, String name, DataType type, int precision) {
        DefaultVariable v_in = new DefaultVariable();
        v_in.setArity(arity);
        v_in.setName(name);
        v_in.setDataType(type);
        v_in.setPrecision_(precision);
        v_in.setMin_(0d);
        v_in.setMax_(10d);
        v_in.persist();
        return v_in;
    }

    public  DefaultServerSimulation getExcelBasedSimulation(byte[] file) {
         DefaultServerSimulation simulation = new  DefaultServerSimulation();
        simulation.setSimulationVersion(1l);
        simulation.persist();
        DefaultVariable dateinput = new DefaultVariable();
        dateinput.setName("Year");
        dateinput.setDataType(DataType.NUM);
        dateinput.setArity(11);
        dateinput.setPrecision_(0);
        dateinput.persist();
        dateinput.setMin_(2000d);
        dateinput.setMax_(2100d);

        DefaultVariable emissionsinput = new DefaultVariable();
        emissionsinput.setName("Emissions");
        emissionsinput.setDataType(DataType.NUM);
        emissionsinput.setArity(11);
        emissionsinput.setPrecision_(2);
        emissionsinput.persist();
        emissionsinput.setMin_(0d);
        emissionsinput.setMax_(5d);


        DefaultVariable gdpOutput = new DefaultVariable();
        gdpOutput.setDataType(DataType.NUM);
        gdpOutput.setName("% GDP");
        gdpOutput.setArity(11);
        gdpOutput.setPrecision_(2);
        gdpOutput.persist();
        gdpOutput.setMin_(-10d);
        gdpOutput.setMax_(20d);

        simulation.getInputs().add(dateinput);
        simulation.getInputs().add(emissionsinput);
        simulation.getOutputs().add(gdpOutput);

        ExcelSimulation esim = new ExcelSimulation();
        esim.setSimulation(simulation);

        ExcelVariable evdatein = new ExcelVariable();
        evdatein.setSimulationVariable(dateinput);
        evdatein.setCellRange("A3:A13");
        evdatein.setWorksheetName("Inputs_Outputs");
        evdatein.setExcelSimulation(esim);

        ExcelVariable evemissionsin = new ExcelVariable();
        evemissionsin.setSimulationVariable(emissionsinput);
        evemissionsin.setCellRange("B3:B13");
        evemissionsin.setWorksheetName("Inputs_Outputs");
        evemissionsin.setExcelSimulation(esim);

        ExcelVariable evgdpout = new ExcelVariable();
        evgdpout.setSimulationVariable(gdpOutput);
        evgdpout.setCellRange("D3:D13");
        evgdpout.setWorksheetName("Inputs_Outputs");
        evgdpout.setExcelSimulation(esim);

        esim.getInputs().add(evdatein);
        esim.getInputs().add(evemissionsin);
        esim.getOutputs().add(evgdpout);

        esim.setFile(file);

        esim.persist();
        simulation.setUrl("/excel/"+esim.getId());

        simulation.setRunStrategy(new ExcelRunnerStrategy(simulation));
        simulation.persist();

        return simulation;

    }





}
