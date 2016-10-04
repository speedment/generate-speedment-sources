package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.DefaultJavadocTag;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import static com.speedment.common.codegen.internal.util.Formatting.indent;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Interface;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.runtime.config.identifier.ColumnIdentifier;
import com.speedment.runtime.core.field.ComparableForeignKeyField;
import com.speedment.runtime.core.field.ReferenceField;
import com.speedment.runtime.core.field.method.ReferenceGetter;
import com.speedment.runtime.core.field.method.ReferenceSetter;
import com.speedment.runtime.core.field.trait.HasFinder;
import com.speedment.runtime.core.internal.field.ComparableForeignKeyFieldImpl;
import com.speedment.runtime.typemapper.TypeMapper;

/**
 *
 * @author Emil Forslund
 */
public final class ForeignKeyFieldPattern extends AbstractSiblingPattern {

    public ForeignKeyFieldPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "ForeignKeyField";
    }

    @Override
    protected Class<?> getSiblingClass() {
        return ComparableForeignKeyField.class;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        return Interface.of(getClassName())
            
            /******************************************************************/
            /*                         Documentation                          */
            /******************************************************************/
            .set(Javadoc.of(formatJavadoc(
                    "A field that represents a primitive {@code %2$s} value " + 
                    "that references another column using a foreign key."
                ))
                .add(DefaultJavadocTag.PARAM.setValue("<ENTITY>").setText("entity type"))
                .add(DefaultJavadocTag.PARAM.setValue("<D>").setText("database type"))
                .add(DefaultJavadocTag.PARAM.setValue("<FK_ENTITY>").setText("foreign entity type"))
                .add(DefaultJavadocTag.AUTHOR.setValue("Emil Forslund"))
                .add(DefaultJavadocTag.SINCE.setValue("3.0.0"))
                .add(DefaultJavadocTag.SEE.setValue(ReferenceField.class.getSimpleName()))
                .add(DefaultJavadocTag.SEE.setValue(ComparableForeignKeyField.class.getSimpleName()))
            )
            
            /******************************************************************/
            /*                       Class Declaration                        */
            /******************************************************************/
            .add(generatedAnnotation())
            .public_()
            .add(Generic.of("ENTITY"))
            .add(Generic.of("D"))
            .add(Generic.of("FK_ENTITY"))
            .add(SimpleParameterizedType.create(
                siblingOf(ReferenceField.class, "%1$sField"),
                SimpleType.create("ENTITY"),
                SimpleType.create("D")
            ))
            .add(SimpleParameterizedType.create(
                HasFinder.class,
                SimpleType.create("ENTITY"),
                SimpleType.create("FK_ENTITY")
            ))
            
            /******************************************************************/
            /*                   Static Construction Method                   */
            /******************************************************************/
            .add(Method.of("create", SimpleParameterizedType.create(
                getFullClassName(),
                SimpleType.create("ENTITY"),
                SimpleType.create("D"),
                SimpleType.create("FK_ENTITY")
            )).static_()
                .add(Generic.of("ENTITY"))
                .add(Generic.of("D"))
                .add(Generic.of("FK_ENTITY"))
                .set(Javadoc.of(formatJavadoc(
                        "Creates a new {@link %3$sForeignKeyField} using the default " + 
                        "implementation."
                    ))
                    .add(DefaultJavadocTag.PARAM.setValue("<ENTITY>").setText("entity type"))
                    .add(DefaultJavadocTag.PARAM.setValue("<D>").setText("database type"))
                    .add(DefaultJavadocTag.PARAM.setValue("<FK_ENTITY>").setText("foreign entity type"))
                    .add(DefaultJavadocTag.PARAM.setValue("identifier").setText("column that this field represents"))
                    .add(DefaultJavadocTag.PARAM.setValue("getter").setText("method reference to the getter in the entity"))
                    .add(DefaultJavadocTag.PARAM.setValue("setter").setText("method reference to the setter in the entity"))
                    .add(DefaultJavadocTag.PARAM.setValue("referenced").setText("field in the foreign entity that is referenced"))
                    .add(DefaultJavadocTag.PARAM.setValue("typeMapper").setText("type mapper that is applied"))
                    .add(DefaultJavadocTag.PARAM.setValue("unique").setText("if represented column only contains unique values"))
                    .add(DefaultJavadocTag.RETURN.setValue("the created field"))
                )
                .add(Field.of("identifier", SimpleParameterizedType.create(ColumnIdentifier.class, SimpleType.create("ENTITY"))))
                .add(Field.of("getter", SimpleParameterizedType.create(siblingOf(ReferenceGetter.class, ucPrimitive() + "Getter"), SimpleType.create("ENTITY"))))
                .add(Field.of("setter", SimpleParameterizedType.create(siblingOf(ReferenceSetter.class, ucPrimitive() + "Setter"), SimpleType.create("ENTITY"))))
                .add(Field.of("referenced", SimpleParameterizedType.create(siblingOf(ReferenceField.class, ucPrimitive() + "Field"), SimpleType.create("FK_ENTITY"), SimpleType.create("D"))))
                .add(Field.of("typeMapper", SimpleParameterizedType.create(TypeMapper.class, SimpleType.create("D"), wrapperType())))
                .add(Field.of("unique", boolean.class))
                .call(() -> file.add(Import.of(siblingOf(ComparableForeignKeyFieldImpl.class, ucPrimitive() + "ForeignKeyFieldImpl"))))
                .add("return new " + ucPrimitive() + "ForeignKeyFieldImpl<>(")
                .add(indent("identifier, getter, setter, referenced, typeMapper, unique"))
                .add(");")
            )
        ;
    }
}