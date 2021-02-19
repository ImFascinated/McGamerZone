package zone.themcgamer.buildServer.backup;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.common.ZipUtils;
import zone.themcgamer.core.world.MGZWorld;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Braydon
 */
public class BackupTask {
    private static final int MAX_BACKUP_SIZE = 15;

    public BackupTask(JavaPlugin plugin, MGZWorld world) {
        if (world.getWorld() != null)
            throw new IllegalStateException("Cannot backup a loaded world");
        File backupDirectory = new File("backups" + File.separator + world.getCategory().name() + File.separator + world.getName());
        if (!backupDirectory.exists())
            backupDirectory.mkdirs();
        plugin.getLogger().info("Backing up world \"" + world.getName() + "\" under category \"" + world.getCategory().name() + "\"");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            File[] files = backupDirectory.listFiles();
            if (files != null && (files.length > MAX_BACKUP_SIZE)) {
                File firstBackup = null;
                for (File file : files) {
                    if (firstBackup == null || (file.lastModified() < firstBackup.lastModified()))
                        firstBackup = file;
                }
                if (firstBackup != null)
                    FileUtils.deleteQuietly(firstBackup);
            }
            File worldDirectory = new File("maps" + File.separator + world.getCategory().name() + File.separator + world.getName());
            if (!worldDirectory.exists())
                return;
            File zipFile = new File(backupDirectory, new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()) + ".zip");
            ZipUtils.zip(worldDirectory.getPath(), zipFile.getPath());
            plugin.getLogger().info("Created backup \"" + zipFile.getPath() + "\"");
        });
    }
}