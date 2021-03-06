package world.arainu.core.metaverseplugin.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.iphone.Drilling;
import world.arainu.core.metaverseplugin.scheduler.DrillingScheduler;
import world.arainu.core.metaverseplugin.scheduler.ParticleScheduler;
import world.arainu.core.metaverseplugin.utils.ChatUtil;
import world.arainu.core.metaverseplugin.utils.ItemUtil;
import world.arainu.core.metaverseplugin.utils.ParticleUtil;
import world.arainu.core.metaverseplugin.utils.SoundUtil;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * ??????????????????????????????????????????
 *
 * @author kumitatepazuru
 */
public class DrillingListener implements Listener {
    @Getter
    private static DrillingListener instance;
    private final HashMap<Inventory, Block> invList = new HashMap<>();
    private final HashMap<Block, ParticleUtil> particleMap = new HashMap<>();
    private final HashMap<Block, ParticleUtil> particleDrillingMap = new HashMap<>();
    private final HashMap<Block, DrillingScheduler> drillingTaskMap = new HashMap<>();
    private final List<Location> locationList = new ArrayList<>();

    /**
     * ????????????SQL???????????????????????????
     */
    public DrillingListener() {
        instance = this;
        for (sqlUtil.returnDrilling i : Objects.requireNonNull(sqlUtil.getDrillingBlocks())) {
            Block block = i.location().getWorld().getBlockAt(i.location());
            if (block.getType() == Material.CHEST) {
                Inventory inv = ((Chest) block.getState()).getInventory();
                ItemStack pickaxe = inv.getItem(0);
                if (pickaxe == null) {
                    pickaxe = new ItemStack(Material.AIR);
                }
                ItemStack shovel = inv.getItem(1);
                if (shovel == null) {
                    shovel = new ItemStack(Material.AIR);
                }
                block.removeMetadata("metaverse-drilling", MetaversePlugin.getInstance());
                block.removeMetadata("metaverse-drilling__vector", MetaversePlugin.getInstance());
                block.removeMetadata("metaverse-drilling__pickaxe", MetaversePlugin.getInstance());
                block.removeMetadata("metaverse-drilling__shovel", MetaversePlugin.getInstance());
                block.removeMetadata("metaverse-drilling__vector2", MetaversePlugin.getInstance());
                block.removeMetadata("metaverse-drilling__item", MetaversePlugin.getInstance());
                block.setMetadata("metaverse-drilling", new FixedMetadataValue(MetaversePlugin.getInstance(), i.player()));
                block.setMetadata("metaverse-drilling__vector", new FixedMetadataValue(MetaversePlugin.getInstance(), i.vector3D()));
                block.setMetadata("metaverse-drilling__pickaxe", new FixedMetadataValue(MetaversePlugin.getInstance(), pickaxe));
                block.setMetadata("metaverse-drilling__shovel", new FixedMetadataValue(MetaversePlugin.getInstance(), shovel));
                block.setMetadata("metaverse-drilling__vector2", new FixedMetadataValue(MetaversePlugin.getInstance(), i.vector3D2()));
                block.setMetadata("metaverse-drilling__item", new FixedMetadataValue(MetaversePlugin.getInstance(), i.item()));
                block.setMetadata("metaverse-drilling__starting", new FixedMetadataValue(MetaversePlugin.getInstance(), i.starting()));
                block.setType(Material.BRICKS);
                createCube(block, Objects.requireNonNull(i.vector3D()));
                locationList.add(i.location());

                if (i.starting()) {
                    startDrilling(block, Bukkit.getOfflinePlayer(i.player()), i.vector3D());
                }
            } else {
                MetaversePlugin.logger().warning("drilling block " + i + " is not found.removed.");
            }
        }
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????
     *
     * @param e ???????????????
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();
        if (!block.hasMetadata("metaverse-drilling")) {
            return;
        }
        UUID playerUID = (UUID) block.getMetadata("metaverse-drilling").get(0).value();
        if (!Objects.requireNonNull(playerUID).equals(player.getUniqueId())) {
            ChatUtil.error(player, "????????????????????????????????????????????????????????????????????????????????????");
            e.setCancelled(true);
            return;
        }
        final ItemStack pickaxe = (ItemStack) block.getMetadata("metaverse-drilling__pickaxe").get(0).value();
        final ItemStack shovel = (ItemStack) block.getMetadata("metaverse-drilling__shovel").get(0).value();
        if (Objects.requireNonNull(pickaxe).getType() != Material.AIR) {
            ItemUtil.addItem(pickaxe, player.getInventory(), player);
        }
        if (Objects.requireNonNull(shovel).getType() != Material.AIR) {
            ItemUtil.addItem(shovel, player.getInventory(), player);
        }
        locationList.remove(block.getLocation());
        block.removeMetadata("metaverse-drilling", MetaversePlugin.getInstance());
        block.removeMetadata("metaverse-drilling__vector", MetaversePlugin.getInstance());
        block.removeMetadata("metaverse-drilling__pickaxe", MetaversePlugin.getInstance());
        block.removeMetadata("metaverse-drilling__shovel", MetaversePlugin.getInstance());
        block.removeMetadata("metaverse-drilling__vector2", MetaversePlugin.getInstance());
        block.removeMetadata("metaverse-drilling__item", MetaversePlugin.getInstance());
        block.removeMetadata("metaverse-drilling__starting", MetaversePlugin.getInstance());
        ParticleScheduler.removeQueue(particleMap.get(block));
        particleMap.remove(block);
        ParticleScheduler.removeQueue(particleDrillingMap.get(block));
        particleDrillingMap.remove(block);
        if (drillingTaskMap.containsKey(block)) {
            ChatUtil.warning(player, "??????????????????????????????????????????????????????????????????????????????");
            drillingTaskMap.get(block).cancel();
            drillingTaskMap.remove(block);
        }
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     *
     * @param e ???????????????
     */
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        e.blockList().forEach(block -> {
            if (block.hasMetadata("metaverse-drilling")) {
                Bukkit.getServer().getScheduler().runTaskLater(MetaversePlugin.getInstance(), () -> block.setType(Material.BRICKS), 1);
            }
        });
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     *
     * @param e ???????????????
     */
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        e.blockList().forEach(block -> {
            if (block.hasMetadata("metaverse-drilling")) {
                Bukkit.getServer().getScheduler().runTaskLater(MetaversePlugin.getInstance(), () -> block.setType(Material.BRICKS), 1);
            }
        });
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param e ????????????
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        if (item.getItemMeta().getPersistentDataContainer().has(Drilling.getKey(), PersistentDataType.INTEGER)) {
            Player p = e.getPlayer();
            final ItemStack air = new ItemStack(Material.AIR);
            final Vector vector3D = new Vector(5, 1, 5);
            locationList.add(e.getBlockPlaced().getLocation());
            e.getBlockPlaced().setMetadata("metaverse-drilling", new FixedMetadataValue(MetaversePlugin.getInstance(), p.getUniqueId()));
            e.getBlockPlaced().setMetadata("metaverse-drilling__vector", new FixedMetadataValue(MetaversePlugin.getInstance(), vector3D));
            e.getBlockPlaced().setMetadata("metaverse-drilling__pickaxe", new FixedMetadataValue(MetaversePlugin.getInstance(), air));
            e.getBlockPlaced().setMetadata("metaverse-drilling__shovel", new FixedMetadataValue(MetaversePlugin.getInstance(), air));
            e.getBlockPlaced().setMetadata("metaverse-drilling__vector2", new FixedMetadataValue(MetaversePlugin.getInstance(), new Vector(0, 0, 0)));
            e.getBlockPlaced().setMetadata("metaverse-drilling__item", new FixedMetadataValue(MetaversePlugin.getInstance(), false));
            e.getBlockPlaced().setMetadata("metaverse-drilling__starting", new FixedMetadataValue(MetaversePlugin.getInstance(), false));
            createCube(e.getBlockPlaced(), vector3D);
        }
    }

