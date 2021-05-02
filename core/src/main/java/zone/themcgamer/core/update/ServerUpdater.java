package zone.themcgamer.core.update;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.core.module.Module;
import zone.themcgamer.core.module.ModuleInfo;
import zone.themcgamer.core.plugin.MGZPlugin;
import zone.themcgamer.core.traveler.ServerTraveler;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;
import zone.themcgamer.data.jedis.data.server.ServerState;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Braydon
 */
@ModuleInfo(name = "Server Updater")
public class ServerUpdater extends Module {
    private static final long CHECK_DELAY = 60L * 20L; // 1 Minute

    private final ServerTraveler traveler;

    private boolean updatePendingRestart;
    private long updateFoundTime;
    private int restartDelay;

    public ServerUpdater(JavaPlugin plugin, ServerTraveler traveler) {
        super(plugin);
        this.traveler = traveler;

        // Creating the jars directory
        File jarsDirectory = new File(File.separator + "home" + File.separator + "minecraft" + File.separator + "upload" + File.separator + "jars");
        if (!jarsDirectory.exists())
            jarsDirectory.mkdirs();

        // Mapping the jar hashes for the files inside of the plugins directory
        Map<String, String> jarHashes = new HashMap<>();
        for (Map.Entry<File, String> entry : getChecksums(new File("plugins")).entrySet())
            jarHashes.put(entry.getKey().getName(), entry.getValue());
        log("Listing jars...");
        for (Map.Entry<String, String> entry : jarHashes.entrySet())
            log("'" + entry.getKey() + "' = '" + entry.getValue() + "'");

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            // If there is a pending restart and if the restart delay has passed since the restart was found, update the server
            if (updatePendingRestart) {
                if ((int) (((System.currentTimeMillis() - updateFoundTime)) / 60000L) >= restartDelay)
                    update();
                return;
            }

            // Get the checksums from the update directory and compare them with the old checksums that
            // we fetched before. If a checksum is different, the server will have a restart delay
            // generated based on how many other servers there are opened in the same group
            for (Map.Entry<File, String> entry : getChecksums(jarsDirectory).entrySet()) {
                String fileName = entry.getKey().getName();
                String oldChecksum = jarHashes.get(fileName);
                if (oldChecksum == null)
                    continue;
                String newChecksum = entry.getValue();
                if (!oldChecksum.equals(newChecksum)) {
                    updateFoundTime = System.currentTimeMillis();
                    restartDelay = ThreadLocalRandom.current().nextInt(0, Math.min(MGZPlugin.getMinecraftServer().getGroup().getServers().size(), 3));
                    updatePendingRestart = true;

                    String timeString = (restartDelay <= 0L ? "now" : "in " + restartDelay + " minute" + (restartDelay == 1 ? "" : "s"));
                    log("Jar '" + fileName + "' was updated:");
                    log(" Old checksum = '" + oldChecksum + "'");
                    log(" New checksum = '" + newChecksum + "'");
                    log(" Restarting " + timeString);
                    if (restartDelay <= 0L)
                        update();
                    break;
                }
                jarHashes.put(fileName, newChecksum);
            }
        }, CHECK_DELAY, CHECK_DELAY);
    }

    /**
     * Mark the server as updating, send all the players to the next available server, and shutdown the server
     */
    private void update() {
        MinecraftServer minecraftServer = MGZPlugin.getMinecraftServer();
        try {
            traveler.sendAll("Hub", "&6" + minecraftServer.getName() + " &7is being updated");
        } catch (IllegalArgumentException ignored) {}
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> minecraftServer.setState(ServerState.UPDATING), 10L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), Bukkit::shutdown, 40L);
    }

    /**
     * Get a map of checksums from the given {@link File} directory
     *
     * @param directory the directory
     * @return the map of checksums
     */
    private Map<File, String> getChecksums(File directory) {
        Map<File, String> checksums = new HashMap<>();
        if (!directory.exists())
            return checksums;
        File[] files = directory.listFiles();
        if (files == null)
            return checksums;
        for (File file : files) {
            String name = file.getName();
            if (!name.contains("."))
                continue;
            String[] split = name.split("\\.");
            if (split.length < 1 || (!split[split.length - 1].equalsIgnoreCase("jar")))
                continue;
            Optional<String> optionalChecksum = getChecksum(file);
            if (optionalChecksum.isEmpty()) {
                log("Failed to retrieve checksum for file '" + file.getAbsolutePath() + "' in directory '" +
                        directory.getAbsolutePath() + "', continuing...");
                continue;
            }
            checksums.put(file, optionalChecksum.get());
        }
        return checksums;
    }

    /**
     * Get the checksum for the given {@link File}
     *
     * @param file the file to get the checksum for
     * @return the optional checksum
     */
    private Optional<String> getChecksum(@NonNull File file) {
        StringBuilder builder = new StringBuilder();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            FileInputStream inputStream = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int read;
            while ((read = inputStream.read(bytes)) != -1)
                digest.update(bytes, 0, read);
            byte[] mdbytes = digest.digest();
            for (byte mdbyte : mdbytes)
                builder.append(Integer.toString((mdbyte & 0xff) + 0x100, 16).substring(1));
        } catch (NoSuchAlgorithmException | IOException ex) {
            ex.printStackTrace();
        }
        String checksum = builder.toString();
        return checksum.isEmpty() ? Optional.empty() : Optional.of(checksum);
    }
}