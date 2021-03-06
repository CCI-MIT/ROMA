package edu.mit.cci.roma.server;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import edu.mit.cci.roma.api.TupleStatus;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.util.SimulationValidationException;

/**
 * User: jintrone
 * Date: 2/14/13
 * Time: 1:06 PM
 */

@Entity(name = "Tuple")
@Configurable
@Table(name = "tuple")
public class ServerTuple extends Tuple {


    @PersistenceContext
    transient EntityManager entityManager;


    @NotNull
    @ManyToOne(targetEntity = DefaultServerVariable.class, fetch = FetchType.EAGER)
    public Variable getVar() {
        return super.getVar();
    }

    @Column(columnDefinition = "LONGTEXT")
    public String getValue_() {
        return super.getValue_();
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return super.getId();
    }


    public ServerTuple(Variable v) {
        super(v);
    }

    public ServerTuple() {
        super();
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
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

    @Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Tuple attached = findTuple(this.getId());
            this.entityManager.remove(attached);
        }
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public ServerTuple merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        ServerTuple merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public static final EntityManager entityManager() {
        EntityManager em = new ServerTuple().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static ServerTuple copy(Tuple t) {
        ServerTuple result = new ServerTuple();
        result.setVar(t.getVar());

        result.setValue_(t.getValue_());

        result.persist();
        return result;
    }

    public static long countTuples() {
        return entityManager().createQuery("select count(o) from Tuple o", Long.class).getSingleResult();
    }

    public static List<ServerTuple> findAllTuples() {
        return entityManager().createQuery("select o from Tuple o", ServerTuple.class).getResultList();
    }

    public static ServerTuple findTuple(Long id) {
        if (id == null) return null;
        return entityManager().find(ServerTuple.class, id);
    }

    public static List<ServerTuple> findTupleEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("select o from Tuple o", ServerTuple.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Values: ").append(java.util.Arrays.toString(getValues())).append(", ");
        sb.append("Id_: ").append(getId_()).append(", ");
        sb.append("Var: ").append(getVar()).append(", ");
        sb.append("Value_: ").append(getValue_());
        return sb.toString();
    }
}
