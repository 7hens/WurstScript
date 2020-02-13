package tests.wurstscript.tests;

import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

public class GenericsWithTypeclassesTests extends WurstScriptTest {


    @Test
    public void identity() {
        testAssertOkLines(true,
            "package test",
            "	native testSuccess()",
            "	function identity<A:>(A a) returns A",
            "		return a",
            "	init",
            "		int x = identity(3)",
            "		string s = identity(\"a\")",
            "		if x == 3 and s == \"a\"",
            "			testSuccess()",
            "endpackage"
        );
    }

    @Test
    public void identityTrans() {
        testAssertOkLines(true,
            "package test",
            "	native testSuccess()",
            "	function identity1<A:>(A a) returns A",
            "		return a",
            "	function identity2<B:>(B a) returns B",
            "		return identity1(a)",
            "	function identity3<C:>(C a) returns C",
            "		return identity2(a)",
            "	init",
            "		int x = identity3(3)",
            "		string s = identity3(\"a\")",
            "		if x == 3 and s == \"a\"",
            "			testSuccess()",
            "endpackage"
        );
    }

    @Test
    public void identityRec() {
        testAssertOkLines(true,
            "package test",
            "	native testSuccess()",
            "	function identity<A:>(int i, A a) returns A",
            "		if i > 0",
            "			return identity(i - 1, a)",
            "		return a",
            "	init",
            "		int x = identity(5, 3)",
            "		string s = identity(5, \"a\")",
            "		if x == 3 and s == \"a\"",
            "			testSuccess()",
            "endpackage"
        );
    }

    @Test
    @Ignore // TODO
    public void identityRecTypeCreation() {
        testAssertErrorsLines(true, "some error message",
            "package test",
            "	native testSuccess()",
            "	class C<T>",
            "		construct(T t)",
            "	function blub<A:>(int i, A a) returns int",
            "		if i <= 0",
            "			return 0",
            "		return 1 + blub<C<A>>(i-1, new C(a))",
            "	init",
            "		int x = blub(5, 3)",
            "		if x == 5",
            "			testSuccess()",
            "endpackage"
        );
    }

    @Test
    public void identityRecMut() {
        testAssertOkLines(true,
            "package test",
            "	native testSuccess()",
            "	function identity1<A:>(int i, A a) returns A",
            "		if i > 0",
            "			return identity2(i - 1, a)",
            "		return a",
            "	function identity2<A:>(int i, A a) returns A",
            "		if i > 0",
            "			return identity1(i - 1, a)",
            "		return a",
            "	init",
            "		int x = identity1(5, 3)",
            "		string s = identity1(5, \"a\")",
            "		if x == 3 and s == \"a\"",
            "			testSuccess()",
            "endpackage"
        );
    }

    @Test
    public void extensionFunc() {
        testAssertOkLines(true,
            "package test",
            "	native testSuccess()",
            "	function boolean.choice<A:>(A x, A y) returns A",
            "		if this",
            "			return x",
            "		return y",
            "	init",
            "		int x = true.choice(5, 3)",
            "		string s = false.choice(\"a\", \"b\")",
            "		if x == 5 and s == \"b\"",
            "			testSuccess()",
            "endpackage"
        );
    }


    @Test
    public void extensionFuncReceiver() {
        testAssertOkLines(true,
            "package test",
            "	native testSuccess()",
            "	function A.choice<A:>(boolean b, A y) returns A",
            "		if b",
            "			return this",
            "		return y",
            "	init",
            "		int x = (5).choice(true, 3)",
            "		string s = \"a\".choice(false, \"b\")",
            "		if x == 5 and s == \"b\"",
            "			testSuccess()",
            "endpackage"
        );
    }

    @Test
    public void genericsDispatch() {
        testAssertOkLines(true,
            "package Test",
            "native testSuccess()",
            "class Cell<T:>",
            "	T o",
            "init",
            "	Cell<int> x = new Cell<int>()",
            "	Cell<string> y = new Cell<string>()",
            "	x.o = 3",
            "	y.o = \"a\"",
            "	if x.o == 3 and y.o == \"a\"",
            "		testSuccess()"
        );
    }


    @Test
    public void identity2() {
        testAssertOkLines(true,
            "package test",
            "	native testSuccess()",
            "	class C",
            "	function identity<A:>(A a) returns A",
            "		return a",
            "	init",
            "		C a = new C()",
            "		C b = identity(a)",
            "		if a == b",
            "			testSuccess()",
            "endpackage"
        );
    }


    @Test
    public void function() {
        testAssertOkLines(true,
            "package test",
            "	native testSuccess()",
            "	class List<T:>",
            "		function iterator() returns Iterator<T>",
            "			return new Iterator<T>(this)",
            "	class Iterator<S:>",
            "		S t",
            "		construct(List<S> t)",
            "			int x = 1",
            "		function hasNext() returns boolean",
            "			return true",
            "		function next() returns S",
            "			return t",
            "	class A",
            "	class B",
            "	class C",
            "	init",
            "		List<B> a = new List<B>()",
//				"		for B b in a",
            "		Iterator<B> iterator = a.iterator()",
            "		while iterator.hasNext()",
            "			B b = iterator.next()",
            "			testSuccess()",
            "endpackage"
        );
    }

    @Test
    public void testSubtypeGenericClass() {
        testAssertOkLines(false,
            "package test",
            "	class A<T:>",
            "	class B<S:> extends A<S>",
            "	init",
            "		A<int> x = new B<int>",
            "endpackage"
        );
    }

    @Test
    public void testSubtypeGenericClass2() {
        testAssertOkLines(false,
            "package test",
            "	class A<T:>",
            "	class B<S:> extends A<S>",
            "	function foo<X>()",
            "		A<X> x = new B<X>",
            "endpackage"
        );
    }

