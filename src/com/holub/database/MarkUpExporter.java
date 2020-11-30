/**
 * 
 */
package com.holub.database;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import com.holub.tools.ArrayIterator;

/**
 * @author wkdthf21
 * 
 * Abstract class for xml exporter and html exporter
 * Export algorithm is encapsulated using a template method
 * 
 */
public abstract class MarkUpExporter implements Table.Exporter {
	
	/**
	* @methodName : callExportProcess
	* @Author : wkdthf21
	* @return : 
	* @Desc : Export algorithm is encapsulated using a template method
	*/
	final void callExportProcess(String tableName, 
									int width,
									int height,
									Iterator columnNames,
									LinkedList rowSet) throws IOException {
		startTable();
		storeMetadata(tableName, width, height, columnNames);
		
		for (Iterator i = rowSet.iterator(); i.hasNext();)
			storeRow(new ArrayIterator((Object[]) i.next()));
		
		endTable();
	}
	
	
	
}
