package com.speedment.sources.pattern.function;

import static com.speedment.common.codegen.constant.DefaultJavadocTag.*;
import static com.speedment.common.codegen.constant.DefaultType.set;
import static com.speedment.common.codegen.constant.DefaultType.supplier;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.*;
import com.speedment.sources.pattern.AbstractPattern;
import java.util.function.BinaryOperator;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;

/**
 * @author Emil Forslund
 * @since  3.0.13
 */
public final class ToLongCollectorPattern extends AbstractPattern {

    public ToLongCollectorPattern(java.lang.Class<?> wrapper,
                                  java.lang.Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getFullClassName() {
        return "com.speedment.common.function.collector." + getClassName();
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "ToLongCollector";
    }

    @Override
    public ClassOrInterface<?> make(File file) {

        file.add(Import.of(Collector.class));

        return Interface.of(getClassName())
            ////////////////////////////////////////////////////////////////////
            //                         Documentation                          //
            ////////////////////////////////////////////////////////////////////
            .set(Javadoc.of(formatJavadoc(
                    "Primitive collector that operates on {@code %2$s} " +
                    "values, resulting in a single {@code long}."
                ))
                .add(PARAM.setValue("<A>").setText("the intermediary accumulating type"))
                .add(AUTHOR.setValue("Emil Forslund"))
                .add(SINCE.setValue("1.0.3"))
                .add(SEE.setValue("Collector"))
            )

            ////////////////////////////////////////////////////////////////////
            //                     Interface Declaration                      //
            ////////////////////////////////////////////////////////////////////
            .public_()
            .add(Generic.of("A"))

            ////////////////////////////////////////////////////////////////////
            //                            Methods                             //
            ////////////////////////////////////////////////////////////////////
            .add(Method.of("supplier", supplier(SimpleType.create("A")))
                .set(Javadoc.of(
                    formatJavadoc(
                        "Returns a supplier that can create an intermediary " +
                        "accumulating object."
                    ))
                    .add(RETURN.setValue("the supplier for the accumulating object"))
                    .add(SEE.setValue("Collector#supplier()"))
                )
            )

            .add(Method.of("accumulator", SimpleParameterizedType.create(
                    FunctionUtil.objXConsumer(ucPrimitive()),
                    SimpleType.create("A")
                ))
                .set(Javadoc.of(formatJavadoc(
                        "Stateless function that takes an accumulating " +
                        "object returned by{@link #supplier()} and adds a " +
                        "single {@code %2$s} value to it."
                    ))
                    .add(RETURN.setValue("the accumulator"))
                    .add(SEE.setValue("Collector#accumulator()"))
                )
            )

            .add(Method.of("combiner", SimpleParameterizedType.create(
                    BinaryOperator.class,
                    SimpleType.create("A")
                ))
                .set(Javadoc.of(
                    formatJavadoc(
                        "Stateless function that takes two accumulating " +
                        "objects and returns a single one representing the " +
                        "combined result. This can be either one of the two " +
                        "instances or a completely new instance."
                    ))
                    .add(RETURN.setValue("the combiner"))
                    .add(SEE.setValue("Collector#combiner()"))
                )
            )

            .add(Method.of("finisher", SimpleParameterizedType.create(
                    ToLongFunction.class,
                    SimpleType.create("A")
                ))
                .set(Javadoc.of(formatJavadoc(
                        "Returns a finisher function that takes an " +
                        "accumulating object and turns it into the final " +
                        "{@code long}."
                    ))
                    .add(RETURN.setValue("the finisher"))
                    .add(SEE.setValue("Collector#finisher()"))
                )
            )

            .add(Method.of("characteristics", set(Collector.Characteristics.class))
                .set(Javadoc.of(
                    formatJavadoc(
                        "Returns a set of characteristics for this collector."
                    ))
                    .add(RETURN.setValue("the characteristics for this collector"))
                    .add(SEE.setValue("Collector#characteristics()"))
                )
            );
    }
}