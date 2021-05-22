package zone.themcgamer.discordbot.events;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import zone.themcgamer.discordbot.BGSoftwareBot;
import zone.themcgamer.discordbot.BotConstants;
import zone.themcgamer.discordbot.utilities.EmbedUtils;
import zone.themcgamer.discordbot.utilities.GuildUtils;
import zone.themcgamer.discordbot.utilities.Responses;

import javax.annotation.Nonnull;
import java.util.Arrays;

@RequiredArgsConstructor
public class GuildListener extends ListenerAdapter {
    private final BGSoftwareBot main;

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        Member member = event.getMember();
        String contentRaw = event.getMessage().getContentRaw().toLowerCase();
        String name = event.getChannel().getName();

        if (member == null)
            return;
        if (member.getUser().isBot())
            return;
        if (GuildUtils.hasRole(member, BotConstants.SUPPORT_TEAM) == null)
            return;

        for (Responses value : Responses.values()) {
            if (Arrays.asList(value.getTriggerWords()).contains(contentRaw)) {
                if (!value.isRequireHelpCategory())
                    return;
                if (!value.getRequiredChannel().equals(event.getChannel().getId()))
                    return;
                EmbedBuilder embedBuilder = EmbedUtils.defaultEmbed();
                embedBuilder.setTitle(value.getTitle().replace("{plugin}", (isSupportCategory(event.getMessage().getCategory()) ? name : value.getDefaultReplace())));
                embedBuilder.setDescription(value.getDescription().replace("{plugin}", (isSupportCategory(event.getMessage().getCategory()) ? name : value.getDefaultReplace())));
                event.getMessage().reply(embedBuilder.build()).queue();
                break;
            }
        }
    }

    protected boolean isSupportCategory(Category category) {
        if (category == null)
            return false;
        return (category.getId().equals(BotConstants.SUPPORT_CATEGORY));
    }
}
