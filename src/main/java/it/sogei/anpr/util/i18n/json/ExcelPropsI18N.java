package it.sogei.anpr.util.i18n.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;
import java.util.Set;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.xmlbeans.impl.common.ReaderInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelPropsI18N {

	private final static Logger logger = LoggerFactory.getLogger( ExcelPropsI18N.class );
	
	/**
	 * Aggiunge un foglio alla workbook con le seguenti modalità : 
	 * 
	 * Inserisce una riga per ogni proprietà nel json di input "jsonTesti"
	 * 
	 * colonna1 - percorso - (percorso della priorità in jsonTest, es. allegati.add-1 da it_allegati.json)
	 * colonna2 - descrizione - il valore della corrispondente proprietà in jsonLabel
	 * colonna3 - testo - il valore della corrispondente proprietà in jsonTest
	 * colonna4 - traduzione - vuota (a cura dell'utente)
	 * 
	 * @param workbook	il workbook a cui aggiungere il foglio
	 * @param nomeSheet	il nome del foglio da aggiungere 
	 * @param jsonTesti	il json di input con i test
	 * @param jsonLabel il json di input con le etichette
	 * 
	 * @return il foglio creato
	 */
	public Sheet createSheet( Workbook workbook, String nomeSheet, Reader propTesti, Reader propLabel )  {
		Sheet sheet = workbook.createSheet( nomeSheet );
		logger.info( "generazione sheet : {}", nomeSheet );
		
		try {
			Properties prop = new Properties();
			
			prop.loadFromXML(new ReaderInputStream(propTesti, "UTF-8"));
			
			Set<Object> keys = prop.keySet();
			
			for(Object k : keys) {
				System.out.println(k);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sheet;
	}

}
