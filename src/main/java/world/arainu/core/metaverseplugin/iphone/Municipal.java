package world.arainu.core.metaverseplugin.iphone;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
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
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.store.ServerStore;
import world.arainu.core.metaverseplugin.utils.ChatUtil;
import world.arainu.core.metaverseplugin.utils.ItemUtil;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.ArrayList;
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
        if (DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(menuItem.getClicker().getUniqueId()) != null) {
            final DynmapAPI dynmap = MetaversePlugin.getDynmap();
            final MarkerAPI marker = dynmap.getMarkerAPI();
            MarkerSet markerSet = marker.getMarkerSet("municipal");
            if (markerSet == null) {
                markerSet = marker.createMarkerSet("municipal", "自治体", null, true);
            }
            List<MenuItem> menuList = markerSet.getAreaMarkers().stream().map(n -> Arrays.asList(n.getMarkerID(), n.getLabel())).map(
                    data -> {
                        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta skull = (SkullMeta) item.getItemMeta();
                        skull.setOwningPlayer(Bukkit.getOfflinePlayer(Objects.requireNonNull(sqlUtil.getMunicipal(data.get(0))).uuid()));
                        item.setItemMeta(skull);
                        return new MenuItem(data.get(1), this::manage, true, item,data);
                    }
            ).collect(Collectors.toList());
            ItemStack item = new ItemStack(Material.SLIME_BALL);
            item.lore(List.of(Component.text("費用:15000円")));
            Gui.getInstance().openMultiPageMenu(menuItem.getClicker(), "自治体メニュー", menuList,
                    new MenuItem("自分で自治体を作る", this::createMunicipal, true, item));
        } else {
            ChatUtil.error(menuItem.getClicker(), "自治体機能はdiscordとminecraftを連携することによって使用できます。\niphone内の「discordと連携する」から、discordとminecraftを連携してください。");
        }
    }

    private void manage(MenuItem menuItem) {
        List<?> data = (List<?>) menuItem.getCustomData();
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setOwningPlayer(Bukkit.getOfflinePlayer(Objects.requireNonNull(sqlUtil.getMunicipal((String) data.get(0))).uuid()));
        item.setItemMeta(skull);
        List<MenuItem> menus = new ArrayList<>();
        if(menuItem.getClicker().isOp()){
            ItemStack removeItem = new ItemStack(Material.BARRIER);
            removeItem.lore(List.of(Component.text("Moderator only")));
            menus.add(new MenuItem("自治体を削除する",this::remove,true,removeItem,data));
        }
        menus.add(new MenuItem("この自治体の住民になる",this::addResidents,true,Material.NAME_TAG,data.get(0)));
        menus.add(new MenuItem("この自治体の住民一覧を見る",this::listResidents,true,Material.BOOK,data));
        menus.add(new MenuItem("前ページに戻る",this::executeGui,true,Material.ARROW,null,8,0));
        Gui.getInstance().openMenu(menuItem.getClicker(),"自治体:"+data.get(1),menus);
    }

    private void addResidents(MenuItem menuItem) {
        String data = (String) menuItem.getCustomData();
        sqlUtil.MunicipalData municipalData = sqlUtil.getMunicipal(data);
        List<String> member = Objects.requireNonNull(municipalData).member();
        member.add(menuItem.getClicker().getUniqueId().toString());
        sqlUtil.addMunicipal(municipalData.uuid(),data,member);
        ChatUtil.success(menuItem.getClicker(),"正常に自治体の住民になりました！");
    }

    private void remove(MenuItem menuItem) {
        List<?> data = (List<?>) menuItem.getCustomData();
        final DynmapAPI dynmap = MetaversePlugin.getDynmap();
        final MarkerAPI marker = dynmap.getMarkerAPI();
        MarkerSet markerSet = marker.getMarkerSet("municipal");
        if (markerSet == null) {
            markerSet = marker.createMarkerSet("municipal", "自治体", null, true);
        }
        AreaMarker areaMarker = markerSet.findAreaMarker((String) data.get(0));
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getWorld(areaMarker.getWorld()))));
        Objects.requireNonNull(regions).removeRegion("region-"+data.get(0));
        areaMarker.deleteMarker();
        sqlUtil.removeMunicipal((String) data.get(0));
    }

    private void listResidents(MenuItem menuItem) {
        List<?> data = (List<?>) menuItem.getCustomData();
        sqlUtil.MunicipalData municipalData = Objects.requireNonNull(sqlUtil.getMunicipal((String) data.get(0)));
        Gui.getInstance().openMultiPageMenu(menuItem.getClicker(), "自治体の住民一覧", municipalData.member().stream().map(n -> {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skull = (SkullMeta) item.getItemMeta();
            skull.setOwningPlayer(Bukkit.getOfflinePlayer(municipalData.uuid()));
            item.setItemMeta(skull);
            return new MenuItem(Bukkit.getOfflinePlayer(municipalData.uuid()).getName(),null,false,item);
        }).collect(Collectors.toList()),new MenuItem("前ページに戻る",this::manage,true,Material.ARROW,data));
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
