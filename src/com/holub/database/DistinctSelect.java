package com.holub.database;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/** Class of adding distinct algorithm to parent class Instance </br></br>
 * 
 * DistinctSelect Support Distinct Keyword in select statement </br>
 *   
 * @author wkdthf21
 * @param SelectAlgorithm selectAlgorithm
 * @see DecoratorSelect 
 *
 */
public class DistinctSelect extends DecoratorSelect{
	
	public DistinctSelect(SelectAlgorithm selectAlgorithm) {
		super(selectAlgorithm);
	}

	/**
	 * add distinct selecting from parent's result table
	 * @return Table
	 */
	@Override
	public Table doSelect() {
		// TODO Auto-generated method stub
		Table table = super.selectAlgorithm.doSelect();
		String[] columnNames = null;
		columnNames = new String[table.rows().columnCount()];
		for(int i = 0; i < table.rows().columnCount(); i++) 
			columnNames[i] = table.rows().columnName(i);
		
		Table resultTable = new ConcreteTable(null, columnNames);
		Set<String> rowSet = new HashSet<>();
		Cursor current = table.rows();
		while (current.advance()) {
			Object[] values = new Object[columnNames.length];
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < columnNames.length; i++) {
				Object item = current.column(columnNames[i]);				
				values[i] = item;
				sb.append(String.valueOf(item));
			}
			if(!rowSet.contains(sb.toString())) {
				rowSet.add(sb.toString());
				resultTable.insert(values);
			}
		}
		
		return new UnmodifiableTable(resultTable);
	}

}
