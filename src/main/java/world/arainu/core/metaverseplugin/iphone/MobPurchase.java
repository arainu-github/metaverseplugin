package world.arainu.core.metaverseplugin.iphone;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;

import java.util.Arrays;
import java.util.function.Consumer;

public class MobPurchase extends iPhoneBase{
    @Override
    public void executeGui(MenuItem menuItem) {

        Gui.getInstance().openMenu(menuItem.getClicker(), ChatColor.AQUA + "召喚するモブを選んでください。", Arrays.asList(
                new MenuItem("ウーパールーパー", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.AXOLOTL);}, true, Material.AXOLOTL_SPAWN_EGG),
                new MenuItem("コウモリ", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.BAT);}, true)
                /**
                 * @Todo
                 * このリストにスポーンエッグを追加していく
                 */
        ));
    }
}
