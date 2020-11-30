/**
 * 
 */
package com.holub.database;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/***
 *	For example:
 *	<PRE>
 *	Table orders  = TableFactory.create( ... );
 *	//...
 *	Writer out = new FileWriter( "orders.xml" );
 *	people.export( new XMLExporter(out) );
 *	out.close();
 *	</PRE>
 *	The output file for a xml table called "orders" with
 *	columns "item," "quantity," and "date" would look
 *	like this:
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
 *	The metadata contains table name and column names
 *	The data contains rows and each row contains item in column order
 * @see Table
 * @see Table.Exporter
 * @see XMLImporter
 */
public class XMLExporter extends MarkUpExporter{

	
	private final Writer out;
	private 	  int	 width;
	private 	  int	 height;
	
	public XMLExporter(Writer out) {
		this.out = out;
	}
	
	public void startTable() throws IOException {
		out.write("<?xml version=\"1.0\" encoding=\"EUC-KR\"?>");
		out.write("<table>");
	}

	
	public void storeMetadata(String tableName, int width, int height, Iterator columnNames) throws IOException {
		this.width = width;
		this.height = height;
		tableName = tableName == null ? "<anonymous>" : tableName;
		
		out.write(String.format("<metadata><tableName>%s</tableName>", tableName));
		storeColumnName(columnNames);
		out.write("</metadata><data>");
	}

	public void storeRow(Iterator row) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("<row>");
		while(row.hasNext()){	
			Object item = row.next();
			if( item != null ) {
				sb.append(String.format("<item>%s</item>", item.toString()));
			}
		}
		sb.append("</row>");
		out.write(sb.toString());
	}

	
	public void endTable() throws IOException {
		out.write("</data></table>");
	}
	
	
	private void storeColumnName(Iterator row) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("<columnName>");
		while(row.hasNext()){	
			Object item = row.next();
			if( item != null ) {
				sb.append(String.format("<item>%s</item>", item.toString()));
			}
		}
		sb.append("</columnName>");
		out.write(sb.toString());
	}

}
