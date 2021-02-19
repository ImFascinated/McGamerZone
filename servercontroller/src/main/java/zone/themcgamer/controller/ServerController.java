package zone.themcgamer.controller;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import zone.themcgamer.common.BuildData;
import zone.themcgamer.data.jedis.JedisController;
import zone.themcgamer.data.jedis.data.Node;
import zone.themcgamer.data.jedis.data.ServerGroup;
import zone.themcgamer.data.jedis.data.server.MinecraftServer;
import zone.themcgamer.data.jedis.data.server.ServerState;
import zone.themcgamer.data.jedis.repository.RedisRepository;
import zone.themcgamer.data.jedis.repository.impl.MinecraftServerRepository;
import zone.themcgamer.data.jedis.repository.impl.NodeRepository;
import zone.themcgamer.data.jedis.repository.impl.ServerGroupRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.*;
import java.util.stream.Collectors;

/**
 * @author Braydon
 */
public class ServerController {
    private static Logger logger;

    // Repositories
    private static NodeRepository nodeRepository;
    private static ServerGroupRepository serverGroupRepository;
    private static MinecraftServerRepository minecraftServerRepository;

    private static Node node;
    private static final Set<ProcessRunner> processes = new HashSet<>();

    // Servers
    private static final Set<MinecraftServer> starting = new HashSet<>();
    private static final Set<MinecraftServer> lagging = new HashSet<>();

    private static ServerGroupCreator groupCreator;

