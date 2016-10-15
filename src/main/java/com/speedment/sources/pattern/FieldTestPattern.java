package com.speedment.sources.pattern;

import com.speedment.common.codegen.constant.DefaultJavadocTag;
import com.speedment.common.codegen.constant.DefaultType;
import com.speedment.common.codegen.constant.SimpleParameterizedType;
import com.speedment.common.codegen.model.AnnotationUsage;
import com.speedment.common.codegen.model.Class;
import com.speedment.common.codegen.model.ClassOrInterface;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Import;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.model.Method;
import com.speedment.common.codegen.model.Value;
import static com.speedment.common.codegen.util.Formatting.ucfirst;
import com.speedment.runtime.field.ReferenceField;
import com.speedment.runtime.typemapper.TypeMapper;
import java.lang.reflect.Type;
import static java.util.stream.Collectors.joining;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Emil Forslund
 * @since  3.0.3
 */
public final class FieldTestPattern extends AbstractSiblingPattern {

    public FieldTestPattern(
            java.lang.Class<?> wrapper, 
            java.lang.Class<?> primitive) {
        
        super(wrapper, primitive);
    }

    @Override
    protected java.lang.Class<?> getSiblingClass() {
        return ReferenceField.class;
    }

    @Override
    public String getClassName() {
        return ucPrimitive() + "FieldTest";
    }

    @Override
    public boolean isTestClass() {
        return true;
    }

