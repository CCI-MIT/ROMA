package edu.mit.cci.roma.server;

import edu.mit.cci.roma.impl.DefaultScenario;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "Scenario")
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Configurable
@DiscriminatorValue(value = "CompositeScenario")
public class CompositeScenario extends DefaultServerScenario {

	private Map<Step, ScenarioList> childScenarios = new HashMap<Step, ScenarioList>();

	private int lastStep;

	public void addToStep(Step s, DefaultScenario scenario) {
		if (!childScenarios.containsKey(s)) {
			ScenarioList list = new ScenarioList();
			list.persist();
			childScenarios.put(s, list);

		}
		childScenarios.get(s).getScenarios()
				.add((DefaultServerScenario) scenario);

	}

	public void clearStep(Step s) {
		if (childScenarios.containsKey(s)) {
			childScenarios.get(s).getScenarios().clear();
		}
	}

	@ManyToMany
	@JoinTable(name = "step_scenario")
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

	public static long countCompositeScenarios() {
		return entityManager().createQuery(
				"select count(o) from CompositeScenario o", Long.class)
				.getSingleResult();
	}

	public static List<CompositeScenario> findAllCompositeScenarios() {
		return entityManager().createQuery("select o from CompositeScenario o",
				CompositeScenario.class).getResultList();
	}

	public static CompositeScenario findCompositeScenario(Long id) {
		if (id == null)
			return null;
		return entityManager().find(CompositeScenario.class, id);
	}

	public static List<CompositeScenario> findCompositeScenarioEntries(
			int firstResult, int maxResults) {
		return entityManager()
				.createQuery("select o from CompositeScenario o",
						CompositeScenario.class).setFirstResult(firstResult)
				.setMaxResults(maxResults).getResultList();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("IdAsString: ").append(getIdAsString()).append(", ");
		sb.append("Id: ").append(getId()).append(", ");
		sb.append("Version: ").append(getVersion()).append(", ");
		sb.append("Simulation: ").append(getSimulation()).append(", ");
		sb.append("Values_: ")
				.append(getValues_() == null ? "null" : getValues_().size())
				.append(", ");
		sb.append("Created: ").append(getCreated()).append(", ");
		sb.append("ChildScenarios: ")
				.append(getChildScenarios() == null ? "null"
						: getChildScenarios().size()).append(", ");
		sb.append("LastStep: ").append(getLastStep());
		return sb.toString();
	}

}
