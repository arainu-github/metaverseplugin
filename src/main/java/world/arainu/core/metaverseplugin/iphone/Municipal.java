package world.arainu.core.metaverseplugin.iphone;

import github.scarsz.discordsrv.DiscordSRV;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.store.ServerStore;
import world.arainu.core.metaverseplugin.utils.ChatUtil;
import world.arainu.core.metaverseplugin.utils.ItemUtil;

import java.util.Collections;
import java.util.List;

public class Municipal extends iPhoneBase {
    @Override
    public void executeGui(MenuItem menuItem) {
        if(DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(menuItem.getClicker().getUniqueId()) != null) {
            Gui.getInstance().openMenu(menuItem.getClicker(), "自治体メニュー", List.of(
                    new MenuItem("自分で自治体を作る", this::createMunicipal, true, Material.SLIME_BALL)
            ));
        } else {
            ChatUtil.error(menuItem.getClicker(),"自治体機能はdiscordとminecraftを連携することによって使用できます。\niphone内の「discordと連携する」から、discordとminecraftを連携してください。");
        }
    }

    public static ItemStack createItemStack(){
        final ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(Component.text("自治体作成ブック").color(NamedTextColor.LIGHT_PURPLE));
        itemMeta.lore(Collections.singletonList(Component.text("本を開くことで使用できる。")));
        itemMeta.getPersistentDataContainer().set(ServerStore.getMunicipalBookKey(), PersistentDataType.INTEGER, 1);
        item.setItemMeta(itemMeta);
        return item;
    }

    public void createMunicipal(MenuItem menuItem) {
        Player player = menuItem.getClicker();
        Economy econ = MetaversePlugin.getEcon();
        if(econ.has(player, 2000)) {
            econ.withdrawPlayer(player, 2000);
            player.sendMessage(Component.text("まずはじめに、自治体の区域を設定しましょう。\n手元に")
                    .append(Component.text("自治体作成ブック").color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD).decorate(TextDecoration.UNDERLINED))
                    .append(Component.text("がインベントリ内にあるのでそこから操作をして区域の始点を設定しましょう。")));
            ItemUtil.addItem(createItemStack(),player.getInventory(),player);
        } else {
            ChatUtil.error(player,"作成には2000円必要ですが、あなたにはそこまでお金はありません！\n残高: " + econ.format(econ.getBalance(player)));
        }
    }
}
