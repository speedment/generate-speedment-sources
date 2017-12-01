package com.speedment.sources.pattern.tuple;

import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.internal.model.value.ReferenceValueImpl;
import com.speedment.common.codegen.model.Class;
import com.speedment.common.codegen.model.*;
import com.speedment.common.tuple.Tuple;
import com.speedment.common.tuple.TupleOfNullables;
import com.speedment.common.tuple.internal.AbstractTuple;
import com.speedment.common.tuple.internal.AbstractTupleOfNullables;
import com.speedment.sources.Pattern;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.speedment.common.codegen.constant.DefaultAnnotationUsage.OVERRIDE;
import static com.speedment.common.codegen.constant.DefaultAnnotationUsage.SUPPRESS_WARNINGS_UNCHECKED;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.AUTHOR;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.PARAM;
import static com.speedment.common.codegen.constant.DefaultType.optional;
import static com.speedment.common.invariant.IntRangeUtil.requireNonNegative;
import static com.speedment.sources.pattern.tuple.TupleUtil.*;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author Per Minborg
 */
public class TupleImplPattern implements Pattern {

    private final int degree;
    private final boolean nullable;

    public TupleImplPattern(int degree, boolean nullable) {
        this.degree = requireNonNegative(degree);
        this.nullable = nullable;
    }

    @Override
    public String getClassName() {
        return nullable
            ? tupleOfNullablesImplementationSimpleName(degree)
            : tupleImplementationSimpleName(degree);
    }

    @Override
    public String getFullClassName() {
        return nullable
            ? tupleOfNullablesImplementationName(degree)
            : tupleImplementationName(degree);
    }

    @Override
    public boolean isTestClass() {
        return false;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        if (nullable) {
            file.add(Import.of(TupleOfNullables.class));
            file.add(Import.of(Optional.class));
        } else {
            file.add(Import.of(Tuple.class));
        }
        file.add(Import.of(SimpleType.create(tupleName(degree))));

        final Javadoc javadoc = Javadoc.of(
            "An implementation class of a {@link " + (nullable ? tupleOfNullablesSimpleName(degree) : tupleSimpleName(degree)) + " }")
            .add(AUTHOR.setValue("Per Minborg"));

        IntStream.range(0, degree).forEachOrdered(parameter -> {
            javadoc.add(PARAM.setValue("<" + genericTypeName(parameter) + ">").setText("type of element " + parameter));
        });

        final Class clazz = Class.of(getClassName())
            .public_().final_()
            .set(javadoc)
            .setSupertype(nullable ? AbstractTupleOfNullables.class : AbstractTuple.class)
            .add(nullable ? tupleOfNullablesGenericType(degree) : tupleGenericType(degree));

        IntStream.range(0, degree).forEachOrdered(parameter -> {
            clazz.add(Generic.of(genericTypeName(parameter)));
        });

        final Constructor constructor = Constructor.of();
        if (degree == 0) {
            clazz.add(
                Field.of(
                    "EMPTY_TUPLE",
                    (nullable
                        ? tupleOfNullablesGenericType(0)
                        : tupleGenericType(0)))
                    .set(new ReferenceValueImpl("new "
                        + (nullable
                            ? tupleOfNullablesImplementationSimpleName(degree)
                            : tupleImplementationSimpleName(degree))
                        + "()"))
                    .public_().static_().final_()
            );
            constructor.private_();
        } else {
            constructor.public_();
        }
        final Javadoc javaDocConstructor = Javadoc.of("Constructs a {@link " + (nullable ? "TupleOfNullables" : "Tuple") + " } of type {@link " + (nullable ? tupleOfNullablesSimpleName(degree) : tupleSimpleName(degree)) + " }.");
        IntStream.range(0, degree).forEachOrdered(parameter -> {
            javaDocConstructor.add(PARAM.setValue(elementName(parameter)).setText("element " + parameter));
            final Type parameterType = SimpleType.create(genericTypeName(parameter));
            constructor.add(Field.of(elementName(parameter), parameterType));
        });
        constructor.set(javaDocConstructor);
        constructor.add(
            "super("
            + Stream.concat(
                Stream.of(getClassName() + ".class"),
                IntStream.range(0, degree).mapToObj(TupleUtil::elementName)
            ).collect(joining(", "))
            + ");"
        );

        clazz.add(constructor);

        IntStream.range(0, degree)
            .mapToObj(this::getterMethod)
            .forEachOrdered(clazz::add);

        return clazz;
    }

    private Method getterMethod(int index) {
        final Type genericType = SimpleType.create(genericTypeName(index));
        return Method.of("get" + index, nullable ? optional(genericType) : genericType)
            .public_()
            .add(SUPPRESS_WARNINGS_UNCHECKED)
            .add(OVERRIDE)
            .add("return "
                + (nullable
                    ? ("Optional.ofNullable((" + genericTypeName(index) + ")")
                    : ("((" + genericTypeName(index)) + ")")
                + " values[" + index + "]);");
    }

}
