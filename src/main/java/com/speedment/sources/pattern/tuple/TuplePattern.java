package com.speedment.sources.pattern.tuple;

import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.*;
import com.speedment.common.tuple.MutableTuple;
import com.speedment.common.tuple.Tuple;
import com.speedment.common.tuple.TupleOfNullables;
import com.speedment.common.tuple.getter.TupleGetter;
import com.speedment.sources.Pattern;

import java.lang.Class;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import static com.speedment.common.codegen.constant.DefaultAnnotationUsage.OVERRIDE;
import static com.speedment.common.codegen.constant.DefaultAnnotationUsage.SUPPRESS_WARNINGS_UNCHECKED;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.*;
import static com.speedment.common.codegen.util.Formatting.block;
import static com.speedment.common.codegen.util.Formatting.nl;
import static com.speedment.common.invariant.IntRangeUtil.requireNonNegative;
import static com.speedment.sources.pattern.tuple.TupleUtil.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author Per Minborg
 */
public final class TuplePattern implements Pattern {

    private final int degree;
    private final TupleType type;

    public TuplePattern(int degree, TupleType type) {
        this.degree = requireNonNegative(degree);
        this.type = requireNonNull(type);
    }

    @Override
    public String getFullClassName() {
        return type.eval(
            tupleName(degree),
            tupleOfNullablesName(degree),
            mutableTupleName(degree)
        );
    }

    @Override
    public String getClassName() {
        return type.eval(
            tupleSimpleName(degree),
            tupleOfNullablesSimpleName(degree),
            mutableTupleSimpleName(degree)
        );
    }

    @Override
    public boolean isTestClass() {
        return false;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        file.add(Import.of(Tuple.class));
        file.add(Import.of(MutableTuple.class));
        file.add(Import.of(TupleOfNullables.class));
        final Javadoc javadoc = type.eval(
            Javadoc.of(
                "This interface defines a generic {@link Tuple} of degree " + degree + " that can hold non-null "
                + "values. A Tuple is type safe, immutable and thread safe. For tuples that can hold "
                + "null elements see {@link TupleOfNullables}. For mutable tuples see {@link MutableTuple} " + nl()
                + nl()
                + "This {@link Tuple} has a degree of " + degree + nl()
                + "<p>" + nl()
                + "Generated by " + getClass().getName()
            ),
            Javadoc.of(
                "This interface defines a generic {@link TupleOfNullables} of degree" + degree + " that can hold null "
                + "values. A TupleOfNullable is type safe, immutable and thread safe. For pure non-null "
                + "value elements see {@link Tuple}. For mutable tuples see {@link MutableTuple}"
                + nl()
                + "This {@link TupleOfNullables} has a degree of " + degree + nl()
                + "<p>" + nl()
                + "Generated by " + getClass().getName()
            ),
            Javadoc.of(
                "This interface defines a generic {@link MutableTuple} of any order that can hold null "
                + "values. A MutableTuple is type safe but <em>NOT</em> thread safe. For pure non-null "
                + "value elements see {@link Tuple}"
                + nl()
                + "This {@link MutableTuple} has a degree of " + degree + nl()
                + "<p>" + nl()
                + "Generated by " + getClass().getName()
            )
        );

        javadoc
            .add(AUTHOR.setValue("Per Minborg"))
            .add(SEE.setValue("Tuple"))
            .add(SEE.setValue("TupleOfNullables"))
            .add(SEE.setValue("MutableTuple"));

        IntStream.range(0, degree).forEachOrdered(parameter -> {
            javadoc.add(PARAM.setValue("<" + genericTypeName(parameter) + ">").setText("type of element " + parameter));
        });

        final Interface iface = Interface.of(getClassName())
            .public_()
            .set(javadoc)
            .add(type.eval(Tuple.class, TupleOfNullables.class, MutableTuple.class));

        IntStream.range(0, degree).forEachOrdered(parameter -> {
            iface.add(Generic.of(genericTypeName(parameter)));
        });

        IntStream.range(0, degree)
            .mapToObj(this::getterMethod)
            .forEachOrdered(iface::add);

        if (isMutable()) {
            IntStream.range(0, degree)
                .mapToObj(this::setterMethod)
                .forEachOrdered(iface::add);
        }

        iface.add(
            Method.of("degree", int.class)
                .default_()
                .add(OVERRIDE)
                .add("return " + degree + ";")
        );

        iface.add(getterMethod());
        if (degree > 0) {
            file.add(Import.of(TupleGetter.class));
        }
        for (int i = 0; i < degree; i++) {
            {
                final Type genericType = SimpleType.create(genericTypeName(i));
                final Type getterType = SimpleParameterizedType.create(
                    siblingOf(TupleGetter.class, "TupleGetter" + i),
                    SimpleParameterizedType.create(
                        SimpleType.create(getFullClassName()),
                        IntStream.range(0, degree)
                            .mapToObj(TupleUtil::genericTypeName)
                            .map(SimpleType::create)
                            .toArray(Type[]::new)
                    ),
                    type.eval(
                        genericType,
                        SimpleParameterizedType.create(Optional.class, genericType),
                        SimpleParameterizedType.create(Optional.class, genericType))
                );

                final Javadoc doc = Javadoc.of(
                    "Returns a {@link TupleGetter getter} for the "
                    + pluralize(i) + " element in the {@code Tuple}."
                ).add(RETURN.setValue("the element at the " + pluralize(i) + " position"));

                IntStream.range(0, degree)
                    .mapToObj(j -> PARAM.setValue("<" + genericTypeName(j) + ">").setText("the " + pluralize(j) + " element type"))
                    .forEachOrdered(doc::add);

                final Method getterMethod = Method.of("getter" + i, getterType)
                    .set(doc)
                    .static_()
                    .add("return "+getClassName()+ "::get" + i + ";");

                IntStream.range(0, degree)
                    .mapToObj(TupleUtil::genericTypeName)
                    .map(Generic::of)
                    .forEachOrdered(getterMethod::add);

                iface.add(getterMethod);

                if (type != TupleType.IMMUTABLE) {
                    // getOrNull
                    iface.add(getterOrNullMethod(i));

                    // getterOrNull
                    final Type getterOrNullType = SimpleParameterizedType.create(
                        siblingOf(TupleGetter.class, "TupleGetter" + i),
                        SimpleParameterizedType.create(
                            SimpleType.create(getFullClassName()),
                            IntStream.range(0, degree)
                                .mapToObj(TupleUtil::genericTypeName)
                                .map(SimpleType::create)
                                .toArray(Type[]::new)
                        ),
                            genericType

                    );

                    final Method getterOrNullMethod = Method.of("getterOrNull" + i, getterOrNullType)
                        .set(doc)
                        .static_()
                        .add("return "+getClassName()+ "::getOrNull" + i + ";");

                    IntStream.range(0, degree)
                        .mapToObj(TupleUtil::genericTypeName)
                        .map(Generic::of)
                        .forEachOrdered(getterOrNullMethod::add);

                    iface.add(getterOrNullMethod);

                }

            }
            
            if (isMutable()) {
                final Type setterType = SimpleParameterizedType.create(
                    BiConsumer.class,
                    SimpleParameterizedType.create(
                        getClassName(),
                        IntStream.range(0, degree)
                            .mapToObj(TupleUtil::genericTypeName)
                            .map(SimpleType::create)
                            .toArray(Type[]::new)
                    ),
                    SimpleType.create(genericTypeName(i))
                );

                final Javadoc doc = Javadoc.of(
                    "Returns a setter for the " + pluralize(i) + " element in the {@code MutableTuple}."
                ).add(RETURN.setValue("the element at the " + pluralize(i) + " position"));

                IntStream.range(0, degree)
                    .mapToObj(j -> PARAM.setValue("<" + genericTypeName(j) + ">").setText("the " + pluralize(j) + " element type"))
                    .forEachOrdered(doc::add);

                final Method getterMethod = Method.of("setter" + i, setterType)
                    .set(doc)
                    .static_()
                    .add("return MutableTuple" + degree + "::set" + i + ";");

                IntStream.range(0, degree)
                    .mapToObj(TupleUtil::genericTypeName)
                    .map(Generic::of)
                    .forEachOrdered(getterMethod::add);

                iface.add(getterMethod);
            }
            
        }

        return iface;
    }

