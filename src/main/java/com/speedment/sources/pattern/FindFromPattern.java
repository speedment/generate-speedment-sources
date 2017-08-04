package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.*;
import com.speedment.common.codegen.model.*;
import com.speedment.runtime.config.identifier.TableIdentifier;
import com.speedment.runtime.field.ReferenceField;
import com.speedment.runtime.field.exception.SpeedmentFieldException;
import com.speedment.runtime.field.internal.method.FindFromReference;

import java.lang.Class;
import java.lang.reflect.Type;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.speedment.common.codegen.util.Formatting.indent;

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

        file.add(Import.of(SpeedmentFieldException.class));
        
        final Type fieldType = SimpleParameterizedType.create(
            siblingOf(ReferenceField.class, "%1$sForeignKeyField"),
            SimpleType.create("ENTITY"),
            DefaultType.WILDCARD,
            SimpleType.create("FK_ENTITY")
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
            wrapperType(),
            fieldType,
            fkFieldType
        );
        
        return com.speedment.common.codegen.model.Class.of(getClassName())
            
            ////////////////////////////////////////////////////////////////////
            /*                         Documentation                          */
            ////////////////////////////////////////////////////////////////////
            .set(Javadoc.of()
                .add(DefaultJavadocTag.PARAM.setValue("<ENTITY>").setText("entity type"))
                .add(DefaultJavadocTag.PARAM.setValue("<FK_ENTITY>").setText("foreign entity type"))
                .add(DefaultJavadocTag.AUTHOR.setValue("Emil Forslund"))
                .add(DefaultJavadocTag.SINCE.setValue("3.0.0"))
            )
            
            ////////////////////////////////////////////////////////////////////
            /*                       Class Declaration                        */
            ////////////////////////////////////////////////////////////////////
            .public_().final_()
            .add(generatedAnnotation())
            .add(Generic.of(SimpleType.create("ENTITY")))
            .add(Generic.of(SimpleType.create("FK_ENTITY")))
            .setSupertype(superType)
            
            ////////////////////////////////////////////////////////////////////
            /*                          Constructor                           */
            ////////////////////////////////////////////////////////////////////
            .add(Constructor.of().public_()
                .add(Field.of("source", fieldType))
                .add(Field.of("target", fkFieldType))
                .add(Field.of("identifier", SimpleParameterizedType.create(
                    TableIdentifier.class, SimpleType.create("FK_ENTITY")
                )))
                .add(Field.of("streamSupplier", SimpleParameterizedType.create(
                    Supplier.class, SimpleParameterizedType.create(
                        Stream.class,
                        SimpleType.create("FK_ENTITY")
                    )
                )))
                .add("super(source, target, identifier, streamSupplier);")
            )
            
            ////////////////////////////////////////////////////////////////////
            /*                            Methods                             */
            ////////////////////////////////////////////////////////////////////
            .add(Method.of("apply", SimpleType.create("FK_ENTITY")).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add(Field.of("entity", SimpleType.create("ENTITY")))
                .add(
                    "final " + primitive() + " value = getSourceField().getter().applyAs" + ucPrimitive() + "(entity);",
                    "return stream()",
                    indent(".filter(getTargetField().equal(value))"),
                    indent(".findAny()"),
                    indent(".orElseThrow(() -> new SpeedmentFieldException("),
                    indent("\"Error! Could not find any entities in table '\" + ", 2),
                    indent("getTableIdentifier() + ", 2),
                    indent("\"' with '\" + getTargetField().identifier().getColumnName() + ", 2),
                    indent("\"' = '\" + value + \"'.\"", 2),
                    indent("));")
                )
            )
        ;
    }
}