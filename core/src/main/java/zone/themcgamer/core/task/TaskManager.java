package zone.themcgamer.core.task;

import org.bukkit.plugin.java.JavaPlugin;
import zone.themcgamer.core.account.MiniAccount;
import zone.themcgamer.core.module.ModuleInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Braydon
 */
@ModuleInfo(name = "Task Manager")
public class TaskManager extends MiniAccount<TaskClient> {
    public TaskManager(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public TaskClient getAccount(int accountId, UUID uuid, String name, String ip, String encryptedIp) {
        return new TaskClient();
    }

    @Override
    public String getQuery(int accountId, UUID uuid, String name, String ip, String encryptedIp) {
        return "SELECT task FROM `tasks` WHERE `accountId` = '" + accountId + "';";
    }

    @Override
    public void loadAccount(int accountId, UUID uuid, String name, String ip, String encryptedIp, ResultSet resultSet) throws SQLException {
        Optional<TaskClient> client = lookup(uuid);
        if (client.isEmpty())
            return;
        while (resultSet.next()) {
            Task task = Task.match(resultSet.getString("task"));
            if (task == null)
                continue;
            client.get().getCompletedTasks().add(task);
        }
    }
}