package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.DefaultAnnotationUsage;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.sources.Pattern;
import com.speedment.common.codegen.internal.model.value.TextValue;
import com.speedment.common.codegen.internal.util.Formatting;
import com.speedment.common.codegen.model.AnnotationUsage;
import com.speedment.runtime.annotation.Api;
import java.lang.reflect.Type;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author Emil Forslund
 */
abstract class AbstractPattern implements Pattern {
    
    private final Class<?> wrapper, primitive;

    protected AbstractPattern(Class<?> wrapper, Class<?> primitive) {
        this.wrapper   = requireNonNull(wrapper);
        this.primitive = requireNonNull(primitive);
    }
    
    protected final String wrapper() {
        return wrapper.getSimpleName();
    }
    
    protected final String primitive() {
        return primitive.getSimpleName();
    }
    
    protected final String ucPrimitive() {
        return Formatting.ucfirst(primitive());
    }
    
    protected final Type wrapperType() {
        return wrapper;
    }
    
    protected final Type primitiveType() {
        return primitive;
    }
    
    protected final AnnotationUsage apiAnnotation() {
        return AnnotationUsage.of(Api.class).put("version", new TextValue("3.0"));
    }
    
    protected final AnnotationUsage generatedAnnotation() {
        return DefaultAnnotationUsage.GENERATED.put("value", new TextValue("Speedment"));
    }
    
    protected final Type siblingOf(Class<?> packageOf, String name) {
        return SimpleType.create(packageOf.getPackage().getName() + "." + String.format(name, ucPrimitive()));
    }
    
    protected final Type cousinOf(Class<?> cousinOf, String packageName, String className) {
        final String siblingPackage = cousinOf.getPackage().getName();
        final String parentPackage = siblingPackage.substring(0, siblingPackage.lastIndexOf("."));
        return SimpleType.create(parentPackage + "." + 
            String.format(packageName, ucPrimitive()) + "." + 
            String.format(className, ucPrimitive())
        );
    }
    
    protected final String formatJavadoc(String text) {
        return String.format(text, wrapper(), primitive())
            .replace(Formatting.tab(), "\t");
    }
}