package com.speedment.sources.pattern;

import com.speedment.common.codegen.internal.model.constant.DefaultAnnotationUsage;
import com.speedment.common.codegen.internal.model.constant.DefaultJavadocTag;
import com.speedment.common.codegen.internal.model.constant.DefaultType;
import static com.speedment.common.codegen.internal.util.Formatting.block;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Constructor;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.common.codegen.model.Type;
import com.speedment.runtime.field.trait.HasReferenceValue;
import com.speedment.runtime.internal.field.comparator.ReferenceFieldComparator;
import com.speedment.runtime.internal.field.comparator.ReferenceFieldComparatorImpl;
import com.speedment.runtime.util.NullUtil;
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
        file.add(Import.of(Type.of(Objects.class)).static_().setStaticMember("requireNonNull"));
        file.add(Import.of(Type.of(NullUtil.class)).static_().setStaticMember("requireNonNulls"));
        
        final Type fieldType = siblingOf(HasReferenceValue.class, "Has%1$sValue")
            .add(Generic.of(Type.of("ENTITY")))
            .add(Generic.of(Type.of("D")));
        
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
            .add(Generic.of(Type.of("ENTITY")))
            .add(Generic.of(Type.of("D")))
            .add(siblingOf(ReferenceFieldComparator.class, "%1$sFieldComparator")
                .add(Generic.of(Type.of("ENTITY")))
                .add(Generic.of(Type.of("D")))
            )
            
            /******************************************************************/
            /*                        Private Fields                          */
            /******************************************************************/
            .add(Field.of("field", fieldType).private_().final_())
            .add(Field.of("reversed", DefaultType.BOOLEAN_PRIMITIVE).private_())
            
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
            
            .add(Method.of("isReversed", DefaultType.BOOLEAN_PRIMITIVE)
                .add(DefaultAnnotationUsage.OVERRIDE)
                .public_()
                .add("return reversed;")
            )
            
            .add(Method.of("reversed", Type.of(Comparator.class)
                    .add(Generic.of(Type.of("ENTITY")))
                )
                .add(DefaultAnnotationUsage.OVERRIDE)
                .public_()
                .add(
                    "reversed = !reversed;",
                    "return this;"
                )
            )
            
            .add(Method.of("compare", DefaultType.INT_PRIMITIVE)
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add(Field.of("first", Type.of("ENTITY")))
                .add(Field.of("second", Type.of("ENTITY")))
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
            .add(Method.of("applyReversed", 
                    DefaultType.INT_PRIMITIVE
                )
                .private_()
                .add(Field.of("compare", 
                    isLong()   ? DefaultType.LONG_PRIMITIVE   : 
                    isDouble() ? DefaultType.DOUBLE_PRIMITIVE : 
                    isFloat()  ? DefaultType.FLOAT_PRIMITIVE  :
                                 DefaultType.INT_PRIMITIVE
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