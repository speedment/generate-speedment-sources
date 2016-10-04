package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.DefaultAnnotationUsage;
import com.speedment.common.codegen.constant.DefaultJavadocTag;
import com.speedment.common.codegen.constant.DefaultType;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Constructor;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.runtime.field.predicate.PredicateType;
import com.speedment.runtime.field.trait.HasReferenceValue;
import com.speedment.runtime.field.internal.predicate.AbstractFieldPredicate;

import com.speedment.common.codegen.model.Method;
import com.speedment.common.tuple.Tuple1;
import com.speedment.runtime.field.internal.predicate.reference.ReferenceInPredicate;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 *
 * @author Emil Forslund
 */
public final class InPredicatePattern extends AbstractCousinPattern {

    public InPredicatePattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "InPredicate";
    }

    @Override
    protected String getPackageName() {
        return primitive() + "s";
    }

    @Override
    protected Class<?> getCousinClass() {
        return ReferenceInPredicate.class;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        file.add(Import.of(Objects.class).static_().setStaticMember("requireNonNull"));
        file.add(Import.of(PredicateType.class));
        
        final String enumConstant = PredicateType.class.getSimpleName() + "." + PredicateType.IN.name();
        final Type hasValueType = SimpleParameterizedType.create(
            siblingOf(HasReferenceValue.class, "Has%1$sValue"),
            SimpleType.create("ENTITY"),
            SimpleType.create("D")
        );
        
        return com.speedment.common.codegen.model.Class.of(getClassName())
            
            /******************************************************************/
            /*                         Documentation                          */
            /******************************************************************/
            .set(Javadoc.of(formatJavadoc(
                "A predicate that evaluates if a value is included in a set of %2$ss."
                ))
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
            .add(Generic.of("ENTITY"))
            .add(Generic.of("D"))
            .setSupertype(SimpleParameterizedType.create(
                AbstractFieldPredicate.class,
                SimpleType.create("ENTITY"),
                wrapperType(),
                hasValueType
            ))
            .add(SimpleParameterizedType.create(
                Tuple1.class,
                DefaultType.set(wrapperType())
            ))
            
            /******************************************************************/
            /*                     Private Member Fields                      */
            /******************************************************************/
            .add(Field.of("set", DefaultType.set(wrapperType()))
                .private_().final_()
            )
            
            /******************************************************************/
            /*                          Constructor                           */
            /******************************************************************/
            .add(Constructor.of().public_()
                .add(Field.of("field", hasValueType))
                .add(Field.of("set", DefaultType.set(wrapperType())))
                .add(
                    "super(" + enumConstant + ", field, entity -> set.contains(field.getAs" + ucPrimitive() + "(entity)));",
                    "this.set = requireNonNull(set);"
                )
            )
            
            /******************************************************************/
            /*                            Methods                             */
            /******************************************************************/
            .add(Method.of("get0", DefaultType.set(wrapperType())).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return set;")
            )
        ;
    }
}