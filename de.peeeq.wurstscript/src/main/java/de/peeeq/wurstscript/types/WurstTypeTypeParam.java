package de.peeeq.wurstscript.types;

import de.peeeq.wurstscript.ast.Element;
import de.peeeq.wurstscript.ast.TypeExpr;
import de.peeeq.wurstscript.ast.TypeExprList;
import de.peeeq.wurstscript.ast.TypeParamDef;
import de.peeeq.wurstscript.attributes.names.FuncLink;
import de.peeeq.wurstscript.jassIm.ImExprOpt;
import de.peeeq.wurstscript.jassIm.ImType;
import de.peeeq.wurstscript.jassIm.JassIm;
import de.peeeq.wurstscript.translation.imtranslation.ImTranslator;
import io.vavr.control.Option;
import org.eclipse.jdt.annotation.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WurstTypeTypeParam extends WurstType {

    private TypeParamDef def;

    public WurstTypeTypeParam(TypeParamDef t) {
        this.def = t;
    }

    @Override
    VariableBinding matchAgainstSupertypeIntern(WurstType other, @Nullable Element location, VariableBinding mapping, VariablePosition variablePosition) {
        if (variablePosition == VariablePosition.LEFT) {
            Option<WurstTypeBoundTypeParam> binding = mapping.get(def);
            if (binding.isDefined()) {
                // already bound, use bound type
                return binding.get().matchAgainstSupertypeIntern(other, location, mapping, variablePosition);
            } else if (mapping.isVar(def)) {
                // not bound -> add mapping
                return mapping.set(def, new WurstTypeBoundTypeParam(def, other, location));
            }
        }
        if (other instanceof WurstTypeTypeParam) {
            WurstTypeTypeParam other2 = (WurstTypeTypeParam) other;
            if (other2.def == this.def) {
                // same type parameter, no change and match
                return mapping;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return def.getName();
    }

    @Override
    public String getFullName() {
        return getName() + " (type parameter line " + def.getSource().getLine() + ")";
    }

    public TypeParamDef getDef() {
        return def;
    }

    @Override
    public VariableBinding getTypeArgBinding() {
        return VariableBinding.emptyMapping();
    }

    @Override
    public WurstType setTypeArgs(VariableBinding typeParamBounds) {
        if (typeParamBounds.contains(def)) {
            return typeParamBounds.get(def).get();
        }
        return this;
    }

    @Override
    public ImType imTranslateType(ImTranslator tr) {
        if (hasTypeConstraints()) {
            return JassIm.ImTypeVarRef(tr.getTypeVar(def));
        }
        return JassIm.ImAnyType();
    }

    /** Using the new template generics with type constraints*/
    private boolean hasTypeConstraints() {
        return def.getTypeParamConstraints() instanceof TypeExprList;
    }

    @Override
    public ImExprOpt getDefaultValue(ImTranslator tr) {
        return JassIm.ImNull(this.imTranslateType(tr));
    }


    @Override
    public boolean isCastableToInt() {
        return !hasTypeConstraints();
    }

    @Override
    protected boolean isNullable() {
        return !hasTypeConstraints();
    }

    @Override
    public void addMemberMethods(Element node, String name, List<FuncLink> result) {
        getMemberMethods(node)
            .filter(fl -> fl.getName().equals(name))
            .collect(Collectors.toCollection(() -> result));
    }

    @Override
    public Stream<FuncLink> getMemberMethods(Element node) {
        return getTypeConstraints()
            .flatMap(i ->
                i.getMemberMethods(node))
            .map(fl -> fl.withReceiverType(this));
    }

    private Stream<WurstTypeInterface> getTypeConstraints() {
        if (def.getTypeParamConstraints() instanceof TypeExprList) {
            TypeExprList constraints = (TypeExprList) def.getTypeParamConstraints();
            return constraints.stream()
                .map(TypeExpr::attrTyp)
                .filter(t -> t instanceof WurstTypeInterface)
                .map(t -> (WurstTypeInterface) t);
        } else {
            return Stream.empty();
        }
    }
}
