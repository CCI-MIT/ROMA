package edu.mit.cci.simulation.model;

import edu.mit.cci.simulation.api.DataType;
import edu.mit.cci.simulation.impl.DefaultScenario;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@RooJavaBean
@RooToString
@RooEntity
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
}
