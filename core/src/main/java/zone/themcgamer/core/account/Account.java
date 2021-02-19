package zone.themcgamer.core.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import zone.themcgamer.data.Rank;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author Braydon
 * @implNote The account implemention for {@link Account}
 */
@AllArgsConstructor @Setter @Getter @ToString
public class Account {
    private final int id;
    private final UUID uuid;
    private final String name;
    private Rank primaryRank;
    private Rank[] secondaryRanks;
    private final double gold, gems;
    private final String lastEncryptedIpAddress, ipAddress, encryptedIpAddress;
    private final long firstLogin, lastLogin;

    public String getPrimaryRankName() {
        return primaryRank.getDisplayName();
    }

    public String[] getSecondaryRanksNames() {
        String[] rankNames = new String[secondaryRanks.length];
        for (int i = 0; i < secondaryRanks.length; i++)
            rankNames[i] = secondaryRanks[i].getDisplayName();
        return rankNames;
    }

    public String getDisplayName() {
        return primaryRank.getColor() + name;
    }

    /**
     * Check whether or not the player has the provided {@link Rank}
     * @param rank the rank to check
     * @return if the player has the rank
     */
    public boolean hasRank(Rank rank) {
        if (rank.ordinal() < primaryRank.ordinal()) // If the rank provided is above the player's rank, we return false
            return false;
        boolean checkSecondary = false;
        // If the player's primary rank is a staff rank, and the rank to check is a donator rank, we skip over
        // the primary rank checking and move onto the secondary rank checking
        if ((primaryRank.getCategory() == Rank.RankCategory.STAFF && rank.getCategory() == Rank.RankCategory.DONATOR)
                || rank.getCategory() == Rank.RankCategory.SUB) {
            checkSecondary = true;
        }
        // If we aren't checking the secondary ranks, we check if the rank being checked is higher than the player's
        // rank and return the value
        if (!checkSecondary)
            return rank.ordinal() >= primaryRank.ordinal();
        List<Rank> secondaryRanks = Arrays.asList(this.secondaryRanks);
        if (rank.getCategory() == Rank.RankCategory.DONATOR) {
            int index = rank.ordinal();
            if (index > 0) { // If the rank index is above 0, we're able to fetch the previous rank in the list
                Rank previousRank = Rank.values()[index - 1];
                // If the rank before the rank being checked is a donator rank, then we check if the player has
                // the previous rank. If the player doesn't have the previous rank, but they have the rank being
                // checked, we return true as they have the rank, otherwise return the hasPrevious value
                if (previousRank.getCategory() == Rank.RankCategory.DONATOR) {
                    boolean hasPrevious = hasRank(previousRank);
                    if (!hasPrevious && secondaryRanks.contains(rank))
                        return true;
                    return hasPrevious;
                }
            }
        }
        return secondaryRanks.contains(rank);
    }
}