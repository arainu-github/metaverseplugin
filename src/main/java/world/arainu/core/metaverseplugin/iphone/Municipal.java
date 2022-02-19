package world.arainu.core.metaverseplugin.iphone;

import github.scarsz.discordsrv.DiscordSRV;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.store.ServerStore;
import world.arainu.core.metaverseplugin.utils.ChatUtil;
import world.arainu.core.metaverseplugin.utils.ItemUtil;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * iPhoneの自治体に関するプログラム群があるクラス
 * @author kumitatepazuru
 */
public class Municipal extends iPhoneBase {
    @Override
    public void executeGui(MenuItem menuItem) {
        if(DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(menuItem.getClicker().getUniqueId()) != null) {
            final DynmapAPI dynmap = MetaversePlugin.getDynmap();
            final MarkerAPI marker = dynmap.getMarkerAPI();
            MarkerSet markerSet = marker.getMarkerSet("municipal");
            if(markerSet == null) {
                markerSet = marker.createMarkerSet("municipal","自治体",null,true);
            }
            List<MenuItem> menuList = markerSet.getAreaMarkers().stream().map(n -> Arrays.asList(n.getMarkerID(),n.getLabel())).map(
                    data -> {
                        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta skull = (SkullMeta) item.getItemMeta();
                        skull.setOwningPlayer(Bukkit.getOfflinePlayer(Objects.requireNonNull(sqlUtil.getMunicipal(data.get(0))).uuid()));
                        item.setItemMeta(skull);
                        return new MenuItem(data.get(1),null,true, item);
                    }
            ).collect(Collectors.toList());
            ItemStack item = new ItemStack(Material.SLIME_BALL);
            item.lore(List.of(Component.text("費用:15000円")));
            Gui.getInstance().openMultiPageMenu(menuItem.getClicker(), "自治体メニュー", menuList,
                    new MenuItem("自分で自治体を作る", this::createMunicipal, true, item));
        } else {
            ChatUtil.error(menuItem.getClicker(),"自治体機能はdiscordとminecraftを連携することによって使用できます。\niphone内の「discordと連携する」から、discordとminecraftを連携してください。");
        }
    }

    /**
     * 自治体ブックを作成する関数。
     * @return 自治体作成ブックのデータ
     */
    public static ItemStack createItemStack(){
        final ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(Component.text("自治体作成ブック").color(NamedTextColor.LIGHT_PURPLE));
        itemMeta.lore(Collections.singletonList(Component.text("本を開くことで使用できる。")));
        itemMeta.getPersistentDataContainer().set(ServerStore.getMunicipalBookKey(), PersistentDataType.INTEGER, 1);
        item.setItemMeta(itemMeta);
        return item;
    }

    private void createMunicipal(MenuItem menuItem) {
        Player player = menuItem.getClicker();
        Economy econ = MetaversePlugin.getEcon();
        if(econ.has(player, 15000)) {
            econ.withdrawPlayer(player, 15000);
            ChatUtil.success(player,"15000円を自治体作成費用として徴収しました。");
            player.sendMessage(Component.text("まずはじめに、自治体の区域を設定しましょう。\n手元に")
                    .append(Component.text("自治体作成ブック").color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD).decorate(TextDecoration.UNDERLINED))
                    .append(Component.text("がインベントリ内にあるのでそこから操作をして区域の始点を設定しましょう。")));
            ItemUtil.addItem(createItemStack(),player.getInventory(),player);
        } else {
            ChatUtil.error(player,"作成には15000円必要ですが、あなたにはそこまでお金はありません！\n残高: " + econ.format(econ.getBalance(player)));
        }
    }
}
