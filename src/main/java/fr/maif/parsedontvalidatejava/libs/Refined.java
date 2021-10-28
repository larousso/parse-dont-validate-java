package fr.maif.parsedontvalidatejava.libs;

import fr.maif.json.JsResult;
import fr.maif.json.JsonFormat;
import fr.maif.json.JsonRead;
import fr.maif.json.JsonWrite;

import java.util.function.Function;

import static fr.maif.json.JsonRead._string;
import static fr.maif.json.JsonWrite.$string;

public interface Refined<T> {

    T value();

    static <R extends Refined<String>> JsonFormat<R> refinedString(Function<String, R> creator) {
        return JsonFormat.of(read(_string(), creator), write($string()));
    }

    static <T, R extends Refined<T>> JsonFormat<R> format(JsonFormat<T> underlying, Function<T, R> creator) {
        return JsonFormat.of(read(underlying, creator), write(underlying));
    }

    static <T, R extends Refined<T>> JsonFormat<R> refined(JsonFormat<T> underlying, Function<T, R> creator) {
        return format(underlying, creator);
    }

    static <T, R extends Refined<T>> JsonWrite<R> write(JsonWrite<T> write) {
        return refined -> write.write(refined.value());
    }

    static <T, R extends Refined<T>> JsonRead<R> read(JsonRead<T> read, Function<T, R> creator) {
        return read.flatMapResult(value -> {
            try {
                return JsResult.success(creator.apply(value));
            } catch (Validations.ValidationException e) {
                return JsResult.error(e.errors.map(JsResult.Error::error));
            }
        });
    }
}
