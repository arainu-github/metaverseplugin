package world.arainu.core.metaverseplugin.iphone;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.utils.ChatUtil;

import java.util.Arrays;
import java.util.function.Consumer;

public class MobPurchase extends iPhoneBase{
    @Override
    public void executeGui(MenuItem menuItem) {
        Player player = menuItem.getClicker();
        if (MetaversePlugin.getEcon().has(player, MetaversePlugin.getConfiguration().getInt("mob.money"))) {
            Gui.getInstance().openMenu(menuItem.getClicker(), ChatColor.AQUA + "召喚するモブを選んでください。", Arrays.asList(
                    new MenuItem("ウーパールーパー", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.AXOLOTL);}, true, Material.AXOLOTL_SPAWN_EGG),
                    new MenuItem("コウモリ", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.BAT);}, true, Material.BAT_SPAWN_EGG),
                    new MenuItem("ミツバチ", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.BEE);}, true, Material.BEE_SPAWN_EGG),
                    new MenuItem("猫", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.CAT);}, true, Material.CAT_SPAWN_EGG),
                    new MenuItem("鶏", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.CHICKEN);}, true, Material.CHICKEN_SPAWN_EGG),
                    new MenuItem("タラ", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.COD);}, true, Material.COD_SPAWN_EGG),
                    new MenuItem("牛", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.COW);}, true, Material.COW_SPAWN_EGG),
                    new MenuItem("イルカ", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.DOLPHIN);}, true, Material.DOLPHIN_SPAWN_EGG),
                    new MenuItem("ロバ", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.DONKEY);}, true, Material.DONKEY_SPAWN_EGG),
                    new MenuItem("キツネ", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.FOX);}, true, Material.FOX_SPAWN_EGG),
                    new MenuItem("馬", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.HORSE);}, true, Material.HORSE_SPAWN_EGG),
                    new MenuItem("ラマ", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.LLAMA);}, true, Material.LLAMA_SPAWN_EGG),
                    new MenuItem("山猫", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.OCELOT);}, true, Material.OCELOT_SPAWN_EGG),
                    new MenuItem("オウム", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.PARROT);}, true, Material.PARROT_SPAWN_EGG),
                    new MenuItem("パンダ", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.PANDA);}, true, Material.PANDA_SPAWN_EGG),
                    new MenuItem("豚", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.PIG);}, true, Material.PIG_SPAWN_EGG),
                    new MenuItem("シロクマ", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.POLAR_BEAR);}, true, Material.POLAR_BEAR_SPAWN_EGG),
                    new MenuItem("フグ", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.PUFFERFISH);}, true, Material.PUFFERFISH),
                    new MenuItem("ウサギ", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.RABBIT);}, true, Material.RABBIT_SPAWN_EGG),
                    new MenuItem("鮭", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.SALMON);}, true, Material.SALMON_SPAWN_EGG),
                    new MenuItem("羊", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.SHEEP);}, true, Material.SHEEP_SPAWN_EGG),
                    new MenuItem("スケルトンホース", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.SKELETON_HORSE);}, true, Material.SKELETON_HORSE_SPAWN_EGG),
                    new MenuItem("イカ", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.SQUID);}, true, Material.SQUID_SPAWN_EGG),
                    new MenuItem("ストライダー", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.STRIDER);}, true, Material.STRIDER_SPAWN_EGG),
                    new MenuItem("行商のラマ", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.TRADER_LLAMA);}, true, Material.TRADER_LLAMA_SPAWN_EGG),
                    new MenuItem("熱帯魚", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.TROPICAL_FISH);}, true, Material.TROPICAL_FISH),
                    new MenuItem("カメ", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.TURTLE);}, true, Material.TURTLE_SPAWN_EGG),
                    new MenuItem("村人", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.VILLAGER);}, true, Material.VILLAGER_SPAWN_EGG),
                    new MenuItem("行商人", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.WANDERING_TRADER);}, true, Material.WANDERING_TRADER_SPAWN_EGG),
                    new MenuItem("狼", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.WOLF);}, true, Material.WOLF_SPAWN_EGG),
                    new MenuItem("ゾンビホース", (item) -> {Player clicker = item.getClicker(); clicker.getWorld().spawnEntity(clicker.getLocation(), EntityType.ZOMBIE_HORSE);}, true, Material.ZOMBIE_HORSE_SPAWN_EGG)
            ));
        } else {
            ChatUtil.warning(player, "銀行残高が少なすぎるためトラップタワーにいけません！\n必要料金: 200円");
        }

    }
}
