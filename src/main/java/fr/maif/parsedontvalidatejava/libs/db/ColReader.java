package fr.maif.parsedontvalidatejava.libs.db;

import io.r2dbc.spi.Row;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface ColReader<T> {

    T read(Row row);

    default <R> ColReader<R> map(Function<T, R> map) {
        return row -> map.apply(this.read(row));
    }

    default <R, S> ColReader<S> and(ColReader<R> reader, BiFunction<T, R, S> map) {
        return row -> map.apply(this.read(row), reader.read(row));
    }

    static <R, T> ColReader<R> col(ColReader<T> reader, Function<T, R> mapper) {
        return reader.map(mapper);
    }

}
