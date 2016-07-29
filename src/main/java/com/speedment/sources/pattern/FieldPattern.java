package com.speedment.sources.pattern;

import com.speedment.common.codegen.internal.model.constant.DefaultJavadocTag;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Interface;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Type;
import com.speedment.runtime.field.ReferenceField;
import com.speedment.runtime.field.trait.HasComparableOperators;
import com.speedment.runtime.field.trait.HasReferenceValue;

/**
 *
 * @author Emil Forslund
 */
public final class FieldPattern extends AbstractSiblingPattern {

    public FieldPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "Field";
    }

    @Override
    protected Class<?> getSiblingClass() {
        return ReferenceField.class;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        return Interface.of(getClassName())
            
            /******************************************************************/
            /*                         Documentation                          */
            /******************************************************************/
            .set(Javadoc.of(formatJavadoc(
                "A field that represents a primitive {@code %2$s} value."
                ))
                .add(DefaultJavadocTag.PARAM.setValue("<ENTITY>").setText("entity type"))
                .add(DefaultJavadocTag.PARAM.setValue("<D>").setText("database type"))
                .add(DefaultJavadocTag.AUTHOR.setValue("Emil Forslund"))
                .add(DefaultJavadocTag.SINCE.setValue("3.0.0"))
                .add(DefaultJavadocTag.SEE.setValue(ReferenceField.class.getSimpleName()))
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
            .add(siblingOf(HasReferenceValue.class, "Has%1$sValue")
                .add(Generic.of(Type.of("ENTITY")))
                .add(Generic.of(Type.of("D")))
            )
            .add(Type.of(HasComparableOperators.class)
                .add(Generic.of(Type.of("ENTITY")))
                .add(Generic.of(wrapperType()))
            )
        ;
    }
}