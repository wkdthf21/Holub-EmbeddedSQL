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
 * @see MarkUpExporter
 */

public class HTMLExporter extends MarkUpExporter{
	
	private final Writer out;
	private 	  int	 width;
	private 	  int	 height;
	
	private static final String TALBE_NAME_HTML = "<thead><tr align =\"center\" height = \"50\"><p><td colspan = \"%s\"> %s </td></p></tr>";
	private static final String ROW_HTML = "<tr align = center>%s</tr>";
	private static final String ITEM_HTML = "<td>%s</td>";
	
	public HTMLExporter(Writer out) {
		this.out = out;
	}
	
	/**
	* @methodName : makeStart
	* @Author : wkdthf21
	* @return : 
	* @Desc : make HTML opening tag
	*/
	@Override
	public void makeStart() throws IOException {
		// TODO Auto-generated method stub
		out.write("<html><body><table border=\"1\" width =\"500\" height=\"300\" align =\"center\" >");
	}

	/**
	* @methodName : makeEnd
	* @Author : wkdthf21
	* @return : 
	* @Desc : make HTML closing tag
	*/
	@Override
	public void makeEnd() throws IOException {
		// TODO Auto-generated method stub
		out.write("</table></body></html>");
	}

	/**
	* @methodName : makeMetadata
	* @Author : wkdthf21
	* @return : 
	* @Desc : set width to column-size and save table name, column names into HTML file
	*/
	@Override
	public void makeMetadata(String tableName, int width, int height, Iterator columnNames) throws IOException {
		// TODO Auto-generated method stub
		this.width = width;
		this.height = height;
		tableName = tableName == null ? "<anonymous>" : tableName;
		out.write(String.format(TALBE_NAME_HTML, width, tableName));
		makeRow( columnNames );
		out.write("</thead>");
	}

	/**
	* @methodName : makeDataStart
	* @Author : wkdthf21
	* @return : 
	* @Desc : make data start tag (<tbody>)
	* 		  the function is only used when the number of rows is more than 0
	*/
	@Override
	public void makeDataStart() throws IOException {
		// TODO Auto-generated method stub
		out.write("<tbody>");
	}

	/**
	* @methodName : makeDataEnd
	* @Author : wkdthf21
	* @return : 
	* @Desc : make data end tag (</tbody>)
	* 		  the function is only used when the number of rows is more than 0
	*/
	@Override
	public void makeDataEnd() throws IOException {
		// TODO Auto-generated method stub
		out.write("</tbody>");
	}

	/**
	* @methodName : makeRow
	* @Author : wkdthf21
	* @return : 
	* @Desc : make HTML row using iterator
	*/
	@Override
	public void makeRow(Iterator row) throws IOException {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		while( row.hasNext()){	
			Object data = row.next();
			if( data != null ) sb.append(String.format(ITEM_HTML, data.toString()));
		}
		out.write(String.format(ROW_HTML, sb.toString()));
	}
	
}