    public static void main(String[] args) {
        long started = System.currentTimeMillis();
        File logsDirectory = new File("logs");
        if (!logsDirectory.exists())
            logsDirectory.mkdirs();
        logger = new Logger("Server Controller", null) {{
            setLevel(Level.ALL);
        }};
        // Setting up the logger
        try {
            String dateString = "MM-dd-yyyy HH:mm:ss";
            // The Windows operating system cannot have ":" in file names, so we need to replace
            // the character with something that will work
            if (System.getProperty("os.name").startsWith("Windows"))
                dateString = dateString.replace(":", "-");
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateString);
            SimpleFormatter formatter = new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord record) {
                    return String.format(
                            "[%s | %s] %s",
                            dateFormat.format(new Date(record.getMillis())),
                            record.getLevel().getLocalizedName(),
                            record.getMessage()
                    ) + "\n";
                }
            };
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(formatter);
            FileHandler fileHandler = new FileHandler("logs" + File.separator + dateFormat.format(new Date()) + ".log");
            fileHandler.setFormatter(formatter);
            logger.addHandler(consoleHandler);
            logger.addHandler(fileHandler);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        new JedisController().start(); // Initializing Redis

        // Setting the repository fields
        RedisRepository.getRepository(NodeRepository.class).ifPresent(nodeRepository -> ServerController.nodeRepository = nodeRepository);
        RedisRepository.getRepository(ServerGroupRepository.class).ifPresent(groupRepository -> serverGroupRepository = groupRepository);
        RedisRepository.getRepository(MinecraftServerRepository.class).ifPresent(minecraftServerRepository -> ServerController.minecraftServerRepository = minecraftServerRepository);

        // Setting the node and posting it to Redis
        try {
            URL url = new URL("http://checkip.amazonaws.com");
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                node = new Node(InetAddress.getLocalHost().getHostName(), bufferedReader.readLine(), "25665-25765");
            }
            if (node == null || (node.getName().trim().isEmpty() || node.getAddress().trim().isEmpty())) {
                System.err.println("Cannot resolve Node");
                System.exit(1);
                return;
            }
            nodeRepository.post(node);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Starting the controller thread
        new ControllerThread().start();

        // This is needed for Pterodactyl so the controller is marked as started
        System.out.println("Done (" + (System.currentTimeMillis() - started) + "ms)! For help, type \"help\" or \"?\"\n");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (groupCreator != null) {
                if (line.equalsIgnoreCase("exit")) {
                    groupCreator = null;
                    logger.info("Exited the group creator");
                } else {
                    if (groupCreator.getName() == null) {
                        groupCreator.setName(line);
                        logger.info("Alright, we'll call your server group \"" + groupCreator.getName() + "\"");
                        logger.info("Now, how much memory should each server have?");
                    } else if (groupCreator.getMemoryPerServer() == -1L) {
                        long memoryPerServer = -1;
                        try {
                            memoryPerServer = Long.parseLong(line);
                        } catch (NumberFormatException ignored) {}
                        if (memoryPerServer < 512) {
                            logger.warning("Invalid memory amount, each server must have 512mb of ram or more");
                            continue;
                        }
                        groupCreator.setMemoryPerServer(memoryPerServer);
                        logger.info("Okay, each server under your server group will have " + memoryPerServer + " of ram");
                        logger.info("What is the path of the template you would like the server group to use?");
                    } else if (groupCreator.getTemplatePath() == null) {
                        groupCreator.setTemplatePath(line);
                        logger.info("Okay, the container path will be \"" + groupCreator.getTemplatePath() + "\"");
                        logger.info("What would you like the plugin jar name to be?");
                    } else if (groupCreator.getPluginJarName() == null) {
                        groupCreator.setPluginJarName(line);
                        logger.info("Okay, the plugin jar name will be \"" + groupCreator.getPluginJarName() + "\"");
                        logger.info("What would you like the world path to be?");
                    } else if (groupCreator.getWorldPath() == null) {
                        groupCreator.setWorldPath(line);
                        logger.info("Okay, the world path will be \"" + groupCreator.getWorldPath() + "\"");
                        logger.info("What would you like the startup script to be?");
                    } else if (groupCreator.getStartupScript() == null) {
                        groupCreator.setStartupScript(line);
                        logger.info("Okay, the startup script will be \"" + groupCreator.getStartupScript() + "\"");
                        logger.info("Almost done, what would you like the private address to be?");
                    } else if (groupCreator.getPrivateAddress() == null) {
                        groupCreator.setPrivateAddress(line);
                        logger.info("Okay, the private address will be \"" + groupCreator.getPrivateAddress() + "\"");
                        logger.info("Alright, last thing! Would you like your server group to be static?");
                    } else {
                        boolean staticGroup = Boolean.parseBoolean(line);
                        groupCreator.setStaticGroup(staticGroup);

                        logger.info("All done, your server group is being built...");
                        ServerGroup serverGroup = groupCreator.build();
                        serverGroupRepository.post(serverGroup);
                        logger.info("Server group created: " + serverGroup.toString());
                        groupCreator = null;
                    }
                }
                continue;
            }

            switch (line.toLowerCase()) {
                case "build": {
                    System.out.println("Build = " + BuildData.getBuild().toString());
                    break;
                }
                case "stats": {
                    List<ServerGroup> serverGroups = serverGroupRepository.getCached();

                    System.out.println("Server Groups = " + serverGroups.size() + ":");
                    for (ServerGroup serverGroup : serverGroups)
                        System.out.println("  " + serverGroup.toString());

                    System.out.println("--------------------------");

                    List<MinecraftServer> minecraftServers = minecraftServerRepository.getCached();

                    System.out.println("Minecraft Servers = " + minecraftServers.size() + ":");
                    int online = 0;
                    for (MinecraftServer minecraftServer : minecraftServers) {
                        online+= minecraftServer.getOnline();
                        System.out.println("  " + minecraftServer.toString());
                    }

                    System.out.println("Online players = " + online);
                    break;
                }
                case "creategroup": {
                    groupCreator = new ServerGroupCreator();
                    logger.info("Hi there, welcome to the server group creator! What would you like your server group to be named?");
                    break;
                }
            }
        }
    }

    private static class ControllerThread extends Thread {
        public ControllerThread() {
            super("Server Controller Thread");
            logger.info("Started thread \"" + getName() + "\"");
        }

        @Override @SneakyThrows
        public void run() {
            while (isAlive()) {
                // This should never happen
                if (node == null) {
                    logger.severe("Sleeping thread, the node is null...");
                    Thread.sleep(1000L);
                    continue;
                }
                // Removing started servers from the starting list
                for (MinecraftServer minecraftServer : minecraftServerRepository.getCached()) {
                    if (!minecraftServer.isRunning())
                        continue;
                    starting.remove(minecraftServer);
                }

                // Stopping Slow Servers. A server would be considered "slow" if it is in the STARTING
                // state for 30 seconds or longer
                AtomicInteger slowStopped = new AtomicInteger();
                starting.removeIf(server -> {
                    if (server.getGroup().isStaticGroup())
                        return false;
                    if (server.getState() == ServerState.STARTING
                            && (System.currentTimeMillis() - server.getLastStateChange()) >= TimeUnit.SECONDS.toMillis(30L)) {
                        slowStopped.incrementAndGet();
                        stopServer(server, StopCause.SLOW_STARTUP);
                        return true;
                    }
                    return false;
                });
                if (slowStopped.get() > 0) {
                    int amount = slowStopped.get();
                    logger.info("Stopped " + amount + " server" + (amount == 1 ? "" : "s") + " because they are taking too long to start");
                    Thread.sleep(750L);
                    continue;
                }

                // Stopping Dead Servers
                int deadStopped = 0;
                for (MinecraftServer server : new ArrayList<>(minecraftServerRepository.getCached())) {
                    if (server.isDead() && !server.getGroup().isStaticGroup() && (server.getNode() == null || (server.getNode().equals(node)))) {
                        deadStopped++;
                        stopServer(server, StopCause.DEAD);
                    }
                }
                if (deadStopped > 0) {
                    logger.info("Stopped " + deadStopped + " server" + (deadStopped == 1 ? "" : "s") + " because they didn't send a heartbeat");
                    Thread.sleep(750L);
                    continue;
                }

                // Removing Extra Directories
                int extraDirectoriesRemoved = 0;
                for (ServerGroup serverGroup : serverGroupRepository.getCached()) {
                    File serversDirectory = new File(File.separator + "home" + File.separator + "minecraft" + File.separator + "servers" + File.separator + serverGroup.getName());
                    if (!serversDirectory.exists())
                        continue;
                    File[] files = serversDirectory.listFiles();
                    if (files == null)
                        continue;
                    for (File directory : files) {
                        if (!directory.isDirectory())
                            continue;
                        String serverId = directory.getName();
                        Optional<MinecraftServer> optionalMinecraftServer = minecraftServerRepository.lookup(serverId);
                        if (optionalMinecraftServer.isPresent())
                            continue;
                        extraDirectoriesRemoved++;
                        FileUtils.deleteQuietly(directory);
                        Thread.sleep(350L);
                    }
                }
                if (extraDirectoriesRemoved > 0) {
                    logger.info("Removed " + extraDirectoriesRemoved + " extra " + (extraDirectoriesRemoved == 1 ? "directory" : "directories"));
                    Thread.sleep(750L);
                    continue;
                }

                // Stopping Shutting Down Servers
                int shuttingDownStopped = 0;
                for (MinecraftServer server : new ArrayList<>(minecraftServerRepository.getCached())) {
                    if (server.getGroup().isStaticGroup() || (server.getNode() != null && (!server.getNode().equals(node))))
                        continue;
                    if (server.getState().isShuttingDownState()) {
                        shuttingDownStopped++;
                        stopServer(server, StopCause.SHUTDOWN);
                    }
                }
                if (shuttingDownStopped > 0) {
                    logger.info("Stopped " + shuttingDownStopped + " server" + (shuttingDownStopped == 1 ? "" : "s") + " because they were shutdown");
                    Thread.sleep(750L);
                    continue;
                }

                // Stopping Laggy Servers
                int laggyStopped = 0;
                for (MinecraftServer server : minecraftServerRepository.getCached()) {
                    if ((server.getNode() != null && (!server.getNode().equals(node)) || server.getGroup().isStaticGroup() || !server.isRunning()))
                        continue;
                    if (!server.isLagging()) {
                        lagging.remove(server);
                        continue;
                    }
                    if (lagging.contains(server)) {
                        laggyStopped++;
                        stopServer(server, StopCause.LAGGY);
                    } else {
                        lagging.add(server);
                        logger.info("Server \"" + server.getId() + "\" is lagging, it will be stopped if it continues to lag");
                    }
                }
                if (laggyStopped > 0) {
                    logger.info("Stopped " + laggyStopped + " server" + (laggyStopped == 1 ? "" : "s") + " because they were lagging");
                    Thread.sleep(750L);
                    continue;
                }

                // Stopping Extra Servers
                int extraStopped = 0;
                for (ServerGroup group : serverGroupRepository.getCached()) {
                    int runningServers = Math.toIntExact(group.getServers().stream().filter(MinecraftServer::isRunning).count());
                    int serversToStop = runningServers - group.getMaxServers();
                    if (serversToStop <= 0)
                        continue;
                    logger.info("Attempting to stop " + serversToStop + " extra server" + (serversToStop == 1 ? "" : "s") + " for server group '" + group.getName() + "'");
                    while (serversToStop > 0) {
                        List<MinecraftServer> servers = group.getServers().stream()
                                .filter(minecraftServer -> minecraftServer.isRunning() && minecraftServer.getNode().equals(node))
                                .sorted((a, b) -> Integer.compare(b.getNumericId(), a.getNumericId()))
                                .collect(Collectors.toList());
                        if (servers.isEmpty()) {
                            logger.warning("Attempted to close an extra server but found none to close");
                            break;
                        }
                        stopServer(servers.get(0), StopCause.OVER_LIMIT);
                        extraStopped++;
                        serversToStop--;
                        Thread.sleep(350L);
                    }
                }
                if (extraStopped > 0) {
                    logger.info("Stopped " + extraStopped + " extra server" + (extraStopped == 1 ? "" : "s") + " because they were over the group limit");
                    Thread.sleep(750L);
                    continue;
                }

                // Creating Servers
                for (ServerGroup group : serverGroupRepository.getCached()) {
                    if (group.isStaticGroup())
                        continue;
                    List<MinecraftServer> starting = ServerController.starting.stream()
                            .filter(server -> server.getGroup().equals(group))
                            .collect(Collectors.toList());
                    int runningServers = Math.toIntExact(group.getServers().stream().filter(MinecraftServer::isRunning).count());
                    int startingCount = starting.size();
                    if (runningServers + startingCount >= group.getMaxServers())
                        continue;
                    int serversToCreate = Math.max(Math.min(group.getMinServers(), group.getMaxServers()) - (runningServers + startingCount), 0);
                    serversToCreate+= group.getServers().stream().filter(minecraftServer -> minecraftServer.getOnline() >= group.getMinPlayers()).count();

                    while ((runningServers + startingCount) + serversToCreate > group.getMaxServers())
                        serversToCreate--;

                    if (serversToCreate <= 0)
                        continue;
                    logger.info("Attempting to create " + serversToCreate + " server" + (serversToCreate == 1 ? "" : "s") + " for server group '" + group.getName() + "'");

                    List<String> usedIds = new ArrayList<>();
                    List<Integer> usedNumericIds = new ArrayList<>();
                    Set<Long> usedPorts = new HashSet<>();

                    while (serversToCreate > 0) {
                        serversToCreate--;

                        List<MinecraftServer> servers = new ArrayList<>(group.getServers());
                        servers.addAll(starting);

                        for (MinecraftServer server : servers) {
                            usedIds.add(server.getId());
                            if (server.isRunning())
                                usedNumericIds.add(server.getNumericId());
                        }
                        for (MinecraftServer server : servers)
                            usedPorts.add(server.getPort());

                        long port = node.getNextAvailablePort(usedPorts);
                        if (port == -1)
                            continue;

                        String idString = "";
                        while (idString.trim().isEmpty() || usedIds.contains(idString)) {
                            idString = group.getName().toLowerCase();
                            idString+= generateID(true, false);
                            idString+= generateID(false, true);
                        }

                        int numericId = 1;
                        while (usedNumericIds.contains(numericId))
                            numericId++;

                        MinecraftServer server = new MinecraftServer(
                                idString,
                                numericId,
                                group.getName() + "-" + numericId,
                                node,
                                group,
                                node.getAddress(),
                                port,
                                0,
                                0,
                                ServerState.STARTING,
                                System.currentTimeMillis(),
                                0,
                                0,
                                20D,
                                group.getHost(),
                                group.getGame(),
                                "",
                                System.currentTimeMillis(),
                                -1
                        );
                        startingCount++;
                        usedIds.add(idString);
                        usedNumericIds.add(numericId);
                        usedPorts.add(port);
                        ServerController.starting.add(server);
                        minecraftServerRepository.post(server);

                        ProcessRunner processRunner = new ProcessRunner(new String[] { "/bin/sh", "/home/minecraft/createServer.sh",
                                server.getGroup().getName(),
                                server.getId(),
                                group.getServerJar(),
                                group.getTemplatePath(),
                                group.getPluginJarName(),
                                group.getWorldPath(),
                                "" + port,
                                group.getStartupScript()
                                        .replace("{{SERVER_MEMORY}}", "" + group.getMemoryPerServer())
                                        .replace("{{SERVER_JARFILE}}", "server.jar")
                        });
                        processRunner.start(error -> {
                            if (error)
                                logger.severe("Failed creating Minecraft server '" + server.getId() + "'@'" + server.getAddress() + "' (" + server.getName() + ") on port " + port);
                            else {
                                logger.info("Created Minecraft server '" + server.getId() + "'@'" + server.getAddress() + "' (" + server.getName() + ") on port " + port);
                            }
                        });
                        processRunner.join(100L);
                        if (!processRunner.isDone())
                            processes.add(processRunner);
                    }
                }
                while (!processes.isEmpty()) {
                    processes.removeIf(process -> {
                        try {
                            process.join(100L);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        return process.isDone();
                    });
                    if (!processes.isEmpty()) {
                        logger.info("Sleeping... waiting for " + processes.size() + " processes");
                        Thread.sleep(3000L);
                    }
                }
                Thread.sleep(500L);
            }
        }
    }

    /**
     * Generate a unique id based on the given values
     * @param includeCapitalLetters Whether or not to include capital letters in the unique id
     * @param includeLowercaseLetters Whether or not to include lowercase letters in the unique id
     * @return the unique id
     */
    private static String generateID(boolean includeCapitalLetters, boolean includeLowercaseLetters) {
        String alphaNumericString = "";
        if (!includeCapitalLetters && !includeLowercaseLetters)
            includeCapitalLetters = includeLowercaseLetters = true;
        if (includeCapitalLetters)
            alphaNumericString+= "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if (includeLowercaseLetters)
            alphaNumericString+= "abcdefghijklmnopqrstuvxyz";
        StringBuilder builder = new StringBuilder(2);
        for (int i = 0; i < 2; i++) {
            int index = (int) (alphaNumericString.length() * Math.random());
            builder.append(alphaNumericString.charAt(index));
        }
        return builder.toString();
    }

    /**
     * Stop the given {@link MinecraftServer} with the given {@link StopCause}
     * @param server The Minecraft server to stop
     * @param cause The cause to stop the server
     */
    @SneakyThrows
    private static void stopServer(MinecraftServer server, StopCause cause) {
        if (cause != StopCause.SLOW_STARTUP)
            starting.remove(server);

        String reason = "N/A";
        switch (cause) {
            case SLOW_STARTUP: {
                reason = "Slow Startup";
                break;
            }
            case DEAD: {
                reason = "Sent no heartbeat";
                break;
            }
            case SHUTDOWN: {
                reason = "Shutdown (state: " + server.getState().name() + ")";
                break;
            }
            case LAGGY: {
                reason = "Lagging (tps: " + server.getTps() + ")";
                break;
            }
            case OVER_LIMIT: {
                reason = "Over limit (max: " + server.getGroup().getMaxServers() + ")";
                break;
            }
        }
        String finalReason = reason;

        // Calling the stopServer.sh script
        ProcessRunner processRunner = new ProcessRunner(new String[] { "/bin/sh", "/home/minecraft/stopServer.sh",
                server.getGroup().getName(),
                server.getId()
        });
        processRunner.start(error -> {
            if (error)
                logger.severe("Failed to stop server \"" + server.getId() + "\"");
            else {
                logger.info("Stopped server \"" + server.getId() + "\" (" + server.getName() + "): " + finalReason);
                lagging.remove(server);
                minecraftServerRepository.remove(server);
            }
        });
        processRunner.join(50L);
        if (!processRunner.isDone()) {
            processes.add(processRunner);
        }
    }

    private enum StopCause {
        SLOW_STARTUP, DEAD, SHUTDOWN, LAGGY, OVER_LIMIT
    }
}