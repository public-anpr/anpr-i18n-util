package test.it.sogei.anpr.util.i18n.json;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.sogei.anpr.util.i18n.json.StubJsonI18N;

/**
 * 


 * @author mttfranci
 *
 */
public class TestStubJsonI18N {

	private final static Logger logger = LoggerFactory.getLogger( TestStubJsonI18N.class );
	
	private final static String TARGET_BASE_PATH = "target/stub_json";
	
	private final static String INPUT_JSON_IT_PATH = "src/test/resources/input_json/it";
	
	public void testJsonParserWorker( String codiceLingua ) {
		File jsonFileInputPath = new File( INPUT_JSON_IT_PATH );
		File jsonFileOutputPath = new File( TARGET_BASE_PATH, codiceLingua );
		logger.info( "crea output dir -> {}", jsonFileOutputPath.mkdirs() );
		StubJsonI18N stub = new StubJsonI18N();
		try {
			stub.generaStubLinguaTuttiFile(jsonFileInputPath, jsonFileOutputPath, codiceLingua);
		} catch (Exception e) {
			String message = "Generazione fallita : "+e; 
			logger.error( message , e );
			fail( message );
		}
	}
	
	@Test
	public void testJsonParserDe() {
		this.testJsonParserWorker( "de" );
	}
	
	@Test
	public void testJsonParserEn() {
		this.testJsonParserWorker( "en" );
	}

	@Test
	public void testJsonParserLabel() {
		this.testJsonParserWorker( "label" );
	}
	
}
