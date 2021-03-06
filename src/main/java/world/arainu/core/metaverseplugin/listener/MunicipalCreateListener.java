package world.arainu.core.metaverseplugin.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.GenericMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.geysermc.cumulus.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.iphone.Municipal;
import world.arainu.core.metaverseplugin.scheduler.ParticleScheduler;
import world.arainu.core.metaverseplugin.store.ServerStore;
import world.arainu.core.metaverseplugin.utils.ChatUtil;
import world.arainu.core.metaverseplugin.utils.ParticleUtil;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ????????????????????????????????????????????????
 *
 * @author kumitatepazuru
 */
public class MunicipalCreateListener implements Listener {

    private final static HashMap<Player, ParticleUtil> playerParticle = new HashMap<>();

    static public MarkerSet getMunicipalMarker() {
        final DynmapAPI dynmap = MetaversePlugin.getDynmap();
        final MarkerAPI marker = dynmap.getMarkerAPI();
        MarkerSet markerSet = marker.getMarkerSet("municipal");
        if (markerSet == null) {
            markerSet = marker.createMarkerSet("municipal", "?????????", null, true);
        }
        return markerSet;
    }

    /**
     * ???????????????????????????????????????????????????????????????
     *
     * @param e ????????????
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() == Material.ENCHANTED_BOOK) {
            if (item.getItemMeta().getPersistentDataContainer().has(ServerStore.getMunicipalBookKey(), PersistentDataType.INTEGER)) {
                e.setCancelled(true);
                final List<MenuItem> menuItem;
                if (!ServerStore.getMarkerData().containsKey(e.getPlayer())) {
                    menuItem = List.of(
                            new MenuItem("???????????????????????????", this::create, true, Material.COBBLED_DEEPSLATE)
                    );
                } else if (ServerStore.getMarkerData().get(e.getPlayer()).size() < 3) {
                    menuItem = List.of(
                            new MenuItem("???????????????????????????", this::add, true, Material.CALCITE),
                            new MenuItem("????????????????????????", this::begin, true, Material.REDSTONE_BLOCK)
                    );
                } else {
                    menuItem = List.of(
                            new MenuItem("???????????????????????????", this::add, true, Material.CALCITE),
                            new MenuItem("????????????????????????", this::begin, true, Material.REDSTONE_BLOCK),
                            new MenuItem("??????????????????", this::end, true, Material.TUFF)
                    );
                }
                Gui.getInstance().openMenu(e.getPlayer(), "???????????????????????????", menuItem);
            }
        }
    }

    private void begin(MenuItem menuItem) {
        Player p = menuItem.getClicker();
        ParticleScheduler.removeQueue(playerParticle.get(p));
        playerParticle.remove(p);
        final HashMap<Player, ArrayList<Location>> markerData = ServerStore.getMarkerData();
        markerData.remove(p);
        ServerStore.setMarkerData(markerData);
        ChatUtil.success(p, "????????????????????????????????????????????????????????????");
    }

    private void create(MenuItem menuItem) {
        final Player player = menuItem.getClicker();
        final HashMap<Player, ArrayList<Location>> markerData = ServerStore.getMarkerData();
        markerData.put(player, new ArrayList<>() {{
            add(player.getLocation());
        }});
        ServerStore.setMarkerData(markerData);
        player.sendMessage(Component.text("?????????????????????????????????????????????Java????????????????????????????????????????????????????????????????????????????????????????????????\n")
                .append(Component.text("????????????????????????").color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD).decorate(TextDecoration.UNDERLINED))
                .append(Component.text("??????????????????????????????????????????\n?????????????????????3??????????????????????????????????????????????????????"))
        );
    }

    private void add(MenuItem menuItem) {
        final Player player = menuItem.getClicker();
        final HashMap<Player, ArrayList<Location>> markerData = ServerStore.getMarkerData();
        final ArrayList<Location> data = markerData.get(player);
        data.add(player.getLocation());
        markerData.replace(player, data);
        ServerStore.setMarkerData(markerData);
        if (data.get(0).getWorld().getUID() == player.getWorld().getUID()) {
            if (playerParticle.containsKey(player)) {
                ParticleScheduler.removeQueue(playerParticle.get(player));
                playerParticle.remove(player);
            }
            ParticleUtil particle = new ParticleUtil();
            for (int i = 0; i < data.size(); i++) {
                final Location data1 = data.get(i);
                final Location data2;
                if (i + 1 == data.size()) {
                    data2 = data.get(0);
                } else {
                    data2 = data.get(i + 1);
                }
                particle.addLine(new ParticleUtil.Vector(data1.getX(), data1.getZ(), data2.getX(), data2.getZ(), player.getWorld(), Collections.singletonList(player)));
            }
            ParticleScheduler.addQueue(particle);
            playerParticle.put(player, particle);
            player.sendMessage(Component.text("??????????????????????????????"));
        } else {
            ChatUtil.error(player, "??????????????????????????????????????????????????????????????????????????????");
        }
    }

    private void end(MenuItem menuItem) {
        final Player player = menuItem.getClicker();
        final HashMap<Player, ArrayList<Location>> markerData = ServerStore.getMarkerData();
        player.getInventory().remove(Municipal.createItemStack());
        if (Gui.isBedrock(player)) {
            CustomForm.Builder builder = CustomForm.builder()
                    .title("??????????????????")
                    .input("?????????????????????")
                    .responseHandler((form, responseData) -> {
                        ParticleScheduler.removeQueue(playerParticle.get(player));
                        playerParticle.remove(player);
                        CustomFormResponse response = form.parseResponse(responseData);
                        if (!response.isCorrect()) {
                            ChatUtil.warning(player, "??????????????????????????????????????????");
                            markerData.remove(player);
                            ServerStore.setMarkerData(markerData);
                        } else if (Objects.requireNonNull(response.getInput(0)).length() < 64) {
                            createMunicipal(player, response.getInput(0));
                        } else {
                            ChatUtil.error(player, "?????????????????????64????????????????????????????????????");
                        }
                    });
            final FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
            fPlayer.sendForm(builder);
        } else {
            AtomicBoolean complete = new AtomicBoolean(false);
            new AnvilGUI.Builder()
                    .onClose(p -> {
                        ParticleScheduler.removeQueue(playerParticle.get(p));
                        playerParticle.remove(p);
                        if (!complete.get()) {
                            ChatUtil.warning(p, "??????????????????????????????????????????");
                            markerData.remove(p);
                            ServerStore.setMarkerData(markerData);
                        }
                    })
                    .onComplete((p, text) -> {
                        if (text.length() < 64) {
                            complete.set(true);
                            createMunicipal(p, text);
                        } else {
                            ChatUtil.error(p, "?????????????????????64????????????????????????????????????");
                        }
                        return AnvilGUI.Response.close();
                    })
                    .title("?????????????????????")
                    .text("")
                    .plugin(MetaversePlugin.getInstance())
                    .open(player);
        }
    }

    private void createMunicipal(Player p, String title) {
        final MarkerSet markerSet = getMunicipalMarker();
        final HashMap<Player, ArrayList<Location>> markerData = ServerStore.getMarkerData();
        double[] X_list = markerData.get(p).stream().map(Location::getX).mapToDouble(b -> b).toArray();
        double[] Z_list = markerData.get(p).stream().map(Location::getZ).mapToDouble(b -> b).toArray();
        int i = 0;
        List<String> names = markerSet.getAreaMarkers().stream().map(GenericMarker::getMarkerID).toList();
        while (names.contains("m" + i)) {
            i++;
        }
        markerSet.createAreaMarker("m" + i, title, false, markerData.get(p).get(0).getWorld().getName(), X_list, Z_list, true);

        // WG??????
        List<BlockVector2> points = new ArrayList<>();
        for (int j = 0; j < X_list.length; j++) {
            points.add(BlockVector2.at(X_list[j], Z_list[j]));
        }
        ProtectedRegion region = new ProtectedPolygonalRegion("region-m" + i, points, -64, 319);
        for (Municipal.Permission j : Municipal.PERMISSION_NAMES) {
            region.setFlag(j.flag(), StateFlag.State.ALLOW);
        }
        region.setFlag(Flags.BUILD, StateFlag.State.ALLOW);
        region.setFlag(Flags.INTERACT, StateFlag.State.ALLOW);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(markerData.get(p).get(0).getWorld()));
        Objects.requireNonNull(regions).addRegion(region);
        markerData.remove(p);
        ServerStore.setMarkerData(markerData);
        String discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(p.getUniqueId());
        ServerListener.getChannel().sendMessage(
                new EmbedBuilder()
                        .setTitle("`" + p.getName() + "`????????????`" + title + "`????????????????????????")
                        .setDescription("?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????")
                        .addField("?????????????????????", "MCID: `" + p.getName() + "`\nUUID: `" + p.getUniqueId() + "`\ndiscordID: " + discordId + "\ndiscordTag: " + DiscordUtil.getUserById(discordId).getAsTag(), false)
                        .setFooter("????????????????????????" + ServerStore.getServerDisplayName())
                        .setImage("https://data.arainu.world/images/checklist.png")
                        .setColor(Color.PINK)
                        .build()
        ).queue();
        sqlUtil.addMunicipal(p.getUniqueId(), "m" + i, List.of(p.getUniqueId().toString()));
        ChatUtil.success(p, "??????????????????????????????????????????");
    }
}
