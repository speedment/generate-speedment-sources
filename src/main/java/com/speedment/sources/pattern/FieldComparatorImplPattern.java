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
import static com.speedment.common.codegen.util.Formatting.block;
import com.speedment.runtime.field.trait.HasReferenceValue;
import com.speedment.runtime.field.internal.comparator.ReferenceFieldComparator;
import com.speedment.runtime.field.internal.comparator.ReferenceFieldComparatorImpl;
import com.speedment.common.invariant.NullUtil;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Objects;

/**
 *
 * @author Emil Forslund
 */
public final class FieldComparatorImplPattern extends AbstractSiblingPattern {

    public FieldComparatorImplPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    protected Class<?> getSiblingClass() {
        return ReferenceFieldComparatorImpl.class;
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "FieldComparatorImpl";
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        file.add(Import.of(Objects.class).static_().setStaticMember("requireNonNull"));
        file.add(Import.of(NullUtil.class).static_().setStaticMember("requireNonNulls"));
        
        final Type fieldType = SimpleParameterizedType.create(
            siblingOf(HasReferenceValue.class, "Has%1$sValue"),
            SimpleType.create("ENTITY"),
            SimpleType.create("D")
        );
        
        return com.speedment.common.codegen.model.Class.of(getClassName())
            
            /******************************************************************/
            /*                         Documentation                          */
            /******************************************************************/
            .set(Javadoc.of()
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
            .add(Generic.of(SimpleType.create("ENTITY")))
            .add(Generic.of(SimpleType.create("D")))
            .add(SimpleParameterizedType.create(
                siblingOf(ReferenceFieldComparator.class, "%1$sFieldComparator"),
                SimpleType.create("ENTITY"),
                SimpleType.create("D")
            ))
            
            /******************************************************************/
            /*                        Private Fields                          */
            /******************************************************************/
            .add(Field.of("field", fieldType).private_().final_())
            .add(Field.of("reversed", boolean.class).private_())
            
            /******************************************************************/
            /*                          Constructor                           */
            /******************************************************************/
            .add(Constructor.of().public_()
                .add(Field.of("field", fieldType))
                .add(
                    "this.field    = requireNonNull(field);",
                    "this.reversed = false;"
                )
            )
            
            /******************************************************************/
            /*                            Methods                             */
            /******************************************************************/
            .add(Method.of("getField", fieldType)
                .add(DefaultAnnotationUsage.OVERRIDE)
                .public_()
                .add("return field;")
            )
            
            .add(Method.of("isReversed", boolean.class)
                .add(DefaultAnnotationUsage.OVERRIDE)
                .public_()
                .add("return reversed;")
            )
            
            .add(Method.of("reversed", SimpleParameterizedType.create(
                    Comparator.class, SimpleType.create("ENTITY")
                ))
                .add(DefaultAnnotationUsage.OVERRIDE)
                .public_()
                .add(
                    "reversed = !reversed;",
                    "return this;"
                )
            )
            
            .add(Method.of("compare", int.class)
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add(Field.of("first", SimpleType.create("ENTITY")))
                .add(Field.of("second", SimpleType.create("ENTITY")))
                .public_()
                .add(
                    "requireNonNulls(first, second);",
                    "final " + primitive() + " a = field.getAs" + ucPrimitive() + "(first);",
                    "final " + primitive() + " b = field.getAs" + ucPrimitive() + "(second);",
                    "return applyReversed(a - b);"
                )
            )
            
            /******************************************************************/
            /*                       Private Methods                          */
            /******************************************************************/
            .add(Method.of("applyReversed", int.class)
                .private_()
                .add(Field.of("compare", 
                    isLong()   ? long.class   : 
                    isDouble() ? double.class : 
                    isFloat()  ? float.class  :
                                 int.class
                ))
                .add(
                    "if (compare == 0) " + block(
                        "return 0;"
                    ) + " else " + block(
                        "if (reversed) " + block(
                            "if (compare > 0) " + block(
                                "return -1;"
                            ) + " else " + block(
                                "return 1;"
                            )
                        ) + " else " + block(
                            "if (compare > 0) " + block(
                                "return 1;"
                            ) + " else " + block(
                                "return -1;"
                            )
                        )
                    )
                )
            )
        ;
    }
    
    private boolean isLong() {
        return long.class.getSimpleName().equals(primitive());
    }
    
    private boolean isDouble() {
        return double.class.getSimpleName().equals(primitive());
    }
    
    private boolean isFloat() {
        return float.class.getSimpleName().equals(primitive());
    }
}