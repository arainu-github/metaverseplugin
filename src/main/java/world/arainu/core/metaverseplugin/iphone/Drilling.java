package world.arainu.core.metaverseplugin.iphone;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.utils.ItemUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Drilling extends iPhoneBase {
    @Getter private static final NamespacedKey key = new NamespacedKey(MetaversePlugin.getInstance(), "metaverse-drilling");

    @Override
    public void executeGui(MenuItem menuItem) {
        Player p = menuItem.getClicker();
        ItemStack item = new ItemStack(Material.BRICKS);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(Component.text("採掘マシーン").color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD).decorate(TextDecoration.ITALIC));
        itemMeta.getPersistentDataContainer().set(key,PersistentDataType.INTEGER,1);
        itemMeta.lore(Collections.singletonList(Component.text("採掘したい場所の中心に設置して使用する。")));
        item.setItemMeta(itemMeta);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        if(!p.getInventory().all(Material.BRICKS).values().stream().map(itemStack -> itemStack.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)).collect(Collectors.toList()).contains(true)) {
            ItemUtil.addItem(item,p.getInventory(),p);
            Component component = Component.text("採掘マシーン").color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD).decorate(TextDecoration.UNDERLINED);
            component = component.append(Component.text("をインベントリ内に追加しました。\nこちらのブロックを採掘したい場所の端に設置して専用画面を開きましょう。").color(NamedTextColor.WHITE)
                    .decorations(new HashMap<>() {{
                        put(TextDecoration.BOLD, TextDecoration.State.FALSE);
                        put(TextDecoration.UNDERLINED, TextDecoration.State.FALSE);
                    }}));
            p.sendMessage(component);
        }
    }
}
