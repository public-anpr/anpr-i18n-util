package test.it.sogei.anpr.util.i18n.json;

import java.io.IOException;
import org.apache.poi.EncryptedDocumentException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.sogei.anpr.util.i18n.json.Excel2Json_I18N;
import it.sogei.anpr.util.i18n.json.Excel2Props_I18N;

public class TestExcel_2_File_I18N {
	
	private final static String INPUT_XLSX_PATH_JSON = "target/traduzioni_json.xlsx";
	private final static String INPUT_XLSX_PATH_PROPS = "target/traduzioni_props.xlsx";
	private final static Logger logger = LoggerFactory.getLogger( Test_GenerateExcel_I18N.class );

	
	@Test
	public void testJsonGenerator() {
		Excel2Json_I18N convertitore = new Excel2Json_I18N();
		try {
			convertitore.convertXLSX(INPUT_XLSX_PATH_JSON);
		} catch (EncryptedDocumentException | IOException e) {
			logger.error( "Errore : "+e, e );
		}
	}
	
	@Test
	public void testPropertiesGenerator() {
		Excel2Props_I18N convertitore = new Excel2Props_I18N();
		try {
			convertitore.convertXLSX(INPUT_XLSX_PATH_PROPS);
		} catch (EncryptedDocumentException | IOException e) {
			logger.error( "Errore : "+e, e );
		}
	}
 
	
}
