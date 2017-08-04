package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.*;
import com.speedment.runtime.config.identifier.ColumnIdentifier;
import com.speedment.runtime.field.ReferenceField;
import com.speedment.runtime.field.internal.ReferenceFieldImpl;
import com.speedment.runtime.field.method.ReferenceGetter;
import com.speedment.runtime.field.method.ReferenceSetter;
import com.speedment.runtime.field.trait.HasComparableOperators;
import com.speedment.runtime.field.trait.HasReferenceValue;
import com.speedment.runtime.typemapper.TypeMapper;

import java.lang.Class;

import static com.speedment.common.codegen.constant.DefaultJavadocTag.*;
import static com.speedment.common.codegen.util.Formatting.indent;

/**
 *
 * @author Emil Forslund
 */
public final class FieldPattern extends AbstractSiblingPattern {

    public FieldPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "Field";
    }

    @Override
    protected Class<?> getSiblingClass() {
        return ReferenceField.class;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        file.add(Import.of(siblingOf(ReferenceFieldImpl.class, 
            ucPrimitive() + "FieldImpl"
        )));
        
        return Interface.of(getClassName())
            
            ////////////////////////////////////////////////////////////////////
            /*                         Documentation                          */
            ////////////////////////////////////////////////////////////////////
            .set(Javadoc.of(formatJavadoc(
                "A field that represents a primitive {@code %2$s} value."
                ))
                .add(PARAM.setValue("<ENTITY>").setText("entity type"))
                .add(PARAM.setValue("<D>").setText("database type"))
                .add(AUTHOR.setValue("Emil Forslund"))
                .add(SINCE.setValue("3.0.0"))
                .add(SEE.setValue(ReferenceField.class.getSimpleName()))
            )
            
            ////////////////////////////////////////////////////////////////////
            /*                       Class Declaration                        */
            ////////////////////////////////////////////////////////////////////
            .public_()
            .add(generatedAnnotation())
            .add(Generic.of("ENTITY"))
            .add(Generic.of("D"))
            .add(SimpleParameterizedType.create(
                com.speedment.runtime.field.Field.class,
                SimpleType.create("ENTITY")
            ))
            .add(SimpleParameterizedType.create(
                siblingOf(HasReferenceValue.class, "Has%1$sValue"),
                SimpleType.create("ENTITY"),
                SimpleType.create("D")
            ))
            .add(SimpleParameterizedType.create(
                HasComparableOperators.class,
                SimpleType.create("ENTITY"),
                wrapperType()
            ))
            
            ////////////////////////////////////////////////////////////////////
            /*                   Static Construction Method                   */
            ////////////////////////////////////////////////////////////////////
            .add(Method.of("create", SimpleParameterizedType.create(
                getFullClassName(),
                SimpleType.create("ENTITY"),
                SimpleType.create("D")
            )).static_()
                .add(Generic.of("ENTITY"))
                .add(Generic.of("D"))
                .set(Javadoc.of(formatJavadoc(
                        "Creates a new {@link %3$sField} using the default " + 
                        "implementation."
                    ))
                    .add(PARAM.setValue("<ENTITY>")
                        .setText("entity type"))
                    .add(PARAM.setValue("<D>")
                        .setText("database type"))
                    .add(PARAM.setValue("identifier")
                        .setText("column that this field represents"))
                    .add(PARAM.setValue("getter")
                        .setText("method reference to getter in entity"))
                    .add(PARAM.setValue("setter")
                        .setText("method reference to setter in entity"))
                    .add(PARAM.setValue("typeMapper")
                        .setText("type mapper that is applied"))
                    .add(PARAM.setValue("unique")
                        .setText("if column only contains unique values"))
                    .add(RETURN.setValue("the created field"))
                )
                .add(Field.of("identifier", SimpleParameterizedType.create(
                    ColumnIdentifier.class, SimpleType.create("ENTITY")
                )))
                .add(Field.of("getter", SimpleParameterizedType.create(
                    siblingOf(ReferenceGetter.class, ucPrimitive() + "Getter"), 
                    SimpleType.create("ENTITY")
                )))
                .add(Field.of("setter", SimpleParameterizedType.create(
                    siblingOf(ReferenceSetter.class, ucPrimitive() + "Setter"), 
                    SimpleType.create("ENTITY")
                )))
                .add(Field.of("typeMapper", SimpleParameterizedType.create(
                    TypeMapper.class, SimpleType.create("D"), wrapperType()
                )))
                .add(Field.of("unique", boolean.class))
                .add("return new " + ucPrimitive() + "FieldImpl<>(")
                .add(indent("identifier, getter, setter, typeMapper, unique"))
                .add(");")
            )
        ;
    }
}