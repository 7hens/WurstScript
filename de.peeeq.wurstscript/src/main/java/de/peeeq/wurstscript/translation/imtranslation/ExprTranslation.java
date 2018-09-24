package de.peeeq.wurstscript.translation.imtranslation;

import com.google.common.collect.Lists;
import de.peeeq.wurstscript.WurstOperator;
import de.peeeq.wurstscript.ast.*;
import de.peeeq.wurstscript.ast.Element;
import de.peeeq.wurstscript.attributes.CompileError;
import de.peeeq.wurstscript.attributes.names.NameLink;
import de.peeeq.wurstscript.jassIm.ImClass;
import de.peeeq.wurstscript.jassIm.*;
import de.peeeq.wurstscript.jassIm.ImExprs;
import de.peeeq.wurstscript.jassIm.ImFunction;
import de.peeeq.wurstscript.jassIm.ImMethod;
import de.peeeq.wurstscript.jassIm.ImStmts;
import de.peeeq.wurstscript.jassIm.ImTupleExpr;
import de.peeeq.wurstscript.jassIm.ImVar;
import de.peeeq.wurstscript.translation.imtranslation.purity.Pure;
import de.peeeq.wurstscript.translation.imtranslation.purity.ReadsGlobals;
import de.peeeq.wurstscript.types.*;
import de.peeeq.wurstscript.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static de.peeeq.wurstscript.jassIm.JassIm.*;

public class ExprTranslation {

    public static ImExpr translate(ExprBinary e, ImTranslator t, ImFunction f) {
        return wrapTranslation(e, t, translateIntern(e, t, f));
    }

    public static ImExpr translate(ExprUnary e, ImTranslator t, ImFunction f) {
        return wrapTranslation(e, t, translateIntern(e, t, f));
    }

    public static ImExpr translate(ExprBoolVal e, ImTranslator t, ImFunction f) {
        return wrapTranslation(e, t, translateIntern(e, t, f));
    }

    public static ImExpr translate(ExprFuncRef e, ImTranslator t, ImFunction f) {
        return wrapTranslation(e, t, translateIntern(e, t, f));
    }

    public static ImExpr translate(ExprIntVal e, ImTranslator t, ImFunction f) {
        return wrapTranslation(e, t, translateIntern(e, t, f));
    }

    public static ImExpr translate(ExprNull e, ImTranslator t, ImFunction f) {
        return wrapTranslation(e, t, translateIntern(e, t, f));
    }

    public static ImExpr translate(ExprRealVal e, ImTranslator t, ImFunction f) {
        return wrapTranslation(e, t, translateIntern(e, t, f));
    }

    public static ImExpr translate(ExprStringVal e, ImTranslator t, ImFunction f) {
        return wrapTranslation(e, t, translateIntern(e, t, f));
    }

    public static ImExpr translate(ExprThis e, ImTranslator t, ImFunction f) {
        return wrapTranslation(e, t, translateIntern(e, t, f));
    }

    public static ImExpr translate(ExprSuper e, ImTranslator t, ImFunction f) {
        return wrapTranslation(e, t, translateIntern(e, t, f));
    }

    public static ImExpr translate(NameRef e, ImTranslator t, ImFunction f) {
        return wrapTranslation(e, t, translateIntern(e, t, f));
    }

    public static ImExpr translate(ExprCast e, ImTranslator t, ImFunction f) {
        return wrapTranslation(e, t, translateIntern(e, t, f));
    }

    public static ImExpr translate(FunctionCall e, ImTranslator t, ImFunction f) {
        return wrapTranslation(e, t, translateIntern(e, t, f));
    }

    public static ImExpr translate(ExprIncomplete e, ImTranslator t, ImFunction f) {
        return wrapTranslation(e, t, translateIntern(e, t, f));
    }

    public static ImExpr translate(ExprNewObject e, ImTranslator t, ImFunction f) {
        return wrapTranslation(e, t, translateIntern(e, t, f));
    }

