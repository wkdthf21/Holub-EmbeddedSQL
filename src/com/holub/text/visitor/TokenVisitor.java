package com.holub.text.visitor;

import com.holub.text.BeginToken;
import com.holub.text.RegexToken;
import com.holub.text.SimpleToken;
import com.holub.text.Token;
import com.holub.text.TokenSet;
import com.holub.text.WordToken;

/**
 * @author wkdthf21
 * 
 * support a variety of algorithm about token concrete classes
 * 
 * algorithms for tokens may be added in the future, and
 * Adding those algorithms to token concrete classes weakens cohesion
 * So I used the visitor pattern
 * 
 * @see Token
 * @see TokenSet
 * @see BeginToken
 * @see SimpleToken
 * @see WordToken
 * @see RegexToken
 * @see KeywordPrintVisitor
 * 
 */
public interface TokenVisitor {
	public void visit(BeginToken token, StringBuilder sb);
	public void visit(SimpleToken token, StringBuilder sb);
	public void visit(WordToken token, StringBuilder sb);
	public void visit(RegexToken token, StringBuilder sb);
}
