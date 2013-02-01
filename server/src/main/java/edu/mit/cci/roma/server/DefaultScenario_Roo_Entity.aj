package edu.mit.cci.roma.server;

import edu.mit.cci.roma.impl.DefaultScenario;


import javax.persistence.*;

import java.lang.Integer;
import java.lang.Long;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

privileged aspect DefaultScenario_Roo_Entity {
    
    declare @type: DefaultScenario: @Entity;




    declare @field:*  * DefaultScenario.id:@Id;
    declare @field:*  * DefaultScenario.id:@GeneratedValue(strategy=GenerationType.AUTO);
    declare @field:*  * DefaultScenario.id:@Column(name="id");

    declare @field:*  * DefaultScenario.simulation:@ManyToOne(targetEntity = DefaultServerSimulation.class);

    declare @field:*  * DefaultScenario:values_:@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER);

    declare @field:*  * DefaultScenario.created:@Temporal(TemporalType.TIMESTAMP);
    declare @field:*  * DefaultScenario.created:@DateTimeFormat(style = "S-");



    
    @PersistenceContext
    transient EntityManager DefaultScenario.entityManager;
    

    
    @Version
    @Column(name = "version")
    private Integer DefaultScenario.version;


    
    public Integer DefaultScenario.getVersion() {
        return this.version;
    }
    
    public void DefaultScenario.setVersion(Integer version) {
        this.version = version;
    }
    
    @Transactional
    public void DefaultScenario.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void DefaultScenario.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            DefaultScenario attached = DefaultScenario.findDefaultScenario(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void DefaultScenario.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public DefaultScenario DefaultScenario.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        DefaultScenario merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager DefaultScenario.entityManager() {
        EntityManager em = new DefaultScenario().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long DefaultScenario.countDefaultScenarios() {
        return entityManager().createQuery("select count(o) from DefaultScenario o", Long.class).getSingleResult();
    }
    
    public static List<DefaultScenario> DefaultScenario.findAllDefaultScenarios() {
        return entityManager().createQuery("select o from DefaultScenario o", DefaultScenario.class).getResultList();
    }
    
    public static DefaultScenario DefaultScenario.findDefaultScenario(Long id) {
        if (id == null) return null;
        return entityManager().find(DefaultScenario.class, id);
    }
    
    public static List<DefaultScenario> DefaultScenario.findDefaultScenarioEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("select o from DefaultScenario o", DefaultScenario.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
