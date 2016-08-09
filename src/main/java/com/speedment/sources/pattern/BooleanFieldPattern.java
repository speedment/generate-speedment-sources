package com.speedment.sources.pattern;

import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.File;
import com.speedment.runtime.field.ReferenceField;
import com.speedment.runtime.field.trait.HasComparableOperators;

/**
 *
 * @author Emil Forslund
 */
public final class BooleanFieldPattern extends AbstractSiblingPattern {

    private final FieldPattern delegator;
    
    public BooleanFieldPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
        delegator = new FieldPattern(wrapper, primitive);
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "Field";
    }

    @Override
    protected Class<?> getSiblingClass() {
        return ReferenceField.class;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        return delegator.make(file)
            
            // Remove the 'HasComparableOperators'-interface
            .call(intrf -> {
                intrf.getInterfaces().removeIf(t -> 
                    t.getTypeName().equals(HasComparableOperators.class.getName())
                );
            });
    }
}