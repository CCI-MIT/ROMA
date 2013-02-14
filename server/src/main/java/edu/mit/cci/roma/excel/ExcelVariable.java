package edu.mit.cci.roma.excel;

import edu.mit.cci.roma.impl.DefaultVariable;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Version;
import java.util.List;

@Entity
@Configurable
public class ExcelVariable {

    @ManyToOne
    private ExcelSimulation excelSimulation;

    private String worksheetName;

    private String cellRange;

    private String rewriteCellRange;

    @ManyToOne
    private DefaultVariable simulationVariable;


    public ExcelVariable() {
    }

    public ExcelVariable(ExcelSimulation sim, DefaultVariable var, String worksheet, String cellrange) {
        super();
        setExcelSimulation(sim);
        setSimulationVariable(var);
        setWorksheetName(worksheet);
        setCellRange(cellrange);

    }

    public ExcelVariable(ExcelSimulation sim, DefaultVariable var, String worksheet, String cellrange, String rewriteCellRange) {
        super();
        setExcelSimulation(sim);
        setSimulationVariable(var);
        setWorksheetName(worksheet);
        setCellRange(cellrange);
        setRewriteCellRange(rewriteCellRange);
    }

public ExcelSimulation getExcelSimulation() {
        return this.excelSimulation;
    }

    public void setExcelSimulation(ExcelSimulation excelSimulation) {
        this.excelSimulation = excelSimulation;
    }

    public String getWorksheetName() {
        return this.worksheetName;
    }

    public void setWorksheetName(String worksheetName) {
        this.worksheetName = worksheetName;
    }

    public String getCellRange() {
        return this.cellRange;
    }

    public void setCellRange(String cellRange) {
        this.cellRange = cellRange;
    }

     public String getRewriteCellRange() {
        return this.rewriteCellRange;
    }

    public void setRewriteCellRange(String cellRange) {
        this.rewriteCellRange = cellRange;
    }

    public DefaultVariable getSimulationVariable() {
        return this.simulationVariable;
    }

    public void setSimulationVariable(DefaultVariable simulationVariable) {
        this.simulationVariable = simulationVariable;
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
            ExcelVariable attached = findExcelVariable(this.id);
            this.entityManager.remove(attached);
        }
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public ExcelVariable merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        ExcelVariable merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public static final EntityManager entityManager() {
        EntityManager em = new ExcelVariable().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countExcelVariables() {
        return entityManager().createQuery("select count(o) from ExcelVariable o", Long.class).getSingleResult();
    }

    public static List<ExcelVariable> findAllExcelVariables() {
        return entityManager().createQuery("select o from ExcelVariable o", ExcelVariable.class).getResultList();
    }

    public static ExcelVariable findExcelVariable(Long id) {
        if (id == null) return null;
        return entityManager().find(ExcelVariable.class, id);
    }

    public static List<ExcelVariable> findExcelVariableEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("select o from ExcelVariable o", ExcelVariable.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

     public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ExcelSimulation: ").append(getExcelSimulation()).append(", ");
        sb.append("WorksheetName: ").append(getWorksheetName()).append(", ");
        sb.append("CellRange: ").append(getCellRange()).append(", ");
        sb.append("SimulationVariable: ").append(getSimulationVariable());
        return sb.toString();
    }
}
