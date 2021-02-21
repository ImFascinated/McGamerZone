package zone.themcgamer.api.route;

import com.google.common.base.Strings;
import spark.Request;
import spark.Response;
import zone.themcgamer.api.APIException;
import zone.themcgamer.api.APIVersion;
import zone.themcgamer.api.RestPath;
import zone.themcgamer.common.EnumUtils;
import zone.themcgamer.data.APIAccessLevel;
import zone.themcgamer.data.ChatFilterLevel;
import zone.themcgamer.data.jedis.data.APIKey;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This route handles everything associated with the chat
 *
 * @author Braydon
 */
public class ChatRoute {
    private static final Map<ChatFilterLevel, List<String>> filteredWords = new HashMap<>() {{
        put(ChatFilterLevel.LOW, Arrays.asList(
                "ass"
        ));
        put(ChatFilterLevel.MEDIUM, Arrays.asList(
                "fuck", "shit"
        ));
        put(ChatFilterLevel.HIGH, Arrays.asList(
                "nigger"
        ));
    }};

    /**
     * This path handles filtering the provided text with the provided {@link ChatFilterLevel}
     */
    @RestPath(path = "/filter/:text/:level", version = APIVersion.V1, accessLevel = APIAccessLevel.DEV)
    public String filterText(Request request, Response response, APIKey apiKey) throws APIException {
        String text = request.params(":text");
        ChatFilterLevel chatFilterLevel = EnumUtils.fromString(ChatFilterLevel.class, request.params(":level").toUpperCase());
        if (chatFilterLevel == null)
            throw new APIException("Invalid filter level, valid options: " + Arrays.stream(ChatFilterLevel.values()).map(ChatFilterLevel::name).collect(Collectors.joining(", ")));
        String filteredMessage = text;
        text = text.replace("{dot}", ".");
        if (chatFilterLevel.isUrls()) {
            // TODO: 2/20/21 filter urls with regex patterns
        }
        if (chatFilterLevel.isIps()) {
            // TODO: 2/20/21 filter ips with regex patterns
        }
        List<String> filteredWords = new ArrayList<>();
        for (ChatFilterLevel filterLevel : ChatFilterLevel.values()) {
            if (chatFilterLevel.ordinal() >= filterLevel.ordinal()) {
                List<String> list = ChatRoute.filteredWords.getOrDefault(filterLevel, new ArrayList<>());
                if (list.isEmpty())
                    continue;
                filteredWords.addAll(list);
            }
        }
        for (String word : text.split(" ")) {
            for (String filteredWord : filteredWords) {
                if (word.toLowerCase().contains(filteredWord.toLowerCase())) {
                    filteredMessage = filteredMessage.replace(word, Strings.repeat("*", word.length()));
                }
            }
        }
        return filteredMessage;
    }
}