/**
 * 
 */
package com.holub.database;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
	private static String[] columnNames = { "last", "first", "addrId" };
	private Object[][] dataArr = {
			new Object[] { "Fred", "Flintstone", "1" },
			new Object[] { "Wilma", "Flintstone", "2" },
			new Object[] { "Allen", "Holub", "0" }
	};
	
	@BeforeAll
	static void create_people_table() throws IOException {
		people = TableFactory.create("people", columnNames);
		System.out.println("people table is created");
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
		System.out.println("people table data all deleted");
	}
    

    @DisplayName("ConcreteTable의 export 함수로 html export가 성공하여 파일이 생성되었는지")
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
    	int width = dataArr.length;
    	String tableName = "people";
    	Reader in = new FileReader(path);
    	int cur = 0;
    	String read = "";
    	while((cur = in.read()) != -1) {
    		read += (char)cur;
    	}
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("<html><body><table border=\"1\" width =\"500\" height=\"300\" align =\"center\" >"); 
    	sb.append("<tr align =\"center\"><p><td colspan = \"" + width + "\"> " + tableName +  " </td></p></tr>");
    	
    	sb.append("<tr align = center>");
    	for(String columnName : columnNames) {
			sb.append("<td>");
			sb.append(columnName.toString());
			sb.append("</td>");
    	}
    	sb.append("</tr>");
    	
    	
    	for(Object[] row : dataArr) {
    		sb.append("<tr align = center>");
    		for(Object data : row) {
    			sb.append("<td>");
    			sb.append(data.toString());
    			sb.append("</td>");
    		}
    		sb.append("</tr>");
    	}
    	
    	sb.append("</table></body></html>");
    	
    	assertEquals(sb.toString(), read);
    	
    	in.close();
    }

}
