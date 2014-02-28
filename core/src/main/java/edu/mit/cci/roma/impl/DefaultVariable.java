package edu.mit.cci.roma.impl;

import edu.mit.cci.roma.api.DataType;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.jaxb.JaxbReference;
import edu.mit.cci.roma.util.U;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * User: jintrone
 * Date: 2/9/11
 * Time: 2:00 PM
 */
@XmlRootElement(name = "Variable")
@XmlAccessorType(XmlAccessType.NONE)
public class DefaultVariable implements Variable {

    public DefaultVariable() {

    }

    public DefaultVariable(String name, String description, Integer arity, Integer precision, Double min, Double max) {
        setName(name);
        setDescription(description);
        setArity(arity);
        setPrecision_(precision);
        setMax_(max);
        setMin_(min);
        setDataType(DataType.NUM);
    }

    public DefaultVariable(String name, String description, Integer arity, String[] options) {
        setName(name);
        setDescription(description);
        setArity(arity);
        setOptions(options);
        setDataType(DataType.CAT);
    }

    public DefaultVariable(String name, String description, Integer arity) {
        setName(name);
        setDescription(description);
        setArity(arity);
        setDataType(DataType.TXT);
    }

    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "Description")
    private String description;


    @XmlElement(name = "Arity")
    private Integer arity = 1;



    @XmlElement(name = "DataType")
    private DataType dataType;

    @XmlElement(name = "Precision")
    private Integer precision_;

    @XmlElement(name = "Max")
    private Double max_;

    @XmlElement(name = "Min")
    private Double min_;


    @XmlElement(name = "ExternalName")
    private String externalName;

    @XmlElement(name = "OptionsRaw")
    private String _optionsRaw;


    @XmlElement(name = "Options")
    private String[] options;


    @XmlJavaTypeAdapter(JaxbReference.Adapter.class)
    private Variable indexingVariable;

    @Override
    public String[] getOptions() {
        if (_optionsRaw == null) {
            return null;
        } else if (options == null) {
            options = U.unescape(_optionsRaw, null, null);
        }
        return options;
    }

    @Override
    public void setOptions(String[] options) {
        this.options = options;
        _optionsRaw = U.escape(options, null);
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    @XmlElement(name = "Units")
    private String units;

    @Override
    public String getLabels() {
        return labels;
    }

    @Override
    public void setLabels(String labels) {
        this.labels = labels;
    }

    @XmlElement
    private String labels;

    @XmlElement(name = "Defaults")
    private String defaultValue;

    @Override
    public String getId_() {
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

     public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getArity() {
        return this.arity;
    }

    public void setArity(Integer arity) {
        this.arity = arity;
    }

    public DataType getDataType() {
        return this.dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Integer getPrecision_() {
        return this.precision_;
    }

    public void setPrecision_(Integer precision_) {
        this.precision_ = precision_;
    }

    public Double getMax_() {
        return this.max_;
    }

    public void setMax_(Double max_) {
        this.max_ = max_;
    }

    public Double getMin_() {
        return this.min_;
    }

    public void setMin_(Double min_) {
        this.min_ = min_;
    }

    public String getExternalName() {
        return this.externalName;
    }

    public void setExternalName(String externalName) {
        this.externalName = externalName;
    }

    public String get_optionsRaw() {
        return this._optionsRaw;
    }

    public void set_optionsRaw(String _optionsRaw) {
        this._optionsRaw = _optionsRaw;
    }

    public Variable getIndexingVariable() {
        return this.indexingVariable;
    }

    public void setIndexingVariable(Variable indexingVariable) {
        this.indexingVariable = indexingVariable;
    }

    public String getDefaultValue() {
    	return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
    	this.defaultValue = defaultValue;
    }

     public String toString() {
        StringBuilder sb = new StringBuilder();
        // sb.append("Options: ").append(java.util.Arrays.toString(getOptions())).append(", ");
        sb.append("Id_: ").append(getId_()).append(", ");
        sb.append("Name: ").append(getName()).append(", ");

        return sb.toString();
    }

}
