package zone.themcgamer.data.jedis.command.impl.discord;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DiscordUnlinkCommand {
    private final Long discordId;
}