    public static ImExpr translate(ExprInstanceOf e, ImTranslator t, ImFunction f) {
        return wrapTranslation(e, t, translateIntern(e, t, f));
    }

    private static ImExpr wrapTranslation(Expr e, ImTranslator t, ImExpr translated) {
        WurstType actualType = e.attrTypRaw();
        WurstType expectedTypRaw = e.attrExpectedTypRaw();
        return wrapTranslation(e, t, translated, actualType, expectedTypRaw);
    }

    static ImExpr wrapTranslation(Element trace, ImTranslator t, ImExpr translated, WurstType actualType, WurstType expectedTypRaw) {
        ImFunction toIndex = null;
        ImFunction fromIndex = null;
        if (actualType instanceof WurstTypeBoundTypeParam) {
            WurstTypeBoundTypeParam wtb = (WurstTypeBoundTypeParam) actualType;
            FuncDef fromIndexFunc = wtb.getFromIndex();
            if (fromIndexFunc != null) {
                fromIndex = t.getFuncFor(fromIndexFunc);
            }
        }
        if (expectedTypRaw instanceof WurstTypeBoundTypeParam) {
            WurstTypeBoundTypeParam wtb = (WurstTypeBoundTypeParam) expectedTypRaw;
            FuncDef toIndexFunc = wtb.getToIndex();
            if (toIndexFunc != null) {
                toIndex = t.getFuncFor(toIndexFunc);
            }
        }

//        System.out.println("CAll " + Utils.prettyPrintWithLine(trace));
//        System.out.println("  actualType = " + actualType.getFullName());
//        System.out.println("  expectedTypRaw = " + expectedTypRaw.getFullName());

        if (toIndex != null && fromIndex != null) {
//            System.out.println("  --> cancel");
            // the two conversions cancel each other out
            return translated;
        } else if (fromIndex != null) {
//            System.out.println("  --> fromIndex");
            return JassIm.ImFunctionCall(trace, fromIndex, JassIm.ImExprs(translated), false, CallType.NORMAL);
        } else if (toIndex != null) {
//            System.out.println("  --> toIndex");
            return JassIm.ImFunctionCall(trace, toIndex, JassIm.ImExprs(translated), false, CallType.NORMAL);
        }
        return translated;
    }

    public static ImExpr translateIntern(ExprBinary e, ImTranslator t, ImFunction f) {
        ImExpr left = e.getLeft().imTranslateExpr(t, f);
        ImExpr right = e.getRight().imTranslateExpr(t, f);
        WurstOperator op = e.getOp();
        if (e.attrFuncLink() != null) {
            // overloaded operator
            ImFunction calledFunc = t.getFuncFor(e.attrFuncDef());
            return JassIm.ImFunctionCall(e, calledFunc, ImExprs(left, right), false, CallType.NORMAL);
        }
        if (op == WurstOperator.DIV_REAL) {
            if (Utils.isJassCode(e)) {
                if (e.getLeft().attrTyp().isSubtypeOf(WurstTypeInt.instance(), e)
                        && e.getRight().attrTyp().isSubtypeOf(WurstTypeInt.instance(), e)) {
                    // in jass when we have int1 / int2 this actually means int1
                    // div int2
                    op = WurstOperator.DIV_INT;
                }
            } else {
                if (e.getLeft().attrTyp().isSubtypeOf(WurstTypeInt.instance(), e)
                        && e.getRight().attrTyp().isSubtypeOf(WurstTypeInt.instance(), e)) {
                    // we want a real division but have 2 ints so we need to
                    // multiply with 1.0
                    // TODO is this really needed or handled in IM->Jass
                    // translation?
                    left = ImOperatorCall(WurstOperator.MULT, ImExprs(left, ImRealVal("1.")));
                }
            }
        }
        return ImOperatorCall(op, ImExprs(left, right));
    }

