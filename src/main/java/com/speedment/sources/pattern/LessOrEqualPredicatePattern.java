package com.speedment.sources.pattern;

import com.speedment.runtime.field.predicate.PredicateType;

/**
 *
 * @author Emil Forslund
 */
public final class LessOrEqualPredicatePattern extends AbstractSimpleComparatorPattern {

    public LessOrEqualPredicatePattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "LessOrEqualPredicate";
    }

    @Override
    protected String getOperator() {
        return "<=";
    }

    @Override
    protected PredicateType getPredicateType() {
        return PredicateType.LESS_OR_EQUAL;
    }
}