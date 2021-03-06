package easyCsvHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;


public class EasyCsvHandler {
	
	CSVPrinter printer;
	int numOfHeaderElements = 0;


	public void createCsvFile(String filePath, EasyCsvFile csvFile) throws Exception {
		createCsvFile(filePath, csvFile, ",");
	}
	
	public void createCsvFile(String filePath, EasyCsvFile csvFile, String delimiter) throws Exception {
		createFileWithHeader(filePath, csvFile.header, delimiter.charAt(0));
		writeRecordsInFile(csvFile.records);
		closeFile();
	}
	
	
	private void createFileWithHeader(String filePath, String[] header, char delimiter) throws IOException {
		BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath));
		CSVFormat format = CSVFormat.DEFAULT
				.withHeader(header)
				.withDelimiter(delimiter);
		
		numOfHeaderElements = header.length;
		printer = new CSVPrinter(writer, format);
	}

	private void writeRecordsInFile(List<String[]> records) throws Exception {
		for(String[] record : records) { 
			appendFile(record);
		}
	}
	
	private void appendFile(String[] record) throws Exception{
		if(numOfHeaderElements != record.length) throw new Exception("record length not equal to header length");
		printer.printRecord((Object[]) record);
	}
	
	private void closeFile() throws IOException {
		printer.flush();
	}
	

	public EasyCsvFile parseCsvFile(String filePath) throws IOException{
		return parseCsvFile(filePath, ",");
	}

		
	public EasyCsvFile parseCsvFile(String filePath, String delimiter) throws IOException {
		List<String[]> csvTable = parseFile(filePath, delimiter.charAt(0));
		return new EasyCsvFile(getHeader(csvTable), getRecords(csvTable));
	}
	
	
	private List<String[]> parseFile(String filePath, char delimiter) throws IOException {
		BufferedReader reader = Files.newBufferedReader(Paths.get(filePath));
		CSVFormat format = CSVFormat.DEFAULT
				.withDelimiter(delimiter)
				.withFirstRecordAsHeader()
				.withTrim();
		
		CSVParser parser = new CSVParser(reader, format);
		List<String[]> csvTable = new ArrayList<>();

		Map<String, Integer> headerMap = parser.getHeaderMap(); 
		csvTable.add(getValues(headerMap));
		
		for(CSVRecord record : parser.getRecords()) {
			csvTable.add(getValues(record));
		}
		
		parser.close();
		return csvTable;
	}
	
	private String[] getValues(Map<String,Integer> headerMap) {
		String[] headerElements = new String[headerMap.size()];
		
		
		for(String element : headerMap.keySet()) {
			int pos = headerMap.get(element);
			headerElements[pos] = element;
		}
		
		return headerElements;
	}
	
	private String[] getValues(CSVRecord record) {
		String[] recordElements = new String[record.size()];
		
		for(int i = 0 ; i < record.size() ; i++) {
			recordElements[i] = record.get(i);
		}
		
		return recordElements;
	}
	
	private String[] getHeader(List<String[]> csvTable) {
		return csvTable.get(0);
	}
	
	private List<String[]> getRecords(List<String[]> csvTable) {
		 return new ArrayList<String[]>(csvTable.subList(1, csvTable.size()));
	}
	
	
	
	public static List<String[]> findRecords(EasyCsvFile csvFile, String headerString, String element)  {
		int headerIndex = getHeaderIndex(csvFile, headerString);
		return findRecords(csvFile, headerIndex, element);
	}

	
	
	public static int getHeaderIndex(EasyCsvFile csvFile, String headerString) {
		return getHeaderIndex(csvFile.header, headerString);
	}
	
	
	
	public static int getHeaderIndex(String[] header, String headerString) {
		int headerIndex = -1;
		for(int i = 0 ; i < header.length ; i++) {
			if(header[i].equals(headerString)) {
				headerIndex = i;
				break;
			}
		}
		
		return headerIndex;
	}
	
	
	
	public static List<String[]> findRecords(EasyCsvFile csvFile, int headerIndex, String element) {
		List<String[]> foundRecords = new ArrayList<>();
		
		if(headerIndex < 0 || headerIndex > csvFile.header.length) return foundRecords;

		for(String[] record : csvFile.records) {
			if(record[headerIndex].equals(element)) {
				foundRecords.add(record);
			}
		}
		
		return foundRecords;
	}
	
}
