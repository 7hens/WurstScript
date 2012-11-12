package de.peeeq.wurstscript.tests;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.experimental.categories.Categories.ExcludeCategory;

import de.peeeq.wurstscript.jassinterpreter.DebugPrintError;

public class ClassesTests extends PscriptTest {
	
	private static final String TEST_DIR = "./testscripts/valid/classes/";
	private static final String TEST_DIR2 = "./testscripts/concept/";

	@Test
	public void classes1() throws IOException {
		testAssertOkFile(new File(TEST_DIR + "Classes_1.pscript"), true);
	}

	@Test
	public void classes_construct() throws IOException {
		testAssertOkFile(new File(TEST_DIR + "Classes_construct.pscript"), true);
	}
	
	@Test
	public void OverrideClass() throws IOException {
		testAssertOkFile(new File(TEST_DIR2 + "OverrideTest.wurst"), false);
	}

	@Test
	public void classes_lifecycle() throws IOException {
		testAssertOkFile(new File(TEST_DIR + "Classes_lifecycle.pscript"), true);
	}

	@Test
	public void classes_method_implicit() throws IOException {
		testAssertOkFile(new File(TEST_DIR + "Classes_method_implicit.pscript"), true);
	}

	@Test
	public void classes_method() throws IOException {
		testAssertOkFile(new File(TEST_DIR + "Classes_method.pscript"), true);
	}
	
	@Test
	public void classes_static_func() {
		testAssertErrorsLines(false, "static", 
				"package test",
				"	class C",
				"		function foo() returns int",
				"			return 3",
				"		static function bar()",
				"			foo()",
				"endpackage"
			);
	}

	@Test
	public void classes_static_var() {
		testAssertErrorsLines(false, "static", 
				"package test",
				"	class C",
				"		static int size = 0",
				"		function foo() returns int",
				"			return this.size",
				"endpackage"
			);
	}
	
	@Test
	public void classes_static_var2() {
		testAssertErrorsLines(false, "static", 
				"package test",
				"	class C",
				"		static int size = 0",
				"		function foo() returns int",
				"			this.size++",
				"			return this.size",
				"endpackage"
			);
	}
	
	
	@Test
	public void array_members() {
		testAssertErrorsLines(false, "must be static", 
				"package test",
				"	class C",
				"		int array blub",
				"endpackage"
			);
	}

	@Test
	public void classes_static_var_get() {
		testAssertErrorsLines(false, "static", 
				"package test",
				"	class C",
				"		int i = 3",
				"		static function foo() returns int",
				"			return i",
				"endpackage"
			);
	}
	
		@Test
	public void constantVars() {
		testAssertErrorsLines(false, "Grammatical error", 
				"package test",
				"	class C",
				"		constant int i",
				"		function foo() returns int",
				"			i++",
				"			return i",
				"endpackage"
			);
	}
	
		
		
	
	@Test
	public void classes_static_var_set() {
		testAssertErrorsLines(false, "static", 
				"package test",
				"	class C",
				"		int i = 3",
				"		static function foo(int j)",
				"			i = j",
				"endpackage"
			);
	}
	
	@Test
	public void classes_static_var_set2() {
		testAssertErrorsLines(false, "static", 
				"package test",
				"	class C",
				"		int i = 3",
				"		static function foo(int j)",
				"			this.i = j",
				"endpackage"
			);
	}
	

	@Test
	public void classes_double_defined() {
		testAssertErrorsLines(false, "already defined", 
				"package test",
				"	class C",
				"		function foo() returns int",
				"			return 3",
				"		static function foo() returns int",
				"			return 4",
				"endpackage"
			);
	}
	
	@Test
	public void static_field() {
		testAssertOkLines(true, 
				"package test",
				"	native testSuccess()",
				"	class C",
				"		static int i = 3",
				"	init",
				"			C.i++",
				"			if C.i == 4",
				"				testSuccess()",
				"endpackage"
			);
	}
	