    private void createCube(Block block, Vector vector3D) {
        final Location location = block.getLocation();
        final int X = (int) location.getX();
        final int Y = (int) location.getY();
        final int Z = (int) location.getZ();
        ParticleUtil particle = new ParticleUtil();
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z, X + 1 + vector3D.getX(), Y, Z, location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z + vector3D.getZ(), X + 1 + vector3D.getX(), Y, Z + vector3D.getZ(), location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z, X + 1, Y, Z + vector3D.getZ(), location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1 + vector3D.getX(), Y, Z, X + 1 + vector3D.getX(), Y, Z + vector3D.getZ(), location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z, X + 1, Y + vector3D.getY(), Z, location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z + vector3D.getZ(), X + 1, Y + vector3D.getY(), Z + vector3D.getZ(), location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1 + vector3D.getX(), Y, Z, X + 1 + vector3D.getX(), Y + vector3D.getY(), Z, location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1 + vector3D.getX(), Y, Z + vector3D.getZ(), X + 1 + vector3D.getX(), Y + vector3D.getY(), Z + vector3D.getZ(), location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y + vector3D.getY(), Z, X + 1 + vector3D.getX(), Y + vector3D.getY(), Z, location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y + vector3D.getY(), Z + vector3D.getZ(), X + 1 + vector3D.getX(), Y + vector3D.getY(), Z + vector3D.getZ(), location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y + vector3D.getY(), Z, X + 1, Y + vector3D.getY(), Z + vector3D.getZ(), location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1 + vector3D.getX(), Y + vector3D.getY(), Z, X + 1 + vector3D.getX(), Y + vector3D.getY(), Z + vector3D.getZ(), location.getWorld(), null));
        particle.addBlockLine(block, null);
        ParticleScheduler.addQueue(particle);
        particleMap.put(block, particle);
    }

    private void update(Inventory inv, Block block) {
        inv.clear();
        List<String> loc = Arrays.asList("X", "Y", "Z");
        int count = 0;
        final Vector vector3D = Objects.requireNonNull((Vector) block.getMetadata("metaverse-drilling__vector").get(0).value());
        final ItemStack pickaxe = Objects.requireNonNull((ItemStack) block.getMetadata("metaverse-drilling__pickaxe").get(0).value());
        final ItemStack shovel = Objects.requireNonNull((ItemStack) block.getMetadata("metaverse-drilling__shovel").get(0).value());
        final Vector startPos = Objects.requireNonNull((Vector) block.getMetadata("metaverse-drilling__vector2").get(0).value());
        final boolean isItem = block.getMetadata("metaverse-drilling__item").get(0).asBoolean();
        Economy econ = MetaversePlugin.getEcon();

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
            itemMeta.displayName(Component.text(i + "??????????????????????????????").color(NamedTextColor.RED));
            down_button.setItemMeta(itemMeta);

            final ItemStack display = new ItemStack(Material.PAPER);
            itemMeta = display.getItemMeta();
            final int amount;
            switch (i) {
                case "X" -> amount = (int) vector3D.getX();
                case "Y" -> amount = (int) vector3D.getY();
                case "Z" -> amount = (int) vector3D.getZ();
                default -> throw new IllegalStateException("Unexpected value: " + i);
            }
            itemMeta.displayName(Component.text(i + "??????????????????:" + amount));
            display.setItemMeta(itemMeta);

            final ItemStack up_button = new ItemStack(Material.GREEN_WOOL);
            itemMeta = up_button.getItemMeta();
            itemMeta.displayName(Component.text(i + "??????????????????????????????").color(NamedTextColor.GREEN));
            up_button.setItemMeta(itemMeta);

            inv.setItem(count * 9 + 1, down_button);
            inv.setItem(count * 9 + 2, display);
            inv.setItem(count * 9 + 3, up_button);
            count++;
        }

        final ItemStack descriptionPaper1 = new ItemStack(Material.PAPER);
        itemMeta = descriptionPaper1.getItemMeta();
        itemMeta.displayName(Component.text("??????????????????????????????????????????????????????????????????"));
        descriptionPaper1.setItemMeta(itemMeta);
        inv.setItem(6, descriptionPaper1);
        inv.setItem(15, shovel);

        final ItemStack descriptionPaper2 = new ItemStack(Material.PAPER);
        itemMeta = descriptionPaper2.getItemMeta();
        itemMeta.displayName(Component.text("??????????????????????????????????????????????????????????????????"));
        descriptionPaper2.setItemMeta(itemMeta);
        inv.setItem(5, descriptionPaper2);
        inv.setItem(14, pickaxe);

        final ItemStack startButton;
        if (drillingTaskMap.containsKey(block)) {
            startButton = new ItemStack(Material.BARRIER);
            itemMeta = startButton.getItemMeta();
            itemMeta.displayName(Component.text("?????????????????????").color(NamedTextColor.RED));
        } else {
            startButton = new ItemStack(Material.REDSTONE);
            itemMeta = startButton.getItemMeta();
            itemMeta.displayName(Component.text("?????????????????????").color(NamedTextColor.GREEN));
            if (startPos.getX() + startPos.getY() + startPos.getZ() == 0) {
                final int drillingAmount;
                if (isItem) {
                    drillingAmount = (int) (vector3D.getX() * vector3D.getY() * vector3D.getZ() * 30);
                } else {
                    drillingAmount = (int) (vector3D.getX() * vector3D.getY() * vector3D.getZ() * 10);
                }
                itemMeta.lore(Arrays.asList(
                        Component.text((int) (vector3D.getX() * vector3D.getY() * vector3D.getZ()) + "???????????????"),
                        Component.text(econ.format(drillingAmount) + "????????????????????????????????????")));
            }
        }
        startButton.setItemMeta(itemMeta);
        inv.setItem(16, new ItemStack(startButton));

        final ItemStack itemButton;
        if (isItem) {
            itemButton = new ItemStack(Material.CHEST);
            itemMeta = itemButton.getItemMeta();
            itemMeta.displayName(Component.text("????????????????????????????????????"));
            itemMeta.lore(Arrays.asList(Component.text("???????????????????????????????????????????????????????????????"), Component.text("??????????????????????????????????????????????????????????????????"), Component.text("??????????????????????????????")));
        } else {
            itemButton = new ItemStack(Material.STRUCTURE_VOID);
            itemMeta = itemButton.getItemMeta();
            itemMeta.displayName(Component.text("?????????????????????"));
            itemMeta.lore(Arrays.asList(Component.text("???????????????????????????????????????????????????"), Component.text("??????????????????????????????")));
        }
        itemButton.setItemMeta(itemMeta);
        inv.setItem(17, new ItemStack(itemButton));
        ParticleScheduler.removeQueue(particleMap.get(block));
        particleMap.remove(block);
        createCube(block, vector3D);
    }

    /**
     * ?????????????????????????????????????????????GUI??????????????????
     *
     * @param e ????????????
     */
    //????????????????????????????????????
    // ??? ????????????????????????+X??????
    // ?????????????????????????????????  ???+Z??????
    @EventHandler
    public void onBlockClick(PlayerInteractEvent e) {
        if (e.getAction().isRightClick() && !e.getPlayer().isSneaking() && e.getClickedBlock() != null) {
            Block block = e.getClickedBlock();
            if (!block.getMetadata("metaverse-drilling").isEmpty()) {
                UUID playerUID = (UUID) block.getMetadata("metaverse-drilling").get(0).value();
                if (Objects.requireNonNull(playerUID).equals(e.getPlayer().getUniqueId())) {
                    e.setCancelled(true);
                    Inventory inv = Bukkit.createInventory(null, 27, Component.text("??????????????????"));
                    update(inv, block);
                    e.getPlayer().openInventory(inv);
                    invList.put(inv, block);
                } else {
                    ChatUtil.error(e.getPlayer(), "????????????????????????????????????????????????????????????????????????????????????");
                }
            }
        }
    }

    /**
     * ?????????????????????GUI????????????????????????????????????
     *
     * @param e ????????????
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!invList.containsKey(e.getInventory())) return;
        final Player p = (Player) e.getWhoClicked();
        final int id = e.getRawSlot();
        final Block block = invList.get(e.getInventory());
        final boolean isItem = block.getMetadata("metaverse-drilling__item").get(0).asBoolean();
        Vector vector3D = Objects.requireNonNull((Vector) block.getMetadata("metaverse-drilling__vector").get(0).value());
        e.setCancelled(true);

        switch (id) {
            case 1 -> {
                final Vector pos = Objects.requireNonNull((Vector) block.getMetadata("metaverse-drilling__vector2").get(0).value());
                if (pos.getX() + pos.getY() + pos.getZ() == 0) {
                    if (vector3D.getX() > 0) {
                        vector3D.subtract(new Vector(1, 0, 0));
                        update(e.getInventory(), block);
                        SoundUtil.playClickSound(p);
                    }
                } else {
                    ChatUtil.error(p, "???????????????????????????????????????????????????");
                }
            }
            case 10 -> {
                final Vector pos = Objects.requireNonNull((Vector) block.getMetadata("metaverse-drilling__vector2").get(0).value());
                if (pos.getX() + pos.getY() + pos.getZ() == 0) {
                    if (vector3D.getY() > 0) {
                        vector3D.subtract(new Vector(0, 1, 0));
                        update(e.getInventory(), block);
                        SoundUtil.playClickSound(p);
                    }
                } else {
                    ChatUtil.error(p, "???????????????????????????????????????????????????");
                }
            }
            case 19 -> {
                final Vector pos = Objects.requireNonNull((Vector) block.getMetadata("metaverse-drilling__vector2").get(0).value());
                if (pos.getX() + pos.getY() + pos.getZ() == 0) {
                    if (vector3D.getZ() > 0) {
                        vector3D.subtract(new Vector(0, 0, 1));
                        update(e.getInventory(), block);
                        SoundUtil.playClickSound(p);
                    }
                } else {
                    ChatUtil.error(p, "???????????????????????????????????????????????????");
                }
            }
            case 3 -> {
                final Vector pos = Objects.requireNonNull((Vector) block.getMetadata("metaverse-drilling__vector2").get(0).value());
                if (pos.getX() + pos.getY() + pos.getZ() == 0) {
                    if (vector3D.getX() < 100) {
                        vector3D.add(new Vector(1, 0, 0));
                        update(e.getInventory(), block);
                        SoundUtil.playClickSound(p);
                    }
                } else {
                    ChatUtil.error(p, "???????????????????????????????????????????????????");
                }
            }
            case 12 -> {
                final Vector pos = Objects.requireNonNull((Vector) block.getMetadata("metaverse-drilling__vector2").get(0).value());
                if (pos.getX() + pos.getY() + pos.getZ() == 0) {
                    if (vector3D.getY() < 50) {
                        vector3D.add(new Vector(0, 1, 0));
                        update(e.getInventory(), block);
                        SoundUtil.playClickSound(p);
                    }
                } else {
                    ChatUtil.error(p, "???????????????????????????????????????????????????");
                }
            }
            case 21 -> {
                final Vector pos = Objects.requireNonNull((Vector) block.getMetadata("metaverse-drilling__vector2").get(0).value());
                if (pos.getX() + pos.getY() + pos.getZ() == 0) {
                    if (vector3D.getZ() < 100) {
                        vector3D.add(new Vector(0, 0, 1));
                        update(e.getInventory(), block);
                        SoundUtil.playClickSound(p);
                    }
                }
            }
            case 16 -> {
                e.getInventory().close();
                invList.remove(e.getInventory());
                SoundUtil.playClickSound(p);
                if (drillingTaskMap.containsKey(block)) {
                    drillingTaskMap.get(block).ended = 3;
                } else {
                    final Vector startPos = Objects.requireNonNull((Vector) block.getMetadata("metaverse-drilling__vector2").get(0).value());
                    boolean drillingOk = true;
                    if (startPos.getX() + startPos.getY() + startPos.getZ() == 0) {
                        Economy econ = MetaversePlugin.getEcon();
                        final int drillingAmount;
                        if (isItem) {
                            drillingAmount = (int) (vector3D.getX() * vector3D.getY() * vector3D.getZ() * 30);
                        } else {
                            drillingAmount = (int) (vector3D.getX() * vector3D.getY() * vector3D.getZ() * 10);
                        }
                        if (econ.has(p, drillingAmount)) {
                            ChatUtil.success(p, econ.format(drillingAmount) + "?????????????????????????????????????????????");
                            econ.withdrawPlayer(p, drillingAmount);
                        } else {
                            ChatUtil.error(p, "?????????????????????????????????????????????????????????\n??????: " + econ.format(econ.getBalance(p)) + "\n????????????: " + econ.format(drillingAmount));
                            drillingOk = false;
                        }
                    } else {
                        ChatUtil.success(p, "????????????????????????????????????");
                    }
                    if (drillingOk) {
                        startDrilling(block, p, vector3D);
                    }
                }
            }
            case 17 -> {
                final Vector pos = Objects.requireNonNull((Vector) block.getMetadata("metaverse-drilling__vector2").get(0).value());
                if (pos.getX() + pos.getY() + pos.getZ() == 0) {
                    SoundUtil.playClickSound(p);
                    if (isItem)
                        block.setMetadata("metaverse-drilling__item", new FixedMetadataValue(MetaversePlugin.getInstance(), false));
                    else
                        block.setMetadata("metaverse-drilling__item", new FixedMetadataValue(MetaversePlugin.getInstance(), true));
                    update(e.getInventory(), block);
                } else {
                    ChatUtil.error(p, "???????????????????????????????????????????????????");
                }
            }
            default -> {
                if (id > 26) {
                    if (e.getClick() != ClickType.SHIFT_LEFT && e.getClick() != ClickType.SHIFT_RIGHT) {
                        e.setCancelled(false);
                    }
                }
            }
        }
        setToolData(id,block,e);
    }

    private void setToolData(int id, Block block, InventoryInteractEvent e){
        switch(id) {
            case 14 -> {
                e.setCancelled(false);
                Bukkit.getScheduler().runTaskLater(MetaversePlugin.getInstance(), () -> {
                    if (drillingTaskMap.containsKey(block)) {
                        drillingTaskMap.get(block).cancel();
                    }
                    ItemStack item = e.getInventory().getItem(14);
                    if (item == null) {
                        item = new ItemStack(Material.AIR);
                    }
                    block.removeMetadata("metaverse-drilling__pickaxe", MetaversePlugin.getInstance());
                    block.setMetadata("metaverse-drilling__pickaxe", new FixedMetadataValue(MetaversePlugin.getInstance(), item));
                }, 1);
            }
            case 15 -> {
                e.setCancelled(false);
                Bukkit.getScheduler().runTaskLater(MetaversePlugin.getInstance(), () -> {
                    if (drillingTaskMap.containsKey(block)) {
                        drillingTaskMap.get(block).cancel();
                    }
                    ItemStack item = e.getInventory().getItem(15);
                    if (item == null) {
                        item = new ItemStack(Material.AIR);
                    }
                    block.removeMetadata("metaverse-drilling__shovel", MetaversePlugin.getInstance());
                    block.setMetadata("metaverse-drilling__shovel", new FixedMetadataValue(MetaversePlugin.getInstance(), item));
                }, 1);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e){
        if (!invList.containsKey(e.getInventory())) return;
        final Block block = invList.get(e.getInventory());
        for (Integer integer : e.getRawSlots()) {
            setToolData(integer, block, e);
        }
    }

    private void startDrilling(Block block, OfflinePlayer p, Vector vector3D) {
        block.removeMetadata("metaverse-drilling__starting", MetaversePlugin.getInstance());
        block.setMetadata("metaverse-drilling__starting", new FixedMetadataValue(MetaversePlugin.getInstance(), true));
        Bukkit.getScheduler().runTaskTimer(MetaversePlugin.getInstance(),
                (r) -> {
                    boolean ok = true;
                    try {
                        final Vector pos = Objects.requireNonNull((Vector) block.getMetadata("metaverse-drilling__vector2").get(0).value());
                        if (drillingTaskMap.containsKey(block)) {
                            switch (drillingTaskMap.get(block).ended) {
                                case 0 -> ok = false;
                                case 3 -> {
                                    ParticleScheduler.removeQueue(particleDrillingMap.get(block));
                                    particleDrillingMap.remove(block);
                                    if (p.isOnline()) {
                                        ChatUtil.warning(Objects.requireNonNull(p.getPlayer()), "????????????????????????????????????");
                                    }
                                    drillingTaskMap.remove(block);
                                    ok = false;
                                    block.removeMetadata("metaverse-drilling__starting", MetaversePlugin.getInstance());
                                    block.setMetadata("metaverse-drilling__starting", new FixedMetadataValue(MetaversePlugin.getInstance(), false));
                                    r.cancel();
                                }
                            }
                        }

                        if (pos.getX() > vector3D.getX() - 1) {
                            pos.setX(0);
                            pos.add(new Vector(0, 0, 1));
                        }
                        if (pos.getZ() > vector3D.getZ() - 1) {
                            pos.setZ(0);
                            pos.add(new Vector(0, 1, 0));
                        }
                        if (pos.getY() > vector3D.getY() - 1) {
                            ParticleScheduler.removeQueue(particleDrillingMap.get(block));
                            particleDrillingMap.remove(block);
                            if (p.isOnline()) {
                                ChatUtil.success(p.getPlayer(), "???????????????????????????????????????");
                            }
                            block.removeMetadata("metaverse-drilling__starting", MetaversePlugin.getInstance());
                            block.setMetadata("metaverse-drilling__starting", new FixedMetadataValue(MetaversePlugin.getInstance(), false));
                            drillingTaskMap.remove(block);
                            pos.zero();
                            ok = false;
                            r.cancel();
                        }
                        if (ok) {
                            ParticleScheduler.removeQueue(particleDrillingMap.get(block));
                            particleDrillingMap.remove(block);
                            final ItemStack pickaxe = Objects.requireNonNull((ItemStack) block.getMetadata("metaverse-drilling__pickaxe").get(0).value());
                            final ItemStack shovel = Objects.requireNonNull((ItemStack) block.getMetadata("metaverse-drilling__shovel").get(0).value());
                            final Location location = block.getLocation();
                            location.add(pos.getX() + 1, pos.getY(), pos.getZ());

                            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(p.getPlayer());
                            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                            RegionQuery query = container.createQuery();
                            final StateFlag.State canBreak = query.queryState(BukkitAdapter.adapt(location), localPlayer, Flags.BLOCK_BREAK);

                            if (canBreak == StateFlag.State.DENY && !p.isOp()) {
                                if (p.isOnline()) {
                                    ChatUtil.warning(Objects.requireNonNull(p.getPlayer()), "????????????????????????X:" + location.getBlockX() + ",Y:" + location.getBlockY() + ",Z:" + location.getBlockZ() + "???????????????????????????????????????");
                                }
                                pos.add(new Vector(1, 0, 0));
                            } else {
                                final Block nextBlock = block.getWorld().getBlockAt(location);
                                List<Integer> delayList = Arrays.asList(getDelay(nextBlock, pickaxe), getDelay(nextBlock, shovel), getDelay(nextBlock, new ItemStack(Material.AIR)));
                                int minDelay = Collections.min(delayList);
                                int index = delayList.indexOf(minDelay);
                                if (index != 2) {
                                    if (minDelay >= delayList.get(2)) {
                                        index = 2;
                                        minDelay = delayList.get(2);
                                    }
                                }

                                final ItemStack useTool;
                                if (index == 0) {
                                    useTool = pickaxe;
                                } else if (index == 1) {
                                    useTool = shovel;
                                } else {
                                    useTool = new ItemStack(Material.AIR);
                                }
                                removeDurability(useTool);
                                final ParticleUtil particleUtil = new ParticleUtil();
                                particleUtil.addBlockLine(nextBlock, null);
                                ParticleScheduler.addQueue(particleUtil);
                                particleDrillingMap.put(block, particleUtil);

                                DrillingScheduler newTask = new DrillingScheduler(block, useTool);
                                newTask.runTaskLater(MetaversePlugin.getInstance(), minDelay);
                                drillingTaskMap.remove(block);
                                drillingTaskMap.put(block, newTask);
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {
                        r.cancel();
                    }
                }, 0, 1);
    }

    private void removeDurability(ItemStack item) {
        if (item.getItemMeta() instanceof Damageable meta) {
            final int level = item.getEnchantments().getOrDefault(Enchantment.DURABILITY, 0);
            Random random = new Random();
            int randomValue = random.nextInt(100);
            if (randomValue < 100 / (level + 1)) {
                meta.setDamage(meta.getDamage() + 1);
                if (meta.getDamage() == item.getType().getMaxDurability()) {
                    item.setType(Material.AIR);
                } else {
                    item.setItemMeta(meta);
                }
            }
        }
    }

    private int getDelay(Block block, ItemStack item) {
        double multiply = 5;
        if (block.isValidTool(item)) {
            multiply = 1.5;
        }
        final int delay;
        float blockDestroySpeed;
        try {
            blockDestroySpeed = block.getDestroySpeed(item, true);
        } catch (NullPointerException e) {
            blockDestroySpeed = 1;
        }
        if (block.getType().getHardness() * 30 <= blockDestroySpeed) {
            delay = 0;
        } else {
            delay = (int) (block.getType().getHardness() * multiply / blockDestroySpeed * 20 + 6);
        }
        return delay;
    }

    /**
     * ?????????????????????????????????????????????????????????????????????
     */
    public void saveData() {
        MetaversePlugin.logger().info("saving drilling data...");
        sqlUtil.truncateDrillingBlock();
        for (Location i : locationList) {
            MetaversePlugin.logger().info("drilling location:"+i);
            Block block = i.getWorld().getBlockAt(i);
            UUID player = (UUID) block.getMetadata("metaverse-drilling").get(0).value();
            Vector vector3D = (Vector) block.getMetadata("metaverse-drilling__vector").get(0).value();
            Vector vector3D2 = (Vector) block.getMetadata("metaverse-drilling__vector2").get(0).value();
            ItemStack pickaxe = (ItemStack) block.getMetadata("metaverse-drilling__pickaxe").get(0).value();
            ItemStack shovel = (ItemStack) block.getMetadata("metaverse-drilling__shovel").get(0).value();
            boolean item = block.getMetadata("metaverse-drilling__item").get(0).asBoolean();
            boolean starting = block.getMetadata("metaverse-drilling__starting").get(0).asBoolean();
            block.setType(Material.CHEST);
            Chest chest = (Chest) block.getState();
            chest.getInventory().addItem(Objects.requireNonNull(pickaxe), Objects.requireNonNull(shovel));
            sqlUtil.addDrillingBlock(i, Objects.requireNonNull(player), Objects.requireNonNull(vector3D), Objects.requireNonNull(vector3D2), item, starting);
        }
    }
}
