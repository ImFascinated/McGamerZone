package zone.themcgamer.core.discord.commands;

import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.data.jedis.cache.CacheRepository;
import zone.themcgamer.data.jedis.repository.RedisRepository;

public class LinkCommand {

    @Command(name = "link", description = "Link your discord account", playersOnly = true)
    public void onCommand(CommandProvider command) {

        //Check if user is linked
        //Here

        String token = "321dsa12";

        CacheRepository cacheRepository = RedisRepository.getRepository(CacheRepository.class).orElse(null);
    }

}