    private Method getterMethod(int index) {
        final String returnText = "the " + pluralize(index) + " element from this tuple.";
        final Javadoc doc = Javadoc.of("Returns " + returnText)
            .add(RETURN.setValue(returnText));

        return Method.of(
            "get" + index,
            type.eval(
                SimpleType.create(genericTypeName(index)),
                SimpleParameterizedType.create(Optional.class, SimpleType.create(genericTypeName(index))),
                SimpleParameterizedType.create(Optional.class, SimpleType.create(genericTypeName(index)))
            )
        ).set(doc);
    }

    private Method getterOrNullMethod(int index) {
        final String returnText = "the " + pluralize(index) + " element from this tuple or {@code null} if no such element is present.";
        final Javadoc doc = Javadoc.of("Returns " + returnText)
            .add(RETURN.setValue(returnText));

        return Method.of(
            "getOrNull" + index,
            SimpleType.create(genericTypeName(index))
        ).set(doc)
            .default_()
            .add("return get"+index+"().orElse(null);");
    }

    private Method setterMethod(int index) {
        final Javadoc doc = Javadoc.of("Sets the " + pluralize(index) + " element in this tuple.")
            .add(PARAM.setValue("t" + index).setText("the new value for the " + pluralize(index) + " element"));
        return Method.of("set" + index, void.class)
            .add(
                Field.of("t" + index, SimpleType.create(genericTypeName(index)))
            )
            .set(doc);
    }

    private Method getterMethod() {
        Method method = Method.of(
            "get",
            type.eval(
                Object.class,
                SimpleParameterizedType.create(Optional.class, Object.class),
                SimpleParameterizedType.create(Optional.class, Object.class)
            )
        )
            .default_()
            .add(Field.of("index", int.class));
        if (degree == 0) {
            method.add(throwNewIndexOutOfBoundsException());
        } else if (degree == 1) {
            method.add("if (index == 0) "
                +block("return "+type.eval("", "(Optional<Object>)", "(Optional<Object>)") + "get" + 0 + "();")
                + " else "
                + block(throwNewIndexOutOfBoundsException())
            );
        } else {
            method.add("switch (index) "
                        + block(
                    IntStream.range(0, degree)
                        .mapToObj(i -> "case " + i + " : return " + type.eval("", "(Optional<Object>)", "(Optional<Object>)") + "get" + i + "();")
                        .collect(joining(nl()))
                        + nl()
                        + "default : " + throwNewIndexOutOfBoundsException()
                    )
                );
        }
        if (type != TupleType.IMMUTABLE) {
            method.add(SUPPRESS_WARNINGS_UNCHECKED);
        }
        return method;
    }

    private String throwNewIndexOutOfBoundsException() {
        return "throw new IndexOutOfBoundsException(String.format(\"Index %d is outside bounds of tuple of degree %s\", index, degree()));";
    }

    private Type siblingOf(Class<?> packageOf, String name) {
        return SimpleType.create(packageOf.getPackage().getName() + "." + name);
    }
    
    private boolean isMutable() {
        return TupleType.MUTABLE.equals(type);
    }
    
}
