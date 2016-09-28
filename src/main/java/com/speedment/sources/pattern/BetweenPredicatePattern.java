package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.DefaultAnnotationUsage;
import com.speedment.common.codegen.constant.DefaultJavadocTag;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.constant.SimpleType;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Constructor;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Generic;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.runtime.field.predicate.Inclusion;
import com.speedment.runtime.field.predicate.PredicateType;
import com.speedment.runtime.field.trait.HasReferenceValue;
import com.speedment.runtime.internal.field.predicate.AbstractFieldPredicate;
import com.speedment.runtime.internal.field.predicate.BetweenPredicate;
import com.speedment.runtime.internal.field.predicate.reference.ReferenceBetweenPredicate;
import java.util.Objects;

import com.speedment.common.codegen.model.Method;
import static com.speedment.common.codegen.internal.util.Formatting.block;
import static com.speedment.common.codegen.internal.util.Formatting.indent;
import com.speedment.common.tuple.Tuple2;
import java.lang.reflect.Type;

/**
 *
 * @author Emil Forslund
 */
public final class BetweenPredicatePattern extends AbstractCousinPattern {

    public BetweenPredicatePattern(Class<?> wrapper, Class<?> primitive) {
        super(wrapper, primitive);
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "BetweenPredicate";
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
        
        final String enumConstant = PredicateType.class.getSimpleName() + "." + PredicateType.BETWEEN.name();
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
                "A predicate that evaluates if a value is between two %2$ss."
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
            .add(Generic.of("ENTITY"))
            .add(Generic.of("D"))
            .setSupertype(SimpleParameterizedType.create(
                AbstractFieldPredicate.class,
                SimpleType.create("ENTITY"),
                wrapperType(),
                hasValueType
            ))
            
            /******************************************************************/
            /*                     Implemented Interfaces                     */
            /******************************************************************/
            .add(BetweenPredicate.class)
            .add(SimpleParameterizedType.create(
                Tuple2.class,
                wrapperType(),
                wrapperType()
            ))
            
            /******************************************************************/
            /*                        Private Fields                          */
            /******************************************************************/
            .add(Field.of("start", primitiveType()).private_().final_())
            .add(Field.of("end", primitiveType()).private_().final_())
            .add(Field.of("inclusion", Inclusion.class).private_().final_())
            
            /******************************************************************/
            /*                          Constructor                           */
            /******************************************************************/
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
                            indent("return (start < fieldValue && end > fieldValue);"),
                            "",
                            "case " + Inclusion.START_EXCLUSIVE_END_INCLUSIVE.name() + " :",
                            indent("return (start < fieldValue && end >= fieldValue);"),
                            "",
                            "case " + Inclusion.START_INCLUSIVE_END_EXCLUSIVE.name() + " :",
                            indent("return (start <= fieldValue && end > fieldValue);"),
                            "",
                            "case " + Inclusion.START_INCLUSIVE_END_INCLUSIVE.name() + " :",
                            indent("return (start <= fieldValue && end >= fieldValue);"),
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
            
            /******************************************************************/
            /*                           Methods                              */
            /******************************************************************/
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
        ;
    }
}