    @Test
    public void testSubtypeGenericInterface() {
        testAssertOkLines(false,
            "package test",
            "	interface I<T:>",
            "	class B<S:> implements I<S>",
            "	init",
            "		I<int> x = new B<int>",
            "endpackage"
        );
    }

    @Test
    public void identityFail1() {
        testAssertOkLines(true,
            "package test",
            "	native testSuccess()",
            "	function identity<A:>(A a) returns A",
            "		return a",
            "	init",
            "		real x = identity(3.14)",
            "		if x == 3.14",
            "			testSuccess()",
            "endpackage"
        );
    }

    @Test
    public void identityFail2() {
        testAssertErrorsLines(true, "Cannot assign C to real",
            "package test",
            "	function identity<A:>(A a) returns A",
            "		return a",
            "	class C",
            "		int y",
            "	init",
            "		real x = identity(new C())",
            "endpackage"
        );
    }


    @Test
    public void cellExample() {
        testAssertErrorsLines(true, "Wrong parameter type",
            "package test",
            "	native testSuccess()",
            "	class Cell<T:>",
            "		T elem",
            "		function set(T t)",
            "			elem = t",
            "		function get() returns T",
            "			return elem",
            "	class A",
            "	class B",
            "	init",
            "		Cell<A> c = new Cell<A>()",
            "		c.set(new B())",
            "endpackage"
        );
    }

    @Test
    public void implicitConversions() {
        testAssertOkLines(true,
            "package test",
            "	native testSuccess()",
            "	class Cell<T:>",
            "		T elem",
            "		function set(T t)",
            "			elem = t",
            "		function get() returns T",
            "			return elem",
            "",
            "	tuple bla(int z, int y)",
            "	init",
            "		Cell<bla> c = new Cell<bla>()",
            "		c.set(bla(5, 3))",
            "		if c.get() == bla(5, 3)",
            "			testSuccess()",
            "endpackage"
        );
    }

    @Test
    public void implicitConversions2() {
        testAssertOkLines(true,
            "package test",
            "	native testSuccess()",
            "	class Cell<T:>",
            "		T elem",
            "		function set(T t)",
            "			elem = t",
            "		function get() returns T",
            "			return elem",
            "",
            "	tuple bla(int z, int y)",
            "	init",
            "		Cell<bla> c = new Cell<bla>()",
            "		c.set(bla(5, 3))",
            "		c.set(c.get())",
            "		if c.get() == bla(5, 3)",
            "			testSuccess()",
            "endpackage"
        );
    }

    @Test
    public void implicitConversions3() {
        testAssertOkLines(true,
            "package test",
            "	native testSuccess()",
            "	public interface FoldClosure<T:, Q:>",
            "		function apply(T t, Q q) returns Q",
            "	class Cell<T:>",
            "		T elem",
            "		function set(T t)",
            "			elem = t",
            "		function get() returns T",
            "			return elem",
            "		function fold<Q:>(Q start, FoldClosure<T,Q> f) returns Q",
            "			return f.apply(elem, start)",
            "",
            "	tuple bla(int z, int y)",
            "	init",
            "		Cell<bla> c = new Cell<bla>()",
            "		c.set(bla(5, 3))",
            "		let x = c.fold(2, (e, a) -> e.z + a)",
            "		if x == 7",
            "			testSuccess()",
            "endpackage"
        );
    }

