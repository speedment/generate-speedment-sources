package com.speedment.sources.pattern;

import com.speedment.runtime.field.predicate.PredicateType;

/**
 *
 * @author Emil Forslund
 */
public final class NotEqualPredicatePattern extends AbstractSimpleComparatorPattern {

    public NotEqualPredicatePattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "NotEqualPredicate";
    }

    @Override
    protected String getOperator() {
        return "!=";
    }

    @Override
    protected PredicateType getPredicateType() {
        return PredicateType.NOT_EQUAL;
    }
}