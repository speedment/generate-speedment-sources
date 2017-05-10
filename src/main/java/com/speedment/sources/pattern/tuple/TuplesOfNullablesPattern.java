package com.speedment.sources.pattern.tuple;

import static com.speedment.common.codegen.constant.DefaultJavadocTag.AUTHOR;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.Class;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Constructor;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.tuple.Tuple;
import com.speedment.common.tuple.TupleOfNullables;
import com.speedment.sources.pattern.AbstractSiblingPattern;
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

        return clazz;
    }

}
