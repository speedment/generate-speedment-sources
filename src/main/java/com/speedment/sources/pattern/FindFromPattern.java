package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.DefaultAnnotationUsage;
import com.speedment.common.codegen.constant.DefaultJavadocTag;
import com.speedment.common.codegen.constant.DefaultType;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import static com.speedment.common.codegen.internal.util.Formatting.indent;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Constructor;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.runtime.exception.SpeedmentException;
import com.speedment.runtime.field.ReferenceField;
import com.speedment.runtime.internal.field.finder.FindFromReference;
import com.speedment.runtime.manager.Manager;
import java.lang.reflect.Type;

/**
 *
 * @author Emil Forslund
 */
public final class FindFromPattern extends AbstractSiblingPattern {

    public FindFromPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    protected Class<?> getSiblingClass() {
        return FindFromReference.class;
    }

    @Override
    public String getClassName() {
        return "FindFrom" + ucPrimitive();
    }

    @Override
    public ClassOrInterface<?> make(File file) {

        file.add(Import.of(SpeedmentException.class));
        
        final Type fieldType = SimpleParameterizedType.create(
            siblingOf(ReferenceField.class, "%1$sField"),
            SimpleType.create("ENTITY"),
            DefaultType.WILDCARD
        );
        
        final Type fkFieldType = SimpleParameterizedType.create(
            siblingOf(ReferenceField.class, "%1$sField"),
            SimpleType.create("FK_ENTITY"),
            DefaultType.WILDCARD
        );
        
        final Type superType = SimpleParameterizedType.create(
            siblingOf(FindFromReference.class, "AbstractFindFrom"),
            SimpleType.create("ENTITY"),
            SimpleType.create("FK_ENTITY"),
            fieldType,
            fkFieldType
        );
        
        return com.speedment.common.codegen.model.Class.of(getClassName())
            
            /******************************************************************/
            /*                         Documentation                          */
            /******************************************************************/
            .set(Javadoc.of()
                .add(DefaultJavadocTag.PARAM.setValue("<ENTITY>").setText("entity type"))
                .add(DefaultJavadocTag.PARAM.setValue("<FK_ENTITY>").setText("foreign entity type"))
                .add(DefaultJavadocTag.AUTHOR.setValue("Emil Forslund"))
                .add(DefaultJavadocTag.SINCE.setValue("3.0.0"))
            )
            
            /******************************************************************/
            /*                       Class Declaration                        */
            /******************************************************************/
            .public_().final_()
            .add(DefaultAnnotationUsage.GENERATED)
            .add(Generic.of(SimpleType.create("ENTITY")))
            .add(Generic.of(SimpleType.create("FK_ENTITY")))
            .setSupertype(superType)
            
            /******************************************************************/
            /*                          Constructor                           */
            /******************************************************************/
            .add(Constructor.of().public_()
                .add(Field.of("source", fieldType))
                .add(Field.of("target", fkFieldType))
                .add(Field.of("manager", SimpleParameterizedType.create(
                    Manager.class, SimpleType.create("FK_ENTITY")
                )))
                .add("super(source, target, manager);")
            )
            
            /******************************************************************/
            /*                            Methods                             */
            /******************************************************************/
            .add(Method.of("apply", SimpleType.create("FK_ENTITY")).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add(Field.of("entity", SimpleType.create("ENTITY")))
                .add(
                    "final " + primitive() + " value = getSourceField().getter().getAs" + ucPrimitive() + "(entity);",
                    "return getTargetManager().findAny(getTargetField(), value)",
                    indent(".orElseThrow(() -> new SpeedmentException("),
                    indent("\"Error! Could not find any \" + ", 2),
                    indent("getTargetManager().getEntityClass().getSimpleName() + ", 2),
                    indent("\" with '\" + getTargetField().identifier().columnName() + ", 2),
                    indent("\"' = '\" + value + \"'.\"", 2),
                    indent("));")
                )
            )
        ;
    }
}