    @Test
    public void implicitConversions4() { // #490
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "interface TFunc<T:>",
            "    abstract function run(T t)",
            "",
            "function runFunc(TFunc<bool> func)",
            "    func.run(false)",
            "",
            "init",
            "    runFunc( (bool b) -> begin",
            "        testSuccess()",
            "    end )"
        );
    }

    @Test
    public void implicitConversions5() { // #490
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "@extern native R2I(real r) returns int",
            "@extern native R2S(real r) returns string",
            "native println(string s)",
            "interface F<A:,R:>",
            "	function apply(A a) returns R",
            "class Cell<T:>",
            "	T elem",
            "	construct(T t)",
            "		this.elem = t",
            "	function get() returns T",
            "		return elem",
            "	function map<R:>(F<T,R> f) returns Cell<R>",
            "		return new Cell(f.apply(elem))",
            "function real.assertEquals(real expected)",
            "	if this == expected",
            "		testSuccess()",
            "	else",
            "		println(R2S(this))",
            "init",
            "	let a = new Cell(5)",
            "	let b = a.map(i -> i*10.)",
            "	b.get().assertEquals(50)"
        );
    }

    @Test
    public void implicitConversionsFail() {
        testAssertOkLines(true,
            "package test",
            "	native testSuccess()",
            "	class Cell<T:>",
            "		T elem",
            "		function set(T t)",
            "			elem = t",
            "		function get() returns T",
            "			return elem",
            "",
            "	tuple bla(int z, int y)",
            "	init",
            "		Cell<bla> c = new Cell<bla>()",
            "		c.set(bla(3,4))",
            "		if c.get() == bla(3,4)",
            "			testSuccess()",
            "endpackage"
        );
    }


    @Test
    public void implicitConversionsAssign() {
        testAssertOkLines(false,
            "type unit extends handle",
            "package test",
            "	native testSuccess()",
            "	class Cell<T:>",
            "		T elem",
            "		function set(T t)",
            "			elem = t",
            "		function get() returns T",
            "			return elem",
            "	init",
            "		Cell<unit> c = new Cell<unit>()",
            "		Cell<unit> c2 = c",
            "endpackage"
        );
    }

    @Test
    public void nativeTypes() {
        testAssertOkLines(false,
            "type effect extends handle",
            "package Test",
            "class L<T:>",
            "init",
            "	L<effect> l = new L<effect>()"
        );
    }


    @Test
    public void implicitConversionFailSimple() { // see bug #121
        testAssertErrorsLines(false, "cast expression not defined for expression type int",
            "type effect extends handle",
            "package Test",

            "class List<T:>",
            "	function get() returns T",
            "		return 0 castTo T",

            "init",
            "	List<effect> fxs = new List<effect>()",
            "	let f = fxs.get()",
            "endpackage");
    }


    @Test
    public void cast() {
        testAssertOkLines(false,
            "package Test",
            "native testSuccess()",
            "class Cell<T:>",
            "	T o",
            "class A",
            "	function foo() returns int",
            "		return 5",
            "class B extends A",
            "	override function foo() returns int",
            "		return 6",
            "init",
            "	Cell<A> c = new Cell<A>()",
            "	c.o = new B()",
            "	B b = c.o castTo B"
        );
    }

    @Test
    public void genericsDispatch2() {
        testAssertOkLines(true,
            "package Test",
            "native testSuccess()",
            "class Cell<T:>",
            "	T o",
            "class A",
            "	function foo() returns int",
            "		return 5",
            "class B extends A",
            "	override function foo() returns int",
            "		return 6",
            "init",
            "	Cell<A> c = new Cell<A>()",
            "	c.o = new B()",
            "	if c.o.foo() == 6",
            "		testSuccess()"
        );
    }

    @Test
    public void genericsSubstitute1() {
        testAssertOkLines(false,
            "package Test",
            "native testSuccess()",
            "class A<T:>",
            "	function bla(T t)",
            "class B extends A<C>",
            "class C",
            "init",
            "	let b = new B",
            "	b.bla(new C)"
        );
    }

    @Test
    public void genericsSubstitute2() {
        testAssertOkLines(false,
            "package Test",
            "native testSuccess()",
            "interface I<S:,T:>",
            "	function bla(T t, S s)",
            "		skip",
            "class A<U:> implements I<U,D>",
            "class B extends A<C>",
            "class C",
            "class D",
            "init",
            "	let b = new B",
            "	b.bla(new D, new C)"
        );
    }

    @Test
    public void genericsSubstitute3() {
        testAssertOkLines(false,
            "package Test",
            "native testSuccess()",
            "interface I<S:,T:>",
            "	function bla(T t, S s)",
            "		skip",
            "interface J<T:,S:> extends I<S,T>",
            "	function foo()",
            "		skip",
            "class A<U:> implements J<D,U>",
            "class B extends A<C>",
            "class C",
            "class D",
            "init",
            "	let b = new B",
            "	b.bla(new D, new C)"
        );
    }

    @Test
    public void genericsSubstitute() {
        testAssertOkLines(false,
            "package Test",
            "class A<T:>",
            "	function bla(T a)",
            "class B extends A<MyType>",
            "	function do()",
            "		bla(new MyType)",
            "class MyType"

        );
    }

    @Test
    public void genericsSubstitute_override() {
        testAssertOkLines(false,
            "package Test",
            "class A<T:>",
            "	function bla(T a)",
            "class B extends A<MyType>",
            "	override function bla(MyType t)",
            "		skip",
            "class MyType"

        );
    }


    @Test
    public void genericsSubstitute_override_interface() {
        testAssertOkLines(false,
            "package Test",
            "interface I<S:,T:>",
            "	function bla(S s, T t)",
            "interface J<T:> extends I<int,T>",
            "	function foo(T t)",
            "class B implements J<MyType>",
            "	override function bla(int s, MyType t)",
            "		skip",
            "	override function foo(MyType t)",
            "class MyType"

        );
    }

    @Test
    public void genericsSubstitute_override_interface_fail() {
        testAssertErrorsLines(false, "Parameter int s should have type MyType to override function bla",
            "package Test",
            "interface I<T:,S:>",
            "	function bla(S s, T t)",
            "interface J<T:> extends I<int,T>",
            "	function foo(T t)",
            "class B implements J<MyType>",
            "	override function bla(int s, MyType t)",
            "		skip",
            "	override function foo(MyType t)",
            "class MyType"

        );
    }


    @Test
    public void genericMethod1() {
        testAssertOkLines(false,
            "package Test",
            "class Blub<T:>",
            "function bla<T:>(Blub<T> t)",
            "init",
            "	bla(new Blub<int>)"
        );
    }

    @Test
    public void genericExtensionMethod1() {
        testAssertOkLines(false,
            "package Test",
            "class Blub<T:>",
            "function Blub<T>.bla<T:>()",
            "	skip",
            "init",
            "	new Blub<int>.bla()"
        );
    }

    @Test
    public void genericReturnOverride() {
        testAssertErrorsLines(false, "Cannot return null, expected expression of type T",
            "package Test",
            "interface I<T:>",
            "	function f() returns T",
            "class C<T:> implements I<T>",
            "	function f() returns T",
            "		return null"
        );
    }

    @Test
    public void genericReturnOverride2() {
        testAssertOkLines(false,
            "package Test",
            "interface I<S:>",
            "	function f(S t) returns S",
            "class C<T:> implements I<T>",
            "	function f(T t) returns T",
            "		return t"
        );
    }

    @Test
    public void genericRecursive() {
        testAssertOkLines(false,
            "package Test",
            "public class C<K:>",
            "	C<K> x",
            "	function foo()",
            "		this.x.x = null"
        );
    }

    @Test
    public void genericRecursive2() {
        testAssertOkLines(false,
            "package Test",
            "public class C<K:>",
            "	C<K> x",
            "	function foo()",
            "		C<K> c = new C<K>",
            "		c.x.x = null"
        );
    }

    @Test
    public void genericChain1() {
        testAssertOkLines(false,
            "package Test",
            "class A",
            "public class C<K:>",
            "	K x",
            "init",
            "	C<C<C<A>>> c = null",
            "	c.x.x.x = new A"
        );
    }

    @Test
    public void genericChain1Err() {
        testAssertErrorsLines(false, "Cannot assign",
            "package Test",
            "class A",
            "public class C<K:>",
            "	K x",
            "init",
            "	C<C<C<A>>> c = null",
            "	c.x.x = new A"
        );
    }

    @Test
    public void genericChain2() {
        testAssertOkLines(false,
            "package Test",
            "class A",
            "public class C<K:>",
            "	C<C<K>> x",
            "init",
            "	C<A> c = null",
            "	c.x.x.x = new C<C<C<C<A>>>>"
        );
    }

    @Test
    public void genericChain2ErrA() {
        testAssertErrorsLines(false, "Cannot assign",
            "package Test",
            "class A",
            "public class C<K:>",
            "	C<C<K>> x",
            "init",
            "	C<A> c = null",
            "	c.x.x.x = new C<C<C<C<C<A>>>>>"
        );
    }

    @Test
    public void genericChain2ErrB() {
        testAssertErrorsLines(false, "Cannot assign",
            "package Test",
            "class A",
            "public class C<K:>",
            "	C<C<K>> x",
            "init",
            "	C<A> c = null",
            "	c.x.x.x = new C<C<C<A>>>"
        );
    }

    @Test
    public void implicitsWithClass() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "interface Comparison<T:>",
            "	function leq(T t, T u) returns bool",
            "class BoolComp implements Comparison<bool>",
            "	override function leq(bool a, bool b) returns bool",
            "		return not a or b",
            "Comparison<bool> bc = new BoolComp",
            "init",
            "	if bc.leq(false, true)",
            "		testSuccess()"
        );
    }

    @Test
    public void implicitsWithClass2() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "class Comparison<T:>",
            "	function leq(T t, T u) returns bool",
            "		return true",
            "class BoolComp extends Comparison<bool>",
            "	override function leq(bool a, bool b) returns bool",
            "		return not a or b",
            "Comparison<bool> bc = new BoolComp",
            "init",
            "	if bc.leq(false, true)",
            "		testSuccess()"
        );
    }

    @Test
    public void implicitsWithClass3() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "class Comparison<T:>",
            "	function leq(T t, T u) returns bool",
            "		return true",
            "class ComparisonX<U:> extends Comparison<U>",
            "class BoolComp extends ComparisonX<bool>",
            "	override function leq(bool a, bool b) returns bool",
            "		return not a or b",
            "ComparisonX<bool> bc = new BoolComp",
            "init",
            "	if bc.leq(false, true)",
            "		testSuccess()"
        );
    }

    @Test
    public void implicitsWithClosures() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "interface Comparison<T:>",
            "	function leq(T t, T u) returns bool",
            "Comparison<bool> bc = (bool a, bool b) -> not a or b",
            "init",
            "	if bc.leq(false, true)",
            "		testSuccess()"
        );
    }


    @Test
    public void genericForIn() {
        testAssertErrorsLines(true, "cast expression not defined for expression type int",
            "package test",
            "native testSuccess()",
            "class C<T:>",
            "	function iterator() returns Iterator<T>",
            "		return new Iterator<T>()",
            "class Iterator<T:>",
            "	private int i = 0",
            "	function next() returns T",
            "		i = i + 1",
            "		return i castTo T",
            "	function hasNext() returns boolean",
            "		return i < 10",
            "	function close()",
            "		destroy this",
            "init",
            "	let c = new C<int>",
            "	for i in c",
            "		if i == 5",
            "			testSuccess()"
        );
    }

    @Test
    @Ignore
    public void genericForFrom() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "class C<T:>",
            "	function iterator() returns Iterator<T>",
            "		return new Iterator<T>()",
            "class Iterator<T:>",
            "	private int i = 0",
            "	function next() returns T",
            "		i = i + 1",
            "		return i castTo T",
            "	function hasNext() returns boolean",
            "		return i < 10",
            "init",
            "	let c = new C<int>",
            "	let iter = c.iterator()",
            "	for i from iter",
            "		if i == 5",
            "			testSuccess()"
        );
    }

    @Test
    public void genericOverload() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "class C<T:>",
            "	private T x",
            "	construct(T x)",
            "		this.x = x",
            "	function foo(T t)",
            "		foo(new C(t))",
            "	function foo(C<T> t)",
            "		testSuccess()",
            "init",
            "	let c = new C(1)",
            "	c.foo(1)"
        );
    }

    @Test
    public void genericOverload2() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "class C<T:>",
            "	private T x",
            "	construct(T x)",
            "		this.x = x",
            "	function foo(T t)",
            "		foo(new C(t))",
            "	function foo(C<T> t)",
            "		testSuccess()",
            "	function test()",
            "		let c = new C(1)",
            "		c.foo(1)",
            "init",
            "	new C(1).test()"
        );
    }

    @Test
    public void inferType() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "function id(int x) returns int",
            "	return x",
            "class C<T:>",
            "	var x = id(4)",
            "init",
            "	let x= new C<int>",
            "	if x.x == 4",
            "		testSuccess()"
        );
    }

    @Test
    public void simpleFunctionCall() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "class C<T:>",
            "	function foo() returns int",
            "		return 4",
            "init",
            "	let x = new C<int>",
            "	if x.foo() == 4",
            "		testSuccess()"
        );
    }

    @Test
    public void simpleFunctionCall2() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "class C<T:>",
            "	function foo() returns int",
            "		return bar()",
            "	function bar() returns int",
            "		return 4",
            "init",
            "	let x = new C<int>",
            "	if x.foo() == 4",
            "		testSuccess()"
        );
    }

    @Test
    public void genericFunctionOverload() { // #628
        testAssertOkLines(false,
            "package test",
            "native testSuccess()",
            "class LinkedList<T:>",
            "	T x",
            "public function LinkedList<string>.foo(string separator) returns string",
            "	this.foo<string>(s -> s, separator) // Doesn't work",
            "	this.foo2<string>(s -> s, separator) // Works",
            "	return separator",
            "interface ToStringClosure<A:>",
            "	function apply(A a) returns A",
            "public function LinkedList<T>.foo<T:>(ToStringClosure<T> cls, string separator)",
            "public function LinkedList<T>.foo2<T:>(ToStringClosure<T> cls, string separator)",
            "init",
            "	let x = new LinkedList<string>",
            "	x.foo(\"a\")"
        );
    }

    @Test
    public void extensionFunc2() { // #718
        testAssertOkLines(false,
            "package test",
            "native testSuccess()",
            "public function T.foo<T:>() returns T",
            "	return this",
            "init",
            "	let x = \"hello\".foo()",
            "	if x == \"hello\"",
            "		testSuccess()"
        );
    }

    @Test
    public void strangeFoldl() { // #655
        testAssertOkLines(false,
            "package test",
            "native testSuccess()",
            "class LinkedList<T:>",
            "	T x",
            "	function foldl<Q:, S:>(Q startValue, FoldClosure<S, Q> predicate) returns Q",
            "		return startValue",
            "interface FoldClosure<X:, Y:>",
            "	function run(X t, Y q) returns Y",
            "init",
            "	let x = new LinkedList<int>",
            "	x.foldl<int,int>(0, (x, y) -> x + y)"
        );
    }

    @Test
    @Ignore
    public void normalFoldlInfer() { // #657
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "@extern native I2S(int i) returns string",
            "string array s",
            "int s_max = -1",
            "class LinkedList<T:>",
            "	T x",
            "	function foldl<Q:>(Q startValue, FoldClosure<T, Q> predicate) returns Q",
            "		return predicate.run(x, startValue)",
            "	function toString() returns string",
            "		let fold = foldl(\"[\", (i, q) -> q + I2S(i castTo int) + \",\")",
            "		return fold + \"]\"",
            "interface FoldClosure<T:, Q:>",
            "	function run(T t, Q q) returns Q",
            "init",
            "	let x = new LinkedList<int>",
            "	x.x = 5",
            "	if x.toString() == \"[5,]\"",
            "		testSuccess()"
        );
    }

    @Test
    public void inheritField() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "abstract class A<T:>",
            "	int someInt",
            "class B extends A<int>",
            "	construct()",
            "		someInt = 1",
            "init",
            "	if new B().someInt == 1",
            "		testSuccess()"
        );
    }

    @Test
    public void inheritField2() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "abstract class A<T:>",
            "	int someInt",
            "class B<K:> extends A<K>",
            "	construct()",
            "		someInt = 1",
            "init",
            "	if new B<int>().someInt == 1",
            "		testSuccess()"
        );
    }


    @Test
    public void inheritMethod() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "abstract class A<T:>",
            "	function someInt()",
            "		testSuccess()",
            "class B extends A<int>",
            "	construct()",
            "		someInt()",
            "init",
            "	new B()"
        );
    }

    @Test
    public void nullWithGeneric() {
        testAssertErrorsLines(false, "Cannot compare types T with null",
            "package test",
            "native testSuccess()",
            "function foo<T:>(T t)",
            "	if t == null",
            "		testSuccess()",
            "init",
            "	foo(null)"
        );
    }

    @Test
    public void missingTypeArgsFunc() {
        testAssertErrorsLines(false, "Cannot return null, expected expression of type T",
            "package test",
            "function foo<T:>() returns T",
            "	return null",
            "init",
            "	let x = foo()"
        );
    }

    @Test
    public void missingTypeArgsMethod() {
        testAssertErrorsLines(false, "Cannot return null, expected expression of type T",
            "package test",
            "class C",
            "	function foo<T:>() returns T",
            "		return null",
            "init",
            "	let c = new C",
            "	let x = c.foo()"
        );
    }

    @Test
    public void missingTypeArgsConstructor() {
        testAssertErrorsLines(false, "Cannot infer type for type parameter T",
            "package test",
            "class C<T:>",
            "init",
            "	let c = new C"
        );
    }


    @Test
    public void tooManyTypeArgsFunc() {
        testAssertErrorsLines(false, "Cannot return null, expected expression of type T",
            "package test",
            "function foo<T:>() returns T",
            "	return null",
            "init",
            "	let x = foo<int, int>()"
        );
    }

    @Test
    public void tooManyTypeArgsMethod() {
        testAssertErrorsLines(false, "Cannot return null, expected expression of type T",
            "package test",
            "class C",
            "	function foo<T:>() returns T",
            "		return null",
            "init",
            "	let c = new C",
            "	let x = c.foo<int, int>()"
        );
    }

    @Test
    public void tooManyTypeArgsConstructor() {
        testAssertErrorsLines(false, "Too many type arguments given",
            "package test",
            "class C<T:>",
            "init",
            "	let c = new C<int, int>"
        );
    }

    @Test
    public void capturedType() { // #490
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "native println(string s)",
            "interface F<A:,R:>",
            "	function apply(A a) returns R",
            "function twice<X:>(F<X, X> f) returns F<X, X>",
            "	return x -> f.apply(f.apply(x))", // line 7
            "init",
            "	F<int, int> plus1 = x -> x + 1", // line 9
            "	F<int, int> plus2 = twice(plus1)",
//                "	F<string, string> shout = twice((string s) -> s + \"!\")", // line 11
            "	if  plus2.apply(1) == 3",
            "		testSuccess()"
        );
    }

    @Test
    public void severalSubMethods() { // #490
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "native println(string s)",
            "class B<T:>",
            "	function id(T x) returns T",
            "		return x",
            "class A<X:, Y:> extends B<Y>",
            "	override function id(Y y) returns Y",
            "		return y",
            "init",
            "	B<int> a = new A<int,int>",
            "	B<int> b = new A<string, int>",
            "	if a.id(4) == 4 and b.id(2) == 2",
            "		testSuccess()"
        );
    }

    @Test
    public void simpleCastTest() {
        testAssertOkLines(true,
            "package test",
            "	native testSuccess()",
            "	class A",
            "	class B extends A",
            "		int x",
            "	function get() returns A",
            "		let r = new B",
            "		r.x = 5",
            "		return r",
            "	init",
            "		if (get() castTo B).x == 5",
            "			testSuccess()",
            "endpackage"
        );
    }

    @Test
    public void abstractReturnT() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "abstract class F<T:>",
            "	abstract function get() returns T",
            "class X<T:> extends F<T>",
            "	T t",
            "	construct(T t)",
            "		this.t = t",
            "	override function get() returns T",
            "		return t",
            "init",
            "	F<int> x = new X(42)",
            "	if x.get() == 42",
            "		testSuccess()"
        );
    }


    @Test
    public void typeClassMustBeInterface1() {
        testAssertErrorsLines(false, "Could not find type ToIndex",
            "package test",
            "native testSuccess()",
            "function foo<T: ToIndex>(T x)",
            "    testSuccess()"
        );
    }

    @Test
    public void typeClassMustBeInterface2() {
        testAssertErrorsLines(false, "Invalid type constraint ToIndex. Type constraint must be an interface type.",
            "package test",
            "native testSuccess()",
            "tuple ToIndex(int x)",
            "function foo<T: ToIndex>(T x)",
            "    testSuccess()"
        );
    }

    @Test
    public void callMustSatisfyConstraint() {
        testAssertErrorsLines(false, "Type integer does not satisfy constraint T: ToIndex",
            "package test",
            "native testSuccess()",
            "interface ToIndex<T>",
            "    function toIndex(T x) returns int",
            "function foo<T: ToIndex>(T x)",
            "    testSuccess()",
            "init",
            "    foo(42)"
        );
    }

    @Test
    public void simpleTypeClass() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "native testFail(string s)",
            "interface ToIndex<T:>",
            "	function toIndex(T x) returns int", // 5
            "class A",
            "implements ToIndex<A>",
            "    function toIndex(A x) returns int",
            "        return 42",
            "function foo<Q: ToIndex>(Q x)", // 10
            "    if Q.toIndex(x) == 42",
            "        testSuccess()",
            "init",
            "    let a = new A",
            "    foo(a)"
        );
    }


    @Test
    public void twoTypeClasses() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "native testFail(string s)",
            "interface Plus<T:>",
            "	function plus(T x, T y) returns T",
            "interface Times<T:>",
            "	function times(T x, T y) returns T",
            "implements Plus<int>",
            "    function plus(int x, int y) returns int",
            "        return x + y",
            "implements Times<int>",
            "    function times(int x, int y) returns int",
            "        return x * y",
            "function calc<Q: Plus and Times>(Q x) returns Q",
            "    return Q.plus(x, Q.times(x, x))",
            "init",
            "    if calc(6) == 42",
            "        testSuccess()"
        );
    }

    @Test
    public void forwardTypeClass() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "interface ToIndex<T>",
            "	function toIndex(T elem) returns int",
            "class A",
            "implements ToIndex<A>",
            "    function toIndex(A x) returns int",
            "        return 42",
            "function foo<T: ToIndex>(T x)",
            "    if T.toIndex(x) == 42",
            "        testSuccess()",
            "function bar<T: ToIndex>(T x)",
            "    foo(x)",
            "init",
            "    let a = new A",
            "    bar(a)"
        );
    }

    @Test
    public void subTypeClass() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "native testFail(string s)",
            "interface A<T:>",
            "	function x(T x) returns int",
            "interface B<T:> extends A<T>",
            "	function y(T x) returns int",
            "class C",
            "implements B<C>",
            "    function x(C x) returns int",
            "        return 42",
            "    function y(C x) returns int",
            "        return 43",
            "function foo<Q: A>(Q x)",
            "    if Q.x(x) == 42",
            "        testSuccess()",
            "init",
            "    let c = new C",
            "    foo(c)"
        );
    }

    @Test
    public void typeClassAmbiguous() {
        testAssertErrorsLines(true, "There are multiple instances",
            "package test",
            "native testSuccess()",
            "native testFail(string s)",
            "interface A<T:>",
            "	function x(T x) returns int",
            "interface B<T:> extends A<T>",
            "	function y(T x) returns int",
            "class C",
            "implements A<C>",
            "    function x(C x) returns int",
            "        return 17",
            "implements B<C>",
            "    function x(C x) returns int",
            "        return 42",
            "    function y(C x) returns int",
            "        return 43",
            "function foo<Q: A>(Q x)",
            "    if Q.x(x) == 42",
            "        testSuccess()",
            "init",
            "    let c = new C",
            "    foo(c)"
        );
    }


    @Test
    public void buildInstance() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "native testFail(string s)",
            "interface A<X:>",
            "	function x(X x) returns int",
            "interface B<Y:>",
            "	function y(Y y) returns int",
            "class C",
            "implements A<C>",
            "    function x(C c) returns int",
            "        return 41",
            "implements B<T> for T: A",
            "    function y(T t) returns int",
            "        return T.x(t) + 1",
            "function foo<Q: B>(Q q)",
            "    if Q.y(q) == 42",
            "        testSuccess()",
            "init",
            "    let c = new C",
            "    foo(c)"
        );
    }

    @Test
    public void classParameterConstraint() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "native testFail(string s)",
            "@extern native S2I(string s) returns int",
            "interface ToInt<X:>",
            "	function toInt(X x) returns int",
            "class C<T: ToInt>",
            "    int elem",
            "    function set(T e)",
            "        this.elem = T.toInt(e)",
            "    function get() returns int",
            "        return elem",
            "implements ToInt<string>",
            "    function toInt(string s) returns int",
            "        return S2I(s)",
            "init",
            "    let c = new C<string>",
            "    c.set(\"42\")",
            "    if c.get() == 42",
            "        testSuccess()"
        );
    }

    @Test
    public void classParameterConstraintBuild() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "native testFail(string s)",
            "@extern native S2I(string s) returns int",
            "interface ToInt<X:>",
            "	function toInt(X x) returns int",
            "class C<T: ToInt>",
            "    int elem",
            "    function set(T e)",
            "        this.elem = T.toInt(e)",
            "    function get() returns int",
            "        return elem",
            "class Cell<T:>",
            "    T elem",
            "implements ToInt<string>",
            "    function toInt(string s) returns int",
            "        return S2I(s)",
            "implements ToInt<Cell<T>> for T: ToInt",
            "    function toInt(Cell<T> c) returns int",
            "        return T.toInt(c.elem)",
            "init",
            "    let cell = new Cell<string>",
            "    cell.elem = \"42\"",
            "    let c = new C<Cell<string>>",
            "    c.set(cell)",
            "    if c.get() == 42",
            "        testSuccess()"
        );
    }

    @Test
    public void subtypeConstraint1() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "native testFail(string s)",
            "@extern native S2I(string s) returns int",
            "interface ToInt<X:>",
            "	function toInt(X x) returns int",
            "class C<T: ToInt>",
            "    int elem",
            "    function set(T e)",
            "        this.elem = T.toInt(e)",
            "    function get() returns int",
            "        return elem",
            "class D extends C<string>",
            "implements ToInt<string>",
            "    function toInt(string s) returns int",
            "        return S2I(s)",
            "init",
            "    let d = new D",
            "    d.set(\"42\")",
            "    if d.get() == 42",
            "        testSuccess()"
        );
    }

    @Test
    public void subtypeConstraintDependent() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "native testFail(string s)",
            "@extern native S2I(string s) returns int",
            "interface ToInt<X:>",
            "	function toInt(X x) returns int",
            "class C<T: ToInt>",
            "    int elem",
            "    function set(T e)",
            "        this.elem = T.toInt(e)",
            "    function get() returns int",
            "        return elem",
            "class D<S: ToInt> extends C<S>",
            "class Cell<T:>",
            "    T elem",
            "implements ToInt<string>",
            "    function toInt(string s) returns int",
            "        return S2I(s)",
            "implements ToInt<Cell<T>> for T: ToInt",
            "    function toInt(Cell<T> c) returns int",
            "        return T.toInt(c.elem)",
            "init",
            "    let d = new D<Cell<string>>",
            "    let c = new Cell<string>",
            "    c.elem = \"42\"",
            "    d.set(c)",
            "    if d.get() == 42",
            "        testSuccess()"
        );
    }

    @Test
    public void iteratorExample() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "native testFail(string s)",
            "@extern native S2I(string s) returns int",
            "interface Iterable<E:, I: Iterator<E>, T:>",
