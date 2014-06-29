package edu.mit.cci.roma.pangaea.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vensim.Vensim;

import edu.mit.cci.roma.pangaea.core.model.ModelInfo;
import edu.mit.cci.roma.pangaea.corenew.PangaeaPropsUtils;
import edu.mit.cci.roma.pangaea.corenew.VensimModelDefinition;
import edu.mit.cci.testing.TestingUtils;
import edu.mit.cci.testing.TestingUtilsException;

public class EnroadsTest2 {
	private static VensimHelper vensimHelper;

	public static final String DLL_LIBNAME_PARAM = "vensim_lib_name";
	public static final String MODEL_PATH_PARAM = "vensim_model_path";
	public static double ALLOWED_DIFFERENCE_IN_PERCENTS = 0.1;
	private static final Logger _log = Logger.getLogger(EnRoadsTest.class);

	@BeforeClass
	public static void loadProperties() throws TestingUtilsException,
			VensimException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, PangaeaException {
		/*TestingUtils.loadPropertiesToSystem(EnRoadsTest.class.getClassLoader()
				.getResource("test.properties").getFile());
				*/
		Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
		fieldSysPath.setAccessible(true);
		fieldSysPath.set(null, null);

		String libName = PangaeaPropsUtils.getVensimLibName();
		VensimModelDefinition modelDef = PangaeaPropsUtils.getModelForName("enroads");
		
		

		System.out.println(libName + "\t" + modelDef.getPath());
		System.out.println(System.getenv());
		vensimHelper = new VensimHelper(libName, modelDef.getPath());
		
		vensimHelper.run();
		
		
	}

	@Test
	public void variablesTest() throws VensimException, PangaeaException,
			IOException {
		
		boolean setInputs = true;
		FileWriter fw = new FileWriter("/tmp/enroads_vars.txt");

		fw.append("#### Variable names ####\n");
		for (String varName : vensimHelper.getVariables()) {
			fw.append(varName + "\n");
		}

		fw.flush();
		fw.close();

		fw = new FileWriter("/tmp/enroads_vars_def.txt");
		fw.append("#### Variable definitions ####\n");
		ModelInfo modelInfo = new ModelInfo(vensimHelper);
		System.out.println("inputs: " + modelInfo.getInputs().size());
		for (String varName : vensimHelper.getVariables()) {
			fw.append(vensimHelper.getVariableInfo(varName) + "\n");
			/*
			if (vensimHelper.getVariableAttributes(varName).get(Vensim.ATTRIB_VARTYPE)[0].toString().equalsIgnoreCase("constant")) {
				System.out.println("constant: " + varName);
			}
			*/
		}

		fw.flush();
		fw.close();

		Workbook workbook = null;

		Workbook destWorkbook = new XSSFWorkbook();
		Workbook allVariablesWorkbook = new XSSFWorkbook();

		try {
			System.out.println(new File("./").getAbsolutePath());
			// URL url = this.getClass().getClassLoader().getResource("colabTest_old.xls");
			//URL url = this.getClass().getClassLoader().getResource("BAU_old_data.xls");
			//URL url = this.getClass().getClassLoader().getResource("Ref3.xls");
			URL url = this.getClass().getClassLoader().getResource("AVGtest.xls");
			workbook = WorkbookFactory.create(new File(url.getFile()));

		} catch (IOException e) {
			throw new RuntimeException("can't open enroadsTestData.xls", e);
		} catch (InvalidFormatException e) {
			throw new RuntimeException("can't open enroadsTestData.xls", e);
		}

		Sheet sheet = workbook.getSheetAt(0);
		Sheet destSheet = destWorkbook.createSheet();
		Sheet allVariablesSheet = allVariablesWorkbook.createSheet();

		Iterator<Row> rowIterator = sheet.rowIterator();

		Map<String, RowIdxValues> expectedValues = new TreeMap<String, RowIdxValues>();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			Map<Integer, Float> values = new TreeMap<Integer, Float>();
			String varName = row.getCell(0).getStringCellValue();
			for (int i = 0; i < 6; i++) {
				Cell cell = row.getCell(i + 1);
				Float val = null;
				if (cell != null) {
					val = (float) cell.getNumericCellValue();
				}
				values.put(2000 + i * 20, val);
			}
			expectedValues.put(varName, new RowIdxValues(row.getRowNum(),
					values));

		}

		// set variables values
		Set<String> inputsNames = new HashSet<String>();

		URL url = this.getClass().getClassLoader().getResource("enroadsAvgs.cin");
		BufferedReader inputsReader = new BufferedReader(new FileReader(new File(url.getFile())));
		
