package com.speedment.sources.pattern;

import static com.speedment.common.codegen.constant.DefaultJavadocTag.AUTHOR;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.PARAM;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.RETURN;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.SINCE;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Interface;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.runtime.field.method.GetReference;
import com.speedment.runtime.field.method.ReferenceGetter;
import com.speedment.runtime.field.trait.HasReferenceValue;

/**
 *
 * @author Emil Forslund
 * @since  3.0.2
 */
public final class GetPattern extends AbstractSiblingPattern {

    public GetPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    protected java.lang.Class<?> getSiblingClass() {
        return GetReference.class;
    }

    @Override
    public String getClassName() {
        return "Get" + ucPrimitive();
    }

    @Override
    public Interface make(File file) {
        return Interface.of(getClassName())
            .public_()
            .set(Javadoc.of(
                "A more detailed {@link " + ucPrimitive() + "Getter} that " + 
                "also contains information about the field that created it.")
                .add(PARAM.setValue("<ENTITY>").setText("the entity type"))
                .add(PARAM.setValue("<D>").setText("the database type"))
                .add(AUTHOR.setValue("Emil Forslund"))
                .add(SINCE.setValue("3.0.2"))
            )
            .add(Generic.of("ENTITY"))
            .add(Generic.of("D"))
            .add(SimpleParameterizedType.create(
                siblingOf(ReferenceGetter.class, "%1$sGetter"), 
                SimpleType.create("ENTITY")
            ))
            .add(Method.of("getField", SimpleParameterizedType.create(
                    siblingOf(HasReferenceValue.class, "Has%1$sValue"), 
                    SimpleType.create("ENTITY"),
                    SimpleType.create("D")
                ))
                .set(Javadoc.of(
                    "Returns the field that created the " + 
                    "{@code get()}-operation.")
                    .add(RETURN.setValue("the field"))
                )
            );
    }

}

//    /**
//     * A more detailed {@link LongGetter} that also contains information about the
//     * field that created it.
//     * 
//     * @param <ENTITY> the entity type
//     * @param <D>      the database type
//     * 
//     * @author Emil Forslund
//     * @since  3.0.2
//     */
//    public interface FieldLongGetter<ENTITY, D> extends LongGetter<ENTITY> {
//
//        /**
//         * Returns the field that created the {@code get()}-operation.
//         * 
//         * @return  the field
//         */
//        HasLongValue<ENTITY, D> getField();
//
//    }