//            "interface Iterable<E:, I: , T:>",
            "    function iterator(T t) returns I",
            "interface Iterator<T:, I:>",
            "    function hasNext(I i) returns bool",
            "    function next(I i) returns T",
            "interface Group<T:>",
            "    function zero() returns T",
            "    function plus(T a, T b) returns T",
            "function sum<T: Group, C: Iterable<T, I>, I: Iterator<T>>(C coll) returns T",
            "    var res = T.zero()",
            "    let iter = C.iterator(coll)",
            "    while I.hasNext(iter)",
            "        res = T.plus(res, I.next(iter))",
            "    return res",
            "implements Group<int>",
            "    function zero() returns int",
            "        return 0",
            "    function plus(int a, int b) returns int",
            "        return a + b",
            "class List<T>",
            "    T elem",
            "    List<T> next",
            "    construct(T elem, List<T> next)",
            "        this.elem = elem",
            "        this.next = next",
            "class ListIterator<T>",
            "    List<T> current",
            "    construct(List<T> list)",
            "        this.current = list",
            "implements Iterator<T, ListIterator<T>> for T",
            "    function hasNext(ListIterator<T> iter) returns bool",
            "        return iter.current != null",
            "    function next(ListIterator<T> iter) returns T",
            "        let res = iter.current.elem",
            "        iter.current = iter.current.next",
            "        return res",
            "implements Iterable<E, ListIterator<E>, List<E>> for E",
            "    function iterator(List<E> list) returns ListIterator<E>",
            "         return new ListIterator<E>(list)",
            "init",
            "    let list = new List<int>(100, new List<int>(20, new List<int>(3, null)))",
