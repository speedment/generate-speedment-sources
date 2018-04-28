package com.speedment.sources.pattern.tuple;

import java.util.function.Supplier;

/**
 *
 * @author Per Minborg
 */
public enum TupleType {

    IMMUTABLE, IMMUTABLE_NULLABLE, MUTABLE;

    public <T> T eval(T immutable, T nullable, T mutable) {
        switch (this) {
            case IMMUTABLE:
                return immutable;
            case IMMUTABLE_NULLABLE:
                return nullable;
            case MUTABLE:
                return mutable;
        }
        throw new UnsupportedOperationException();
    }

    public <T> T eval(Supplier<T> immutable, Supplier<T> nullable, Supplier<T> mutable) {
                switch (this) {
            case IMMUTABLE:
                return immutable.get();
            case IMMUTABLE_NULLABLE:
                return nullable.get();
            case MUTABLE:
                return mutable.get();
        }
        throw new UnsupportedOperationException();
    }

}
