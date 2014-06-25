package edu.mit.cci.roma.excel.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class EMFCombineSpreadsheetsUtil {
	private final static File sreadsheetsDir = new File("/home/janusz/workdir/pangaea/emf_spreadsheets");
	private final static File outputSpreadsheetFile = new File("/home/janusz/workdir/pangaea/ROMA/server/src/test/resources/2014_may_models/emf_combined/emf_20140520.xlsx");
	private final static File outputsCsvFile = new File("/home/janusz/workdir/pangaea/ROMA/server/src/test/resources/2014_may_models/emf_combined/emf_20140520_outputs.csv");
	private final static File inputsCsvFile = new File("/home/janusz/workdir/pangaea/ROMA/server/src/test/resources/2014_may_models/emf_combined/emf_20140520_inputs.csv");
	private final static File simCsvFile = new File("/home/janusz/workdir/pangaea/ROMA/server/src/test/resources/2014_may_models/emf_combined/emf_20140520_sim.csv");
	private final static File outputsConfigurationFile = new File("/home/janusz/workdir/pangaea/emf_spreadsheets/conf.csv");
	
	public static void main(String[] args) throws InvalidFormatException, IOException {
		Map<String, Set<String>> perOutputModels = new HashMap<String, Set<String>>();
		Set<String> availableScenarios = new TreeSet<String>();
		outputSpreadsheetFile.getParentFile().mkdirs();
		outputSpreadsheetFile.delete();
		outputsCsvFile.delete();
		inputsCsvFile.delete();
		simCsvFile.delete();
		
		Workbook outputSpreadseet = new HSSFWorkbook();
		
		CSVWriter outputsCsv = new CSVWriter(new FileWriter(outputsCsvFile));
		CSVWriter inputsCsv = new CSVWriter( new FileWriter(inputsCsvFile)); 
		CSVWriter simCsv = new CSVWriter(new FileWriter(simCsvFile));
		CSVReader outputsConfCsv = new CSVReader(new FileReader(outputsConfigurationFile));
		
		//CopySheets.mergeExcelFiles(outputSpreadseet,  sreadsheetsDir.listFiles());
		for (File inFile: sreadsheetsDir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".xlsx");
			}
		})) {
			System.out.println("processing file: " + inFile.getAbsolutePath());
			Workbook wbFrom = WorkbookFactory.create(inFile);
			copySheets(wbFrom, outputSpreadseet, inFile);
			copySheets(wbFrom, outputSpreadseet, inFile);
		}
		long nextId = new Date().getTime()/1000;
		
		
		outputsCsv.writeNext(new String[] {"description","id","internalname","name","profile","vartype","units","labels","external","varcontext","indexingid","defaultval","id","max","metadata","min","categories","sheet","rowsrange"});
		inputsCsv.writeNext(new String[] {"description","id","internalname","name","profile","vartype","units","labels","external","varcontext","indexingid","defaultval","id","max","metadata","min","categories","sheet","rowsrange"});
		for (int i=0; i < outputSpreadseet.getNumberOfSheets(); i++) {
			Sheet modelSheet = outputSpreadseet.getSheetAt(i);

			if (modelSheet.getSheetName().contains("Inputs_Inputs") || modelSheet.getSheetName().endsWith("_out")) continue;
			System.out.println("Adding outputs to sheet: " + modelSheet.getSheetName());
			
			Sheet outputSheet = outputSpreadseet.createSheet(modelSheet.getSheetName() + "_out");
			// find all of the distinct model names, for every name create a row with "model", "concat(model, inputs_inputs!B1)", vlookup....
			Set<String> models = new TreeSet<String>();
			for (int r=1; r <= modelSheet.getLastRowNum(); r++) {
				Row row = modelSheet.getRow(r);
				if (row == null) continue;
				if (row.getCell(0).getStringCellValue().contains("generated")) {
					// ignore
					continue;
				}
				models.add(row.getCell(0).getStringCellValue());
				availableScenarios.add(row.getCell(1).getStringCellValue());
			}
			perOutputModels.put(outputSheet.getSheetName(), models);
			
			// create index row
			Row indexRow = outputSheet.createRow(0);
			for (int tmp=0; tmp < 11; tmp++) {
				Cell c = indexRow.createCell(2+tmp);
				c.setCellFormula("'" + modelSheet.getSheetName() + "'!" + ((char) ('F' + tmp)) + "1");
			}
			
			
			for (String model: models) {
				Row outputRow = outputSheet.createRow(outputSheet.getLastRowNum()+1);
				
				outputRow.createCell(0).setCellValue(model);
				outputRow.createCell(1).setCellFormula("CONCATENATE(A" + (outputRow.getRowNum()+ 1) + ",'Inputs_Inputs'!B$1)");
				for (int tmp = 0; tmp < 11; tmp++) {
					outputRow.createCell(2+tmp).setCellFormula("VLOOKUP($B" + (outputRow.getRowNum() + 1) + ",'" + modelSheet.getSheetName() + "'!$E$2:$Q$500," + (2+tmp) + ",FALSE)");
				}
			}
			
		}
		// read header and ignore it
		String line[] = outputsConfCsv.readNext();
		while ((line = outputsConfCsv.readNext()) != null && line.length > 0) {
			String outputSheetName = line[0];
			String outputLabel = line[2];
			String outputUnit = line[1];
			
			long indexId = nextId++;
			// index output
			outputsCsv.writeNext(new String[] {"Year",
					String.valueOf(indexId),
					"Time_output" + indexId,
					"Time",
					"java.lang.Integer",
					"RANGE",
					"Year",
					"Year",
					"",
					"INDEX",
					"NULL",
					"",
					String.valueOf(indexId),
					"2100.0",
					String.valueOf(indexId),
					"2005.0",
					"NULL",
					outputSheetName,
					"C1:M1"});
			
			int rowNumber = 2;
			long outputId = nextId++;
			for (String model: perOutputModels.get(outputSheetName)) {
				outputsCsv.writeNext(new String[] {
						model, 
						String.valueOf(nextId++),
						outputSheetName + model,
						model,
						"java.lang.Double",
						"RANGE",
						outputLabel,
						outputUnit,
						"",
						"INDEXED",
						String.valueOf(indexId),
						"",
						String.valueOf(outputId),
						"NULL",
						String.valueOf(outputId),
						"NULL",
						"NULL",
						outputSheetName, 
						"C" + rowNumber + ":M" + rowNumber++});
			}
		}
		StringBuilder availableScenariosStr = new StringBuilder();
		for (String scenario: availableScenarios) {
			if (availableScenariosStr.length() > 0) {
				availableScenariosStr.append(";");
			}
			availableScenariosStr.append(scenario);
		}
		inputsCsv.writeNext(new String[] {
				"Input scenario",
				String.valueOf(nextId),
				"input_scenario",
				"Input scenario",
				"java.lang.String",
				"CATEGORICAL",
				"Input scenario",
				"Text",
				"",
				"SCALAR",
				"NULL",
				"",
				String.valueOf(nextId),
				"NULL",
				String.valueOf(nextId),
				"NULL",
				availableScenariosStr.toString(),
				"Inputs_Inputs",
				"B1:B1"
				
		});
		nextId++;
		
		simCsv.writeNext(new String[] {"description","id","name","url","state","creation","compositeString","configured","type"});
		String modelName = "EMF " + new Date();
		simCsv.writeNext(new String[] {modelName,String.valueOf(nextId),modelName,"","PUBLIC",new Date().toString(),"NULL","NULL","none"});
		

		
		FileOutputStream fos = new FileOutputStream(outputSpreadsheetFile);
		outputSpreadseet.write(fos);
		fos.flush();
		fos.close();
		outputsCsv.flush();
		outputsCsv.close();
		inputsCsv.flush();
		inputsCsv.close();
		simCsv.flush();
		simCsv.close();
		outputsConfCsv.close();
		
	}
	
	private static void copySheets(Workbook wbFrom, Workbook wbTo, File wbFromFile) {
		Map<String, String> srcSheetMapping = new HashMap<String, String>();
		

		for (int i = 0 ; i < wbFrom.getNumberOfSheets(); i++) {
			Sheet sheetFrom = wbFrom.getSheetAt(i);
			String newName = wbFromFile.getName();
			newName = newName.substring(0, newName.lastIndexOf('.')) + "_" + wbFrom.getSheetName(i);
			srcSheetMapping.put(sheetFrom.getSheetName(), newName);
		}

		for (int i = 0 ; i < wbFrom.getNumberOfSheets(); i++) {
			Sheet sheetFrom = wbFrom.getSheetAt(i);
			String sheetName = srcSheetMapping.get(sheetFrom.getSheetName());
			Sheet sheetTo = wbTo.getSheet(sheetName);
			if (sheetTo == null) {
				
				sheetTo = wbTo.createSheet(sheetName);
			}
			
			for (int r = 0; r <= sheetFrom.getLastRowNum(); r++) {
				Row rowFrom = sheetFrom.getRow(r);
				if (rowFrom == null) continue;
				
				Row rowTo = sheetTo.getRow(r);
				if (rowTo == null) {
					rowTo = sheetTo.createRow(r);
				}
				for (int c = 0; c <= rowFrom.getLastCellNum(); c++) {
					
					Cell cellFrom = rowFrom.getCell(c);
					if (cellFrom == null) continue;
					Cell cellTo = rowTo.getCell(c);
					if (cellTo == null) {
						cellTo = rowTo.createCell(c);
					}
					
					cellTo.setCellType(cellFrom.getCellType());
					
					switch (cellFrom.getCellType()) {
					case Cell.CELL_TYPE_BLANK: 
						break;
					case Cell.CELL_TYPE_BOOLEAN:
						cellTo.setCellValue(cellFrom.getBooleanCellValue());
						break;
					case Cell.CELL_TYPE_ERROR:
						cellTo.setCellErrorValue(cellFrom.getErrorCellValue());
						break;
					case Cell.CELL_TYPE_FORMULA:
						String fromFormula = cellFrom.getCellFormula();
						String toFormula = fromFormula;
						
						// replace all references to inner sheets in that formula
						for (Map.Entry<String, String> entry: srcSheetMapping.entrySet()) {

							toFormula = toFormula.replace(entry.getKey() + "!", "'" + entry.getValue() + "'!");
							toFormula = toFormula.replace("'" + entry.getKey() + "'!", "'" + entry.getValue() + "'!");
						}
						
						
						cellTo.setCellFormula(toFormula);
						break;
					case Cell.CELL_TYPE_NUMERIC:
						cellTo.setCellValue(cellFrom.getNumericCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
						cellTo.setCellValue(cellFrom.getStringCellValue());
						break;
					}
				}

				// cell with concatenated model and scenario// for now we overwrite unit column
				
				Cell cellWithModelAndScenario = rowTo.getCell(4);
				if (cellWithModelAndScenario == null) {
					cellWithModelAndScenario = rowTo.createCell(4);
				}

				cellWithModelAndScenario.setCellFormula("CONCATENATE(A" + (r+1) + ",B" + (r+1) + ")");
				
			}
			
		}
	}

}
