package com.holub.database;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/** Select Algorithm Class of doing Select All Using Constructor parameter </br></br>
 * 
 * Support below select statement form : </br>
   SELECT * From TABLE_NAME_LIST Where EXPRESSION
 * @author wkdthf21
 * @param Table source
 * @param Collection other
 * @param Selector where
 * @see SelectAlgorithm
 */
public class DefaultSelect extends SelectAlgorithm{
	
	private Table source;
	private Collection other;
	private Selector where;
	
	public DefaultSelect(Table source, Collection other, Selector where) {
		this.source = source;
		this.other = other;
		this.where = where;
	}

	@Override
	public Table doSelect() {
		// TODO Auto-generated method stub
		Table[] otherTables = null;
		if(other != null) {
			otherTables = (Table[]) other.toArray(new Table[other.size()]);
		}
		
		return new UnmodifiableTable(select(otherTables));
	}
	
	//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	/* private method start */
	
	/** 
	 * select function when otherTables exists
	 * @param otherTables
	 * @return Table
	 */
	private Table select(Table[] otherTables) {
		
		// select from only source table
		if(otherTables == null || otherTables.length == 0) return select();
		
		Table[] allTables = new Table[otherTables.length + 1];
		allTables[0] = source;
		System.arraycopy(otherTables, 0, allTables, 1, otherTables.length);
		
		// If we need join & requested Columns is *
		String[] columnNames = makeAllColumns(otherTables);
		
		Table resultTable = new ConcreteTable(null, columnNames);
		Cursor[] envelope = new Cursor[allTables.length];

		selectFromCartesianProduct(0, where, columnNames, allTables, envelope, resultTable);
		
		return resultTable;
	}
	
	
	/** 
	 * select function when otherTables doesn't exist
	 * @return Table
	 */
	private Table select() {
		
		String[] columnNames = new String[source.rows().columnCount()];
		for(int i = 0; i < source.rows().columnCount(); i++) columnNames[i] = source.rows().columnName(i);

		Table resultTable = new ConcreteTable(null, (String[]) columnNames.clone());

		Cursor current = source.rows();
		Cursor[] envelope = new Cursor[] { current };

		while (current.advance()) {
			if (where.approve(envelope)) {
				Object[] newRow = new Object[columnNames.length];
				for (int column = 0; column < columnNames.length; ++column) {
					newRow[column] = current.column(columnNames[column]);
				}
				resultTable.insert(newRow);
			}
		}
		return resultTable;
	}
	
	
	/** 
	 * make column name array of other tables & source table
	 * @param otherTables
	 * @return String[]
	 */
	private String[] makeAllColumns(Table[] otherTables) {
		Set<String> columnsSet = new HashSet<>();
		for(Table otherTable : otherTables) {
			for(int i = 0; i < otherTable.rows().columnCount(); i++) {
				columnsSet.add(otherTable.rows().columnName(i));
			}
		}
		for(int i = 0; i < source.rows().columnCount(); i++) columnsSet.add(source.rows().columnName(i));
		String[] columnNames = new String[columnsSet.size()];
		Object[] otherColumnsObjArr = columnsSet.toArray();
		for(int i = 0; i < otherColumnsObjArr.length; i++) columnNames[i] = (String)otherColumnsObjArr[i];
		return columnNames;
	}
	
	
	/** 
	 * make cartesian product & select using where clause
	 * @param 
	 * @return 
	 */
	private void selectFromCartesianProduct(int level, Selector where, String[] requestedColumns,
			Table[] allTables, Cursor[] allIterators, Table resultTable) {
		
		allIterators[level] = allTables[level].rows();
		while (allIterators[level].advance()) { 

			if (level < allIterators.length - 1)
				selectFromCartesianProduct(level + 1, where, requestedColumns, allTables, allIterators, resultTable);

			if (level == allIterators.length - 1) {
				if (where.approve(allIterators))
					insertApprovedRows(resultTable, requestedColumns, allIterators);
			}
		}
	}
	
	/** 
	 * insert Approved Rows into resultTable
	 * @param 
	 * @return 
	 */
	private void insertApprovedRows(Table resultTable, String[] requestedColumns, Cursor[] allTables) {

		Object[] resultRow = new Object[requestedColumns.length];

		for (int i = 0; i < requestedColumns.length; ++i) {
			for (int table = 0; table < allTables.length; ++table) {
				try {
					resultRow[i] = allTables[table].column(requestedColumns[i]);
					break; 
				} catch (Exception e) {
				}
			}
		}
		resultTable.insert( resultRow );
	}

}
