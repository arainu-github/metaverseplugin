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
import org.bukkit.event.block.BlockBreakEvent;
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
import world.arainu.core.metaverseplugin.scheduler.DrillingScheduler;
import world.arainu.core.metaverseplugin.scheduler.ParticleScheduler;
import world.arainu.core.metaverseplugin.utils.ParticleUtil;
import world.arainu.core.metaverseplugin.utils.SoundUtil;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DrillingListener implements Listener {
    HashMap<Inventory, Block> invList = new HashMap<>();
    HashMap<Block, ParticleUtil> particleMap = new HashMap<>();
    HashMap<Block, DrillingScheduler> drillingTaskMap = new HashMap<>();

    public DrillingListener() {
        for (Location i : Objects.requireNonNull(sqlUtil.getDrillingBlocks())) {
            Block block = i.getWorld().getBlockAt(i);
            if (block.getMetadata("metaverse-drilling__vector").size() == 0) {
                Bukkit.getLogger().warning("drilling block " + i + " is not found.removed.");
                sqlUtil.removeDrillingBlock(i);
            } else {
                Vector3D vector3D = (Vector3D) block.getMetadata("metaverse-drilling__vector").get(0).value();
                Player p = (Player) block.getMetadata("metaverse-drilling__player").get(0).value();
                createCube(block, Objects.requireNonNull(vector3D), p);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().hasMetadata("metaverse-drilling")) {
            sqlUtil.removeDrillingBlock(e.getBlock().getLocation());
            e.getBlock().removeMetadata("metaverse-drilling", MetaversePlugin.getInstance());
            e.getBlock().removeMetadata("metaverse-drilling__vector", MetaversePlugin.getInstance());
            e.getBlock().removeMetadata("metaverse-drilling__player", MetaversePlugin.getInstance());
            e.getBlock().removeMetadata("metaverse-drilling__pickaxe", MetaversePlugin.getInstance());
            e.getBlock().removeMetadata("metaverse-drilling__shovel", MetaversePlugin.getInstance());
            ParticleScheduler.removeQueue(particleMap.get(e.getBlock()));
            particleMap.remove(e.getBlock());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        if (item.getItemMeta().getPersistentDataContainer().has(Drilling.getKey(), PersistentDataType.INTEGER)) {
            Player p = e.getPlayer();
            final ItemStack air = new ItemStack(Material.AIR);
            final Vector3D vector3D = new Vector3D(5, 1, 5);
            sqlUtil.addDrillingBlock(e.getBlockPlaced().getLocation());
            e.getBlockPlaced().setMetadata("metaverse-drilling", new FixedMetadataValue(MetaversePlugin.getInstance(), p.getUniqueId()));
            e.getBlockPlaced().setMetadata("metaverse-drilling__vector", new FixedMetadataValue(MetaversePlugin.getInstance(), vector3D));
            e.getBlockPlaced().setMetadata("metaverse-drilling__player", new FixedMetadataValue(MetaversePlugin.getInstance(), p));
            e.getBlockPlaced().setMetadata("metaverse-drilling__pickaxe", new FixedMetadataValue(MetaversePlugin.getInstance(), air));
            e.getBlockPlaced().setMetadata("metaverse-drilling__shovel", new FixedMetadataValue(MetaversePlugin.getInstance(), air));
            createCube(e.getBlockPlaced(), vector3D, p);
        }
    }

    private void createCube(Block block, Vector3D vector3D, Player p) {
        final Location location = block.getLocation();
        final int X = (int) location.getX();
        final int Y = (int) location.getY();
        final int Z = (int) location.getZ();
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
        particleMap.put(block, particle);
    }

    private void update(Inventory inv, Player p, Block block) {
        inv.clear();
        List<String> loc = Arrays.asList("X", "Y", "Z");
        int count = 0;
        Vector3D vector3D = Objects.requireNonNull((Vector3D) block.getMetadata("metaverse-drilling__vector").get(0).value());
        ItemStack pickaxe = Objects.requireNonNull((ItemStack) block.getMetadata("metaverse-drilling__pickaxe").get(0).value());
        ItemStack shovel = Objects.requireNonNull((ItemStack) block.getMetadata("metaverse-drilling__shovel").get(0).value());

        final ItemStack partition = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta itemMeta = partition.getItemMeta();
        itemMeta.displayName(Component.text(""));
        partition.setItemMeta(itemMeta);
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, partition);
        }

        for (String i : loc) {
            final ItemStack down_button = new ItemStack(Material.RED_WOOL);
            itemMeta = down_button.getItemMeta();
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

        final ItemStack descriptionPaper1 = new ItemStack(Material.PAPER);
        itemMeta = descriptionPaper1.getItemMeta();
        itemMeta.displayName(Component.text("下に採掘時に使用するシャベルを入れてください。"));
        descriptionPaper1.setItemMeta(itemMeta);
        inv.setItem(5, descriptionPaper1);
        inv.setItem(14, shovel);

        final ItemStack descriptionPaper2 = new ItemStack(Material.PAPER);
        itemMeta = descriptionPaper2.getItemMeta();
        itemMeta.displayName(Component.text("下に採掘時に使用するツルハシを入れてください。"));
        descriptionPaper2.setItemMeta(itemMeta);
        inv.setItem(4, descriptionPaper2);
        inv.setItem(13, pickaxe);

        final ItemStack startButton = new ItemStack(Material.REDSTONE);
        itemMeta = startButton.getItemMeta();
        itemMeta.displayName(Component.text("採掘を開始する").color(NamedTextColor.GREEN));
        startButton.setItemMeta(itemMeta);
        inv.setItem(15, new ItemStack(startButton));
//        Economy econ = MetaversePlugin.getEcon();
//        final int drillingAmount = (int) (vector3D.x * vector3D.y * vector3D.z);
        ParticleScheduler.removeQueue(particleMap.get(block));
        particleMap.remove(block);
        createCube(block, vector3D, p);
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
                update(inv, e.getPlayer(), block);
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
        Vector3D vector3D = Objects.requireNonNull((Vector3D) block.getMetadata("metaverse-drilling__vector").get(0).value());
        e.setCancelled(true);

        switch (id) {
            case 1 -> {
                if (vector3D.x > 0) {
                    vector3D.x--;
                    update(e.getInventory(), p, block);
                    SoundUtil.playClickSound(p);
                }
            }
            case 10 -> {
                if (vector3D.y > 0) {
                    vector3D.y--;
                    update(e.getInventory(), p, block);
                    SoundUtil.playClickSound(p);
                }
            }
            case 19 -> {
                if (vector3D.z > 0) {
                    vector3D.z--;
                    update(e.getInventory(), p, block);
                    SoundUtil.playClickSound(p);
                }
            }
            case 3 -> {
                if (vector3D.x < 100) {
                    vector3D.x++;
                    update(e.getInventory(), p, block);
                    SoundUtil.playClickSound(p);
                }
            }
            case 12 -> {
                if (vector3D.y < 50) {
                    vector3D.y++;
                    update(e.getInventory(), p, block);
                    SoundUtil.playClickSound(p);
                }
            }
            case 21 -> {
                if (vector3D.z < 100) {
                    vector3D.z++;
                    update(e.getInventory(), p, block);
                    SoundUtil.playClickSound(p);
                }
            }
            case 13 -> {
                e.setCancelled(false);
                Bukkit.getScheduler().runTaskLater(MetaversePlugin.getInstance(),() -> {
                    ItemStack item = e.getInventory().getItem(13);
                    if(item == null){
                        item = new ItemStack(Material.AIR);
                    }
                    block.removeMetadata("metaverse-drilling__pickaxe", MetaversePlugin.getInstance());
                    block.setMetadata("metaverse-drilling__pickaxe",new FixedMetadataValue(MetaversePlugin.getInstance(),item));
                },1);
            }
            case 14 -> {
                e.setCancelled(false);
                Bukkit.getScheduler().runTaskLater(MetaversePlugin.getInstance(),() -> {
                    ItemStack item = e.getInventory().getItem(14);
                    if(item == null){
                        item = new ItemStack(Material.AIR);
                    }
                    block.removeMetadata("metaverse-drilling__shovel", MetaversePlugin.getInstance());
                    block.setMetadata("metaverse-drilling__shovel",new FixedMetadataValue(MetaversePlugin.getInstance(),item));
                },1);
                }
            case 15 -> Bukkit.getScheduler().runTaskTimer(MetaversePlugin.getInstance(),
                    (r) -> {
                        boolean ok = true;
                        final Vector3D pos;
                        if (drillingTaskMap.containsKey(block)) {
                            if (drillingTaskMap.get(block).ended == 0) {
                                ok = false;
                            } else if(drillingTaskMap.get(block).ended == 2){
                                r.cancel();
                            }
                            pos = drillingTaskMap.get(block).getVector3D();
                        }else {
                            pos = new Vector3D(0,0,0);
                        }
//                            Bukkit.getLogger().info(String.valueOf(ok));
                        if(ok){
//                                Bukkit.getLogger().info(String.valueOf(drillingTaskPos.get(block)));
                            final ItemStack pickaxe = (ItemStack) block.getMetadata("metaverse-drilling__pickaxe").get(0).value();
                            final ItemStack shovel = (ItemStack) block.getMetadata("metaverse-drilling__shovel").get(0).value();

                            if (pos.x > vector3D.x-1) {
                                pos.x = 0;
                                pos.z++;
                            }
                            if (pos.z > vector3D.z-1) {
                                pos.z = 0;
                                pos.y++;
                            }
                            final Location location = block.getLocation();
                            location.add(pos.x+1,pos.y,pos.z);
                            final Block nextBlock = block.getWorld().getBlockAt(location);
                            final ItemStack useTool;
                            if(nextBlock.isPreferredTool(Objects.requireNonNull(pickaxe))){
                                useTool = pickaxe;
                            } else if(nextBlock.isPreferredTool(Objects.requireNonNull(shovel))){
                                useTool = shovel;
                            } else {
                                useTool = new ItemStack(Material.AIR);
                            }
                            double multiply = 5;
                            if(nextBlock.isValidTool(useTool)){
                                multiply = 1.5;
                            }
                            final int delay;
                            if(nextBlock.getType().getHardness()*30<=nextBlock.getDestroySpeed(useTool)){
                                delay = 0;
                            } else {
                                delay = (int) (nextBlock.getType().getHardness()*multiply/nextBlock.getDestroySpeed(useTool)*20+6);
                            }
                            DrillingScheduler newTask = new DrillingScheduler(block, pos, vector3D);
                            newTask.runTaskLater(MetaversePlugin.getInstance(), delay);
                            drillingTaskMap.remove(block);
                            drillingTaskMap.put(block, newTask);
                        }
                    }, 0, 1);
            default -> {
                if(id > 26){
                    e.setCancelled(false);
                }
            }
        }
    }
}
