package com.holub.database;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.holub.tools.ArrayIterator;

/**
 * @author yesol
 * 
 * Test exporting table to xml file
 * 
 * The output file for a xml table called "orders" with
 * columns "item," "quantity," and "date" would look
 * like this:
 *
 *	<PRE>
	<?xml version="1.0" encoding="EUC-KR"?>
	<table>
	<metadata>
		<tableName>orders</tableName>
		<columnName>
			<item>item</item>
			<item>quantity</item>
			<item>date</item>
		</columnName>
	</metadata>	
	<data>
	        <row>
	                <item>E16-25A</item>
	                <item>16</item>
	                <item>2020/9/1</item>
	        </row>
	        <row>
	                <item>E16-25B</item>
	                <item>20</item>
	                <item>2020/11/17</item>
	        </row>
	</data>
	</table>
 *	</PRE>
 *
 * @see XMLExporter
 */
@DisplayName("XML Exporter Test 클래스")
class XMLExporterTest {
	
	private static final File path = new File("c:/dp2020/orders.xml");
	private static Table orders;
	private static String tableName = "orders";
	private static String[] columnNames = { "item", "quantity", "date" };
	
	private LinkedList<Object[]> rowSet = new LinkedList<>();
	private Object[][] dataArr = {
			new Object[] { "E16-25A", "16", "2020/9/1" },
			new Object[] { "E16-25B", "20", "2020/11/17" }
	};
	
	private StringBuilder readFile() throws IOException {
		Reader in = new FileReader(path);
    	BufferedReader bufReader = new BufferedReader(in);
    	String line = "";
    	StringBuilder readXML = new StringBuilder();
    	while((line = bufReader.readLine()) != null) readXML.append(line);
    	bufReader.close();
    	return readXML;
	}
	
	@BeforeAll
	static void create_test_table() throws IOException {
		orders = TableFactory.create(tableName, columnNames);
		System.out.println(tableName + " table is created");
	}
	
	@AfterAll
	static void delete_xml_file() {
		if(path.exists()) {
			path.delete();
			System.out.println(path + " is deleted");
		}
	}
	
	@AfterEach
	void delete_table_data() {
		Cursor current = orders.rows();
		while (current.advance()) {
			current.delete();
		}
		System.out.println(tableName + " table data all deleted");
	}
	
	@DisplayName("XMLExporter 함수 Test : startTable()")
	@Test
	void test_startTable() throws IOException {
		// given
		Writer out = new FileWriter(path);
		XMLExporter xmlExporter = new XMLExporter(out);
		
		// when
		xmlExporter.startTable();
		out.close();
		
		// then
		StringBuilder readXML = readFile();
    	assertEquals(readXML.toString(), "<?xml version=\"1.0\" encoding=\"EUC-KR\"?><table>");
	}
	
