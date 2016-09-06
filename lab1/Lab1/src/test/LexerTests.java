package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import lexer.Lexer;

import org.junit.Test;

import frontend.Token;
import frontend.Token.Type;
import static frontend.Token.Type.*;

/**
 * This class contains unit tests for your lexer. Currently, there is only one test, but you
 * are strongly encouraged to write your own tests.
 */
public class LexerTests {
	// helper method to run tests; no need to change this
	private final void runtest(String input, Token... output) throws AssertionError {
		Lexer lexer = new Lexer(new StringReader(input));
		int i=0;
		Token actual, expected;
		try {
			do {
				assertTrue(i < output.length);
				expected = output[i++];
				
				try {
					actual = lexer.nextToken();
					
//					System.out.println(actual);
//					System.out.println(expected);
					
					assertEquals(expected, actual);
				} catch(Error e) {
					if (expected != null) {
						throw new AssertionError(e.getMessage());
//						fail(e.getMessage());
					}
					
					return;
				}
			} while(!actual.isEOF());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/** Example unit test. */
	@Test
	public void testKWs() throws AssertionError {
		// first argument to runtest is the string to lex; the remaining arguments
		// are the expected tokens
		runtest("module false return while",
				new Token(MODULE, 0, 0, "module"),
				new Token(FALSE, 0, 7, "false"),
				new Token(RETURN, 0, 13, "return"),
				new Token(WHILE, 0, 20, "while"),
				new Token(EOF, 0, 25, ""));
	}
	
	@Test
	public void testPunc() throws AssertionError {		
		// first argument to runtest is the string to lex; the remaining arguments
		// are the expected tokens
		runtest("( ) { } [ ] , ;",
				new Token(LPAREN, 0, 0, "("),
				new Token(RPAREN, 0, 2, ")"),
				new Token(LCURLY, 0, 4, "{"),
				new Token(RCURLY, 0, 6, "}"),
				new Token(LBRACKET, 0, 8, "["),
				new Token(RBRACKET, 0, 10, "]"),
				new Token(COMMA, 0, 12, ","),
				new Token(SEMICOLON, 0, 14, ";"),
				new Token(EOF, 0, 15, ""));
	}
	
	@Test
	public void testOperators() throws AssertionError {		
		// first argument to runtest is the string to lex; the remaining arguments
		// are the expected tokens
		runtest("/ == = < <= > >= - != * +",
				new Token(DIV, 0, 0, "/"),
				new Token(EQEQ, 0, 2, "=="),
				new Token(EQL, 0, 5, "="),
				new Token(LT, 0, 7, "<"),
				new Token(LEQ, 0, 9, "<="),
				new Token(GT, 0, 12, ">"),
				new Token(GEQ, 0, 14, ">="),
				new Token(MINUS, 0, 17, "-"),
				new Token(NEQ, 0, 19, "!="),
				new Token(TIMES, 0, 22, "*"),
				new Token(PLUS, 0, 24, "+"),
				new Token(EOF, 0, 25, ""));
	}
	
	@Test
	public void testKWsAndPunc() throws AssertionError {		
		// first argument to runtest is the string to lex; the remaining arguments
		// are the expected tokens
		runtest("while ()",
				new Token(WHILE, 0, 0, "while"),
				new Token(LPAREN, 0, 6, "("),
				new Token(RPAREN, 0, 7, ")"),
				new Token(EOF, 0, 8, ""));
	}
	
	@Test
	public void testKWsPuncAndOperators() throws AssertionError {
		runtest("while ( != ) {}", 
				new Token(WHILE, 0, 0, "while"),
				new Token(LPAREN, 0, 6, "("),
				new Token(NEQ, 0, 8, "!="),
				new Token(RPAREN, 0, 11, ")"),
				new Token(LCURLY, 0, 13, "{"),
				new Token(RCURLY, 0, 14, "}"),
				new Token(EOF, 0, 15, ""));
	}
	
	@Test
	public void testValidId() throws AssertionError {
		runtest("_123\ntestvar\ntestvar123\ntestvar911_\nTestVar10\ntest_var\nTEST_VAR\nT\n_", 
				new Token(ID, 0, 0, "_123"),
				new Token(ID, 1, 0, "testvar"),
				new Token(ID, 2, 0, "testvar123"),
				new Token(ID, 3, 0, "testvar911_"),
				new Token(ID, 4, 0, "TestVar10"),
				new Token(ID, 5, 0, "test_var"),
				new Token(ID, 6, 0, "TEST_VAR"),
				new Token(ID, 7, 0, "T"),
				new Token(ID, 8, 0, "_"),
				new Token(EOF, 8, 1, ""));
	}
	
	@Test(expected = AssertionError.class)
	public void testInvalidId_startWithDigit() throws AssertionError {
		runtest("123");
	}
	
	@Test(expected = AssertionError.class)
	public void testInvalidId_includeInvalidChars() throws AssertionError {
		runtest("test|jashd");
	}
	
	@Test
	public void testValidIntLiteral() throws AssertionError {
		runtest("123 \n00123 \n1 \n10000", 
				new Token(INT_LITERAL, 0, 0, "123"),
				new Token(INT_LITERAL, 1, 0, "00123"),
				new Token(INT_LITERAL, 2, 0, "1"),
				new Token(INT_LITERAL, 3, 0, "10000"),
				new Token(EOF, 3, 5, ""));
	}
	
	@Test(expected = AssertionError.class)
	public void testValidInvalidIntLiteral_withAlphabets() throws AssertionError {
		runtest("123a");
	}
	
	@Test(expected = AssertionError.class)
	public void testValidInvalidIntLiteral_withSigns() throws AssertionError {
		runtest("-123");
	}
	
	@Test
	public void testStringLiteral() throws AssertionError {
		runtest("\"abc\" \n\"\\abc\" \n\"123\" \n\"_asd123+-*_\" \n\"\"",
				new Token(STRING_LITERAL, 0, 0, "abc"),
				new Token(STRING_LITERAL, 1, 0, "\\abc"),
				new Token(STRING_LITERAL, 2, 0, "123"),
				new Token(STRING_LITERAL, 3, 0, "_asd123+-*_"),
				new Token(STRING_LITERAL, 4, 0, ""),
				new Token(EOF, 4, 2, ""));
	}

	@Test
	public void testStringLiteralWithDoubleQuote() throws AssertionError {
		runtest("\"\"\"",
				new Token(STRING_LITERAL, 0, 0, ""),
				(Token) null);
	}

	@Test
	public void testStringLiteral2() throws AssertionError {
		runtest("\"\\n\"", 
				new Token(STRING_LITERAL, 0, 0, "\\n"),
				new Token(EOF, 0, 4, ""));

	}
	
	@Test(expected = AssertionError.class)
	public void testStringLiteral_fail1() throws AssertionError {
		runtest("\"s1\"s2\"", 
				new Token(STRING_LITERAL, 0, 0, "s1"),
				new Token(ID, 0, 5, "s2"),
				new Token(EOF, 0, 4, ""));

	}
	
	@Test
	public void testRandom1() throws Exception {
		runtest("if(true){break;}else{break;}", 
				new Token(Type.IF, 0, 0, "if"),
				new Token(Type.LPAREN, 0, 2, "("),
				new Token(Type.TRUE, 0, 3, "true"),
				new Token(Type.RPAREN, 0, 7, ")"),
				new Token(Type.LCURLY, 0, 8, "{"),
				new Token(Type.BREAK, 0, 9, "break"),
				new Token(Type.SEMICOLON, 0, 14, ";"),
				new Token(Type.RCURLY, 0, 15, "}"),
				new Token(Type.ELSE, 0, 16, "else"),
				new Token(Type.LCURLY, 0, 20, "{"),
				new Token(Type.BREAK, 0, 21, "break"),
				new Token(Type.SEMICOLON, 0, 26, ";"),
				new Token(Type.RCURLY, 0, 27, "}"),
				new Token(EOF, 0, 28, "")
		);
	}
	
	@Test
	public void testRandom2() throws AssertionError {
		runtest("\"\\\\\"",
				new Token(STRING_LITERAL, 0, 0, "\\\\"),
				new Token(EOF, 0, 4, ""));
	}
	
	@Test 
	public void testCaseSensitiveKeyword() {
		runtest("Break",
				new Token(Type.ID, 0, 0, "Break"),
				new Token(EOF, 0, 5, ""));
	}
	
	@Test
	public void testOperator() {
		runtest("+-  / * = > < == <= >=!=",
				new Token(PLUS, 0, 0, "+"),
				new Token(MINUS, 0, 1, "-"),
				new Token(DIV, 0, 4, "/"),
				new Token(TIMES, 0, 6, "*"),
				new Token(EQL, 0, 8, "="),
				new Token(GT, 0, 10, ">"),
				new Token(LT, 0, 12, "<"),
				new Token(EQEQ, 0, 14, "=="),
				new Token(LEQ, 0, 17, "<="),
				new Token(GEQ, 0, 20, ">="),
				new Token(NEQ, 0, 22, "!="),
				new Token(EOF, 0, 24, ""));
	}
	
	@Test
	public void testUnaryOperator() {
		runtest("-+10",
				new Token(MINUS, 0, 0, "-"),
				new Token(PLUS, 0, 1, "+"),
				new Token(INT_LITERAL, 0, 2, "10"),
				new Token(EOF, 0, 4, ""));
	}
}
