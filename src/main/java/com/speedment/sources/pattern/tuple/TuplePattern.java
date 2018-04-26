package com.speedment.sources.pattern.tuple;

import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.*;
import com.speedment.common.tuple.Tuple;
import com.speedment.common.tuple.TupleOfNullables;
import com.speedment.common.tuple.getter.TupleGetter;
import com.speedment.sources.Pattern;

import java.lang.Class;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.speedment.common.codegen.constant.DefaultAnnotationUsage.OVERRIDE;
import static com.speedment.common.codegen.constant.DefaultAnnotationUsage.SUPPRESS_WARNINGS_UNCHECKED;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.AUTHOR;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.PARAM;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.RETURN;
import static com.speedment.common.codegen.util.Formatting.block;
import static com.speedment.common.codegen.util.Formatting.nl;
import static com.speedment.common.invariant.IntRangeUtil.requireNonNegative;
import static com.speedment.sources.pattern.tuple.TupleUtil.*;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author Per Minborg
 */
public class TuplePattern implements Pattern {

    private final int degree;
    private final boolean nullable;

    public TuplePattern(int degree, boolean nullable) {
        this.degree = requireNonNegative(degree);
        this.nullable = nullable;
    }

    @Override
    public String getFullClassName() {
        return nullable ? tupleOfNullablesName(degree) : tupleName(degree);
    }

    @Override
    public String getClassName() {
        return nullable ? tupleOfNullablesSimpleName(degree) : tupleSimpleName(degree);
    }

    @Override
    public boolean isTestClass() {
        return false;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        file.add(Import.of(Tuple.class));

        final Javadoc javadoc
            = nullable
                ? Javadoc.of(
                    " This interface defines a generic Tuple of degree " + degree + " that can hold non-null\n"
                    + "values. A Tuple is type safe, immutable and thread safe. For Tuples that can hold\n"
                    + "null elements see {@link TupleOfNullables}" + nl()
                    + nl()
                    + "This {@link Tuple } has a degree of " + degree
                )
                : Javadoc.of("This interface defines a generic Tuple of any order that can hold null\n"
                    + "values. A Tuple is type safe, immutable and thread safe. For pure non-null\n"
                    + "value elements see {@link Tuple}"
                    + nl()
                    + "This {@link Tuple } has a degree of " + degree);

        javadoc.add(AUTHOR.setValue("Per Minborg"));

        IntStream.range(0, degree).forEachOrdered(parameter -> {
            javadoc.add(PARAM.setValue("<" + genericTypeName(parameter) + ">").setText("type of element " + parameter));
        });

        Type t = Tuple.class;

        final Interface iface = Interface.of(getClassName())
            .public_()
            .set(javadoc)
            .add(nullable ? TupleOfNullables.class : Tuple.class);

        IntStream.range(0, degree).forEachOrdered(parameter -> {
            iface.add(Generic.of(genericTypeName(parameter)));
        });

        IntStream.range(0, degree)
            .mapToObj(this::getterMethod)
            .forEachOrdered(iface::add);

        iface.add(
            Method.of("degree", int.class)
                .default_()
                .add(OVERRIDE)
                .add("return " + degree + ";")
        );

        iface.add(getterMethod());

        file.add(Import.of(TupleGetter.class));
        for (int i = 0; i < degree; i++) {
            final Type getterType = SimpleParameterizedType.create(
                siblingOf(TupleGetter.class, "TupleGetter" + i),
                SimpleParameterizedType.create(
                    siblingOf(Tuple.class, "Tuple" + degree),
                    IntStream.range(0, degree)
                        .mapToObj(TupleUtil::genericTypeName)
                        .map(SimpleType::create)
                        .toArray(Type[]::new)
                ),
                SimpleType.create(genericTypeName(i))
            );

            final Javadoc doc = Javadoc.of(
                "Returns a {@link TupleGetter getter} for the " +
                pluralize(i) + " element in the {@code Tuple}."
            ).add(RETURN.setValue("the element at the " + pluralize(i) + " position"));

            IntStream.range(0, degree)
                .mapToObj(j -> PARAM.setValue(genericTypeName(j)).setText("the " + pluralize(j) + " element type"))
                .forEachOrdered(doc::add);

            final Method getterMethod = Method.of("getter" + i, getterType)
                .set(Javadoc.of("Returns a {@link TupleGetter getter} for the " + pluralize(i) + " element in the {@code Tuple}.")

                )
                .static_()
                .add("return Tuple" + degree + "::get" + i + ";");

            IntStream.range(0, degree)
                .mapToObj(TupleUtil::genericTypeName)
                .map(Generic::of)
                .forEachOrdered(getterMethod::add);

            iface.add(getterMethod);
        }

        return iface;
    }

    private Method getterMethod(int index) {
        return Method.of(
            "get" + index,
            nullable
                ? SimpleParameterizedType.create(Optional.class, SimpleType.create(genericTypeName(index)))
                : SimpleType.create(genericTypeName(index))
        );
    }

    private Method getterMethod() {
        Method method = Method.of("get", nullable ? SimpleParameterizedType.create(Optional.class, Object.class) : Object.class)
            .default_()
            .add(Field.of("index", int.class))
            .add("switch (index) "
                + block(
                    IntStream.range(0, degree)
                        .mapToObj(i -> "case " + i + " : return " + (nullable ? "(Optional<Object>)" : "") + "get" + i + "();")
                        .collect(joining(nl()))
                    + nl()
                    + "default : throw new IllegalArgumentException(String.format(\"Index %d is outside bounds of tuple of degree %s\", index, degree()\n"
                    + "));"
                )
            );
        if (nullable) {
            method.add(SUPPRESS_WARNINGS_UNCHECKED);
        }
        return method;
    }

    private Type siblingOf(Class<?> packageOf, String name) {
        return SimpleType.create(packageOf.getPackage().getName() + "." + name);
    }
}
