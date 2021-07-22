package it.sogei.anpr.util.i18n.json;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
	public Sheet createSheet( Workbook workbook, String nomeSheet, Reader jsonTesti, Reader jsonLabel, Reader jsonTradOld, String lang) {
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
		if (StringUtils.isNotEmpty(lang)) {
			Cell cell5 = rigaEtichette.createCell(4);
			cell5.setCellValue("TRADUZIONE PRECEDENTE");
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			LinkedHashMap<String,?> mappaLable = mapper.readValue(jsonLabel, LinkedHashMap.class);
			LinkedHashMap<String,?> mappaTesti = mapper.readValue(jsonTesti, LinkedHashMap.class);
			Set<String> keys = mappaTesti.keySet();
 			riga++;
 			if (StringUtils.isNotEmpty(lang) && jsonTradOld != null) {
 				LinkedHashMap<String,?> mappaTradOld = mapper.readValue(jsonTradOld, LinkedHashMap.class);
 				riga = this.scorriMap(mappaTesti, mappaLable, mappaTradOld, riga, sheet, "");
 			} else {
 				riga = this.scorriMap(mappaTesti, mappaLable,riga,sheet,"");
 			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return sheet;
	}
	
	public Sheet createSheet( Workbook workbook, String nomeSheet, Reader jsonTesti, Reader jsonLabel) {
		return this.createSheet(workbook, nomeSheet, jsonTesti, jsonLabel, null, null);
	}

	
	private int scorriMap(Map mappaText,Map mappaLabel,int rowNum, Sheet sheet, String keyPrecedente) {
		Set<String> keys = mappaText.keySet();
		for (String k:keys) {
			
			if (mappaText.get(k) instanceof Map) {
				keyPrecedente = k+".";
				rowNum = this.scorriMap((Map)mappaText.get(k),(Map)mappaLabel.get(k), rowNum, sheet,keyPrecedente);
			} else if (mappaText.get(k) instanceof ArrayList<?>) {
				ArrayList<String> listText = (ArrayList<String>) mappaText.get(k);
				ArrayList<String> listLabel = (ArrayList<String>) mappaLabel.get(k);
				for (int i=0; i< listText.size(); i++) {
					Row row = sheet.createRow(rowNum);
					Cell cellaPath = row.createCell(0);
					if (StringUtils.isEmpty(keyPrecedente)) {
						cellaPath.setCellValue(k);
					} else {
						cellaPath.setCellValue(keyPrecedente+k);
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
					cellaPath.setCellValue(keyPrecedente+k);
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
	
	private int scorriMap(Map mappaText,Map mappaLabel, Map mappaTradOld,int rowNum, Sheet sheet, String keyPrecedente) {
		Set<String> keys = mappaText.keySet();
		for (String k:keys) {
			
			if (mappaText.get(k) instanceof Map) {
				keyPrecedente = k+".";
				rowNum = this.scorriMap((Map)mappaText.get(k),(Map)mappaLabel.get(k),(Map)mappaTradOld.get(k), rowNum, sheet,keyPrecedente);
			} else if (mappaText.get(k) instanceof ArrayList<?>) {
				ArrayList<String> listText = (ArrayList<String>) mappaText.get(k);
				ArrayList<String> listLabel = (ArrayList<String>) mappaLabel.get(k);
				ArrayList<String> listTradOld = (ArrayList<String>) mappaTradOld.get(k);
				for (int i=0; i< listText.size(); i++) {
					Row row = sheet.createRow(rowNum);
					Cell cellaPath = row.createCell(0);
					if (StringUtils.isEmpty(keyPrecedente)) {
						cellaPath.setCellValue(k);
					} else {
						cellaPath.setCellValue(keyPrecedente+k);
					}
					Cell cellaLabel = row.createCell(1);
					cellaLabel.setCellValue(listLabel.get(i));
					Cell cellaText = row.createCell(4);
					cellaText.setCellValue(listTradOld.get(i));
					Cell cellaTradOld = row.createCell(4);
					cellaTradOld.setCellValue(listTradOld.get(i));
					rowNum++;
				}
			} else {
				Row row = sheet.createRow(rowNum);
				Cell cellaPath = row.createCell(0);
				if (StringUtils.isEmpty(keyPrecedente)) {
					cellaPath.setCellValue(k);
				} else {
					cellaPath.setCellValue(keyPrecedente+k);
				}
				Cell cellaLabel = row.createCell(1);
				cellaLabel.setCellValue((String)mappaLabel.get(k));
				Cell cellaText = row.createCell(2);
				cellaText.setCellValue((String)mappaText.get(k));
				Cell cellaTradOld = row.createCell(4);
				cellaTradOld.setCellValue((String)mappaTradOld.get(k));
				rowNum++;
			}
		}
		return rowNum;
	}
	
	
	

	public Sheet createSummary( Workbook workbook, Reader jsonSummary ) {
		String nomeSheet = "summary";
		Sheet sheet = workbook.createSheet( nomeSheet );
		logger.info( "generazione sheet : {}", nomeSheet );
		String[] etichette = {"LINGUA TRADUZIONE","PATH ORIGINALE ITALIANO","PATH ETICHETTE","PATH TRADUZIONI PRECEDENTI","SHEET LIST"};
		for (int index=0; index < etichette.length; index++) {
			Row rigaEtichette = sheet.createRow(index);
			Cell cella = rigaEtichette.createCell(0);
			cella.setCellValue(etichette[index]);
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			LinkedHashMap<String,?> mappa = mapper.readValue(jsonSummary, LinkedHashMap.class);
			Set<String> keys = mappa.keySet();
			this.scorriMapSummary(mappa, sheet);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return sheet;
	}
	
	private void scorriMapSummary(Map mappaSummary, Sheet sheet) {
		Set<String> keys = mappaSummary.keySet();
		int rowNum = 0;
		for (String k:keys) {
			if (mappaSummary.get(k) instanceof Map) {
				Map<String,?> mapTemp = (Map<String,?>)mappaSummary.get(k);
				Set<String> keys2 = mapTemp.keySet();
				for (String k2:keys2) {
					if (sheet.getRow(rowNum) == null) {
						Row row = sheet.createRow(rowNum);
					} 
					Row row = sheet.getRow(rowNum);
					Cell cella1 = row.createCell(1);
					cella1.setCellValue(k2);
					Cell cella2 = row.createCell(2);
					cella2.setCellValue((String)mapTemp.get(k2));
					rowNum++;
				}
			} else {
				if (sheet.getRow(rowNum) == null) {
					Row row = sheet.createRow(rowNum);
				} 
				Row row = sheet.getRow(rowNum);				
				Cell cell = row.createCell(1);
				cell.setCellValue((String)mappaSummary.get(k));
				rowNum++;
			}
		}
	}
	
}