    @Override
    public ClassOrInterface<?> make(File file) {
        
        final Type basicEntity = siblingOf(ReferenceField.class, "BasicEntity");
        
        return Class.of(getClassName())
            
            /******************************************************************/
            /*                         Documentation                          */
            /******************************************************************/
            .set(Javadoc.of(formatJavadoc(
                "JUnit tests for the primitive {@code %2$s} field class."
                ))
                .add(DefaultJavadocTag.AUTHOR.setValue("Emil Forslund"))
                .add(DefaultJavadocTag.SINCE.setValue("3.0.3"))
                .add(DefaultJavadocTag.SEE.setValue(ucPrimitive() + "Field"))
            )
            
            /******************************************************************/
            /*                       Class Declaration                        */
            /******************************************************************/
            .public_().final_()
            .add(generatedAnnotation())
            
            /******************************************************************/
            /*                            Variables                           */
            /******************************************************************/
            .add(Field.of("FORMATTER", DefaultType.function(basicEntity, String.class))
                .private_().final_().static_()
                .set(Value.ofReference("entity -> \"\" + entity.getVar" + ucPrimitive() + "()"))
            )
            .add(Field.of("field", SimpleParameterizedType.create(siblingOf(ReferenceField.class, "%sField"), basicEntity, wrapperType())).private_())
            .add(Field.of("entities", DefaultType.list(basicEntity)).private_())
            .add(Field.of("a", basicEntity).private_())
            .add(Field.of("b", basicEntity).private_())
            .add(Field.of("c", basicEntity).private_())
            .add(Field.of("d", basicEntity).private_())
            .add(Field.of("e", basicEntity).private_())
            .add(Field.of("f", basicEntity).private_())
            .add(Field.of("g", basicEntity).private_())
            .add(Field.of("h", basicEntity).private_())
            .add(Field.of("i", basicEntity).private_())
            .add(Field.of("j", basicEntity).private_())
            .add(Field.of("k", basicEntity).private_())
            .add(Field.of("l", basicEntity).private_())
            
            /******************************************************************/
            /*                             Methods                            */
            /******************************************************************/
            .call(() -> file.add(Import.of(TypeMapper.class)))
            .add(Method.of("setUp", void.class).public_()
                .add(AnnotationUsage.of(Before.class))
                .add(
                    "field = " + ucPrimitive() + "Field.create(",
                    "    BasicEntity.Identifier.VAR_" + ucPrimitive().toUpperCase() + ",",
                    "    BasicEntity::getVar" + ucPrimitive() + ",",
                    "    BasicEntity::setVar" + ucPrimitive() + ",",
                    "    " + TypeMapper.class.getSimpleName() + ".primitive(),",
                    "    false",
                    ");",
                    "",
                    "a = new BasicEntity().setVar" + ucPrimitive() + "(" + value("0") + ");",
                    "b = new BasicEntity().setVar" + ucPrimitive() + "(" + value("-1") + ");",
                    "c = new BasicEntity().setVar" + ucPrimitive() + "(" + value("1") + ");",
                    "d = new BasicEntity().setVar" + ucPrimitive() + "(" + value("1") + ");",
                    "e = new BasicEntity().setVar" + ucPrimitive() + "(" + value("2") + ");",
                    "f = new BasicEntity().setVar" + ucPrimitive() + "(" + value("2") + ");",
                    "g = new BasicEntity().setVar" + ucPrimitive() + "(" + value("3") + ");",
                    "h = new BasicEntity().setVar" + ucPrimitive() + "(" + value("-5") + ");",
                    "i = new BasicEntity().setVar" + ucPrimitive() + "(" + value("1") + ");",
                    "j = new BasicEntity().setVar" + ucPrimitive() + "(" + wrapper() + ".MIN_VALUE);",
                    "k = new BasicEntity().setVar" + ucPrimitive() + "(" + wrapper() + ".MAX_VALUE);",
                    "l = new BasicEntity().setVar" + ucPrimitive() + "(" + value("0") + ");",
                    "",
                    "entities = Arrays.asList(a, b, c, d, e, f, g, h, i, j, k, l);"
                )
            )
            
            .add(testBetweenMethod("between", 
                array("a", "c", "d", "i", "l"), 
                array("a", "b", "c", "d", "i", "l"), 
                array("c", "d", "i"),
                array("a", "c", "d", "i", "l"),
                array("c", "d", "e", "f", "i"),
                array("a", "c", "d", "e", "f", "i", "l")
            ))
            
            .add(testComparisonMethod("equal", 
                array("b"), 
                array("a", "l"), 
                array("c", "d", "i"),
                array("e", "f"),
                array("g"),
                array("h"),
                array("j"),
                array("k"),
                array()
            ))
            
            .add(testComparisonMethod("greaterOrEqual", 
                array("a", "b", "c", "d", "e", "f", "g", "i", "k", "l"), 
                array("a", "c", "d", "e", "f", "g", "i", "k", "l"), 
                array("c", "d", "e", "f", "g", "i", "k"),
                array("e", "f", "g", "k"),
                array("g", "k"),
                array("a", "b", "c", "d", "e", "f", "g", "h", "i", "k", "l"),
                array("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l"),
                array("k"),
                array("k")
            ))
            
            .add(testComparisonMethod("greaterThan", 
                array("a", "c", "d", "e", "f", "g", "i", "k", "l"), 
                array("c", "d", "e", "f", "g", "i", "k"), 
                array("e", "f", "g", "k"),
                array("g", "k"),
                array("k"),
                array("a", "b", "c", "d", "e", "f", "g", "i", "k", "l"),
                array("a", "b", "c", "d", "e", "f", "g", "h", "i", "k", "l"),
                array(),
                array("k")
            ))
            
            .add(testComparisonMethod("greaterThan", 
                array("a", "c", "d", "e", "f", "g", "i", "k", "l"), 
                array("c", "d", "e", "f", "g", "i", "k"), 
                array("e", "f", "g", "k"),
                array("g", "k"),
                array("k"),
                array("a", "b", "c", "d", "e", "f", "g", "i", "k", "l"),
                array("a", "b", "c", "d", "e", "f", "g", "h", "i", "k", "l"),
                array(),
                array("k")
            ))
            
            .add(testInMethod("in", false,
                array(),
                array("a", "l"),
                array("a", "c", "d", "i", "l"),
                array("a", "c", "d", "i", "l"),
                array("b", "c", "d", "e", "f", "g", "i"),
                array("j", "k"),
                array("c", "d", "e", "f", "g", "i"),
                array(), 
                array()
            ))
            
            .add(testInMethod("in", true,
                array(),
                array("a", "l"),
                array("a", "c", "d", "i", "l"),
                array("a", "c", "d", "i", "l"),
                array("b", "c", "d", "e", "f", "g", "i"),
                array("j", "k"),
                array("c", "d", "e", "f", "g", "i"),
                array(), 
                array()
            ))
            
            .add(testComparisonMethod("lessThan", 
                array("h", "j"), 
                array("b", "h" , "j"), 
                array("a", "b", "h" , "j", "l"), 
                array("a", "b", "c", "d", "h" , "i", "j", "l"), 
                array("a", "b", "c", "d", "e", "f", "h" , "i", "j", "l"),
                array("j"), 
                array(), 
                array("a", "b", "c", "d", "e", "f", "g", "h" , "i", "j", "l"), 
                array("a", "b", "c", "d", "e", "f", "g", "h" , "i", "j", "l")
            ))
            
            .add(testComparisonMethod("lessOrEqual", 
                array("b", "h", "j"), 
                array("a", "b", "h" , "j", "l"), 
                array("a", "b", "c", "d", "h" , "i", "j", "l"), 
                array("a", "b", "c", "d", "e", "f", "h" , "i", "j", "l"),
                array("a", "b", "c", "d", "e", "f", "g", "h" , "i", "j", "l"),
                array("h", "j"), 
                array("j"), 
                array("a", "b", "c", "d", "e", "f", "g", "h" , "i", "j", "k", "l"), 
                array("a", "b", "c", "d", "e", "f", "g", "h" , "i", "j", "l")
            ))
               
            .add(testBetweenMethod("notBetween", 
                array("b", "e", "f", "g", "h", "j", "k"),
                array("e", "f", "g", "h", "j", "k"), 
                array("a", "b", "e", "f", "g", "h", "j", "k", "l"),
                array("b", "e", "f", "g", "h", "j", "k"),
                array("a", "b", "g", "h", "j", "k", "l"),
                array("b", "g", "h", "j", "k")
            ))
            
            .add(testComparisonMethod("notEqual", 
                array("a", "c", "d", "e", "f", "g", "h" , "i", "j", "k", "l"), 
                array("b", "c", "d", "e", "f", "g", "h" , "i", "j", "k"), 
                array("a", "b", "e", "f", "g", "h" , "j", "k", "l"), 
                array("a", "b", "c", "d", "g", "h" , "i", "j", "k", "l"),
                array("a", "b", "c", "d", "e", "f", "h" , "i", "j", "k", "l"),
                array("a", "b", "c", "d", "e", "f", "g", "i", "j", "k", "l"), 
                array("a", "b", "c", "d", "e", "f", "g", "h" , "i", "k", "l"), 
                array("a", "b", "c", "d", "e", "f", "g", "h" , "i", "j", "l"), 
                array("a", "b", "c", "d", "e", "f", "g", "h" , "i", "j", "k", "l")
            ))
            
            .add(testInMethod("notIn", false,
                array("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l"),
                array(     "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"     ),
                array(     "b",      "d", "e", "f", "g", "h",      "j", "k"     ),
                array(     "b",      "d", "e", "f", "g", "h",      "j", "k"     ),
                array("a",                               "h",      "j", "k", "l"),
                array("a", "b", "c", "d", "e", "f", "g", "h", "i",           "l"),
                array("a", "b",                          "h",      "j", "k", "l"),
                array("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l"),
                array("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l")
            ))
            
            .add(testInMethod("notIn", true,
                array("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l"),
                array(     "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"     ),
                array(     "b",      "d", "e", "f", "g", "h",      "j", "k"     ),
                array(     "b",      "d", "e", "f", "g", "h",      "j", "k"     ),
                array("a",                               "h",      "j", "k", "l"),
                array("a", "b", "c", "d", "e", "f", "g", "h", "i",           "l"),
                array("a", "b",                          "h",      "j", "k", "l"),
                array("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l"),
                array("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l")
            ))
            
                // array("a", "b", "c", "d", "e", "f", "g", "h" , "i", "j", "k", "l")
        ;
    }
    
