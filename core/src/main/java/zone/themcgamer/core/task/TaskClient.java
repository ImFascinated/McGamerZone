package zone.themcgamer.core.task;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Braydon
 */
@Getter
public class TaskClient {
    private final List<Task> completedTasks = new ArrayList<>();
}