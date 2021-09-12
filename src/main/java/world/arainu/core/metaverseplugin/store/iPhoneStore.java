package world.arainu.core.metaverseplugin.store;

import lombok.Getter;
import world.arainu.core.metaverseplugin.gui.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * iPhoneに関するデータを格納するstore
 * @author kumitatepazuru
 */
public class iPhoneStore {
    @Getter private static final List<MenuItem> GuiItem = new ArrayList<>();
    @Getter private static final List<MenuItem> ModonlyGuiItem = new ArrayList<>();

    /**
     * Guiアイテムを追加する関数。
     * @param menuItem メニューアイテム
     */
    public static void addGuiItem(MenuItem menuItem) {
        GuiItem.add(menuItem);
    }

    /**
     * Guiアイテムを追加する関数。
     * @param menuItem メニューアイテム
     * @param modonly モデレータにしか使用できないようにするか
     */
    public static void addGuiItem(MenuItem menuItem,Boolean modonly) {
        if (modonly) {
            ModonlyGuiItem.add(menuItem);
        }
    }
}