    private Method testBetweenMethod(String name, String[] e0, String[] e1, String[] e2, String[] e3, String[] e4, String[] e5) {
        return Method.of("test" + ucfirst(name), void.class).public_()
            .add(AnnotationUsage.of(Test.class))
            .add(
                "// Create a number of predicates",
                "final Predicate<BasicEntity> t0 = field." + name + "(" + value("0") + ", " + value("2") + ");",
                "final Predicate<BasicEntity> t1 = field." + name + "(" + value("-2") + ", " + value("2") + ");",
                "final Predicate<BasicEntity> t2 = field." + name + "(" + value("0") + ", " + value("2") + ", Inclusion.START_EXCLUSIVE_END_EXCLUSIVE);",
                "final Predicate<BasicEntity> t3 = field." + name + "(" + value("0") + ", " + value("2") + ", Inclusion.START_INCLUSIVE_END_EXCLUSIVE);",
                "final Predicate<BasicEntity> t4 = field." + name + "(" + value("0") + ", " + value("2") + ", Inclusion.START_EXCLUSIVE_END_INCLUSIVE);",
                "final Predicate<BasicEntity> t5 = field." + name + "(" + value("0") + ", " + value("2") + ", Inclusion.START_INCLUSIVE_END_INCLUSIVE);",
                "",
                "// Create a number of expected results",
                "final List<BasicEntity> e0 = asList(" + Stream.of(e0).collect(joining(", ")) + ");",
                "final List<BasicEntity> e1 = asList(" + Stream.of(e1).collect(joining(", ")) + ");",
                "final List<BasicEntity> e2 = asList(" + Stream.of(e2).collect(joining(", ")) + ");",
                "final List<BasicEntity> e3 = asList(" + Stream.of(e3).collect(joining(", ")) + ");",
                "final List<BasicEntity> e4 = asList(" + Stream.of(e4).collect(joining(", ")) + ");",
                "final List<BasicEntity> e5 = asList(" + Stream.of(e5).collect(joining(", ")) + ");",
                "",
                "// Create a number of actual results",
                "final List<BasicEntity> a0 = entities.stream().filter(t0).collect(toList());",
                "final List<BasicEntity> a1 = entities.stream().filter(t1).collect(toList());",
                "final List<BasicEntity> a2 = entities.stream().filter(t2).collect(toList());",
                "final List<BasicEntity> a3 = entities.stream().filter(t3).collect(toList());",
                "final List<BasicEntity> a4 = entities.stream().filter(t4).collect(toList());",
                "final List<BasicEntity> a5 = entities.stream().filter(t5).collect(toList());",
                "",
                "// Test the results",
                "assertListEqual(\"Test 0: " + name + "(0, 2):\",                                a0, e0, FORMATTER);",
                "assertListEqual(\"Test 1: " + name + "(-2, 2):\",                               a1, e1, FORMATTER);",
                "assertListEqual(\"Test 2: " + name + "(0, 2, START_EXCLUSIVE_END_EXCLUSIVE):\", a2, e2, FORMATTER);",
                "assertListEqual(\"Test 3: " + name + "(0, 2, START_INCLUSIVE_END_EXCLUSIVE):\", a3, e3, FORMATTER);",
                "assertListEqual(\"Test 4: " + name + "(0, 2, START_EXCLUSIVE_END_INCLUSIVE):\", a4, e4, FORMATTER);",
                "assertListEqual(\"Test 5: " + name + "(0, 2, START_INCLUSIVE_END_INCLUSIVE):\", a5, e5, FORMATTER);"
            )
        ;
    }
    
