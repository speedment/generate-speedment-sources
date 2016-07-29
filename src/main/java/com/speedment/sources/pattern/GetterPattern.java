package com.speedment.sources.pattern;

import com.speedment.common.codegen.internal.model.constant.DefaultJavadocTag;
import com.speedment.common.codegen.model.AnnotationUsage;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Interface;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.common.codegen.model.Type;
import com.speedment.runtime.field.method.Getter;
import com.speedment.runtime.field.method.ReferenceGetter;

/**
 *
 * @author Emil Forslund
 */
public final class GetterPattern extends AbstractSiblingPattern {
    
    public GetterPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }
    
    @Override
    public String getClassName() {
        return ucPrimitive() + "Getter";
    }

    @Override
    protected Class<?> getSiblingClass() {
        return ReferenceGetter.class;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        return Interface.of(getClassName())
            
            /******************************************************************/
            /*                         Documentation                          */
            /******************************************************************/
            .set(Javadoc.of(
                formatJavadoc(
                    "A short-cut functional reference to the {@code getXXX(value)} method for a " +
                    "particular field in an entity." +
                    "\n<p>\n" +
                    "A {@code %1$sGetter<ENTITY>} has the following signature:\n" +
                    "{@code\n" +
                    "    interface ENTITY {\n" +
                    "        %2$s getXXX();\n" +
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
            .add(AnnotationUsage.of(Type.of(FunctionalInterface.class)))
            .public_()
            .add(Generic.of("ENTITY"))
            .add(Type.of(Getter.class).add(Generic.of("ENTITY")))
            
            /******************************************************************/
            /*                            Methods                             */
            /******************************************************************/
            .add(Method.of("getAs" + ucPrimitive(), primitiveType())
                .set(Javadoc.of(
                        "Returns the member represented by this getter in the specified " +
                        "instance."
                    )
                    .add(DefaultJavadocTag.PARAM.setValue("instance").setText("the instance to get from"))
                    .add(DefaultJavadocTag.RETURN.setValue("the value"))
                )
                .add(Field.of("instance", Type.of("ENTITY")))
            );
    }
}