package com.speedment.sources;

import com.speedment.sources.pattern.BetweenPredicatePattern;
import com.speedment.sources.pattern.EqualPredicatePattern;
import com.speedment.sources.pattern.FieldComparatorImplPattern;
import com.speedment.sources.pattern.FieldComparatorPattern;
import com.speedment.sources.pattern.FieldImplPattern;
import com.speedment.sources.pattern.FieldPattern;
import com.speedment.sources.pattern.ForeignKeyFieldImplPattern;
import com.speedment.sources.pattern.ForeignKeyFieldPattern;
import com.speedment.sources.pattern.GetterPattern;
import com.speedment.sources.pattern.GreaterOrEqualPredicatePattern;
import com.speedment.sources.pattern.GreaterThanPredicatePattern;
import com.speedment.sources.pattern.HasValuePattern;
import com.speedment.sources.pattern.InPredicatePattern;
import com.speedment.sources.pattern.SetToImplPattern;
import com.speedment.sources.pattern.SetToPattern;
import com.speedment.sources.pattern.SetterPattern;
import com.speedment.common.codegen.Generator;
import com.speedment.common.codegen.controller.AutoImports;
import com.speedment.common.codegen.internal.java.JavaGenerator;
import com.speedment.common.codegen.internal.util.Formatting;
import com.speedment.common.codegen.model.File;
import com.speedment.sources.pattern.BooleanFieldImplPattern;
import com.speedment.sources.pattern.BooleanFieldPattern;
import com.speedment.sources.pattern.FindFromPattern;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

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
            .resolve("runtime")
            .resolve("src")
            .resolve("main")
            .resolve("java");
        
        if (!Files.exists(srcPath)) {
            System.err.println("Could not find java sources folder '" + srcPath.toString() + "'.");
            System.exit(-3);
        }
        
        System.out.println("Building Code Patterns...");
        
        Formatting.tab("    ");
        final Set<Pattern> patterns = new HashSet<>();
        install(patterns, BetweenPredicatePattern::new);
        install(patterns, EqualPredicatePattern::new);
        install(patterns, FieldComparatorImplPattern::new);
        install(patterns, FieldComparatorPattern::new);
        install(patterns, FieldImplPattern::new);
        install(patterns, FieldPattern::new);
        install(patterns, FindFromPattern::new);
        install(patterns, ForeignKeyFieldImplPattern::new);
        install(patterns, ForeignKeyFieldPattern::new);
        install(patterns, GetterPattern::new);
        install(patterns, GreaterOrEqualPredicatePattern::new);
        install(patterns, GreaterThanPredicatePattern::new);
        install(patterns, HasValuePattern::new);
        install(patterns, InPredicatePattern::new);
        install(patterns, SetToPattern::new);
        install(patterns, SetToImplPattern::new);
        install(patterns, SetterPattern::new);
        
        // Boolean types
        patterns.add(new GetterPattern(Boolean.class, boolean.class));
        patterns.add(new SetterPattern(Boolean.class, boolean.class));
        patterns.add(new SetToPattern(Boolean.class, boolean.class));
        patterns.add(new SetToImplPattern(Boolean.class, boolean.class));
        patterns.add(new HasValuePattern(Boolean.class, boolean.class));
        patterns.add(new BooleanFieldPattern(Boolean.class, boolean.class));
        patterns.add(new BooleanFieldImplPattern(Boolean.class, boolean.class));
        patterns.add(new ForeignKeyFieldPattern(Boolean.class, boolean.class));
        
        final Generator gen = new JavaGenerator();
        final AtomicInteger counter = new AtomicInteger();

        System.out.println("Generating Sources...");
        patterns.forEach(pattern -> {
            final String packageName = pattern.getFullClassName();
            final String fileName    = pattern.getClassName() + ".java";
            final File file          = File.of(packageName + ".java");
            
            Path currentPath = srcPath;
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
        
        System.out.println("All " + counter.get() + " files was created successfully.");
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
