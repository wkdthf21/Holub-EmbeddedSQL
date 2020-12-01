/**
 * 
 */
package com.holub.database;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.holub.tools.ArrayIterator;

/**
 * @author wkdthf21
 * 
 * Test exporting table to HTML file 
 * 
 * The output file for a HTML table called "name" with
 * columns "first," "last," and "addrId" would look
 * like this:
 * 
 *			name
 *	first	last	addrId
 *	Fred	Flintstone	1
 *	Wilma	Flintstone	2
 *	Allen	Holub	0
 *	
 * @see HTMLExporter
 */
@DisplayName("HTML Exporter Test 클래스")
class HTMLExporterTest {
	
	private static final File path = new File("c:/dp2020/people.html");
	private static Table people;
	private static String tableName = "people";
	private static String[] columnNames = { "last", "first", "addrId" };
	
	
	private LinkedList<Object[]> rowSet = new LinkedList<>();
	private Object[][] dataArr = {
			new Object[] { "Fred", "Flintstone", "1" },
			new Object[] { "Wilma", "Flintstone", "2" },
			new Object[] { "Allen", "Holub", "0" }
	};
	
	
	private StringBuilder readFile() throws IOException {
		Reader in = new FileReader(path);
    	BufferedReader bufReader = new BufferedReader(in);
    	String line = "";
    	StringBuilder readHTML = new StringBuilder();
    	while((line = bufReader.readLine()) != null) readHTML.append(line);
    	bufReader.close();
    	return readHTML;
	}
	
	
	@BeforeAll
	static void create_people_table() throws IOException {
		people = TableFactory.create(tableName, columnNames);
		System.out.println(tableName + " table is created");
	}
	
	@AfterAll
	static void delete_html_file() {
		if(path.exists()) {
			path.delete();
			System.out.println(path + " is deleted");
		}
	}
	
	@AfterEach
	void delete_table_data() {
		Cursor current = people.rows();
		while (current.advance()) {
			current.delete();
		}
		System.out.println(tableName + " table data all deleted");
	}
	
	
    @DisplayName("HTMLExporter 함수 Test : startTable()")
    @Test
    void test_start_table() throws IOException {
    	// given
    	Writer out = new FileWriter(path);
    	HTMLExporter htmlExporter = new HTMLExporter(out);
    	
    	// when
    	htmlExporter.startTable();
    	out.close();
    	
    	// then
    	StringBuilder readHtml = readFile();
    	assertEquals(readHtml.toString(), "<html><body><table border=\"1\" width =\"500\" height=\"300\" align =\"center\" >");
    	
    }
    
    
    @DisplayName("HTMLExporter 함수 Test : storeMetadata()")
    @Test
    void test_store_meta_data() throws IOException {
    	// given
    	Writer out = new FileWriter(path);
    	HTMLExporter htmlExporter = new HTMLExporter(out);
    	
    	// when
    	htmlExporter.storeMetadata(tableName, columnNames.length, 0, new ArrayIterator(columnNames));
    	out.close();
    	
    	// then
    	StringBuilder readHtml = readFile();
    	StringBuilder answerHtml = new StringBuilder();
    	
    	answerHtml.append(String.format("<thead><tr align =\"center\" height = \"50\"><p><td colspan = \"%s\"> %s </td></p></tr>", columnNames.length, tableName));
    	answerHtml.append("<tr align = center>");
    	for(String columnName : columnNames) {
    		answerHtml.append(String.format("<td>%s</td>", columnName.toString()));
    	}
    	answerHtml.append("</tr></thead>");
    	assertEquals(readHtml.toString(), answerHtml.toString());
    }

	
    @DisplayName("HTMLExporter 함수 Test : tableName이 비어있는 경우 테이블 명이 <anonymous> 인지")
    @Test
    void test_store_meath_data_with_empty_tablename() throws IOException {
    	// given
    	Writer out = new FileWriter(path);
    	HTMLExporter htmlExporter = new HTMLExporter(out);
    	
    	// when
    	String tableNameNull = null;
    	htmlExporter.storeMetadata(tableNameNull, columnNames.length, 0, new ArrayIterator(columnNames));
    	out.close();
    	
    	// then
    	StringBuilder readHtml = readFile();
    	StringBuilder answerHtml = new StringBuilder();
    	answerHtml.append(String.format("<thead><tr align =\"center\" height = \"50\"><p><td colspan = \"%s\"> <anonymous> </td></p></tr>", columnNames.length));
    	answerHtml.append("<tr align = center>");
    	for(String columnName : columnNames) {
    		answerHtml.append(String.format("<td>%s</td>", columnName.toString()));
    	}
    	answerHtml.append("</tr></thead>");
    	
    	assertEquals(readHtml.toString(), answerHtml.toString());
    }
    
    
    @DisplayName("HTMLExporter 함수 Test : storeRow()")
    @Test
    void test_store_row() throws IOException {
    	// given
    	Writer out = new FileWriter(path);
    	HTMLExporter htmlExporter = new HTMLExporter(out);
    	
    	// when
    	for(Object[] row : dataArr) {
    		htmlExporter.storeRow(new ArrayIterator(row));
    	}
    	
    	out.close();
    	
    	// then
    	StringBuilder readHtml = readFile();
    	StringBuilder answerHtml = new StringBuilder();
    	answerHtml.append("<tbody>");
    	for(Object[] row : dataArr) {
    		answerHtml.append("<tr align = center>");
    		for(Object data : row) {
    			answerHtml.append(String.format("<td>%s</td>", data.toString()));
    		}
    		answerHtml.append("</tr>");
    	}
    	assertEquals(answerHtml.toString(), readHtml.toString());
    }
    
