package com.holub.database;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author wkdthf21
 * 
 * Abstract class for xml importer
 * 
 * Override Table.Importer Functions
 * Unlike loadRow() when importing a csv file, in.readLine() cannot be used when importing an xml file
 * It is necessary to parse the rows when calling loadRow() for the first time.
 *
 */
public abstract class MarkUpImporter implements Table.Importer {
	
	
	/*******************************************************************
	 * 	override Table.Importer to parse the rows when calling loadRow() for the first time */
	private int rowIdx = 0;
	
	
	@Override
	public void startTable()			throws IOException {
		rowIdx = 0;
		parseStart();
	}
	
	@Override
	public Iterator loadRow()			throws IOException {
		if(rowIdx == 0) parseRows();
		return returnRow(rowIdx++);
	}
	
	@Override
	public void endTable() throws IOException{}
	
	
	
	/*******************************************************************
	 * 	abstract class list */
	public abstract void parseStart();
	public abstract void parseRows();
	public abstract Iterator returnRow(int rowIdx);
	
}
