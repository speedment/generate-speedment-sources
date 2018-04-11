package com.speedment.sources.pattern.function;

import com.speedment.common.codegen.constant.SimpleType;
import java.lang.reflect.Type;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;

/**
 *
 * @author Per Minborg
 */
public final class FunctionUtil {

    private FunctionUtil() {
    }

    public static Type objXConsumer(String ucPrimitive) {
        switch (ucPrimitive) {
            case "Long":
                return ObjLongConsumer.class;
            case "Int":
                return ObjIntConsumer.class;
            case "Double":
                return ObjDoubleConsumer.class;
            default:
                return SimpleType.create("com.speedment.common.function.Obj" + ucPrimitive + "Consumer");
        }

    }

}
