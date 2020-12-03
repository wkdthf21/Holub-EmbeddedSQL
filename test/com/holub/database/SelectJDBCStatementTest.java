package com.holub.database;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import com.holub.database.jdbc.JDBCStatement;

/**
 * @author wkdthf21
 * 
 * Test fixed Select Function of ConcreteTable to solve the problem 
 * of not being able to "Select * From name, address WHERE name.addrId = address.addrId"
 * 
 * Tests JDBCStatement works when click the Run button in the console
 * To Console Test, Create Database and JDBCStatement arbitrary objects
 * 
 * [! Move all files in Dbase folder to c:/dp2020 folder before testing]
 * 
 * @see Console
 * @see JDBCStatement
 * @see Database
 * @see SelectTest
 * @see ConcreteTable - select(Selector where, String[] requestedColumns, Table[] otherTables) : Table\
 * @see Database - doSelect( List columns, String into, List requestedTableNames, final Expression where )
 */
public class SelectJDBCStatementTest {
	
	private File path = new File("c:/dp2020");
	private Database database;
	private Statement statement;
	
	
	@DisplayName("Select * from name, address where name.addrId = address.addrId 를 Console에 입력했을떼 Statement가 처리하는 것 테스트")
	@Test
	public void process_sql() throws SQLException, IOException{
		
		// given
		database = new Database(path);
		statement = new JDBCStatement(database);
		String sqlQuery = "Select * From name, address Where name.addrId = address.addrId";
		
		// when
		ResultSet results = statement.executeQuery( sqlQuery );
		
		// then
		ResultSetMetaData metadata = results.getMetaData();		
		int			 columns = metadata.getColumnCount();
		StringBuilder columnsSb = new StringBuilder();
		StringBuilder dataSb = new StringBuilder();
		
		for(int i = 1; i <= columns; i++) columnsSb.append(metadata.getColumnName(i) + " ");
		
		while( results.next() )
		{	for( int i = 1; i <= columns; ++i )
				dataSb.append( results.getString(metadata.getColumnName(i)) + " ");
			dataSb.append("\r\n");
		}
		
		assertEquals(columns, 7);
		assertEquals(columnsSb.toString(), "zip last city street addrId state first ");
		assertEquals(dataSb.toString(), "00000 Flintstone Bedrock 34 Quarry Ln. 1 AZ Fred \r\n" + 
											"00000 Flintstone Bedrock 34 Quarry Ln. 1 AZ Wilma \r\n" + 
											"99998 Holub Berkeley 12 MyStreet 0 CA Allen \r\n");
	}	

	
}
