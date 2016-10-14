package com.speedment.sources.pattern;

import com.speedment.runtime.field.predicate.PredicateType;

/**
 *
 * @author Emil Forslund
 */
public final class GreaterOrEqualPredicatePattern extends AbstractSimpleComparatorPattern {

    public GreaterOrEqualPredicatePattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "GreaterOrEqualPredicate";
    }

    @Override
    protected String getOperator() {
        return ">=";
    }

    @Override
    protected PredicateType getPredicateType() {
        return PredicateType.GREATER_OR_EQUAL;
    }
}