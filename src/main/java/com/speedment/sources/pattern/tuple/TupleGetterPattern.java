package com.speedment.sources.pattern.tuple;

import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.AnnotationUsage;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Interface;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.common.tuple.Tuple;
import com.speedment.common.tuple.getter.TupleGetter;
import com.speedment.sources.Pattern;

import static com.speedment.common.codegen.constant.DefaultAnnotationUsage.OVERRIDE;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.AUTHOR;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.PARAM;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.SINCE;
import static com.speedment.sources.pattern.tuple.TupleUtil.genericTypeName;
import static com.speedment.sources.pattern.tuple.TupleUtil.pluralize;

/**
 * @author Emil Forslund
 * @since  1.2.0
 */
public class TupleGetterPattern implements Pattern {

    private final int degree;

    public TupleGetterPattern(int degree) {
        this.degree = degree;
    }

    @Override
    public String getClassName() {
        return "TupleGetter" + degree;
    }

    @Override
    public String getFullClassName() {
        return "com.speedment.common.tuple.getter." + getClassName();
    }

    @Override
    public boolean isTestClass() {
        return false;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        return Interface.of(getClassName()).public_()
            .add(AnnotationUsage.of(FunctionalInterface.class))
            .add(Generic.of("TUPLE").add(Tuple.class))
            .add(Generic.of(genericTypeName(degree)))
            .add(SimpleParameterizedType.create(TupleGetter.class,
                SimpleType.create("TUPLE"),
                SimpleType.create(genericTypeName(degree))
            ))
            .set(Javadoc.of(
                "Specialization of {@link TupleGetter} that always returns " +
                    "the " + pluralize(degree) + " element.")
                .add(PARAM.setValue("<TUPLE>")
                    .setText("the type of the {@code Tuple} to get a " +
                        "value from"))
                .add(PARAM.setValue("<" + genericTypeName(degree) + ">")
                    .setText("the type of the element to return"))
                .add(AUTHOR.setValue("Emil Forslund"))
                .add(SINCE.setValue("1.0.8"))
            )
            .add(Method.of("getOrdinal", int.class)
                .default_().add(OVERRIDE)
                .add("return " + degree + ";")
            );
    }
}
