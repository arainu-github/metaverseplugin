package world.arainu.core.metaverseplugin.iphone;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import world.arainu.core.metaverseplugin.commands.CommandPlayerOnlyBase;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;

/**
 * iphoneのGuiのアイテムのベースクラス
 * @author kumitatepazuru
 */
public abstract class iPhoneBase extends CommandPlayerOnlyBase {
    @Getter private static iPhoneBase Instance;

    /**
     * iphoneのGuiのアイテムのベースクラス
     * @author kumitatepazuru
     */
    iPhoneBase() {
        Instance = this;
    }

    @Override
    public boolean execute(Player player, Command command, String label, String[] args) {
        Gui.error(player, "コマンドは実装されていません");
        return false;
    }

    /**
     * Guiから実行したときに動く関数
     * @param menuItem クリックしたアイテムの情報が入っている
     */
    public abstract void executeGui(MenuItem menuItem);
}
