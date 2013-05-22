package edu.mit.cci.roma.server;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import edu.mit.cci.roma.api.Simulation;
import edu.mit.cci.roma.impl.DefaultScenario;

/**
 * User: jintrone
 * Date: 2/13/13
 * Time: 3:10 PM
 */

@Entity(name="DefaultScenario")
@Configurable
@Table(name="default_scenario")
@DiscriminatorValue(value = "DefaultScenario")
public class DefaultServerScenario extends DefaultScenario {




    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id")
    public Long getId() {
        return super.getId();
    }


    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "S-")
    public Date getCreated() {
       return super.getCreated();
    }

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity=ServerTuple.class)
    public Set<edu.mit.cci.roma.impl.Tuple> getValues_() {
        return super.getValues_();
    }

    @ManyToOne(targetEntity = DefaultServerSimulation.class)
    public Simulation getSimulation() {
      return super.getSimulation();
    }


    @PersistenceContext
    transient EntityManager entityManager;



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
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

    @Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            DefaultScenario attached = findDefaultScenario(this.getId());
            this.entityManager.remove(attached);
        }
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public DefaultScenario merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        DefaultScenario merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public static final EntityManager entityManager() {
        EntityManager em = new DefaultServerScenario().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countDefaultScenarios() {
        return entityManager().createQuery("select count(o) from DefaultScenario o", Long.class).getSingleResult();
    }

    public static List<DefaultServerScenario> findAllDefaultScenarios() {
        return entityManager().createQuery("select o from DefaultScenario o", DefaultServerScenario.class).getResultList();
    }

    public static DefaultServerScenario findDefaultScenario(Long id) {
        if (id == null) return null;
        return entityManager().find(DefaultServerScenario.class, id);
    }

    public static List<DefaultServerScenario> findDefaultScenarioEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("select o from DefaultScenario o", DefaultServerScenario.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IdAsString: ").append(getIdAsString()).append(", ");
        sb.append("Simulation: ").append(getSimulation()).append(", ");
        sb.append("Values_: ").append(getValues_() == null ? "null" : getValues_().size()).append(", ");
        sb.append("Created: ").append(getCreated());
        return sb.toString();
    }

}
