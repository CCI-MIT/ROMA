package edu.mit.cci.roma.util;

import edu.mit.cci.roma.api.DataType;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.Tuple;
import org.apache.log4j.Logger;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: jintrone
 * Date: 3/8/11
 * Time: 8:29 PM
 */
public class SimulationValidation {

    public static Logger log = Logger.getLogger(SimulationValidation.class);



    public static void notNull(Object o, String objectName) throws SimulationValidationException {
        if (o == null) throw new SimulationValidationException(objectName + " cannot be null");
    }

    public static void equalsArity(Variable v, int expected) throws SimulationValidationException {
        if (v == null || expected != v.getArity()) {
            log.error("Variable " + v.getName() + " should have arity " + expected + " but instead has " + v.getArity());
            throw new SimulationValidationException("Variable " + v.getName() + " should have arity " + expected + " but instead has " + v.getArity());
        }
    }

    public static void atMostArity(Variable v, int expected) throws SimulationValidationException {
        if (expected > v.getArity())
            throw new SimulationValidationException("Data is longer than specified arity of " + v.getArity());
    }



    public static void isComplete(Variable v) throws SimulationValidationException {
        if (v == null) {
            throw new SimulationValidationException("Variable is null");
        }
        if (v.getDataType() == null || v.getArity() == null ||
                (v.getDataType() == DataType.NUM && (v.getMin_() != null && v.getMax_() != null && v.getMin_() > v.getMax_())) ||
                (v.getDataType() == DataType.CAT && (v.getOptions() == null || v.getOptions().length == 0))) {
            throw new SimulationValidationException("Variable is not completely specified");
        }
    }

    public static void canSet(Tuple tuple, String[] values) throws SimulationValidationException {


        if (values.length > tuple.getVar().getArity()) {
            throw new SimulationValidationException("Cannot set tuple value with length > expected arity");
        }
    }

    public static void checkDataType(Variable var, String[] v, boolean ignoreNulls) throws SimulationValidationException {

        for (String aV : v) {
            checkDataType(var, aV, ignoreNulls);
        }
    }

    public static void checkDataType(Variable var, String s, boolean ignoreNulls) throws SimulationValidationException {
        List<String> options = var.getDataType() == DataType.CAT ? Arrays.asList(var.getOptions()) : Collections.<String>emptyList();
        if (ignoreNulls && s == null) return;
        if (var.getDataType() == DataType.NUM) {

            try {
                Double.parseDouble(s);
            } catch (NumberFormatException ex) {
                throw new SimulationValidationException("Value  " + s + " cannot be interpreted as a number");
            }
        } else if (var.getDataType() == DataType.CAT && !options.contains(s)) {
            throw new SimulationValidationException("Value " + s + " is not a known categorical option");
        }
    }
}
