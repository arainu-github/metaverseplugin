package world.arainu.core.metaverseplugin.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.iphone.Bank;
import world.arainu.core.metaverseplugin.store.BankStore;
import world.arainu.core.metaverseplugin.utils.BankNotice;
import world.arainu.core.metaverseplugin.utils.ChatUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 銀行に関するイベントをハンドリングしているクラス
 *
 * @author kumitatepazuru
 */
public class BankListener implements Listener {
    /**
     * インベントリをクリックしたときに発火する関数
     *
     * @param e イベント
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        HashMap<UUID, Integer> gui_hashmap = BankStore.getGui_hashmap();
        if (BankStore.getGui_hashmap().containsKey(p.getUniqueId())) {
            final int id = e.getRawSlot();
            Inventory inv = e.getInventory();
            switch (id) {
                case 4 -> {
                    e.setCancelled(true);
                    Bukkit.getScheduler().runTaskLater(MetaversePlugin.getInstance(), () -> {
                        final VillagerListener.ReturnMoney money = VillagerListener.getTotalmoney(inv);
                        final int total_money = money.getTotal_money();
                        int required_money = Objects.requireNonNull(BankStore.getGui_hashmap().get(p.getUniqueId()));
                        if (total_money >= required_money) {
                            final Economy econ = MetaversePlugin.getEcon();
                            Bank.addMoneyForPlayer(p, total_money - required_money);
                            econ.depositPlayer(p, required_money);
                            ChatUtil.success(p, econ.format(required_money) + "を正常に入金しました。");
                            gui_hashmap.remove(p.getUniqueId());
                            BankStore.setGui_hashmap(gui_hashmap);
                            inv.clear();
                            inv.close();
                        }
                    }, 1L);
                }
                case 8 -> {
                    e.setCancelled(true);
                    final Inventory player_inv = e.getWhoClicked().getInventory();
                    final VillagerListener.ReturnMoney returnMoney = VillagerListener.getTotalmoney(player_inv);
                    for (ItemStack i : returnMoney.getMoney_list()) {
                        if (Bank.isMoney(i)) {
                            player_inv.remove(i);
                        }
                    }
                    Bank.addMoneyForInventory(inv, returnMoney.getTotal_money());
                }
                default -> {
                    if (id < 9) e.setCancelled(true);
                }
            }
            if (id != 4) {
                Bukkit.getScheduler().runTaskLater(MetaversePlugin.getInstance(), () -> {
                    VillagerListener.ReturnMoney money = VillagerListener.getTotalmoney(inv);
                    int total = money.getTotal_money();
                    int required_money = Objects.requireNonNull(BankStore.getGui_hashmap().get(p.getUniqueId()));
                    final ItemStack priceItem = Objects.requireNonNull(inv.getItem(4));
                    final ItemMeta itemMeta = priceItem.getItemMeta();
                    if (required_money > total) {
                        itemMeta.lore(Arrays.asList(
                                Component.text("クリックして入金").color(NamedTextColor.GRAY),
                                Component.text(MetaversePlugin.getEcon().format(required_money - total) + "不足しています").color(NamedTextColor.RED)
                        ));
                    } else {
                        itemMeta.lore(List.of(Component.text("クリックして入金").color(NamedTextColor.GREEN)));
                    }
                    priceItem.setItemMeta(itemMeta);
                }, 1);
            }
        }
    }

    /**
     * GUIを閉じたときに発火する関数
     *
     * @param e イベント
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (BankStore.getGui_hashmap().containsKey(p.getUniqueId())) {
            ChatUtil.warning(p, "お金の入金を取りやめました。");
            HashMap<UUID, Integer> gui_hashmap = BankStore.getGui_hashmap();

            Inventory oldInv = e.getInventory();
            List<ItemStack> items = new ArrayList<>();
            for (int i = 9, size = oldInv.getSize(); i < size; i++) {
                ItemStack item = oldInv.getItem(i);
                if (item == null) {
                    continue;
                }
                items.add(item);
            }
            oldInv.clear();
            Inventory newInv = p.getInventory();
            for (ItemStack item : items) {
                // アイテムを入れる
                newInv.addItem(item);
            }

            gui_hashmap.remove(p.getUniqueId());
            BankStore.setGui_hashmap(gui_hashmap);
        }
    }

    /**
     * プレイヤーがログインしてきたとき発火する関数
     *
     * @param e イベント
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Economy econ = MetaversePlugin.getEcon();

        if (BankStore.getRemittance_map().containsKey(e.getPlayer().getUniqueId())) {
            List<BankNotice> remittance = BankStore.getRemittance_map().get(e.getPlayer().getUniqueId());
            for (BankNotice i : remittance) {
                ChatUtil.success(e.getPlayer(), i.getPlayerUID() + "があなたへ" + i.getFormatedMoney() + "送金しました。\n所持金は" + econ.format(econ.getBalance(e.getPlayer())) + "です。");
            }
        }
        HashMap<UUID, Long> login_money_map = BankStore.getLogin_money_map();
        login_money_map.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() / 1000);
        BankStore.setLogin_money_map(login_money_map);
    }

    /**
     * プレイヤーが退出したときに発火する関数
     *
     * @param e イベント
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        HashMap<UUID, Long> login_money_map = BankStore.getLogin_money_map();
        login_money_map.remove(e.getPlayer().getUniqueId());
        BankStore.setLogin_money_map(login_money_map);
    }

    /**
     * プレイヤーがクラフトしたときに発火する関数。通貨でエメラルドブロックを作成できなくする
     *
     * @param e イベント
     */
    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent e) {
        CraftingInventory inv = e.getInventory();
        if (Objects.equals(inv.getResult(), new ItemStack(Material.EMERALD_BLOCK))) {
            for (ItemStack item : inv) {
                if (Bank.isMoney(item))
                    inv.setResult(new ItemStack(Material.AIR));
            }
        }
    }
}
