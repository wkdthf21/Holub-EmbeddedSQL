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
 * @see MarkUpExporter
 * @see XMLImporter
 */
public class XMLExporter extends MarkUpExporter{

	
	private final Writer out;
	private 	  int	 width;
	private 	  int	 height;
	
	private static final String META_XML = "<metadata><tableName>%s</tableName>%s</metadata>";
	private static final String ITEM_XML = "<item>%s</item>";
	private static final String ROW_XML = "<%s>%s</%s>";
	
	public XMLExporter(Writer out) {
		this.out = out;
	}

	@Override
	public void makeStart() throws IOException {
		// TODO Auto-generated method stub
		out.write("<?xml version=\"1.0\" encoding=\"EUC-KR\"?><table>");
	}

	@Override
	public void makeEnd() throws IOException {
		// TODO Auto-generated method stub
		out.write("</table>");
	}

	@Override
	public void makeMetadata(String tableName, int width, int height, Iterator columnNames) throws IOException {
		// TODO Auto-generated method stub
		this.width = width;
		this.height = height;
		tableName = tableName == null ? "<anonymous>" : tableName;
		out.write(String.format(META_XML, tableName, makeRowWithTag("columnName", columnNames)));
	}

	@Override
	public void makeDataStart() throws IOException {
		// TODO Auto-generated method stub
		out.write("<data>");
	}

	@Override
	public void makeDataEnd() throws IOException {
		// TODO Auto-generated method stub
		out.write("</data>");
	}

	@Override
	public void makeRow(Iterator row) throws IOException {
		// TODO Auto-generated method stub
		out.write(makeRowWithTag("row", row));
	}
	
	/**
	* @methodName : makeRowWithTag
	* @Author : wkdthf21
	* @return : 
	* @Desc : make data row to xml structure (<tag><item>%s</item><item>%s</item>...</tag>)
	* 		  tag is string parameter
	*/
	private String makeRowWithTag(String tag, Iterator row) {
		StringBuilder sb = new StringBuilder();
		while(row.hasNext()){	
			Object item = row.next();
			if( item != null ) sb.append(String.format(ITEM_XML, item.toString()));
		}
		return String.format(ROW_XML, tag, sb.toString(), tag);
	}

}
