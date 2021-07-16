package zone.themcgamer.discordbot.command.impl;

import com.jagrosh.jdautilities.command.CommandEvent;
import zone.themcgamer.data.jedis.cache.CacheRepository;
import zone.themcgamer.data.jedis.cache.impl.PlayerStatusCache;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.discordbot.MGZBot;
import zone.themcgamer.discordbot.command.BaseCommand;
import zone.themcgamer.discordbot.guild.Guild;
import zone.themcgamer.discordbot.utilities.EmbedUtils;

import java.util.Arrays;
import java.util.List;

public class OnlineCommand  extends BaseCommand {

    public OnlineCommand() {
        name = "online";
        aliases = new String[]{"players"};
        help = "Get the amount of online players in the network";
        cooldown = 30;
        guildOnly = true;
        guilds = Arrays.asList(Guild.MAIN, Guild.TEAM, Guild.TEST);
    }

    @Override
    protected void execute(CommandEvent event, List<String> args) {
        CacheRepository cacheRepository = RedisRepository.getRepository(CacheRepository.class).orElse(null);

        if (cacheRepository == null) {
            event.reply(EmbedUtils.errorEmbed().setDescription("This command is unavailble right now!").build());
            return;
        }

        int online = Math.toIntExact(cacheRepository.getCached().stream().filter(cacheItem -> cacheItem instanceof PlayerStatusCache).count());
        event.reply(EmbedUtils.defaultEmbed().setDescription("There are currently " + online + " players online!").build());
    }
}
