package edu.mit.cci.roma.server.util;

import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.DefaultVariable;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.util.SimulationComputationException;
import edu.mit.cci.roma.util.SimulationValidation;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: jintrone
 * Date: 2/1/13
 * Time: 10:40 AM
 */
public class U {

    private static Logger log = Logger.getLogger(U.class);

    public static String getCellValueAsString(HSSFSheet worksheet, int rowCounter, int colCounter,
                                                 String defaultValue) throws SimulationException {
           SimulationValidation.notNull(worksheet, "worksheet");

           Row row = worksheet.getRow(rowCounter);
           if (row == null) {
               return defaultValue;
           }
           Cell cell = row.getCell(colCounter);
           if (cell == null) {
               return defaultValue;
           }
           // cell should be either blank,string, numeric or formula
           if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
               return defaultValue;
           } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
               return cell.getStringCellValue();
           } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
               return cell.getNumericCellValue() + "";
           } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
               if (cell.getCachedFormulaResultType() == Cell.CELL_TYPE_STRING) {
                   return cell.getStringCellValue();
               } else if (cell.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC) {
                   return cell.getNumericCellValue() + "";
               } else if (cell.getCachedFormulaResultType() == Cell.CELL_TYPE_ERROR) {
                   throw new SimulationComputationException("Error computing fomula");
               } else {
                   throw new SimulationException("invalid formula type with cached type of  "
                           + cell.getCachedFormulaResultType() + " for row of " + rowCounter + " and col of " + colCounter);
               }
           } else {
               throw new SimulationException("invalid type with type of  " + cell.getCellType() + " for rowr of "
                       + rowCounter + " and col of " + colCounter);
           }

       }

        public static Map<Variable, Tuple> convertToVarTupleMap(Map<String, String> params) throws SimulationException {
        Map<Variable, Tuple> result = new HashMap<Variable, Tuple>();


        for (Map.Entry<String, String> ent : params.entrySet()) {
            Variable v = DefaultVariable.findDefaultVariable(Long.parseLong(ent.getKey()));
            if (v == null)
                throw new SimulationException("Variable for id:" + ent.getKey() + " could not be identified");
            Tuple t = new Tuple(v);
            t.setValue_(ent.getValue());

            result.put(v, t);
        }
        return result;
    }

    /**
     *
     * @param input Presumes data formatted as a URL query parameter string with UTF-8 encoded values
     * @param candidates
     * @return A List of Tuples.  Tuples are not explicitly persisted here.
     * @throws edu.mit.cci.roma.api.SimulationException
     */
    public static List<Tuple> parseVariableMap(String input, Collection<Variable> candidates) throws SimulationException {

        Map<String,String> externallyNamed = new HashMap<String,String>();
        if (candidates !=null) {
            for (Variable v: candidates) {
                if (v.getExternalName()!=null) {
                    externallyNamed.put(v.getExternalName(),v.getId_());
                }
            }
        }

        List<Tuple> result = new ArrayList<Tuple>();
        if (input.trim().isEmpty()) {
            throw (new SimulationException("Error parsing results from string: " + input));
        }
        String[] vars = input.split(edu.mit.cci.roma.util.U.VAR_SEP);
        for (String seg : vars) {
            String[] varval = seg.split(edu.mit.cci.roma.util.U.VAR_VAL_SEP);
            String id = externallyNamed.containsKey(varval[0])?externallyNamed.get(varval[0]):varval[0];
            Long lid = null;
            try {
                lid = Long.parseLong(id);
            } catch (NumberFormatException e) {
               //do nothing
            }
            Variable v = lid!=null? DefaultVariable.findDefaultVariable(lid):null;
            if (v == null) {
                log.warn("Could not identify variable in response: " + varval[0]);
                continue;
            } else if (!(candidates.contains(v))) {
                log.info("Found variable "+v.getName()+" but not looking for it");
                continue;
            }
            Tuple t = new Tuple(v);
            t.setValue_(varval[1]);

            result.add(t);
        }
        return result;

    }

}