		String line = inputsReader.readLine(); 
		StringBuilder mins = new StringBuilder();
		StringBuilder maxes = new StringBuilder();
		StringBuilder avgs = new StringBuilder();
		while (line != null && line.trim().length() > 0) { 
			String[] nameAndValue = line.split("=");
			inputsNames.add(nameAndValue[0]);
			if (setInputs) {
				vensimHelper.setVariable(nameAndValue[0], (float) Double.parseDouble(nameAndValue[1])); 
			}
			
			if (vensimHelper.getVariableExists(nameAndValue[0].trim())) {
				mins.append(nameAndValue[0].trim() + "=" + vensimHelper.getVariableAttributes(nameAndValue[0].trim()).get(Vensim.ATTRIB_MIN)[0] + "\n");
				maxes.append(nameAndValue[0].trim() + "=" + vensimHelper.getVariableAttributes(nameAndValue[0].trim()).get(Vensim.ATTRIB_MAX)[0] + "\n");
				avgs.append(nameAndValue[0].trim() + "=" + 
						((
								Double.parseDouble(vensimHelper.getVariableAttributes(nameAndValue[0].trim()).get(Vensim.ATTRIB_MIN)[0]) + 
								Double.parseDouble(vensimHelper.getVariableAttributes(nameAndValue[0].trim()).get(Vensim.ATTRIB_MAX)[0])
						)/2) +
						"\n");
			}
			else {
				System.out.println("Variable doesn't exist: " + nameAndValue[0]);
			}
			
			line = inputsReader.readLine();
			
		}
	 /*
		System.out.println(" ************ Mins ************\n" + mins.toString());
		System.out.println(" ************ Maxes ************\n" + maxes.toString());
		System.out.println(" ************ Avgs ************\n" + avgs.toString());
		*/
		
		inputsReader.close();
		
		vensimHelper.run();
		// System.out.println(Arrays.toString(vensimHelper.getVariable("Energy supply capacity by resource[RNuc]")));
		int variablesChecked = 0;
		int errorCount = 0, okCount = 0;

		CellStyle cs = destWorkbook.createCellStyle();
		cs.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		CellStyle cs2 = destWorkbook.createCellStyle();
		cs2.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
		cs.setFillPattern(cs.SOLID_FOREGROUND);
		cs2.setFillPattern(cs.SOLID_FOREGROUND);

		for (String varName : vensimHelper.getVariables()) {
			float[] values = vensimHelper.getVariable(varName);
			if (values.length == 35) {
				System.out.println(varName + "\t" + Arrays.toString(values));
			}
		}

