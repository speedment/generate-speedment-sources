package com.speedment.sources.pattern.tuple;

import static com.speedment.common.codegen.constant.DefaultAnnotationUsage.SUPPRESS_WARNINGS_UNCHECKED;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.AUTHOR;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.Class;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Constructor;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import static com.speedment.common.codegen.util.Formatting.indent;
import static com.speedment.common.codegen.util.Formatting.nl;
import com.speedment.common.tuple.Tuple;
import com.speedment.sources.Pattern;
import static com.speedment.sources.pattern.tuple.TupleUtil.BASE_PACKAGE;
import static com.speedment.sources.pattern.tuple.TupleUtil.MAX_DEGREE;
import static com.speedment.sources.pattern.tuple.TupleUtil.elementName;
import static com.speedment.sources.pattern.tuple.TupleUtil.genericTypeName;
import static com.speedment.sources.pattern.tuple.TupleUtil.tupleGenericNames;
import static com.speedment.sources.pattern.tuple.TupleUtil.tupleGenericType;
import java.lang.reflect.Type;
import static java.util.stream.Collectors.joining;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author Per Minborg
 */
public class TupleBuilderPattern implements Pattern {

    @Override
    public String getFullClassName() {
        return BASE_PACKAGE + "." + getClassName();
    }

    @Override
    public String getClassName() {
        return "TupleBuilder";
    }

    @Override
    public boolean isTestClass() {
        return false;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        file.add(Import.of(Tuple.class));

        IntStream.range(0, MAX_DEGREE)
            .mapToObj(TupleUtil::tupleName)
            .map(SimpleType::create)
            .map(Import::of)
            .forEachOrdered(file::add);

        final Class clazz = Class.of(getClassName())
            .public_()
            .set(Javadoc.of(
                " This class is a Builder that can be used to build type safe Tuple of degree up to " + (MAX_DEGREE - 1) + " that can hold non-null\n"
                + "values.").add(AUTHOR.setValue("Per Minborg"))
            );

        clazz.add(Field.of("current", Tuple.class).private_());
        clazz.add(Constructor.of().private_()
            .add("this.current = Tuples.of();")
        );
        clazz.add(
            Method.of("builder", buildType(0))
                .public_().static_()
                .add("return new " + getClassName() + "().new " + buildType(0) + "();")
        );

        IntStream.range(0, MAX_DEGREE).forEach(level -> {
            Class subClass = Class.of(buildName(level))
                .public_()
                .setSupertype(SimpleParameterizedType.create("BaseBuilder", tupleGenericType(level)));

            tupleGenericNames(level).map(SimpleType::create).map(Generic::of).forEachOrdered(subClass::add);

            final Type returnType = SimpleParameterizedType.create(buildName(level + 1),
                tupleGenericNames(level + 1)
                    .map(SimpleType::create)
                    .toArray(SimpleType[]::new)
            );

            if (level < MAX_DEGREE - 1) {
                subClass.add(
                    Method.of("add", returnType)
                        .public_()
                        .add(Generic.of(genericTypeName(level)))
                        .add(Field.of(elementName(level), SimpleType.create(genericTypeName(level))))
                        .add("current = Tuples.of("
                            + indent(
                                Stream.concat(
                                    IntStream.range(0, level).mapToObj(i -> "current.get(" + i + ")"),
                                    Stream.of(elementName(level))
                                ).collect(joining("," + nl(), nl(), ""))
                            )
                        )
                        .add(");")
                        .add("return new " + buildName(level + 1) + "<>();")
                );

            }
            clazz.add(subClass);
        });

        clazz.add(
            Class.of("BaseBuilder").private_().add(Generic.of(SimpleType.create("T")))
                .add(
                    Method.of("build", SimpleType.create("T")).public_()
                        .add(SUPPRESS_WARNINGS_UNCHECKED)
                        .add("return (T) current;")
                )
        );

        return clazz;
    }

    private String buildName(int index) {
        return "Builder" + index;
    }

    private Type buildType(int index) {
        return SimpleType.create(buildName(index));
    }

}
