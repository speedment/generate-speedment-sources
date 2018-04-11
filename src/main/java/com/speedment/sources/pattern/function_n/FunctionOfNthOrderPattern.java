package com.speedment.sources.pattern.function_n;

import static com.speedment.common.codegen.constant.DefaultJavadocTag.AUTHOR;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.PARAM;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.RETURN;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.SEE;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.AnnotationUsage;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Interface;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.common.function.TriFunction;
import com.speedment.sources.Pattern;
import java.util.stream.IntStream;

/**
 *
 * @author Per Minborg
 */
public class FunctionOfNthOrderPattern implements Pattern {

    private static final String BASE_PACKAGE = TriFunction.class.getPackage().getName();
    private final int order;

    private static final String[] COUNT_NAMES = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven"};
    private static final String[] ORDINAL_NAMES = {"first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth", "ninth", "tenth", "eleventh"};

    public FunctionOfNthOrderPattern(int order) {
        if (order < 5) {
            throw new IllegalArgumentException("Order must be 5 or greater");
        }
        this.order = order;
    }

    @Override
    public String getFullClassName() {
        return BASE_PACKAGE + "." + getClassName();
    }

    @Override
    public String getClassName() {
        return "Function" + order;
    }

    @Override
    public boolean isTestClass() {
        return false;
    }

    @Override
    public ClassOrInterface<?> make(File file) {

        final Javadoc javadocInterface = Javadoc.of(
            " Represents a function that accepts " + orderName() + " arguments and produces a result.\n"
            + " This is the " + orderName() + "-arity specialization of\n"
            + " {@link java.util.function.Function}.\n"
            + "<p>\n"
            + "This class has been generated."
        ).add(AUTHOR.setValue("Per Minborg"));

        IntStream.range(0, order).forEachOrdered(i
            -> javadocInterface.add(PARAM.setValue("<T" + i + ">").setText("the type of the " + ordinalName(i + 1) + " argument to the function"))
        );
        javadocInterface.add(PARAM.setValue("<R>").setText("the type of the result of the function"));
        javadocInterface.add(SEE.setValue("java.util.function.Function"));

        final Javadoc methodJavadoc = Javadoc.of(
            "Applies this function to the given arguments."
        );

        IntStream.range(0, order).forEachOrdered(i
            -> methodJavadoc.add(PARAM.setValue("t" + i).setText("the " + ordinalName(i + 1) + " function argument"))
        );
        methodJavadoc.add(RETURN.setValue("the function result"));

        final Method applyMethod = Method.of("apply", SimpleType.create("R"))
            .set(methodJavadoc);

        IntStream.range(0, order).forEachOrdered(i
            -> applyMethod.add(Field.of("t" + i, SimpleType.create("T" + i)))
        );

        Interface inter = Interface.of(getClassName())
            .public_()
            .set(javadocInterface)
            .add(AnnotationUsage.of(FunctionalInterface.class))
            .add(applyMethod);

        IntStream.range(0, order).forEachOrdered(i
            -> inter.add(Generic.of("T" + i))
        );
        inter.add(Generic.of("R"));

        return inter;

    }

    String orderName() {
        final int index = order - 1;
        if (index >= COUNT_NAMES.length) {
            return Integer.toString(order);
        }
        return COUNT_NAMES[index];
    }

    String ordinalName(int ordinal) {
        final int index = ordinal - 1;
        if (index >= ORDINAL_NAMES.length) {
            return Integer.toString(ordinal) + ":th";
        }
        return ORDINAL_NAMES[index];
    }

}
