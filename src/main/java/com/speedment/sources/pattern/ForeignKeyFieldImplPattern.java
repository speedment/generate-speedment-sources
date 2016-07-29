package com.speedment.sources.pattern;

import com.speedment.common.codegen.internal.model.constant.DefaultAnnotationUsage;
import com.speedment.common.codegen.internal.model.constant.DefaultJavadocTag;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Method;
import com.speedment.common.codegen.model.Type;
import com.speedment.runtime.field.ReferenceForeignKeyField;
import com.speedment.runtime.field.method.Finder;
import com.speedment.runtime.internal.field.ReferenceForeignKeyFieldImpl;
import java.util.List;
import java.util.function.Predicate;

/**
 *
 * @author Emil Forslund
 */
public final class ForeignKeyFieldImplPattern extends AbstractSiblingPattern {

    private final FieldImplPattern delegator;
    
    public ForeignKeyFieldImplPattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
        delegator = new FieldImplPattern(wrapper, primitive);
    }

    @Override
    protected Class<?> getSiblingClass() {
        return ReferenceForeignKeyFieldImpl.class;
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "ForeignKeyFieldImpl";
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        final Type finderType = Type.of(Finder.class)
            .add(Generic.of(Type.of("ENTITY")))
            .add(Generic.of(Type.of("FK_ENTITY")));
        
        return ((com.speedment.common.codegen.model.Class) delegator.make(file))
            
            // Change the name of the class
            .setName(getClassName())
            
            // Add an extra parameter to the javadoc section
            .call(c -> c.getJavadoc().ifPresent(doc -> doc.add(
                DefaultJavadocTag.PARAM.setValue("<FK_ENTITY>").setText("foreign entity type")
            )))
            
            // Add an extra generic type parameter
            .add(Generic.of(Type.of("FK_ENTITY")))
            
            // Change which interfaces are implemented
            .add(siblingOf(ReferenceForeignKeyField.class, "%1$sForeignKeyField")
                .add(Generic.of(Type.of("ENTITY")))
                .add(Generic.of(Type.of("D")))
                .add(Generic.of(Type.of("FK_ENTITY")))
            )
            
            // Insert a new private field just before the 'typeMapper'-field
            .call(c -> c.getFields().add(
                indexOf(c.getFields(), f -> "typeMapper".equals(f.getName())),
                Field.of("finder", finderType).private_().final_()
            ))
            
            // Insert a new constructor parameter just before 'typeMapper' and
            // a line that sets the member variable to its value
            .call(c -> c.getConstructors().forEach(constr -> {
                constr.getFields().add(
                    indexOf(constr.getFields(), f -> "typeMapper".equals(f.getName())),
                    Field.of("finder", finderType)
                );
                
                constr.getCode().add(
                    indexOf(constr.getCode(), row -> row.startsWith("this.typeMapper")),
                    "this.finder     = requireNonNull(finder);"
                );
            }))
            
            // Insert the finder()-method
            .call(c -> c.getMethods().add(
                indexOf(c.getMethods(), m -> "typeMapper".equals(m.getName())),
                Method.of("finder", finderType).public_()
                    .add(DefaultAnnotationUsage.OVERRIDE)
                    .add("return finder;")
            ))
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