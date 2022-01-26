package world.arainu.core.metaverseplugin.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import world.arainu.core.metaverseplugin.iphone.ChestLock;
import world.arainu.core.metaverseplugin.utils.ChatUtil;

import java.util.Objects;
import java.util.UUID;

public class ChestLockListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getAction().isRightClick() && !player.isSneaking() && e.getClickedBlock() != null) {
            if (e.getClickedBlock().getType() == Material.CHEST) {
                Chest state = (Chest) e.getClickedBlock().getState();
                PersistentDataContainer persistentDataContainer = state.getPersistentDataContainer();
                if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                    if (player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(ChestLock.getChestIDKey(), PersistentDataType.INTEGER)) {
                        e.setCancelled(true);
                        if (persistentDataContainer.has(ChestLock.getChestIDKey(), PersistentDataType.STRING)) {
                            ChatUtil.error(player, "チェストをには既に鍵がかかっています！");
                        } else {
                            persistentDataContainer.set(ChestLock.getChestIDKey(), PersistentDataType.STRING, player.getUniqueId().toString());
                            state.update();
                            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                            ChatUtil.success(player, "チェストに鍵をかけました。\n今後はこのチェストの破壊や閲覧はあなたしかできません。\n再設置をすると鍵は外れます。");
                        }
                        return;
                    }
                }
                if (persistentDataContainer.has(ChestLock.getChestIDKey(), PersistentDataType.STRING)) {
                    UUID target = UUID.fromString(Objects.requireNonNull(persistentDataContainer.get(ChestLock.getChestIDKey(), PersistentDataType.STRING)));
                    if (!player.getUniqueId().equals(target)) {
                        player.sendActionBar(Component.text("チェストはロックされています！").color(NamedTextColor.RED));
                        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1, 1f);
                        e.setCancelled(true);
                    }
                }

            }
        }
    }
}
