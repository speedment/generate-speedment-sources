package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.DefaultAnnotationUsage;
import com.speedment.common.codegen.constant.DefaultJavadocTag;
import com.speedment.common.codegen.constant.DefaultType;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Method;
import com.speedment.runtime.config.identifier.TableIdentifier;
import com.speedment.runtime.core.field.ComparableForeignKeyField;
import com.speedment.runtime.core.field.ReferenceField;
import com.speedment.runtime.core.field.method.BackwardFinder;
import com.speedment.runtime.core.field.method.FindFrom;
import com.speedment.runtime.core.internal.field.ReferenceFieldImpl;
import com.speedment.runtime.core.internal.field.method.FindFromReference;
import com.speedment.runtime.core.internal.field.method.BackwardFinderImpl;
import com.speedment.runtime.core.manager.Manager;
import com.speedment.sources.Pattern;
import java.lang.reflect.Type;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 *
 * @author Emil Forslund
 */
public final class ForeignKeyFieldImplPattern extends AbstractSiblingPattern {

    private final Pattern delegator;
    
    public ForeignKeyFieldImplPattern(Class<?> wrapper, Class<?> primitive) {
        this(wrapper, primitive, new FieldImplPattern(wrapper, primitive));
    }
    
    public ForeignKeyFieldImplPattern(Class<?> wrapper, Class<?> primitive, Pattern delegator) {
        super(wrapper, primitive);
        this.delegator = requireNonNull(delegator);
    }

    @Override
    protected Class<?> getSiblingClass() {
        return ReferenceFieldImpl.class;
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "ForeignKeyFieldImpl";
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        final Type referencedFieldType = SimpleParameterizedType.create(
            siblingOf(ReferenceField.class, "%1$sField"),
            SimpleType.create("FK_ENTITY"),
            SimpleType.create("D")
        );
        
        return ((com.speedment.common.codegen.model.Class) delegator.make(file))
            
            // Change the name of the class
            .setName(getClassName())
            
            // Add an extra parameter to the javadoc section
            .call(c -> c.getJavadoc().ifPresent(doc -> doc.add(
                DefaultJavadocTag.PARAM.setValue("<FK_ENTITY>").setText("foreign entity type")
            )))
            
            // Add an extra generic type parameter
            .add(Generic.of(SimpleType.create("FK_ENTITY")))
            
            // Change which interfaces are implemented
            .add(SimpleParameterizedType.create(
                siblingOf(ComparableForeignKeyField.class, "%1$sForeignKeyField"),
                SimpleType.create("ENTITY"),
                SimpleType.create("D"),
                SimpleType.create("FK_ENTITY")
            ))
            
            // Insert two new private fields just before the 'typeMapper'-field
            .call(c -> {
                c.getFields().add(
                    indexOf(c.getFields(), f -> "typeMapper".equals(f.getName())),
                    Field.of("referenced", referencedFieldType).private_().final_()
                );
            })
            
            // Insert two new constructor parameters just before 'typeMapper' 
            // and a line that sets the member variables to their new values
            .call(c -> c.getConstructors().forEach(constr -> {
                constr.getFields().add(
                    indexOf(constr.getFields(), f -> "typeMapper".equals(f.getName())),
                    Field.of("referenced", referencedFieldType)
                );
                
                constr.getCode().add(
                    indexOf(constr.getCode(), row -> row.startsWith("this.typeMapper")),
                    "this.referenced = requireNonNull(referenced);"
                );
            }))
            
            // Insert the finder()-method
            .call(c -> {
                final Type findFromType = siblingOf(FindFromReference.class, "FindFrom%1$s");
                file.add(Import.of(findFromType));
                
                c.getMethods().add(
                    indexOf(c.getMethods(), m -> "typeMapper".equals(m.getName())),
                    Method.of("finder", SimpleParameterizedType.create(
                        FindFrom.class,
                        SimpleType.create("ENTITY"),
                        SimpleType.create("FK_ENTITY")
                    )).public_()
                        .add(DefaultAnnotationUsage.OVERRIDE)
                        .add(Field.of("identifier", SimpleParameterizedType.create(TableIdentifier.class, SimpleType.create("FK_ENTITY"))))
                        .add(Field.of("streamSupplier", DefaultType.supplier(DefaultType.stream(SimpleType.create("FK_ENTITY")))))
                        .add("return new FindFrom" + ucPrimitive() + "<>(this, referenced, identifier, streamSupplier);")
                );
                
                c.getMethods().add(
                    indexOf(c.getMethods(), m -> "finder".equals(m.getName())),
                    Method.of("getReferencedField", referencedFieldType).public_()
                        .add(DefaultAnnotationUsage.OVERRIDE)
                        .add("return referenced;")
                );
                
                c.getMethods().add(
                    indexOf(c.getMethods(), m -> "finder".equals(m.getName())),
                    Method.of("backwardFinder", 
                        SimpleParameterizedType.create(
                            BackwardFinder.class, 
                            SimpleType.create("FK_ENTITY"), 
                            SimpleType.create("ENTITY")
                        ))
                        .public_()
                        .add(DefaultAnnotationUsage.OVERRIDE)
                        .add(Field.of("identifier", SimpleParameterizedType.create(TableIdentifier.class, SimpleType.create("ENTITY"))))
                        .add(Field.of("streamSupplier", DefaultType.supplier(DefaultType.stream(SimpleType.create("ENTITY")))))
                        .call(() -> file.add(Import.of(BackwardFinderImpl.class)))
                        .add("return new " + BackwardFinderImpl.class.getSimpleName() + "<>(this, identifier, streamSupplier);")
                );
            })
        ;
    }

    private static <T> int indexOf(List<T> list, Predicate<T> search) {
        for (int i = 0; i < list.size(); i++) {
            if (search.test(list.get(i))) {
                return i;
            }
        }
        
        throw new RuntimeException(
            "Expected to find atleast one match in " + list
        );
    }
}