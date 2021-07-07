package it.sogei.anpr.util.i18n.json;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.fugerit.java.core.lang.helpers.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ExcelJsonI18N {

	private final static Logger logger = LoggerFactory.getLogger( ExcelJsonI18N.class );
	
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
	public Sheet createSheet( Workbook workbook, String nomeSheet, Reader jsonTesti, Reader jsonLabel ) {
		Sheet sheet = workbook.createSheet( nomeSheet );
		logger.info( "generazione sheet : {}", nomeSheet );
		int riga = 0;
		Row rigaEtichette = sheet.createRow(riga);
		Cell cell1 = rigaEtichette.createCell(0);
		cell1.setCellValue("PERCORSO");
		Cell cell2 = rigaEtichette.createCell(1);
		cell2.setCellValue("DESCRIZIONE");
		Cell cell3 = rigaEtichette.createCell(2);
		cell3.setCellValue("TESTO");
		Cell cell4 = rigaEtichette.createCell(3);
		cell4.setCellValue("TRADUZIONE");
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Map<String,?> mappaLable = mapper.readValue(jsonLabel, Map.class);
			Map<String,?> mappaTesti = mapper.readValue(jsonTesti, Map.class);
			Set<String> keys = mappaTesti.keySet();
 			riga++;
			riga = this.scorriMap(mappaTesti, mappaLable,riga,sheet,"");

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		return sheet;
	}
	
	private int scorriMap(Map mappaText,Map mappaLabel,int rowNum, Sheet sheet, String keyPrecedente) {
		Set<String> keys = mappaText.keySet();
		for (String k:keys) {
			if (mappaText.get(k) instanceof Map) {
				keyPrecedente = (StringUtils.isEmpty(keyPrecedente)) ? k : keyPrecedente+"."+k;
				this.scorriMap((Map)mappaText.get(k),(Map)mappaLabel.get(k), rowNum, sheet,keyPrecedente);
			} else if (mappaText.get(k) instanceof ArrayList<?>) {
				ArrayList<String> listText = (ArrayList<String>) mappaText.get(k);
				ArrayList<String> listLabel = (ArrayList<String>) mappaLabel.get(k);
				for (int i=0; i< listText.size(); i++) {
					Row row = sheet.createRow(rowNum);
					Cell cellaPath = row.createCell(0);
					if (StringUtils.isEmpty(keyPrecedente)) {
						cellaPath.setCellValue(k);
					} else {
						cellaPath.setCellValue(keyPrecedente+"."+k);
					}
					Cell cellaLabel = row.createCell(1);
					cellaLabel.setCellValue(listLabel.get(i));
					Cell cellaText = row.createCell(2);
					cellaText.setCellValue(listText.get(i));
					rowNum++;
				}
			} else {
				Row row = sheet.createRow(rowNum);
				Cell cellaPath = row.createCell(0);
				if (StringUtils.isEmpty(keyPrecedente)) {
					cellaPath.setCellValue(k);
				} else {
					cellaPath.setCellValue(keyPrecedente+"."+k);
				}
				Cell cellaLabel = row.createCell(1);
				cellaLabel.setCellValue((String)mappaLabel.get(k));
				Cell cellaText = row.createCell(2);
				cellaText.setCellValue((String)mappaText.get(k));
				rowNum++;
			}
		}
		return rowNum;
	}

	
}


