package zone.themcgamer.data.jedis.command.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import zone.themcgamer.data.jedis.command.JedisCommand;

@RequiredArgsConstructor @Getter
public class StaffChatCommand extends JedisCommand {
    private final String prefix, username, server, message;
}