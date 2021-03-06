package com.speedment.sources.pattern.tuple;

import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.Class;
import com.speedment.common.codegen.model.*;
import com.speedment.common.tuple.MutableTuple;
import com.speedment.sources.pattern.AbstractSiblingPattern;

import java.util.NoSuchElementException;

import static com.speedment.common.codegen.constant.DefaultJavadocTag.AUTHOR;
import static com.speedment.common.codegen.util.Formatting.packageName;
import static com.speedment.sources.pattern.tuple.TupleUtil.MAX_DEGREE;
import static com.speedment.sources.pattern.tuple.TupleUtil.ofMethod;

/**
 *
 * @author Per Minborg
 */
public class MutableTuplesPattern extends AbstractSiblingPattern {

    public MutableTuplesPattern() {
        super(Integer.class, int.class);
    }

    @Override
    protected java.lang.Class<?> getSiblingClass() {
        return MutableTuple.class;
    }

    @Override
    public String getClassName() {
        return getSiblingClass().getSimpleName() + "s";
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        // file.add(Import.of(SimpleType.create(getSiblingClass().getName() + ".internal")).setStaticMember("*"));

        file.add(
            Import.of(
                SimpleType.create(packageName(TupleUtil.mutableTupleImplementationName(1)).orElseThrow(NoSuchElementException::new))
            ).setStaticMember("*")
        );

/*        IntStream.range(0, MAX_DEGREE)
            .mapToObj(TupleUtil::mutableTupleImplementationName)
            .map(SimpleType::create)
            .map(Import::of)
            .forEach(file::add);*/

/*        file.add(Import.of(MutableTuple.class));*/

        final Class clazz = Class.of(getClassName())
            .public_().final_()
            .set(Javadoc.of(
                "A collection of static methods to produce different types of {@link MutableTuple} objects\n"
                + "<p>\n"
                + "Generated by " + getClass().getName()
            )
                .add(AUTHOR.setValue("Per Minborg"))
            );

        for (int degree = 0; degree < MAX_DEGREE; degree++) {
            clazz.add(ofMethod(degree, TupleType.MUTABLE));
            clazz.add(ofMethod(degree, TupleType.MUTABLE, true));
            //file.add(Import.of(SimpleType.create(tupleMapperImplementationName(degree))));
        }

/*        file.add(Import.of(UnsupportedOperationException.class));*/
        clazz.add(
            Constructor.of().private_()
        );

//        IntStream.range(0, MAX_DEGREE)
//            .forEachOrdered(i -> {
//                Method m = Method.of("create"+i, type)
//            });
            

// It makes no sense to have an array method since there is no set(int index) method
//
//        file.add(Import.of(SimpleType.create(BASE_PACKAGE + ".internal.TupleInfiniteDegreeImpl")));
//        clazz.add(
//            Method.of("ofArray", MutableTuple.class)
//                .public_().static_()
//                .set(
//                    Javadoc.of("Creates and returns a {@link Tuple} with the given parameters")
//                        .add(PARAM.setValue("elements").setText("array of elements to use for the Tuple"))
//                        .add(RETURN.setText("a {@link Tuple} with the given parameters"))
//                        .add(SEE.setText("Tuple"))
//                )
//                .add(Field.of("elements", Object[].class))
//                .add("return new TupleInfiniteDegreeImpl(elements);")
//        );
        return clazz;
    }

}
