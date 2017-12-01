package com.speedment.sources.pattern.tuple;

import com.speedment.common.codegen.constant.DefaultType;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.internal.model.value.ReferenceValueImpl;
import com.speedment.common.codegen.model.Class;
import com.speedment.common.codegen.model.*;
import com.speedment.common.tuple.Tuples;
import com.speedment.sources.Pattern;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.stream.IntStream;

import static com.speedment.common.codegen.constant.DefaultAnnotationUsage.OVERRIDE;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.AUTHOR;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.PARAM;
import static com.speedment.common.codegen.constant.DefaultType.WILDCARD;
import static com.speedment.common.codegen.constant.DefaultType.function;
import static com.speedment.common.codegen.util.Formatting.*;
import static com.speedment.common.invariant.IntRangeUtil.requireNonNegative;
import static com.speedment.sources.pattern.tuple.TupleUtil.*;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author Per Minborg
 */
public class TupleMapperImplPattern implements Pattern {

    private final int degree;
    private final boolean nullable;

    public TupleMapperImplPattern(int degree, boolean nullable) {
        this.degree = requireNonNegative(degree);
        this.nullable = nullable;
    }

    @Override
    public String getClassName() {
        return nullable
            ? tupleOfNullablesMapperImplementationSimpleName(degree)
            : tupleMapperImplementationSimpleName(degree);
    }

    @Override
    public String getFullClassName() {
        return nullable
            ? tupleOfNullablesMapperImplementationName(degree)
            : tupleMapperImplementationName(degree);
    }

    @Override
    public boolean isTestClass() {
        return false;
    }

    @Override
    public ClassOrInterface<?> make(File file) {

        final Type tupleMapperType = nullable ? tupleOfNullablesMapperGenericType(degree) : tupleMapperGenericType(degree);

        file.add(Import.of(tupleMapperType));
        file.add(Import.of(SimpleType.create(tupleName(degree))));
        if (nullable) {
            file.add(Import.of(SimpleType.create(BASE_PACKAGE + ".TuplesOfNullables")));
        } else {
            file.add(Import.of(Tuples.class));
        }

        if (degree > 0) {
            file.add(Import.of(Objects.class).setStaticMember("requireNonNull").static_());
        }
        file.add(Import.of(IndexOutOfBoundsException.class));

        final Javadoc javadoc = Javadoc.of(
            "An implementation class of a {@link TupleMapper } of degree " + degree)
            .add(AUTHOR.setValue("Per Minborg"));

        javadoc.add(PARAM.setValue("<T>").setText("Type of the original object for the mapper to use when creating a {@code Tuple }"));
        IntStream.range(0, degree).forEachOrdered(parameter -> {
            javadoc.add(PARAM.setValue("<" + genericTypeName(parameter) + ">").setText("type of element " + parameter));
        });

        final Class clazz = Class.of(getClassName())
            .public_().final_()
            .set(javadoc)
            .add(tupleMapperType);

        clazz.add(Generic.of("T"));
        IntStream.range(0, degree).forEachOrdered(parameter -> {
            clazz.add(Generic.of(genericTypeName(parameter)));
        });

        IntStream.range(0, degree).forEachOrdered(parameter -> {
            clazz.add(Field.of(mapperName(parameter), functionType(parameter)).private_().final_());
        });

        final Constructor constructor = Constructor.of();
        if (degree == 0) {
            clazz.add(
                Field.of("EMPTY_MAPPER", SimpleParameterizedType.create(getClassName(), WILDCARD)).set(new ReferenceValueImpl("new " + getClassName() + "<>()"))
                    .public_().static_().final_()
            );
            constructor.private_();
        } else {
            constructor.public_();
        }
        final Javadoc javaDocConstructor = Javadoc.of("Constructs a {@link TupleMapper } that can create {@link " + tupleSimpleName(degree) + " }.");
        IntStream.range(0, degree).forEachOrdered(parameter -> {
            javaDocConstructor.add(PARAM.setValue(mapperName(parameter)).setText("mapper to apply for element " + parameter));
            constructor.add(Field.of(mapperName(parameter), functionType(parameter)));
            constructor.add("this." + mapperName(parameter) + "\t=\trequireNonNull(" + mapperName(parameter) + ");");
        });
        constructor.set(javaDocConstructor);
        clazz.add(constructor);
        clazz.add(applyMethod());
        clazz.add(degreeMethod());
        clazz.add(getMethod());

        IntStream.range(0, degree)
            .mapToObj(this::getterMethod)
            .forEachOrdered(clazz::add);

        return clazz;
    }

    private Method degreeMethod() {
        return Method.of("degree", int.class)
            .public_()
            .add(OVERRIDE)
            .add("return " + degree + ";");
    }

    private Method applyMethod() {
        final Method method = Method.of("apply", nullable ? tupleOfNullablesGenericType(degree) : tupleGenericType(degree))
            .public_()
            .add(OVERRIDE)
            .add(Field.of("t", SimpleType.create("T")))
            .add("return " + (nullable ? "TuplesOfNullables.ofNullables" : "Tuples.of") + "(" + nl()
                + indent(
                    IntStream.range(0, degree)
                        .mapToObj(i -> mapperName(i) + ".apply(t)")
                        .collect(joining("," + nl()))
                ) + nl() + ");");

        return method;
    }

    private Method getMethod() {
        final Method method = Method.of("get", function(SimpleType.create("T"), DefaultType.WILDCARD))
            .add(Field.of("index", int.class))
            .public_()
            .add(OVERRIDE);

        method.add("switch(index)"
            + block(
                IntStream.range(0, degree).mapToObj(i -> "case " + i + "\t: return\tget" + i + "();").collect(joining(nl()))
                + nl() + "default : throw new " + IndexOutOfBoundsException.class.getSimpleName() + "();"
            )
        );

        return method;
    }

    private Type functionType(int index) {
        return function(SimpleType.create("T"), SimpleType.create(genericTypeName(index)));
    }

    private Method getterMethod(int index) {
        return Method.of("get" + index, functionType(index))
            .public_()
            .add("return " + mapperName(index) + ";");
    }

}
