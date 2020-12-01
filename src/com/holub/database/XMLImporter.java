package com.holub.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.holub.tools.ArrayIterator;


/***
 *	Pass this importer to a {@link Table} constructor (such
 *	as
 *	{link com.holub.database.ConcreteTable#ConcreteTable(Table.Importer)}
 *	to initialize
 *	a <code>Table</code> from XML file. 
 *  
 *  For example:
 *	<PRE>
 *	Reader in = new FileReader( "orders.xml" );
 *	orders = new ConcreteTable( new CSVImporter(in) );
 *	in.close();
 *	</PRE>
 *	The input file for a table called "name" with
 *	columns "first," "last," and "addrId" would look
 *	like this:
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
 * @see Table
 * @see Table.Importer
 * @see XMLExporter
 */
public class XMLImporter extends MarkUpImporter{
	
	private BufferedReader  in;			// null once end-of-file reached
	private String[]        columnNames;
	private String          tableName;
	private LinkedList<Object[]> rowSet;
	private Document doc;
	
	public XMLImporter(Reader in) throws SAXException, IOException, ParserConfigurationException {
		this.in = in instanceof BufferedReader
						? (BufferedReader)in
		                : new BufferedReader(in)
		                ;
		
		
		this.rowSet = new LinkedList<>();
		
		DocumentBuilderFactory factory  =  DocumentBuilderFactory.newInstance();
		DocumentBuilder builder    =  factory.newDocumentBuilder();
		String line = "";
		StringBuilder xml = new StringBuilder();
		while((line = this.in.readLine()) != null) {
			xml.append(line);
		}
		doc = builder.parse(new InputSource(new StringReader(xml.toString())));
	}
	
	
	
	/*******************************************************************
	 * 	return function list */
	@Override
	public String loadTableName() throws IOException {
		// TODO Auto-generated method stub
		return tableName;
	}

	@Override
	public int loadWidth() throws IOException {
		// TODO Auto-generated method stub
		return columnNames.length;
	}

	@Override
	public Iterator loadColumnNames() throws IOException {
		// TODO Auto-generated method stub
		return new ArrayIterator(columnNames);
	}
	
	

	/*******************************************************************
	 * 	override function list */	
	/**
	* @methodName : parseStart
	* @Author : wkdthf21 
	* @return : 
	* @Desc : parse tableName & columnNames & width from XML
	*/
	@Override
	public void parseStart() {
		// TODO Auto-generated method stub	
		tableName = doc.getElementsByTagName("tableName").item(0).getTextContent();
		
		NodeList columnList = doc.getElementsByTagName("columnName");
		Node node = columnList.item(0);
		if(node.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) node;
			NodeList itemList = element.getElementsByTagName("item");
			columnNames = new String[itemList.getLength()];
			for(int i = 0; i < itemList.getLength(); i++) {
				columnNames[i] = itemList.item(i).getTextContent();
			}
		}
	}
	
	
	/**
	* @methodName : parseRows
	* @Author : wkdthf21 
	* @return : 
	* @Desc : parse data rows from XML and make rowList
	*/
	@Override
	public void parseRows() {
		// TODO Auto-generated method stub
		NodeList rowList = doc.getElementsByTagName("row");
		for(int i = 0; i < rowList.getLength(); i++) {
			Node node = rowList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				NodeList itemList = element.getElementsByTagName("item");
				Object[] row = new Object[columnNames.length];
				for(int j = 0; j < itemList.getLength(); j++) {
					row[j] = itemList.item(j).getTextContent();
				}
				rowSet.add(row);
			}
		}
	}
	
	/**
	* @methodName : returnRow
	* @Author : wkdthf21 
	* @return : Iterator
	* @Desc : return row data from rowList using rowCnt
	*/
	@Override
	public Iterator returnRow(int rowIdx) {
		// TODO Auto-generated method stub
		if(rowSet.size() <= rowIdx) return null;
		return new ArrayIterator(rowSet.get(rowIdx));
	}

}