		for (String varName : expectedValues.keySet()) {
			variablesChecked++;
			boolean hasError = false;
			// System.out.println(varName);
			if (varName.equals("Time")) {
				continue;
			}
			Map<Integer, Float> expectedBoxed = expectedValues.get(varName).values;
			// System.out.println(variablesChecked + ". " + varName + "\t" +
			// String.valueOf(expectedBoxed));
			if (expectedBoxed == null) {
				continue;
			}
			float[] calculatedValuesFull = vensimHelper.getVariable(varName);
			Pair<float[], float[]> calculatedIndexedValues = vensimHelper
					.getVariableIndexed(varName);
			Map<Integer, Float> calculatedValues = new TreeMap<Integer, Float>();

			for (int i = 0; i < calculatedIndexedValues.getLeft().length; i++) {
				float idx = calculatedIndexedValues.getLeft()[i];
				if (expectedBoxed.containsKey((int) idx)) {
					calculatedValues.put((int) idx,
							calculatedIndexedValues.getRight()[i]);
				}
			}

			if (calculatedIndexedValues.getLeft().length == 1) {
				calculatedValues.put(2000,
						calculatedIndexedValues.getRight()[0]);

			}

			List<Integer> yearsWithErrors = new ArrayList<Integer>();
			for (int year : expectedBoxed.keySet()) {
				if (!calculatedValues.containsKey(year)) {
					if (expectedBoxed.get(year) == null)
						continue;
					String msg = "Can't find corresponding calculated value in var: "
							+ ", year: "
							+ year
							+ ", expected: "
							+ expectedBoxed
							+ "\nall actual: "
							+ calculatedValues
							+ "\ncalculatedFull (len): "
							+ " ("
							+ calculatedValuesFull.length
							+ ") "
							+ Arrays.toString(calculatedValuesFull);
					_log.error(msg);
					throw new RuntimeException(msg);
				}
				Float valA = calculatedValues.get(year);
				Float valC = calculatedValues.get(year-3);
				Float valB = expectedBoxed.get(year);

				if (areEqualWithTollerance(valA, valB)) {
					okCount++;
				}
				else if (areEqualWithTollerance(valC, valB)) {
					okCount++;
				}
				else {
					errorCount ++;
					yearsWithErrors.add(year);
					hasError = true;
				}
					
			}
			
			
			
			

			if (hasError) {
				System.out.println("has error!:");
				Row sourceRow = sheet.getRow(expectedValues.get(varName).row);
				Row row = destSheet.createRow(destSheet.getLastRowNum()+1);
				Row row2 = destSheet.createRow(destSheet.getLastRowNum()+1);
				errorCount++;

				row.createCell(0).setCellValue(varName);
				
				for (Map.Entry<Integer, Float> entry: expectedValues.get(varName).values.entrySet()) {
					int i = (entry.getKey() - 2000) / 20;
					Float valA = entry.getValue();
					Float valB = calculatedValues.get(entry.getKey());
					
					//System.out.println("expected: " + valA + "\tcalculated: " + valB);
					//System.out.println(Arrays.toString(calculatedValuesFull));
					boolean areEqual = areEqualWithTollerance(valA, valB);
					
					if (entry.getValue() != null) {
						Cell cell = row.createCell(1 + i);
						cell.setCellValue(entry.getValue());
						if (!areEqual) {
							cell.setCellStyle(cs);
						}
					}
					if (calculatedValues.get(entry.getKey())  != null) {
						Cell cell = row2.createCell(1 + i);
						cell.setCellValue(calculatedValues.get(entry.getKey()));
						if (!areEqual) {
							cell.setCellStyle(cs2);
						}
						
					}
					
				}
				
				
/*
				for (int i = 0; i < yearsWithErrors.size(); i++) {
					int rowIdx = 
					Cell cell = row.createCell(1 + i);
					cell.setCellValue(expected[i]); 
					Cell sourceCell = sourceRow.getCell(1 + i);
					if (sourceCell != null) {
						cell.setCellValue(sourceCell.getNumericCellValue());
					}
					cell.setCellStyle(cs);

					cell = row2.createCell(1 + i);
					cell.setCellValue(calculatedValuesShort[i]);
					cell.setCellStyle(cs2);
				}
*/
			}

			// write values to all variables sheet
			Row allVariablesRow =  allVariablesSheet.createRow(allVariablesSheet.getLastRowNum() + 1);
			
			allVariablesRow.createCell(0).setCellValue(varName);
			int i = 1;
			for (int year : calculatedValues.keySet()) {
				Cell cell = allVariablesRow.createCell(i++);
				if(expectedBoxed.get(year) != null) {
//					cell.setCellValue(year);
					cell.setCellValue(expectedBoxed.get(year));
				}
			}
			
			if (inputsNames.contains(varName)) {
				allVariablesRow.createCell(7).setCellValue("INPUT");
				
			}
			else {

				allVariablesRow.createCell(7).setCellValue("OUTPUT");
			}
			
			String[] unitAttr = vensimHelper.getVariableAttributes(varName).get(Vensim.ATTRIB_UNITS);
			if (unitAttr.length == 0 && varName.indexOf("[") > 0) {
				unitAttr = vensimHelper.getVariableAttributes(varName.substring(0, varName.indexOf("["))).get(Vensim.ATTRIB_UNITS);
			}
			allVariablesRow.createCell(8).setCellValue(Arrays.toString(unitAttr));


		}
		System.out.println("errorCount: " + errorCount);
		System.out.println("okCount: " + okCount);

		FileOutputStream fos = new FileOutputStream(
				new File("/tmp/output.xlsx"));

		FileOutputStream allVariablesFos = new FileOutputStream(
				new File("/tmp/allVariables.xlsx"));
		destWorkbook.write(fos);
		allVariablesWorkbook.write(allVariablesFos);
		fos.flush();
		fos.close();
		allVariablesFos.flush();
		allVariablesFos.close();
		System.out.println("end...");
		Assert.assertEquals(
				"Should check all values from spreadsheet but nothing found",
				expectedValues.size(), variablesChecked);
	}
	
	private boolean areEqualWithTollerance(Float valA, Float valB) {
		if (valA != null && !valA.equals(valB)) {
			if (valA != null && valB != null) {
				if (Math.abs((double) valA - valB) <= Math.abs(valB) * ALLOWED_DIFFERENCE_IN_PERCENTS
						/ (double) 100) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public static class RowIdxValues {
		public final int row;
		public final Map<Integer, Float> values;

		RowIdxValues(int row, Map<Integer, Float> values) {
			this.row = row;
			this.values = values;
		}

		@Override
		public String toString() {
			return "RowIdxValues [row=" + row + ", values=" + values + "]";
		}

	}

}
