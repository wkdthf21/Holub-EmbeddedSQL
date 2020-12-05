package com.holub.text.visitor;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.holub.text.Token;
import com.holub.text.TokenSet;
import com.holub.text.visitor.KeywordPrintVisitor;
import com.holub.text.visitor.TokenVisitor;

/**
 * 
 * @author yesol
 * 
 * Test a variety of TokenVisitor's Concrete Class
 * 
 * @see TokenVisitor
 * @see KeywordPrintVisitor
 */
class TokenVisitorTest {

	private TokenSet tokens = new TokenSet();
	private Token
					COMMA		= tokens.create( "'," 		), //{=Database.firstToken}
					EQUAL		= tokens.create( "'=" 		),
					LP			= tokens.create( "'(" 		),
					RP 			= tokens.create( "')" 		),
					DOT			= tokens.create( "'." 		),
					STAR		= tokens.create( "'*" 		),
					SLASH		= tokens.create( "'/" 		),
					AND			= tokens.create( "'AND"		),
					BEGIN		= tokens.create( "'BEGIN"	),
					COMMIT		= tokens.create( "'COMMIT"	),
					CREATE		= tokens.create( "'CREATE"	),
					DATABASE	= tokens.create( "'DATABASE"),
					DELETE		= tokens.create( "'DELETE"	),
					DROP		= tokens.create( "'DROP"	),
					DUMP		= tokens.create( "'DUMP"	),
					FROM		= tokens.create( "'FROM"	),
					INSERT 		= tokens.create( "'INSERT"	),
					INTO 		= tokens.create( "'INTO"	),
					KEY 		= tokens.create( "'KEY"		),
					LIKE		= tokens.create( "'LIKE"	),
					NOT 		= tokens.create( "'NOT"		),
					NULL		= tokens.create( "'NULL"	),
					OR			= tokens.create( "'OR"		),
					PRIMARY		= tokens.create( "'PRIMARY"	),
					ROLLBACK	= tokens.create( "'ROLLBACK"),
					SELECT		= tokens.create( "'SELECT"	),
					SET			= tokens.create( "'SET"		),
					TABLE		= tokens.create( "'TABLE"	),
					UPDATE		= tokens.create( "'UPDATE"	),
					USE			= tokens.create( "'USE"		),
					VALUES 		= tokens.create( "'VALUES"	),
					WHERE		= tokens.create( "'WHERE"	),
			
					WORK		= tokens.create( "WORK|TRAN(SACTION)?"		),
					ADDITIVE	= tokens.create( "\\+|-" 					),
					STRING		= tokens.create( "(\".*?\")|('.*?')"		),
					RELOP		= tokens.create( "[<>][=>]?"				),
					NUMBER		= tokens.create( "[0-9]+(\\.[0-9]+)?"		),
			
					INTEGER		= tokens.create( "(small|tiny|big)?int(eger)?"),
					NUMERIC		= tokens.create( "decimal|numeric|real|double"),
					CHAR		= tokens.create( "(var)?char"				),
					DATE		= tokens.create( "date(\\s*\\(.*?\\))?"		),
			
					IDENTIFIER	= tokens.create( "[a-zA-Z_0-9/\\\\:~]+"		); //{=Database.lastToken}
	
	
	
