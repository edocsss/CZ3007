package test;

import static org.junit.Assert.fail;

import java.io.StringReader;

import lexer.Lexer;

import org.junit.Test;

import parser.Parser;

public class ParserTests {
	private void runtest(String src) {
		runtest(src, true);
	}

	private void runtest(String src, boolean succeed) {
		Parser parser = new Parser();
		try {
			parser.parse(new Lexer(new StringReader(src)));
			if(!succeed) {
				fail("Test was supposed to fail, but succeeded");
			}
		} catch (beaver.Parser.Exception e) {
			if(succeed) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		} catch (Throwable e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testEmptyModule() {
		runtest("module Test { }");
	}
	
	@Test
	public void testModuleWithImports() {
		runtest("module Test { import lib1; }");
		runtest("module Test { import lib1; import lib2; }");
		runtest("module Test { import lib1; import lib2; import lib3; }");
	}
	
	@Test
	public void testModuleWithInvalidImports() {
		runtest("module Test { import lib1 }", false);
		runtest("module Test { import lib1 import lib2; }", false);
		runtest("module Test { import lib1; import lib2; import lib3 }", false);
	}
	
	@Test
	public void testModuleWithModuleDeclarations_TypeDeclaration() {
		runtest("module Test { public type t = \"asd\"; }");
		runtest("module Test { type t = \"asd\"; }");
	}
	
	@Test
	public void testModuleWithModuleDeclarations_FieldDeclaration() {
		runtest("module Test { public void t; }");
		runtest("module Test { void t; }");
		runtest("module Test { public int t; }");
		runtest("module Test { int t; }");
		runtest("module Test { public boolean t; }");
		runtest("module Test { boolean t; }");
		
		runtest("module Test { public Class1 t; }");
		runtest("module Test { Class1 t; }");
		
		runtest("module Test { public Class1[] t; }");
		runtest("module Test { Class1[] t; }");
		runtest("module Test { public Class1[][] t; }");
		runtest("module Test { Class1[][] t; }");
		runtest("module Test { public int[] t; }");
		runtest("module Test { int[] t; }");
		runtest("module Test { public int[][] t; }");
		runtest("module Test { int[][] t; }");
		runtest("module Test { int[][][][][] t; }");
	}
	
	@Test
	public void testModuleWithModuleDeclarations_FunctionDeclaration_EmptyStatement() {
		runtest("module Test { public int[][] testArray(int[][] t, boolean x) {} }");
		runtest("module Test { public int[][] testArray(Class1[][] t) {} }");
		runtest("module Test { public int[][] testArray() {} }");
//		runtest("module Test { public int[][] testArray(int[][] t,) {} }", false);
	}
	
	@Test
	public void testModuleWithModuleDeclarations_FunctionDeclaration_LocalVariable() {
		runtest("module Test{ public int[][] testArray() { void t; } }");
		runtest("module Test{ public int[][] testArray() { void t; int x; } }");
		runtest("module Test{ public int[][] testArray() { while (x) {} } }");
//		runtest("module Test{ public int[][] testArray() { void t int x; } }", false);
	}
	
	@Test
	public void testModuleWithModuleDeclarations_FunctionDeclaration_BreakStatement() {
		runtest("module Test{ public int[][] testArray() { break; } }");
	}
	
	@Test
	public void testModuleWithModuleDeclarations_FunctionDeclaration_ReturnStatement() {
		runtest("module Test{ public int[][] testArray() { return; } }");
		runtest("module Test{ public int[][] testArray() { return -100 + 100 - 240 == -80 * 125 * 80; } }");
		runtest("module Test{ public int[][] testArray() { return -100; } }");
		runtest("module Test{ public int[][] testArray() { return -100 + 100 + 240 <= \"Test 1\"; } }");
		runtest("module Test{ public int[][] testArray() { return false != true; } }");
		runtest("module Test{ public int[][] testArray() { return test() > (100 * 200 / 300 % 100); } }");
		runtest("module Test{ public int[][] testArray() { return test(100, \"test\", true, false) > (100 * 200 / 300 % 100); } }");
		runtest("module Test{ public int[][] testArray() { return [1, 2, 3, true, false, test(199, \"testasd\")] >= id1; } }");
		runtest("module Test{ public int[][] testArray() { return arr[getIndex(100)] >= id1; } }");
		runtest("module Test{ public int[][] testArray() { return a = 100 * -100; } }");
		runtest("module Test{ public int[][] testArray() { return a[(100 * 100)] = 100 * -100; } }");
		
//		runtest("module Test{ public int[][] testArray() { return f(a,); } }", false);
//		runtest("module Test{ public int[][] testArray() { return [] } }", false);
//		runtest("module Test{ public int[][] testArray() { return [a, ] } }", false);
	}
	
	@Test
	public void testModuleWithModuleDeclarations_FunctionDeclaration_ExpressionStatement() {
		runtest("module Test{ public int[][] testArray() { -100 + 100 - 240 == -80 * 125 * 80; } }");
		runtest("module Test{ public int[][] testArray() { -100; } }");
		runtest("module Test{ public int[][] testArray() { -100 + 100 + 240 <= \"Test 1\"; } }");
		runtest("module Test{ public int[][] testArray() { false != true; } }");
		runtest("module Test{ public int[][] testArray() { test() > (100 * 200 / 300 % 100); } }");
		runtest("module Test{ public int[][] testArray() { test(100, \"test\", true, false) > (100 * 200 / 300 % 100); } }");
		runtest("module Test{ public int[][] testArray() { [1, 2, 3, true, false, test(199, \"testasd\")] >= id1; } }");
//		runtest("module Test{ public int[][] testArray() { arr[getIndex(100)] >= id1; } }");
		runtest("module Test{ public int[][] testArray() { a = 100 * -100; } }");
		runtest("module Test{ public int[][] testArray() { arr[] = 100 * -100; } }");
		
		// Conflicts between ARrayType and ArrayAccess (Shift or Reduce conflict)
		// arr[]
		// arr[123]
		
//		runtest("module Test{ public int[][] testArray() { return f(a,); } }", false);
//		runtest("module Test{ public int[][] testArray() { return [] } }", false);
//		runtest("module Test{ public int[][] testArray() { return [a, ] } }", false);
	}
}
