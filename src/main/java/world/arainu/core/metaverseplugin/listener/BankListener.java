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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.iphone.Bank;
import world.arainu.core.metaverseplugin.store.BankStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                int required_money = BankStore.getGui_hashmap().get(p.getUniqueId());
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
            p.sendMessage(ChatColor.GOLD + "[メタバースプラグイン] お金の入金を取りやめました。");
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
}
