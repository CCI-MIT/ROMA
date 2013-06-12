package edu.mit.cci.roma.impl;

import edu.mit.cci.roma.api.DataType;
import edu.mit.cci.roma.api.TupleStatus;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.jaxb.JaxbReference;
import edu.mit.cci.roma.util.SimulationValidationException;
import edu.mit.cci.roma.util.U;
import edu.mit.cci.roma.util.SimulationValidation;
import org.apache.log4j.Logger;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;


@XmlRootElement(name = "Tuple")
@XmlAccessorType(XmlAccessType.NONE)
public class Tuple {

    private static Logger log = Logger.getLogger(Tuple.class);

    public Tuple(Variable v) {
        setVar(v);
    }


    @XmlJavaTypeAdapter(JaxbReference.Adapter.class)
    private Variable var;


    @XmlElement(name = "Value")
    private String value_;

    private transient String[] values;

    private transient final Map<Integer, TupleStatus> statuses = new HashMap<Integer, TupleStatus>();

    public Tuple() {

    }



    public String[] getValues() {

        if (values == null) {
            if (value_ == null) return null;
            else {
                this.statuses.clear();
                values = U.unescape(value_, this.statuses, var.getDataType() == DataType.NUM ? var.getPrecision_() : null);
            }
        }
        return values;
    }

    public void setValues(String[] values) throws SimulationValidationException {
        this.statuses.clear();
        _setValues(values);
    }

    private void _setValues(String[] values) throws SimulationValidationException {
        SimulationValidation.isComplete(var);
        SimulationValidation.atMostArity(var, values.length);
        for (int i = 0; i < values.length; i++) {
            SimulationValidation.checkDataType(var, values[i], true);
            if (values[i] == null) continue;
            TupleStatus status = statuses.get(i);
            if (status != null) continue;
            U.process(var, i, values, statuses);
        }
        this.values = values;
        this.value_ = (U.escape(values, statuses));
    }

    public void setValue_(String val) {
        this.value_ = val;
        //_setValues(U.unescape(val, this.statuses, null));
    }


    public TupleStatus getStatus(int i) {
        return statuses.get(i);
    }

    public void setStatus(int i, TupleStatus status) {
        TupleStatus current = getStatus(i);
        if (current != status) {
            statuses.put(i, status);
            value_ = U.updateEscapedArray(i, value_, status);
            values[i] = null;
        }
    }


    public static void main(String[] args) {
        String[] vals = new String[]{"hi;;", "ad%%253Bd;;", "nothing;;;"};
        String encoded = U.escape(vals, null);
        System.err.println(encoded);

        vals = U.unescape(encoded, null, null);
        for (String val : vals) {
            System.err.println(val);
        }

    }


    public String getId_() {
        return "" + getId();
    }




    private Long id;


    @XmlAttribute
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Variable getVar() {
           return this.var;
       }

       public void setVar(Variable var) {
           this.var = var;
       }

       public String getValue_() {
           return this.value_;
       }


}
