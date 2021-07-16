package zone.themcgamer.buildServer.parse;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import zone.themcgamer.common.DoubleUtils;
import zone.themcgamer.common.MathUtils;
import zone.themcgamer.common.ZipUtils;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.world.MGZWorld;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Braydon
 */
public class ParseTask {
    @Getter private final MGZWorld mgzWorld;
    private final int radius;
    private final List<Integer> ids;

    private World world;
    private Location center;
    private long started;

    @Setter private boolean preparing = true;
    private int x, y, z, parsedBlocks;
    @Getter private boolean completed;

    private final Map<String, List<Location>> dataPoints = new HashMap<>();

    public ParseTask(MGZWorld mgzWorld, int radius, List<Integer> ids) {
        this.mgzWorld = mgzWorld;
        this.radius = radius;
        this.ids = ids;
        x = z = -radius;
    }

    public void start(World world, Location center) {
        if (!preparing)
            return;
        this.world = world;
        this.center = center;
        started = System.currentTimeMillis();
        preparing = false;
    }

    public void run() {
        if (preparing)
            return;
        long started = System.currentTimeMillis();
        for (; x <= radius; x++) {
            for (; y <= 256; y++) {
                for (; z <= radius; z++) {
                    if ((System.currentTimeMillis() - started) >= 10L)
                        return;
                    parsedBlocks++;
                    if (parsedBlocks % 15_000_000 == 0) {
                        double complete = (double) parsedBlocks / 1_000_000;
                        double total = (double) ((radius * 2) * 256 * (radius * 2)) / 1_000_000;
                        double percent = MathUtils.round(complete / total * 100, 1);
                        Bukkit.broadcastMessage(Style.main("Map", "Parse of map §b" + mgzWorld.getName() + " §7is §6" + percent + "% §7complete"));
                    }
                    Block block = world.getBlockAt(center.getBlockX() + x, y, center.getBlockZ() + z);
                    Location blockLocation = block.getLocation();
                    blockLocation.setX(blockLocation.getBlockX() + .5);
                    blockLocation.setZ(blockLocation.getBlockZ() + .5);

                    if (block.getType() == Material.SPONGE) {
                        Block blockAbove = block.getRelative(BlockFace.UP);
                        if (blockAbove.getType() == Material.SIGN || blockAbove.getType() == Material.SIGN_POST) {
                            Sign sign = (Sign) blockAbove.getState();
                            String[] lines = sign.getLines();
                            if (lines.length < 1)
                                continue;
                            StringBuilder signText = new StringBuilder(lines[0]);
                            if (lines.length >= 2)
                                signText.append(" ").append(lines[1]);
                            if (lines.length >= 3)
                                signText.append(" ").append(lines[2]);
                            if (lines.length >= 4)
                                signText.append(" ").append(lines[3]);
                            String dataPointName = signText.toString().trim();

                            List<Location> locations = dataPoints.getOrDefault(dataPointName, new ArrayList<>());
                            locations.add(blockLocation);
                            dataPoints.put(dataPointName, locations);

                            block.setType(Material.AIR);
                            blockAbove.setType(Material.AIR);
                        }
                    } else if (ids.contains(block.getTypeId())) {
                        String dataPointName = block.getType().name();
                        List<Location> locations = dataPoints.getOrDefault(dataPointName, new ArrayList<>());
                        locations.add(blockLocation);
                        dataPoints.put(dataPointName, locations);
                    }
                }
                z = -radius;
            }
            y = 0;
        }
        // Unloading the world
        Bukkit.unloadWorld(world, true);

        File directory = world.getWorldFolder();

        // Removing unnecessary files
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                if (fileName.equals("level.dat") || fileName.equals("region") || fileName.equals(MGZWorld.FILE_NAME))
                    continue;
                FileUtils.deleteQuietly(file);
            }
        }
        // Saving properties file
        mgzWorld.setDataPoints(dataPoints);
        mgzWorld.save(new File(world.getWorldFolder(), MGZWorld.FILE_NAME));

        // Zipping the parsed world
        File targetDirectory = new File(File.separator + "home" + File.separator + "minecraft" + File.separator + "ftp" + File.separator + "upload" + File.separator + "upload" + File.separator +
                "maps" + File.separator + mgzWorld.getCategory().name());
        if (!targetDirectory.exists())
            targetDirectory.mkdirs();
        File targetFile = new File(targetDirectory, mgzWorld.getName().replaceAll(" ", "_") + ".zip");
        if (targetFile.exists())
            FileUtils.deleteQuietly(targetFile);
        ZipUtils.zip(directory.getPath(), targetFile.getPath());

        // Deleting the parsed world
        FileUtils.deleteQuietly(directory);

        // Marking the parse as complete
        completed = true;

        // Announcing the parse summary
        long elapsed = System.currentTimeMillis() - this.started;
        String time;
        if (elapsed < 1000)
            time = elapsed + "ms";
        else time = (elapsed / 1000) + " seconds";

        Bukkit.broadcastMessage(Style.main("Map", "Parse summary of §b" + mgzWorld.getName() + "§7:"));
        Bukkit.broadcastMessage(Style.color(" §8- §7Blocks §f" + DoubleUtils.format(parsedBlocks, false)));
        if (!dataPoints.isEmpty()) {
            Bukkit.broadcastMessage("    §bData Points");
            for (Map.Entry<String, List<Location>> entry : dataPoints.entrySet())
                Bukkit.broadcastMessage("       §6" + entry.getKey() + " §f" + entry.getValue().size());
        }
        Bukkit.broadcastMessage(Style.color(" §8- §7Time §f" + time));
    }
}