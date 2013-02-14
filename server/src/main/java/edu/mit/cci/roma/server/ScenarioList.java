package edu.mit.cci.roma.server;

import com.sun.tools.javac.resources.version;
import edu.mit.cci.roma.api.DataType;
import edu.mit.cci.roma.impl.DefaultScenario;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configurable
@Entity
public class ScenarioList {

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<DefaultScenario> scenarios = new HashSet<DefaultScenario>();

    @ElementCollection
    private Set<DataType> testField = new HashSet<DataType>();

     public Set<DefaultScenario> getScenarios() {
        return this.scenarios;
    }

    public void setScenarios(Set<DefaultScenario> scenarios) {
        this.scenarios = scenarios;
    }

    public Set<DataType> getTestField() {
        return this.testField;
    }

    public void setTestField(Set<DataType> testField) {
        this.testField = testField;
    }

     public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Scenarios: ").append(getScenarios() == null ? "null" : getScenarios().size()).append(", ");
        sb.append("TestField: ").append(getTestField() == null ? "null" : getTestField().size());
        return sb.toString();
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
            ScenarioList attached = findScenarioList(this.id);
            this.entityManager.remove(attached);
        }
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public ScenarioList merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        ScenarioList merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public static final EntityManager entityManager() {
        EntityManager em = new ScenarioList().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countScenarioLists() {
        return entityManager().createQuery("select count(o) from ScenarioList o", Long.class).getSingleResult();
    }

    public static List<ScenarioList> findAllScenarioLists() {
        return entityManager().createQuery("select o from ScenarioList o", ScenarioList.class).getResultList();
    }

    public static ScenarioList findScenarioList(Long id) {
        if (id == null) return null;
        return entityManager().find(ScenarioList.class, id);
    }

    public static List<ScenarioList> findScenarioListEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("select o from ScenarioList o", ScenarioList.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }


}