    @DisplayName("HTMLExporter 함수 Test : endTable()")
    @Test
    void test_end_table() throws IOException {
    	// given
    	Writer out = new FileWriter(path);
    	HTMLExporter htmlExporter = new HTMLExporter(out);
    	
    	// when
    	htmlExporter.endTable();
    	out.close();
    	
    	// then
    	StringBuilder readHtml = readFile();
    	assertEquals(readHtml.toString(), "</table></body></html>");
    	
    }
    

    @DisplayName("ConcreteTable의 export 함수로 파일이 생성되는지")
    @Test
    void export_empty_table() throws IOException {
    	
    	// given
    	Writer out = new FileWriter(path);
    	
    	// when
    	people.export(new HTMLExporter(out));
    	out.close();
    	
    	// then
    	File file = new File(path.toString());
    	assertTrue(file.exists());
    	
    }
    
    @DisplayName("빈 table을 export했을 때 <tbody> tag가 없는지")
    @Test
    void check_exported_empty_content() throws IOException{
    	
    	// given
    	Writer out = new FileWriter(path);
    	
    	// when
    	people.export(new HTMLExporter(out));
    	out.close();
    	
    	// then
    	StringBuilder readHtml = readFile();
    	StringBuilder answerHtml = new StringBuilder();
    	answerHtml.append("<html><body><table border=\"1\" width =\"500\" height=\"300\" align =\"center\" >"); 
    	answerHtml.append(String.format("<thead><tr align =\"center\" height = \"50\"><p><td colspan = \"%s\"> %s </td></p></tr>", columnNames.length, tableName));
    	
    	answerHtml.append("<tr align = center>");
    	for(String columnName : columnNames) {
    		answerHtml.append(String.format("<td>%s</td>", columnName.toString()));
    	}
    	answerHtml.append("</tr></thead></table></body></html>");    	
    	assertEquals(answerHtml.toString(), readHtml.toString());
    }
    
    
    @DisplayName("ConcreteTable의 export 함수로 export된 데이터 내용이 table에 있는 내용과 동일한지")
    @Test
    void check_exported_content() throws IOException {
    	
    	// given
    	Writer out = new FileWriter(path);
    	for(Object[] data : dataArr)
    		people.insert(data);
    	
    	// when
    	people.export(new HTMLExporter(out));
    	out.close();
    	
    	// then
    	StringBuilder readHtml = readFile();
    	StringBuilder answerHtml = new StringBuilder();
    	answerHtml.append("<html><body><table border=\"1\" width =\"500\" height=\"300\" align =\"center\" >"); 
    	answerHtml.append(String.format("<thead><tr align =\"center\" height = \"50\"><p><td colspan = \"%s\"> %s </td></p></tr>", columnNames.length, tableName));
    	
    	answerHtml.append("<tr align = center>");
    	for(String columnName : columnNames) {
    		answerHtml.append(String.format("<td>%s</td>", columnName.toString()));
    	}
    	answerHtml.append("</tr></thead>");
    	
    	answerHtml.append("<tbody>");
    	for(Object[] row : dataArr) {
    		answerHtml.append("<tr align = center>");
    		for(Object data : row) {
    			answerHtml.append(String.format("<td>%s</td>", data.toString()));
    		}
    		answerHtml.append("</tr>");
    	}
    	answerHtml.append("</tbody></table></body></html>");
    	
    	assertEquals(answerHtml.toString(), readHtml.toString());
    	
    }
    
    
    @DisplayName("Template Method Pattern 작동 확인")
    @Test
    void test_template_method() throws IOException {
    	
    	// given
    	Writer out = new FileWriter(path);
    	
    	for(Object[] data : dataArr) rowSet.add(data);
    	
    	// when
    	HTMLExporter htmlExporter = new HTMLExporter(out);
    	htmlExporter.callExportProcess(tableName, columnNames.length, rowSet.size(), new ArrayIterator(columnNames), rowSet);
    	out.close();
    	
    	// then
    	StringBuilder readHtml = readFile();
    	StringBuilder answerHtml = new StringBuilder();
    	answerHtml.append("<html><body><table border=\"1\" width =\"500\" height=\"300\" align =\"center\" >"); 
    	answerHtml.append(String.format("<thead><tr align =\"center\" height = \"50\"><p><td colspan = \"%s\"> %s </td></p></tr>", columnNames.length, tableName));
    	
    	answerHtml.append("<tr align = center>");
    	for(String columnName : columnNames) {
    		answerHtml.append(String.format("<td>%s</td>", columnName.toString()));
    	}
    	answerHtml.append("</tr></thead>");
    	
    	answerHtml.append("<tbody>");
    	for(Object[] row : dataArr) {
    		answerHtml.append("<tr align = center>");
    		for(Object data : row) {
    			answerHtml.append(String.format("<td>%s</td>", data.toString()));
    		}
    		answerHtml.append("</tr>");
    	}
    	answerHtml.append("</tbody></table></body></html>");
    	
    	assertEquals(answerHtml.toString(), readHtml.toString());
    }

}
