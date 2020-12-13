package com.holub.database;

import java.util.Collection;

/** Abstract Class of adding algorithm to DefaultSelect Instance </br></br>
 * 
 * You can add a variety of select algorithm like </br>
 * distinct keyword, order by, group by, aggregation function, having clause..
 * by extending this class(DecoratorSelect)
 * 
 * @author wkdthf21
 * @param SelectAlgorithm selectAlgorithm
 * @see SelectAlgorithm
 * @see DefaultSelect
 * @see ColumnsSelect
 * @see DistinctSelect
 *
 */
public abstract class DecoratorSelect extends SelectAlgorithm {
	
	protected SelectAlgorithm selectAlgorithm;
	
	public DecoratorSelect() {}
	public DecoratorSelect(SelectAlgorithm selectAlgorithm) {
		this.selectAlgorithm = selectAlgorithm;
	}
	abstract public Table doSelect();
}
