package world.arainu.core.metaverseplugin.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import world.arainu.core.metaverseplugin.MetaversePlugin;

import java.util.*;
import java.util.function.Consumer;

/**
 * UI システムのメインクラス。
 *
 * @author kumitatepazuru
 */
public class Gui implements Listener {
    private final HashMap<Inventory, MenuData> multiPageMenuMap = new HashMap<>();

    /**
     * Guiで主に使用するItemStackと場所(index)を紐付けるクラス
     */
    public record PosItemStack(ItemStack item, int index) {
    }

    /**
     * インスタンスを取得します。
     *
     * @return インスタンス
     */
    public static Gui getInstance() {
        return instance == null ? (instance = new Gui()) : instance;
    }

    /**
     * 内部的に使用するものです。
     */
    public static void resetInstance() {
        instance = null;
    }

    /**
     * メニューを開きます。
     *
     * @param player メニューを開くプレイヤー
     * @param title  メニューのタイトル
     * @param items  メニューのアイテム
     */
    public void openMenu(Player player, String title, Collection<MenuItem> items) {
        if (isBedrock(player)) {
            openMenuBedrockImpl(player, title, items.toArray(MenuItem[]::new));
        } else {
            openMenuJavaImpl(player, title, items.toArray(MenuItem[]::new));
        }
    }

    /**
     * JavaでインベントリをメニューUIとして使うため、そのハンドリングを行います。
     *
     * @param e ハンドリングに使用するイベント
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        final Inventory inv = e.getInventory();
        final HumanEntity p = e.getWhoClicked();

        // 管理インベントリでなければ無視
        if (!invMap.containsKey(inv)) return;
        e.setCancelled(true);

        final HashMap<Integer, MenuItem> menuItems = invMap.get(inv);
        final int id = e.getRawSlot();

        if (id < 0) return;
        else if (!menuItems.containsKey(id)) return;
        final MenuItem clickedMenuItem = menuItems.get(id);
        runHandler(clickedMenuItem,(Player) p);
    }

    /**
     * JavaでインベントリをメニューUIとして使うため、そのハンドリングを行います。
     *
     * @param e ハンドリングに使用するイベント
     */
    @EventHandler
    public void MultiMenuInventoryClick(InventoryClickEvent e) {
        final Inventory inv = e.getInventory();
        final Player p = (Player) e.getWhoClicked();
        final int id = e.getRawSlot();

        if (multiPageMenuMap.containsKey(inv)) {
            e.setCancelled(true);
            MenuData invData = multiPageMenuMap.get(inv);

            switch (id) {
                case 3 -> {
                    if (invData.page() != 1) {
                        update(inv, invData.page() - 1, invData.menuItems(),invData.centerItem());
                        multiPageMenuMap.replace(inv, new MenuData(invData.menuItems(), invData.centerItem(), invData.page() - 1));
                    }
                }
                case 5 -> {
                    if (invData.page() < Math.floor(invData.menuItems().size() / 18f) + 1) {
                        update(inv, invData.page() + 1, invData.menuItems(),invData.centerItem());
                        multiPageMenuMap.replace(inv, new MenuData(invData.menuItems(), invData.centerItem(), invData.page() + 1));
                    }
                }
                case 4 -> {
                    final MenuItem clickedMenuItem = invData.centerItem();
                    runHandler(clickedMenuItem,p);
                }
            }
            if (id > 8 && id < 27 && e.getCurrentItem() != null) {
                final MenuItem clickedMenuItem = invData.menuItems().get((invData.page()-1)*18+id-9);
                runHandler(clickedMenuItem,p);
            }
        }
    }

