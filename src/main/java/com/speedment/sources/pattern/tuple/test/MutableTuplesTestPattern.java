package com.speedment.sources.pattern.tuple.test;

import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.Class;
import com.speedment.common.codegen.model.*;
import com.speedment.common.tuple.MutableTuple;
import com.speedment.sources.Pattern;
import com.speedment.sources.pattern.tuple.TupleUtil;

import java.lang.reflect.Type;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.speedment.common.codegen.util.Formatting.block;
import static com.speedment.common.codegen.util.Formatting.shortName;
import static com.speedment.sources.pattern.tuple.TupleUtil.BASE_PACKAGE;
import static com.speedment.sources.pattern.tuple.TupleUtil.MAX_DEGREE;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author Per Minborg
 */
public class MutableTuplesTestPattern implements Pattern {

    private static final Type TEST = SimpleType.create("org.junit.jupiter.api.Test");

    @Override
    public String getClassName() {
        return shortName(getFullClassName());
    }

    @Override
    public String getFullClassName() {
        return BASE_PACKAGE + ".MutableTuplesTest";
    }

    @Override
    public boolean isTestClass() {
        return true;
    }

    @Override
    public ClassOrInterface<?> make(File file) {

        file.add(Import.of(TEST));
        file.add(Import.of(SimpleType.create("org.junit.jupiter.api.Assertions")).static_().setStaticMember("*"));
        file.add(Import.of(Supplier.class));
        file.add(Import.of(NoSuchElementException.class));

        IntStream.range(0, MAX_DEGREE)
            .mapToObj(TupleUtil::mutableTupleName)
            .map(SimpleType::create)
            .map(Import::of)
            .forEach(file::add);


        final Class clazz = Class.of(getClassName())
            .final_();

        IntStream.range(0, MAX_DEGREE)
            .boxed()
            .flatMap(i -> Stream.of(create(i), constructor(i), test(i)))
            .forEachOrdered(clazz::add);

        clazz.add(assertTuple());

        return clazz;
    }

    private Method create(int degree) {
        return Method.of("create" + degree, void.class)
            .add(AnnotationUsage.of(TEST))
            .add("final MutableTuple" + degree +
                (degree == 0 ? "" : "<" + range(degree, s -> "Integer") + ">") +
                " tuple = MutableTuples.create" + degree + "();")
            .add("test"+degree+"(tuple);");
    }

    private Method constructor(int degree) {
        return Method.of("constructor" + degree, void.class)
            .add(AnnotationUsage.of(TEST))
            .add("final Supplier<MutableTuple" + degree +
                (degree == 0 ? "" : "<" + range(degree, unused -> "Integer") + ">") +
                "> constructor = MutableTuples.constructor(" + range(degree, unused -> "Integer.class") + ");")
            .add("test"+degree+"(constructor.get());");
    }

    private Method test(int degree) {
        final Type[] generics = IntStream.range(0, degree).mapToObj(unused -> Integer.class)
            .toArray(java.lang.Class[]::new);
        final Type type = SimpleParameterizedType.create(SimpleType.create("MutableTuple"+degree), generics);

        final Method m = Method.of("test" + degree, void.class)
            .add(Field.of("tuple", type).final_());

        IntStream.range(0,degree)
            .mapToObj(i -> "tuple.set" + i + "(" + i + ");")
            .forEach(m::add);

        return m.add("assertTuple(tuple, " + degree + ");");
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
            .add(Field.of("tuple", MutableTuple.class))
            .add(Field.of("degree", int.class))
            .add("assertEquals(degree, tuple.degree());")
            .add("for (int i = 0; i < degree; i++)"
                +block("assertEquals(i, tuple.get(i).orElseThrow(NoSuchElementException::new));")
            );
    }

}
