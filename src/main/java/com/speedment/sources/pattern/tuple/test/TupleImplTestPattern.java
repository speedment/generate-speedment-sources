package com.speedment.sources.pattern.tuple.test;

import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.Class;
import com.speedment.common.codegen.model.*;
import com.speedment.sources.Pattern;

import java.lang.reflect.Type;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static com.speedment.common.invariant.IntRangeUtil.requireNonNegative;
import static com.speedment.sources.pattern.tuple.TupleUtil.*;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author Per Minborg
 */
public class TupleImplTestPattern implements Pattern {

    private static final Type TEST = SimpleType.create("org.junit.jupiter.api.Test");
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
        file.add(Import.of(TEST));
        file.add(Import.of(SimpleType.create("org.junit.jupiter.api.Assertions")).static_().setStaticMember("*"));
        if (nullable) {
            file.add(Import.of(NoSuchElementException.class));
        }

        final Type[] intTypes = IntStream.range(0, degree)
            .mapToObj($ -> Integer.class)
            .toArray(java.lang.Class<?>[]::new);

        final Type superType = SimpleParameterizedType.create(
            "AbstractTupleImplTest",
            SimpleParameterizedType.create(
                nullable ? tupleOfNullablesImplementationSimpleName(degree) : tupleImplementationSimpleName(degree),
                intTypes
            )
        );

        final Class clazz = Class.of(getClassName())
            .final_()
            .setSupertype(superType);

        IntStream.range(0, degree).forEachOrdered(parameter -> {
            clazz.add(Generic.of(genericTypeName(parameter)));
        });

        final Constructor constructor;
        if (nullable) {
            constructor = Constructor.of()
                .add(
                    degree == 0
                        ? "super(() -> (Tuple0OfNullablesImpl) Tuple0OfNullablesImpl.EMPTY_TUPLE, 0);"
                        : "super(() -> new " + tupleOfNullablesImplementationSimpleName(degree) + "<>("
                        + IntStream.range(0, degree).boxed().map(Object::toString)
                        .collect(joining(", "))
                        + "), " + degree + ");"
                );
        } else {
            constructor = Constructor.of()
                .add(
                    degree == 0
                        ? "super(() -> (Tuple0Impl) Tuple0Impl.EMPTY_TUPLE, 0);"
                        : "super(() -> new " + tupleImplementationSimpleName(degree) + "<>("
                        + IntStream.range(0, degree).boxed().map(Object::toString)
                        .collect(joining(", "))
                        + "), " + degree + ");"
                );
        }

        clazz.add(constructor);

        IntStream.range(0, degree)
            .mapToObj(i -> this.getterTestMethod(i, "get", nullable))
            .forEachOrdered(clazz::add);

        if (nullable) {
            IntStream.range(0, degree)
                .mapToObj(i -> this.getterTestMethod(i, "getOrNull", false))
                .forEachOrdered(clazz::add);
        }

        file.imports(Import.of(IndexOutOfBoundsException.class));
        file.imports(Import.of(IntStream.class));

        final Method getTest = Method.of("get", void.class)
            .add(AnnotationUsage.of(TEST))
            .add("IntStream.range(0, " + degree + ").forEach(i -> assertEquals(i, instance.get(i)" + decorateGetter(nullable) + "));")
            .add("assertThrows(IndexOutOfBoundsException.class, () -> instance.get(-1));")
            .add("assertThrows(IndexOutOfBoundsException.class, () -> instance.get(" + degree + "));");

        clazz.add(getTest);

        return clazz;
    }

    private Method getterTestMethod(int index, String getName, boolean nulls) {
        return Method.of(getName + index + "Test", void.class)
            .add(AnnotationUsage.of(TEST))
            .add("assertEquals(" + index + ", (int) instance." + getName + index + "()" +
                decorateGetter(nulls) +
                ");");
    }

    private String decorateGetter(boolean nulls) {
        return nulls ? ".orElseThrow(NoSuchElementException::new)" : "";
    }

}
