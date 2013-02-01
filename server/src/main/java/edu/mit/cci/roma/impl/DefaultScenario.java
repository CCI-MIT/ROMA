package edu.mit.cci.roma.impl;


import edu.mit.cci.roma.api.Scenario;
import edu.mit.cci.roma.api.Simulation;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.jaxb.JaxbReference;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@XmlRootElement(name = "Scenario")
@XmlAccessorType(XmlAccessType.NONE)
public class DefaultScenario implements Scenario {

    public Tuple getVariableValue(Variable v) {
        for (Tuple t : values_) {
            if (t.getVar().equals(v)) {
                return t;
            }
        }
        return null;
    }


    @XmlJavaTypeAdapter(JaxbReference.Adapter.class)
    private Simulation simulation;


    @XmlElement(name = "Tuples")
    private Set<Tuple> values_ = new HashSet<Tuple>();

    @XmlElement(name = "Created")
    private Date created;


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @XmlElement(name = "Name")
    private String name;


    @XmlElement(name = "User")
    private String user;


    public String getIdAsString() {
        return "" + getId();
    }


    private Long id;

    @XmlAttribute(name = "Id")
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Simulation getSimulation() {
        return this.simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public Set<Tuple> getValues_() {
        return this.values_;
    }

    public void setValues_(Set<Tuple> values_) {
        this.values_ = values_;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }




}