    public static ImExpr translateIntern(ExprUnary e, ImTranslator t, ImFunction f) {
        return ImOperatorCall(e.getOpU(), ImExprs(e.getRight().imTranslateExpr(t, f)));
    }

    public static ImExpr translateIntern(ExprBoolVal e, ImTranslator t, ImFunction f) {
        return JassIm.ImBoolVal(e.getValB());
    }

    public static ImExpr translateIntern(ExprFuncRef e, ImTranslator t, ImFunction f) {
        ImFunction func = t.getFuncFor(e.attrFuncDef());
        return ImFuncRef(func);
    }

    public static ImExpr translateIntern(ExprIntVal e, ImTranslator t, ImFunction f) {
        if (e.attrExpectedTyp() instanceof WurstTypeReal) {
            // translate differently when real is expected
            return ImRealVal(e.getValI() + ".");
        }

        return ImIntVal(e.getValI());
    }

    public static ImExpr translateIntern(ExprNull e, ImTranslator t, ImFunction f) {
        WurstType expectedTypeRaw = e.attrExpectedTypRaw();
        if (expectedTypeRaw.isTranslatedToInt()) {
            return ImIntVal(0);
        }
        return ImNull();
    }

    public static ImExpr translateIntern(ExprRealVal e, ImTranslator t, ImFunction f) {
        return ImRealVal(e.getValR());
    }

    public static ImExpr translateIntern(ExprStringVal e, ImTranslator t, ImFunction f) {
        return ImStringVal(e.getValS());
    }

    public static ImExpr translateIntern(ExprThis e, ImTranslator t, ImFunction f) {
        ImVar var = t.getThisVar(f, e);
        return ImVarAccess(var);
    }

    public static ImExpr translateIntern(ExprSuper e, ImTranslator t, ImFunction f) {
        ImVar var = t.getThisVar(f, e);
        return ImVarAccess(var);
    }

    public static ImExpr translateIntern(NameRef e, ImTranslator t, ImFunction f) {
        return translateNameDef(e, t, f);
    }

    private static ImExpr translateNameDef(NameRef e, ImTranslator t, ImFunction f) throws CompileError {
        NameDef decl = e.attrNameDef();
        if (decl == null) {
            // should only happen with gg_ variables
            if (!t.isEclipseMode()) {
                e.addError("Translation Error: Could not find definition of " + e.getVarName() + ".");
            }
            return ImNull();
        }
        if (decl instanceof VarDef) {
            VarDef varDef = (VarDef) decl;

            ImVar v = t.getVarFor(varDef);

            if (e.attrImplicitParameter() instanceof Expr) {
                // we have implicit parameter
                // e.g. "someObject.someField"
                Expr implicitParam = (Expr) e.attrImplicitParameter();

                if (implicitParam.attrTyp() instanceof WurstTypeTuple) {
                    WurstTypeTuple tupleType = (WurstTypeTuple) implicitParam.attrTyp();
                    if (e instanceof ExprMemberVar) {
                        ExprMemberVar e2 = (ExprMemberVar) e;
                        return translateTupleSelection(t, f, e2);
                    } else {
                        throw new CompileError(e.getSource(), "Cannot create tuple access");
                    }
                }

                if (e instanceof AstElementWithIndexes) {
                    ImExpr index1 = implicitParam.imTranslateExpr(t, f);
                    ImExpr index2 = ((AstElementWithIndexes) e).getIndexes().get(0).imTranslateExpr(t, f);
                    return JassIm.ImVarArrayAccess(v, JassIm.ImExprs(index1, index2));

                } else {
                    ImExpr index = implicitParam.imTranslateExpr(t, f);
                    return ImVarArrayAccess(v, JassIm.ImExprs(index));
                }
            } else {
                // direct var access
                if (e instanceof AstElementWithIndexes) {
                    // direct access array var
                    AstElementWithIndexes withIndexes = (AstElementWithIndexes) e;
                    if (withIndexes.getIndexes().size() > 1) {
                        throw new CompileError(e.getSource(), "More than one index is not supported.");
                    }
                    ImExpr index = withIndexes.getIndexes().get(0).imTranslateExpr(t, f);
                    return ImVarArrayAccess(v, JassIm.ImExprs(index));
                } else {
                    // not an array var
                    return ImVarAccess(v);

                }
            }
        } else if (decl instanceof EnumMember) {
            EnumMember enumMember = (EnumMember) decl;
            int id = t.getEnumMemberId(enumMember);
            return ImIntVal(id);
        } else {
            throw new CompileError(e.getSource(), "Cannot translate reference to " + Utils.printElement(decl));
        }
    }

