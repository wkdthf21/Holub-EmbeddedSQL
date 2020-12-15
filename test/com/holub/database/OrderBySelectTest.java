/**
 * 
 */
package com.holub.database;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * OrderBySelect Class's Unit Test Class With OrderBySelect Class & DistinctSelect Class & ColumnsSelect Class </br>
 * Select Distinct COLUMN_NAME_LIST From TABLE_NAME_LIST Where EXPRESSION Order by IDENTIFIER [ASC | DESC] </br>
 * OrderBySelect Class only support one column </br>
 * @author wkdthf21
 * @See OrderBySelect
 * @See DistinctSelect
 * @See DefaultSelect
 * @See ColumnsSelect
 * @See DecoratorSelect
 */
public class OrderBySelectTest {
	
	private Table name = TableFactory.create("name", new String[] { "last", "first", "addrId" });
	private Table address = TableFactory.create("address", new String[] { "addrId", "street", "city", "state", "zip" });
	private Object[][] nameData = {
			new Object[] { "Holub", "Allen", "0" },
			new Object[]{"Flintstone", "Wilma", "1"},
			new Object[]{"Holub", "Fred", "1"},
			new Object[] { "Abc", "Allen", "1" },
	};
	private Object[][] addressData = {
			new Object[]{ "0", "12 A-Street", "Berkeley", "CA", "99998" },
			new Object[]{ "1", "34 B-Street", "Bed", "AZ", "00000" },
	};
	
	@BeforeEach
	void beforeAll() {
		for(Object[] data : nameData) name.insert(data);
		for(Object[] data : addressData) address.insert(data);
	}
	
	@DisplayName("OrderBy Select Test : 하나의 Table에서 1개의 column에 대해 order by")
	@Test
	void test_orderby_one_table() {
		// given
		List columns = new ArrayList();
		List tables = new ArrayList();
		String orderById = "last";
		
		// when
		// Select * From name order by last
		SelectAlgorithm selectAlgorithm = new OrderBySelect(new DefaultSelect(name, tables, new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return true;
			}
		}), orderById);
		
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
					assertEquals(sb.toString(), "Abc Allen 1 \r\n" + 
												"Flintstone Wilma 1 \r\n" + 
												"Holub Allen 0 \r\n" + 
												"Holub Fred 1 \r\n");
				}
				
		);
	}
	
	@DisplayName("OrderBy Select Test : 2개의 Table에서 1개의 column에 대해 order by + where")
	@Test
	void test_orderby_two_table() {
		
		// given
		List columns = new ArrayList();
		List tables = new ArrayList();
		tables.add(address);
		String orderById = "zip";
		
		// when
		// Select * From name, address where name.addrId = 0 order by zip
		SelectAlgorithm selectAlgorithm = new OrderBySelect(new DefaultSelect(name, tables, new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return tables[0].column("addrId").equals("0");
			}
		}), orderById);
		
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
					assertEquals(sb.toString(), "00000 Holub Bed 34 B-Street 0 AZ Allen \r\n" + 
												"99998 Holub Berkeley 12 A-Street 0 CA Allen \r\n");
				}
				
		);
	}
	
	
	@DisplayName("OrderBy Select Test : 1개의 Table에서 1개의 column에 대해 column selct + distinct + order by")
	@Test
	void test_orderby_one_table_distinct() {
		
		// given
		List columns = new ArrayList();
		List tables = new ArrayList();
		columns.add("last");
		String orderById = "last";
		
		// when
		// select distinct last From name order by last
		SelectAlgorithm selectAlgorithm = new OrderBySelect(
				new DistinctSelect(
					new ColumnsSelect(
						new DefaultSelect(name, tables, new Selector.Adapter() {
							public boolean approve(Cursor[] tables) {
								return true;
						}}), columns)), orderById);
		
		
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
					assertEquals(sb.toString(), "Abc \r\n" + 
							"Flintstone \r\n" + 
							"Holub \r\n");
				}
				
		);
	}
	
	
	@DisplayName("OrderBy Select Test : 2개의 Table에서 1개의 column에 대해 order by desc + where")
	@Test
	void test_orderby_desc_two_table() {
		
		// given
		List columns = new ArrayList();
		List tables = new ArrayList();
		tables.add(address);
		String orderById = "desc zip";
		
		// when
		// Select * From name, address where name.addrId = 0 order by zip DESC
		SelectAlgorithm selectAlgorithm = new OrderBySelect(new DefaultSelect(name, tables, new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return tables[0].column("addrId").equals("0");
			}
		}), orderById);
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
												"00000 Holub Bed 34 B-Street 0 AZ Allen \r\n");
				}
				
		);
	}
	
}
