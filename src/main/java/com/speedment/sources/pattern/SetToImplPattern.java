package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.DefaultAnnotationUsage;
import com.speedment.common.codegen.constant.DefaultJavadocTag;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Constructor;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.runtime.field.method.SetToReference;
import com.speedment.runtime.field.trait.HasReferenceValue;
import com.speedment.runtime.field.internal.method.SetToReferenceImpl;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 *
 * @author Emil Forslund
 */
public final class SetToImplPattern extends AbstractSiblingPattern {

    public SetToImplPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getClassName() {
        return "SetTo" + ucPrimitive() + "Impl";
    }

    @Override
    protected Class<?> getSiblingClass() {
        return SetToReferenceImpl.class;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        file.add(Import.of(Objects.class).static_().setStaticMember("requireNonNull"));
        
        final Type fieldType = SimpleParameterizedType.create(
            siblingOf(HasReferenceValue.class, "Has%1$sValue"),
            SimpleType.create("ENTITY"),
            SimpleType.create("D")
        );
        
        return com.speedment.common.codegen.model.Class.of(getClassName())
            
            /******************************************************************/
            /*                         Documentation                          */
            /******************************************************************/
            .set(Javadoc.of(formatJavadoc(
                "A {@code set} operation that will apply a value {@link #getValue()} " +
                "to the field {@link #getField()} of any instance passed to it." +
                "\n<p>\n" +
                "This particular implementation is for values of type {@code %2$s}."
                ))
                .add(DefaultJavadocTag.PARAM.setValue("<ENTITY>").setText("entity type"))
                .add(DefaultJavadocTag.PARAM.setValue("<D>").setText("database type"))
                .add(DefaultJavadocTag.AUTHOR.setValue("Emil Forslund"))
                .add(DefaultJavadocTag.SINCE.setValue("3.0.0"))
            )
            
            /******************************************************************/
            /*                       Class Declaration                        */
            /******************************************************************/
            .public_().final_()
            .add(generatedAnnotation())
            .add(Generic.of("ENTITY"))
            .add(Generic.of("D"))
            .add(SimpleParameterizedType.create(
                siblingOf(SetToReference.class, "SetTo%1$s"),
                SimpleType.create("ENTITY"),
                SimpleType.create("D")
            ))
            
            /******************************************************************/
            /*                      Private Member Fields                     */
            /******************************************************************/
            .add(Field.of("field", fieldType).private_().final_())
            .add(Field.of("newValue", primitiveType()).private_().final_())
            
            /******************************************************************/
            /*                          Constructor                           */
            /******************************************************************/
            .add(Constructor.of().public_()
                .add(Field.of("field", fieldType))
                .add(Field.of("newValue", primitiveType()))
                .add("this.field    = requireNonNull(field);")
                .add("this.newValue = requireNonNull(newValue);")
            )
            
            /******************************************************************/
            /*                            Methods                             */
            /******************************************************************/
            .add(Method.of("getField", fieldType).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return field;")
            )
            
            .add(Method.of("getValue", primitiveType()).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return newValue;")
            )
            
            .add(Method.of("apply", SimpleType.create("ENTITY")).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add(Field.of("entity", SimpleType.create("ENTITY")))
                .add("return field.setter().setAs" + ucPrimitive() + "(entity, newValue);")
            )
        ;
    }
}