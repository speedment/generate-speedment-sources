package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.DefaultAnnotationUsage;
import com.speedment.common.codegen.constant.DefaultJavadocTag;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.*;
import com.speedment.runtime.config.identifier.ColumnIdentifier;
import com.speedment.runtime.field.ReferenceField;
import com.speedment.runtime.field.internal.ReferenceFieldImpl;
import com.speedment.runtime.field.internal.method.GetReferenceImpl;
import com.speedment.runtime.field.internal.predicate.reference.ReferenceEqualPredicate;
import com.speedment.runtime.field.method.ReferenceGetter;
import com.speedment.runtime.field.method.ReferenceSetter;
import com.speedment.runtime.field.predicate.FieldPredicate;
import com.speedment.runtime.typemapper.TypeMapper;

import java.lang.Class;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Predicate;

/**
 *
 * @author Emil Forslund
 */
public final class BooleanFieldImplPattern extends AbstractSiblingPattern {

    public BooleanFieldImplPattern(Class<?> wrapper, Class<?> primitive) {
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
        file.add(Import.of(Objects.class).static_().setStaticMember("requireNonNull"));
        file.add(Import.of(siblingOf(GetReferenceImpl.class, "Get%1$sImpl")));

        final Type entityType = SimpleType.create("ENTITY");
        final Type dType      = SimpleType.create("D");

        final Type fieldType = SimpleParameterizedType.create(
            siblingOf(ReferenceField.class, "%1$sField"),
            entityType,
            dType
        );
        
        final Type identifierType = SimpleParameterizedType.create(ColumnIdentifier.class,
            entityType
        );
        
        final Type getterType = SimpleParameterizedType.create(
            siblingOf(ReferenceGetter.class, "%1$sGetter"),
            entityType
        );
        
        final Type getType = SimpleParameterizedType.create(
            siblingOf(ReferenceGetter.class, "Get%1$s"),
            entityType,
            dType
        );
        
        final Type setterType = SimpleParameterizedType.create(
            siblingOf(ReferenceSetter.class, "%1$sSetter"),
            entityType
        );
        
        final Type typeMapperType = SimpleParameterizedType.create(
            TypeMapper.class,
            dType,
            wrapperType()
        );
       
        return com.speedment.common.codegen.model.Class.of(getClassName())
            
            ////////////////////////////////////////////////////////////////////
            /*                         Documentation                          */
            ////////////////////////////////////////////////////////////////////
            .set(Javadoc.of(formatJavadoc("Default implementation of the {@link " + ucPrimitive() + "Field}-interface."))
                .add(DefaultJavadocTag.PARAM.setValue("<ENTITY>").setText("entity type"))
                .add(DefaultJavadocTag.PARAM.setValue("<D>").setText("database type"))
                .add(DefaultJavadocTag.AUTHOR.setValue("Emil Forslund"))
                .add(DefaultJavadocTag.SINCE.setValue("3.0.0"))
            )
            
            ////////////////////////////////////////////////////////////////////
            /*                       Class Declaration                        */
            ////////////////////////////////////////////////////////////////////
            .public_().final_()
            .add(generatedAnnotation())
            .add(fieldType)
            .add(Generic.of(SimpleType.create("ENTITY")))
            .add(Generic.of(SimpleType.create("D")))
            
            ////////////////////////////////////////////////////////////////////
            /*                        Private Fields                          */
            ////////////////////////////////////////////////////////////////////
            .add(Field.of("identifier", identifierType).private_().final_())
            .add(Field.of("getter", getType).private_().final_())
            .add(Field.of("setter", setterType).private_().final_())
            .add(Field.of("typeMapper", typeMapperType).private_().final_())
            .add(Field.of("unique", boolean.class).private_().final_())
            
            ////////////////////////////////////////////////////////////////////
            /*                          Constructor                           */
            ////////////////////////////////////////////////////////////////////
            .add(Constructor.of().public_()
                .add(Field.of("identifier", identifierType))
                .add(Field.of("getter", getterType))
                .add(Field.of("setter", setterType))
                .add(Field.of("typeMapper", typeMapperType))
                .add(Field.of("unique", boolean.class))
                .add(
                    "this.identifier = requireNonNull(identifier);",
                    "this.getter     = new Get" + ucPrimitive() + "Impl<>(this, getter);",
                    "this.setter     = requireNonNull(setter);",
                    "this.typeMapper = requireNonNull(typeMapper);",
                    "this.unique     = unique;"
                )
            )
            
            ////////////////////////////////////////////////////////////////////
            /*                            Getters                             */
            ////////////////////////////////////////////////////////////////////
            .add(Method.of("identifier", identifierType).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return identifier;")
            )
            
            .add(Method.of("setter", setterType).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return setter;")
            )
            
            .add(Method.of("getter", getType).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return getter;")
            )
            
            .add(Method.of("typeMapper", typeMapperType).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return typeMapper;")
            )
            
            .add(Method.of("isUnique", boolean.class).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return unique;")
            )

            ////////////////////////////////////////////////////////////////////
            //                        Operators                               //
            ////////////////////////////////////////////////////////////////////
            .add(newUnaryOperator(file, false))
            .add(newUnaryOperator(file, true))
        ;
    }

    private Method newUnaryOperator(File file, boolean negated) {
        file.add(Import.of(cousinOf(ReferenceEqualPredicate.class, primitive() + "s", "%1$s" + (negated ? "Not" : "") + "EqualPredicate")));

        final Type predicateType = SimpleParameterizedType.create(
            negated ? Predicate.class : FieldPredicate.class,
            SimpleType.create("ENTITY")
        );

        return Method.of(negated ? "notEqual" : "equal", predicateType)
            .public_()
            .add(DefaultAnnotationUsage.OVERRIDE)
            .add(Field.of("value", primitiveType()))
            .add("return new " + ucPrimitive() + (negated ? "Not" : "") + "Equal" + "Predicate<>(this, value)" + ";");
    }
}