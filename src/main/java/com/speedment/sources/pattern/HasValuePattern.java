package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.DefaultAnnotationUsage;
import com.speedment.common.codegen.constant.DefaultJavadocTag;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Interface;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.runtime.config.mapper.TypeMapper;
import com.speedment.runtime.field.method.ReferenceGetter;
import com.speedment.runtime.field.method.ReferenceSetter;
import com.speedment.runtime.field.method.SetToReference;
import com.speedment.runtime.field.trait.HasReferenceValue;
import com.speedment.runtime.internal.field.setter.SetToReferenceImpl;

/**
 *
 * @author Emil Forslund
 */
public final class HasValuePattern extends AbstractSiblingPattern {

    public HasValuePattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getClassName() {
        return "Has" + ucPrimitive() + "Value";
    }

    @Override
    protected Class<?> getSiblingClass() {
        return HasReferenceValue.class;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        file.add(Import.of(siblingOf(SetToReferenceImpl.class, "SetTo" + ucPrimitive()+ "Impl")));
        
        return Interface.of(getClassName())
            
            /******************************************************************/
            /*                         Documentation                          */
            /******************************************************************/
            .set(Javadoc.of(formatJavadoc(
                "A representation of an Entity field that is a primitive {@code %2$s} type."
                ))
                .add(DefaultJavadocTag.PARAM.setValue("<ENTITY>").setText("entity type"))
                .add(DefaultJavadocTag.PARAM.setValue("<D>").setText("database type"))
                .add(DefaultJavadocTag.AUTHOR.setValue("Emil Forslund"))
                .add(DefaultJavadocTag.SINCE.setValue("3.0.0"))
            )
            
            /******************************************************************/
            /*                       Class Declaration                        */
            /******************************************************************/
            .add(apiAnnotation())
            .add(generatedAnnotation())
            .public_()
            .add(Generic.of("ENTITY"))
            .add(Generic.of("D"))
            .add(SimpleParameterizedType.create(
                com.speedment.runtime.field.Field.class,
                SimpleType.create("ENTITY")
            ))
            
            /******************************************************************/
            /*                            Methods                             */
            /******************************************************************/
            .add(Method.of("setter", SimpleParameterizedType.create(
                    siblingOf(ReferenceSetter.class, "%1$sSetter"), 
                    SimpleType.create("ENTITY")
                ))
                .add(DefaultAnnotationUsage.OVERRIDE)
            )
            
            .add(Method.of("getter", SimpleParameterizedType.create(
                    siblingOf(ReferenceGetter.class, "%1$sGetter"),
                    SimpleType.create("ENTITY")
                ))
                .add(DefaultAnnotationUsage.OVERRIDE)
            )
            
            .add(Method.of("typeMapper", SimpleParameterizedType.create(
                    TypeMapper.class,
                    SimpleType.create("D"),
                    wrapperType()
                ))
                .add(DefaultAnnotationUsage.OVERRIDE)
            )
            
            .add(Method.of("getAs" + ucPrimitive(), primitiveType())
                .default_()
                .set(Javadoc.of(formatJavadoc(
                        "Gets the value from the Entity field."
                    ))
                    .add(DefaultJavadocTag.PARAM.setValue("entity").setText("the entity"))
                    .add(DefaultJavadocTag.RETURN.setValue("the value of the field"))
                )
                .add(Field.of("entity", SimpleType.create("ENTITY")))
                .add("return getter().getAs" + ucPrimitive() + "(entity);")
            )
            
            .add(Method.of("set", SimpleType.create("ENTITY"))
                .default_()
                .set(Javadoc.of(formatJavadoc(
                        "Sets the value in the given Entity."
                    ))
                    .add(DefaultJavadocTag.PARAM.setValue("entity").setText("the entity"))
                    .add(DefaultJavadocTag.PARAM.setValue("value").setText("to set"))
                    .add(DefaultJavadocTag.RETURN.setValue("the entity itself"))
                )
                .add(Field.of("entity", SimpleType.create("ENTITY")))
                .add(Field.of("value", primitiveType()))
                .add("return setter().setAs" + ucPrimitive() + "(entity, value);")
            )
            
            .add(Method.of("setTo", SimpleParameterizedType.create(
                    siblingOf(SetToReference.class, "SetTo%1$s"),
                    SimpleType.create("ENTITY"),
                    SimpleType.create("D")
                ))
                .default_()
                .set(Javadoc.of(formatJavadoc(
                        "Creates and returns a setter handler with a given value."
                    ))
                    .add(DefaultJavadocTag.PARAM.setValue("value").setText("to set"))
                    .add(DefaultJavadocTag.RETURN.setValue("a set-operation with a given value"))
                )
                .add(Field.of("value", primitiveType()))
                .add("return new SetTo" + ucPrimitive()+ "Impl<>(this, value);")
            )
        ;
    }
}