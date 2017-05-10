package com.speedment.sources.pattern;

/**
 *
 * @author Emil Forslund
 */
public abstract class AbstractSiblingPattern extends AbstractPattern {
    
    protected AbstractSiblingPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }
    
    protected abstract Class<?> getSiblingClass();

    @Override
    public final String getFullClassName() {
        return getSiblingClass().getPackage().getName() + "." + getClassName();
    }
}