package world.arainu.core.metaverseplugin.iphone;

import world.arainu.core.metaverseplugin.gui.MenuItem;

public class BEAdvancements extends iPhoneBase {
    @Override
    public void executeGui(MenuItem menuItem) {
        menuItem.getClicker().performCommand("geyser advancements");
    }
}
