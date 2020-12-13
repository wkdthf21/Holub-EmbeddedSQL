package com.holub.database;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.holub.database.Cursor;
import com.holub.database.Table;
import com.holub.database.TableFactory;

/**
 * DefaultSelect Class Unit Test Class </br>
 * Select * From TABLE_NAME_LIST Where EXPRESSION
 * 
 * @author wkdthf21
 * @See DefaultSelect
 */
public class DefaultSelectTest {
	
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
	
	@DisplayName("Default Select Test : 1개의 Table에서 Select All")
	@Test
	void select_all_from_one_table() {
		
		// given
		List columns = new ArrayList();
		List tables = new ArrayList();
		
		// when
		// Select * From name WHERE name.addrID = 0
		SelectAlgorithm selectAlgorithm = new DefaultSelect(name, tables, new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return tables[0].column("addrId").equals("0");
			}
		});
		
		Table result = selectAlgorithm.doSelect();

		// then
		assertAll(	
				() -> assertNotNull(result),
				() -> {
					int rowCnt = 0;
					Cursor cursor = result.rows();
					while(cursor.advance()) {
						Iterator iter = cursor.columns();
						rowCnt++;
						int idx = 0;
						while(iter.hasNext()) {
							assertEquals(iter.next(), nameData[0][idx++]);
						}
					}
					assertTrue(rowCnt == 1);
				}
			);
	}
	
	@DisplayName("Default Select Test : 2개의 겹치는 컬럼이 없는 Table에서 Select All")
	@Test
	void select_all_from_two_table() {
		
		// given
		List columns = new ArrayList();
		List tables = new ArrayList();
		tables.add(orders);
		
		// when
		// Select first, last, addrId From name, orders WHERE name.addrID = 0
		SelectAlgorithm selectAlgorithm = new DefaultSelect(name, tables, new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return tables[0].column("addrId").equals("0");
			}
		});
		
		Table result = selectAlgorithm.doSelect();
		
		// then
		assertAll(	
				() -> assertNotNull(result),
				() -> {
					Cursor cursor = result.rows();
					StringBuilder sb = new StringBuilder();
					while(cursor.advance()) {
						Iterator iter = cursor.columns();
						while(iter.hasNext()) sb.append(iter.next());
					}
					assertEquals("E16-25A162020/9/1Holub0Allen", sb.toString());
				}
			);
	}
	
	@DisplayName("Default Select Test : must3 sql문장(겹치는 컬럼이 있는 2개의 Table에서 Select All)")
	@Test
	void select_all_from_two_table_with_common_column() {
		// given
		List columns = null;
		List tables = new ArrayList();
		tables.add(address);
		
		// when
		// Select * From name, address WHERE name.addrId = address.addrId
		SelectAlgorithm selectAlgorithm = new DefaultSelect(name, tables, new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return tables[0].column("addrId").equals(tables[1].column("addrId"));
			}
		});
		
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
	
	
	@DisplayName("Default Select Test : 2개의 겹치는 Table에서 Where절 없이 Select All")
	@Test
	void select_all_from_two_table_with_common_column_no_where() {
		// given
		List columns = null;
		List tables = new ArrayList();
		tables.add(address);
		
		// when
		// Select * From name, address
		SelectAlgorithm selectAlgorithm = new DefaultSelect(name, tables, new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return true;
			}
		});
		
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
											"00000 Holub Bed 34 B-Street 0 AZ Allen \r\n" + 
											"99998 Flintstone Berkeley 12 A-Street 1 CA Wilma \r\n" + 
											"00000 Flintstone Bed 34 B-Street 1 AZ Wilma \r\n" + 
											"99998 Flintstone Berkeley 12 A-Street 1 CA Fred \r\n" + 
											"00000 Flintstone Bed 34 B-Street 1 AZ Fred \r\n");
			}
		);
		
	}
	
}
