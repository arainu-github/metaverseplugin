package world.arainu.core.metaverseplugin.listener;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.iphone.Bank;
import world.arainu.core.metaverseplugin.store.BankStore;
import world.arainu.core.metaverseplugin.utils.BankNotice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 銀行に関するイベントをハンドリングしているクラス
 * @author kumitatepazuru
 */
public class BankListener implements Listener {
    /**
     * インベントリをクリックしたときに発火する関数
     * @param e イベント
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        HashMap<UUID, Integer> gui_hashmap = BankStore.getGui_hashmap();
        if (BankStore.getGui_hashmap().containsKey(p.getUniqueId())) {
            Bukkit.getScheduler().runTaskLater(MetaversePlugin.getInstance(), () -> {
                Inventory inv = e.getInventory();
                List<ItemStack> money_list = new ArrayList<>(inv.all(Material.EMERALD).values());
                int total_money = 0;
                for (ItemStack i : money_list) {
                    PersistentDataContainer persistentDataContainer = i.getItemMeta().getPersistentDataContainer();
                    if (persistentDataContainer.has(BankStore.getKey(), PersistentDataType.INTEGER)) {
                        total_money += persistentDataContainer.get(BankStore.getKey(), PersistentDataType.INTEGER) * i.getAmount();
                    }
                }
                try {
                    int required_money = Objects.requireNonNull(BankStore.getGui_hashmap().get(p.getUniqueId()));
                    if (total_money >= required_money) {
                        final Economy econ = MetaversePlugin.getEcon();
                        Bank.addMoneyForPlayer(p, total_money - required_money);
                        econ.depositPlayer(p, required_money);
                        p.sendMessage(ChatColor.GREEN + "[メタバースプラグイン] " + econ.format(required_money) + "を正常に入金しました。");
                        gui_hashmap.remove(p.getUniqueId());
                        BankStore.setGui_hashmap(gui_hashmap);
                        inv.clear();
                        inv.close();
                    }
                } catch (NullPointerException ignored){
                }
            }, 1L);
        }
    }

    /**
     * GUIを閉じたときに発火する関数
     * @param e イベント
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (BankStore.getGui_hashmap().containsKey(p.getUniqueId())) {
            Gui.warning(p,"お金の入金を取りやめました。");
            HashMap<UUID, Integer> gui_hashmap = BankStore.getGui_hashmap();

            Inventory oldInv = e.getInventory();
            List<ItemStack> items = new ArrayList<>();
            for (int i = 0, size = oldInv.getSize(); i < size; i++) {
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
     * @param e イベント
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Economy econ = MetaversePlugin.getEcon();

        if (BankStore.getRemittance_map().containsKey(e.getPlayer().getUniqueId())){
            List<BankNotice> remittance = BankStore.getRemittance_map().get(e.getPlayer().getUniqueId());
            for(BankNotice i : remittance){
                e.getPlayer().sendMessage(ChatColor.GREEN + "[メタバースプラグイン] " + i.getPlayerUID() + "があなたへ" + i.getFormatedMoney() + "送金しました。");
                e.getPlayer().sendMessage(ChatColor.GREEN + "[メタバースプラグイン] 所持金は" + econ.format(econ.getBalance(e.getPlayer())) + "です。");
            }
        }
        HashMap<UUID,Long> login_money_map = BankStore.getLogin_money_map();
        login_money_map.put(e.getPlayer().getUniqueId(),System.currentTimeMillis() / 1000);
        BankStore.setLogin_money_map(login_money_map);
    }

    /**
     * プレイヤーが退出したときに発火する関数
     * @param e イベント
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        HashMap<UUID,Long> login_money_map = BankStore.getLogin_money_map();
        login_money_map.remove(e.getPlayer().getUniqueId());
        BankStore.setLogin_money_map(login_money_map);
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent e){
        CraftingInventory inv = e.getInventory();
        if(Objects.equals(inv.getResult(), new ItemStack(Material.EMERALD_BLOCK))){
            for (ItemStack item :inv){
                if(item.getItemMeta().getPersistentDataContainer().has(BankStore.getKey(), PersistentDataType.INTEGER))
                    inv.setResult(new ItemStack(Material.AIR));
            }
        }
    }
}
