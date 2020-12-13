package com.holub.database;

import java.util.Collection;
import java.util.Iterator;

/**
 * ColumnsSelect Support Selecting specific columns </br>
 *   
 * @author wkdthf21
 * @param SelectAlgorithm selectAlgorithm
 * @param Collection requestedColumns
 * @see DecoratorSelect 
 *
 */
public class ColumnsSelect extends DecoratorSelect {
	
	private Collection requestedColumns;
	
	public ColumnsSelect(SelectAlgorithm selectAlgorithm, Collection requestedColumns) {
		super(selectAlgorithm);
		this.requestedColumns = requestedColumns;
	}
	
	/**
	 * add selecting specific columns from parent's result table
	 * @return Table
	 */
	@Override
	public Table doSelect() {
		// TODO Auto-generated method stub
		String[] columnNames = null;
		if(requestedColumns != null) {
			columnNames = new String[requestedColumns.size()];
			int i = 0;
			Iterator column = requestedColumns.iterator();
			while (column.hasNext())
				columnNames[i++] = column.next().toString();
		}
		
		return new UnmodifiableTable(doSelect(columnNames));
	}
	
	//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	/* private method start */
	private Table doSelect(String[] columnNames) {
		
		Table table = super.selectAlgorithm.doSelect();
		Table resultTable = new ConcreteTable(null, columnNames);
		
		// If we need join & requested Columns is *
		if(columnNames == null || columnNames.length == 0) {
			columnNames = new String[table.rows().columnCount()];
			for(int i = 0; i < table.rows().columnCount(); i++) 
				columnNames[i] = table.rows().columnName(i);
		}
		
		selectRequestedColumns(table, resultTable, columnNames);
		
		return resultTable;
	}
	
	
	private void selectRequestedColumns(Table sourceTable, Table resultTable, String[] columnNames) {
		Object[] resultRow = new Object[columnNames.length];
		Cursor current = sourceTable.rows();
		while(current.advance()) {
			for (int i = 0; i < columnNames.length; ++i) {
				resultRow[i] = current.column(columnNames[i]);
			}
			resultTable.insert(resultRow);
		}
	}

}
