package com.speedment.sources;

import com.speedment.common.codegen.Generator;
import com.speedment.common.codegen.controller.AutoImports;
import com.speedment.common.codegen.internal.java.JavaGenerator;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Javadoc;
import com.speedment.common.codegen.util.Formatting;
import com.speedment.sources.pattern.*;
import com.speedment.sources.pattern.function.*;
import com.speedment.sources.pattern.tuple.*;
import com.speedment.sources.pattern.tuple.test.TupleImplTestPattern;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

import static com.speedment.sources.pattern.tuple.TupleUtil.MAX_DEGREE;
import static java.util.stream.Collectors.joining;

/**
 * The main entry point of the program.
 *
 * @author Emil Forslund
 */
public final class Main {

    /**
     * The main entry point for the application.
     * <p>
     * The program requires the base directory of the Speedment sources to be
     * specified as a command-line parameter in the following way:
     * <em>Example:</em>
     * {@code java -jar generate-speedment-sources.jar C:/Users/Emil/Documents/GitHub/speedment}
     *
     * @param param
     */
    public final static void main(String... param) {

        if (param.length < 1) {
            System.err.println("Expected command line parameter 'Speedment base directory'.");
            System.exit(-1);
        }

        final Path basePath = Paths.get(param[0]);
        if (!Files.exists(basePath)) {
            System.err.println("Could not find specified base directory '" + param[0] + "'.");
            System.exit(-2);
        }

        final Path srcPath = basePath
            .resolve("runtime-parent")
            .resolve("runtime-field")
            .resolve("src");

        final Path mainJava = srcPath
            .resolve("main")
            .resolve("java");

        final Path testJava = srcPath
            .resolve("test")
            .resolve("java");

        final Path srcPathTuple = basePath
            .resolve("common-parent")
            .resolve("tuple")
            .resolve("src");

        final Path srcPathFunction = basePath
            .resolve("common-parent")
            .resolve("function")
            .resolve("src");

        final Path tupleMainJava = srcPathTuple
            .resolve("main")
            .resolve("java");

        final Path tupleTestJava = srcPathTuple
            .resolve("test")
            .resolve("java");

        final Path functionMainJava = srcPathFunction
            .resolve("main")
            .resolve("java");

        final Path functionTestJava = srcPathFunction
            .resolve("test")
            .resolve("java");

        if (!Files.exists(mainJava)) {
            System.err.println("Could not find java sources folder '" + mainJava.toString() + "'.");
            System.exit(-3);
        }

        if (!Files.exists(tupleMainJava)) {
            System.err.println("Could not find java sources folder '" + tupleMainJava.toString() + "'.");
            System.exit(-3);
        }

        if (!Files.exists(functionMainJava)) {
            System.err.println("Could not find java sources folder '" + functionMainJava.toString() + "'.");
            System.exit(-3);
        }

        final Path licenseHeaderPath = basePath.resolve("license_header.txt");
        if (!Files.exists(licenseHeaderPath)) {
            System.err.println("Could not find the license_header.txt-file in the root folder '" + basePath + "'.");
            System.exit(-4);
        }

        final String licenseHeader;
        try {
            licenseHeader = Files.lines(licenseHeaderPath)
                .map(replace("currentYear", Integer.toString(Calendar.getInstance().get(Calendar.YEAR))))
                .collect(joining(Formatting.nl()));
        } catch (final IOException ex) {
            throw new RuntimeException("Error loading license header '" + licenseHeaderPath + "'.", ex);
        }

        System.out.println("Building Code Patterns...");

        Formatting.tab("    ");
        final Set<Pattern> patterns = new HashSet<>();
        install(patterns, BetweenPredicatePattern::new);
        install(patterns, NotBetweenPredicatePattern::new);
        install(patterns, EqualPredicatePattern::new);
        install(patterns, NotEqualPredicatePattern::new);
        install(patterns, FieldComparatorImplPattern::new);
        install(patterns, FieldComparatorPattern::new);
        install(patterns, GetPattern::new);
        install(patterns, GetImplPattern::new);
        install(patterns, FieldImplPattern::new);
        install(patterns, FieldPattern::new);
        install(patterns, FieldTestPattern::new);
        install(patterns, FindFromPattern::new);
        install(patterns, ForeignKeyFieldImplPattern::new);
        install(patterns, ForeignKeyFieldPattern::new);
        install(patterns, GetterPattern::new);
        install(patterns, GreaterOrEqualPredicatePattern::new);
        install(patterns, GreaterThanPredicatePattern::new);
        install(patterns, LessOrEqualPredicatePattern::new);
        install(patterns, LessThanPredicatePattern::new);
        install(patterns, HasValuePattern::new);
        install(patterns, InPredicatePattern::new);
        install(patterns, NotInPredicatePattern::new);
        install(patterns, SetToPattern::new);
        install(patterns, SetToImplPattern::new);
        install(patterns, SetterPattern::new);

        // Boolean types
        patterns.add(new GetterPattern(Boolean.class, boolean.class));
        patterns.add(new GetPattern(Boolean.class, boolean.class));
        patterns.add(new GetImplPattern(Boolean.class, boolean.class));
        patterns.add(new SetterPattern(Boolean.class, boolean.class));
        patterns.add(new SetToPattern(Boolean.class, boolean.class));
        patterns.add(new SetToImplPattern(Boolean.class, boolean.class));
        patterns.add(new HasValuePattern(Boolean.class, boolean.class));
        patterns.add(new BooleanFieldPattern(Boolean.class, boolean.class));
        patterns.add(new BooleanFieldImplPattern(Boolean.class, boolean.class));
        patterns.add(new EqualPredicatePattern(Boolean.class, boolean.class));
        patterns.add(new NotEqualPredicatePattern(Boolean.class, boolean.class));

        final Set<Pattern> tuplePatterns = new HashSet<>();
        tuplePatterns.add(new TuplesPattern());
        tuplePatterns.add(new TuplesOfNullablesPattern());
        tuplePatterns.add(new TupleBuilderPattern());

        final Set<Pattern> functionPatterns = new HashSet<>();
        install(functionPatterns, FunctionPattern::new);
        install(functionPatterns, ToFunctionPattern::new);
        install(functionPatterns, ObjConsumer::new);
        install(functionPatterns, ToLongCollectorPattern::new);
        install(functionPatterns, ToDoubleCollectorPattern::new);
        install(functionPatterns, LongToFunctionPattern::new);
        functionPatterns.add(new FunctionPattern(Boolean.class, boolean.class));
        functionPatterns.add(new ToFunctionPattern(Boolean.class, boolean.class));
        functionPatterns.add(new ObjConsumer(Boolean.class, boolean.class));
        functionPatterns.add(new ToLongCollectorPattern(Boolean.class, boolean.class));
        functionPatterns.add(new ToDoubleCollectorPattern(Boolean.class, boolean.class));
        functionPatterns.add(new LongToFunctionPattern(Boolean.class, boolean.class));

        IntStream.range(0, MAX_DEGREE)
            .mapToObj(i -> new TupleImplPattern(i, false))
            .forEachOrdered(tuplePatterns::add);

        IntStream.range(0, MAX_DEGREE)
            .mapToObj(i -> new TupleImplPattern(i, true))
            .forEachOrdered(tuplePatterns::add);

        IntStream.range(0, MAX_DEGREE)
            .mapToObj(i -> new TuplePattern(i, false))
            .forEachOrdered(tuplePatterns::add);

        IntStream.range(0, MAX_DEGREE)
            .mapToObj(i -> new TuplePattern(i, true))
            .forEachOrdered(tuplePatterns::add);

        IntStream.range(0, MAX_DEGREE)
            .mapToObj(i -> new TupleMapperImplPattern(i, false))
            .forEachOrdered(tuplePatterns::add);

        IntStream.range(0, MAX_DEGREE)
            .mapToObj(i -> new TupleMapperImplPattern(i, true))
            .forEachOrdered(tuplePatterns::add);

        IntStream.range(0, MAX_DEGREE)
            .mapToObj(i -> new TupleImplTestPattern(i, false))
            .forEachOrdered(tuplePatterns::add);

        final AtomicInteger counter = new AtomicInteger();

        System.out.println("Generating Sources...");

        generate(patterns, mainJava, testJava, licenseHeader, counter);
        generate(tuplePatterns, tupleMainJava, tupleTestJava, licenseHeader, counter);
        generate(functionPatterns, functionMainJava, functionTestJava, licenseHeader, counter);

        System.out.println("All " + counter.get() + " files was created successfully.");
    }

