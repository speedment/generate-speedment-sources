package com.speedment.sources.pattern.tuple;

import static com.speedment.common.codegen.constant.DefaultAnnotationUsage.SUPPRESS_WARNINGS_UNCHECKED;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.PARAM;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.RETURN;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.SEE;
import static com.speedment.common.codegen.constant.DefaultType.function;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.common.tuple.Tuple;
import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.function.Supplier;
import static java.util.stream.Collectors.joining;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author Per Minborg
 */
public final class TupleUtil {

    public static final int MAX_DEGREE = 24;

    public static final String BASE_PACKAGE = Tuple.class.getPackage().getName();
    public static final String OF_NULLABLES = "OfNullables";
    public static final String MUTABLE = "Mutable";

    public static Stream<String> tupleGenericNames(int degree) {
        return IntStream.range(0, degree)
            .mapToObj(TupleUtil::genericTypeName);
    }

    public static Type tupleGenericType(int degree) {
        final SimpleType[] generics = tupleGenericNames(degree)
            .map(SimpleType::create)
            .toArray(SimpleType[]::new);
        return SimpleParameterizedType.create(tupleName(degree), generics);
    }

    public static Type tupleOfNullablesGenericType(int degree) {
        final SimpleType[] generics = tupleGenericNames(degree)
            .map(SimpleType::create)
            .toArray(SimpleType[]::new);
        return SimpleParameterizedType.create(tupleOfNullablesName(degree), generics);
    }

    public static Type mutableTupleGenericType(int degree) {
        final SimpleType[] generics = tupleGenericNames(degree)
            .map(SimpleType::create)
            .toArray(SimpleType[]::new);
        return SimpleParameterizedType.create(mutableTupleName(degree), generics);
    }

    public static Type tupleMapperGenericType(int degree) {
        return SimpleParameterizedType.create(BASE_PACKAGE + ".TupleMapper", SimpleType.create("T"), tupleGenericType(degree));
    }

    public static Type tupleOfNullablesMapperGenericType(int degree) {
        return SimpleParameterizedType.create(BASE_PACKAGE + ".TupleMapper", SimpleType.create("T"), tupleOfNullablesGenericType(degree));
    }

    public static String tupleName(int degree) {
        return BASE_PACKAGE + "." + tupleSimpleName(degree);
    }

    public static String tupleOfNullablesName(int degree) {
        return BASE_PACKAGE + ".nullable." + tupleOfNullablesSimpleName(degree);
    }

    public static String mutableTupleName(int degree) {
        return BASE_PACKAGE + ".mutable." + mutableTupleSimpleName(degree);
    }

    public static String tupleSimpleName(int degree) {
        return "Tuple" + degree;
    }

    public static String tupleOfNullablesSimpleName(int degree) {
        return "Tuple" + degree + OF_NULLABLES;
    }

    public static String mutableTupleSimpleName(int degree) {
        return MUTABLE + "Tuple" + degree;
    }

    public static String tupleImplementationName(int degree) {
        return BASE_PACKAGE + ".internal.nonnullable." + tupleImplementationSimpleName(degree);
    }

    public static String tupleImplementationSimpleName(int degree) {
        return "Tuple" + degree + "Impl";
    }

    public static String tupleOfNullablesImplementationName(int degree) {
        return BASE_PACKAGE + ".internal.nullable." + tupleOfNullablesImplementationSimpleName(degree);
    }

    public static String tupleOfNullablesImplementationSimpleName(int degree) {
        return "Tuple" + degree + OF_NULLABLES + "Impl";
    }

    public static String mutableTupleImplementationName(int degree) {
        return BASE_PACKAGE + ".internal.mutable." + mutableTupleImplementationSimpleName(degree);
    }

    public static String mutableTupleImplementationSimpleName(int degree) {
        return MUTABLE + "Tuple" + degree + "Impl";
    }

    public static String tupleMapperImplementationName(int degree) {
        return BASE_PACKAGE + ".internal.nonnullable.mapper." + tupleMapperImplementationSimpleName(degree);
    }

    public static String tupleOfNullablesMapperImplementationName(int degree) {
        return BASE_PACKAGE + ".internal.nullable.mapper." + tupleOfNullablesMapperImplementationSimpleName(degree);
    }

    public static String tupleMapperImplementationSimpleName(int degree) {
        return tupleSimpleName(degree) + "MapperImpl";
    }

    public static String tupleOfNullablesMapperImplementationSimpleName(int degree) {
        return tupleOfNullablesSimpleName(degree) + "MapperImpl";
    }

    public static String genericTypeName(int degree) {
        return "T" + degree;
    }

    public static String elementName(int degree) {
        return "e" + degree;
    }

    public static String mapperName(int degree) {
        return "m" + degree;
    }

    public static Method ofMethod(int degree, TupleType tupleType) {
        return ofMethod(degree, tupleType, false);
    }