	@Test
	public void static_field_other_package() {
		testAssertOkLines(true,
				"package Blub",
				"	public class C",
				"		static int i = 3",
				"endpackage",
				"package test",
				"	import Blub",
				"	native testSuccess()",
				"	init",
				"		C.i++",
				"		if C.i == 4",
				"			testSuccess()",
				"endpackage"
			);
	}
	
	
	@Test
	public void static_static_array_field() {
		testAssertOkLines(true,
				"package Blub",
				"	public class C",
				"		static int array xs",
				"		static function setX(int i, int x)",
				"			xs[i] = x",
				"		static function getX(int i) returns int",
				"			return xs[i]",
				"endpackage",
				"package test",
				"	import Blub",
				"	native testSuccess()",
				"	init",
				"		C.setX(7, 4)",
				"		if C.getX(7) == 4",
				"			testSuccess()",
				"endpackage"
			);
	}
	
	
	@Test
	public void ondestroy() {
		testAssertOkLines(false, 
				"package test",
				"	native testSuccess()",
				"	class C",
				"		int i = 3",
				"		ondestroy",
				"			i = i + 1",
				"			testSuccess()",
				"	init",
				"		destroy new C()",
				"endpackage"
			);
	}
	

	@Test
	public void recyling() {
		testAssertOkLines(true, 
				"package test",
				"	native testSuccess()",
				"	native println(string msg)",
				"	native I2S(int i) returns string",
				"	class C",
				"		int i",
				"",
				"	init",
				"		C array cs",
				"		for int i = 0 to 6000",
				"			cs[i] = new C()",
				"		for int j = 0 to 6000",
				"			destroy cs[j]",
				"		for int k = 0 to 6000",
				"			cs[k] = new C()",
				"			println(I2S(cs[k] castTo int))",
				"		if cs[6000] castTo int <= 6001",
				"			testSuccess()",
				"endpackage"
			);
	}
	
	@Test
	public void cast_class() {
		testAssertOkLines(true, 
				"package test",
				"	native testSuccess()",
				"	class A",
				"	class B extends A",
				"	init",
				"		A a = new B()",
				"		if a instanceof B",
				"			B b = a castTo B",
				"			testSuccess()",
				"endpackage"
			);
	}
	
	@Test
	public void cast_class2() {
		testAssertOkLines(true, 
				"package test",
				"	native testSuccess()",
				"	class A",
				"	class B extends A",
				"	class C extends B",
				"	init",
				"		A a = new C()",
				"		if a instanceof B",
				"			B b = a castTo B",
				"			testSuccess()",
				"endpackage"
			);
	}
	
	@Test
	public void cast_class_unrelated() {
		testAssertErrorsLines(true, "not directly related", 
				"package test",
				"	native testSuccess()",
				"	class A",
				"	class B",
				"	init",
				"		A a = new A()",
				"		if a instanceof B",
				"			B b = a castTo B",
				"			testSuccess()",
				"endpackage"
			);
	}
	
	
	@Test
	public void override() {
		testAssertErrorsLines(false, "uses override", 
				"package test",
				"	native testSuccess()",
				"	class A",
				"		override function foo()",
				"			skip",
				"endpackage"
			);
	}
	
	@Test
	public void override_valid() {
		testAssertOkLines(true, 
				"package test",
				"	native testSuccess()",
				"	class A",
				"		function foo() returns int",
				"			return 7",
				"	class B extends A",
				"		override function foo() returns int",
				"			return 8",
				"	init",
				"		A b = new B()",
				"		if b.foo() == 8",
				"			testSuccess()",
				"endpackage"
			);
	}
	
	@Test
	public void override_valid_trans() {
		testAssertOkLines(true, 
				"package test",
				"	native testSuccess()",
				"	class A",
				"		function foo() returns int",
				"			return 7",
				"	class B extends A",
				"		override function foo() returns int",
				"			return 8",
				"	class C extends B",
				"	init",
				"		A c = new C()",
				"		if c.foo() == 8",
				"			testSuccess()",
				"endpackage"
			);
	}
	
