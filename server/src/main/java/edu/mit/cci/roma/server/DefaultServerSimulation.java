package edu.mit.cci.roma.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;
import org.hibernate.annotations.AccessType;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import edu.mit.cci.roma.api.Scenario;
import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.excel.ExcelRunnerStrategy;
import edu.mit.cci.roma.excel.ExcelSimulation;
import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.java.JavaRunnerStrategy;
import edu.mit.cci.roma.java.JavaSimulation;
import edu.mit.cci.roma.util.SimulationValidationException;

/**
 * User: jintrone Date: 1/29/13 Time: 10:40 AM
 */

@Entity(name = "DefaultSimulation")
@AccessType(value = "property")
@Configurable
@Table(name = "default_simulation")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "DefaultSimulation")
@DiscriminatorColumn(name = "dtype")
@XmlRootElement(name = "Simulation")
public class DefaultServerSimulation extends DefaultSimulation {

    @Column(columnDefinition = "LONGTEXT")
    public String getType() {
        return super.getType();
    }

    @Override
    public String getName() {
        return super.getName();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public String getUrl() {
        return super.getUrl();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Long getSimulationVersion() {
        return super.getSimulationVersion();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Column(columnDefinition = "LONGTEXT")
    public String getDescription() {
        return super.getDescription();
    }


    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreated() {
        return super.getCreated();
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return super.getId();
    }

    private static Logger log = Logger.getLogger(DefaultServerSimulation.class);

    @Transient
    private transient RunStrategy runStrategy = new RunStrategy.Post();

    @PersistenceContext
    transient EntityManager entityManager;

//    Set<Variable> _outputs = new HashSet<Variable>();
//
//    Set<Variable> _inputs = new HashSet<Variable>();


    @ManyToMany(cascade = CascadeType.ALL, targetEntity = DefaultServerVariable.class)
    @JoinTable(name = "default_simulation_outputs",
            joinColumns = {@JoinColumn(name = "default_simulation")},
            inverseJoinColumns = {@JoinColumn(name = "outputs")})
    public Set<Variable> getOutputs() {
        return outputs;
    }





    @ManyToMany(cascade = CascadeType.ALL, targetEntity = DefaultServerVariable.class)
    @JoinTable(name = "default_simulation_inputs",
            joinColumns = {@JoinColumn(name = "default_simulation")},
            inverseJoinColumns = {@JoinColumn(name = "inputs")})
    public Set<Variable> getInputs() {
        return inputs;
    }


    public Scenario run(List<Tuple> siminputs) throws SimulationException {

        DefaultServerScenario result = new DefaultServerScenario();
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
        result.setCreated(new Date());
        
        // allow post calculation modifications
        getRunStrategy().prePersistScenario(result);
        
        result.persist();
        return result;

    }

    public void setRunStrategy(RunStrategy r) {
        this.runStrategy = r;
    }

    @Transient
    public RunStrategy getRunStrategy() throws SimulationValidationException {
        if (getUrl() != null) {
        	if (getUrl().startsWith(ExcelSimulation.EXCEL_URL) && !(runStrategy instanceof ExcelRunnerStrategy)) {
        		new ExcelRunnerStrategy(this);
        	}
        	else if (getUrl().startsWith(JavaSimulation.JAVA_URL) && ! (runStrategy instanceof JavaRunnerStrategy)) {
        		new JavaRunnerStrategy(this);
        	}
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
    public Set<Tuple> runRaw(Collection<Tuple> siminputs)
            throws SimulationException, SimulationException {
        Set<Variable> mine = new HashSet<Variable>(getInputs());
        Set<Tuple> result = new HashSet<Tuple>();

        List<Tuple> selectedinputs = new ArrayList<Tuple>();
        for (Tuple t : siminputs) {
            if (mine.remove(t.getVar())) {
                selectedinputs.add(t);
            }
        }
        if (!mine.isEmpty()) {
            throw new SimulationException(String.format("Missing input variables: [%s], got: [%s]", mine, siminputs));
        }
        String response = null;
        response = getRunStrategy().run(url, selectedinputs);

        result.addAll(edu.mit.cci.roma.server.util.U.parseVariableMap(response,
        		getRunStrategy().getResultSimulation(this).getOutputs()));
        result.addAll(edu.mit.cci.roma.server.util.U.parseVariableMap(response,
        		getRunStrategy().getResultSimulation(this).getInputs()));

        return result;
    }

    private Integer version;

    @Version
    @Column(name = "version")
    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Transactional
    public void persist() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

    @Transactional
    public void remove() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            DefaultServerSimulation attached = DefaultServerSimulation
                    .findDefaultServerSimulation(this.id);
            this.entityManager.remove(attached);
        }
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public DefaultServerSimulation merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        DefaultServerSimulation merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public static final EntityManager entityManager() {
        EntityManager em = new DefaultServerSimulation().entityManager;
        if (em == null)
            throw new IllegalStateException(
                    "Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countDefaultServerSimulations() {
        return entityManager().createQuery(
                "select count(o) from DefaultSimulation o", Long.class)
                .getSingleResult();
    }


    public static List<DefaultServerSimulation> findAllDefaultServerSimulations() {

        List<DefaultServerSimulation> result  =  entityManager().createQuery(
                "select o from DefaultSimulation o",
                DefaultServerSimulation.class).getResultList();
       // entityManager().flush();
        return result;
    }

    public static DefaultServerSimulation findDefaultServerSimulation(Long id) {
        if (id == null)
            return null;
        return entityManager().find(DefaultServerSimulation.class, id);
    }

    public static List<DefaultServerSimulation> findDefaultServerSimulationEntries(
            int firstResult, int maxResults) {
        return entityManager()
                .createQuery("select o from DefaultSimulation o",
                        DefaultServerSimulation.class)
                .setFirstResult(firstResult).setMaxResults(maxResults)
                .getResultList();
    }

}
