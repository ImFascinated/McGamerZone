package zone.themcgamer.core.common.menu;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Braydon
 */
@Getter
public abstract class PaginatedMenu<T> extends Menu {
    private final int page;
    private final List<T> input;
    private final int itemsPerPage;
    private final Map<Integer, T> contents = new HashMap<>();

    public PaginatedMenu(Player player, String title, MenuType type, int page, List<T> input, int itemsPerPage) {
        super(player, title, type);
        this.page = page;
        this.input = input;
        this.itemsPerPage = itemsPerPage;
        populateContents(itemsPerPage);
    }

    public PaginatedMenu(Player player, String title, int rows, MenuType type, int page, List<T> input, int itemsPerPage) {
        super(player, title, rows, type);
        this.page = page;
        this.input = input;
        this.itemsPerPage = itemsPerPage;
        populateContents(itemsPerPage);
    }

    /**
     * Calculate and return what the max page is
     *
     * @return the max page
     */
    protected int getMaxPage() {
        int maxPage = (int) Math.ceil((double) input.size() / itemsPerPage);
        return maxPage <= 0 ? 1 : maxPage;
    }

    /**
     * Populate the contents map with the position and value
     *
     * @param itemsPerPage - The amount of items to display per page
     */
    private void populateContents(int itemsPerPage) {
        for (int i = itemsPerPage * (page - 1); i < itemsPerPage * page && i < input.size(); i++)
            contents.put(i + 1, input.get(i));
        if (hasFlag(MenuFlag.DEBUG))
            player.sendMessage("input=" + input.size() + ", contents for page=" + contents.size());
    }
}