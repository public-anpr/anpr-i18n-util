package it.sogei.anpr.util.i18n.json;

import java.io.IOException;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.xmlbeans.impl.common.ReaderInputStream;
import org.fugerit.java.core.lang.helpers.StringUtils;
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
	public Sheet createSheet( Workbook workbook, String nomeSheet, Reader propTesti, Reader propEtichette, Reader propTradOld, String lang )  {
		Sheet sheet = workbook.createSheet( nomeSheet );
		logger.info( "generazione sheet : {}", nomeSheet );
		int rowNum = 0;
		Row rigaEtichette = sheet.createRow(rowNum);
		Cell cell1 = rigaEtichette.createCell(0);
		cell1.setCellValue("PERCORSO");
		Cell cell2 = rigaEtichette.createCell(1);
		cell2.setCellValue("DESCRIZIONE");
		Cell cell3 = rigaEtichette.createCell(2);
		cell3.setCellValue("TESTO");
		Cell cell4 = rigaEtichette.createCell(3);
		cell4.setCellValue("TRADUZIONE");
		if (StringUtils.isNotEmpty(lang)) {
			Cell cell5 = rigaEtichette.createCell(4);
			cell5.setCellValue("TRADUZIONE PRECEDENTE");
		}

		rowNum++;
		try {
			SortedProperties propText = new  SortedProperties();
			propText.loadFromXML(new ReaderInputStream(propTesti, "UTF-8"));
			SortedProperties propLabel = new  SortedProperties();
			propLabel.loadFromXML(new ReaderInputStream(propEtichette, "UTF-8"));
			SortedProperties propTrad = new  SortedProperties();
			if (propTradOld != null && StringUtils.isNotEmpty(lang)) {
				propTrad.loadFromXML(new ReaderInputStream(propTradOld, "UTF-8"));
			}
			Enumeration<?> keySet = propText.propertyNames();
			while (keySet.hasMoreElements()) {
				String key = (String) keySet.nextElement();
				Row row = sheet.createRow(rowNum);
				Cell cellaPath = row.createCell(0);
				cellaPath.setCellValue(key.toString());
				Cell cellaLabel = row.createCell(1);
				cellaLabel.setCellValue(propLabel.getProperty(key));
				Cell cellaText = row.createCell(2);
				cellaText.setCellValue(propText.getProperty(key));
				if (StringUtils.isNotEmpty(lang) && propTradOld != null) {
					Cell cellaTradOld = row.createCell(4);
					cellaTradOld.setCellValue(propTrad.getProperty(key));
				}

				rowNum++;
			}
		} catch (IOException e) {
			logger.error( "Errore : "+e, e );
		}
		return sheet;
	}
	
	public Sheet createSheet( Workbook workbook, String nomeSheet, Reader propTesti, Reader propEtichette )  {
		return this.createSheet(workbook, nomeSheet, propTesti, propEtichette, null, null);
	}	
	
	public Sheet createSummary( Workbook workbook, Reader prop ) {
		String nomeSheet = "summary";
		Sheet sheet = workbook.createSheet( nomeSheet );
		logger.info( "generazione sheet : {}", nomeSheet );
		String[] etichette = {"LINGUA TRADUZIONE","PATH ORIGINALE ITALIANO","PATH ETICHETTE","PATH TRADUZIONI PRECEDENTI","SHEET LIST"};
		for (int index=0; index < etichette.length; index++) {
			Row rigaEtichette = sheet.createRow(index);
			Cell cella = rigaEtichette.createCell(0);
			cella.setCellValue(etichette[index]);
		}
		try {
			SortedProperties propSummary = new  SortedProperties();
			propSummary.loadFromXML(new ReaderInputStream(prop, "UTF-8"));
			int rowNum=0;
			Enumeration<?> keySet = propSummary.propertyNames();
			while (keySet.hasMoreElements()) {
				String key = (String) keySet.nextElement();
				if(sheet.getRow(rowNum) == null) {
					Row row = sheet.createRow(rowNum);
				}
				Row row = sheet.getRow(rowNum);
				Cell cella = row.createCell(1);
				cella.setCellValue(propSummary.getProperty(key));
				rowNum++;
			}
		} catch (IOException e) {
			logger.error( "Errore : "+e, e );
		}
		return sheet;
	}

}
