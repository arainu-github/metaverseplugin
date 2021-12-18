package world.arainu.core.metaverseplugin.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.dynmap.utils.Vector3D;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.iphone.Drilling;
import world.arainu.core.metaverseplugin.scheduler.ParticleScheduler;
import world.arainu.core.metaverseplugin.utils.ParticleUtil;
import world.arainu.core.metaverseplugin.utils.SoundUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DrillingListener implements Listener {
    HashMap<Player, Vector3D> locData = new HashMap<>();
    HashMap<Inventory, Block> invList = new HashMap<>();
    HashMap<Player, ParticleUtil> particleMap = new HashMap<>();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Bukkit.getLogger().info(String.valueOf(e.getItemInHand()));
        ItemStack item = e.getItemInHand();
        if (item.getItemMeta().getPersistentDataContainer().has(Drilling.getKey(), PersistentDataType.INTEGER)) {
//            sqlUtil.addDrillingBlock(e.getPlayer().getUniqueId(),e.getBlockPlaced());
            e.getBlockPlaced().setMetadata("metaverse-drilling", new FixedMetadataValue(MetaversePlugin.getInstance(), 1));
            Bukkit.getLogger().info(String.valueOf(e.getBlockPlaced().getMetadata("metaverse-drilling").get(0).asInt()));
            locData.put(e.getPlayer(), new Vector3D(5, 1, 5));
        }
    }

    private void update(Inventory inv, Player p, Location location) {
        inv.clear();
        List<String> loc = Arrays.asList("X", "Y", "Z");
        int count = 0;
        Vector3D vector3D = locData.get(p);
        for (String i : loc) {
            final ItemStack down_button = new ItemStack(Material.RED_WOOL);
            ItemMeta itemMeta = down_button.getItemMeta();
            itemMeta.displayName(Component.text(i + "座標の掘る量を減らす").color(NamedTextColor.RED));
            down_button.setItemMeta(itemMeta);

            final ItemStack display = new ItemStack(Material.PAPER);
            itemMeta = display.getItemMeta();
            final int amount;
            switch (i) {
                case "X" -> amount = (int) vector3D.x;
                case "Y" -> amount = (int) vector3D.y;
                case "Z" -> amount = (int) vector3D.z;
                default -> throw new IllegalStateException("Unexpected value: " + i);
            }
            itemMeta.displayName(Component.text(i + "座標の掘る量:" + amount));
            display.setItemMeta(itemMeta);

            final ItemStack up_button = new ItemStack(Material.GREEN_WOOL);
            itemMeta = up_button.getItemMeta();
            itemMeta.displayName(Component.text(i + "座標の掘る量を増やす").color(NamedTextColor.GREEN));
            up_button.setItemMeta(itemMeta);

            inv.setItem(count * 9 + 1, down_button);
            inv.setItem(count * 9 + 2, display);
            inv.setItem(count * 9 + 3, up_button);
            count++;
        }
//        Economy econ = MetaversePlugin.getEcon();
//        final int drillingAmount = (int) (vector3D.x * vector3D.y * vector3D.z);
        if (particleMap.containsKey(p)) {
            ParticleScheduler.removeQueue(particleMap.get(p));
            particleMap.remove(p);
        }
        int X = (int) location.getX();
        int Y = (int) location.getY();
        int Z = (int) location.getZ();
        ParticleUtil particle = new ParticleUtil();
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z, X + 1 + vector3D.x, Y, Z, location.getWorld(), Collections.singletonList(p)));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z + vector3D.z, X + 1 + vector3D.x, Y, Z + vector3D.z, location.getWorld(), Collections.singletonList(p)));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z, X + 1, Y, Z + vector3D.z, location.getWorld(), Collections.singletonList(p)));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1 + vector3D.x, Y, Z, X + 1 + vector3D.x, Y, Z + vector3D.z, location.getWorld(), Collections.singletonList(p)));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z, X + 1, Y + vector3D.y, Z, location.getWorld(), Collections.singletonList(p)));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z + vector3D.z, X + 1, Y + vector3D.y, Z + vector3D.z, location.getWorld(), Collections.singletonList(p)));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1 + vector3D.x, Y, Z, X + 1 + vector3D.x, Y + vector3D.y, Z, location.getWorld(), Collections.singletonList(p)));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1 + vector3D.x, Y, Z + vector3D.z, X + 1 + vector3D.x, Y + vector3D.y, Z + vector3D.z, location.getWorld(), Collections.singletonList(p)));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y + vector3D.y, Z, X + 1 + vector3D.x, Y + vector3D.y, Z, location.getWorld(), Collections.singletonList(p)));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y + vector3D.y, Z + vector3D.z, X + 1 + vector3D.x, Y + vector3D.y, Z + vector3D.z, location.getWorld(), Collections.singletonList(p)));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y + vector3D.y, Z, X + 1, Y + vector3D.y, Z + vector3D.z, location.getWorld(), Collections.singletonList(p)));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1 + vector3D.x, Y + vector3D.y, Z, X + 1 + vector3D.x, Y + vector3D.y, Z + vector3D.z, location.getWorld(), Collections.singletonList(p)));
        ParticleScheduler.addQueue(particle);
        particleMap.put(p, particle);
    }

    //○┏━━━━━━━━━┓
    // ┃ 整地箇所　┃　→+X方向
    // ┗━━━━━━━━━┛  ↓+Z方向
    @EventHandler
    public void onBlockClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = Objects.requireNonNull(e.getClickedBlock());
            if (!block.getMetadata("metaverse-drilling").isEmpty()) {
                e.setCancelled(true);
                Inventory inv = Bukkit.createInventory(null, 27, Component.text("採掘マシーン"));
                update(inv, e.getPlayer(), block.getLocation());
                e.getPlayer().openInventory(inv);
                invList.put(inv, block);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!invList.containsKey(e.getInventory())) return;
        final Player p = (Player) e.getWhoClicked();
        final int id = e.getRawSlot();
        final Block block = invList.get(e.getInventory());
        e.setCancelled(true);

        switch (id) {
            case 1 -> {
                if (locData.get(p).x > 0) {
                    locData.get(p).x--;
                    update(e.getInventory(), p, block.getLocation());
                    SoundUtil.playClickSound(p);
                }
            }
            case 10 -> {
                if (locData.get(p).y > 0) {
                    locData.get(p).y--;
                    update(e.getInventory(), p, block.getLocation());
                    SoundUtil.playClickSound(p);
                }
            }
            case 19 -> {
                if (locData.get(p).z > 0) {
                    locData.get(p).z--;
                    update(e.getInventory(), p, block.getLocation());
                    SoundUtil.playClickSound(p);
                }
            }
            case 3 -> {
                if (locData.get(p).x < 100) {
                    locData.get(p).x++;
                    update(e.getInventory(), p, block.getLocation());
                    SoundUtil.playClickSound(p);
                }
            }
            case 12 -> {
                if (locData.get(p).y < 50) {
                    locData.get(p).y++;
                    update(e.getInventory(), p, block.getLocation());
                    SoundUtil.playClickSound(p);
                }
            }
            case 21 -> {
                if (locData.get(p).z < 100) {
                    locData.get(p).z++;
                    update(e.getInventory(), p, block.getLocation());
                    SoundUtil.playClickSound(p);
                }
            }
        }
    }
}
