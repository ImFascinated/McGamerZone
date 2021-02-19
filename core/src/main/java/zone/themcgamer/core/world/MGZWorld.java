package zone.themcgamer.core.world;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Braydon
 */
@Setter @Getter @ToString
public class MGZWorld {
    public static final String FILE_NAME = "mgzWorld.properties";
    @Getter private static final List<MGZWorld> worlds = new ArrayList<>();

    private World world;
    private File dataFile;
    private String name, originalCreator, author;
    private String preset;
    private WorldCategory category;
    private final List<String> admins = new ArrayList<>();
    @Setter private Map<String, List<Location>> dataPoints = new HashMap<>();

    private MGZWorld(World world) {
        this.world = world;
        dataFile = new File(world.getWorldFolder(), FILE_NAME);
        loadData();
        worlds.add(this);
    }

    public MGZWorld(World world, File file, String name, String author, String preset, WorldCategory category) {
        this.world = world;
        dataFile = file;
        this.name = name;
        originalCreator = author;
        this.author = author;
        this.preset = preset;
        this.category = category;
    }

    @SneakyThrows
    public MGZWorld(File file) {
        if (file.isFile())
            dataFile = file;
        else {
            File dataFile = new File(file, FILE_NAME);
            if (!dataFile.exists())
                throw new FileNotFoundException("Cannot read data file for map");
            else this.dataFile = dataFile;
        }
        loadData();
    }

    public Location getDataPoint(String name) {
        List<Location> dataPoints = getDataPoints(name);
        if (dataPoints.isEmpty())
            return null;
        return dataPoints.get(0);
    }

    public List<Location> getDataPoints(String name) {
        return dataPoints.getOrDefault(name, new ArrayList<>());
    }

    public boolean hasPrivileges(Player player) {
        if (player.isOp() || player.getName().equalsIgnoreCase(originalCreator))
            return true;
        return isAdmin(player);
    }

    public boolean isAdmin(Player player) {
        return admins.contains(player.getName().toLowerCase());
    }

    public void save() {
        save(dataFile);
    }

    @SneakyThrows
    public void save(File file) {
        if (!dataFile.exists())
            dataFile.createNewFile();
        Properties properties = new Properties();
        try (OutputStream outputStream = new FileOutputStream(file)) {
            properties.setProperty("name", name);
            properties.setProperty("originalCreator", author);
            properties.setProperty("author", author);
            properties.setProperty("generator", preset == null ? WorldGenerator.FLAT.getPreset() : preset);
            properties.setProperty("category", category.name());
            properties.setProperty("admins", admins.isEmpty() ? "null" : String.join(",", admins));

            if (!dataPoints.isEmpty()) {
                for (Map.Entry<String, List<Location>> entry : dataPoints.entrySet()) {
                    StringBuilder builder = new StringBuilder();
                    for (Location location : entry.getValue())
                        builder.append(locationToString(location)).append(",");
                    String locationsString = builder.toString();
                    locationsString = locationsString.substring(0, locationsString.length() - 1);
                    properties.setProperty("dp-" + entry.getKey(), locationsString);
                }
            }

            properties.store(outputStream, null);
        }
    }

    @SneakyThrows
    private void loadData() {
        if (dataFile == null || (!dataFile.exists()))
            throw new FileNotFoundException();
        try {
            FileInputStream fileInputStream = new FileInputStream(dataFile);
            try {
                Properties properties = new Properties();
                properties.load(fileInputStream);
                name = properties.getProperty("name");
                originalCreator = properties.getProperty("originalCreator");
                author = properties.getProperty("author");

                if (properties.stringPropertyNames().contains("generator"))
                    preset = properties.getProperty("generator");
                else { // Legacy Worlds
                    if (Boolean.parseBoolean(properties.getProperty("void")))
                        preset = WorldGenerator.VOID.getPreset();
                    else preset = WorldGenerator.FLAT.getPreset();
                }

                category = WorldCategory.valueOf(properties.getProperty("category"));

                String adminsString = properties.getProperty("admins");
                if (!adminsString.equals("null"))
                    admins.addAll(Arrays.stream(adminsString.split(",")).collect(Collectors.toList()));

                for (String propertyName : properties.stringPropertyNames()) {
                    if (!propertyName.startsWith("dp-"))
                        continue;
                    String dataPointName = propertyName.split("-")[1];
                    String propertyValue = properties.getProperty(propertyName);
                    if (!propertyValue.contains(","))
                        dataPoints.put(dataPointName, Collections.singletonList(fromStringLocation(propertyValue)));
                    else {
                        for (String locationString : propertyValue.split(",")) {
                            List<Location> locations = dataPoints.getOrDefault(dataPointName, new ArrayList<>());
                            locations.add(fromStringLocation(locationString));
                            dataPoints.put(dataPointName, locations);
                        }
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    fileInputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static MGZWorld get(World world) {
        MGZWorld foundWorld = worlds.stream()
                .filter(mgzWorld -> mgzWorld.getWorld().getName().equals(world.getName()))
                .findFirst().orElse(null);
        if (foundWorld != null)
            return foundWorld;
        return new MGZWorld(world);
    }

    private String locationToString(Location location) {
        if (location == null)
            return "null";
        return location.getX() + "|" +
                location.getY() + "|" +
                location.getZ() + "|" +
                location.getYaw() + "|" +
                location.getPitch();
    }

    private Location fromStringLocation(String s) {
        if (s == null || (s.equals("null") || s.trim().isEmpty()))
            return null;
        String[] data = s.split("\\|");
        double x = Double.parseDouble(data[0]);
        double y = Double.parseDouble(data[1]);
        double z = Double.parseDouble(data[2]);
        float yaw = Float.parseFloat(data[3]);
        float pitch = Float.parseFloat(data[4]);
        return new Location(world, x, y, z, yaw, pitch);
    }
}