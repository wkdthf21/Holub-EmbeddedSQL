package com.holub.text.visitor;

import com.holub.text.BeginToken;
import com.holub.text.RegexToken;
import com.holub.text.SimpleToken;
import com.holub.text.WordToken;

/**
 * @author wkdthf21
 * Print keywords supported by Holub-SQL
 */
public class KeywordPrintVisitor implements TokenVisitor {
	
	private int cnt = 0;
	
	@Override
	public void visit(WordToken token, StringBuilder sb) {
		// TODO Auto-generated method stub
		if(cnt == 0) sb.append("=========== Get keywords supported by Holub-SQL =========== \r\n");
		if(cnt % 3 == 0) sb.append("\r\n");
		cnt++;
		sb.append(String.format("%10s%10s", token.toString().toUpperCase(), "|"));
	}
	
	@Override
	public void visit(BeginToken token, StringBuilder sb) {/*nothing to do*/}
	@Override
	public void visit(SimpleToken token, StringBuilder sb) {/*nothing to do*/}
	@Override
	public void visit(RegexToken token, StringBuilder sb) {/*nothing to do*/}

}
