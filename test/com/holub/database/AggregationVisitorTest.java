package com.holub.database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AggregationVisitorTest {

	private Table name = TableFactory.create("name", new String[] { "last", "first", "addrId" });
	private Table nullTable = TableFactory.create("address", new String[] { "count" });
	private Table orders = TableFactory.create("orders", new String[] { "item", "quantity", "data" });
	private Object[][] nameData = {
			new Object[] { "Holub", "Allen", "0" },
			new Object[]{"Flintstone", "Wilma", "1"},
			new Object[]{"Flintstone", "Fred", "1"}
	};
	private Object[][] nullTableData = {
			new Object[]{ "100"},
			new Object[]{ "250"},
			new Object[]{ ""},
			new Object[]{ null },
	};
	private Object[][] ordersData = {
			new Object[] { "E16-25A", "16", "2020/9/1" },
			new Object[] { "E16-25B", "20", "2020/11/27" },
			new Object[] { "E16-25A", "40", "2020/10/5" },
	};
	
	
	@BeforeEach
	void beforeAll() {
		for(Object[] data : nameData) name.insert(data);
		for(Object[] data : nullTableData) nullTable.insert(data);
		for(Object[] data : ordersData) orders.insert(data);
	}
	
	@DisplayName("SumVisitor Test : 1개의 Table에서 Where절 없을때 sum()이 제대로 동작하는지")
	@Test
	void test_sumVisitor_from_table() {
		
		// given
		// select sum(quantity) from orders
		List<String> sumColumns = new ArrayList<>();
		String sumColumn = "quantity";
		sumColumns.add(sumColumn);
		SumVisitor sumVisitor = new SumVisitor();
		Selector where = new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return true;
			}
		};
		List<Table> otherTables = null;
		
		// when
		Table result = orders.accept(sumVisitor, where, sumColumns, otherTables);
		
		// then
		System.out.println("select sum(quantity) from orders");
		System.out.println("=====================================================");
		System.out.println(String.format("sum(%s)", sumColumn));
		System.out.println("-------------");
		printTable(result);
		System.out.println("");
		
		Cursor cursor = result.rows();
		assertTrue(cursor.advance());
		assertTrue(cursor.columns().hasNext());
		assertEquals(cursor.columns().next(), "76");
		
	}
	
	@DisplayName("SumVisitor Test : 1개의 Table에서 Where절 있을때 sum()이 제대로 동작하는지")
	@Test
	void test_sumVisitor_from_table_with_where() {
		
		// given
		// select sum(quantity) from orders where item = E16-25A 
		List<String> sumColumns = new ArrayList<>();
		String sumColumn = "quantity";
		sumColumns.add(sumColumn);
		SumVisitor sumVisitor = new SumVisitor();
		Selector where = new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return tables[0].column("item").equals("E16-25A");
			}
		};
		List<Table> otherTables = null;
		
		// when
		Table result = orders.accept(sumVisitor, where, sumColumns, otherTables);
		
		// then
		System.out.println("select sum(quantity) from orders where item = E16-25A");
		System.out.println("=====================================================");
		System.out.println(String.format("sum(%s)", sumColumn));
		System.out.println("-------------");
		printTable(result);
		System.out.println("");
		
		Cursor cursor = result.rows();
		assertTrue(cursor.advance());
		assertTrue(cursor.columns().hasNext());
		assertEquals(cursor.columns().next(), "56");
		
	}
	
	@DisplayName("SumVisitor Test : 다수의 Table에서 Where절 없을때 sum()이 제대로 동작하는지")
	@Test
	void test_sumVisitor_from_multiple_table() {
		
		// given
		// select sum(quantity) from orders, name 
		List<String> sumColumns = new ArrayList<>();
		String sumColumn = "quantity";
		sumColumns.add(sumColumn);
		SumVisitor sumVisitor = new SumVisitor();
		Selector where = new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return true;
			}
		};
		
		List<Table> otherTables = new ArrayList<>();
		otherTables.add(name);
		
		// when
		Table result = orders.accept(sumVisitor, where, sumColumns, otherTables);
		
		// then
		System.out.println("select sum(quantity) from orders, name");
		System.out.println("=====================================================");
		System.out.println(String.format("sum(%s)", sumColumn));
		System.out.println("-------------");
		printTable(result);
		System.out.println("");
		
		Cursor cursor = result.rows();
		assertTrue(cursor.advance());
		assertTrue(cursor.columns().hasNext());
		assertEquals(cursor.columns().next(), "228");
		
	}
	
	
	@DisplayName("SumVisitor Test : 다수의 Table에서 Where절 있을때 sum()이 제대로 동작하는지")
	@Test
	void test_sumVisitor_from_multiple_table_with_where() {
		
		// given
		// select sum(quantity) from orders, name where where item = E16-25B
		List<String> sumColumns = new ArrayList<>();
		String sumColumn = "quantity";
		sumColumns.add(sumColumn);
		SumVisitor sumVisitor = new SumVisitor();
		Selector where = new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return tables[0].column("item").equals("E16-25B");
			}
		};
		
		List<Table> otherTables = new ArrayList<>();
		otherTables.add(name);
		
		// when
		Table result = orders.accept(sumVisitor, where, sumColumns, otherTables);
		
		// then
		System.out.println("select sum(quantity) from orders, name where where item = E16-25B");
		System.out.println("=====================================================");
		System.out.println(String.format("sum(%s)", sumColumn));
		System.out.println("-------------");
		printTable(result);
		System.out.println("");
		
		Cursor cursor = result.rows();
		assertTrue(cursor.advance());
		assertTrue(cursor.columns().hasNext());
		assertEquals(cursor.columns().next(), "60");
		
	}
	
	
	
	@DisplayName("SumVisitor Test : null data가 있을 때 null과 빈 String 값을 무시하는지")
	@Test
	void test_sumVisitor_when_data_contains_null() {
		
		// given
		// select sum(item) from nullTable
		List<String> sumColumns = new ArrayList<>();
		String sumColumn = "count";
		sumColumns.add(sumColumn);
		SumVisitor sumVisitor = new SumVisitor();
		Selector where = new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return true;
			}
		};
		
		List<Table> otherTables = null;
		
		
		// when
		Table result = nullTable.accept(sumVisitor, where, sumColumns, otherTables);
		
		// then
		System.out.println("select sum(item) from nullTable");
		System.out.println("=====================================================");
		System.out.println(String.format("sum(%s)", sumColumn));
		System.out.println("-------------");
		printTable(result);
		System.out.println("");
		
		Cursor cursor = result.rows();
		assertTrue(cursor.advance());
		assertTrue(cursor.columns().hasNext());
		assertEquals(cursor.columns().next(), "350");
		
	}
	
	
	@DisplayName("SumVisitor Test : 더할 수 없는 컬럼을 sum의 컬럼으로 지정했을 때")
	@Test
	void test_sumVisitor_when_row_cannot_be_added() {
		
		// given
		// select sum(last) from name
		List<String> sumColumns = new ArrayList<>();
		String sumColumn = "last";
		sumColumns.add(sumColumn);
		SumVisitor sumVisitor = new SumVisitor();
		Selector where = new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return true;
			}
		};
		
		List<Table> otherTables = null;
		
		// then
	    assertThrows(NumberFormatException.class, 
	            ()->{
	            	name.accept(sumVisitor, where, sumColumns, otherTables);
	            });
	}
	
	
	
	private void printTable(Table t) {
		Cursor current = t.rows();
		while (current.advance()) {
			for (Iterator columns = current.columns(); columns.hasNext();)
				System.out.print((String) columns.next() + " ");
			System.out.println("");
		}
	}

}
