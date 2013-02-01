package edu.mit.cci.roma.excel.server;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml")
public class ExcelReadingFromDBTest {
	
	@Test
	public void testReadingFromDB() throws FileNotFoundException, IOException {
		
		for (ExcelSimulation excelSim: ExcelSimulation.findAllExcelSimulations()) {
	        ByteArrayInputStream inputStream = new ByteArrayInputStream(excelSim.getFile());
	        
	        try {
	        	new HSSFWorkbook(new POIFSFileSystem(inputStream));
	        }
	        catch (Throwable e) {
	        	e.printStackTrace();
	        	Assert.fail("Can't read workbook for roma " + excelSim.getSimulation().getName());
	        }
		}
	}

}
