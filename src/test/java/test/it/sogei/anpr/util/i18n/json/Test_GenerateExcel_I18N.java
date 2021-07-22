package test.it.sogei.anpr.util.i18n.json;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.fugerit.java.core.lang.helpers.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.sogei.anpr.util.i18n.json.ExcelJsonI18N;
import it.sogei.anpr.util.i18n.json.ExcelPropsI18N;
import it.sogei.anpr.util.i18n.json.StubJsonI18N;

/**
 * 

	../anpr-sc-home-fe/src/common/locales/it/it_avvisi.json

 * @author mttfranci
 *
 */
public class Test_GenerateExcel_I18N {

	private final static Logger logger = LoggerFactory.getLogger( Test_GenerateExcel_I18N.class );
	
	private final static String INPUT_PROPS_PATH = "src/test/resources/input_props/";
	private final static String INPUT_PROPERTIES_SUMMARY_PATH = "src/test/resources/input_props/";
	private final static String INPUT_JSON_PATH = "src/test/resources/input_json/";
	private final static String INPUT_JSON_SUMMARY_PATH = "src/test/resources/input_json/";
	private final static String LANG_DE = "de";
	private final static String LANG_EN = "en"; 

	@Test
	public void testExcelParser_FromJson() {
		String lang = LANG_EN; //lingua della traduzione
		try ( Workbook workbook = new XSSFWorkbook();
			FileOutputStream outputXlsWriter = new FileOutputStream( new File( "target/traduzioni_json.xlsx" ) ) ) {
			File inputJsonPathIt = new File( INPUT_JSON_PATH, StubJsonI18N.CODICE_LINGUA_IT );
			ExcelJsonI18N excel = new ExcelJsonI18N();
			File inputJsonSummaryPath = new File( INPUT_JSON_SUMMARY_PATH+lang+"_summary.json");
			int label = 0;
			
			for ( File currentInputItFile : inputJsonPathIt.listFiles() ) {
				FileReader inputTradOld = null;
				File currentInputLabelFile = new File( currentInputItFile.getCanonicalPath().replaceAll( "\\\\"+StubJsonI18N.CODICE_LINGUA_IT+"\\\\"+StubJsonI18N.CODICE_LINGUA_IT , "\\\\label\\\\label" ) );
				if (StringUtils.isNotEmpty(lang) ) {
					File currentInputTradFile = new File( currentInputItFile.getCanonicalPath().replaceAll( "\\\\"+StubJsonI18N.CODICE_LINGUA_IT+"\\\\"+StubJsonI18N.CODICE_LINGUA_IT , "\\\\"+lang+"\\\\"+lang ) );
					inputTradOld = new FileReader(currentInputTradFile);
				}
				try ( FileReader inputIt = new FileReader(currentInputItFile);
					FileReader inputLabel = new FileReader(currentInputLabelFile);
					FileReader inputJsonSummary = new FileReader(inputJsonSummaryPath);) {
					if (label == 0) {
						excel.createSummary(workbook, inputJsonSummary);
						label=1;
					}
					String sheetName = currentInputItFile.getName().replace( ".json" , "" ).replace( "it_" , "" );
					if (StringUtils.isNotEmpty(lang) && inputTradOld!=null) {
						excel.createSheet(workbook, sheetName, inputIt, inputLabel, inputTradOld, lang ); 
					} else {
						excel.createSheet(workbook, sheetName, inputIt, inputLabel ); 
					}
				}
			}
			workbook.write( outputXlsWriter );
		} catch (Exception e) {
			logger.error( "Errore : "+e, e );
		}
	}
	
	@Test
	public void testExcelParser_FromProperties() {
		String lang = LANG_EN;  //lingua della traduzione
		try ( Workbook workbook = new XSSFWorkbook();
				FileOutputStream outputXlsWriter = new FileOutputStream( new File( "target/traduzioni_props.xlsx" ) ) ) {
			File inputJsonPathIt = new File( INPUT_PROPS_PATH, StubJsonI18N.CODICE_LINGUA_IT );
			ExcelPropsI18N excel = new ExcelPropsI18N();
			File inputPropsSummaryPath = new File( INPUT_PROPERTIES_SUMMARY_PATH+lang+"_summary.xml");
			int label = 0;

			for ( File currentInputItFile : inputJsonPathIt.listFiles() ) {
				FileReader inputTradOld = null;
				File currentInputLabelFile = new File( currentInputItFile.getCanonicalPath().replaceAll( "\\\\"+StubJsonI18N.CODICE_LINGUA_IT+"\\\\"+StubJsonI18N.CODICE_LINGUA_IT , "\\\\label\\\\label" ) );
				if ( StringUtils.isNotEmpty(lang) ) {
					File currentInputTradFile = new File( currentInputItFile.getCanonicalPath().replaceAll( "\\\\"+StubJsonI18N.CODICE_LINGUA_IT+"\\\\"+StubJsonI18N.CODICE_LINGUA_IT , "\\\\"+lang+"\\\\"+lang ) );
					inputTradOld = new FileReader(currentInputTradFile);
				}
				try ( FileReader inputIt = new FileReader(currentInputItFile);
						FileReader inputLabel = new FileReader(currentInputLabelFile);
						FileReader inputPropsSummary = new FileReader(inputPropsSummaryPath);) {
						if (label == 0) {
							excel.createSummary(workbook, inputPropsSummary);
							label=1;
						}
					String sheetName = currentInputItFile.getName().replace( ".xml" , "" ).replace( "it_" , "" );
					if (StringUtils.isNotEmpty(lang) && inputTradOld!=null) {
						excel.createSheet(workbook, sheetName, inputIt, inputLabel, inputTradOld, lang ); 
					} else {
						excel.createSheet(workbook, sheetName, inputIt, inputLabel ); 
					}
				}
			}
			workbook.write( outputXlsWriter );
		} catch (Exception e) {
			logger.error( "Errore : "+e, e );
		}
	}
	
	
}
