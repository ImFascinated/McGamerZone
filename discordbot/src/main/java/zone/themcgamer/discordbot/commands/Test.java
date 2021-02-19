package zone.themcgamer.discordbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.doc.standard.Error;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

@CommandInfo(
        name = {"mycommand", "coolcommand"},
        description = "Use this command if you are cool! B)",
        requirements = {"The bot has all necessary permissions."})
@Error(
        prefix = "Test »",
        value = "Rip this command had an error.",
        response = "Invalid page number")
public class Test extends Command {
    @Override
    protected void execute(CommandEvent commandEvent) {

    }

    private OkHttpClient clientWithApiKey(String apiKey) {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request originalRequest = chain.request();
                    HttpUrl newUrl = originalRequest.url().newBuilder()
                            .addQueryParameter("key", apiKey).build();
                    Request request = originalRequest.newBuilder().url(newUrl).build();
                    return chain.proceed(request);
                }).build();
    }
}
