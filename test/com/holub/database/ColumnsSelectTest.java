package com.holub.database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

/**
 * 
 * ColumnsSelect Class's Unit Test Class With DefaultSelect Class </br>
 * Select COLUMN_NAME_LIST From TABLE_NAME_LIST Where EXPRESSION
 * 
 * @author wkdthf21
 * @See DistinctSelect
 * @See DefaultSelect
 * @See DecoratorSelect
 */
public class ColumnsSelectTest {
	
	private Table name = TableFactory.create("name", new String[] { "last", "first", "addrId" });
	private Table address = TableFactory.create("address", new String[] { "addrId", "street", "city", "state", "zip" });
	private Table orders = TableFactory.create("orders", new String[] { "item", "quantity", "data" });
	private Object[][] nameData = {
			new Object[] { "Holub", "Allen", "0" },
			new Object[]{"Flintstone", "Wilma", "1"},
			new Object[]{"Flintstone", "Fred", "1"}
	};
	private Object[][] addressData = {
			new Object[]{ "0", "12 A-Street", "Berkeley", "CA", "99998" },
			new Object[]{ "1", "34 B-Street", "Bed", "AZ", "00000" }
	};
	private Object[][] ordersData = {
			new Object[] { "E16-25A", "16", "2020/9/1" }
	};
	
	@BeforeEach
	void beforeAll() {
		for(Object[] data : nameData) name.insert(data);
		for(Object[] data : addressData) address.insert(data);
		for(Object[] data : ordersData) orders.insert(data);
	}
	
	@DisplayName("Columns Select Test : 1개의 Table에서 Select")
	@Test
	void select_from_one_table() {
		// given
		List columns = new ArrayList();
		columns.add("last");
		columns.add("first");

		List tables = new ArrayList();
		
		// when
		// Select first, last From name WHERE name.addrID = 0
		SelectAlgorithm selectAlgorithm = new ColumnsSelect(new DefaultSelect(name, tables, new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return tables[0].column("addrId").equals("0");
			}
		}), columns);
		
		Table result = selectAlgorithm.doSelect();

		// then
		assertAll(	
			() -> assertNotNull(result),
			() -> {
				Cursor cursor = result.rows();
				StringBuilder sb = new StringBuilder();
				while(cursor.advance()) {
					Iterator iter = cursor.columns();
					while(iter.hasNext()) sb.append(iter.next() + " ");
					sb.append("\r\n");
				}
				assertEquals(sb.toString(), "Holub Allen \r\n");
			}
		);
	}
	
	@DisplayName("Columns Select Test : 2개의 겹치는 컬럼이 없는 Table에서 Select 특정 컬럼")
	@Test
	void select_from_two_table() {
		// given
		List columns = new ArrayList();
		columns.add("last");
		columns.add("first");
		columns.add("addrId");
		columns.add("item");
		
		List tables = new ArrayList();
		tables.add(orders);
		
		// when
		// Select first, last, addrId, item From name, orders WHERE name.addrID = 0
		SelectAlgorithm selectAlgorithm = new ColumnsSelect(new DefaultSelect(name, tables, new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return tables[0].column("addrId").equals("0");
			}
		}), columns);

		Table result = selectAlgorithm.doSelect();

		// then
		assertAll(	
			() -> assertNotNull(result),
			() -> {
				Cursor cursor = result.rows();
				StringBuilder sb = new StringBuilder();
				while(cursor.advance()) {
					Iterator iter = cursor.columns();
					while(iter.hasNext()) sb.append(iter.next() + " ");
					sb.append("\r\n");
				}
				assertEquals(sb.toString(), "Holub Allen 0 E16-25A \r\n");
			}
		);
		
	}
	
	@DisplayName("Columns Select Test : 2개의 겹치는 Table에서 Select 특정 컬럼")
	@Test
	void select_from_two_table_with_common_column() {
		// given
		List columns = new ArrayList();
		columns.add("last");
		columns.add("first");
		columns.add("street");
		columns.add("city");
		columns.add("state");
		columns.add("zip");
		
		List tables = new ArrayList();
		tables.add(address);
		
		// when
		// Select first, last, street, city, state, zip From name, address WHERE name.addrId = 0 and address.addrId = 1
		SelectAlgorithm selectAlgorithm = new ColumnsSelect(new DefaultSelect(name, tables, new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return tables[0].column("addrId").equals("0") && tables[1].column("addrId").equals("1");
			}
		}), columns);

		Table result = selectAlgorithm.doSelect();
		
		// then
		assertAll(	
			() -> assertNotNull(result),
			() -> {
				Cursor cursor = result.rows();
				StringBuilder sb = new StringBuilder();
				while(cursor.advance()) {
					Iterator iter = cursor.columns();
					while(iter.hasNext()) sb.append(iter.next() + " ");
					sb.append("\r\n");
				}
				assertEquals(sb.toString(), "Holub Allen 34 B-Street Bed AZ 00000 \r\n");
			}
		);
	}
	
	@DisplayName("Columns Select Test : 1개의 Table에서 Select All")
	@Test
	void select_all_from_one_table() {
		// given
		List columns = new ArrayList();
		List tables = new ArrayList();
		
		// when
		// Select * From name WHERE name.addrID = 0
		SelectAlgorithm selectAlgorithm = new ColumnsSelect(new DefaultSelect(name, tables, new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return tables[0].column("addrId").equals("0");
			}
		}), columns);
		
		Table result = selectAlgorithm.doSelect();
		
		// then
		assertAll(	
			() -> assertNotNull(result),
			() -> {
				Cursor cursor = result.rows();
				StringBuilder sb = new StringBuilder();
				while(cursor.advance()) {
					Iterator iter = cursor.columns();
					while(iter.hasNext()) sb.append(iter.next() + " ");
					sb.append("\r\n");
				}
				assertEquals(sb.toString(), "Holub Allen 0 \r\n");
			}
		);
	}
	
	@DisplayName("Columns Select Test : (must3 쿼리) 2개의 Table에서 Select All")
	@Test
	void select_all_from_two_table() {
		// given
		List columns = new ArrayList();
		List tables = new ArrayList();
		tables.add(address);
		
		// when
		// Select * From address, name WHERE name.addrId = address.addrId
		SelectAlgorithm selectAlgorithm = new ColumnsSelect(new DefaultSelect(name, tables, new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return tables[0].column("addrId").equals(tables[1].column("addrId"));
			}
		}), columns);
		
		Table result = selectAlgorithm.doSelect();
		
		// then
		assertAll(	
			() -> assertNotNull(result),
			() -> {
				Cursor cursor = result.rows();
				StringBuilder sb = new StringBuilder();
				while(cursor.advance()) {
					Iterator iter = cursor.columns();
					while(iter.hasNext()) sb.append(iter.next() + " ");
					sb.append("\r\n");
				}
				assertEquals(sb.toString(), "99998 Holub Berkeley 12 A-Street 0 CA Allen \r\n" + 
						"00000 Flintstone Bed 34 B-Street 1 AZ Wilma \r\n" + 
						"00000 Flintstone Bed 34 B-Street 1 AZ Fred \r\n");
			}
		);
	}
	
}
