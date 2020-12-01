package com.holub.database;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 * @author yesol
 *
 * Test importing table from xml file
 * [! Move all files in Dbase folder to C:/dp2020 folder before testing]
 * 
 * xml file example :
 * <?xml version="1.0" encoding="EUC-KR"?>
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
	
 *
 */
class XMLImporterTest {

	private static Reader in;
	private static String tableName = "orders";
	private static String fileName = "c:/dp2020/" + tableName + ".xml";
	private Table table;
	private String[] columnNames = { "item", "quantity", "date" };
	
	private LinkedList<Object[]> rowSet = new LinkedList<>();
	private Object[][] dataArr = {
			new Object[] { "E16-25A", "16", "2020/9/1" },
			new Object[] { "E16-25B", "20", "2020/11/17" }
	};

	
	@DisplayName("XMLImporter - startTable() 테스트 : tableName, columnNames, width가 제대로 파싱되는지")
	@Test
	void test_startTable() throws IOException, SAXException, ParserConfigurationException {
		// given
		in = new FileReader( fileName );
		XMLImporter xmlImporter = new XMLImporter(in);
		
		// when
		xmlImporter.startTable();
		
		// then
		Iterator iter = xmlImporter.loadColumnNames();
		for(String columnName : columnNames) {
			assertTrue(iter.hasNext());
			assertEquals(columnName, iter.next());
		}
		assertEquals(xmlImporter.loadTableName(), tableName);
		assertEquals(xmlImporter.loadWidth(), columnNames.length);
	}
	
	
	@DisplayName("XMLImporter - parseRows() 테스트 : Data의 Row들이 제대로 파싱되는지")
	@Test
	void test_parseRows() throws SAXException, IOException, ParserConfigurationException {
		// given
		in = new FileReader( fileName );
		XMLImporter xmlImporter = new XMLImporter(in);
		xmlImporter.parseStart();
		
		// when
		xmlImporter.parseRows();
		
		// then
		int idx = 0;
		for(int i = 0; i < dataArr.length; i++) {
			Object[] expectedData = dataArr[i];
			Iterator actualData = xmlImporter.returnRow(i);
			idx = 0;
			while(actualData.hasNext()) {
				assertEquals(actualData.next(), expectedData[idx++]);
			}
		}
	}
	
	
	@DisplayName("XMLImporter가 ConcreteTable의 생성자에서 제대로 동작하여 Table 데이터가 정상적인지")
	@Test
	void test_XMLImporter_in_concreteTable_construction() throws SAXException, IOException, ParserConfigurationException {
		// given
		in = new FileReader( fileName );
		XMLImporter xmlImporter = new XMLImporter(in);
		
		// when
		table = new ConcreteTable(xmlImporter);
		
		// then
		Cursor current = table.rows();
		for(int i = 0; i < dataArr.length; i++) {
			Object[] data = dataArr[i];
			// row 개수가 같아야 한다
			assertTrue(current.advance());
			int j = 0;
			for (Iterator columns = current.columns(); columns.hasNext();)
				assertEquals(data[j++], columns.next());
		}
		assertEquals(xmlImporter.loadTableName(), tableName);
		assertEquals(xmlImporter.loadWidth(), columnNames.length);
	}
}
