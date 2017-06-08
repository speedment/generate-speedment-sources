package com.speedment.sources.pattern.function;

import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.*;
import com.speedment.sources.pattern.AbstractPattern;

import static com.speedment.common.codegen.constant.DefaultJavadocTag.*;

/**
 * @author Emil Forslund
 * @since  3.0.11
 */
public final class FunctionPattern extends AbstractPattern {

    public FunctionPattern(java.lang.Class<?> wrapper,
                           java.lang.Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getFullClassName() {
        return "com.speedment.common.function." + getClassName();
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "Function";
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        return Interface.of(getClassName())
            ////////////////////////////////////////////////////////////////////
            //                         Documentation                          //
            ////////////////////////////////////////////////////////////////////
            .set(Javadoc.of(formatJavadoc(
                    "A function that takes a primitive {@code %2$s} and " +
                    "returns an object."
                ))
                .add(PARAM.setValue("<T>").setText("return type"))
                .add(AUTHOR.setValue("Emil Forslund"))
                .add(SINCE.setValue("1.0.2"))
            )

            ////////////////////////////////////////////////////////////////////
            //                     Interface Declaration                      //
            ////////////////////////////////////////////////////////////////////
            .public_()
            .add(functionalInterface())
            .add(Generic.of("T"))
            .add(Method.of("apply", SimpleType.create("T"))
                .add(Field.of("value", primitiveType()))
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
