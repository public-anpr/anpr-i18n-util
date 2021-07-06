package test.it.sogei.anpr.util.i18n.json;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.sogei.anpr.util.i18n.json.ExcelJsonI18N;
import it.sogei.anpr.util.i18n.json.StubJsonI18N;

/**
 * 

	../anpr-sc-home-fe/src/common/locales/it/it_avvisi.json

 * @author mttfranci
 *
 */
public class TestExcelJsonI18N {

	private final static Logger logger = LoggerFactory.getLogger( TestExcelJsonI18N.class );
	
	private final static String INPUT_JSON_PATH = "src/test/resources/input_json/";
	
	@Test
	public void testExcelParser() {
		try ( Workbook workbook = new XSSFWorkbook();
				FileOutputStream outputXlsWriter = new FileOutputStream( new File( "target/traduzioni_json.xlsx" ) ) ) {
			File inputJsonPathIt = new File( INPUT_JSON_PATH, StubJsonI18N.CODICE_LINGUA_IT );
			ExcelJsonI18N excel = new ExcelJsonI18N();
			
			for ( File currentInputItFile : inputJsonPathIt.listFiles() ) {
				File currentInputLabelFile = new File( currentInputItFile.getCanonicalPath().replaceAll( "/"+StubJsonI18N.CODICE_LINGUA_IT+"/"+StubJsonI18N.CODICE_LINGUA_IT , "/label/label" ) );
				try ( FileReader inputIt = new FileReader(currentInputItFile);
						FileReader inputLabel = new FileReader(currentInputLabelFile) ) {
					String sheetName = currentInputItFile.getName().replace( ".json" , "" ).replace( "it_" , "" );
					excel.createSheet(workbook, sheetName, inputIt, inputLabel );
				}
			}
			workbook.write( outputXlsWriter );
		} catch (Exception e) {
			logger.error( "Errore : "+e, e );
		}
	}
	
}
