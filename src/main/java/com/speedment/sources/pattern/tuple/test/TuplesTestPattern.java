package com.speedment.sources.pattern.tuple.test;

import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.Class;
import com.speedment.common.codegen.model.*;
import com.speedment.common.tuple.Tuple;
import com.speedment.sources.Pattern;

import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.speedment.common.codegen.util.Formatting.block;
import static com.speedment.common.codegen.util.Formatting.shortName;
import static com.speedment.common.invariant.IntRangeUtil.requireNonNegative;
import static com.speedment.sources.pattern.tuple.TupleUtil.BASE_PACKAGE;
import static com.speedment.sources.pattern.tuple.TupleUtil.MAX_DEGREE;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author Per Minborg
 */
public class TuplesTestPattern implements Pattern {

    private static final Type TEST = SimpleType.create("org.junit.jupiter.api.Test");

    @Override
    public String getClassName() {
        return shortName(getFullClassName());
    }

    @Override
    public String getFullClassName() {
        return BASE_PACKAGE + ".TuplesTest";
    }

    @Override
    public boolean isTestClass() {
        return true;
    }

    @Override
    public ClassOrInterface<?> make(File file) {

        file.add(Import.of(TEST));
        file.add(Import.of(SimpleType.create("org.junit.jupiter.api.Assertions")).static_().setStaticMember("*"));
        file.add(Import.of(Function.class));

        final Class clazz = Class.of(getClassName())
            .final_();

        IntStream.range(0, MAX_DEGREE)
            .boxed()
            .flatMap(i -> Stream.of(of(i), toTuple(i)))
            .forEachOrdered(clazz::add);

        clazz.add(assertTuple());

        return clazz;
    }

    private Method of(int degree) {
        return Method.of("of" + degree, void.class)
            .add(AnnotationUsage.of(TEST))
            .add("assertTuple(Tuples.of(" + range(degree, UnaryOperator.identity()) + "), " + degree + ");");
    }

    private Method toTuple(int degree) {
        return Method.of("toTuple" + degree, void.class)
            .add(AnnotationUsage.of(TEST))
            .add("final Function<Integer, Tuple" + degree +
                (degree == 0 ? "" : "<" + range(degree, unused -> "Integer") + ">") +
                "> mapper = Tuples.toTuple(" + range(degree, s -> "i -> i + " + s) + ");")
            .add("assertTuple(mapper.apply(0), " + degree + ");");
    }

    private String range(int degree, UnaryOperator<String> mapper) {
        return IntStream.range(0, degree)
            .boxed()
            .map(Object::toString)
            .map(mapper)
            .collect(joining(", "));
    }

    private Method assertTuple() {
        return Method.of("assertTuple", void.class)
            .private_()
            .add(Field.of("tuple", Tuple.class))
            .add(Field.of("degree", int.class))
            .add("assertEquals(degree, tuple.degree());")
            .add("for (int i = 0; i < degree; i++)"
                +block("assertEquals(i, tuple.get(i));")
            );
    }

}