	@Test
	public void override_valid_void() {
		testAssertOkLines(true, 
				"package test",
				"	native testSuccess()",
				"	int i",
				"	class A",
				"		function foo()",
				"			i = 7",
				"	class B extends A",
				"		override function foo()",
				"			i = 8",
				"	init",
				"		A b = new B()",
				"		b.foo()",
				"		if i == 8",
				"			testSuccess()",
				"endpackage"
			);
	}
	
	@Test
	public void override_valid_trans_big() {
		testAssertOkLines(true, 
				"package test",
				"	native testSuccess()",
				"	native testFail(string msg)",
				"	class A",
				"		function foo() returns int",
				"			return 7",
				"	class B extends A",
				"		override function foo() returns int",
				"			return 8",
				"	class B1 extends B",
				"	class B2 extends B",
				"	class B11 extends B1",
				"	class B111 extends B11",
				"	class C extends A",
				"		override function foo() returns int",
				"			return 9",
				"	class C1 extends C",
				"	class C2 extends C",
				"	class C11 extends C1",
				"	class C111 extends C11",
				"	init",
				"		A a = new C11()",
				"		if a.foo() != 9",
				"			testFail(\"c11\")",
				"		a = new B11()",
				"		if a.foo() != 8",
				"			testFail(\"b11\")",
				"		a = new C()",
				"		if a.foo() != 9",
				"			testFail(\"C\")",
				"		a = new A()",
				"		if a.foo() != 7",
				"			testFail(\"A\")",
				
				
				"		testSuccess()",
				"endpackage"
			);
	}
	
	@Test(expected=DebugPrintError.class)
	public void NPE() {
		testAssertOkLines(true, 
				"package test",
				"	native testSuccess()",
				"	class A",
				"		function foo() returns int",
				"			return 7",
				"	init",
				"		A a = null",
				"		if a.foo() == 7",
				"			testSuccess()",
				"endpackage"
			);
	}
	
	@Test(expected=DebugPrintError.class)
	public void destroyed() {
		testAssertOkLines(true, 
				"package test",
				"	native testSuccess()",
				"	class A",
				"		function foo() returns int",
				"			return 7",
				"	init",
				"		A a = new A()",
				"		destroy a",
				"		if a.foo() == 7",
				"			testSuccess()",
				"endpackage"
			);
	}
	
	@Test
	public void abstract_class() {
		testAssertOkLines(true, 
				"package test",
				"	native testSuccess()",
				"	int i",
				"	abstract class A",
				"		abstract function foo()",
				"	class B extends A",
				"		override function foo()",
				"			i = 8",
				"	init",
				"		A b = new B()",
				"		b.foo()",
				"		if i == 8",
				"			testSuccess()",
				"endpackage"
			);
	}
	
	@Test
	public void abstract_class2() {
		testAssertOkLines(true, 
				"package test",
				"	native testSuccess()",
				"	int i",
				"	abstract class A",
				"		abstract function foo() returns int",
				"	class B extends A",
				"		override function foo() returns int",
				"			i = 8",
				"			return i",
				"	init",
				"		A b = new B()",
				"		if b.foo() == 8",
				"			testSuccess()",
				"endpackage"
			);
	}
	
	@Test
	public void abstract_fail() {
		testAssertErrorsLines(true, "Cannot create an instance of the abstract class A", 
				"package test",
				"	native testSuccess()",
				"	int i",
				"	abstract class A",
				"		abstract function foo() returns int",
				"	init",
				"		A b = new A()",
				"		if b.foo() == 8",
				"			testSuccess()",
				"endpackage"
			);
	}
	

	@Test
	public void abstract_fail2() {
		testAssertErrorsLines(true, "must implement the abstract function foo", 
				"package test",
				"	native testSuccess()",
				"	int i",
				"	abstract class A",
				"		abstract function foo() returns int",
				"	abstract class B extends A",
				"	class C extends B",
				"endpackage"
			);
	}
}
