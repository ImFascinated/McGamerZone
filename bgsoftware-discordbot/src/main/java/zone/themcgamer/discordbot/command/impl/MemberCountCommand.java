package zone.themcgamer.discordbot.command.impl;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import zone.themcgamer.discordbot.command.BaseCommand;
import zone.themcgamer.discordbot.guild.Guild;
import zone.themcgamer.discordbot.utilities.EmbedUtils;

import java.util.Arrays;
import java.util.List;

public class MemberCountCommand extends BaseCommand {

    public MemberCountCommand() {
        name = "membercount";
        aliases = new String[]{"members"};
        help = "Shows the amount of members in the guild";
        cooldown = 10;
        arguments = "";
        guildOnly = true;
        guilds = Arrays.asList(Guild.MAIN, Guild.TEST);
    }

    @Override
    protected void execute(CommandEvent event, List<String> args) {
        net.dv8tion.jda.api.entities.Guild guild = event.getGuild();
        guild.loadMembers().onSuccess(members -> {
            EmbedBuilder embedBuilder = EmbedUtils.defaultEmbed();
            embedBuilder.setTitle("Member Count");
            embedBuilder.addField("Humans", String.valueOf(members.stream().filter(member -> !member.getUser().isBot()).count()), true);
            embedBuilder.addField("Bots", String.valueOf(members.stream().filter(member -> member.getUser().isBot()).count()), true);

            event.getChannel().sendMessage(embedBuilder.build()).queue();
        });
    }
}

