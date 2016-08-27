package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.DefaultAnnotationUsage;
import com.speedment.common.codegen.constant.DefaultJavadocTag;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Constructor;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.runtime.config.identifier.FieldIdentifier;
import com.speedment.runtime.config.mapper.TypeMapper;
import com.speedment.runtime.field.ReferenceField;
import com.speedment.runtime.field.method.ReferenceGetter;
import com.speedment.runtime.field.method.ReferenceSetter;
import com.speedment.runtime.internal.field.ReferenceFieldImpl;
import java.lang.reflect.Type;
import java.util.Objects;

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
        
        final Type fieldType = SimpleParameterizedType.create(
            siblingOf(ReferenceField.class, "%1$sField"),
            SimpleType.create("ENTITY"),
            SimpleType.create("D")
        );
        
        final Type identifierType = SimpleParameterizedType.create(
            FieldIdentifier.class,
            SimpleType.create("ENTITY")
        );
        
        final Type getterType = SimpleParameterizedType.create(
            siblingOf(ReferenceGetter.class, "%1$sGetter"),
            SimpleType.create("ENTITY")
        );
        
        final Type setterType = SimpleParameterizedType.create(
            siblingOf(ReferenceSetter.class, "%1$sSetter"),
            SimpleType.create("ENTITY")
        );
        
        final Type typeMapperType = SimpleParameterizedType.create(
            TypeMapper.class,
            SimpleType.create("D"),
            wrapperType()
        );
       
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
            .add(generatedAnnotation())
            .add(fieldType)
            .add(Generic.of(SimpleType.create("ENTITY")))
            .add(Generic.of(SimpleType.create("D")))
            
            /******************************************************************/
            /*                        Private Fields                          */
            /******************************************************************/
            .add(Field.of("identifier", identifierType).private_().final_())
            .add(Field.of("getter", getterType).private_().final_())
            .add(Field.of("setter", setterType).private_().final_())
            .add(Field.of("typeMapper", typeMapperType).private_().final_())
            .add(Field.of("unique", boolean.class).private_().final_())
            
            /******************************************************************/
            /*                          Constructor                           */
            /******************************************************************/
            .add(Constructor.of().public_()
                .add(Field.of("identifier", identifierType))
                .add(Field.of("getter", getterType))
                .add(Field.of("setter", setterType))
                .add(Field.of("typeMapper", typeMapperType))
                .add(Field.of("unique", boolean.class))
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
            
            .add(Method.of("isUnique", boolean.class).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return unique;")
            )
        ;
    }
}