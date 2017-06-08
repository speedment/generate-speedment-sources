package com.speedment.sources.pattern.function;

import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.*;
import com.speedment.sources.pattern.AbstractPattern;

import static com.speedment.common.codegen.constant.DefaultJavadocTag.*;

/**
 * @author Emil Forslund
 * @since  3.0.11
 */
public final class ToFunctionPattern extends AbstractPattern {

    public ToFunctionPattern(java.lang.Class<?> wrapper,
                             java.lang.Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getFullClassName() {
        return "com.speedment.common.function." + getClassName();
    }

    @Override
    public String getClassName() {
        return "To" + ucPrimitive() + "Function";
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        return Interface.of(getClassName())
            ////////////////////////////////////////////////////////////////////
            //                         Documentation                          //
            ////////////////////////////////////////////////////////////////////
            .set(Javadoc.of(formatJavadoc(
                    "A function that takes an object and returns a " +
                    "primitive {@code %2$s}."
                ))
                .add(PARAM.setValue("<T>").setText("argument type"))
                .add(AUTHOR.setValue("Emil Forslund"))
                .add(SINCE.setValue("1.0.2"))
            )

            ////////////////////////////////////////////////////////////////////
            //                     Interface Declaration                      //
            ////////////////////////////////////////////////////////////////////
            .public_()
            .add(functionalInterface())
            .add(Generic.of("T"))
            .add(Method.of("applyAs" + ucPrimitive(), primitiveType())
                .add(Field.of("value", SimpleType.create("T")))
                .set(Javadoc.of(
                    formatJavadoc(
                        "Applies this function to the given argument."
                    ))
                    .add(PARAM.setValue("value").setText("the argument"))
                    .add(RETURN.setValue("the result"))
                )
            );
    }
}
