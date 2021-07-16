package zone.themcgamer.data.jedis.repository.impl;

import zone.themcgamer.data.jedis.JedisController;
import zone.themcgamer.data.jedis.data.DiscordLink;
import zone.themcgamer.data.jedis.data.Node;
import zone.themcgamer.data.jedis.repository.RedisRepository;

import java.util.*;

/**
 * @author Braydon
 * @implNote This class serves the purpose of fetching all {@link Node}'s
 *           from Redis
 */
public class DiscordLinkRepository extends RedisRepository<String, DiscordLink> {
    public DiscordLinkRepository(JedisController controller) {
        super(controller, "discordLinking:*");
    }

    /*@Override
    public Optional<DiscordLink> lookup(UUID uuid) {
        return getCached().stream().filter(discordLink -> discordLink.getUuid().equals(uuid)).findFirst();
    }*/

    @Override
    public Optional<DiscordLink> lookup(String token) {
        return getCached().stream().filter(discordLink -> discordLink.getToken().equals(token)).findFirst();
    }

    @Override
    public String getKey(DiscordLink discordLink) {
        return "discordLinking:" + discordLink.getUuid();
    }

    @Override
    public Optional<DiscordLink> fromMap(Map<String, String> map) {
        return null;
    }

    @Override
    public long getExpiration(DiscordLink discordLink) {
        return discordLink.getTimestamp().getTime();
    }

    @Override
    public Map<String, Object> toMap(DiscordLink discordLink) {
        Map<String, Object> data = new HashMap<>();
        data.put("uuid", discordLink.getUuid());
        data.put("token", discordLink.getToken());
        data.put("timeCreated", discordLink.getTimestamp());
        return data;
    }


}
