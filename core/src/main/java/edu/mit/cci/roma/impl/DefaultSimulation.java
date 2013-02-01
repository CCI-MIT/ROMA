package edu.mit.cci.roma.impl;


import edu.mit.cci.roma.api.Scenario;
import edu.mit.cci.roma.api.Simulation;
import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.jaxb.JaxbCollection;
import org.apache.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;


import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * User: jintrone
 * Date: 2/10/11
 * Time: 3:17 PM
 */


@XmlRootElement(name = "Simulation")
@XmlAccessorType(XmlAccessType.NONE)
public class DefaultSimulation implements Simulation {

    private static Logger log = Logger.getLogger(DefaultSimulation.class);


    @XmlAttribute(name = "Id")
    protected Long id;


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @DateTimeFormat(style = "S-")
    @XmlElement(name = "Creation")
    protected Date created;

    @NotNull
    protected Long simulationVersion;

    @XmlElement(name = "Description")
    protected String description;

    @XmlElement(name = "Name")
    protected String name;

    @XmlElement(name = "Url")
    protected String url;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name = "Type")
    protected String type;


    @XmlJavaTypeAdapter(JaxbCollection.Adapter.class)
    protected final Set<Variable> inputs = new HashSet<Variable>();


    @XmlJavaTypeAdapter(JaxbCollection.Adapter.class)
    protected final Set<Variable> outputs = new HashSet<Variable>();


    public Scenario run(List<Tuple> siminputs) throws SimulationException {

        throw new UnsupportedOperationException("Simulation is not runnable.");
    }

    @Override
    public void setInputs(Set<Variable> i) {
        this.inputs.clear();
        if (i != null) {
            inputs.addAll(i);
        }
    }

    @Override
    public void setOutputs(Set<Variable> o) {
        this.outputs.clear();
        if (o != null) {
            outputs.addAll(o);
        }
    }

    public Set<Variable> getInputs() {
        return this.inputs;
    }

    public Set<Variable> getOutputs() {
        return this.outputs;
    }

    @Override
    public String getIdAsString() {
        return "" + getId();
    }


    public static Map<String, String> parseTypes(Simulation sim) {
        if (sim.getType() == null) return Collections.emptyMap();
        Map<String, String> result = new HashMap<String, String>();
        for (String type : sim.getType().split(";")) {
            String[] kv = type.split("=");
            if (kv.length > 1) {
                result.put(kv[0], kv[1]);
            }
        }
        return result;
    }

    public DefaultVariable findVariableWithExternalName(String name, boolean input) {
        Set<Variable> vs = input ? getInputs() : getOutputs();
        for (Variable v : vs) {
            if (name.equals(v.getExternalName())) return (DefaultVariable) v;

        }
        return null;

    }


    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getSimulationVersion() {
        return this.simulationVersion;
    }

    public void setSimulationVersion(Long simulationVersion) {
        this.simulationVersion = simulationVersion;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IdAsString: ").append(getIdAsString()).append(", ");
        sb.append("Created: ").append(getCreated()).append(", ");
        sb.append("SimulationVersion: ").append(getSimulationVersion()).append(", ");
        sb.append("Description: ").append(getDescription()).append(", ");
        sb.append("Name: ").append(getName()).append(", ");
        sb.append("Url: ").append(getUrl()).append(", ");
        sb.append("Inputs: ").append(getInputs()).append(", ");
        sb.append("Outputs: ").append(getOutputs());
        return sb.toString();
    }


}
