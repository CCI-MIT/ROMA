package edu.mit.cci.roma.server;

import edu.mit.cci.roma.api.Scenario;
import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.DefaultScenario;
import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.impl.DefaultVariable;
import edu.mit.cci.roma.impl.Tuple;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: jintrone
 * Date: 1/29/13
 * Time: 11:55 AM
 */

@Entity
@Configurable
@DiscriminatorValue(value = "CompositeSimulation")
public class CompositeServerSimulation extends DefaultServerSimulation {

    private List<Step> steps = new ArrayList<Step>();

   
    private Set<CompositeStepMapping> stepMapping = new HashSet<CompositeStepMapping>();

    @Transient
    private static transient Logger log = Logger.getLogger(CompositeServerSimulation.class);

    public Scenario run(List<Tuple> siminputs) throws SimulationException {
        CompositeScenario result = new CompositeScenario();
        result.setSimulation(this);
        Collections.sort(steps, new Comparator<Step>() {
            public int compare(Step step, Step step1) {
                return (int) Math.signum(step.getOrder_() - step1.getOrder_());
            }
        });

        for (Step s : steps) {
            List<Tuple> toNextStep = new ArrayList<Tuple>();
            boolean mappingFound = false;

            for (CompositeStepMapping m : stepMapping) {
                if (!s.equals(m.getToStep())) {
                    continue;
                }
                mappingFound = true;

                log.debug("Running step " + s.getOrder_() + " with step mapping from " + (m.getFromStep() == null ? "start" : m.getFromStep().getOrder_()));
                List<Tuple> fromPriorStep = new ArrayList<Tuple>();
                if (m.getFromStep() == null) {
                    fromPriorStep.addAll(siminputs);
                } else {
                    ScenarioList sl = result.getChildScenarios().get(m.getFromStep());
                    if (sl == null) {
                        throw new SimulationException("Missing scenario information required for step");
                    }
                    for (DefaultScenario scenario : sl.getScenarios()) {
                        for (Variable v : scenario.getSimulation().getOutputs()) {
                            fromPriorStep.add(scenario.getVariableValue(v));
                        }

                    }
                }

                for (Tuple t : fromPriorStep) {
                    if (m.getMapping().containsKey(t.getVar())) {
                        for (DefaultVariable v : m.getMapping().get(t.getVar()).getVariables()) {
                            Tuple nt = ServerTuple.copy(t);
                            nt.setVar(v);
                            toNextStep.add(nt);
                        }
                    }
                }

            }

            if (!mappingFound) {
                throw new SimulationException("Missing a mapping step; cannot reach step " + s.getOrder_());
            }

            for (DefaultSimulation sim : s.getSimulations()) {
                result.addToStep(s, (DefaultScenario) sim.run(toNextStep));
            }
        }

        for (CompositeStepMapping m : stepMapping) {
            if (m.getToStep() == null) {
                ScenarioList sl = result.getChildScenarios().get(m.getFromStep());
                if (sl == null) {
                    throw new SimulationException("Missing scenario information required for final step");
                }
                for (DefaultScenario scenario : sl.getScenarios()) {
                    for (Variable v : scenario.getSimulation().getOutputs()) {
                        if (m.getFromVars().contains(v)) {
                            Tuple old = scenario.getVariableValue(v);
                            // if there is a mapping for current variable, add it to output set
                            if (m.getMapping().containsKey(old.getVar())) {
                                for (DefaultVariable nv : m.getMapping().get(old.getVar()).getVariables()) {
                                    Tuple n = ServerTuple.copy(old);
                                    n.setVar(nv);
                                    result.getValues_().add(n);
                                }
                            }
                        }
                    }
                }
            }

        }
        result.getValues_().addAll(siminputs);
        result.persist();
        return result;


    }


    @ManyToMany(cascade = CascadeType.ALL)
    @javax.persistence.OrderBy("order_")
    public List<Step> getSteps() {
        return this.steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    public Set<CompositeStepMapping> getStepMapping() {
        return this.stepMapping;
    }

    public void setStepMapping(Set<CompositeStepMapping> stepMapping) {
        this.stepMapping = stepMapping;
    }


     public static long countCompositeSimulations() {
        return entityManager().createQuery("select count(o) from CompositeServerSimulation o", Long.class).getSingleResult();
    }

    public static List<CompositeServerSimulation> findAllCompositeSimulations() {
        return entityManager().createQuery("select o from CompositeServerSimulation o", CompositeServerSimulation.class).getResultList();
    }

    public static CompositeServerSimulation findCompositeSimulation(Long id) {
        if (id == null) return null;
        return entityManager().find(CompositeServerSimulation.class, id);
    }

    public static List<CompositeServerSimulation> findCompositeSimulationEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("select o from CompositeServerSimulation o", CompositeServerSimulation.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
}
