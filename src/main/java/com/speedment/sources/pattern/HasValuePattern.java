package com.speedment.sources.pattern;

import com.speedment.common.codegen.internal.model.constant.DefaultAnnotationUsage;
import com.speedment.common.codegen.internal.model.constant.DefaultJavadocTag;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Interface;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.common.codegen.model.Type;
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
            .public_()
            .add(Generic.of("ENTITY"))
            .add(Generic.of("D"))
            .add(Type.of(com.speedment.runtime.field.Field.class)
                .add(Generic.of(Type.of("ENTITY")))
            )
            
            /******************************************************************/
            /*                            Methods                             */
            /******************************************************************/
            .add(Method.of("setter", siblingOf(ReferenceSetter.class, "%1$sSetter").add(Generic.of("ENTITY")))
                .add(DefaultAnnotationUsage.OVERRIDE)
            )
            
            .add(Method.of("getter", siblingOf(ReferenceGetter.class, "%1$sGetter").add(Generic.of("ENTITY")))
                .add(DefaultAnnotationUsage.OVERRIDE)
            )
            
            .add(Method.of("typeMapper", Type.of(TypeMapper.class)
                    .add(Generic.of(Type.of("D")))
                    .add(Generic.of(wrapperType()))
                )
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
                .add(Field.of("entity", Type.of("ENTITY")))
                .add("return getter().getAs" + ucPrimitive() + "(entity);")
            )
            
            .add(Method.of("set", Type.of("ENTITY"))
                .default_()
                .set(Javadoc.of(formatJavadoc(
                        "Sets the value in the given Entity."
                    ))
                    .add(DefaultJavadocTag.PARAM.setValue("entity").setText("the entity"))
                    .add(DefaultJavadocTag.PARAM.setValue("value").setText("to set"))
                    .add(DefaultJavadocTag.RETURN.setValue("the entity itself"))
                )
                .add(Field.of("entity", Type.of("ENTITY")))
                .add(Field.of("value", primitiveType()))
                .add("return setter().setAs" + ucPrimitive() + "(entity, value);")
            )
            
            .add(Method.of("setTo", siblingOf(SetToReference.class, "SetTo%1$s")
                    .add(Generic.of(Type.of("ENTITY")))
                    .add(Generic.of(Type.of("D")))
                )
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