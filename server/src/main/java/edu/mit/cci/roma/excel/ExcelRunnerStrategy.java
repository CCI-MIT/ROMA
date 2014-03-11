package edu.mit.cci.roma.excel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.AreaReference;

import edu.mit.cci.roma.api.DataType;
import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.api.TupleStatus;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.server.DefaultServerSimulation;
import edu.mit.cci.roma.server.RunStrategy;
import edu.mit.cci.roma.util.SimulationComputationException;
import edu.mit.cci.roma.util.SimulationValidation;
import edu.mit.cci.roma.util.U;

/**
 * User: jintrone
 * Date: 3/8/11
 * Time: 10:30 AM
 */
public class ExcelRunnerStrategy implements RunStrategy {

    private DefaultServerSimulation sim;
    private static Logger log = Logger.getLogger(ExcelRunnerStrategy.class);


    public ExcelRunnerStrategy(DefaultServerSimulation sim) {
        this.sim = sim;
        sim.setRunStrategy(this);
    }

    public String run(String url, List<Tuple> params) throws SimulationException {
        ExcelValidation.excelUrl(url);

        Long id = Long.parseLong(url.substring(ExcelSimulation.EXCEL_URL.length()));
        ExcelSimulation esim = ExcelSimulation.findExcelSimulation(id);
        if (esim == null) {
            throw new SimulationException("Could not identify excel model");
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(esim.getFile());
        Workbook workbook = null;

        try {
            workbook = WorkbookFactory.create(inputStream);
        } catch (IOException e) {
            throw new SimulationException("Error loading excel workbook", e);
        } catch (InvalidFormatException e) {
            throw new SimulationException("Error loading excel workbook", e);
		}

        if (workbook == null) {
            throw new SimulationException("Workbook could not be found for roma " + esim.getSimulation().getName());
        }

        Map<Variable,Tuple> vmap = new HashMap<Variable,Tuple>();
        for (Tuple t:params) {
           vmap.put(t.getVar(),t);
        }

        for (ExcelVariable ev : esim.getInputs()) {

            String paramId = ev.getSimulationVariable().getExternalName();
            Tuple input = vmap.get(ev.getSimulationVariable());
            if (input == null) {
              log.warn("Missing input variable: " + ev.getSimulationVariable());
            }
            writeInput(ev, input.getValues(), workbook);
        }

        runForumlas(workbook);
        Map<Variable, Object[]> result = new HashMap<Variable, Object[]>();
        for (ExcelVariable ev : esim.getOutputs()) {
            result.put(ev.getSimulationVariable(), readOutput(ev, workbook, ev.getCellRange()));
        }
        for (ExcelVariable ev:esim.getInputs()) {
            if (ev.getRewriteCellRange()!=null) {
               result.put(ev.getSimulationVariable(),readOutput(ev,workbook,ev.getRewriteCellRange()));
            }
        }

        return U.createStringRepresentation(result);
    }

    public void writeInput(ExcelVariable v, String[] data, Workbook workbook) throws SimulationException {
        Sheet sheet = workbook.getSheet(v.getWorksheetName());
        SimulationValidation.equalsArity(v.getSimulationVariable(), data.length);
        SimulationValidation.notNull(sheet, "Worksheet");
        ExcelValidation.validateExcelCoordinates(v.getCellRange());

        AreaReference area = new AreaReference(v.getCellRange());

        int height = 1+area.getLastCell().getRow() - area.getFirstCell().getRow();
        int width = 1+area.getLastCell().getCol() - area.getFirstCell().getCol();

        int startrow = area.getFirstCell().getRow();
        int startcol = area.getFirstCell().getCol();

        int dx = width == 1 ? 0 : 1;
        int dy = height == 1 ? 0 : 1;

        DataType type = v.getSimulationVariable().getDataType();

        for (int i = 0; i < Math.max(width, height); i++) {
            Cell cell = sheet.getRow(startrow + (dy * i)).getCell(startcol + (i * dx));

            switch (type) {

                case NUM: {
                    Double d = null;
                    if (data[i]==null) {
                        cell.setCellValue(data[i]);
                    } else cell.setCellValue(Double.parseDouble(data[i]));
                    break;
                }

                case TXT: {
                    cell.setCellValue(data[i]);
                    break;
                }

                case CAT: {
                	try {
                		Double d = Double.parseDouble(data[i]);
                		if (d != null) {
                			cell.setCellValue(d);
                		}
                	}
                	catch (NumberFormatException e) {
                		cell.setCellValue(data[i]);
                	}
                    break;
                }
            }
        }


    }

    public void runForumlas(Workbook workbook) throws SimulationException {
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            for (Row r : sheet) {
                for (Cell c : r) {
                    if (c.getCellType() == Cell.CELL_TYPE_FORMULA) {
                    	try {
                            evaluator.evaluateFormulaCell(c);
                        } catch (Exception e) {
                            throw new SimulationException(e);
                        }
                    }
                }
            }

        }
    }

    public Object[] readOutput(ExcelVariable ev, Workbook workbook, String cellRange) throws SimulationException {
        Sheet sheet = workbook.getSheet(ev.getWorksheetName());
        SimulationValidation.notNull(sheet, "Worksheet");
        ExcelValidation.validateExcelCoordinates(cellRange);

        AreaReference area = new AreaReference(cellRange);

        int  height= 1 + area.getLastCell().getRow() - area.getFirstCell().getRow();
        int width = 1 + area.getLastCell().getCol() - area.getFirstCell().getCol();

        int startrow = area.getFirstCell().getRow();
        int startcol = area.getFirstCell().getCol();

        int dx = width == 1 ? 0 : 1;
        int dy = height == 1 ? 0 : 1;

        SimulationValidation.equalsArity(ev.getSimulationVariable(), Math.max(width, height));
        Object[] result = new Object[ev.getSimulationVariable().getArity()];
        for (int i = 0; i < Math.max(width, height); i++) {
            try {
                result[i] = edu.mit.cci.roma.server.util.U.getCellValueAsString(sheet, startrow + (dy * i), startcol + (i * dx), null);
                TupleStatus status = TupleStatus.decode((String)result[i]);
                if (status!=null) {
                    result[i] = status;
                }
            } catch (SimulationComputationException ex) {

                result[i] = TupleStatus.ERR_CALC;
            }
        }
        return result;

    }




}



