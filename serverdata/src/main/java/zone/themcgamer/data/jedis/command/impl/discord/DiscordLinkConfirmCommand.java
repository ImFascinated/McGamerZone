package zone.themcgamer.data.jedis.command.impl.discord;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class DiscordLinkConfirmCommand {
    private final UUID uuid;
    private final Long discordId;
}
