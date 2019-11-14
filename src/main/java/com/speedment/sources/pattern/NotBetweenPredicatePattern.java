package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.DefaultAnnotationUsage;
import com.speedment.common.codegen.constant.DefaultJavadocTag;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.*;
import com.speedment.common.tuple.Tuple2;
import com.speedment.runtime.field.internal.predicate.AbstractFieldPredicate;
import com.speedment.runtime.field.internal.predicate.reference.ReferenceBetweenPredicate;
import com.speedment.runtime.field.internal.predicate.reference.ReferenceInPredicate;
import com.speedment.runtime.field.predicate.Inclusion;
import com.speedment.runtime.field.predicate.PredicateType;
import com.speedment.runtime.field.predicate.trait.HasInclusion;
import com.speedment.runtime.field.trait.HasReferenceValue;

import java.lang.Class;
import java.lang.reflect.Type;
import java.util.Objects;

import static com.speedment.common.codegen.util.Formatting.block;
import static com.speedment.common.codegen.util.Formatting.indent;

/**
 *
 * @author Emil Forslund
 * @since  3.0.11
 */
public final class NotBetweenPredicatePattern extends AbstractCousinPattern {

    public NotBetweenPredicatePattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "NotBetweenPredicate";
    }

    @Override
    protected String getPackageName() {
        return primitive() + "s";
    }

    @Override
    protected Class<?> getCousinClass() {
        return ReferenceBetweenPredicate.class;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        file.add(Import.of(Objects.class).static_().setStaticMember("requireNonNull"));
        file.add(Import.of(PredicateType.class));
        
        final String enumConstant = PredicateType.class.getSimpleName() + "." + PredicateType.NOT_BETWEEN.name();
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
                "A predicate that evaluates if a value is not between two %2$ss."
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

            ////////////////////////////////////////////////////////////////////
            //                     Implemented Interfaces                     //
            ////////////////////////////////////////////////////////////////////
            .add(HasInclusion.class)
            .add(SimpleParameterizedType.create(
                Tuple2.class,
                wrapperType(),
                wrapperType()
            ))

            ////////////////////////////////////////////////////////////////////
            //                        Private Fields                          //
            ////////////////////////////////////////////////////////////////////
            .add(Field.of("start", primitiveType()).private_().final_())
            .add(Field.of("end", primitiveType()).private_().final_())
            .add(Field.of("inclusion", Inclusion.class).private_().final_())

            ////////////////////////////////////////////////////////////////////
            //                          Constructors                          //
            ////////////////////////////////////////////////////////////////////
            .add(Constructor.of().public_()
                .add(Field.of("field", hasValueType))
                .add(Field.of("start", primitiveType()))
                .add(Field.of("end", primitiveType()))
                .add(Field.of("inclusion", Inclusion.class))
                .add(
                    "super(" + enumConstant + ", field, entity -> " + block(
                        "final " + primitive() + " fieldValue = field.getAs" + ucPrimitive() + "(entity);",
                        "",
                        "switch (inclusion) " + block(
                            "case " + Inclusion.START_EXCLUSIVE_END_EXCLUSIVE.name() + " :",
                            indent("return (start >= fieldValue || end <= fieldValue);"),
                            "",
                            "case " + Inclusion.START_EXCLUSIVE_END_INCLUSIVE.name() + " :",
                            indent("return (start >= fieldValue || end < fieldValue);"),
                            "",
                            "case " + Inclusion.START_INCLUSIVE_END_EXCLUSIVE.name() + " :",
                            indent("return (start > fieldValue || end <= fieldValue);"),
                            "",
                            "case " + Inclusion.START_INCLUSIVE_END_INCLUSIVE.name() + " :",
                            indent("return (start > fieldValue || end < fieldValue);"),
                            "",
                            "default : throw new " + IllegalStateException.class.getSimpleName() + "(\"Inclusion unknown: \" + inclusion);"
                        )
                    ) + ");",
                    "",
                    "this.start     = start;",
                    "this.end       = end;",
                    "this.inclusion = requireNonNull(inclusion);"
                )
            )

            ////////////////////////////////////////////////////////////////////
            //                           Methods                              //
            ////////////////////////////////////////////////////////////////////
            .add(Method.of("get0", wrapperType()).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return start;")
            )
            
            .add(Method.of("get1", wrapperType()).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return end;")
            )
            
            .add(Method.of("getInclusion", Inclusion.class).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return inclusion;")
            )
            
            .add(Method.of("negate", SimpleParameterizedType.create(
                cousinOf(ReferenceInPredicate.class,
                    getPackageName(),
                    ucPrimitive() + "BetweenPredicate"
                ),
                SimpleType.create("ENTITY"),
                SimpleType.create("D")
            )).public_()
                .add(DefaultAnnotationUsage.OVERRIDE)
                .add("return new " + ucPrimitive() + "BetweenPredicate<>(getField(), start, end, inclusion);")
            );
        
    }
}