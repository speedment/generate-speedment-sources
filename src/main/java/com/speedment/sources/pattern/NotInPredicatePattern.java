package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.*;
import com.speedment.common.codegen.model.*;
import com.speedment.common.tuple.Tuple1;
import com.speedment.runtime.field.internal.predicate.AbstractFieldPredicate;
import com.speedment.runtime.field.internal.predicate.reference.ReferenceInPredicate;
import com.speedment.runtime.field.predicate.PredicateType;
import com.speedment.runtime.field.trait.HasReferenceValue;

import java.lang.Class;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 *
 * @author Emil Forslund
 */
public final class NotInPredicatePattern extends AbstractCousinPattern {

    public NotInPredicatePattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "NotInPredicate";
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

            ////////////////////////////////////////////////////////////////////
            //                         Documentation                          //
            ////////////////////////////////////////////////////////////////////
            .set(Javadoc.of(formatJavadoc(
                    "A predicate that evaluates if a value is not included " +
                    "in a set of %2$ss."
                ))
                .add(DefaultJavadocTag.PARAM.setValue("<ENTITY>").setText("entity type"))
                .add(DefaultJavadocTag.PARAM.setValue("<D>").setText("database type"))
                .add(DefaultJavadocTag.AUTHOR.setValue("Emil Forslund"))
                .add(DefaultJavadocTag.SINCE.setValue("3.0.11"))
            )

            ////////////////////////////////////////////////////////////////////
            //                       Class Declaration                        //
            ////////////////////////////////////////////////////////////////////
            .public_().final_()
            .add(generatedAnnotation())
            .add(Generic.of("ENTITY"))
            .add(Generic.of("D"))
            .setSupertype(SimpleParameterizedType.create(
                AbstractFieldPredicate.class,
                SimpleType.create("ENTITY"),
                hasValueType
            ))
            .add(SimpleParameterizedType.create(
                Tuple1.class,
                DefaultType.set(wrapperType())
            ))

            ////////////////////////////////////////////////////////////////////
            //                     Private Member Fields                      //
            ////////////////////////////////////////////////////////////////////
            .add(Field.of("set", DefaultType.set(wrapperType()))
                .private_().final_()
            )

            ////////////////////////////////////////////////////////////////////
            //                          Constructors                          //
            ////////////////////////////////////////////////////////////////////
            .add(Constructor.of()
                .add(Field.of("field", hasValueType))
                .add(Field.of("set", DefaultType.set(wrapperType())))
                .add(
                    "super(" + enumConstant + ", field, entity -> !set.contains(field.getAs" + ucPrimitive() + "(entity)));",
                    "this.set = requireNonNull(set);"
                )
            )

            ////////////////////////////////////////////////////////////////////
            //                           Methods                              //
            ////////////////////////////////////////////////////////////////////
            .add(Method.of("get0", DefaultType.set(wrapperType())).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return set;")
            )
            
            .add(Method.of("negate", SimpleParameterizedType.create(
                cousinOf(ReferenceInPredicate.class,
                    getPackageName(),
                    ucPrimitive() + "InPredicate"
                ),
                SimpleType.create("ENTITY"),
                SimpleType.create("D")
            )).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return new " + ucPrimitive() + "InPredicate<>(getField(), set);")
            )
        ;
    }
}