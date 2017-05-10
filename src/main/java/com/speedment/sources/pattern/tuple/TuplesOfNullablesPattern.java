package com.speedment.sources.pattern.tuple;

import static com.speedment.common.codegen.constant.DefaultJavadocTag.AUTHOR;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.PARAM;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.RETURN;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.SEE;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.Class;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Constructor;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.common.tuple.Tuple;
import com.speedment.common.tuple.TupleOfNullables;
import com.speedment.sources.pattern.AbstractSiblingPattern;
import static com.speedment.sources.pattern.tuple.TupleUtil.BASE_PACKAGE;
import static com.speedment.sources.pattern.tuple.TupleUtil.MAX_DEGREE;
import static com.speedment.sources.pattern.tuple.TupleUtil.ofMethod;
import static com.speedment.sources.pattern.tuple.TupleUtil.toTupleMethod;
import static com.speedment.sources.pattern.tuple.TupleUtil.tupleOfNullablesMapperImplementationName;
import java.util.stream.IntStream;

/**
 *
 * @author Per Minborg
 */
public class TuplesOfNullablesPattern extends AbstractSiblingPattern {

    public TuplesOfNullablesPattern() {
        super(Integer.class, int.class);
    }

    @Override
    protected java.lang.Class<?> getSiblingClass() {
        return TupleOfNullables.class;
    }

    @Override
    public String getClassName() {
        return "TuplesOfNullables";
    }

    @Override
    public ClassOrInterface<?> make(File file) {

        IntStream.range(0, MAX_DEGREE)
            .mapToObj(TupleUtil::tupleOfNullablesImplementationName)
            .map(SimpleType::create)
            .map(Import::of)
            .forEach(file::add);

        file.add(Import.of(Tuple.class));

        final Class clazz = Class.of(getClassName())
            .public_().final_()
            .set(Javadoc.of(
                "A collection of static methods to produce different types of {@link TupleOfNullables } objects")
                .add(AUTHOR.setValue("Per Minborg"))
            );

        for (int degree = 0; degree < MAX_DEGREE; degree++) {
            clazz.add(ofMethod(degree, true));
            clazz.add(toTupleMethod(degree, true));
            file.add(Import.of(SimpleType.create(tupleOfNullablesMapperImplementationName(degree))));
        }

        file.add(Import.of(UnsupportedOperationException.class));
        clazz.add(
            Constructor.of().private_()
                .add("throw new " + UnsupportedOperationException.class.getSimpleName() + "();")
        );

        file.add(Import.of(SimpleType.create(BASE_PACKAGE + ".internal.TupleInfiniteDegreeOfNullablesImpl")));
        clazz.add(
            Method.of("ofNullables", TupleOfNullables.class)
                .public_().static_()
                .set(
                    Javadoc.of("Creates and returns a {@link TupleOfNullables} with the given parameters")
                        .add(PARAM.setValue("elements").setText("array of elements to use for the TupleOfNullables"))
                        .add(RETURN.setText("a {@link TupleOfNullables} with the given parameters"))
                        .add(SEE.setText("TupleOfNullables"))
                )
                .add(Field.of("elements", Object[].class))
                .add("return new TupleInfiniteDegreeOfNullablesImpl(elements);")
        );

        return clazz;
    }

}