    private static ImExpr translateTupleSelection(ImTranslator t, ImFunction f, ExprMemberVar mv) {
        List<WParameter> indexes = new ArrayList<>();

        Expr expr = mv;
        while (true) {
            if (expr instanceof ExprMemberVar) {
                ExprMemberVar mv2 = (ExprMemberVar) expr;
                Expr left = mv2.getLeft();
                if (left.attrTyp() instanceof WurstTypeTuple) {
                    indexes.add(0, (WParameter) mv2.attrNameDef());
                    expr = left;
                    continue;
                }
            }
            break;
        }

        WurstTypeTuple tt = (WurstTypeTuple) expr.attrTyp();
        int tupleIndex = 0;
        WurstType resultTupleType = null;
        for (int i = 0; i < indexes.size(); i++) {
            WParameter param = indexes.get(i);
            TupleDef tdef = tt.getTupleDef();
            int pos = 0;
            while (tdef.getParameters().get(pos) != param) {
                tupleIndex += tupleSize(tdef.getParameters().get(pos).getTyp().attrTyp());
                pos++;
            }
            resultTupleType = tdef.getParameters().get(pos).getTyp().attrTyp();
            if (i < indexes.size() - 1) {
                tt = (WurstTypeTuple) tdef.getParameters().get(pos).getTyp().attrTyp();
            }
        }
        ImExpr exprTr = expr.imTranslateExpr(t, f);
        if (resultTupleType instanceof WurstTypeTuple) {
            // if the result is a tuple, create it:
            int tupleSize = tupleSize(resultTupleType);

            if (exprTr.attrPurity() instanceof Pure || exprTr.attrPurity() instanceof ReadsGlobals) {
                ImExprs exprs = JassIm.ImExprs();
                for (int i = 0; i < tupleSize; i++) {
                    exprs.add(ImTupleSelection((ImExpr) exprTr.copy(), tupleIndex + i));
                }
                return ImTupleExpr(exprs);
            } else {
                ImVar temp = JassIm.ImVar(expr, exprTr.attrTyp(), "temp", false);
                // for impure expressions use a temporary:
                f.getLocals().add(temp);

                ImExprs exprs = JassIm.ImExprs();
                for (int i = 0; i < tupleSize; i++) {
                    // TODO use temporary var
                    exprs.add(ImTupleSelection(JassIm.ImVarAccess(temp), tupleIndex + i));
                }
                return JassIm.ImStatementExpr(JassIm.ImStmts(ImSet(expr, ImVarAccess(temp), exprTr)), ImTupleExpr(exprs));
            }
        } else {
            return ImTupleSelection(exprTr, tupleIndex);
        }

    }

    /**
     * counts the components of a tuple (including nested)
     */
    private static int tupleSize(WurstType t) {
        if (t instanceof WurstTypeTuple) {
            WurstTypeTuple tt = (WurstTypeTuple) t;
            int sum = 0;
            for (WParameter p : tt.getTupleDef().getParameters()) {
                sum += tupleSize(p.getTyp().attrTyp());
            }
            return sum;
        }
        // all other types have size 1
        return 1;
    }

    public static ImExpr translateIntern(ExprCast e, ImTranslator t, ImFunction f) {
        return e.getExpr().imTranslateExpr(t, f);
    }

