/**
 * 
 */
package com.holub.database;

import java.util.*;

/** Class of adding order by algorithm to parent class Instance </br></br>
 * 
 * OrderBySelect Support Order by Keyword in select statement </br>
 * OrderBySelect Class only support one column </br>
 * And ASC and DESC can be added after column id </br>
 * Default is ASC (ascending)
 *   
 * @author wkdthf21
 * @param SelectAlgorithm selectAlgorithm
 * @see DecoratorSelect 
 *
 */
public class OrderBySelect extends DecoratorSelect {

	String idOrderBy;
	public OrderBySelect(SelectAlgorithm selectAlgorithm, String idOrderBy) {
		super(selectAlgorithm);
		this.idOrderBy = idOrderBy;
	}
	
	private static class OrderByValue{
		int columnIdx;
		String data;
		public OrderByValue(int columnIdx, String data) {
			this.columnIdx = columnIdx;
			this.data = data;
		}
	}
	
	
	@Override
	public Table doSelect() {
		// TODO Auto-generated method stub
		Table table = super.selectAlgorithm.doSelect();
		String[] columnNames = null;
		columnNames = new String[table.rows().columnCount()];
		for(int i = 0; i < table.rows().columnCount(); i++) 
			columnNames[i] = table.rows().columnName(i);
		
		Table resultTable = new ConcreteTable(null, columnNames);
		doOrderBy(table, resultTable);
		
		return resultTable;
	}
	
	
	private void doOrderBy(Table table, Table resultTable) {
		
		Map<String, List<List<OrderByValue>>> map = new HashMap<>();
		Object[][] tableValues = new Object[getRowsCount(table)][table.rows().columnCount()];
		List<String> keyList = new ArrayList<>();
		int keyIdx = 0;
		// find key column index
		for(int i = 0; i < table.rows().columnCount(); i++) 
			if(table.rows().columnName(i).equals(idOrderBy)) {
				keyIdx = i;
		}
		
		// make map
		Cursor current = table.rows();
		int idx;
		Iterator iter;
		while(current.advance()) {
			iter = current.columns();
			String keyData = "", item = "";
			List<OrderByValue> value = new ArrayList<>(); 
			idx = 0;
			while(iter.hasNext()) {
				item = String.valueOf(iter.next());
				if(idx == keyIdx) keyData = item;
				else value.add(new OrderByValue(idx, item));
				++idx;
			}
			if(!map.containsKey (keyData)) map.put(keyData, new ArrayList<>(new ArrayList<>()));
			map.get(keyData).add(value);
		}
		
		// sort & make result Table
		keyList = new ArrayList<>( map.keySet() );
		if(idOrderBy.length() > 5 && idOrderBy.substring(0, 5).toLowerCase().compareTo("desc ") == 0) Collections.sort(keyList, Collections.reverseOrder());
		else Collections.sort(keyList);
		
		int rowIdx = 0;
		for(String key : keyList) {
			List<List<OrderByValue>> values = map.get(key);
			for(int i = 0; i < values.size(); i++) {
				List<OrderByValue> value = values.get(i);
				tableValues[rowIdx][keyIdx] = key;
				for(OrderByValue item : value) {
					tableValues[rowIdx][item.columnIdx] = item.data;
				}
				++rowIdx;
			}
		}
				
		for(Object[] values : tableValues) {
			resultTable.insert(values);
		}
	}

	private int getRowsCount(Table table) {
		Cursor current = table.rows();
		int count = 0;
		while(current.advance()) ++count;
		return count;
	}
	
}



