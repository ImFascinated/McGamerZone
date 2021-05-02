package zone.themcgamer.core.nametag.protocol;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import zone.themcgamer.core.common.ServerUtils;
import zone.themcgamer.core.common.ServerVersion;
import zone.themcgamer.core.nametag.NametagHandler;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Braydon (credits: https://github.com/sgtcaze/NametagEdit)
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class PacketWrapper {
    private static Constructor<?> CHAT_COMPONENT_CONSTRUCTOR;
    private static Class<? extends Enum> CHAT_FORMAT_TYPE;

    static {
        if (ServerVersion.getVersion().isNativeVersion()) {
            String version = ServerVersion.NMS_VERSION;
            try {
                Class<?> typeChatComponentText = Class.forName("net.minecraft.server." + version + ".ChatComponentText");
                CHAT_COMPONENT_CONSTRUCTOR = typeChatComponentText.getConstructor(String.class);
                CHAT_FORMAT_TYPE = (Class<? extends Enum<?>>) Class.forName("net.minecraft.server." + version + ".EnumChatFormat");
            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                ex.printStackTrace();
            }
        }
    }

    private final Object packet = PacketAccessor.createPacket();

    public PacketWrapper(String name, int param, List<String> members) {
        if (param != 3 && param != 4)
            throw new IllegalArgumentException("Method must be join or leave for player constructor");
        setupDefaults(name, param);
        setupMembers(members);
    }

    public PacketWrapper(String name, String prefix, String suffix, int param, Collection<?> players) {
        setupDefaults(name, param);

        if (param == 0 || param == 2) {
            try {
                if (param == 0)
                    ((Collection) PacketAccessor.MEMBERS.get(packet)).addAll(players);
                if (ServerVersion.getVersion().isLegacy()) {
                    PacketAccessor.PREFIX.set(packet, prefix);
                    PacketAccessor.SUFFIX.set(packet, suffix);
                    PacketAccessor.DISPLAY_NAME.set(packet, name);
                } else {
                    String color = ChatColor.getLastColors(prefix);
                    String colorCode = null;

                    if (!color.isEmpty()) {
                        colorCode = color.substring(color.length() - 1);
                        String chatColor = ChatColor.getByChar(colorCode).name();
                        if (chatColor.equalsIgnoreCase("MAGIC"))
                            chatColor = "OBFUSCATED";
                        Enum<?> colorEnum = Enum.valueOf(CHAT_FORMAT_TYPE, chatColor);
                        PacketAccessor.TEAM_COLOR.set(packet, colorEnum);
                    }
                    PacketAccessor.PREFIX.set(packet, CHAT_COMPONENT_CONSTRUCTOR.newInstance(prefix));

                    if (colorCode != null)
                        suffix = ChatColor.getByChar(colorCode) + suffix;
                    PacketAccessor.SUFFIX.set(packet, CHAT_COMPONENT_CONSTRUCTOR.newInstance(suffix));

                    PacketAccessor.DISPLAY_NAME.set(packet, CHAT_COMPONENT_CONSTRUCTOR.newInstance(name));
                }
                PacketAccessor.PACK_OPTION.set(packet, 1);

                if (PacketAccessor.VISIBILITY != null)
                    PacketAccessor.VISIBILITY.set(packet, "always");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Send the wrapped packet to the loaded players in the server
     */
    public void send() {
        PacketAccessor.sendPacket(ServerUtils.getLoadedPlayers(), packet);
    }

    /**
     * Send the wrapped packet to the given {@link Player}
     *
     * @param player the player to send the wrapped packet to
     */
    public void send(Player player) {
        PacketAccessor.sendPacket(player, packet);
    }

    private void setupDefaults(String name, int param) {
        try {
            PacketAccessor.TEAM_NAME.set(packet, name);
            PacketAccessor.PARAM_INT.set(packet, param);
            if (NametagHandler.DISABLE_PUSH && PacketAccessor.PUSH != null)
                PacketAccessor.PUSH.set(packet, "never");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setupMembers(Collection<?> players) {
        try {
            players = players == null || players.isEmpty() ? new ArrayList<>() : players;
            ((Collection) PacketAccessor.MEMBERS.get(packet)).addAll(players);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}