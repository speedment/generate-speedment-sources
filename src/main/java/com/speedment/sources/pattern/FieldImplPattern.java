package com.speedment.sources.pattern;

import com.speedment.common.codegen.internal.model.constant.DefaultAnnotationUsage;
import com.speedment.common.codegen.internal.model.constant.DefaultJavadocTag;
import com.speedment.common.codegen.internal.model.constant.DefaultType;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Constructor;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.common.codegen.model.Type;
import com.speedment.runtime.config.identifier.FieldIdentifier;
import com.speedment.runtime.config.mapper.TypeMapper;
import com.speedment.runtime.field.ReferenceField;
import com.speedment.runtime.field.method.ReferenceGetter;
import com.speedment.runtime.field.method.ReferenceSetter;
import com.speedment.runtime.field.predicate.FieldPredicate;
import com.speedment.runtime.field.predicate.Inclusion;
import com.speedment.runtime.internal.field.ReferenceFieldImpl;
import com.speedment.runtime.internal.field.comparator.ReferenceFieldComparator;
import com.speedment.runtime.internal.field.comparator.ReferenceFieldComparatorImpl;
import com.speedment.runtime.internal.field.predicate.reference.ReferenceEqualPredicate;
import java.util.Objects;
import java.util.function.Predicate;

/**
 *
 * @author Emil Forslund
 */
public final class FieldImplPattern extends AbstractSiblingPattern {

    public FieldImplPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    protected Class<?> getSiblingClass() {
        return ReferenceFieldImpl.class;
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "FieldImpl";
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        file.add(Import.of(Type.of(Objects.class)).static_().setStaticMember("requireNonNull"));
        
        final Type fieldType = siblingOf(ReferenceField.class, "%1$sField")
            .add(Generic.of(Type.of("ENTITY")))
            .add(Generic.of(Type.of("D")));
        
        final Type identifierType = Type.of(FieldIdentifier.class)
            .add(Generic.of(Type.of("ENTITY")));
        
        final Type getterType = siblingOf(ReferenceGetter.class, "%1$sGetter")
            .add(Generic.of(Type.of("ENTITY")));
        
        final Type setterType = siblingOf(ReferenceSetter.class, "%1$sSetter")
            .add(Generic.of(Type.of("ENTITY")));
        
        final Type typeMapperType = Type.of(TypeMapper.class)
            .add(Generic.of(Type.of("D")))
            .add(Generic.of(wrapperType()));
        
        final Type comparatorType = siblingOf(ReferenceFieldComparator.class, "%1$sFieldComparator")
            .add(Generic.of(Type.of("ENTITY")))
            .add(Generic.of(Type.of("D")));
       
        return com.speedment.common.codegen.model.Class.of(getClassName())
            
            /******************************************************************/
            /*                         Documentation                          */
            /******************************************************************/
            .set(Javadoc.of()
                .add(DefaultJavadocTag.PARAM.setValue("<ENTITY>").setText("entity type"))
                .add(DefaultJavadocTag.PARAM.setValue("<D>").setText("database type"))
                .add(DefaultJavadocTag.AUTHOR.setValue("Emil Forslund"))
                .add(DefaultJavadocTag.SINCE.setValue("3.0.0"))
            )
            
            /******************************************************************/
            /*                       Class Declaration                        */
            /******************************************************************/
            .public_().final_()
            .add(fieldType)
            .add(Generic.of(Type.of("ENTITY")))
            .add(Generic.of(Type.of("D")))
            
            /******************************************************************/
            /*                        Private Fields                          */
            /******************************************************************/
            .add(Field.of("identifier", identifierType).private_().final_())
            .add(Field.of("getter", getterType).private_().final_())
            .add(Field.of("setter", setterType).private_().final_())
            .add(Field.of("typeMapper", typeMapperType).private_().final_())
            .add(Field.of("unique", DefaultType.BOOLEAN_PRIMITIVE).private_().final_())
            
            /******************************************************************/
            /*                          Constructor                           */
            /******************************************************************/
            .add(Constructor.of().public_()
                .add(Field.of("identifier", identifierType))
                .add(Field.of("getter", getterType))
                .add(Field.of("setter", setterType))
                .add(Field.of("typeMapper", typeMapperType))
                .add(Field.of("unique", DefaultType.BOOLEAN_PRIMITIVE))
                .add(
                    "this.identifier = requireNonNull(identifier);",
                    "this.getter     = requireNonNull(getter);",
                    "this.setter     = requireNonNull(setter);",
                    "this.typeMapper = requireNonNull(typeMapper);",
                    "this.unique     = unique;"
                )
            )
            
            /******************************************************************/
            /*                            Getters                             */
            /******************************************************************/
            .add(Method.of("identifier", identifierType).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return identifier;")
            )
            
            .add(Method.of("setter", setterType).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return setter;")
            )
            
            .add(Method.of("getter", getterType).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return getter;")
            )
            
            .add(Method.of("typeMapper", typeMapperType).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return typeMapper;")
            )
            
            .add(Method.of("isUnique", DefaultType.BOOLEAN_PRIMITIVE).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return unique;")
            )
            
            /******************************************************************/
            /*                           Comparators                          */
            /******************************************************************/
            .call(c -> file.add(Import.of(siblingOf(ReferenceFieldComparatorImpl.class, "%1$sFieldComparatorImpl"))))
            
            .add(Method.of("comparator", comparatorType).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return new " + ucPrimitive() + "FieldComparatorImpl<>(this);")
            )
            
            .add(Method.of("comparatorNullFieldsFirst", comparatorType).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return comparator();")
            )
            
            .add(Method.of("comparatorNullFieldsLast", comparatorType).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return comparator();")
            )
            
            /******************************************************************/
            /*                           Operators                            */
            /******************************************************************/
            .add(newUnaryOperator(  file, "equal",          "Equal",          false))
            .add(newUnaryOperator(  file, "greaterThan",    "GreaterThan",    false))
            .add(newUnaryOperator(  file, "greaterOrEqual", "GreaterOrEqual", false))
            .add(newBetweenOperator(file, "between",                          false))
            .add(newInOperator(     file, "in",                               false))
            .add(newUnaryOperator(  file, "notEqual",       "Equal",           true))
            .add(newUnaryOperator(  file, "lessOrEqual",    "GreaterThan",     true))
            .add(newUnaryOperator(  file, "lessThan",       "GreaterOrEqual",  true))
            .add(newBetweenOperator(file, "notBetween",                        true))
            .add(newInOperator(     file, "notIn",                             true))
        ;
    }
    
