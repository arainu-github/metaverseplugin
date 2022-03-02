package world.arainu.core.metaverseplugin.iphone;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.utils.ChatUtil;
import world.arainu.core.metaverseplugin.utils.ItemUtil;

import java.util.Arrays;

/**
 * iPhoneからチェストの鍵を入手するクラス。
 *
 * @author kumitatepazuru
 */
public class ChestLock extends iPhoneBase {
    @Getter
    private static final NamespacedKey chestIDKey = new NamespacedKey(MetaversePlugin.getInstance(), "metaverse-key");

    @Override
    public void executeGui(MenuItem menuItem) {
        ItemStack chestKey = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta keyMeta = chestKey.getItemMeta();
        keyMeta.displayName(Component.text("設定されていない鍵").color(NamedTextColor.GOLD));
        keyMeta.lore(Arrays.asList(Component.text("チェストに向かって使用することで"), Component.text("チェストに鍵をかけられる。")));
        keyMeta.getPersistentDataContainer().set(chestIDKey, PersistentDataType.INTEGER, 1);
        chestKey.setItemMeta(keyMeta);
        ItemUtil.addItem(chestKey, menuItem.getClicker().getInventory(), menuItem.getClicker());
        Economy econ = MetaversePlugin.getEcon();
        ChatUtil.success(menuItem.getClicker(), econ.format(300) + "を支払い鍵を与えました。");
    }
}
