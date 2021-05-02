package zone.themcgamer.core.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.NonNull;
import org.apache.commons.lang3.StringEscapeUtils;
import zone.themcgamer.core.api.json.JsonRequest;
import zone.themcgamer.core.api.json.JsonResponse;
import zone.themcgamer.data.ChatFilterLevel;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * @author Braydon
 */
public class WebAPI {
    public static final String API_KEY = "406321cc-48f3-454b-900a-972fd88e4882";
    private static final String URL = "https://api.mcgamerzone.net/v1";
    public static HttpClient HTTP_CLIENT;

    static {
        HTTP_CLIENT = java.net.http.HttpClient.newBuilder()
                .version(java.net.http.HttpClient.Version.HTTP_1_1)
                .followRedirects(java.net.http.HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(30L))
                .build();
    }

    public static String filterText(@NonNull String text, @NonNull ChatFilterLevel chatFilterLevel) {
        // TODO: 3/17/2021 debug this
        JsonResponse jsonResponse = new JsonRequest(URLEncoder.encode(URL + "/filter/" + text.replaceAll(" ", "%20") + "/" + chatFilterLevel.name(), StandardCharsets.UTF_8)).getResponse();
        JsonElement jsonElement = jsonResponse.getJsonElement();
        if (!(jsonElement instanceof JsonObject))
            return text;
        JsonObject jsonObject = (JsonObject) jsonElement;
        return jsonObject.get("value").getAsString();
    }
}