package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.DefaultJavadocTag;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.AnnotationUsage;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Interface;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.runtime.field.method.ReferenceSetter;
import com.speedment.runtime.field.method.Setter;

/**
 *
 * @author Emil Forslund
 */
public final class SetterPattern extends AbstractSiblingPattern {
    
    public SetterPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }
    
    @Override
    public String getClassName() {
        return ucPrimitive() + "Setter";
    }

    @Override
    protected Class<?> getSiblingClass() {
        return ReferenceSetter.class;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        return Interface.of(getClassName())
            
            /******************************************************************/
            /*                         Documentation                          */
            /******************************************************************/
            .set(Javadoc.of(
                formatJavadoc(
                    "A short-cut functional reference to the {@code setXXX(value)} method for a " +
                    "particular field in an entity. The referenced method should return a " +
                    "reference to itself." +
                    "\n<p>\n" +
                    "A {@code %1$sSetter<ENTITY>} has the following signature:\n" +
                    "{@code\n" +
                    "    interface ENTITY {\n" +
                    "        ENTITY setXXX(%2$s value);\n" +
                    "    }\n" +
                    "}"
                ))
                .add(DefaultJavadocTag.PARAM.setValue("<ENTITY>").setText("the entity"))
                .add(DefaultJavadocTag.AUTHOR.setValue("Emil Forslund"))
                .add(DefaultJavadocTag.SINCE.setValue("3.0.0"))
            )
            
            /******************************************************************/
            /*                       Class Declaration                        */
            /******************************************************************/
            .add(apiAnnotation())
            .add(AnnotationUsage.of(FunctionalInterface.class))
            .public_()
            .add(Generic.of("ENTITY"))
            .add(SimpleParameterizedType.create(
                Setter.class, 
                SimpleType.create("ENTITY")
            ))
            
            /******************************************************************/
            /*                            Methods                             */
            /******************************************************************/
            .add(Method.of("setAs" + ucPrimitive(), SimpleType.create("ENTITY"))
                .set(Javadoc.of(
                        "Sets the member represented by this setter in the specified " +
                        "instance to the specified value, returning a reference to " +
                        "the same instance as result."
                    )
                    .add(DefaultJavadocTag.PARAM.setValue("instance").setText("the instance to set it in"))
                    .add(DefaultJavadocTag.PARAM.setValue("value").setText("the new value"))
                    .add(DefaultJavadocTag.RETURN.setValue("a reference to that instance"))
                )
                .add(Field.of("instance", SimpleType.create("ENTITY")))
                .add(Field.of("value", primitiveType()))
            );
    }
    
}