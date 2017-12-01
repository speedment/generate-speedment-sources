package com.speedment.sources.pattern.tuple.test;

import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.*;
import com.speedment.common.codegen.model.Class;
import com.speedment.sources.Pattern;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.stream.IntStream;

import static com.speedment.common.invariant.IntRangeUtil.requireNonNegative;
import static com.speedment.sources.pattern.tuple.TupleUtil.*;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author Per Minborg
 */
public class TupleImplTestPattern implements Pattern {

    private final int degree;
    private final boolean nullable;

    public TupleImplTestPattern(int degree, boolean nullable) {
        this.degree = requireNonNegative(degree);
        this.nullable = nullable;
    }

    @Override
    public String getClassName() {
        return (nullable
            ? tupleOfNullablesImplementationSimpleName(degree)
            : tupleImplementationSimpleName(degree)) + "Test";
    }

    @Override
    public String getFullClassName() {
        return (nullable
            ? tupleOfNullablesImplementationName(degree)
            : tupleImplementationName(degree)) + "Test";
    }

    @Override
    public boolean isTestClass() {
        return true;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        file.add(Import.of(SimpleType.create("org.junit.Assert")).static_().setStaticMember("*"));

        final Type[] intTypes = IntStream.range(0, degree)
            .mapToObj($ -> Integer.class)
            .toArray(java.lang.Class<?>[]::new);

        final Type superType = SimpleParameterizedType.create(
            "AbstractTupleImplTest",
            SimpleParameterizedType.create(
                tupleImplementationSimpleName(degree),
                intTypes
            )
        );

        final Class clazz = Class.of(getClassName())
            .public_().final_()
            .setSupertype(superType);

        IntStream.range(0, degree).forEachOrdered(parameter -> {
            clazz.add(Generic.of(genericTypeName(parameter)));
        });

        final Constructor constructor = Constructor.of()
            .public_()
            .add(
                degree == 0
                    ? "super(() -> (Tuple0Impl) Tuple0Impl.EMPTY_TUPLE, 0);"
                    : "super(() -> new " + tupleImplementationSimpleName(degree) + "<>("
                    + IntStream.range(0, degree).mapToObj(Integer::valueOf).map(Object::toString)
                        .collect(joining(", "))
                    + "), " + degree + ");"
            );

        clazz.add(constructor);

        IntStream.range(0, degree)
            .mapToObj(this::getterTestMethod)
            .forEachOrdered(clazz::add);

        return clazz;
    }

    private Method getterTestMethod(int index) {
        return Method.of("get" + index + "Test", void.class)
            .public_()
            .add(AnnotationUsage.of(Test.class))
            .add("assertEquals(" + index + ", (int) instance.get" + index + "());");
    }

}
