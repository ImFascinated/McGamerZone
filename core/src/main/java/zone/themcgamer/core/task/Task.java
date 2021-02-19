package zone.themcgamer.core.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Braydon
 */
@AllArgsConstructor @Getter
public enum Task {
    TEST("test");

    private final String id;

    /**
     * Get the {@link Task} matching the given id
     * @param id the id
     * @return the task, othewise null
     */
    public static Task match(String id) {
        return Arrays.stream(values()).filter(task -> task.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }
}