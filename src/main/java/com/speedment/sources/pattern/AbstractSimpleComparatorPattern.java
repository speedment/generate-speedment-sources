package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.DefaultAnnotationUsage;
import com.speedment.common.codegen.constant.DefaultJavadocTag;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.*;
import com.speedment.common.tuple.Tuple1;
import com.speedment.runtime.field.internal.predicate.AbstractFieldPredicate;
import com.speedment.runtime.field.internal.predicate.reference.ReferenceEqualPredicate;
import com.speedment.runtime.field.predicate.PredicateType;
import com.speedment.runtime.field.trait.HasReferenceValue;

import java.lang.Class;
import java.lang.reflect.Type;

import static com.speedment.common.codegen.util.Formatting.shortName;

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
        file.add(Import.of(PredicateType.class));
        
        final String enumConstant = PredicateType.class.getSimpleName() + "." + getPredicateType().name();
        final Type hasValueType = SimpleParameterizedType.create(
            siblingOf(HasReferenceValue.class, "Has%1$sValue"),
            SimpleType.create("ENTITY"),
            SimpleType.create("D")
        );

        final String reverseOperator;
        switch (getPredicateType()) {
            case EQUAL            : reverseOperator = "NotEqual";       break;
            case NOT_EQUAL        : reverseOperator = "Equal";          break;
            case LESS_THAN        : reverseOperator = "GreaterOrEqual"; break;
            case LESS_OR_EQUAL    : reverseOperator = "GreaterThan";    break;
            case GREATER_THAN     : reverseOperator = "LessOrEqual";    break;
            case GREATER_OR_EQUAL : reverseOperator = "LessThan";       break;
            default : throw new IllegalArgumentException(
                getPredicateType() +
                " is not one of the six supported predicate types."
            );
        }

        final Type reverseType = cousinOf(
            ReferenceEqualPredicate.class,
            getPackageName(),
            ucPrimitive() + reverseOperator + "Predicate"
        );

        return com.speedment.common.codegen.model.Class.of(getClassName())
            
            // Documentation
            .set(Javadoc.of(formatJavadoc(
                    "A predicate that evaluates if a value is {@code " + getOperator() + "} a specified {@code %2$s}."
                ))
                .add(DefaultJavadocTag.PARAM.setValue("<ENTITY>").setText("entity type"))
                .add(DefaultJavadocTag.PARAM.setValue("<D>").setText("database type"))
                .add(DefaultJavadocTag.AUTHOR.setValue("Emil Forslund"))
                .add(DefaultJavadocTag.SINCE.setValue("3.0.0"))
            )
            
            // Class declaration
            .public_().final_()
            .add(generatedAnnotation())
            .add(Generic.of("ENTITY"))
            .add(Generic.of("D"))
            
            // Supertype
            .setSupertype(SimpleParameterizedType.create(
                AbstractFieldPredicate.class,
                SimpleType.create("ENTITY"),
                hasValueType
            ))
            
            // Implemented interfaces
            .add(SimpleParameterizedType.create(
                Tuple1.class,
                wrapperType()
            ))
            
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
                .add("return value;"))
                
            .add(Method.of("negate", SimpleParameterizedType.create(
                reverseType,
                SimpleType.create("ENTITY"),
                SimpleType.create("D")
            )).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return new " + shortName(reverseType.getTypeName()) +
                    "<>(getField(), value);")
            )
        ;
    }
}