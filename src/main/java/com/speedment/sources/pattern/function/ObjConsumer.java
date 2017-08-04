package com.speedment.sources.pattern.function;

import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.*;
import com.speedment.sources.pattern.AbstractPattern;

import java.util.function.BiConsumer;

import static com.speedment.common.codegen.constant.DefaultJavadocTag.*;

/**
 * @author Emil Forslund
 * @since  3.0.13
 */
public final class ObjConsumer extends AbstractPattern {

    public ObjConsumer(java.lang.Class<?> wrapper,
                       java.lang.Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getFullClassName() {
        return "com.speedment.common.function." + getClassName();
    }

    @Override
    public String getClassName() {
        return "Obj" + ucPrimitive() + "Consumer";
    }

    @Override
    public ClassOrInterface<?> make(File file) {

        file.add(Import.of(BiConsumer.class));

        return Interface.of(getClassName())
            ////////////////////////////////////////////////////////////////////
            //                         Documentation                          //
            ////////////////////////////////////////////////////////////////////
            .set(Javadoc.of(formatJavadoc(
                    "Represents an operation that accepts an object-valued and a " +
                    "{@code %2$s}-valued argument, and returns no result. This is the " +
                    "{@code (reference, %2$s)} specialization of {@link BiConsumer}. " +
                    "Unlike most other functional interfaces, {@code Obj" +
                    ucPrimitive() + "Consumer} is expected to operate via " +
                    "side-effects.\n" +
                    "<p>\n" +
                    "This is a <a href=\"package-summary.html\">functional interface</a> " +
                    "whose functional method is {@link #accept(Object, %2$s)}."
                ))
                .add(PARAM.setValue("<T>").setText("the type of the object argument to the operation"))
                .add(AUTHOR.setValue("Emil Forslund"))
                .add(SINCE.setValue("1.0.3"))
                .add(SEE.setValue("BiConsumer"))
            )

            ////////////////////////////////////////////////////////////////////
            //                     Interface Declaration                      //
            ////////////////////////////////////////////////////////////////////
            .public_()
            .add(functionalInterface())
            .add(Generic.of("T"))
            .add(Method.of("accept", void.class)
                .add(Field.of("t", SimpleType.create("T")))
                .add(Field.of("value", primitiveType()))
                .set(Javadoc.of(
                    formatJavadoc(
                        "Performs this operation on the given arguments."
                    ))
                    .add(PARAM.setValue("t").setText("the first input argument"))
                    .add(PARAM.setValue("value").setText("the second input argument"))
                )
            );
    }
}