    public static Method ofMethod(int degree, TupleType tupleType, boolean emptyConstructor) {
        final Type type;
        final Method method;

        final boolean isSupplier = TupleType.MUTABLE.equals(tupleType) && !emptyConstructor;

        switch (tupleType) {
            case IMMUTABLE: {
                type = tupleGenericType(degree);
                method = Method.of("of", type);
                break;
            }
            case IMMUTABLE_NULLABLE: {
                type = tupleOfNullablesGenericType(degree);
                method = Method.of("ofNullables", type);
                break;
            }
            case MUTABLE: {
                if (emptyConstructor) {
                    type = mutableTupleGenericType(degree);
                    method = Method.of("create" + degree, type);
                } else {
                    type = SimpleParameterizedType.create(Supplier.class, mutableTupleGenericType(degree));
                    method = Method.of("constructor", type);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException();
        }

        method.public_().static_();

        final String name = tupleType.eval(tupleSimpleName(degree), tupleOfNullablesSimpleName(degree), mutableTupleSimpleName(degree));
        final String returns = "a new {@link " + name + "} " + (isSupplier ? "constructor that creates an object " : " ") + tupleType.eval("with the given parameters.", "with the given parameters.", "that is empty.");
        final Javadoc javadoc = Javadoc.of("Creates and returns " + returns);
        IntStream.range(0, degree).forEachOrdered(parameter -> {
            javadoc.add(PARAM.setValue("<" + genericTypeName(parameter) + ">").setText("type of element " + parameter));
        });
        if (!emptyConstructor) {
            IntStream.range(0, degree).forEachOrdered(parameter -> {
                javadoc.add(PARAM.setValue(elementName(parameter)).setText("element " + parameter));
            });
        }
        javadoc.add(RETURN.setValue(returns));
        javadoc.add(SEE.setValue(tupleSimpleName(degree)));
        javadoc.add(SEE.setValue("Tuple"));
        method.set(javadoc);

        if (degree == 0) {
            tupleType.eval(
                () -> method.add("return Tuple0Impl.EMPTY_TUPLE;"),
                () -> method.add("return Tuple0OfNullablesImpl.EMPTY_TUPLE;"),
                isSupplier
                    ? () -> method.add("return () -> MutableTuple0Impl.EMPTY_TUPLE;")
                    : () -> method.add("return MutableTuple0Impl.EMPTY_TUPLE;")
            );
        } else {
            IntStream.range(0, degree).forEachOrdered(parameter -> {

                final Type parameterType = SimpleType.create(genericTypeName(parameter));
                method.add(Generic.of(parameterType));
                tupleType.eval(
                    () -> method.add(Field.of(elementName(parameter), parameterType)),
                    () -> method.add(Field.of(elementName(parameter), parameterType)),
                    () -> emptyConstructor ? "" : method.add(Field.of(elementName(parameter), SimpleParameterizedType.create(Class.class, parameterType)))
                );
                //method.add(Field.of(elementName(parameter), parameterType));
            });

            method.add("return " + (isSupplier ? "() -> " : "") + " new "
                + tupleType.eval(
                    tupleImplementationSimpleName(degree),
                    tupleOfNullablesImplementationSimpleName(degree),
                    mutableTupleImplementationSimpleName(degree)
                )
                + "<>("
                + tupleType.eval(
                    IntStream.range(0, degree).mapToObj(TupleUtil::elementName).collect(joining(", ")),
                    IntStream.range(0, degree).mapToObj(TupleUtil::elementName).collect(joining(", ")),
                    "")
                + ");"
            );
        }
        return method;
    }

    public static Method toTupleMethod(int degree, boolean nullable) {
        final Type type = SimpleParameterizedType.create(Function.class, SimpleType.create("T"), nullable ? tupleOfNullablesGenericType(degree) : tupleGenericType(degree));
        final Method method = Method.of(nullable ? "toTupleOfNullables" : "toTuple", type).public_().static_();
        final Javadoc javadoc = Javadoc.of("Creates and returns a Function that, when applied, creates a {@link " + (nullable ? tupleOfNullablesSimpleName(degree) : tupleSimpleName(degree)) + "} from an initial object of type T by applying the given mappers.");

        javadoc.add(PARAM.setValue("<T>").setText("type of the initial object to be used by the function to create a {@link " + tupleSimpleName(degree) + " }"));
        IntStream.range(0, degree).forEachOrdered(parameter -> {
            javadoc.add(PARAM.setValue("<" + genericTypeName(parameter) + ">").setText(" target type of " + mapperName(parameter)));
        });
        IntStream.range(0, degree).forEachOrdered(parameter -> {
            javadoc.add(PARAM.setValue(mapperName(parameter)).setText(" mapper to apply for " + tupleSimpleName(degree) + "'s element " + parameter));
        });
        javadoc.add(RETURN.setValue("a Function that, when applied, creates a {@link " + tupleSimpleName(degree) + "} from an initial object of type T by applying the given mappers."));
        javadoc.add(SEE.setValue(tupleSimpleName(degree)));
        javadoc.add(SEE.setValue("Tuple"));
        method.set(javadoc);

        method.add(Generic.of("T"));
        if (degree == 0) {
            method.add("return (Function<T, " + (nullable ? "Tuple0OfNullables" : "Tuple0") + ">) " + (nullable ? tupleOfNullablesMapperImplementationSimpleName(0) : tupleMapperImplementationSimpleName(0)) + ".EMPTY_MAPPER;");
            method.add(SUPPRESS_WARNINGS_UNCHECKED);
        } else {
            for (int parameter = 0; parameter < degree; parameter++) {
                final Type parameterType = SimpleType.create(genericTypeName(parameter));
                final Type functionType = function(SimpleType.create("T"), parameterType);
                method.add(Generic.of(parameterType));
                method.add(Field.of(mapperName(parameter), functionType));
            }
            method.add("return new "
                + (nullable ? tupleOfNullablesMapperImplementationSimpleName(degree) : tupleMapperImplementationSimpleName(degree))
                + "<>("
                + IntStream.range(0, degree).mapToObj(TupleUtil::mapperName).collect(joining(", "))
                + ");"
            );
        }
        return method;
    }

    static String pluralize(int degree) {
        switch (degree % 10) {
            case 0:
                return degree + "th";
            case 1:
                return degree + "st";
            case 2:
                return degree + "nd";
            case 3:
                return degree + "rd";
            default:
                return degree + "th";
        }
    }

    private TupleUtil() {
    }
}
