package edu.mit.cci.roma.server;

import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.api.TupleStatus;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.impl.DefaultVariable;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.util.SimulationComputationException;
import edu.mit.cci.roma.util.U;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: jintrone
 * Date: 1/29/13
 * Time: 11:43 AM
 */

@Configurable
@Entity
public class MappedServerSimulation extends DefaultServerSimulation {

     @ManyToOne
    private DefaultServerSimulation executorSimulation;

    @ManyToMany(targetEntity = DefaultVariable.class)
    @JoinTable(name = "VAR_MAPPING")
    private Map<DefaultVariable, DefaultVariable> variableMap = new HashMap<DefaultVariable, DefaultVariable>();

    private Integer replication;

    private Integer samplingFrequency = 1;

    @Enumerated
    private ManyToOneMapping manyToOne;

    @ManyToOne(targetEntity = DefaultVariable.class)
    private Variable indexingVariable;


    public MappedServerSimulation() {
        setRunStrategy(new RunStrategy() {

            @Override
            public String run(String url, List<Tuple> params) throws SimulationException {
                Map<Variable, Tuple> inputs = new HashMap<Variable,Tuple>();
                for (Tuple t:params) {
                    inputs.put(t.getVar(),t);
                }
                Map<Variable, String> mergedresults = new HashMap<Variable, String>();
                Set<Tuple> thisrun = new HashSet<Tuple>();
                for (int i = 0; i < replication; i += samplingFrequency) {
                    thisrun.clear();

                    for (Variable input : executorSimulation.getInputs()) {
                        Tuple t = new ServerTuple(input);
                        Tuple values = inputs.get(getVariableMap().get(input));
                        U.copyRange(values, t, i * input.getArity(), (i + 1) * input.getArity());
                        thisrun.add(t);
                    }

                    Set<Tuple> results = executorSimulation.runRaw(thisrun);

                    for (Tuple t : results) {
                        String s = t.getValue_();
                        if (mergedresults.containsKey(t.getVar())) {
                           s = U.join(mergedresults.get(t.getVar()), s);
                        }
                        mergedresults.put(t.getVar(), s);

                    }


                }

                //remap results
                Map<Variable, Tuple> results = new HashMap<Variable, Tuple>();
                for (Map.Entry<Variable,String> ent:mergedresults.entrySet()) {
                    Tuple t = new ServerTuple(getVariableMap().get(ent.getKey()));
                    if (manyToOne != null) {
                        try {
                            t.setValues(new String[]{manyToOne.reduce(U.unescape(ent.getValue(), null, null))});
                        } catch (SimulationComputationException ex) {
                            t.setValues(new String[] {null});
                            t.setStatus(0, TupleStatus.ERR_CALC);
                        }
                    } else {
                        t.setValue_(ent.getValue());
                    }
                     results.put(t.getVar(), t);
                }


                return U.createStringRepresentationFromTuple(results);
            }
        });
    }

    public void setManyToOne(ManyToOneMapping mapping) {
        boolean b = !U.equals(manyToOne, mapping);
        manyToOne = mapping;
        if (b) updateMappings();
    }

    public void setReplication(Integer r) {
        boolean b = !U.equals(replication, r);
        replication = r;
        if (b) updateMappings();
    }

    public void setSamplingFrequency(Integer s) {
        if (s == null || s.intValue() == 0) s = 1;
        boolean b = !U.equals(s, samplingFrequency);
        samplingFrequency = s;
        if (b) updateMappings();
    }

    public void setIndexingVariable(Variable v) {
        this.indexingVariable = v;
        updateIndexVariable();
    }

    public void setExecutorSimulation(DefaultServerSimulation executorSimulation) {
        this.executorSimulation = executorSimulation;
        updateMappings();
    }

    private void updateIndexVariable() {
        if (indexingVariable==null) return;
        if (getOutputs() == null || getOutputs().size() == 0) return;
        Variable myindex = getVariableMap().get(indexingVariable);
        for (Variable mo : getOutputs()) {
            mo.setIndexingVariable(null);
            if (myindex != null) {
                if (!myindex.equals(mo)) {
                    mo.setIndexingVariable(myindex);
                }
            }
        }

    }

    private void updateMappings() {
        if (executorSimulation == null || replication == null || samplingFrequency == null) return;
        getInputs().clear();
        getOutputs().clear();
        getVariableMap().clear();
        DefaultSimulation esim = getExecutorSimulation();
        for (Variable mappedInput : esim.getInputs()) {
            DefaultServerVariable v = new DefaultServerVariable();
            v.persist();
            getInputs().add(v);
            getVariableMap().put((DefaultVariable)mappedInput, v);
            U.copy(mappedInput, v);
            v.setArity(replication * mappedInput.getArity());
        }
        int count =  (int) Math.ceil((double) replication / (double) samplingFrequency);
        int outputArity = manyToOne == null ? count : 1;
        for (Variable mo : esim.getOutputs()) {
            DefaultServerVariable v = new DefaultServerVariable();

            getOutputs().add(v);
            getVariableMap().put((DefaultVariable)mo, v);
            U.copy(mo, v);
            v.setArity(mo.getArity() * outputArity);
            if (getManyToOne() == ManyToOneMapping.SUM) {
                v.setMax_(mo.getMax_()*count);
            }

            v.persist();
        }

        for (Variable mo : getOutputs()) {
            mo.setIndexingVariable(variableMap.get(mo.getIndexingVariable()));
            ((DefaultServerVariable)mo).persist();
        }

        updateIndexVariable();

    }

    public void setVariableMap(Map<DefaultVariable, DefaultVariable> map) {
        this.variableMap.clear();
        if (map != null) {
            variableMap.putAll(map);
        }
    }

    public DefaultSimulation getExecutorSimulation() {
        return this.executorSimulation;
    }

    public Map<DefaultVariable, DefaultVariable> getVariableMap() {
        return this.variableMap;
    }

    public Integer getReplication() {
        return this.replication;
    }

    public Integer getSamplingFrequency() {
        return this.samplingFrequency;
    }

    public ManyToOneMapping getManyToOne() {
        return this.manyToOne;
    }

    public Variable getIndexingVariable() {
        return this.indexingVariable;
    }


    public static MappedServerSimulation findMappedSimulation(Long id) {
        if (id == null) return null;
        return DefaultServerSimulation.entityManager().find(MappedServerSimulation.class, id);
    }

    public static List<MappedServerSimulation> findMappedSimulationEntries(int firstResult, int maxResults) {
        return DefaultServerSimulation.entityManager().createQuery("select o from MappedServerSimulation o", MappedServerSimulation.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }


    public static long countMappedSimulations() {
        return DefaultServerSimulation.entityManager().createQuery("select count(o) from MappedServerSimulation o", Long.class).getSingleResult();
    }

    public static List<MappedServerSimulation> findAllMappedSimulations() {
        return DefaultServerSimulation.entityManager().createQuery("select o from MappedServerSimulation o", MappedServerSimulation.class).getResultList();
    }


}