//            "    if sum(list) == 123",
            "    if sum<int, List<int>, ListIterator<int>>(list) == 123",
            "        testSuccess()"
        );
    }

    @Test
    public void missingFunction() {
        testAssertErrorsLines(false, "must implement the following functions",
            "package test",
            "interface Blub<T:>",
            "	function foo(T x) returns int",
            "	function bar(T x) returns int",
            "class A",
            "implements Blub<A>",
            "    function foo(A x) returns int",
            "        return 42"
        );
    }


    @Test
    public void cyclicImplements() {
        testAssertErrorsLines(false, "Type A does not satisfy constraint Y: Apfel<A>",
            "package test",
            "interface Apfel<T:>",
            "	function a(T x) returns int",
            "interface Birne<T:>",
            "	function b(T x) returns int",
            "class A",
            "implements Apfel<X> for X: Birne",
            "    function a(X t) returns int",
            "        return X.b(t)",
            "implements Birne<Y> for Y: Apfel",
            "    function b(Y t) returns int",
            "        return Y.a(t)",
            "function foo<T: Apfel>(T t)",
            "init",
            "    let a = new A()",
            "    foo(a)"
        );
    }

    @Test
    public void anyRefClass() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "native testFail(string s)",
            "interface AnyRef<T:>",
            "	function toIndex(T x) returns int",
            "	function fromIndex(int index) returns T",
            "class A",
            "class Cell<T: AnyRef>",
            "    int x",
            "    function set(T x)",
            "        this.x = T.toIndex(x)",
            "    function get() returns T",
            "        return T.fromIndex(this.x)",
            "init",
            "    let a = new A",
            "    let c = new Cell<A>",
            "    c.set(a)",
            "    if c.get() == a",
            "        testSuccess()"
        );
    }

    @Test
    public void toIndexFromIndexClass() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "native testFail(string s)",
            "interface ToIndex<T:>",
            "	function toIndex(T x) returns int",
            "interface FromIndex<T:>",
            "	function fromIndex(int index) returns T",
            "class A",
            "class Cell<T: ToIndex and FromIndex>",
            "    int x",
            "    function set(T x)",
            "        this.x = T.toIndex(x)",
            "    function get() returns T",
            "        return T.fromIndex(this.x)",
            "init",
            "    let a = new A",
            "    let c = new Cell<A>",
            "    c.set(a)",
            "    if c.get() == a",
            "        testSuccess()"
        );
    }

    @Test
    public void defaultForClass() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "native testFail(string s)",
            "interface Default<T:>",
            "	function defaultValue() returns T",
            "class A",
            "function d<X: Default>() returns X",
            "    return X.defaultValue()",
            "implements Default<int>",
            "    function defaultValue() returns int",
            "        return 42",
            "init",
            "    let a = d<A>()",
            "    let i = d<int>()",
            "    if a == null and i == 42",
            "        testSuccess()"
        );
    }

    @Test
    public void overloadMiss() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "native testFail(string s)",
            "@extern native I2S(int i) returns string",
            "interface Show<T:>",
            "	function toString(T t) returns string",
            "function T.toString<T: Show>() returns string",
            "    return T.toString(this)",
            "function int.toString() returns string",
            "    return I2S(this)",
            "init",
            "    if 42 .toString() == \"42\"",
            "        testSuccess()"
        );
    }

    @Test
    public void overloadMiss2() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "native testFail(string s)",
            "@extern native I2S(int i) returns string",
            "interface Show<T:>",
            "	function toString(T t) returns string",
            "function T.toString<T: Show>() returns string",
            "    return T.toString(this)",
            "function int.toString() returns string",
            "    return I2S(this)",
            "implements Show<int>",
            "    function toString(int i) returns string",
            "        return i.toString()",
            "init",
            "    if 42 .toString() == \"42\"",
            "        testSuccess()"
        );
    }


    @Test
    public void typeClassFindSupertypeInstance() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "native testFail(string s)",
            "interface ConvertIndex<T:> extends ToIndex<T>, FromIndex<T>",
            "interface ToIndex<T:>",
            "	function toIndex(T x) returns int",
            "interface FromIndex<T:>",
            "	function fromIndex(int i) returns T",
            "class A",
            "implements ConvertIndex<string>",
            "    function toIndex(string x) returns int",
            "        return 42",
            "    function fromIndex(int i) returns string",
            "        return \"42\"",
            "function foo<Q: ToIndex>(Q x) returns int",
            "    return Q.toIndex(x)",
            "init",
            "    let a = new A",
            "    if foo(\"x\") == 42 and foo(a) != 42",
            "        testSuccess()"
            );
    }

    @Test
    public void typeClassExtendedInstance() {
        testAssertOkLines(true,
            "package test",
            "native testSuccess()",
            "native testFail(string s)",
            "interface ConvertIndex<T:> extends ToIndex<T>, FromIndex<T>",
            // TODO should work without these 2 lines:
            "	function toIndex(T x) returns int",
            "	function fromIndex(int i) returns T",
            "interface ToIndex<T:>",
            "	function toIndex(T x) returns int",
            "interface FromIndex<T:>",
            "	function fromIndex(int i) returns T",
            "class A",
            "implements ConvertIndex<string>",
            "    function toIndex(string x) returns int",
            "        return 42",
            "    function fromIndex(int i) returns string",
            "        return \"42\"",
            "function foo<Q: ConvertIndex>(Q x) returns int",
            "    return Q.toIndex(x)",
            "init",
            "    let a = new A",
            "    if foo(\"x\") == 42 and foo(a) != 42",
            "        testSuccess()"
        );
    }

    @Test
    public void instanceViaPublicImport() {
        testAssertOkLines(true,
            "package a",
            "public interface ToIndex<T:>",
            "	function toIndex(T x) returns int",
            "public implements ToIndex<int>",
            "    function toIndex(int x) returns int",
            "        return x",
            "endpackage",
            "package b",
            "import public a",
            "endpackage",
            "package test",
            "import b",
            "native testSuccess()",
            "function foo<Q: ToIndex>(Q x)",
            "    if Q.toIndex(x) == 42",
            "        testSuccess()",
            "init",
            "    foo(42)"
        );
    }
}
