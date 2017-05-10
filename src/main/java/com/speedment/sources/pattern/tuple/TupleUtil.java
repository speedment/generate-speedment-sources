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

    public static Type tupleMapperGenericType(int degree) {
        return SimpleParameterizedType.create(BASE_PACKAGE + ".TupleMapper", SimpleType.create("T"), tupleGenericType(degree));
    }

    public static Type tupleOfNullablesMapperGenericType(int degree) {
        return SimpleParameterizedType.create(BASE_PACKAGE + ".TupleMapper", SimpleType.create("T"), tupleOfNullablesGenericType(degree));
    }

    public static String tupleName(int degree) {
        return BASE_PACKAGE + ".nonnullable." + tupleSimpleName(degree);
    }

    public static String tupleOfNullablesName(int degree) {
        return BASE_PACKAGE + ".nullable." + tupleOfNullablesSimpleName(degree);
    }

    public static String tupleSimpleName(int degree) {
        return "Tuple" + degree;
    }

    public static String tupleOfNullablesSimpleName(int degree) {
        return "Tuple" + degree + OF_NULLABLES;
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

    public static Method ofMethod(int degree, boolean nullable) {
        final Type type;
        final Method method;

        if (nullable) {
            type = tupleOfNullablesGenericType(degree);
            method = Method.of("ofNullables", type);
        } else {
            type = tupleGenericType(degree);
            method = Method.of("of", type);
        }
        method.public_().static_();

        final Javadoc javadoc = Javadoc.of("Creates and returns a {@link "
            + (nullable ? tupleOfNullablesSimpleName(degree) : tupleSimpleName(degree))
            + "} with the given parameters.");

        IntStream.range(0, degree).forEachOrdered(parameter -> {
            javadoc.add(PARAM.setValue("<" + genericTypeName(parameter) + ">").setText("type of element " + parameter));
        });
        IntStream.range(0, degree).forEachOrdered(parameter -> {
            javadoc.add(PARAM.setValue(elementName(parameter)).setText("element " + parameter));
        });
        javadoc.add(RETURN.setValue("a {@link " + (nullable ? tupleOfNullablesSimpleName(degree) : tupleSimpleName(degree)) + "} with the given parameters."));
        javadoc.add(SEE.setValue(tupleSimpleName(degree)));
        javadoc.add(SEE.setValue("Tuple"));
        method.set(javadoc);

        if (degree == 0) {
            if (nullable) {
                method.add("return Tuple0OfNullablesImpl.EMPTY_TUPLE;");
            } else {
                method.add("return Tuple0Impl.EMPTY_TUPLE;");
            }
        } else {
            for (int parameter = 0; parameter < degree; parameter++) {
                final Type parameterType = SimpleType.create(genericTypeName(parameter));
                method.add(Generic.of(parameterType));
                method.add(Field.of(elementName(parameter), parameterType));
            }
            method.add("return new "
                + (nullable ? tupleOfNullablesImplementationSimpleName(degree) : tupleImplementationSimpleName(degree))
                + "<>("
                + IntStream.range(0, degree).mapToObj(TupleUtil::elementName).collect(joining(", "))
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

    private TupleUtil() {
    }
}
