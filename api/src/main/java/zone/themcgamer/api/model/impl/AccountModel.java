package zone.themcgamer.api.model.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import zone.themcgamer.api.model.IModel;
import zone.themcgamer.data.Rank;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Braydon
 */
@AllArgsConstructor @Setter @Getter @ToString
public class AccountModel implements IModel {
    private final int id;
    private final UUID uuid;
    private final String name;
    private final Rank primaryRank;
    private final Rank[] secondaryRanks;
    private final double gold, gems;
    private String encryptedIpAddress;
    private final long firstLogin, lastLogin;
    private long timeCached;

    @Override
    public HashMap<String, Object> toMap() {
        return new HashMap<>() {{
            put("id", id);
            put("uuid", uuid);
            put("name", name);
            put("primaryRank", primaryRank.name());
            put("secondaryRanks", Arrays.stream(secondaryRanks).map(Rank::name).collect(Collectors.joining(", ")));
            put("gold", gold);
            put("gems", gems);
            put("encryptedIpAddress", encryptedIpAddress);
            put("firstLogin", firstLogin);
            put("lastLogin", lastLogin);
            put("timeCached", timeCached);
        }};
    }
}