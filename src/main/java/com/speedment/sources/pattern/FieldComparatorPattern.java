package com.speedment.sources.pattern;

import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Interface;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.runtime.field.comparator.FieldComparator;
import com.speedment.runtime.field.comparator.ReferenceFieldComparator;
import com.speedment.runtime.field.trait.HasReferenceValue;

import static com.speedment.common.codegen.constant.DefaultType.genericType;

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

            ////////////////////////////////////////////////////////////////////
            //                         Documentation                          //
            ////////////////////////////////////////////////////////////////////
            .javadoc(Javadoc.of(formatJavadoc(
                "A {@link FieldComparator} that compares values of a {@link " +
                    ucPrimitive() + "Field}."
                ))
                .param("<ENTITY>", "entity type")
                .param("<D>", "database type")
                .author("Emil Forslund")
                .since("3.0.0")
            )

            ////////////////////////////////////////////////////////////////////
            //                       Class Declaration                        //
            ////////////////////////////////////////////////////////////////////
            .public_()
            .add(generatedAnnotation())
            .generic("ENTITY")
            .generic("D")
            .implement(FieldComparator.class, "ENTITY")

            ////////////////////////////////////////////////////////////////////
            //                            Methods                             //
            ////////////////////////////////////////////////////////////////////
            .add(Method.of("getField", genericType(
                    siblingOf(HasReferenceValue.class, "Has%1$sValue"),
                    "ENTITY", "D"
                )).override()
                .javadoc(Javadoc.of("Gets the field that is being compared.")
                    .returns("the compared field")
                )
            )

            .add(Method.of("reversed", genericType(siblingOf(getSiblingClass(), getClassName()), "ENTITY", "D"))
                .override()
            )
        ;
    }
}