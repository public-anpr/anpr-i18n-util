package it.sogei.anpr.util.i18n.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Excel2Json_I18N {
	
	private final static Logger logger = LoggerFactory.getLogger( ExcelJsonI18N.class );
	private final static int COLONNA_LABEL = 1;
	private final static int COLONNA_TESTO = 2;
	private final static int COLONNA_TRADUZIONE = 3;
	private final static String SHEET_SUMMARY = "summary";
	private final static String BASE_PATH ="src/test/resources/output_json";

	public void convertXLSX (String path) throws EncryptedDocumentException, IOException {
		
		try {
			File inp = new File( path );
			Workbook wb = new XSSFWorkbook(inp);
		    Sheet sheetSummary = wb.getSheet(SHEET_SUMMARY);
		    String[] summaryData = this.readSummary(sheetSummary);	//{lang, pathOrig, pathLabel, pathTrad}
		    
		    // creo i json per ogni sheet
		    for (int sheetNum=1; sheetNum< wb.getNumberOfSheets(); sheetNum++) {
			    String pathOrig = BASE_PATH +"/"+summaryData[1];
			    String pathLabel = BASE_PATH +"/"+summaryData[2];
			    String pathTrad = BASE_PATH +"/"+summaryData[3];
		    		Sheet sheet = wb.getSheetAt(sheetNum);
		    		String sheetName = sheet.getSheetName();
		    		logger.info( "generazione json : {}", sheetName );
                if(sheetName != null && sheetName.length() > 0) {
	                	LinkedHashMap<String, Object> mappaLabel = getMapFromSheet(sheet, COLONNA_LABEL);
	                	LinkedHashMap<String, Object> mappaTrad = getMapFromSheet(sheet, COLONNA_TRADUZIONE);
	                	LinkedHashMap<String, Object> mappaIta = getMapFromSheet(sheet, COLONNA_TESTO);
                    
                    //Generare i json dalle mappe
                    String jsonLabel = new ObjectMapper().writeValueAsString(mappaLabel);
                    String jsonTrad = new ObjectMapper().writeValueAsString(mappaTrad);
                    String jsonIta = new ObjectMapper().writeValueAsString(mappaIta);
                    this.writeJson(pathLabel, sheetName, jsonLabel, "/label_");
                    this.writeJson(pathTrad, sheetName, jsonTrad, "/"+summaryData[0]+"_");
                    this.writeJson(pathOrig, sheetName, jsonIta, "/"+summaryData[1].replace("/", "")+"_");
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
        	 	LinkedHashMap<String, Object> mappa2 = new LinkedHashMap<String, Object>();  
            for(int i=firstRowNum+1; i<=lastRowNum; i++) {
                Row row = sheet.getRow(i);
                Cell cellKey = row.getCell(colKey);
                key1 = cellKey.getStringCellValue();
                String value = "";
                if (row.getCell(colNumb) != null) {
                    value = row.getCell(colNumb).getStringCellValue();
                }
                if (key1.contains(".")) {
                		String[] str = key1.split("\\.");
                		key1 = str[0];
                		String key2 = str[1];
                		
                		mappa2.put(key2,value);
                } else {
                  	mappa.put(key1, value);
                }
            }
            	if (mappa2.keySet() != null ) {
            		mappa.put(key1,mappa2);
             } 
	      }
         return mappa;
	    }
	 
	 private void writeJson(String path, String sheetName, String json, String prefix) {
         try {
				File file = new File(path);
				FileWriter writerLabel = new FileWriter(file+prefix+sheetName+".json");
				writerLabel.write(json);
				writerLabel.close();
			} catch (Exception e) {
				logger.error( "Errore : "+e, e );
			}
	 }
	 
	 private String[] readSummary(Sheet sheetSummary) {
		 String[] summaryData = new String[4]; 		// in formato:{lang, pathOrig, pathLabel, pathTrad}
		 for (int i=0; i< summaryData.length; i++ ) {
			 summaryData[i]=sheetSummary.getRow(i).getCell(1).getStringCellValue();
			 }
		 return summaryData;
	 }
	 
}