	@DisplayName("XMLExporter 함수 Test : storeMetadata()")
	@Test
	void test_storeMetadata() throws IOException {
		// given
		Writer out = new FileWriter(path);
		XMLExporter xmlExporter = new XMLExporter(out);
		
    	// when
    	xmlExporter.storeMetadata(tableName, columnNames.length, 0, new ArrayIterator(columnNames));
    	out.close();
    	
    	// then
    	StringBuilder readXML = readFile();
    	StringBuilder answerXML = new StringBuilder();
    	answerXML.append(String.format("<metadata><tableName>%s</tableName><columnName>", tableName));
    	for(String columnName : columnNames) {
    		answerXML.append(String.format("<item>%s</item>", columnName.toString()));
    	}
    	answerXML.append("</columnName></metadata>");
    	
    	assertEquals(readXML.toString(), answerXML.toString());
	}
	
	
    @DisplayName("XMLExporter 함수 Test : tableName이 비어있는 경우 테이블 명이 <anonymous> 인지")
    @Test
    void test_storeMetadata_with_empty_tablename() throws IOException {
		// given
		Writer out = new FileWriter(path);
		XMLExporter xmlExporter = new XMLExporter(out);
		
    	// when
    	String tableNameNull = null;
    	xmlExporter.storeMetadata(tableNameNull, columnNames.length, 0, new ArrayIterator(columnNames));
    	out.close();
    	
    	// then
    	StringBuilder readXML = readFile();
    	StringBuilder answerXML = new StringBuilder();
    	answerXML.append("<metadata><tableName><anonymous></tableName><columnName>");
    	for(String columnName : columnNames) {
    		answerXML.append(String.format("<item>%s</item>", columnName.toString()));
    	}
    	answerXML.append("</columnName></metadata>");
    	
    	assertEquals(readXML.toString(), answerXML.toString());
    }
	
	
	@DisplayName("XMLExporter 함수 Test : storeRow()")
	@Test
	void test_storeRow() throws IOException {
		// given
		Writer out = new FileWriter(path);
		XMLExporter xmlExporter = new XMLExporter(out);
		
		// when
    	for(Object[] row : dataArr) {
    		xmlExporter.storeRow(new ArrayIterator(row));
    	}
    	out.close();
    	
    	// then
    	StringBuilder readXML = readFile();
    	StringBuilder answerXML = new StringBuilder();
    	answerXML.append("<data>");
    	for(Object[] row : dataArr) {
    		answerXML.append("<row>");
    		for(Object data : row) {
    			answerXML.append(String.format("<item>%s</item>", data.toString()));
    		}
    		answerXML.append("</row>");
    	}
    	assertEquals(readXML.toString(), answerXML.toString());
	}
	
	
	@DisplayName("XMLExporter 함수 Test : endTable()")
	@Test
	void test_endTable() throws IOException {
		// given
		Writer out = new FileWriter(path);
		XMLExporter xmlExporter = new XMLExporter(out);
		
		// when
		xmlExporter.endTable();
		out.close();
		
		// then
		StringBuilder readXML = readFile();
		assertEquals(readXML.toString(), "</table>");
	}
	
	
    @DisplayName("ConcreteTable의 export 함수로 xml export가 성공하여 파일이 생성되었는지")
    @Test
    void export_empty_table() throws IOException {
    	// given
    	Writer out = new FileWriter(path);
    	
    	// when
    	orders.export(new XMLExporter(out));
    	out.close();
    	
    	// then
    	File file = new File(path.toString());
    	assertTrue(file.exists());
    }
    
    
    @DisplayName("빈 table을 export했을 때  <Data> Tag가 없는지")
    @Test
    void check_exported_empty_content() throws IOException{
    	// given
    	Writer out = new FileWriter(path);
    	
    	// when
    	orders.export(new XMLExporter(out));
    	out.close();
    	
    	// then
    	StringBuilder readXML = readFile();
    	StringBuilder answerXML = new StringBuilder();
    	
    	answerXML.append("<?xml version=\"1.0\" encoding=\"EUC-KR\"?><table>");
    	answerXML.append(String.format("<metadata><tableName>%s</tableName><columnName>", tableName));
    	for(String columnName : columnNames) {
    		answerXML.append(String.format("<item>%s</item>", columnName.toString()));
    	}
    	answerXML.append("</columnName></metadata></table>");
    	
    	assertEquals(readXML.toString(), answerXML.toString());
    }
    

    @DisplayName("ConcreteTable의 export 함수로 export된 데이터 내용이 table에 있는 내용과 동일한지")
    @Test
    void check_exported_content() throws IOException {
    	// given
    	Writer out = new FileWriter(path);
    	for(Object[] data : dataArr) orders.insert(data);
    	
    	// when
    	orders.export(new XMLExporter(out));
    	out.close();
    	
    	// then
    	StringBuilder readXML = readFile();
    	StringBuilder answerXML = new StringBuilder();
    	
    	answerXML.append("<?xml version=\"1.0\" encoding=\"EUC-KR\"?><table>");
    	answerXML.append(String.format("<metadata><tableName>%s</tableName><columnName>", tableName));
    	for(String columnName : columnNames) {
    		answerXML.append(String.format("<item>%s</item>", columnName.toString()));
    	}
    	answerXML.append("</columnName></metadata><data>");
    	for(Object[] row : dataArr) {
    		answerXML.append("<row>");
    		for(Object data : row) {
    			answerXML.append(String.format("<item>%s</item>", data.toString()));
    		}
    		answerXML.append("</row>");
    	}
    	answerXML.append("</data></table>");
    	
    	assertEquals(readXML.toString(), answerXML.toString());
    }
    
    
    @DisplayName("Template Method Pattern 작동 확인")
    @Test
    void test_template_method() throws IOException {
    	
    	// given
    	Writer out = new FileWriter(path);
    	
    	for(Object[] data : dataArr) rowSet.add(data);
    	
    	// when
    	XMLExporter xmlExporter = new XMLExporter(out);
    	xmlExporter.callExportProcess(tableName, columnNames.length, rowSet.size(), new ArrayIterator(columnNames), rowSet);
    	out.close();
    	
    	// then
    	StringBuilder readXML = readFile();
    	StringBuilder answerXML = new StringBuilder();
    	
    	answerXML.append("<?xml version=\"1.0\" encoding=\"EUC-KR\"?><table>");
    	answerXML.append(String.format("<metadata><tableName>%s</tableName><columnName>", tableName));
    	for(String columnName : columnNames) {
    		answerXML.append(String.format("<item>%s</item>", columnName.toString()));
    	}
    	answerXML.append("</columnName></metadata><data>");
    	for(Object[] row : dataArr) {
    		answerXML.append("<row>");
    		for(Object data : row) {
    			answerXML.append(String.format("<item>%s</item>", data.toString()));
    		}
    		answerXML.append("</row>");
    	}
    	answerXML.append("</data></table>");
    	
    	assertEquals(readXML.toString(), answerXML.toString());
    	
    }
}

