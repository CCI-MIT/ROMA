package edu.mit.cci.roma.java;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.Size;

import net.sf.json.JSONSerializer;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import edu.mit.cci.roma.api.Scenario;
import edu.mit.cci.roma.api.Simulation;
import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.server.DefaultServerSimulation;
import flexjson.JSONDeserializer;

@Entity
@Configurable
public class JavaSimulation {

    @PersistenceContext
    transient EntityManager entityManager;
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Version
	@Column(name = "version")
	private Integer version;

	public static final String JAVA_URL = "/java/";

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date creation;

	@ManyToOne
	private DefaultServerSimulation simulation;
	
	@Column(length=10000)
	private String configuration;
	
	private String runnerClass;
	
	transient private JavaSimulationRunner javaSimulationRunner;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public DefaultServerSimulation getSimulation() {
		return simulation;
	}

	public void setSimulation(DefaultServerSimulation simulation) {
		this.simulation = simulation;
	}


    public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public String getRunnerClass() {
		return runnerClass;
	}

	public void setRunnerClass(String runnerClass) {
		this.runnerClass = runnerClass;
	}
	

	@SuppressWarnings("unchecked")
	public Map<Variable, Object[]> run(List<Tuple> params) throws SimulationException {
        Map<Variable, Object[]> result = new HashMap<Variable, Object[]>();
        Class<JavaSimulationRunner> simulationRunnerClass = null;
        try {
			simulationRunnerClass = (Class<JavaSimulationRunner>) Class.forName(runnerClass);
			
			javaSimulationRunner = simulationRunnerClass.newInstance();
			javaSimulationRunner.init(configuration == null ? Collections.EMPTY_MAP :  new JSONDeserializer<Map>().deserialize(configuration) );
			return javaSimulationRunner.run(params, simulation.getOutputs());
			
		} catch (Exception e) {
			String msg = String.format("Can't create java simulation runner for class %s", runnerClass);
			throw new SimulationException(msg, e);
		}
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
            JavaSimulation attached = findJavaSimulation(this.id);
            this.entityManager.remove(attached);
        }
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public JavaSimulation merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        JavaSimulation merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static JavaSimulation findJavaSimulation(Long id) {
        if (id == null) return null;
        return entityManager().find(JavaSimulation.class, id);
    }


    public static final EntityManager entityManager() {
        EntityManager em = new JavaSimulation().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public void prePersistScenario(Scenario scenario) throws SimulationException {
		javaSimulationRunner.prePersistScenario(scenario);	
	}

	public Simulation getResultSimulation(Simulation defaultServerSimulation) {
		return javaSimulationRunner.getResultSimulation(defaultServerSimulation);
	}

	public void setConfiguration(Map<String, String[]> outputsToAggregate) {
		this.configuration = JSONSerializer.toJSON(outputsToAggregate).toString();
	}


	

}
