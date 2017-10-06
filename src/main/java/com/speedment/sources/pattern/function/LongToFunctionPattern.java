package com.speedment.sources.pattern.function;

import com.speedment.common.codegen.model.*;
import com.speedment.sources.pattern.AbstractPattern;

import static com.speedment.common.codegen.constant.DefaultJavadocTag.*;

/**
 * @author Emil Forslund
 * @since  3.0.15
 */
public final class LongToFunctionPattern extends AbstractPattern {

    public LongToFunctionPattern(java.lang.Class<?> wrapper,
                                 java.lang.Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getFullClassName() {
        return "com.speedment.common.function." + getClassName();
    }

    @Override
    public String getClassName() {
        return "LongTo" + ucPrimitive() + "Function";
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        return Interface.of(getClassName())
            ////////////////////////////////////////////////////////////////////
            //                         Documentation                          //
            ////////////////////////////////////////////////////////////////////
            .set(Javadoc.of(formatJavadoc(
                    "Functional interface that corresponds to the method signature {@code %2$s apply(long)}."
                ))
                .add(AUTHOR.setValue("Emil Forslund"))
                .add(SINCE.setValue("1.0.4"))
            )

            ////////////////////////////////////////////////////////////////////
            //                     Interface Declaration                      //
            ////////////////////////////////////////////////////////////////////
            .public_()

            ////////////////////////////////////////////////////////////////////
            //                            Methods                             //
            ////////////////////////////////////////////////////////////////////
            .add(Method.of("applyAs" + ucPrimitive(), primitiveType())
                .add(Field.of("value", long.class))
                .set(Javadoc.of(
                    formatJavadoc(
                        "Returns the {@code %2$s} value for the specified {@code long}. This method should operate without side-effects."
                    ))
                    .add(PARAM.setValue("value").setText("the input {@code long} value"))
                    .add(RETURN.setValue("the resulting value"))
                )
            );
    }
}
