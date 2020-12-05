package com.holub.text.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.holub.text.BeginToken;
import com.holub.text.RegexToken;
import com.holub.text.SimpleToken;
import com.holub.text.WordToken;

/**
 * @author wkdthf21
 * Print all tokens with token type
 */
public class AllTokenPrintVisitor implements TokenVisitor {
	
	@Override
	public void visit(BeginToken token, StringBuilder sb) {
		sb.append(String.format("%5s : %5s \r\n", token.getClass().getSimpleName(), token.toString()));
	}
	@Override
	public void visit(SimpleToken token, StringBuilder sb) {
		sb.append(String.format("%5s : %5s \r\n", token.getClass().getSimpleName(), token.toString()));
	}
	@Override
	public void visit(RegexToken token, StringBuilder sb) {
		sb.append(String.format("%5s : %5s \r\n", token.getClass().getSimpleName(), token.toString()));
	}
	@Override
	public void visit(WordToken token, StringBuilder sb) {
		sb.append(String.format("%5s : %5s \r\n", token.getClass().getSimpleName(), token.toString()));
	}

}