    private static void generate(
        final Set<Pattern> patterns,
        final Path mainJava,
        final Path testJava,
        final String licenseHeader,
        final AtomicInteger counter
    ) {
        final Generator gen = new JavaGenerator();
        patterns.forEach(pattern -> {
            final String packageName = pattern.getFullClassName();
            final String fileName = pattern.getClassName() + ".java";
            final File file = File.of(packageName + ".java");

            file.set(Javadoc.of(licenseHeader));
            Path currentPath = pattern.isTestClass() ? testJava : mainJava;

            final String[] folders = packageName.split("\\.");

            for (int i = 0; i < folders.length - 1; i++) {
                final String folder = folders[i];
                currentPath = currentPath.resolve(folder);
            }

            currentPath = currentPath.resolve(fileName);

            file.add(pattern.make(file));
            file.call(new AutoImports(gen.getDependencyMgr()));

            System.out.println("Creating " + currentPath);
            final String code = gen.on(file).get();
            final byte[] bytes = code.getBytes(StandardCharsets.UTF_8);

            try {
                Files.write(currentPath, bytes,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
                );

                counter.incrementAndGet();
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private static UnaryOperator<String> replace(String key, String value) {
        return line -> line.replace("${" + key + "}", value);
    }

    private static void install(Set<Pattern> patterns, BiFunction<Class<?>, Class<?>, ? extends Pattern> patternFactory) {
        patterns.add(patternFactory.apply(Byte.class, byte.class));
        patterns.add(patternFactory.apply(Short.class, short.class));
        patterns.add(patternFactory.apply(Integer.class, int.class));
        patterns.add(patternFactory.apply(Long.class, long.class));
        patterns.add(patternFactory.apply(Float.class, float.class));
        patterns.add(patternFactory.apply(Double.class, double.class));
        patterns.add(patternFactory.apply(Character.class, char.class));
    }
}
