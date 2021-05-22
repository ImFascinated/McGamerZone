package zone.themcgamer.discordbot.events;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import zone.themcgamer.discordbot.BGSoftwareBot;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class MainGuildListener extends ListenerAdapter {
    private final BGSoftwareBot mgzBot;

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        Member member = event.getMember();
        User user = event.getUser();
        Guild guild = event.getGuild();
        if (user.isBot())
            return;
        if (!guild.getId().equals(zone.themcgamer.discordbot.guild.Guild.MAIN.getGuildId()))
            return;

    }
}