	@DisplayName("TokenVisitor Test : KeywordPrintVisitor가 holub-sql에서 제공하는 sql keyword들을 정확히 출력하는지")
	@Test
	void test_KeywordPrintVisitor() {
		// when
		String result = tokens.getSupportedKeywords().toLowerCase();
		
		// then
		assertAll(
				() -> assertNotNull(result),
				() -> assertTrue(result.contains(AND.toString())),
				() -> assertTrue(result.contains(BEGIN.toString())),
				() -> assertTrue(result.contains(COMMIT.toString())),
				() -> assertTrue(result.contains(CREATE.toString())),
				() -> assertTrue(result.contains(DATABASE.toString())),
				() -> assertTrue(result.contains(DELETE.toString())),
				() -> assertTrue(result.contains(DROP.toString())),
				() -> assertTrue(result.contains(DUMP.toString())),
				() -> assertTrue(result.contains(FROM.toString())),
				() -> assertTrue(result.contains(INSERT.toString())),
				() -> assertTrue(result.contains(INTO.toString())),
				() -> assertTrue(result.contains(KEY.toString())),
				() -> assertTrue(result.contains(LIKE.toString())),
				() -> assertTrue(result.contains(NOT.toString())),
				() -> assertTrue(result.contains(NULL.toString())),
				() -> assertTrue(result.contains(OR.toString())),
				() -> assertTrue(result.contains(PRIMARY.toString())),
				() -> assertTrue(result.contains(ROLLBACK.toString())),
				() -> assertTrue(result.contains(SELECT.toString())),
				() -> assertTrue(result.contains(SET.toString())),
				() -> assertTrue(result.contains(TABLE.toString())),
				() -> assertTrue(result.contains(UPDATE.toString())),
				() -> assertTrue(result.contains(USE.toString())),
				() -> assertTrue(result.contains(VALUES.toString())),
				() -> assertTrue(result.contains(WHERE.toString())),
				
				() -> assertFalse(result.contains(DATE.toString()))
			);
	
	}
	
	@DisplayName("TokenVisitor Test : AllTokenPrintVisitor가 holub-sql에서 제공하는 token들을 모두 출력하는지")
	@Test
	void test_AllTokenPrintVisitor() {
		// when
		String result = tokens.printAllTokens().toLowerCase();
		// then
		System.out.println("================= TokenVisitor Test ================= ");
		System.out.println(result);
		assertEquals(result,    "simpletoken :     , \r\n" + 
								"simpletoken :     = \r\n" + 
								"simpletoken :     ( \r\n" + 
								"simpletoken :     ) \r\n" + 
								"simpletoken :     . \r\n" + 
								"simpletoken :     * \r\n" + 
								"simpletoken :     / \r\n" + 
								"wordtoken :   and \r\n" + 
								"wordtoken : begin \r\n" + 
								"wordtoken : commit \r\n" + 
								"wordtoken : create \r\n" + 
								"wordtoken : database \r\n" + 
								"wordtoken : delete \r\n" + 
								"wordtoken :  drop \r\n" + 
								"wordtoken :  dump \r\n" + 
								"wordtoken :  from \r\n" + 
								"wordtoken : insert \r\n" + 
								"wordtoken :  into \r\n" + 
								"wordtoken :   key \r\n" + 
								"wordtoken :  like \r\n" + 
								"wordtoken :   not \r\n" + 
								"wordtoken :  null \r\n" + 
								"wordtoken :    or \r\n" + 
								"wordtoken : primary \r\n" + 
								"wordtoken : rollback \r\n" + 
								"wordtoken : select \r\n" + 
								"wordtoken :   set \r\n" + 
								"wordtoken : table \r\n" + 
								"wordtoken : update \r\n" + 
								"wordtoken :   use \r\n" + 
								"wordtoken : values \r\n" + 
								"wordtoken : where \r\n" + 
								"regextoken : work|tran(saction)? \r\n" + 
								"regextoken :  \\+|- \r\n" + 
								"regextoken : (\".*?\")|('.*?') \r\n" + 
								"regextoken : [<>][=>]? \r\n" + 
								"regextoken : [0-9]+(\\.[0-9]+)? \r\n" + 
								"regextoken : (small|tiny|big)?int(eger)? \r\n" + 
								"regextoken : decimal|numeric|real|double \r\n" + 
								"regextoken : (var)?char \r\n" + 
								"regextoken : date(\\s*\\(.*?\\))? \r\n" + 
								"regextoken : [a-za-z_0-9/\\\\:~]+ \r\n");
	}

}
