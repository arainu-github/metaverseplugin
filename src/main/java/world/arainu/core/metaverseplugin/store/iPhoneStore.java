package world.arainu.core.metaverseplugin.store;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.utils.condition_item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * iPhoneに関するデータを格納するstore
 *
 * @author kumitatepazuru
 */
public class iPhoneStore {
    @Getter
    private static final List<condition_item> GuiItem = new ArrayList<>();

    /**
     * Guiアイテムを追加する関数。
     *
     * @param menuItem メニューアイテム
     */
    public static void addGuiItem(MenuItem menuItem) {
        addGuiItem(menuItem, false);
    }

    /**
     * Guiアイテムを追加する関数。
     *
     * @param menuItem メニューアイテム
     * @param modonly  モデレータにしか使用できないようにするか
     */
    public static void addGuiItem(MenuItem menuItem, Boolean modonly) {
        if (modonly) {
            addGuiItem(menuItem, ServerOperator::isOp);
        } else {
            addGuiItem(menuItem, n -> true);
        }
    }

    /**
     * Guiアイテムを追加する関数。
     *
     * @param menuItem  メニューアイテム
     * @param condition iphoneに表示する条件
     */
    public static void addGuiItem(MenuItem menuItem, Function<Player, Boolean> condition) {
        GuiItem.add(new condition_item(menuItem, condition));
    }
}

