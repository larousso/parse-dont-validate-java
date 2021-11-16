package fr.maif.parsedontvalidatejava.libs;

import com.fasterxml.jackson.databind.node.DoubleNode;
import fr.maif.json.JsResult;
import fr.maif.json.JsonRead;
import fr.maif.json.JsonWrite;
import io.vavr.control.Try;

public class Jsons {

    public static JsonRead<Double> _double() {
        return json -> Try.of(() -> json.asDouble())
                .map(JsResult::success)
                .getOrElseGet(e -> JsResult.error(JsResult.Error.error("Double expected")));
    }

    public static JsonWrite<Double> $double() {
        return d -> new DoubleNode(d);
    }
}
