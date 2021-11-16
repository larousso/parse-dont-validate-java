package fr.maif.parsedontvalidatejava.libs.db;

import io.r2dbc.spi.Row;

import java.time.LocalDateTime;

public interface NamedColReader<T> extends ColReader<T> {

    String col();
    Class<T> clazz();

    @Override
    default T read(Row row) {
        return row.get(col(), clazz());
    }

    static ColReader<String> stringCol(String colonne) {
        return (ColStringRead) () -> colonne;
    }

    static ColReader<Integer> intCol(String colonne) {
        return (ColIntRead) () -> colonne;
    }

    static ColReader<Double> doubleCol(String colonne) {
        return (DoubleIntRead) () -> colonne;
    }

    static ColReader<LocalDateTime> localDateTimeCol(String colonne) {
        return (ColLocalDateTimeRead) () -> colonne;
    }

    interface ColStringRead extends NamedColReader<String> {
        @Override
        default Class<String> clazz() {
            return String.class;
        }
    }

    interface ColIntRead extends NamedColReader<Integer> {
        @Override
        default Class<Integer> clazz() {
            return Integer.class;
        }
    }

    interface DoubleIntRead extends NamedColReader<Double> {
        @Override
        default Class<Double> clazz() {
            return Double.class;
        }
    }

    interface ColLocalDateTimeRead extends NamedColReader<LocalDateTime> {
        @Override
        default Class<LocalDateTime> clazz() {
            return LocalDateTime.class;
        }
    }
}
