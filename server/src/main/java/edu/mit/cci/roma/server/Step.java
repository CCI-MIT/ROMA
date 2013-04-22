package edu.mit.cci.roma.server;


import com.sun.tools.javac.resources.version;
import edu.mit.cci.roma.impl.DefaultSimulation;
import org.springframework.beans.factory.annotation.Configurable;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: jintrone
 * Date: 2/14/11
 * Time: 1:21 PM
 */
@Entity
@Configurable
public class Step {

    private Integer order_ = 0;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<DefaultServerSimulation> simulations = new HashSet<DefaultServerSimulation>();

    public Step() {
    }

    public Step(Integer order, DefaultServerSimulation... sims) {
        setOrder_(order);
        for (DefaultServerSimulation sim : sims) {
            getSimulations().add(sim);
        }
    }

     public Integer getOrder_() {
        return this.order_;
    }

    public void setOrder_(Integer order_) {
        this.order_ = order_;
    }

    public Set<DefaultServerSimulation> getSimulations() {
        return this.simulations;
    }

    public void setSimulations(Set<DefaultServerSimulation> simulations) {
        this.simulations = simulations;
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
            Step attached = findStep(this.id);
            this.entityManager.remove(attached);
        }
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public Step merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Step merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public static final EntityManager entityManager() {
        EntityManager em = new Step().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countSteps() {
        return entityManager().createQuery("select count(o) from Step o", Long.class).getSingleResult();
    }

    public static List<Step> findAllSteps() {
        return entityManager().createQuery("select o from Step o", Step.class).getResultList();
    }

    public static Step findStep(Long id) {
        if (id == null) return null;
        return entityManager().find(Step.class, id);
    }

    public static List<Step> findStepEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("select o from Step o", Step.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order_: ").append(getOrder_()).append(", ");
        sb.append("Simulations: ").append(getSimulations() == null ? "null" : getSimulations().size());
        return sb.toString();
    }
}
