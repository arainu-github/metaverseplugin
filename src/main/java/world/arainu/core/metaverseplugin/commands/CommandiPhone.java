package world.arainu.core.metaverseplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.store.iPhoneStore;
import world.arainu.core.metaverseplugin.utils.condition_item;

import java.util.ArrayList;
import java.util.List;

/**
 * プラグインの機能が全て集結しているメニューGuiを表示する魔法のアイテム
 *
 * @author kumitatepazuru
 */
public class CommandiPhone extends CommandPlayerOnlyBase {
    /**
     * 主要関数
     *
     * @param player iphoneを表示させるプレイヤー
     */
    public static void run(Player player) {
        final String title = ChatColor.BLUE + "iPhone 13 Pro Max";
        List<MenuItem> guiItem = new ArrayList<>();
        for (condition_item item : iPhoneStore.getGuiItem()) {
            if (item.condition().apply(player)) {
                guiItem.add(item.menuItem());
            }
        }
        Gui.getInstance().openMenu(player, title, guiItem);
    }

    /**
     * 主要関数。Guiから呼び出すとき用
     *
     * @param menuItem menuItem
     */
    public static void run(MenuItem menuItem) {
        run(menuItem.getClicker());
    }

    @Override
    public boolean execute(Player player, String[] args) {
        run(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label,
                                      String[] args) {
        return COMPLETE_LIST_EMPTY;
    }
}
