package it.sogei.anpr.util.i18n.json;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Excel2Props_I18N {
	private final static Logger logger = LoggerFactory.getLogger( ExcelJsonI18N.class );
	private final static int COLONNA_LABEL = 1;
	private final static int COLONNA_TESTO = 2;
	private final static int COLONNA_TRADUZIONE = 3;
	private final static String SHEET_SUMMARY = "summary";
	private final static String BASE_PATH ="src/test/resources/output_props";

	public void convertXLSX (String path) throws EncryptedDocumentException, IOException {
		
		try {
			File inp = new File( path );
			Workbook wb = new XSSFWorkbook(inp);
		    Sheet sheetSummary = wb.getSheet(SHEET_SUMMARY);
		    String[] summaryData = this.readSummary(sheetSummary);	//{lang, pathOrig, pathLabel, pathTrad}
		    
		    // creo il file di property per ogni sheet
		    for (int sheetNum=1; sheetNum< wb.getNumberOfSheets(); sheetNum++) {
			    String pathOriginale = BASE_PATH +"/"+summaryData[1];
			    String pathLabel = BASE_PATH +"/"+summaryData[2];
			    String pathTrad = BASE_PATH +"/"+summaryData[3];
		    		Sheet sheet = wb.getSheetAt(sheetNum);
		    		String sheetName = sheet.getSheetName();
		    		logger.info( "generazione property : {}", sheetName );
                if(sheetName != null && sheetName.length() > 0) {
	                	LinkedHashMap<String, Object> mappaLabel = getMapFromSheet(sheet, COLONNA_LABEL);
	                	LinkedHashMap<String, Object> mappaTrad = getMapFromSheet(sheet, COLONNA_TRADUZIONE);
	                	LinkedHashMap<String, Object> mappaIta = getMapFromSheet(sheet, COLONNA_TESTO);
                    
	                	//Genero i file di properties dalle mappe
                    SortedProperties sortedProp = new SortedProperties();
	                	this.savePropertiesToXML(sortedProp, mappaLabel, pathLabel, sheetName, "label_");
	                	this.savePropertiesToXML(sortedProp, mappaTrad, pathTrad, sheetName, summaryData[0]+"_");
	                	this.savePropertiesToXML(sortedProp, mappaIta, pathOriginale, sheetName, summaryData[1].replace("/", "")+"_");
					} 
                }
		    wb.close();
		} catch(Exception ioe) {
		    ioe.printStackTrace();
		}
	}
	
	 private static LinkedHashMap<String, Object> getMapFromSheet(Sheet sheet, int colNumb) {
		 int colKey = 0;
		 LinkedHashMap<String, Object> mappa = new LinkedHashMap<String, Object>();   
         int firstRowNum = sheet.getFirstRowNum();
         int lastRowNum = sheet.getLastRowNum();
         if(lastRowNum > 0) {
        	 	String key1=null;
            for(int i=firstRowNum+1; i<=lastRowNum; i++) {
                Row row = sheet.getRow(i);
                Cell cellKey = row.getCell(colKey);
                key1 = cellKey.getStringCellValue();
                String value = "";
                if (row.getCell(colNumb) != null) {
                    value = row.getCell(colNumb).getStringCellValue();
                }
                mappa.put(key1, value);
            }
	      }
         return mappa;
	    }
	 
	 private String[] readSummary(Sheet sheetSummary) {
		 String[] summaryData = new String[4]; 		// in formato:{lang, pathOrig, pathLabel, pathTrad}
		 for (int i=0; i< summaryData.length; i++ ) {
			 summaryData[i]=sheetSummary.getRow(i).getCell(1).getStringCellValue();
			 }
		 return summaryData;
	 }
	 
	 private void savePropertiesToXML(SortedProperties props, LinkedHashMap<String, Object> map, String path, String sheetName, String filePrefix) throws IOException {
		 Set<String> keySet = map.keySet();
		 for (String key : keySet) {
			 props.setProperty(key, (String)map.get(key));
		 }
		 sheetName+=".xml";
		 FileOutputStream fos = new FileOutputStream(path+filePrefix+sheetName);
		 props.storeToXML(fos, "");
	 }
	 
}
