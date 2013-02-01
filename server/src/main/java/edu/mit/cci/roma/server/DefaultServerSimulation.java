package edu.mit.cci.roma.server;

import edu.mit.cci.roma.api.Scenario;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.excel.ExcelRunnerStrategy;
import edu.mit.cci.roma.excel.ExcelSimulation;
import edu.mit.cci.roma.impl.DefaultScenario;
import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.impl.Tuple;
import org.apache.log4j.Logger;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: jintrone
 * Date: 1/29/13
 * Time: 10:40 AM
 */
public class DefaultServerSimulation extends DefaultSimulation {

   private static Logger log = Logger.getLogger(DefaultServerSimulation.class);

    @Transient
      private transient RunStrategy runStrategy = new RunStrategy.Post();


    public Scenario run(List<Tuple> siminputs) throws SimulationException {

        DefaultScenario result = new DefaultScenario();
        result.setSimulation(this);

        Map<Variable, Tuple> inputMap = new HashMap<Variable, Tuple>();
        for (Tuple t : siminputs) {
            inputMap.put(t.getVar(), t);
        }

        Set<Tuple> response = runRaw(siminputs);
        Set<Variable> outputs = new HashSet<Variable>(getOutputs());
        for (Tuple t : response) {
            if (!outputs.remove(t.getVar())) {
                inputMap.put(t.getVar(), t);
            }
        }
        if (!outputs.isEmpty()) {
            log.warn("Not all outputs were identified, missing: " + outputs);
        }
        result.getValues_().addAll(inputMap.values());
        result.getValues_().addAll(response);
        result.persist();
        return result;

    }


    public void setRunStrategy(RunStrategy r) {
        this.runStrategy = r;
    }

    public RunStrategy getRunStrategy() {
        if (getUrl() != null && getUrl().startsWith(ExcelSimulation.EXCEL_URL) && !(runStrategy instanceof ExcelRunnerStrategy)) {
            new ExcelRunnerStrategy(this);
        }
        return runStrategy;
    }

      /**
     * Runs a roma and returns a map of output variables to tuple values
     *
     * @param siminputs
     * @return
     * @throws SimulationException
     */
    public Set<Tuple> runRaw(Collection<Tuple> siminputs) throws SimulationException {
        Set<Variable> mine = new HashSet<Variable>(getInputs());
        Set<Tuple> result = new HashSet<Tuple>();

        List<Tuple> selectedinputs = new ArrayList<Tuple>();
        for (Tuple t : siminputs) {
            if (mine.remove(t.getVar())) {
                selectedinputs.add(t);
            }
        }
        if (!mine.isEmpty()) {
            throw new SimulationException("Missing input variables: " + mine);
        }
        String response = null;
        response = getRunStrategy().run(url, selectedinputs);

        result.addAll(edu.mit.cci.roma.server.util.U.parseVariableMap(response, getOutputs()));
        result.addAll(edu.mit.cci.roma.server.util.U.parseVariableMap(response, getInputs()));


        return result;
    }


}
