package edu.mit.cci.roma.model;

import edu.mit.cci.roma.impl.DefaultScenario;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.HashMap;
import java.util.Map;


@XmlRootElement(name="Scenario")
@XmlAccessorType(XmlAccessType.NONE)
public class CompositeScenario extends DefaultScenario {

   private Map<Step,ScenarioList> childScenarios = new HashMap<Step,ScenarioList>();

    private int lastStep;


    public void addToStep(Step s, DefaultScenario scenario) {
        if (!childScenarios.containsKey(s)) {
            ScenarioList list = new ScenarioList();
            list.persist();
            childScenarios.put(s,list);

        }
            childScenarios.get(s).getScenarios().add(scenario);

    }

    public void clearStep(Step s) {
        if (childScenarios.containsKey(s)) {
            childScenarios.get(s).getScenarios().clear();
        }
    }

    public Map<Step, ScenarioList> getChildScenarios() {
           return this.childScenarios;
       }

       public void setChildScenarios(Map<Step, ScenarioList> childScenarios) {
           this.childScenarios = childScenarios;
       }

       public int getLastStep() {
           return this.lastStep;
       }

       public void setLastStep(int lastStep) {
           this.lastStep = lastStep;
       }

     public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IdAsString: ").append(getIdAsString()).append(", ");
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("Version: ").append(getVersion()).append(", ");
        sb.append("Simulation: ").append(getSimulation()).append(", ");
        sb.append("Values_: ").append(getValues_() == null ? "null" : getValues_().size()).append(", ");
        sb.append("Created: ").append(getCreated()).append(", ");
        sb.append("ChildScenarios: ").append(getChildScenarios() == null ? "null" : getChildScenarios().size()).append(", ");
        sb.append("LastStep: ").append(getLastStep());
        return sb.toString();
    }


}