    public static ImExpr translateIntern(FunctionCall e, ImTranslator t, ImFunction f) {
        if (e instanceof ExprMemberMethodDotDot) {
            return translateFunctionCall(e, t, f, true);
        } else {
            return translateFunctionCall(e, t, f, false);
        }
    }

    private static ImExpr translateFunctionCall(FunctionCall e, ImTranslator t, ImFunction f, boolean returnReveiver) {

        if (e.getFuncName().equals("getStackTraceString") && e.attrImplicitParameter() instanceof NoExpr
                && e.getArgs().size() == 0) {
            // special built-in error function
            return JassIm.ImGetStackTrace();
        }

        if (e.getFuncName().equals("ExecuteFunc")) {
            ExprStringVal s = (ExprStringVal) e.getArgs().get(0);
            String exFunc = s.getValS();
            NameLink func = Utils.getFirst(e.lookupFuncs(exFunc));
            ImFunction executedFunc = t.getFuncFor((TranslatedToImFunction) func.getDef());
            return JassIm.ImFunctionCall(e, executedFunc, JassIm.ImExprs(), true, CallType.EXECUTE);
        }

        if (e.getFuncName().equals("compiletime")
                && e.attrImplicitParameter() instanceof NoExpr
                && e.getArgs().size() == 1) {
            // special compiletime-expression
            return JassIm.ImCompiletimeExpr(e, e.getArgs().get(0).imTranslateExpr(t, f), t.getCompiletimeExpressionsOrder(e));
        }

        List<Expr> arguments = Lists.newArrayList(e.getArgs());
        Expr leftExpr = null;
        boolean dynamicDispatch = false;

        FunctionDefinition calledFunc = e.attrFuncDef();

        if (e.attrImplicitParameter() instanceof Expr) {
            if (isCalledOnDynamicRef(e) && calledFunc instanceof FuncDef) {
                dynamicDispatch = true;
            }
            // add implicit parameter to front
            // TODO why would I add the implicit parameter here, if it is
            // not a dynamic dispatch?
            leftExpr = (Expr) e.attrImplicitParameter();
        }

        // get real func def (override of module function)
        boolean useRealFuncDef = true;
        if (e instanceof ExprMemberMethod) {
            ExprMemberMethod exprMemberMethod = (ExprMemberMethod) e;
            WurstType left = exprMemberMethod.getLeft().attrTyp();
            if (left instanceof WurstTypeModuleInstanciation) {
                // if we have a call like A.foo() and A is a module,
                // use this function
                useRealFuncDef = false;
            }
        }

        if (calledFunc == null) {
            // this must be an ignored function
            return ImNull();
        }

        if (useRealFuncDef) {
            calledFunc = calledFunc.attrRealFuncDef();
        }

        if (calledFunc == e.attrNearestFuncDef()) {
            // recursive self calls are bound statically
            // this is different to other objectoriented languages but it is
            // necessary
            // because jass does not allow mutually recursive calls
            // The only situation where this would make a difference is with
            // super-calls
            // (or other statically bound calls)
            dynamicDispatch = false;
        }

        ImExpr receiver = leftExpr == null ? null : leftExpr.imTranslateExpr(t, f);
        ImExprs imArgs = translateExprs(arguments, t, f);

        if (calledFunc instanceof TupleDef) {
            // creating a new tuple...
            ImExprs tupleArgs = JassIm.ImExprs();
            flattenTupleArgs(tupleArgs, imArgs);
            return ImTupleExpr(tupleArgs);
        }

        ImStmts stmts = null;
        ImVar tempVar = null;
        if (returnReveiver) {
            if (leftExpr == null)
                throw new Error("impossible");
            tempVar = JassIm.ImVar(leftExpr, leftExpr.attrTyp().imTranslateType(), "receiver", false);
            f.getLocals().add(tempVar);
            stmts = JassIm.ImStmts(ImSet(e, ImVarAccess(tempVar), receiver));
            receiver = JassIm.ImVarAccess(tempVar);
        }

        ImExpr call;
        if (dynamicDispatch) {
            ImMethod method = t.getMethodFor((FuncDef) calledFunc);
            call = JassIm.ImMethodCall(e, method, receiver, imArgs, false);
        } else {
            ImFunction calledImFunc = t.getFuncFor(calledFunc);
            if (receiver != null) {
                imArgs.add(0, receiver);
            }
            call = ImFunctionCall(e, calledImFunc, imArgs, false, CallType.NORMAL);
        }

        if (returnReveiver) {
            if (stmts == null)
                throw new Error("impossible");
            stmts.add(call);
            return JassIm.ImStatementExpr(stmts, JassIm.ImVarAccess(tempVar));
        } else {
            return call;
        }
    }

