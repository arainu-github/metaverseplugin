package world.arainu.core.metaverseplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.store.iPhoneStore;

import java.util.List;

/**
 * プラグインの機能が全て集結しているメニューGuiを表示する魔法のアイテム
 * @author kumitatepazuru
 */
public class CommandiPhone extends CommandPlayerOnlyBase {
    @Override
    public boolean execute(Player player, Command command, String label, String[] args) {
        final String title = ChatColor.BLUE+"iPhone 12 Pro Max";
        if (player.isOp()){
            Gui.getInstance().openMenu(player, title, iPhoneStore.getModonlyGuiItem());
        } else {
            Gui.getInstance().openMenu(player, title, iPhoneStore.getGuiItem());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label,
                                      String[] args) {
        return COMPLETE_LIST_EMPTY;
    }
}
