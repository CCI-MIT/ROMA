package edu.mit.cci.roma.excel;

import edu.mit.cci.roma.util.SimulationValidationException;
import org.apache.poi.ss.util.AreaReference;

/**
 * User: jintrone
 * Date: 2/1/13
 * Time: 10:36 AM
 */
public class Validation {

    public static void validateExcelCoordinates(String cellRange) throws SimulationValidationException {
        AreaReference ref = new AreaReference(cellRange);

        int width = 1 + ref.getLastCell().getCol() - ref.getFirstCell().getCol();
        int height = 1 + ref.getLastCell().getRow() - ref.getLastCell().getRow();

        if (width == 0 || height == 0 || (width > 1 && height > 1)) {
            throw new SimulationValidationException("Cell range must be at least one in both dimensions, but not greater than one in both");
        }
    }

     public static void excelUrl(String url) throws SimulationValidationException {
        if (!url.startsWith(ExcelSimulation.EXCEL_URL)) {
            throw new SimulationValidationException("Url for built-in excel models should begin with /excel/");
        }
        //To change body of created methods use File | Settings | File Templates.
    }
}
