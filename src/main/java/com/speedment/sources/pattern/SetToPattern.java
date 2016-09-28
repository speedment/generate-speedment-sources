package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.DefaultAnnotationUsage;
import com.speedment.common.codegen.constant.DefaultJavadocTag;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Interface;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.runtime.field.method.SetToReference;
import com.speedment.runtime.field.trait.HasReferenceValue;
import java.util.function.UnaryOperator;

/**
 *
 * @author Emil Forslund
 */
public final class SetToPattern extends AbstractSiblingPattern {

    public SetToPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getClassName() {
        return "SetTo" + ucPrimitive();
    }

    @Override
    protected Class<?> getSiblingClass() {
        return SetToReference.class;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        return Interface.of(getClassName())
            
            /******************************************************************/
            /*                         Documentation                          */
            /******************************************************************/
            .set(Javadoc.of(formatJavadoc(
                "Represents a set-operation with all the metadata contained."
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
                UnaryOperator.class, 
                SimpleType.create("ENTITY")
            ))
            
            /******************************************************************/
            /*                            Methods                             */
            /******************************************************************/
            .add(Method.of("getField", SimpleParameterizedType.create(
                    siblingOf(HasReferenceValue.class, "Has%1$sValue"),
                    SimpleType.create("ENTITY"),
                    SimpleType.create("D")
                ))
                .set(Javadoc.of(formatJavadoc(
                        "Returns the field that this setter sets."
                    )).add(DefaultJavadocTag.RETURN.setValue("the field"))
                )
            )
            
            .add(Method.of("getValue", primitiveType())
                .set(Javadoc.of(formatJavadoc(
                        "Returns the value that this setter will set in the " + 
                        "field when it is applied."
                    )).add(DefaultJavadocTag.RETURN.setValue("the field"))
                )
            )
        ;
    }
}