    private Method testComparisonMethod(String name, String[] e0, String[] e1, String[] e2, String[] e3, String[] e4, String[] e5, String[] e6, String[] e7, String[] e8) {
        return Method.of("test" + ucfirst(name), void.class).public_()
            .add(AnnotationUsage.of(Test.class))
            .add(
                "// Create a number of predicates",
                "final Predicate<BasicEntity> t0 = field." + name + "(" + value("-1") + ");",
                "final Predicate<BasicEntity> t1 = field." + name + "(" + value("0") + ");",
                "final Predicate<BasicEntity> t2 = field." + name + "(" + value("1") + ");",
                "final Predicate<BasicEntity> t3 = field." + name + "(" + value("2") + ");",
                "final Predicate<BasicEntity> t4 = field." + name + "(" + value("3") + ");",
                "final Predicate<BasicEntity> t5 = field." + name + "(" + value("-5") + ");",
                "final Predicate<BasicEntity> t6 = field." + name + "(" + wrapper() + ".MIN_VALUE);",
                "final Predicate<BasicEntity> t7 = field." + name + "(" + wrapper() + ".MAX_VALUE);",
                "final Predicate<BasicEntity> t8 = field." + name + "(" + value("100") + ");",
                "",
                "// Create a number of expected results",
                "final List<BasicEntity> e0 = asList(" + Stream.of(e0).collect(joining(", ")) + ");",
                "final List<BasicEntity> e1 = asList(" + Stream.of(e1).collect(joining(", ")) + ");",
                "final List<BasicEntity> e2 = asList(" + Stream.of(e2).collect(joining(", ")) + ");",
                "final List<BasicEntity> e3 = asList(" + Stream.of(e3).collect(joining(", ")) + ");",
                "final List<BasicEntity> e4 = asList(" + Stream.of(e4).collect(joining(", ")) + ");",
                "final List<BasicEntity> e5 = asList(" + Stream.of(e5).collect(joining(", ")) + ");",
                "final List<BasicEntity> e6 = asList(" + Stream.of(e6).collect(joining(", ")) + ");",
                "final List<BasicEntity> e7 = asList(" + Stream.of(e7).collect(joining(", ")) + ");",
                "final List<BasicEntity> e8 = asList(" + Stream.of(e8).collect(joining(", ")) + ");",
                "",
                "// Create a number of actual results",
                "final List<BasicEntity> a0 = entities.stream().filter(t0).collect(toList());",
                "final List<BasicEntity> a1 = entities.stream().filter(t1).collect(toList());",
                "final List<BasicEntity> a2 = entities.stream().filter(t2).collect(toList());",
                "final List<BasicEntity> a3 = entities.stream().filter(t3).collect(toList());",
                "final List<BasicEntity> a4 = entities.stream().filter(t4).collect(toList());",
                "final List<BasicEntity> a5 = entities.stream().filter(t5).collect(toList());",
                "final List<BasicEntity> a6 = entities.stream().filter(t5).collect(toList());",
                "final List<BasicEntity> a7 = entities.stream().filter(t5).collect(toList());",
                "final List<BasicEntity> a8 = entities.stream().filter(t5).collect(toList());",
                "",
                "// Test the results",
                "assertListEqual(\"Test 0: " + name + "(-1):\",        a0, e0, FORMATTER);",
                "assertListEqual(\"Test 1: " + name + "(0):\",         a1, e1, FORMATTER);",
                "assertListEqual(\"Test 2: " + name + "(1):\",         a2, e2, FORMATTER);",
                "assertListEqual(\"Test 3: " + name + "(2):\",         a3, e3, FORMATTER);",
                "assertListEqual(\"Test 4: " + name + "(3):\",         a4, e4, FORMATTER);",
                "assertListEqual(\"Test 5: " + name + "(-5):\",        a5, e5, FORMATTER);",
                "assertListEqual(\"Test 6: " + name + "(MIN_VALUE):\", a6, e6, FORMATTER);",
                "assertListEqual(\"Test 7: " + name + "(MAX_VALUE):\", a7, e7, FORMATTER);",
                "assertListEqual(\"Test 8: " + name + "(100):\",       a8, e8, FORMATTER);"
            )
        ;
    }
    
