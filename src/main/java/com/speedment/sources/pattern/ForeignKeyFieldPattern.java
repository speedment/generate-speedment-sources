package com.speedment.sources.pattern;

import com.speedment.common.codegen.internal.model.constant.DefaultJavadocTag;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Interface;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Type;
import com.speedment.runtime.field.ReferenceField;
import com.speedment.runtime.field.ReferenceForeignKeyField;
import com.speedment.runtime.field.trait.HasFinder;

/**
 *
 * @author Emil Forslund
 */
public final class ForeignKeyFieldPattern extends AbstractSiblingPattern {

    public ForeignKeyFieldPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "ForeignKeyField";
    }

    @Override
    protected Class<?> getSiblingClass() {
        return ReferenceForeignKeyField.class;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        return Interface.of(getClassName())
            
            /******************************************************************/
            /*                         Documentation                          */
            /******************************************************************/
            .set(Javadoc.of(formatJavadoc(
                    "A field that represents a primitive {@code %2$s} value " + 
                    "that references another column using a foreign key."
                ))
                .add(DefaultJavadocTag.PARAM.setValue("<ENTITY>").setText("entity type"))
                .add(DefaultJavadocTag.PARAM.setValue("<D>").setText("database type"))
                .add(DefaultJavadocTag.PARAM.setValue("<FK_ENTITY>").setText("foreign entity type"))
                .add(DefaultJavadocTag.AUTHOR.setValue("Emil Forslund"))
                .add(DefaultJavadocTag.SINCE.setValue("3.0.0"))
                .add(DefaultJavadocTag.SEE.setValue(ReferenceField.class.getSimpleName()))
                .add(DefaultJavadocTag.SEE.setValue(ReferenceForeignKeyField.class.getSimpleName()))
            )
            
            /******************************************************************/
            /*                       Class Declaration                        */
            /******************************************************************/
            .add(apiAnnotation())
            .public_()
            .add(Generic.of("ENTITY"))
            .add(Generic.of("D"))
            .add(Generic.of("FK_ENTITY"))
            .add(siblingOf(ReferenceField.class, "%1$sField")
                .add(Generic.of(Type.of("ENTITY")))
                .add(Generic.of(Type.of("D")))
            )
            .add(Type.of(HasFinder.class)
                .add(Generic.of(Type.of("ENTITY")))
                .add(Generic.of(Type.of("FK_ENTITY")))
            )
        ;
    }
}