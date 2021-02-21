package zone.themcgamer.core.api.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.Getter;

import java.net.http.HttpResponse;

/**
 * This class represents a response from a {@link JsonRequest}
 *
 * @author Braydon
 */
@Getter
public class JsonResponse {
    private final HttpResponse<String> httpResponse;
    private final String json;
    private final JsonElement jsonElement;

    public JsonResponse(HttpResponse<String> httpResponse) {
        this.httpResponse = httpResponse;
        json = httpResponse.body();
        jsonElement = new JsonParser().parse(json);
    }
}