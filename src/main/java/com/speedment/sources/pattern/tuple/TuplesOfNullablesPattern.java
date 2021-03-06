package com.speedment.sources.pattern.tuple;

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

import java.util.stream.IntStream;

import static com.speedment.common.codegen.constant.DefaultJavadocTag.AUTHOR;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.PARAM;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.RETURN;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.SEE;
import static com.speedment.common.codegen.util.Formatting.block;
import static com.speedment.sources.pattern.tuple.TupleUtil.*;

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
                "A collection of static methods to produce different types of {@link TupleOfNullables} objects.\n"
                + "<p>\n"
                + "Generated by " + getClass().getName())
                .add(AUTHOR.setValue("Per Minborg"))
            );

        for (int degree = 0; degree < MAX_DEGREE; degree++) {
            clazz.add(ofMethod(degree, TupleType.IMMUTABLE_NULLABLE));
            clazz.add(toTupleMethod(degree, true));
            file.add(Import.of(SimpleType.create(tupleOfNullablesMapperImplementationName(degree))));
        }

        clazz.add(Constructor.of().private_());

        file.add(Import.of(SimpleType.create(BASE_PACKAGE + ".internal.TupleInfiniteDegreeOfNullablesImpl")));
        clazz.add(
            Method.of("ofNullablesArray", TupleOfNullables.class)
                .public_().static_()
                .set(
                    Javadoc.of("Creates and returns a {@link TupleOfNullables} with the given parameters")
                        .add(PARAM.setValue("el").setText("array of elements to use for the TupleOfNullables"))
                        .add(RETURN.setText("a {@link TupleOfNullables} with the given parameters"))
                        .add(SEE.setText("TupleOfNullables"))
                )
                .add(Field.of("el", Object[].class))
                .add("switch (el.length) " + block(
                    "case 0: return ofNullables();",
                    "case 1: return ofNullables(el[0]);",
                    "case 2: return ofNullables(el[0], el[1]);",
                    "case 3: return ofNullables(el[0], el[1], el[2]);",
                    "case 4: return ofNullables(el[0], el[1], el[2], el[3]);",
                    "case 5: return ofNullables(el[0], el[1], el[2], el[3], el[4]);",
                    "case 6: return ofNullables(el[0], el[1], el[2], el[3], el[4], el[5]);",
                    "case 7: return ofNullables(el[0], el[1], el[2], el[3], el[4], el[5], el[6]);",
                    "case 8: return ofNullables(el[0], el[1], el[2], el[3], el[4], el[5], el[6], el[7]);",
                    "case 9: return ofNullables(el[0], el[1], el[2], el[3], el[4], el[5], el[6], el[7], el[8]);",
                    "case 10: return ofNullables(el[0], el[1], el[2], el[3], el[4], el[5], el[6], el[7], el[8], el[9]);",
                    "case 11: return ofNullables(el[0], el[1], el[2], el[3], el[4], el[5], el[6], el[7], el[8], el[9], el[10]);",
                    "case 12: return ofNullables(el[0], el[1], el[2], el[3], el[4], el[5], el[6], el[7], el[8], el[9], el[10], el[11]);",
                    "case 13: return ofNullables(el[0], el[1], el[2], el[3], el[4], el[5], el[6], el[7], el[8], el[9], el[10], el[11], el[12]);",
                    "case 14: return ofNullables(el[0], el[1], el[2], el[3], el[4], el[5], el[6], el[7], el[8], el[9], el[10], el[11], el[12], el[13]);",
                    "case 15: return ofNullables(el[0], el[1], el[2], el[3], el[4], el[5], el[6], el[7], el[8], el[9], el[10], el[11], el[12], el[13], el[14]);",
                    "case 16: return ofNullables(el[0], el[1], el[2], el[3], el[4], el[5], el[6], el[7], el[8], el[9], el[10], el[11], el[12], el[13], el[14], el[15]);",
                    "case 17: return ofNullables(el[0], el[1], el[2], el[3], el[4], el[5], el[6], el[7], el[8], el[9], el[10], el[11], el[12], el[13], el[14], el[15], el[16]);",
                    "case 18: return ofNullables(el[0], el[1], el[2], el[3], el[4], el[5], el[6], el[7], el[8], el[9], el[10], el[11], el[12], el[13], el[14], el[15], el[16], el[17]);",
                    "case 19: return ofNullables(el[0], el[1], el[2], el[3], el[4], el[5], el[6], el[7], el[8], el[9], el[10], el[11], el[12], el[13], el[14], el[15], el[16], el[17], el[18]);",
                    "case 20: return ofNullables(el[0], el[1], el[2], el[3], el[4], el[5], el[6], el[7], el[8], el[9], el[10], el[11], el[12], el[13], el[14], el[15], el[16], el[17], el[18], el[19]);",
                    "case 21: return ofNullables(el[0], el[1], el[2], el[3], el[4], el[5], el[6], el[7], el[8], el[9], el[10], el[11], el[12], el[13], el[14], el[15], el[16], el[17], el[18], el[19], el[20]);",
                    "case 22: return ofNullables(el[0], el[1], el[2], el[3], el[4], el[5], el[6], el[7], el[8], el[9], el[10], el[11], el[12], el[13], el[14], el[15], el[16], el[17], el[18], el[19], el[20], el[21]);",
                    "case 23: return ofNullables(el[0], el[1], el[2], el[3], el[4], el[5], el[6], el[7], el[8], el[9], el[10], el[11], el[12], el[13], el[14], el[15], el[16], el[17], el[18], el[19], el[20], el[21], el[22]);",
                    "default: return new TupleInfiniteDegreeOfNullablesImpl(el);"
                ))
        );

        return clazz;
    }

}
