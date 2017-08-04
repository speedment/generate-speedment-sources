package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.DefaultJavadocTag;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.*;
import com.speedment.common.function.ToBooleanFunction;
import com.speedment.runtime.field.method.Getter;
import com.speedment.runtime.field.method.ReferenceGetter;

import java.lang.Class;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import static com.speedment.common.codegen.constant.DefaultAnnotationUsage.OVERRIDE;
import static com.speedment.common.codegen.constant.DefaultType.function;

/**
 *
 * @author Emil Forslund
 */
public final class GetterPattern extends AbstractSiblingPattern {
    
    public GetterPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }
    
    @Override
    public String getClassName() {
        return ucPrimitive() + "Getter";
    }

    @Override
    protected Class<?> getSiblingClass() {
        return ReferenceGetter.class;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        final Method getter;
        final Interface intf = Interface.of(getClassName())
            
            ////////////////////////////////////////////////////////////////////
            /*                         Documentation                          */
            ////////////////////////////////////////////////////////////////////
            .set(Javadoc.of(
                formatJavadoc(
                    "A short-cut functional reference to the {@code getXXX(value)} method for a " +
                    "particular field in an entity." +
                    "\n<p>\n" +
                    "A {@code %1$sGetter<ENTITY>} has the following signature:\n" +
                    "{@code\n" +
                    "    interface ENTITY {\n" +
                    "        %2$s getXXX();\n" +
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
                Getter.class, SimpleType.create("ENTITY")
            ))
            
            ////////////////////////////////////////////////////////////////////
            /*                            Methods                             */
            ////////////////////////////////////////////////////////////////////
            .add(getter = Method.of("applyAs" + ucPrimitive(), primitiveType())
                .set(Javadoc.of(
                        "Returns the member represented by this getter in the specified " +
                        "instance."
                    )
                    .add(DefaultJavadocTag.PARAM.setValue("instance").setText("the instance to get from"))
                    .add(DefaultJavadocTag.RETURN.setValue("the value"))
                )
                .add(Field.of("instance", SimpleType.create("ENTITY")))
            )
            .add(Method.of("apply", wrapperType()).default_()
                .add(OVERRIDE)
                .add(Field.of("instance", SimpleType.create("ENTITY")))
                .add("return applyAs" + ucPrimitive() + "(instance);")
            )
            .add(Method.of("asFunction", function(SimpleType.create("ENTITY"), wrapperType()))
                .default_()
                .add(OVERRIDE)
                .add("return this::apply;")
            );
        
        if (primitiveType() == int.class) {
            intf.add(SimpleParameterizedType.create(ToIntFunction.class, SimpleType.create("ENTITY")));
            getter.add(OVERRIDE);
        } else if (primitiveType() == boolean.class) {
            intf.add(SimpleParameterizedType.create(ToBooleanFunction.class, SimpleType.create("ENTITY")));
            getter.add(OVERRIDE);
        } else if (primitiveType() == long.class) {
            intf.add(SimpleParameterizedType.create(ToLongFunction.class, SimpleType.create("ENTITY")));
            getter.add(OVERRIDE);
        } else if (primitiveType() == double.class) {
            intf.add(SimpleParameterizedType.create(ToDoubleFunction.class, SimpleType.create("ENTITY")));
            getter.add(OVERRIDE);
        }
        
        return intf;
    }
}