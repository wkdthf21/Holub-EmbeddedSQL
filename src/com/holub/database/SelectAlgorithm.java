package com.holub.database;

/**
 * 
 * Abstract Class of a variety of Select Algorithm </br></br>
 * < Support below select statement form > </br>
 * SELECT  [DISTINCT] idList [INTO identifier] </br>
           FROM idList [WHERE expr] [GROUP BY idList or identifier] </br>
           [ORDER BY idList or identifier [ASC | DESC]]; </br>
 * 
 * @author wkdthf21
 * @See DefaultSelect
 * @See DecoratorSelect
 * 
 */
public abstract class SelectAlgorithm {
	abstract public Table doSelect();
}