    private Method testInMethod(String name, boolean set, String[] e0, String[] e1, String[] e2, String[] e3, String[] e4, String[] e5, String[] e6, String[] e7, String[] e8) {
        final String setName =  name + (set ? "Set" : "");
        return Method.of("test" + ucfirst(setName), void.class).public_()
            .add(AnnotationUsage.of(Test.class))
            .add(
                "// Create a number of predicates",
                "final Predicate<BasicEntity> t0 = field." + name + "(" + asSet(set) + ");",
                "final Predicate<BasicEntity> t1 = field." + name + "(" + asSet(set, "0") + ");",
                "final Predicate<BasicEntity> t2 = field." + name + "(" + asSet(set, "0", "1") + ");",
                "final Predicate<BasicEntity> t3 = field." + name + "(" + asSet(set, "0", "1", "1") + ");",
                "final Predicate<BasicEntity> t4 = field." + name + "(" + asSet(set, "-1", "1", "2", "3") + ");",
                "final Predicate<BasicEntity> t5 = field." + name + "(" + asSet(set, wrapper() + ".MIN_VALUE", wrapper() + ".MAX_VALUE") + ");",
                "final Predicate<BasicEntity> t6 = field." + name + "(" + asSet(set, "1", "2", "3", "4", "5") + ");",
                "final Predicate<BasicEntity> t7 = field." + name + "(" + asSet(set, "100", "101", "102", "103", "104") + ");",
                "final Predicate<BasicEntity> t8 = field." + name + "(" + asSet(set, "-100") + ");",
                "",
                "// Create a number of expected results",
                "final List<BasicEntity> e0 = asList(" + Stream.of(e0).collect(joining(", ")) + ");",
                "final List<BasicEntity> e1 = asList(" + Stream.of(e1).collect(joining(", ")) + ");",
                "final List<BasicEntity> e2 = asList(" + Stream.of(e2).collect(joining(", ")) + ");",
                "final List<BasicEntity> e3 = asList(" + Stream.of(e3).collect(joining(", ")) + ");",
                "final List<BasicEntity> e4 = asList(" + Stream.of(e4).collect(joining(", ")) + ");",
                "final List<BasicEntity> e5 = asList(" + Stream.of(e5).collect(joining(", ")) + ");",
                "final List<BasicEntity> e6 = asList(" + Stream.of(e6).collect(joining(", ")) + ");",
                "final List<BasicEntity> e7 = asList(" + Stream.of(e7).collect(joining(", ")) + ");",
                "final List<BasicEntity> e8 = asList(" + Stream.of(e8).collect(joining(", ")) + ");",
                "",
                "// Create a number of actual results",
                "final List<BasicEntity> a0 = entities.stream().filter(t0).collect(toList());",
                "final List<BasicEntity> a1 = entities.stream().filter(t1).collect(toList());",
                "final List<BasicEntity> a2 = entities.stream().filter(t2).collect(toList());",
                "final List<BasicEntity> a3 = entities.stream().filter(t3).collect(toList());",
                "final List<BasicEntity> a4 = entities.stream().filter(t4).collect(toList());",
                "final List<BasicEntity> a5 = entities.stream().filter(t5).collect(toList());",
                "final List<BasicEntity> a6 = entities.stream().filter(t5).collect(toList());",
                "final List<BasicEntity> a7 = entities.stream().filter(t5).collect(toList());",
                "final List<BasicEntity> a8 = entities.stream().filter(t5).collect(toList());",
                "",
                "// Test the results",
                "assertListEqual(\"Test 0: " + setName + "():\",                        a0, e0, FORMATTER);",
                "assertListEqual(\"Test 1: " + setName + "(0):\",                       a1, e1, FORMATTER);",
                "assertListEqual(\"Test 2: " + setName + "(0, 1):\",                    a2, e2, FORMATTER);",
                "assertListEqual(\"Test 3: " + setName + "(0, 1, 1):\",                 a3, e3, FORMATTER);",
                "assertListEqual(\"Test 4: " + setName + "(-1, 1, 2, 3):\",             a4, e4, FORMATTER);",
                "assertListEqual(\"Test 5: " + setName + "(MIN_VALUE, MAX_VALUE):\",    a5, e5, FORMATTER);",
                "assertListEqual(\"Test 6: " + setName + "(1, 2, 3, 4, 5):\",           a6, e6, FORMATTER);",
                "assertListEqual(\"Test 7: " + setName + "(100, 101, 102, 103, 104):\", a7, e7, FORMATTER);",
                "assertListEqual(\"Test 8: " + setName + "(-100):\",                    a8, e8, FORMATTER);"
            )
        ;
    }
    
    private String asSet(boolean set, String... values) {
        if (set) {
            switch (values.length) {
                case 0:
                    return "emptySet()";
                case 1:
                    return "singleton(" + value(values[0]) + ")";
                default:
                    return "Stream.of(" + Stream.of(values).map(this::value).collect(joining(", ")) + ").collect(toSet())";
            }
        } else {
            return Stream.of(values).map(this::value).collect(joining(", "));
        }
    }
    
    private String[] array(String... s) {
        return s;
    }
}