package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.DefaultJavadocTag;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.*;
import com.speedment.runtime.field.method.ReferenceSetter;
import com.speedment.runtime.field.method.Setter;

import java.lang.Class;
import java.util.Objects;

import static com.speedment.common.codegen.constant.DefaultAnnotationUsage.OVERRIDE;

/**
 *
 * @author Emil Forslund
 */
public final class SetterPattern extends AbstractSiblingPattern {
    
    public SetterPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }
    
    @Override
    public String getClassName() {
        return ucPrimitive() + "Setter";
    }

    @Override
    protected Class<?> getSiblingClass() {
        return ReferenceSetter.class;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        final Interface intf = Interface.of(getClassName())
            
            ////////////////////////////////////////////////////////////////////
            /*                         Documentation                          */
            ////////////////////////////////////////////////////////////////////
            .set(Javadoc.of(
                formatJavadoc(
                    "A short-cut functional reference to the {@code setXXX(value)} method for a " +
                    "particular field in an entity. The referenced method should return a " +
                    "reference to itself." +
                    "\n<p>\n" +
                    "A {@code %1$sSetter<ENTITY>} has the following signature:\n" +
                    "{@code\n" +
                    "    interface ENTITY {\n" +
                    "        void setXXX(%2$s value);\n" +
                    "    }\n" +
                    "}"
                ))
                .add(DefaultJavadocTag.PARAM.setValue("<ENTITY>").setText("the entity"))
                .add(DefaultJavadocTag.AUTHOR.setValue("Emil Forslund"))
                .add(DefaultJavadocTag.SINCE.setValue("3.0.0"))
            )
            
            ////////////////////////////////////////////////////////////////////
            /*                       Class Declaration                        */
            ////////////////////////////////////////////////////////////////////
            .add(generatedAnnotation())
            .add(AnnotationUsage.of(FunctionalInterface.class))
            .public_()
            .add(Generic.of("ENTITY"))
            .add(SimpleParameterizedType.create(
                Setter.class, 
                SimpleType.create("ENTITY")
            ))
            
            ////////////////////////////////////////////////////////////////////
            /*                            Methods                             */
            ////////////////////////////////////////////////////////////////////
            .add(Method.of("setAs" + ucPrimitive(), void.class)
                .set(Javadoc.of(
                        "Sets the member represented by this setter in the specified " +
                        "instance to the specified value, returning a reference to " +
                        "the same instance as result."
                    )
                    .add(DefaultJavadocTag.PARAM.setValue("instance").setText("the instance to set it in"))
                    .add(DefaultJavadocTag.PARAM.setValue("value").setText("the new value"))
                )
                .add(Field.of("instance", SimpleType.create("ENTITY")))
                .add(Field.of("value", primitiveType()))
            )
        
            .call(() -> file.add(Import.of(Objects.class).static_().setStaticMember("requireNonNull")))
            .add(Method.of("set", void.class).default_()
                .add(OVERRIDE)
                .add(Field.of("instance", SimpleType.create("ENTITY")))
                .add(Field.of("value", Object.class))
                .add(
                    "requireNonNull(value, \"Attempting to set primitive " + primitive() + " field to null.\");",
                    "@SuppressWarnings(\"unchecked\")",
                    "final " + wrapper() + " casted = (" + wrapper() + ") value;",
                    "setAs" + ucPrimitive() + "(instance, casted);"
                )
            );
        
        return intf;
    }
}