    private Method newUnaryOperator(File file, String methodName, String predicateName, boolean negated) {
        file.add(Import.of(cousinOf(ReferenceEqualPredicate.class, primitive() + "s", "%1$s" + predicateName + "Predicate")));
        
        final Type predicateType = Type.of(negated ? Predicate.class : FieldPredicate.class)
            .add(Generic.of(Type.of("ENTITY")));
        
        return Method.of(methodName, predicateType)
            .public_()
            .add(DefaultAnnotationUsage.OVERRIDE)
            .add(Field.of("value", wrapperType()))
            .add("return new " + ucPrimitive() + predicateName + "Predicate<>(this, value)" + (negated ? ".negate()" : "") + ";");
    }
    
    private Method newInOperator(File file, String methodName, boolean negated) {
        file.add(Import.of(cousinOf(ReferenceEqualPredicate.class, primitive() + "s", "%1$sInPredicate")));
        
        final Type predicateType = Type.of(negated ? Predicate.class : FieldPredicate.class)
            .add(Generic.of(Type.of("ENTITY")));
        
        return Method.of(methodName, predicateType)
            .public_()
            .add(DefaultAnnotationUsage.OVERRIDE)
            .add(Field.of("set", DefaultType.set(wrapperType())))
            .add("return new " + ucPrimitive() + "InPredicate<>(this, set)" + (negated ? ".negate()" : "") + ";");
    }
    
    private Method newBetweenOperator(File file, String methodName, boolean negated) {
        file.add(Import.of(cousinOf(ReferenceEqualPredicate.class, primitive() + "s", "%1$sBetweenPredicate")));
        
        final Type predicateType = Type.of(negated ? Predicate.class : FieldPredicate.class)
            .add(Generic.of(Type.of("ENTITY")));
        
        return Method.of(methodName, predicateType)
            .public_()
            .add(DefaultAnnotationUsage.OVERRIDE)
            .add(Field.of("start", wrapperType()))
            .add(Field.of("end", wrapperType()))
            .add(Field.of("inclusion", Type.of(Inclusion.class)))
            .add("return new " + ucPrimitive() + "BetweenPredicate<>(this, start, end, inclusion)" + (negated ? ".negate()" : "") + ";");
    }
}