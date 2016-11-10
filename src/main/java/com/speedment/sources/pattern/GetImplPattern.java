package com.speedment.sources.pattern;

import static com.speedment.common.codegen.constant.DefaultAnnotationUsage.OVERRIDE;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.AUTHOR;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.PARAM;
import static com.speedment.common.codegen.constant.DefaultJavadocTag.SINCE;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.Class;
import com.speedment.common.codegen.model.Constructor;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.runtime.field.internal.method.GetReferenceImpl;
import com.speedment.runtime.field.method.ReferenceGetter;
import com.speedment.runtime.field.trait.HasReferenceValue;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 *
 * @author Emil Forslund
 * @since  3.0.2
 */
public final class GetImplPattern extends AbstractSiblingPattern {

    public GetImplPattern(java.lang.Class<?> wrapper, java.lang.Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    protected java.lang.Class<?> getSiblingClass() {
        return GetReferenceImpl.class;
    }

    @Override
    public String getClassName() {
        return "Get" + ucPrimitive() + "Impl";
    }

    @Override
    public Class make(File file) {
        file.add(Import.of(Objects.class).static_().setStaticMember("requireNonNull"));
        
        final Type fieldType = SimpleParameterizedType.create(
            siblingOf(HasReferenceValue.class, "Has%1$sValue"), 
            SimpleType.create("ENTITY"),
            SimpleType.create("D")
        );
        
        final Type getterType = SimpleParameterizedType.create(
            siblingOf(ReferenceGetter.class, "%1$sGetter"), 
            SimpleType.create("ENTITY")
        );
        
        return Class.of(getClassName())
            .public_().final_()
            .set(Javadoc.of(
                "Default implementation of the {@link Get" + ucPrimitive() + 
                "}-interface.")
                .add(PARAM.setValue("<ENTITY>").setText("the entity type"))
                .add(PARAM.setValue("<D>").setText("the database type"))
                .add(AUTHOR.setValue("Emil Forslund"))
                .add(SINCE.setValue("3.0.2"))
            )
            .add(Generic.of("ENTITY"))
            .add(Generic.of("D"))
            .add(SimpleParameterizedType.create(
                siblingOf(ReferenceGetter.class, "Get%1$s"), 
                SimpleType.create("ENTITY"),
                SimpleType.create("D")
            ))
            .add(Field.of("field", fieldType).private_().final_())
            .add(Field.of("getter", getterType).private_().final_())
            
            .add(Constructor.of()
                .public_()
                .add(Field.of("field", fieldType))
                .add(Field.of("getter", getterType))
                .add(
                    "this.field  = requireNonNull(field);",
                    "this.getter = requireNonNull(getter);"
                )
            )
            
            .add(Method.of("getField", fieldType)
                .public_().add(OVERRIDE)
                .add("return field;")
            )
            
            .add(Method.of("applyAs" + ucPrimitive(), primitiveType())
                .public_().add(OVERRIDE)
                .add(Field.of("instance", SimpleType.create("ENTITY")))
                .add("return getter.applyAs" + ucPrimitive() + "(instance);")
            );
    }

}

//    /**
//     * Default implementation of the {@link GetLong}-interface.
//     * 
//     * @param <ENTITY> the entity type
//     * @param <D>      the database type
//     * 
//     * @author Emil Forslund
//     * @since  3.0.2
//     */
//    public final class GetLongImpl<ENTITY, D> implements GetLong<ENTITY, D> {
//
//        private final HasLongValue<ENTITY, D> field;
//        private final LongGetter<ENTITY> getter;
//
//        public GetLongImpl(HasLongValue<ENTITY, D> field, LongGetter<ENTITY> getter) {
//            this.field  = requireNonNull(field);
//            this.getter = requireNonNull(getter);
//        }
//
//        @Override
//        public HasLongValue<ENTITY, D> getField() {
//            return field;
//        }
//
//        @Override
//        public long applyAsLong(ENTITY instance) {
//            return getter.applyAsLong(instance);
//        }
//    }