    private static void flattenTupleArgs(ImExprs tupleArgs, ImExprs imArgs) {
        for (ImExpr e : imArgs.removeAll()) {
            if (e instanceof ImTupleExpr) {
                ImTupleExpr te = (ImTupleExpr) e;
                flattenTupleArgs(tupleArgs, te.getExprs());
            } else {
                tupleArgs.add(e);
            }
        }

    }

    private static boolean isCalledOnDynamicRef(FunctionCall e) {
        if (e instanceof ExprMemberMethod) {
            ExprMemberMethod mm = (ExprMemberMethod) e;
            return mm.getLeft().attrTyp().allowsDynamicDispatch();
        } else if (e.attrIsDynamicContext()) {
            return true;
        }
        return false;
    }

    private static ImExprs translateExprs(List<Expr> arguments, ImTranslator t, ImFunction f) {
        ImExprs result = ImExprs();
        for (Expr e : arguments) {
            result.add(e.imTranslateExpr(t, f));
        }
        return result;
    }

    public static ImExpr translateIntern(ExprIncomplete e, ImTranslator t, ImFunction f) {
        throw new CompileError(e.getSource(), "Incomplete expression.");
    }

    public static ImExpr translateIntern(ExprNewObject e, ImTranslator t, ImFunction f) {
        ConstructorDef constructorFunc = e.attrConstructorDef();
        ImFunction constructorImFunc = t.getConstructNewFunc(constructorFunc);
        return ImFunctionCall(e, constructorImFunc, translateExprs(e.getArgs(), t, f), false, CallType.NORMAL);
    }

    public static ImExprOpt translate(NoExpr e, ImTranslator translator, ImFunction f) {
        return JassIm.ImNoExpr();
    }

    public static ImExpr translateIntern(ExprInstanceOf e, ImTranslator translator, ImFunction f) {
        WurstType targetType = e.getTyp().attrTyp();
        if (targetType instanceof WurstTypeNamedScope) {
            WurstTypeNamedScope t = (WurstTypeNamedScope) targetType;
            ImClass clazz = translator.getClassFor((StructureDef) t.getDef());
            return JassIm.ImInstanceof(e.getExpr().imTranslateExpr(translator, f), clazz);
        }
        throw new Error("Cannot compile instanceof " + targetType);
    }

    public static ImExpr translate(ExprTypeId e, ImTranslator translator, ImFunction f) {
        WurstType leftType = e.getLeft().attrTyp();
        if (leftType instanceof WurstTypeClassOrInterface) {
            WurstTypeClassOrInterface wtc = (WurstTypeClassOrInterface) leftType;

            ImClass c = translator.getClassFor(wtc.getDef());
            if (wtc.isStaticRef()) {
                return JassIm.ImTypeIdOfClass(c);
            } else {
                return JassIm.ImTypeIdOfObj(e.getLeft().imTranslateExpr(translator, f), c);
            }
        } else {
            throw new Error("not implemented for " + leftType);
        }
    }

    public static ImExpr translate(ExprClosure e, ImTranslator tr, ImFunction f) {
        return new ClosureTranslator(e, tr, f).translate();
    }

