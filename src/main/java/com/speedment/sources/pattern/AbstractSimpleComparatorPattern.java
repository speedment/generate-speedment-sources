package com.speedment.sources.pattern;

import com.speedment.common.codegen.internal.model.constant.DefaultAnnotationUsage;
import com.speedment.common.codegen.internal.model.constant.DefaultJavadocTag;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Constructor;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.common.codegen.model.Type;
import com.speedment.common.tuple.Tuple1;
import com.speedment.runtime.field.predicate.PredicateType;
import com.speedment.runtime.field.trait.HasReferenceValue;
import com.speedment.runtime.internal.field.predicate.AbstractFieldPredicate;
import com.speedment.runtime.internal.field.predicate.reference.ReferenceEqualPredicate;

/**
 *
 * @author Emil Forslund
 */
abstract class AbstractSimpleComparatorPattern extends AbstractCousinPattern {
    
    protected AbstractSimpleComparatorPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }
    
    protected abstract String getOperator();
    
    protected abstract PredicateType getPredicateType();
    
    @Override
    protected final Class<?> getCousinClass() {
        return ReferenceEqualPredicate.class;
    }
    
    @Override
    protected final String getPackageName() {
        return primitive() + "s";
    }
    
    @Override
    public final String getFullClassName() {
        final String siblingPackage = getCousinClass().getPackage().getName();
        final String parentPackage = siblingPackage.substring(0, siblingPackage.lastIndexOf("."));
        return parentPackage + "." + getPackageName() + "." + getClassName();
    }
    
    @Override
    public final ClassOrInterface<?> make(File file) {
        file.add(Import.of(Type.of(PredicateType.class)));
        
        final String enumConstant = PredicateType.class.getSimpleName() + "." + getPredicateType().name();
        final Type hasValueType = siblingOf(HasReferenceValue.class, "Has%1$sValue")
            .add(Generic.of(Type.of("ENTITY")))
            .add(Generic.of(Type.of("D")))
        ;
        
        return com.speedment.common.codegen.model.Class.of(getClassName())
            
            // Documentation
            .set(Javadoc.of()
                .add(DefaultJavadocTag.PARAM.setValue("<ENTITY>").setText("entity type"))
                .add(DefaultJavadocTag.PARAM.setValue("<D>").setText("database type"))
                .add(DefaultJavadocTag.AUTHOR.setValue("Emil Forslund"))
                .add(DefaultJavadocTag.SINCE.setValue("3.0.0"))
            )
            
            // Class declaration
            .public_().final_()
            .add(Generic.of("ENTITY"))
            .add(Generic.of("D"))
            
            // Supertype
            .setSupertype(Type.of(AbstractFieldPredicate.class)
                .add(Generic.of(Type.of("ENTITY")))
                .add(Generic.of(hasValueType))
            )
            
            // Implemented interfaces
            .add(Type.of(Tuple1.class)
                .add(Generic.of(wrapperType()))
            )
            
            // Private member fields
            .add(Field.of("value", primitiveType()).private_().final_())
            
            // Constructor
            .add(Constructor.of().public_()
                .add(Field.of("field", hasValueType))
                .add(Field.of("value", primitiveType()))
                .add("super(" + enumConstant + ", field, entity -> field.getAs" + ucPrimitive() + "(entity) " + getOperator() + " value);",
                    "this.value = value;"
                )
            )
            
            // Methods
            .add(Method.of("get0", wrapperType()).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return value;")
            )
        ;
    }
}