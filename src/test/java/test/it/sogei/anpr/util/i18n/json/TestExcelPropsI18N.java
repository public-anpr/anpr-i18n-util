package test.it.sogei.anpr.util.i18n.json;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.sogei.anpr.util.i18n.json.ExcelPropsI18N;
import it.sogei.anpr.util.i18n.json.StubJsonI18N;

/**
 * 

	../anpr-sc-home-fe/src/common/locales/it/it_avvisi.json

 * @author mttfranci
 *
 */
public class TestExcelPropsI18N {

	private final static Logger logger = LoggerFactory.getLogger( TestExcelPropsI18N.class );
	
	private final static String INPUT_PROPS_PATH = "src/test/resources/input_props/";
	private final static String CODICE_LINGUA_IT = "it";
	private final static String INPUT_PROPERTIES_SUMMARY_PATH = "src/test/resources/input_props/summary.xml";

	@Test
	public void testExcelParser() {
		try ( Workbook workbook = new XSSFWorkbook();
				FileOutputStream outputXlsWriter = new FileOutputStream( new File( "target/traduzioni_props.xlsx" ) ) ) {
			File inputJsonPathIt = new File( INPUT_PROPS_PATH, StubJsonI18N.CODICE_LINGUA_IT );
			ExcelPropsI18N excel = new ExcelPropsI18N();
			File inputPropsSummaryPath = new File( INPUT_PROPERTIES_SUMMARY_PATH);
			int label = 0;
			for ( File currentInputItFile : inputJsonPathIt.listFiles() ) {
				File currentInputLabelFile = new File( currentInputItFile.getCanonicalPath().replaceAll( "\\\\"+CODICE_LINGUA_IT+"\\\\"+CODICE_LINGUA_IT , "\\\\label\\\\label" ) );
				try ( FileReader inputIt = new FileReader(currentInputItFile);
						FileReader inputLabel = new FileReader(currentInputLabelFile);
						FileReader inputPropsSummary = new FileReader(inputPropsSummaryPath);) {
						if (label == 0) {
							excel.createSummary(workbook, inputPropsSummary);
							label=1;
						}
					String sheetName = currentInputItFile.getName().replace( ".xml" , "" ).replace( "it_" , "" );
					excel.createSheet(workbook, sheetName, inputIt, inputLabel );
				}
			}
			workbook.write( outputXlsWriter );
		} catch (Exception e) {
			logger.error( "Errore : "+e, e );
		}
	}
	
}
