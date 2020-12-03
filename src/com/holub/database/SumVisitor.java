package com.holub.database;

import java.util.Collection;

public class SumVisitor implements AggregationVisitor {
	
	
	/*****************************************************************************************
	 * UnmodifiableTable
	 */
	
	@Override
	public Table visit(UnmodifiableTable table, Selector where, String sumColumn, Table[] otherTables) {
		// TODO Auto-generated method stub
		
		// get select * from [otherTables] where [where]
		String[] requestedColumns = null;
		Table allResult = table.select(where, requestedColumns, otherTables);
		
		// calculate sum
		Cursor cursor = allResult.rows();
		int sum = 0;
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
	public Table visit(UnmodifiableTable table, Selector where, String sumColumn, Collection otherTables) {
		// TODO Auto-generated method stub\
		Table[] others = null;
		if(otherTables != null) {
			others = (Table[]) otherTables.toArray(new Table[otherTables.size()]);
		}
		return visit(table, where, sumColumn, others);
	}
	
	

	/*****************************************************************************************
	 * ConcreteTable
	 */
	
	@Override
	public Table visit(ConcreteTable table, Selector where, String sumColumn, Table[] otherTables) {
		// TODO Auto-generated method stub
		// get select * from [otherTables] where [where]
		String[] requestedColumns = null;
		Table allResult = table.select(where, requestedColumns, otherTables);

		// calculate sum
		Cursor cursor = allResult.rows();
		int sum = 0;
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
	public Table visit(ConcreteTable table, Selector where, String sumColumn, Collection otherTables) {
		// TODO Auto-generated method stub
		Table[] others = null;
		if(otherTables != null) {
			others = (Table[]) otherTables.toArray(new Table[otherTables.size()]);
		}
		return visit(table, where, sumColumn, others);
	}

}
