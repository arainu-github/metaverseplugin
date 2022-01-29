package world.arainu.core.metaverseplugin.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.iphone.ChestLock;
import world.arainu.core.metaverseplugin.utils.ChatUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
                            InventoryHolder holder = state.getInventory().getHolder();
                            List<Chest> chests;
                            if (holder instanceof DoubleChest doubleChest) {
                                chests = Arrays.asList((Chest) doubleChest.getLeftSide(), (Chest) doubleChest.getRightSide());
                            } else {
                                chests = Collections.singletonList(state);
                            }
                            for (Chest i : chests) {
                                i.getPersistentDataContainer().set(ChestLock.getChestIDKey(), PersistentDataType.STRING, player.getUniqueId().toString());
                                i.update();
                            }
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

    /**
     * ブロックが爆破によって破壊されないようにする関数。
     *
     * @param e 　イベント
     */
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        breakCheck(e.blockList());
    }

    /**
     * ブロックが爆破によって破壊されないようにする関数。
     *
     * @param e 　イベント
     */
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        breakCheck(e.blockList());
    }

    /**
     * ロック済みチェストが所有者以外のプレイヤーによって破壊されないようにする関数。
     *
     * @param e 　イベント
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (block.getType().equals(Material.CHEST)) {
            PersistentDataContainer persistentDataContainer = ((Chest) block.getState()).getPersistentDataContainer();
            if (!(Objects.equals(persistentDataContainer.get(ChestLock.getChestIDKey(), PersistentDataType.STRING), e.getPlayer().getUniqueId().toString()))) {
                e.setCancelled(true);
            }
        }
    }

    private void breakCheck(List<Block> blockList) {
        blockList.forEach(block -> {
            if (block.getType().equals(Material.CHEST)) {
                PersistentDataContainer persistentDataContainer = ((Chest) block.getState()).getPersistentDataContainer();
                if (persistentDataContainer.has(ChestLock.getChestIDKey(), PersistentDataType.STRING)) {
                    String uuid = persistentDataContainer.get(ChestLock.getChestIDKey(), PersistentDataType.STRING);
                    List<ItemStack> items = new ArrayList<>();
                    for (int i = 0; i < 27; i++) {
                        items.add(((Chest) block.getState()).getInventory().getItem(i));
                    }
                    ((Chest) block.getState()).getInventory().clear();
                    block.setType(Material.AIR);
                    Bukkit.getServer().getScheduler().runTaskLater(MetaversePlugin.getInstance(), () -> {
                        block.setType(Material.CHEST);
                        ((Chest) block.getState()).getPersistentDataContainer().set(ChestLock.getChestIDKey(), PersistentDataType.STRING, Objects.requireNonNull(uuid));
                        for (int i = 0; i < 27; i++) {
                            ((Chest) block.getState()).getInventory().setItem(i, items.get(i));
                        }
                    }, 1);
                }
            }
        });
    }
}
