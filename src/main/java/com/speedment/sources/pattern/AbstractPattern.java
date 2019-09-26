package com.speedment.sources.pattern;

import com.speedment.common.annotation.GeneratedCode;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.AnnotationUsage;
import com.speedment.common.codegen.model.Value;
import com.speedment.common.codegen.util.Formatting;
import com.speedment.sources.Pattern;
import java.lang.reflect.Type;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author Emil Forslund
 */
public abstract class AbstractPattern implements Pattern {
    
    private final Class<?> wrapper, primitive;

    protected AbstractPattern(Class<?> wrapper, Class<?> primitive) {
        this.wrapper   = requireNonNull(wrapper);
        this.primitive = requireNonNull(primitive);
    }

    @Override
    public boolean isTestClass() {
        return false;
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
    
    protected final String value(String s) {
        if (s.contains("MIN_VALUE") || s.contains("MAX_VALUE")) {
            return s;
        }
        if (primitive == char.class) {
            return "'" + s + "'";
        } else if (primitive == boolean.class) {
            return s;
        } else {
            if (primitive == byte.class) {
                return "(byte) " + s;
            } else if (primitive == short.class) {
                return "(short) " + s;
            } else if (primitive == int.class) {
                return s;
            } else if (primitive == long.class) {
                return s + "L";
            } else if (primitive == float.class) {
                return s + "f";
            } else if (primitive == double.class) {
                return s + "d";
            } else {
                return s;
            }
        }
    }
    
    protected final String veryLow() {
        if (primitive == char.class) {
            return " ";
        } else if (primitive == boolean.class) {
            return "false";
        } else if (primitive == byte.class) {
            return "Byte.MIN_VALUE";
        } else if (primitive == short.class) {
            return "Short.MIN_VALUE";
        } else if (primitive == int.class) {
            return "Integer.MIN_VALUE";
        } else if (primitive == long.class) {
            return "Long.MIN_VALUE";
        } else if (primitive == float.class) {
            return "-Float.MAX_VALUE";
        } else if (primitive == double.class) {
            return "-Double.MAX_VALUE";
        } else {
            throw new UnsupportedOperationException();
        }
    }
    
    protected final String veryHigh() {
        if (primitive == char.class) {
            return "}";
        } else if (primitive == boolean.class) {
            return "true";
        } else if (primitive == byte.class) {
            return "Byte.MAX_VALUE";
        } else if (primitive == short.class) {
            return "Short.MAX_VALUE";
        } else if (primitive == int.class) {
            return "Integer.MAX_VALUE";
        } else if (primitive == long.class) {
            return "Long.MAX_VALUE";
        } else if (primitive == float.class) {
            return "Float.MAX_VALUE";
        } else if (primitive == double.class) {
            return "Double.MAX_VALUE";
        } else {
            throw new UnsupportedOperationException();
        }
    }
    
    protected final Type wrapperType() {
        return wrapper;
    }
    
    protected final Type primitiveType() {
        return primitive;
    }

    protected final AnnotationUsage generatedAnnotation() {
        return AnnotationUsage.of(GeneratedCode.class).put("value", Value.ofText("Speedment"));
    }

    protected final AnnotationUsage generatedAnnotation(Class<?> generatorClass) {
        return AnnotationUsage.of(GeneratedCode.class).put("value", Value.ofText(generatorClass.getName()));
    }

    protected final AnnotationUsage functionalInterface() {
        return AnnotationUsage.of(FunctionalInterface.class);
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
        return String.format(text, wrapper(), primitive(), ucPrimitive())
            .replace(Formatting.tab(), "\t");
    }
}