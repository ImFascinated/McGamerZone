package zone.themcgamer.core.common;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * This utility makes it easy to create pages
 * @author Braydon
 */
@RequiredArgsConstructor
public class PageBuilder<T> {
    private final Collection<T> collection;
    private final BiConsumer<Integer, T> contentConsumer;
    private int resultsPerPage = 7;

    /**
     * Set the results per page
     *
     * @param resultsPerPage the results per page
     */
    public PageBuilder<T> resultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
        return this;
    }

    /**
     * Get the maximum number of pages
     *
     * @return the maximum number of pages
     */
    public int getMaxPages() {
        if (collection.size() <= resultsPerPage)
            return 1;
        return collection.size() / resultsPerPage + 1;
    }

    public void send(CommandSender sender, int page) {
        if (collection.isEmpty()) {
            sender.sendMessage("§cNo entries found.");
            return;
        }
        int maxPages = getMaxPages();
        if (page <= 0 || page > maxPages) {
            sender.sendMessage("§cPage out of bounds. There " + (maxPages == 1 ? "is" : "are") + " only §l" + maxPages + " §cpage" + (maxPages == 1 ? "" : "s") + ".");
            return;
        }
        List<T> list = new ArrayList<>(collection);
        for (int i = resultsPerPage * (page - 1); i < resultsPerPage * page && i < collection.size(); i++)
            contentConsumer.accept(i + 1, list.get(i));
    }
}