    public static ImExpr translate(ExprStatementsBlock e, ImTranslator translator, ImFunction f) {

        ImStmts statements = JassIm.ImStmts();
        for (WStatement s : e.getBody()) {
            if (s instanceof StmtReturn) {
                continue;
            }
            ImStmt translated = s.imTranslateStmt(translator, f);
            statements.add(translated);
        }

        ImExprOpt expr = null;
        StmtReturn r = e.getReturnStmt();
        if (r != null && r.getReturnedObj() instanceof Expr) {
            expr = ((Expr) r.getReturnedObj()).imTranslateExpr(translator, f);
        }
        if (expr instanceof ImExpr) {
            return JassIm.ImStatementExpr(statements, (ImExpr) expr);
        } else {
            return JassIm.ImStatementExpr(statements, JassIm.ImNull());
        }
    }

    public static ImExpr translate(ExprDestroy s, ImTranslator t, ImFunction f) {
        WurstType typ = s.getDestroyedObj().attrTyp();
        if (typ instanceof WurstTypeClass) {
            WurstTypeClass classType = (WurstTypeClass) typ;
            return destroyClass(s, t, f, classType.getClassDef());
        } else if (typ instanceof WurstTypeInterface) {
            WurstTypeInterface wti = (WurstTypeInterface) typ;
            return destroyClass(s, t, f, wti.getDef());
        } else if (typ instanceof WurstTypeModuleInstanciation) {
            WurstTypeModuleInstanciation minsType = (WurstTypeModuleInstanciation) typ;
            ClassDef classDef = minsType.getDef().attrNearestClassDef();
            return destroyClass(s, t, f, classDef);
        }
        // TODO destroy interfaces?
        throw new CompileError(s.getSource(), "cannot destroy object of type " + typ);
    }

    public static ImExpr destroyClass(ExprDestroy s, ImTranslator t, ImFunction f, StructureDef classDef) {
        ImMethod destroyFunc = t.destroyMethod.getFor(classDef);
        return JassIm.ImMethodCall(s, destroyFunc, s.getDestroyedObj().imTranslateExpr(t, f), ImExprs(), false);
    }

    public static ImExpr translate(ExprEmpty s, ImTranslator translator, ImFunction f) {
        throw new CompileError(s.getSource(), "cannot translate empty expression");
    }

    public static ImExpr translate(ExprIfElse e, ImTranslator t, ImFunction f) {
        ImExpr ifTrue = e.getIfTrue().imTranslateExpr(t, f);
        ImExpr ifFalse = e.getIfFalse().imTranslateExpr(t, f);
        // TODO common super type of both
        ImVar res = JassIm.ImVar(e, ifTrue.attrTyp(), "cond_result", false);
        f.getLocals().add(res);
        return JassIm.ImStatementExpr(
                ImStmts(
                        ImIf(e, e.getCond().imTranslateExpr(t, f),
                                ImStmts(
                                        ImSet(e.getIfTrue(), ImVarAccess(res), ifTrue)
                                ),
                                ImStmts(
                                        ImSet(e.getIfFalse(), ImVarAccess(res), ifFalse)
                                ))
                ),
                JassIm.ImVarAccess(res)
        );
    }

    public static ImLExpr translateLvalue(ExprVarAccess e, ImTranslator translator, ImFunction f) {
        // TODO fill out from above
        throw new RuntimeException("TODO");
    }

    public static ImLExpr translateLvalue(ExprVarArrayAccess e, ImTranslator translator, ImFunction f) {
        throw new RuntimeException("TODO");
    }

    public static ImLExpr translateLvalue(ExprMemberVar e, ImTranslator translator, ImFunction f) {
        throw new RuntimeException("TODO");
    }

    public static ImLExpr translateLvalue(ExprMemberArrayVar e, ImTranslator translator, ImFunction f) {
        throw new RuntimeException("TODO");
    }

}
