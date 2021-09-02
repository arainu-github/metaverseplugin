package world.arainu.core.metaverseplugin.store;

import lombok.Getter;
import world.arainu.core.metaverseplugin.gui.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class iPhoneStore {
    public iPhoneStore() {
        Instance = this;
    }
    @Getter private static final List<MenuItem> GuiItem = new ArrayList<>();
    @Getter private static final List<MenuItem> ModonlyGuiItem = new ArrayList<>();
    @Getter private static iPhoneStore Instance;

    public static void addGuiItem(MenuItem menuItem) {
        GuiItem.add(menuItem);
    }

    public static void addGuiItem(MenuItem menuItem,Boolean modonly) {
        if (modonly) {
            ModonlyGuiItem.add(menuItem);
        }
    }
}
