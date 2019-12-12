package com.speedment.sources.pattern.tuple.test;

import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.Class;
import com.speedment.common.codegen.model.*;
import com.speedment.common.tuple.getter.TupleGetter;
import com.speedment.sources.Pattern;
import com.speedment.sources.pattern.tuple.TupleUtil;

import java.lang.reflect.Type;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.speedment.common.codegen.util.Formatting.*;
import static com.speedment.sources.pattern.tuple.TupleUtil.BASE_PACKAGE;
import static com.speedment.sources.pattern.tuple.TupleUtil.MAX_DEGREE;
import static java.util.stream.Collectors.joining;

/**
 * @author Per Minborg
 */
public class TupleOfNullablesTestPattern implements Pattern {

    private static final Type TEST = SimpleType.create("org.junit.jupiter.api.Test");

    @Override
    public String getClassName() {
        return shortName(getFullClassName());
    }

    @Override
    public String getFullClassName() {
        return BASE_PACKAGE + ".TupleOfNullablesTest";
    }

    @Override
    public boolean isTestClass() {
        return true;
    }

    @Override
    public ClassOrInterface<?> make(File file) {

        file.add(Import.of(TEST));
        file.add(Import.of(SimpleType.create("org.junit.jupiter.api.Assertions")).static_().setStaticMember("*"));
        file.add(Import.of(TupleGetter.class));
        file.add(Import.of(Optional.class));
        file.add(Import.of(NoSuchElementException.class));

        final Class clazz = Class.of(getClassName())
            .final_();

        IntStream.range(1, MAX_DEGREE)
            .mapToObj(i -> tuple(i, file))
            .forEachOrdered(clazz::add);

        IntStream.range(1, MAX_DEGREE)
            .mapToObj(i -> tupleTest(i, file))
            .forEachOrdered(clazz::add);

        return clazz;
    }

    private Method tuple(int degree, File file) {
        final Type tupleType = SimpleType.create(TupleUtil.tupleOfNullablesName(degree));
        file.add(Import.of(tupleType));
        final String tupleTypeName = shortName(tupleType.getTypeName());
        final Type tupleGenericType = SimpleParameterizedType.create(tupleType, IntStream.range(0, degree).mapToObj(unused -> Integer.class).toArray(java.lang.Class[]::new));
        final String tupleGenericTypeName = tupleTypeName + "<" + IntStream.range(0, degree).mapToObj(unused -> "Integer").collect(joining(", ")) + ">";
        final Method m = Method.of("tuple" + degree, void.class)
            .add(AnnotationUsage.of(TEST))
            .add("final " + tupleGenericTypeName + " tuple = TuplesOfNullables.ofNullables(" + IntStream.range(0, degree).boxed().map(Object::toString).collect(joining(", ")) + ");")
            .add("tupleTest(tuple);");

        m.add("final " + tupleGenericTypeName + " defaultTuple = new " + tupleGenericTypeName + "() ");
        m.add(block(
            IntStream.range(0, degree)
            .mapToObj(i ->
                "@Override" + nl() +
                "public Optional<Integer> get" + i + "() " +
                block("return Optional.of(" + i + ");")
            )

        ) + ";")
        .add("tupleTest(defaultTuple);");
        return m;
    }

    private Method tupleTest(int degree, File file) {
        final Type tupleType = SimpleType.create(TupleUtil.tupleOfNullablesName(degree));
        final String tupleTypeName = shortName(tupleType.getTypeName());
        final Type tupleGenericType = SimpleParameterizedType.create(tupleType, IntStream.range(0, degree).mapToObj(unused -> Integer.class).toArray(java.lang.Class[]::new));
        final String tupleGenericTypeName = tupleTypeName + "<" + IntStream.range(0, degree).mapToObj(unused -> "Integer").collect(joining(", ")) + ">";
        final Method m = Method.of("tupleTest" , void.class)
            .private_()
            .add(Field.of("tuple", tupleGenericType).final_());

        IntStream.range(0, degree).forEach(i -> {
            m.add("TupleGetter<" + tupleGenericTypeName + ", Optional<Integer>> getter" + i + " = " + tupleTypeName + ".getter" + i + "();");
        });
        IntStream.range(0, degree).forEach(i -> {
            m.add("TupleGetter<" + tupleGenericTypeName + ", Integer> getterOrNull" + i + " = " + tupleTypeName + ".getterOrNull" + i + "();");
        });
        IntStream.range(0, degree).forEach(i -> {
            m.add("assertEquals(" + i + ", getter" + i + ".index());");
        });

        IntStream.range(0, degree).forEach(i -> {
            m.add("assertEquals(" + i + ", getter" + i + ".apply(tuple).orElseThrow(NoSuchElementException::new));");
        });
        IntStream.range(0, degree).forEach(i -> {
            m.add("assertEquals(" + i + ", getterOrNull" + i + ".apply(tuple));");
        });

        IntStream.range(0, degree).forEach(i -> {
            m.add("assertEquals(" + i + ", tuple.get(" + i + ").orElseThrow(NoSuchElementException::new));");
        });

        m.add("assertThrows(IndexOutOfBoundsException.class, () -> tuple.get(-1));");
        m.add("assertThrows(IndexOutOfBoundsException.class, () -> tuple.get(" + degree + "));");

        return m;
    }


}
