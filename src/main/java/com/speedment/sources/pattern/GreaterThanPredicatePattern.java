package com.speedment.sources.pattern;

import com.speedment.runtime.field.predicate.PredicateType;

/**
 *
 * @author Emil Forslund
 */
public final class GreaterThanPredicatePattern extends AbstractSimpleComparatorPattern {

    public GreaterThanPredicatePattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "GreaterThanPredicate";
    }

    @Override
    protected String getOperator() {
        return ">=";
    }

    @Override
    protected PredicateType getPredicateType() {
        return PredicateType.GREATER_THAN;
    }
}