package com.speedment.sources.pattern;

/**
 *
 * @author Emil Forslund
 */
abstract class AbstractCousinPattern extends AbstractPattern {
    
    protected AbstractCousinPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }
    
    protected abstract Class<?> getCousinClass();
    protected abstract String getPackageName();
    
    @Override
    public String getFullClassName() {
        final String siblingPackage = getCousinClass().getPackage().getName();
        final String parentPackage = siblingPackage.substring(0, siblingPackage.lastIndexOf("."));
        return parentPackage + "." + getPackageName() + "." + getClassName();
    }
}