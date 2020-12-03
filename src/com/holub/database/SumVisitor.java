package com.holub.database;

import java.util.Collection;
import java.util.Iterator;

public class SumVisitor implements AggregationVisitor {
	
	
	/*****************************************************************************************
	 * UnmodifiableTable
	 */
	
	@Override
	public Table visit(UnmodifiableTable table, Selector where, String[] sumColumns, Table[] otherTables) {
		// TODO Auto-generated method stub
		
		// get select * from [otherTables] where [where]
		String[] requestedColumns = null;
		Table allResult = table.select(where, requestedColumns, otherTables);
		
		// calculate sum
		Cursor cursor = allResult.rows();
		int sum = 0;
		String sumColumn = sumColumns[0];
		while(cursor.advance()) {
			Object obj = cursor.column(sumColumn);
			sum += Integer.parseInt((String) obj);
		}
		
		// make result table
		String[] resultColumns = new String[] {String.format("Sum(%s)", sumColumn)};
		Table result = new ConcreteTable(null, resultColumns);
		result.insert(new Object[] { String.valueOf(sum) });
		
		return result;
	}
	

	@Override
	public Table visit(UnmodifiableTable table, Selector where, Collection requestedColumns, Collection otherTables) {
		// TODO Auto-generated method stub\
		String[] columnNames = null;
		Table[] others = null;
		if(requestedColumns != null) {
			columnNames = new String[requestedColumns.size()];
			int i = 0;
			Iterator column = requestedColumns.iterator();

			while (column.hasNext())
				columnNames[i++] = column.next().toString();
		}
		if(otherTables != null) {
			others = (Table[]) otherTables.toArray(new Table[otherTables.size()]);
		}
		return visit(table, where, columnNames, others);
	}
	
	

	/*****************************************************************************************
	 * ConcreteTable
	 */
	
	@Override
	public Table visit(ConcreteTable table, Selector where, String[] sumColumns, Table[] otherTables) {
		// TODO Auto-generated method stub
		// get select * from [otherTables] where [where]
		String[] requestedColumns = null;
		Table allResult = table.select(where, requestedColumns, otherTables);

		// calculate sum
		Cursor cursor = allResult.rows();
		int sum = 0;
		String sumColumn = sumColumns[0];
		while(cursor.advance()) {
			Object obj = cursor.column(sumColumn);
			String str = (String) obj;
			if(str != null && str.isBlank() == false) 
				sum += Integer.parseInt((String) obj);
		}
		
		// make result table
		String[] resultColumns = new String[] {String.format("Sum(%s)", sumColumn)};
		Table result = new ConcreteTable(null, resultColumns);
		result.insert(new Object[] { String.valueOf(sum) });
		
		return result;
	}


	@Override
	public Table visit(ConcreteTable table, Selector where, Collection requestedColumns, Collection otherTables) {
		// TODO Auto-generated method stub
		String[] columnNames = null;
		Table[] others = null;
		if(requestedColumns != null) {
			columnNames = new String[requestedColumns.size()];
			int i = 0;
			Iterator column = requestedColumns.iterator();

			while (column.hasNext())
				columnNames[i++] = column.next().toString();
		}
		if(otherTables != null) {
			others = (Table[]) otherTables.toArray(new Table[otherTables.size()]);
		}
		return visit(table, where, columnNames, others);
	}

}
