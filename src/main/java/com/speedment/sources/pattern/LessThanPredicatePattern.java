package com.speedment.sources.pattern;

import com.speedment.runtime.field.predicate.PredicateType;

/**
 *
 * @author Emil Forslund
 */
public final class LessThanPredicatePattern extends AbstractSimpleComparatorPattern {

    public LessThanPredicatePattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "LessThanPredicate";
    }

    @Override
    protected String getOperator() {
        return "<";
    }

    @Override
    protected PredicateType getPredicateType() {
        return PredicateType.LESS_THAN;
    }
}