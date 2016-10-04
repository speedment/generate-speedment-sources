package com.speedment.sources.pattern;

import com.speedment.runtime.field.predicate.PredicateType;

/**
 *
 * @author Emil Forslund
 */
public final class EqualPredicatePattern extends AbstractSimpleComparatorPattern {

    public EqualPredicatePattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "EqualPredicate";
    }

    @Override
    protected String getOperator() {
        return "==";
    }

    @Override
    protected PredicateType getPredicateType() {
        return PredicateType.EQUAL;
    }
}