    private void runHandler(MenuItem clickedMenuItem,Player p){
        if (clickedMenuItem.isClose()) {
            p.closeInventory();
        }
        clickedMenuItem.setClicker(p);
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1, 1f);
        final Consumer<MenuItem> handler = clickedMenuItem.getOnClick();
        if (handler != null) handler.accept(clickedMenuItem);
    }

    /**
     * JavaでインベントリをメニューUIとして使うため、そのハンドリングを行います。
     *
     * @param e ハンドリングに使用するイベント
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        final Inventory inv = e.getInventory();
        // GC
        invMap.remove(inv);
        multiPageMenuMap.remove(inv);
    }

    private void openMenuJavaImpl(Player player, String title, MenuItem[] items) {
        Integer max = Arrays.stream(items).map(MenuItem::getY).max(Comparator.naturalOrder()).orElse(0);
        final int size = Math.max(1 + (items.length - 1) / 9, max) * 9;
        final Inventory inv = Bukkit.createInventory(null, size, Component.text(title));
        final HashMap<Integer, MenuItem> itemmap = new HashMap<>();
        final int[] count = {0};

        Arrays.stream(items).map(i -> {
            final ItemStack item = i.getIcon();
            if (i.isShiny()) {
                item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            }
            final int index;
            if (i.getX() > -1) {
                index = i.getX() + i.getY() * 9;
            } else {
                while (itemmap.containsKey(count[0])) {
                    count[0]++;
                }
                index = count[0];
            }
            itemmap.put(index, i);

            return new PosItemStack(item, index);
        }).forEach(i -> inv.setItem(i.index(), i.item()));

        invMap.put(inv, itemmap);
        player.openInventory(inv);
    }

    private void openMenuBedrockImpl(Player player, String title, MenuItem[] items) {
        final SimpleForm.Builder builder = SimpleForm.builder()
                .title(title);

        for (var item : items) {
            List<String> buttonText = new ArrayList<>();
            Component text = item.getIcon().getItemMeta().displayName();
            if (text == null) {
                text = item.getIcon().displayName();
            }
            if (item.isShiny()) {
                text = text.color(NamedTextColor.GREEN);
            }
            if (text instanceof TextComponent) buttonText.add(((TextComponent) text).content());
            else buttonText.add(item.getIcon().getI18NDisplayName());
            if(item.getIcon().lore() != null) {
                for (Component i : Objects.requireNonNull(item.getIcon().lore())) {
                    if(i instanceof TextComponent) {
                        buttonText.add(((TextComponent) i).content());
                    }
                }
            }
            if (item.isClose()) {
                builder.button(String.join("\n",buttonText));
            } else {
                builder.content(String.join("\n",buttonText));
            }
        }

        builder.responseHandler((form, data) -> {
            final SimpleFormResponse res = form.parseResponse(data);
            if (!res.isCorrect()) {
                return;
            }

            final int id = Math.toIntExact(res.getClickedButtonId() + Arrays.stream(items).filter(value -> !value.isClose()).count());
            final Consumer<MenuItem> callback = items[id].getOnClick();
            if (callback != null) {
                items[id].setClicker(player);
                callback.accept(items[id]);
            }
        });

        final FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        fPlayer.sendForm(builder);
    }

    /**
     * プレイヤーがBE勢かを調べる
     *
     * @param player 対象のプレイヤー
     * @return BEの場合はtrue
     */
    public static boolean isBedrock(Player player) {
        return FloodgateApi.getInstance().isFloodgateId(player.getUniqueId());
    }

    /**
     * プレイヤーがエンドにいるか調べる
     *
     * @param player プレイヤー
     * @return エンドにいる場合はtrue
     */
    public static boolean isPlayerInEnd(Player player) {
        return player.getWorld().getEnvironment() == World.Environment.THE_END;
    }

    /**
     * エンドラが死んでいるかどうか調べる
     *
     * @param player 　プレイヤー
     * @return エンドラが死んでる場合はtrue
     */

    public static boolean isEnderDragonLiving(Player player) {
        List<LivingEntity> entity = player.getWorld().getLivingEntities();
        for (LivingEntity i : entity) {
            if (i instanceof EnderDragon) return true;
        }
        return false;
    }

    public void openMultiPageMenu(Player p, String title, List<MenuItem> menuItems,MenuItem centerItem) {
        if (!p.getWorld().getName().equals("world")) {
            p.teleport(new Location(Bukkit.getWorld("world"), 0, 0, 0));
        }
        if (isBedrock(p)) {
            menuItems.add(0,centerItem);
            openMenuBedrockImpl(p,title, menuItems.toArray(new MenuItem[0]));
        } else {
            Inventory inv = Bukkit.createInventory(null, 27, Component.text(title));
            update(inv, 1,menuItems,centerItem);
            p.openInventory(inv);
            multiPageMenuMap.put(inv, new MenuData(menuItems, centerItem,1));
        }
    }

    public void openMultiPageMenu(Player p, String title, List<MenuItem> menuItems){
        openMultiPageMenu(p,title,menuItems,null);
    }

    private void update(Inventory inv, int page, List<MenuItem> menuItems,MenuItem centerItem) {
        inv.clear();
        final ItemStack back_button = new ItemStack(Material.RED_WOOL);
        ItemMeta itemMeta = back_button.getItemMeta();
        itemMeta.displayName(Component.text("前ページ"));
        back_button.setItemMeta(itemMeta);

        final ItemStack up_button = new ItemStack(Material.GREEN_WOOL);
        itemMeta = up_button.getItemMeta();
        itemMeta.displayName(Component.text("次ページ"));
        up_button.setItemMeta(itemMeta);

        final ItemStack centerButton = centerItem.getIcon();

        inv.setItem(3, back_button);
        inv.setItem(4, centerButton);
        inv.setItem(5, up_button);
        int count = 0;
        for (MenuItem i : menuItems) {
            if ((page - 1) * 18 - 1 < count && count < page * 18) {
                    inv.setItem(count + 9 - (page - 1) * 18, i.getIcon());
            }
            count++;
        }
    }

    record MenuData(List<MenuItem> menuItems, MenuItem centerItem, int page) {
    }

    private final HashMap<Inventory, HashMap<Integer, MenuItem>> invMap = new HashMap<>();
    private static Gui instance;
}
