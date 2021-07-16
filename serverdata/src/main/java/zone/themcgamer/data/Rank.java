package zone.themcgamer.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Braydon
 */
@AllArgsConstructor @RequiredArgsConstructor @Getter
public enum Rank {
    // NOTE: It's important that the constant names are NOT changed during production as player's with the rank that
    // was changed will have the default rank instead of the rank they originally had

    // Staff Ranks
    OWNER("Owner", "Owners are the founders of the network. \nOwners manage different portions of the network, \nand they also ensure the best quality.", "§6", "§6§lOwner", RankCategory.STAFF),
    MANAGER("Manager", "Managers work to make sure that everything \non the network including staff members and \nnetwork quality is going as it should.", "§e", "§e§lManager", RankCategory.STAFF),
    DEVELOPER("Dev", "Developers are those who create things for \nthe network. Developers work together to ensure \nthe best quality for players of the network. \nWithout developers, this message wouldn't exist!", "§9", "§9§lDev", RankCategory.STAFF),
    JR_DEVELOPER("Jr.Dev", "Junior Developers are developers who do not \nhave full access to the internals of the network, \nbut they are able to create things using \nthe network's API.", "§9", "§9§lJr.Dev", RankCategory.STAFF),
    ADMIN("Admin", "Administrators are staff members that focus on \na specific portion of the network, and they \nall make sure everything is going as they should.", "§c", "§c§lAdmin", RankCategory.STAFF),
    MODERATOR("Mod", "Moderators are staff members who were \npromoted from Helper, and they have more permissions \nthan they did as a Helper.", "§2", "§2§lMod", RankCategory.STAFF),
    HELPER("Helper", "The Helper rank is the first staff moderation \nrank given to somebody when they join the \nstaff team. Think of them as trial moderators \nas they work their way up to Moderator.", "§a", "§a§lHelper", RankCategory.STAFF),

    // Other
    SR_BUILDER("Sr.Builder", "The Sr.Builder rank is given to those \nwho are considered head builders. They \nare more experienced builders, and they look over \nwhat the rest of the build team does.", "§3", "§3§lSr.Builder", RankCategory.OTHER),
    BUILDER("Builder", "Builders are those who work with other \nBuilders to create amazing maps for you \nto play on. Without them, the world you're in \nprobably wouldn't exist.", "§b", "§b§lBuilder", RankCategory.OTHER),
    PARTNER("Partner", "The Partner rank is given to those who \nare affiliated with those who partner \nwith McGamerZone.", "§d", "§d§lPartner", RankCategory.OTHER),
    YOUTUBER("YouTuber", "YouTubers are content creators that record \nor stream content on McGamerZone that \ngets uploaded to YouTube. Say hi if \nyou see them around, you may be on YouTube!", "§c", "§c§lYouTuber", RankCategory.OTHER),

    // Donor Ranks
    // TODO - Come up with creative descriptions for donor ranks.
    ULTIMATE("Ultimate", "The fifth purchasable rank found at store.mcgamerzone.net", "§5", "§5§lUltimate", RankCategory.DONATOR),
    EXPERT("Expert", "The fourth purchasable rank found at store.mcgamerzone.net", "§b", "§b§lExpert", RankCategory.DONATOR),
    HERO("Hero", "The third purchasable rank found at store.mcgamerzone.net", "§e", "§e§lHero", RankCategory.DONATOR),
    SKILLED("Skilled", "The second purchasable rank found at store.mcgamerzone.net", "§6", "§6§lSkilled", RankCategory.DONATOR),
    GAMER("Gamer", "The first purchasable rank found at store.mcgamerzone.net", "§2", "§2§lGamer", RankCategory.DONATOR),

    DEFAULT("None", "Default player rank", "§7", "§7None", RankCategory.OTHER),

    // Sub Ranks
    BETA("Beta", "A beta tester for McGamerZone", "§5", null, "§5Beta", RankCategory.SUB);

    private final String displayName, description, color, prefix;
    private String suffix;
    private RankCategory category;

    Rank(String displayName, String description, String color, String prefix, RankCategory category) {
        this(displayName, description, color, prefix, null, category);
    }

    public String getNametag() {
        String prefix = color;
        if (hasPrefix() && (category == RankCategory.STAFF || category == RankCategory.OTHER) && this != DEFAULT)
            prefix = this.prefix + " §7";
        return prefix;
    }

    public boolean hasPrefix() {
        return prefix != null;
    }

    public boolean hasSuffix() {
        return suffix != null;
    }

    /**
     * Lookup a rank by a string (constant name or display name)
     * @param s the string to use to lookup
     * @return the optional rank
     */
    public static Optional<Rank> lookup(String s) {
        return Arrays.stream(values())
                .filter(rank -> rank.name().equalsIgnoreCase(s) || rank.getDisplayName().equalsIgnoreCase(s))
                .findFirst();
    }

    public enum RankCategory {
        STAFF, DONATOR, SUB, OTHER
    }
}