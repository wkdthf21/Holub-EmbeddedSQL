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
 * 
 * Export algorithm is encapsulated using a template method
 * Override Table.Exporter Functions to separate data start tag from storeMetadata function
 * (XML and HTML needs data start tag)
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
	
	
	
	/*******************************************************************
	 * 	abstract class list */
	public abstract void makeStart() throws IOException;
	public abstract void makeEnd() throws IOException;
	public abstract void makeMetadata(String tableName, int width, int height, Iterator columnNames) throws IOException;
	public abstract void makeDataStart() throws IOException;
	public abstract void makeDataEnd() throws IOException;
	public abstract void makeRow(Iterator row) throws IOException;
	
	
	
	/*******************************************************************
	 * 	override Table.Exporter to separate data start tag from storeMetadata function */
	private int rowCnt = 0;
	
	@Override
	public void startTable() throws IOException{
		rowCnt = 0;
		makeStart();
	}
	
	@Override
	public void storeMetadata(String tableName, int width, int height, Iterator columnNames) throws IOException{
		rowCnt = 0;
		makeMetadata(tableName, width, height, columnNames);
	}
	
	@Override
	public void storeRow(Iterator row) throws IOException{
		if(rowCnt == 0) makeDataStart();
		makeRow(row);
		++rowCnt;
	}
	
	@Override
	public void endTable() throws IOException{
		if(rowCnt != 0) makeDataEnd();
		makeEnd();
	}
		
}
