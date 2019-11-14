package com.speedment.sources.pattern.tuple.test;

import com.speedment.common.annotation.GeneratedCode;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.Class;
import com.speedment.common.codegen.model.*;
import com.speedment.common.tuple.Tuples;
import com.speedment.common.tuple.TuplesOfNullables;
import com.speedment.sources.Pattern;

import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.speedment.common.codegen.util.Formatting.shortName;
import static com.speedment.common.invariant.IntRangeUtil.requireNonNegative;
import static com.speedment.sources.pattern.tuple.TupleUtil.*;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author Per Minborg
 */
public class TupleMapperImplTestPattern implements Pattern {

    private static final Type TEST = SimpleType.create("org.junit.jupiter.api.Test");
    private final int degree;
    private final boolean nullable;

    public TupleMapperImplTestPattern(int degree, boolean nullable) {
        this.degree = requireNonNegative(degree);
        this.nullable = nullable;
    }

    @Override
    public String getClassName() {
        return shortName(getFullClassName());
    }

    @Override
    public String getFullClassName() {
        return mapperTypeName() + "Test";
    }

    @Override
    public boolean isTestClass() {
        return true;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        file.add(Import.of(TEST));
        file.add(Import.of(SimpleType.create("org.junit.jupiter.api.Assertions")).static_().setStaticMember("*"));
        if (nullable) {
            file.add(Import.of(TuplesOfNullables.class));
        } else {
            file.add(Import.of(Tuples.class));
        }

        final Class clazz = Class.of(getClassName())
            .final_();

        IntStream.range(0, degree)
            .mapToObj(this::field)
            .forEachOrdered(clazz::add);

        final Type instanceType = SimpleParameterizedType.create(mapperTypeName(), IntStream.range(0, degree + 1).mapToObj(i -> Integer.class).toArray(Type[]::new));
        final Field instance = Field.of("instance", instanceType);
        if (degree == 0) {
            instance.set(Value.ofReference("("+shortName(mapperTypeName())+") "+shortName(mapperTypeName())+".EMPTY_MAPPER;"))
            .add(AnnotationUsage.of(SuppressWarnings.class).put("value", Value.ofText("unchecked")));
        } else {
            instance.set(Value.ofReference("new "+shortName(mapperTypeName())+"<>("+ IntStream.range(0, degree).mapToObj(i -> "m"+i).collect(joining(", "))+")"));
        }
        instance
            .private_()
            .final_();

        clazz.add(instance);

        clazz.add(testMethod("degree").add("assertEquals(" + degree + ", instance.degree());"));

        final String tuplesMethod = nullable
            ? "TuplesOfNullables.ofNullables"
            : "Tuples.of";

        clazz.add(testMethod("apply").add("assertEquals("+tuplesMethod+"(" + IntStream.range(0, degree).boxed().map(Object::toString).collect(joining(", ")) + "), instance.apply(0));"));

        IntStream.range(0, degree)
            .mapToObj(this::getterTestMethod)
            .forEachOrdered(clazz::add);

        return clazz;
    }

    private Field field(int index) {
        return Field.of("m" + index, SimpleParameterizedType.create(Function.class, Integer.class, Integer.class))
            .set(Value.ofReference("i -> i + " + index))
            .private_()
            .final_();
    }

    private Method getterTestMethod(int index) {
        return testMethod("get" + index)
            .add("assertEquals(m" + index + ", instance.get" + index + "());");
    }

    private String mapperTypeName() {
        return (nullable
            ? tupleOfNullablesMapperImplementationName(degree)
            : tupleMapperImplementationName(degree));
    }

    private Method testMethod(String name) {
        return Method.of(name, void.class)
            .add(AnnotationUsage.of(TEST));
    }

}
