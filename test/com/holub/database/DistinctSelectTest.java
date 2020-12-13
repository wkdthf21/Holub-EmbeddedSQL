package com.holub.database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

/**
 * DistinctSelect Class's Unit Test Class With DefaultSelect Class & ColumnsSelect Class </br>
 * Select Distinct COLUMN_NAME_LIST From TABLE_NAME_LIST Where EXPRESSION
 * @author wkdthf21
 * @See DistinctSelect
 * @See DefaultSelect
 * @See ColumnsSelect
 * @See DecoratorSelect
 */
public class DistinctSelectTest {
	
	private Table name = TableFactory.create("name", new String[] { "last", "first", "addrId" });
	private Table address = TableFactory.create("address", new String[] { "addrId", "street", "city", "state", "zip" });
	private Object[][] nameData = {
			new Object[] { "Holub", "Allen", "10" },
			new Object[]{"Flintstone", "Wilma", "10"},
			new Object[]{"Flintstone", "Fred", "10"},
			new Object[] { "Holub", "Allen", "27" }
	};
	private Object[][] addressData = {
			new Object[]{ "10", "12 A-Street", "Berkeley", "CA", "99998" },
			new Object[]{ "27", "34 B-Street", "Bed", "AZ", "00000" }
	};
	
	@BeforeEach
	void beforeAll() {
		for(Object[] data : nameData) name.insert(data);
		for(Object[] data : addressData) address.insert(data);
	}
	
	@DisplayName("Distinct Select Test : 1개의 Table에서 1개의 컬럼에 대해 Distinct Select")
	@Test
	void select_distinct_one_column_from_one_table() {
		
		// given
		List columns = new ArrayList();
		columns.add("last"); 
		List tables = new ArrayList();
		
		// when
		// Select distinct last From name WHERE name.addrID = 10		
		SelectAlgorithm selectAlgorithm = new ColumnsSelect(new DefaultSelect(name, tables, new Selector.Adapter() {
			public boolean approve(Cursor[] tables) {
				return tables[0].column("addrId").equals("10");
			}}), columns);
		selectAlgorithm = new DistinctSelect(selectAlgorithm);
		
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
					assertEquals(sb.toString(), "Holub \r\n" + 
												"Flintstone \r\n");
				}
				
		);
	}
	
	
	@DisplayName("Distinct Select Test : 1개의 Table에서 여러개의 컬럼에 대해 Distinct Select")
	@Test
	void select_distinct_columns_from_one_table() {
		// given
		List columns = new ArrayList();
		columns.add("last"); 
		columns.add("first");
		List tables = new ArrayList();
		
		// when
		// Select distinct last, first From name WHERE name.addrID = 10
		SelectAlgorithm selectAlgorithm = new DistinctSelect(
			new ColumnsSelect(
				new DefaultSelect(name, tables, new Selector.Adapter() {
					public boolean approve(Cursor[] tables) {
						return tables[0].column("addrId").equals("10");
				}}), columns));
		
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
					assertEquals(sb.toString(), "Holub Allen \r\n" + 
												"Flintstone Wilma \r\n" +
												"Flintstone Fred \r\n");
				}
				
		);
	}
	
	@DisplayName("Distinct Select Test : 여러개의 Table에서 여러개의 컬럼에 대해 Distinct Select")
	@Test
	void select_distinct_columns_from_tables() {
		// given
		List columns = new ArrayList();
		columns.add("last");
		columns.add("first");
		columns.add("addrId");
		columns.add("street");
		
		List tables = new ArrayList();
		tables.add(address);
		
		// when
		// Select last, first, addrId, street From name, address WHERE name.addrId = address.addrId
		SelectAlgorithm selectAlgorithm = new DistinctSelect(
				new ColumnsSelect(
					new DefaultSelect(name, tables, new Selector.Adapter() {
						public boolean approve(Cursor[] tables) {
							return tables[0].column("addrId").equals(tables[1].column("addrId"));
					}}), columns));
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
				assertEquals(sb.toString(), "Holub Allen 10 12 A-Street \r\n" + 
						"Flintstone Wilma 10 12 A-Street \r\n" + 
						"Flintstone Fred 10 12 A-Street \r\n" + 
						"Holub Allen 27 34 B-Street \r\n");
			}
		);
		
	}
}
