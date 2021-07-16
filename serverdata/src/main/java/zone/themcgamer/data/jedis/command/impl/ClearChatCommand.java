package zone.themcgamer.data.jedis.command.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import zone.themcgamer.data.jedis.command.JedisCommand;

@RequiredArgsConstructor @Getter
public class ClearChatCommand extends JedisCommand {
    private final String serverId;
    private final Integer lines;
}