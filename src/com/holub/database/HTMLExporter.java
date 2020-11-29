package com.holub.database;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;


/***
 *	For example:
 *	<PRE>
 *	Table people  = TableFactory.create( ... );
 *	//...
 *	Writer out = new FileWriter( "people.html" );
 *	people.export( new HTMLExporter(out) );
 *	out.close();
 *	</PRE>
 *	The output file for a HTML table called "name" with
 *	columns "first," "last," and "addrId" would look
 *	like this:
 *	<PRE>
 *			name
 *	first	last	addrId
 *	Fred	Flintstone	1
 *	Wilma	Flintstone	1
 *	Allen	Holub	0
 *	</PRE>
 *	The first line is the table name, the second line
 *	identifies the columns, and the subsequent lines define
 *	the rows.
 *
 * @see Table
 * @see Table.Exporter
 */

public class HTMLExporter implements Table.Exporter{
	
	private final Writer out;
	private 	  int	 width;
	private 	  int	 height;
	
	public HTMLExporter(Writer out) {
		this.out = out;
	}

	/**
	* @methodName : storeMetadata
	* @Author : wkdthf21
	* @return : 
	* @Desc : set width to column-size and save table name, column names into HTML file
	*/
	public void storeMetadata(String tableName, int width, int height, Iterator columnNames) throws IOException {
		this.width = width;
		this.height = height;
		
		out.write("<html>");
		out.write("<body>");
		out.write("<table border=\"1\" width =\"500\" height=\"300\" align =\"center\" >");
		out.write("<tr align =\"center\"><p><td colspan = \"" + width + "\"> " +  tableName + " </td></p></tr>");
		storeRow( columnNames );
	}

	
	/**
	* @methodName : storeRow
	* @Author : wkdthf21
	* @return : 
	* @Desc : make row using iterator
	*/
	public void storeRow(Iterator row) throws IOException {
		out.write("<tr align = center>");
		while( row.hasNext()){	
			Object data = row.next();
			if( data != null )	
				out.write( "<td>" + data.toString() + "</td>");
		}
		out.write("</tr>");
	}
	
	
	/**
	* @methodName : endTable
	* @Author : wkdthf21
	* @return : 
	* @Desc : make HTML closing tag
	*/
	public void endTable()   throws IOException {
		out.write("</table>");
		out.write("</body>");
		out.write("</html>");
	}
	
	
	public void startTable() throws IOException {/*nothing to do*/}
	
}
