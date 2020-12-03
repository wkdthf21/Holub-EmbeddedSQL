package com.holub.database;

import java.util.Collection;

/**
 * 
 * @author yesol
 *
 *	Aggregation Function Interface Used by Select Statement
 *  ( count, sum, avg, max, min ... )
 *  
 *  Related Classes are defined by Visitor Pattern
 *  
 *  For example :
 *  	select count(*) from name
 *  
 * @see SumVisitor
 * @see CountVisitor
 *
 */
public interface AggregationVisitor {
	
	public Table visit(UnmodifiableTable table, Selector where, String[] sumColumn, Table[] otherTables);
	public Table visit(UnmodifiableTable table, Selector where, Collection sumColumn, Collection otherTables);
	
	public Table visit(ConcreteTable table, Selector where, String[] sumColumn, Table[] otherTables);
	public Table visit(ConcreteTable table, Selector where, Collection sumColumn, Collection otherTables);
	
}
