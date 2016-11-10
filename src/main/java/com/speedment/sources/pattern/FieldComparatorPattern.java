package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.DefaultJavadocTag;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Interface;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.runtime.field.ReferenceField;
import com.speedment.runtime.field.comparator.FieldComparator;
import com.speedment.runtime.field.internal.comparator.ReferenceFieldComparator;

/**
 *
 * @author Emil Forslund
 */
public final class FieldComparatorPattern extends AbstractSiblingPattern {

    public FieldComparatorPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    protected Class<?> getSiblingClass() {
        return ReferenceFieldComparator.class;
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "FieldComparator";
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        return Interface.of(getClassName())
            
            /******************************************************************/
            /*                         Documentation                          */
            /******************************************************************/
            .set(Javadoc.of(formatJavadoc(
                "A predicate that evaluates if a value is between two %2$ss."
                ))
                .add(DefaultJavadocTag.PARAM.setValue("<ENTITY>").setText("entity type"))
                .add(DefaultJavadocTag.PARAM.setValue("<D>").setText("database type"))
                .add(DefaultJavadocTag.AUTHOR.setValue("Emil Forslund"))
                .add(DefaultJavadocTag.SINCE.setValue("3.0.0"))
            )
            
            /******************************************************************/
            /*                       Class Declaration                        */
            /******************************************************************/
            .public_()
            .add(generatedAnnotation())
            .add(Generic.of("ENTITY"))
            .add(Generic.of("D"))
            .add(SimpleParameterizedType.create(
                FieldComparator.class, 
                SimpleType.create("ENTITY"),
                wrapperType()
            ))
            
            /******************************************************************/
            /*                            Methods                             */
            /******************************************************************/
            .add(Method.of("getField", SimpleParameterizedType.create(
                    siblingOf(ReferenceField.class, "%1$sField"),
                    SimpleType.create("ENTITY"),
                    SimpleType.create("D")
                ))
                .set(Javadoc.of("Gets the field that is being compared.")
                    .add(DefaultJavadocTag.RETURN.setValue("the compared field"))
                )
            )
            
            .add(Method.of("isReversed", boolean.class)
                .set(Javadoc.of("Returns if this {@code Comparator} is reversed.")
                    .add(DefaultJavadocTag.RETURN.setValue("if this is reversed"))
                )
            )
        ;
    }
}