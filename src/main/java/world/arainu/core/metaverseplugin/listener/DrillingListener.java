package world.arainu.core.metaverseplugin.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
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
import world.arainu.core.metaverseplugin.utils.ChatUtil;
import world.arainu.core.metaverseplugin.utils.ParticleUtil;
import world.arainu.core.metaverseplugin.utils.SoundUtil;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DrillingListener implements Listener {
    HashMap<Inventory, Block> invList = new HashMap<>();
    HashMap<Block, ParticleUtil> particleMap = new HashMap<>();
    HashMap<Block, ParticleUtil> particleDrillingMap = new HashMap<>();
    HashMap<Block, DrillingScheduler> drillingTaskMap = new HashMap<>();

    public DrillingListener() {
        for (Location i : Objects.requireNonNull(sqlUtil.getDrillingBlocks())) {
            Block block = i.getWorld().getBlockAt(i);
            if (block.getMetadata("metaverse-drilling__vector").size() == 0) {
                Bukkit.getLogger().warning("drilling block " + i + " is not found.removed.");
                sqlUtil.removeDrillingBlock(i);
            } else {
                Vector3D vector3D = (Vector3D) block.getMetadata("metaverse-drilling__vector").get(0).value();
                createCube(block, Objects.requireNonNull(vector3D));
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().hasMetadata("metaverse-drilling")) {
            final ItemStack pickaxe = (ItemStack) e.getBlock().getMetadata("metaverse-drilling__pickaxe").get(0).value();
            final ItemStack shovel = (ItemStack) e.getBlock().getMetadata("metaverse-drilling__shovel").get(0).value();
            if(Objects.requireNonNull(pickaxe).getType() != Material.AIR){
                e.getPlayer().getInventory().addItem(pickaxe);
            }
            if(Objects.requireNonNull(shovel).getType() != Material.AIR){
                e.getPlayer().getInventory().addItem(shovel);
            }
            sqlUtil.removeDrillingBlock(e.getBlock().getLocation());
            e.getBlock().removeMetadata("metaverse-drilling", MetaversePlugin.getInstance());
            e.getBlock().removeMetadata("metaverse-drilling__vector", MetaversePlugin.getInstance());
            e.getBlock().removeMetadata("metaverse-drilling__pickaxe", MetaversePlugin.getInstance());
            e.getBlock().removeMetadata("metaverse-drilling__shovel", MetaversePlugin.getInstance());
            ParticleScheduler.removeQueue(particleMap.get(e.getBlock()));
            particleMap.remove(e.getBlock());
            particleDrillingMap.remove(e.getBlock());
            if(drillingTaskMap.containsKey(e.getBlock())){
                drillingTaskMap.get(e.getBlock()).ended = 4;
            }
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
            e.getBlockPlaced().setMetadata("metaverse-drilling", new FixedMetadataValue(MetaversePlugin.getInstance(), p));
            e.getBlockPlaced().setMetadata("metaverse-drilling__vector", new FixedMetadataValue(MetaversePlugin.getInstance(), vector3D));
            e.getBlockPlaced().setMetadata("metaverse-drilling__pickaxe", new FixedMetadataValue(MetaversePlugin.getInstance(), air));
            e.getBlockPlaced().setMetadata("metaverse-drilling__shovel", new FixedMetadataValue(MetaversePlugin.getInstance(), air));
            e.getBlockPlaced().setMetadata("metaverse-drilling__vector2", new FixedMetadataValue(MetaversePlugin.getInstance(), new Vector3D(0,0,0)));
            createCube(e.getBlockPlaced(), vector3D);
        }
    }

    private void createCube(Block block, Vector3D vector3D) {
        final Location location = block.getLocation();
        final int X = (int) location.getX();
        final int Y = (int) location.getY();
        final int Z = (int) location.getZ();
        ParticleUtil particle = new ParticleUtil();
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z, X + 1 + vector3D.x, Y, Z, location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z + vector3D.z, X + 1 + vector3D.x, Y, Z + vector3D.z, location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z, X + 1, Y, Z + vector3D.z, location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1 + vector3D.x, Y, Z, X + 1 + vector3D.x, Y, Z + vector3D.z, location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z, X + 1, Y + vector3D.y, Z, location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z + vector3D.z, X + 1, Y + vector3D.y, Z + vector3D.z, location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1 + vector3D.x, Y, Z, X + 1 + vector3D.x, Y + vector3D.y, Z, location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1 + vector3D.x, Y, Z + vector3D.z, X + 1 + vector3D.x, Y + vector3D.y, Z + vector3D.z, location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y + vector3D.y, Z, X + 1 + vector3D.x, Y + vector3D.y, Z, location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y + vector3D.y, Z + vector3D.z, X + 1 + vector3D.x, Y + vector3D.y, Z + vector3D.z, location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1, Y + vector3D.y, Z, X + 1, Y + vector3D.y, Z + vector3D.z, location.getWorld(), null));
        particle.addThinLine(new ParticleUtil.Vector3D(X + 1 + vector3D.x, Y + vector3D.y, Z, X + 1 + vector3D.x, Y + vector3D.y, Z + vector3D.z, location.getWorld(), null));
        particle.addBlockLine(block,null);
        ParticleScheduler.addQueue(particle);
        particleMap.put(block, particle);
    }

    private void update(Inventory inv, Block block) {
        inv.clear();
        List<String> loc = Arrays.asList("X", "Y", "Z");
        int count = 0;
        final Vector3D vector3D = Objects.requireNonNull((Vector3D) block.getMetadata("metaverse-drilling__vector").get(0).value());
        final ItemStack pickaxe = Objects.requireNonNull((ItemStack) block.getMetadata("metaverse-drilling__pickaxe").get(0).value());
        final ItemStack shovel = Objects.requireNonNull((ItemStack) block.getMetadata("metaverse-drilling__shovel").get(0).value());
        final Vector3D startPos = Objects.requireNonNull((Vector3D) block.getMetadata("metaverse-drilling__vector2").get(0).value());
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
        inv.setItem(6, descriptionPaper1);
        inv.setItem(15, shovel);

        final ItemStack descriptionPaper2 = new ItemStack(Material.PAPER);
        itemMeta = descriptionPaper2.getItemMeta();
        itemMeta.displayName(Component.text("下に採掘時に使用するツルハシを入れてください。"));
        descriptionPaper2.setItemMeta(itemMeta);
        inv.setItem(5, descriptionPaper2);
        inv.setItem(14, pickaxe);

        final ItemStack startButton;
        if(drillingTaskMap.containsKey(block)){
            startButton = new ItemStack(Material.BARRIER);
            itemMeta = startButton.getItemMeta();
            itemMeta.displayName(Component.text("採掘を停止する").color(NamedTextColor.RED));
        } else {
            startButton = new ItemStack(Material.REDSTONE);
            itemMeta = startButton.getItemMeta();
            itemMeta.displayName(Component.text("採掘を開始する").color(NamedTextColor.GREEN));
            if(startPos.x+startPos.y+ startPos.z == 0){
                itemMeta.lore(Arrays.asList(
                        Component.text((int) (vector3D.x* vector3D.y* vector3D.z)+"ブロック分"),
                        Component.text(econ.format(vector3D.x* vector3D.y* vector3D.z*10)+"が銀行から引き下ろされます。")));
            }
        }
        startButton.setItemMeta(itemMeta);
        inv.setItem(16, new ItemStack(startButton));
        ParticleScheduler.removeQueue(particleMap.get(block));
        particleMap.remove(block);
        createCube(block, vector3D);
    }

    //○┏━━━━━━━━━┓
    // ┃ 整地箇所　┃　→+X方向
    // ┗━━━━━━━━━┛  ↓+Z方向
    @EventHandler
    public void onBlockClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = Objects.requireNonNull(e.getClickedBlock());
            if (!block.getMetadata("metaverse-drilling").isEmpty()) {
                if(block.getMetadata("metaverse-drilling").get(0).value() == e.getPlayer()) {
                    e.setCancelled(true);
                    Inventory inv = Bukkit.createInventory(null, 27, Component.text("採掘マシーン"));
                    update(inv, block);
                    e.getPlayer().openInventory(inv);
                    invList.put(inv, block);
                } else {
                    ChatUtil.error(e.getPlayer(),"他のプレイヤーの採掘マシーンを操作することはできません！");
                }
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
                    update(e.getInventory(), block);
                    SoundUtil.playClickSound(p);
                }
            }
            case 10 -> {
                if (vector3D.y > 0) {
                    vector3D.y--;
                    update(e.getInventory(), block);
                    SoundUtil.playClickSound(p);
                }
            }
            case 19 -> {
                if (vector3D.z > 0) {
                    vector3D.z--;
                    update(e.getInventory(), block);
                    SoundUtil.playClickSound(p);
                }
            }
            case 3 -> {
                if (vector3D.x < 100) {
                    vector3D.x++;
                    update(e.getInventory(), block);
                    SoundUtil.playClickSound(p);
                }
            }
            case 12 -> {
                if (vector3D.y < 50) {
                    vector3D.y++;
                    update(e.getInventory(), block);
                    SoundUtil.playClickSound(p);
                }
            }
            case 21 -> {
                if (vector3D.z < 100) {
                    vector3D.z++;
                    update(e.getInventory(), block);
                    SoundUtil.playClickSound(p);
                }
            }
            case 14 -> {
                e.setCancelled(false);
                Bukkit.getScheduler().runTaskLater(MetaversePlugin.getInstance(),() -> {
                    if(drillingTaskMap.containsKey(block)){
                        drillingTaskMap.get(block).cancel();
                    }
                    ItemStack item = e.getInventory().getItem(14);
                    if(item == null){
                        item = new ItemStack(Material.AIR);
                    }
                    block.removeMetadata("metaverse-drilling__pickaxe", MetaversePlugin.getInstance());
                    block.setMetadata("metaverse-drilling__pickaxe",new FixedMetadataValue(MetaversePlugin.getInstance(),item));
                },1);
            }
            case 15 -> {
                e.setCancelled(false);
                Bukkit.getScheduler().runTaskLater(MetaversePlugin.getInstance(),() -> {
                    if(drillingTaskMap.containsKey(block)){
                        drillingTaskMap.get(block).cancel();
                    }
                    ItemStack item = e.getInventory().getItem(15);
                    if(item == null){
                        item = new ItemStack(Material.AIR);
                    }
                    block.removeMetadata("metaverse-drilling__shovel", MetaversePlugin.getInstance());
                    block.setMetadata("metaverse-drilling__shovel",new FixedMetadataValue(MetaversePlugin.getInstance(),item));
                },1);
                }
            case 16 -> {
                e.getInventory().close();
                invList.remove(e.getInventory());
                SoundUtil.playClickSound(p);
                if(drillingTaskMap.containsKey(block)){
                    drillingTaskMap.get(block).ended = 3;
                } else {
                    final Vector3D startPos = Objects.requireNonNull((Vector3D) block.getMetadata("metaverse-drilling__vector2").get(0).value());
                    boolean drillingOk = true;
                    if(startPos.x+startPos.y+startPos.z == 0){
                        Economy econ = MetaversePlugin.getEcon();
                        final int drillingAmount = (int) (vector3D.x * vector3D.y * vector3D.z * 10);
                        if(econ.has(p,drillingAmount)){
                            ChatUtil.success(p, econ.format(drillingAmount)+"を徴収し、採掘を開始しました。");
                            econ.withdrawPlayer(p,drillingAmount);
                        } else {
                            ChatUtil.error(p,"あなたはそこまでお金を持っていません！\n残高: "+econ.format(econ.getBalance(p))+"\n必要料金: "+econ.format(drillingAmount));
                            drillingOk = false;
                        }
                    } else {
                        ChatUtil.success(p, "採掘を再度開始しました。");
                    }
                    if(drillingOk){
                        Bukkit.getScheduler().runTaskTimer(MetaversePlugin.getInstance(),
                                (r) -> {
                                    boolean ok = true;
                                    final Vector3D pos = Objects.requireNonNull((Vector3D) block.getMetadata("metaverse-drilling__vector2").get(0).value());
                                    if (drillingTaskMap.containsKey(block)) {
                                        switch (drillingTaskMap.get(block).ended) {
                                            case 0 -> ok = false;
                                            case 3 -> {
                                                ParticleScheduler.removeQueue(particleDrillingMap.get(block));
                                                particleDrillingMap.remove(block);
                                                ChatUtil.warning(p, "採掘を一時停止しました。");
                                                drillingTaskMap.remove(block);
                                                ok = false;
                                                r.cancel();
                                            }
                                            case 4 -> {
                                                ParticleScheduler.removeQueue(particleDrillingMap.get(block));
                                                particleDrillingMap.remove(block);
                                                ChatUtil.warning(p, "採掘マシーンが破壊されたため採掘を強制終了しました。");
                                                drillingTaskMap.remove(block);
                                                ok = false;
                                                r.cancel();
                                            }
                                        }
                                    }

                                    if (pos.x > vector3D.x - 1) {
                                        pos.x = 0;
                                        pos.z++;
                                    }
                                    if (pos.z > vector3D.z - 1) {
                                        pos.z = 0;
                                        pos.y++;
                                    }
                                    if (pos.y > vector3D.y - 1) {
                                        ParticleScheduler.removeQueue(particleDrillingMap.get(block));
                                        particleDrillingMap.remove(block);
                                        ChatUtil.success(p, "採掘が正常に完了しました。");
                                        drillingTaskMap.remove(block);
                                        pos.x = 0;
                                        pos.y = 0;
                                        pos.z = 0;
                                        ok = false;
                                        r.cancel();
                                    }
                                    if (ok) {
                                        ParticleScheduler.removeQueue(particleDrillingMap.get(block));
                                        particleDrillingMap.remove(block);
                                        final ItemStack pickaxe = (ItemStack) block.getMetadata("metaverse-drilling__pickaxe").get(0).value();
                                        final ItemStack shovel = (ItemStack) block.getMetadata("metaverse-drilling__shovel").get(0).value();
                                        final Location location = block.getLocation();
                                        location.add(pos.x + 1, pos.y, pos.z);

                                        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(p);
                                        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                                        RegionQuery query = container.createQuery();
                                        final StateFlag.State canBreak = query.queryState(BukkitAdapter.adapt(location), localPlayer, Flags.BLOCK_BREAK);

                                        if (canBreak == StateFlag.State.DENY && !p.isOp()) {
                                            ChatUtil.warning(p, "保護区域のため、X:" + location.getBlockX() + ",Y:" + location.getBlockY() + ",Z:" + location.getBlockZ() + "の採掘ができませんでした。");
                                            pos.x++;
                                        } else {
                                            final Block nextBlock = block.getWorld().getBlockAt(location);
                                            final ItemStack useTool;
                                            if (nextBlock.isPreferredTool(Objects.requireNonNull(pickaxe)) && pickaxe.getType() != Material.AIR) {
                                                useTool = pickaxe;
                                            } else if (nextBlock.isPreferredTool(Objects.requireNonNull(shovel))) {
                                                useTool = shovel;
                                            } else {
                                                useTool = new ItemStack(Material.AIR);
                                            }
                                            double multiply = 5;
                                            if (nextBlock.isValidTool(useTool)) {
                                                multiply = 1.5;
                                            }
                                            final int delay;
                                            if (nextBlock.getType().getHardness() * 30 <= nextBlock.getDestroySpeed(useTool,true)) {
                                                delay = 0;
                                            } else {
                                                delay = (int) (nextBlock.getType().getHardness() * multiply / nextBlock.getDestroySpeed(useTool,true) * 20 + 6);
                                            }

                                            final ParticleUtil particleUtil = new ParticleUtil();
                                            particleUtil.addBlockLine(nextBlock, null);
                                            ParticleScheduler.addQueue(particleUtil);
                                            particleDrillingMap.put(block, particleUtil);

                                            DrillingScheduler newTask = new DrillingScheduler(block, vector3D);
                                            newTask.runTaskLater(MetaversePlugin.getInstance(), delay);
                                            drillingTaskMap.remove(block);
                                            drillingTaskMap.put(block, newTask);
                                        }
                                    }
                                }, 0, 1);
                    }
                }
            }
            default -> {
                if(id > 26){
                    e.setCancelled(false);
                }
            }
        }
    }
}
