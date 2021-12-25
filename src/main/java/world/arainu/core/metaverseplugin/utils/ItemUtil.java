package world.arainu.core.metaverseplugin.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * アイテムに関する関数(群)
 *
 * @author AreaEffectCloud
 */

public class ItemUtil {

    public static void addItem(ItemStack item, Inventory inv, Player p) {
        final Map<Integer, ItemStack> map = inv.addItem(item);
        map.values().forEach(e -> p.getWorld().dropItemNaturally(p.getLocation(), e));
    }
}
