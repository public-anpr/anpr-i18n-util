package it.sogei.anpr.util.i18n.json;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.fugerit.java.core.cfg.ConfigException;
import org.fugerit.java.core.cli.ArgUtils;
import org.fugerit.java.core.io.StreamIO;
import org.fugerit.java.core.lang.helpers.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StubJsonI18N {

	public static final String ARG_CODICE_LINGUA = "codice-lingua";
	public static final String ARG_INPUT_PATH = "input-path";
	public static final String ARG_OUTPUT_PATH = "output-path";
	
	/*

	Genera gli stub JSON in un'altra lingua.
	Ogni proprietà del JSON sarà nel formato : 
	valore -> valore (DE)

	Ecco dei parametri di esempio : 

	--codice-lingua de
	--input-path ..\anpr-sc-home-fe\src\common\locales\it
	--output-path ..\anpr-sc-home-fe\src\common\locales\de

	 */
	public static void main( String[] args ) {
		try {
			Properties params = ArgUtils.getArgs( args, true );
			String codiceLingua = params.getProperty( ARG_CODICE_LINGUA );
			String inputPath = params.getProperty( ARG_INPUT_PATH );
			String outputPath = params.getProperty( ARG_OUTPUT_PATH );
			if ( StringUtils.isEmpty( codiceLingua ) || StringUtils.isEmpty( inputPath ) || StringUtils.isEmpty( outputPath ) ) {
				throw new ConfigException( "Devi fornire i parametri ["+ARG_CODICE_LINGUA+","+ARG_INPUT_PATH+","+ARG_OUTPUT_PATH+"]" );
			} else {
				File jsonFileInputPath = new File( inputPath );
				File jsonFileOutputPath = new File( outputPath );
				if ( !jsonFileInputPath.isDirectory() || !jsonFileOutputPath.isDirectory() ) {
					throw new ConfigException( "I percorsi ["+ARG_INPUT_PATH+","+ARG_OUTPUT_PATH+"] devono essere cartelle esistenti" );	
				} else {
					StubJsonI18N stub = new StubJsonI18N();
					stub.generaStubLinguaTuttiFile(jsonFileInputPath, jsonFileOutputPath, codiceLingua.toUpperCase());
					logger.info( "Generazione ok!" );
				}
			}
		} catch (ConfigException e) {
			logger.warn( e.getMessage() );
		} catch (Exception e) {
			logger.error( "Errore : "+e, e );
		}
	}
	
	public static final String CODICE_LINGUA_IT = "it";
	public static final String CODICE_LINGUA_DE = "de";
	
	private final static Logger logger = LoggerFactory.getLogger( StubJsonI18N.class );

	private static void handle( LinkedHashMap<String, Object> map, String lingua ) throws Exception {
		for ( String key : map.keySet() ) {
			Object value = map.get( key );
			if ( value instanceof String ) {
				map.put( key , ((String)value)+" ("+lingua+")" );
			} else if ( value instanceof LinkedHashMap ) {
				@SuppressWarnings("unchecked")
				LinkedHashMap<String, Object> kid = (LinkedHashMap<String, Object>) value;
				handle( kid , lingua );
			} else {
				logger.info( "wrong type -> {}", value );
			}
		}
	}
	
	public void generaStubLinguaTuttiFile( File jsonFileInputPath, File jsonFileOutputPath, String codiceLingua ) throws Exception {
		for ( File currentInput : jsonFileInputPath.listFiles() ) {
			if ( currentInput.getName().endsWith( "json" ) ) {
				String outputName = currentInput.getName().replaceAll( CODICE_LINGUA_IT , codiceLingua );
				File currentOutput = new File( jsonFileOutputPath, outputName );
				try ( Reader reader = new FileReader( currentInput );
						Writer writer = new FileWriter( currentOutput ) ) {
					logger.info( "input -> {}", currentInput );
					StubJsonI18N json = new StubJsonI18N();
					json.generaStubLingua( reader , writer, codiceLingua.toUpperCase() );
					logger.info( "output -> {}", currentOutput );
				} catch (Exception e) {
					logger.error( "Errore : "+e, e );
				}		
			}
		}
	}
	
	public void generaStubLingua( Reader reader, Writer writer, String lingua ) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		try {
			@SuppressWarnings("unchecked")
			LinkedHashMap<String, Object> map = mapper.readValue( StreamIO.readString( reader ) , LinkedHashMap.class );
			handle(map, lingua);
			mapper.writerWithDefaultPrettyPrinter().writeValue( writer , map );
			logger.info( "TEST -> {}", map );
		} catch (Exception e) {
			logger.error( "Errore : "+e, e );
			throw new ConfigException( e );
		}
	}
	
}
