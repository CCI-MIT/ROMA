package edu.mit.cci.roma.excel;

import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.server.DefaultServerSimulation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Configurable
public class ExcelSimulation {


     public static final String EXCEL_URL = "/excel/";

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "S-")
    private Date creation;

    @ManyToOne
    private DefaultServerSimulation simulation;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<ExcelVariable> inputs = new HashSet<ExcelVariable>();

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<ExcelVariable> outputs = new HashSet<ExcelVariable>();

    @Column(columnDefinition = "LONGBLOB")
    private byte[] file;



    public ExcelSimulation() {

    }

    public Date getCreation() {
        return this.creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public DefaultSimulation getSimulation() {
        return this.simulation;
    }

    public void setSimulation(DefaultServerSimulation simulation) {
        this.simulation = simulation;
    }

    public Set<ExcelVariable> getInputs() {
        return this.inputs;
    }

    public void setInputs(Set<ExcelVariable> inputs) {
        this.inputs = inputs;
    }

    public Set<ExcelVariable> getOutputs() {
        return this.outputs;
    }

    public void setOutputs(Set<ExcelVariable> outputs) {
        this.outputs = outputs;
    }

    public byte[] getFile() {
        return this.file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public ExcelSimulation(DefaultServerSimulation sim, File f) throws IOException {
        this.setSimulation(sim);
        InputStream is = new FileInputStream(f);
        setFile(IOUtils.toByteArray(is));
        setCreation(new Date());
        is.close();
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
            ExcelSimulation attached = findExcelSimulation(this.id);
            this.entityManager.remove(attached);
        }
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public ExcelSimulation merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        ExcelSimulation merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public static final EntityManager entityManager() {
        EntityManager em = new ExcelSimulation().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countExcelSimulations() {
        return entityManager().createQuery("select count(o) from ExcelSimulation o", Long.class).getSingleResult();
    }

    public static List<ExcelSimulation> findAllExcelSimulations() {
        return entityManager().createQuery("select o from ExcelSimulation o", ExcelSimulation.class).getResultList();
    }

    public static ExcelSimulation findExcelSimulation(Long id) {
        if (id == null) return null;
        return entityManager().find(ExcelSimulation.class, id);
    }

    public static List<ExcelSimulation> findExcelSimulationEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("select o from ExcelSimulation o", ExcelSimulation.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

      public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Creation: ").append(getCreation()).append(", ");
        sb.append("Simulation: ").append(getSimulation()).append(", ");
        sb.append("Inputs: ").append(getInputs() == null ? "null" : getInputs().size()).append(", ");
        sb.append("Outputs: ").append(getOutputs() == null ? "null" : getOutputs().size()).append(", ");
        sb.append("File: ").append(java.util.Arrays.toString(getFile()));
        return sb.toString();
    }


}
