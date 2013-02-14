package edu.mit.cci.roma.server;

import com.sun.tools.javac.resources.version;
import edu.mit.cci.roma.api.SimulationCreationException;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.impl.DefaultVariable;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Configurable
public class CompositeStepMapping {


    @ManyToOne
    private CompositeServerSimulation parentsim;


    @ManyToOne
    private Step fromStep;

    @ManyToOne
    private Step toStep;


    @Transient
    private Set<Variable> fromVars = null;

    @Transient
    private Set<Variable> toVars = null;


    @ManyToMany(targetEntity = VariableList.class)
    @JoinTable(name = "STEP_VAR_TO_VAR")
    private Map<DefaultVariable, VariableList> mapping = new HashMap<DefaultVariable, VariableList>();


    public CompositeStepMapping() {

    }

    public CompositeStepMapping(CompositeServerSimulation csim, Step s1, Step s2) throws SimulationCreationException {
        this.parentsim = csim;

        if (s1 != null && s2 != null && s1.getOrder_() >= s2.getOrder_()) {
            throw new SimulationCreationException("Mappings can only be between steps that are strictly ordered");
        }

        if ((s1 != null && !csim.getSteps().contains(s1)) || (s2 != null && !csim.getSteps().contains(s2))) {
            throw new SimulationCreationException("Mappings can only be established between steps in the designated parent roma");
        }

        setFromStep(s1);
        setToStep(s2);
        this.parentsim.getStepMapping().add(this);
    }


//    private void put(DefaultVariable from, DefaultVariable to) {
//        if (!mapping.containsKey())
//    }

    public void addLink(Variable fromVar, Variable toVar) throws SimulationCreationException {

        if (!getFromVars().contains(fromVar) || !getToVars().contains(toVar)) {
            throw new SimulationCreationException("From and to variables must correspond to the steps they connect");
        } else if (fromVar.getArity().intValue() != toVar.getArity() || !(fromVar.getDataType().equals(toVar.getDataType()))) {
            throw new SimulationCreationException("From and to variables must have same arity and datatype");

        } else {
            put((DefaultVariable) fromVar, (DefaultVariable) toVar);

        }

    }

    private void put(DefaultVariable from, DefaultVariable to) {
        VariableList list = mapping.get(from);
        if (list == null) {
            list = new VariableList();
            list.persist();
            mapping.put(from, list);

        }
        list.getVariables().add(to);
    }

    public Set<Variable> getFromVars() {
        if (parentsim==null) throw new RuntimeException("Parent roma for step mapping should never be null");
        if (fromVars == null) {
            fromVars = new HashSet<Variable>();
            if (getFromStep() == null) {
                fromVars.addAll(parentsim.getInputs());
            } else {
                for (DefaultSimulation s : getFromStep().getSimulations()) {
                    fromVars.addAll(s.getOutputs());
                }
            }
        }
        return this.fromVars;
    }

    public Set<Variable> getToVars() {
        if (parentsim==null) throw new RuntimeException("Parent roma for step mapping should never be null");
        if (toVars == null) {
            toVars = new HashSet<Variable>();
            if (getToStep() == null) {
                toVars.addAll(parentsim.getOutputs());
            } else {
                for (DefaultSimulation s : toStep.getSimulations()) {
                    toVars.addAll(s.getInputs());
                }
            }
        }
        return this.toVars;
    }


    private void setFromStep(Step from) {
        this.fromStep = from;
        fromVars = null;

    }

    private void setToStep(Step to) {
        this.toStep = to;
        toVars = null;
    }

       public CompositeServerSimulation getParentsim() {
        return this.parentsim;
    }

    public void setParentsim(CompositeServerSimulation parentsim) {
        this.parentsim = parentsim;
    }

    public Step getFromStep() {
        return this.fromStep;
    }

    public Step getToStep() {
        return this.toStep;
    }



    public Map<DefaultVariable, VariableList> getMapping() {
        return this.mapping;
    }

    public void setMapping(Map<DefaultVariable, VariableList> mapping) {
        this.mapping = mapping;
    }


     @PersistenceContext
    transient EntityManager entityManager;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

    @Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            CompositeStepMapping attached = findCompositeStepMapping(this.id);
            this.entityManager.remove(attached);
        }
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public CompositeStepMapping merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        CompositeStepMapping merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public static final EntityManager entityManager() {
        EntityManager em = new CompositeStepMapping().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countCompositeStepMappings() {
        return entityManager().createQuery("select count(o) from CompositeStepMapping o", Long.class).getSingleResult();
    }

    public static List<CompositeStepMapping> findAllCompositeStepMappings() {
        return entityManager().createQuery("select o from CompositeStepMapping o", CompositeStepMapping.class).getResultList();
    }

    public static CompositeStepMapping findCompositeStepMapping(Long id) {
        if (id == null) return null;
        return entityManager().find(CompositeStepMapping.class, id);
    }

    public static List<CompositeStepMapping> findCompositeStepMappingEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("select o from CompositeStepMapping o", CompositeStepMapping.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

     public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Parentsim: ").append(getParentsim()).append(", ");
        sb.append("FromStep: ").append(getFromStep()).append(", ");
        sb.append("ToStep: ").append(getToStep()).append(", ");
        sb.append("FromVars: ").append(getFromVars() == null ? "null" : getFromVars().size()).append(", ");
        sb.append("ToVars: ").append(getToVars() == null ? "null" : getToVars().size()).append(", ");
        sb.append("Mapping: ").append(getMapping() == null ? "null" : getMapping().size());
        return sb.toString();
    }


}
