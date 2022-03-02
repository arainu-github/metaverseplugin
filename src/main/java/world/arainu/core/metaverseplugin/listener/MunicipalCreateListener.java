package world.arainu.core.metaverseplugin.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.WorldGuard;
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
import java.util.stream.Collectors;

/**
 * 自治体作成に使用されるリスナー。
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
            markerSet = marker.createMarkerSet("municipal", "自治体", null, true);
        }
        return markerSet;
    }

    /**
     * 自治体作成ブックを使用したときに動く関数。
     *
     * @param e イベント
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
                            new MenuItem("自治体の始点を作成", this::create, true, Material.COBBLED_DEEPSLATE)
                    );
                } else if (ServerStore.getMarkerData().get(e.getPlayer()).size() < 3) {
                    menuItem = List.of(
                            new MenuItem("自治体の頂点を作成", this::add, true, Material.CALCITE),
                            new MenuItem("最初からやり直す", this::begin, true, Material.REDSTONE_BLOCK)
                    );
                } else {
                    menuItem = List.of(
                            new MenuItem("自治体の頂点を作成", this::add, true, Material.CALCITE),
                            new MenuItem("最初からやり直す", this::begin, true, Material.REDSTONE_BLOCK),
                            new MenuItem("自治体を作成", this::end, true, Material.TUFF)
                    );
                }
                Gui.getInstance().openMenu(e.getPlayer(), "自治体作成メニュー", menuItem);
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
        ChatUtil.success(p, "自治体の頂点・始点をすべて削除しました。");
    }

    private void create(MenuItem menuItem) {
        final Player player = menuItem.getClicker();
        final HashMap<Player, ArrayList<Location>> markerData = ServerStore.getMarkerData();
        markerData.put(player, new ArrayList<>() {{
            add(player.getLocation());
        }});
        ServerStore.setMarkerData(markerData);
        player.sendMessage(Component.text("次に区域の頂点を決めましょう。Java版の方は、パーティクルで囲まれている場所が自治体区域になります。\n")
                .append(Component.text("自治体作成ブック").color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD).decorate(TextDecoration.UNDERLINED))
                .append(Component.text("から追加することができます。\n頂点が始点含む3つ以上になると自治体が作成できます。"))
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
            player.sendMessage(Component.text("頂点を追加しました。"));
        } else {
            ChatUtil.error(player, "自治体の始点と同じワールド内で頂点を決めてください。");
        }
    }

    private void end(MenuItem menuItem) {
        final Player player = menuItem.getClicker();
        final HashMap<Player, ArrayList<Location>> markerData = ServerStore.getMarkerData();
        player.getInventory().remove(Municipal.createItemStack());
        if (Gui.isBedrock(player)) {
            CustomForm.Builder builder = CustomForm.builder()
                    .title("自治体を作成")
                    .input("自治体名を入力")
                    .responseHandler((form, responseData) -> {
                        ParticleScheduler.removeQueue(playerParticle.get(player));
                        playerParticle.remove(player);
                        CustomFormResponse response = form.parseResponse(responseData);
                        if (!response.isCorrect()) {
                            ChatUtil.warning(player, "自治体の作成を中断しました。");
                            markerData.remove(player);
                            ServerStore.setMarkerData(markerData);
                        } else if (Objects.requireNonNull(response.getInput(0)).length() < 64) {
                            createMunicipal(player, response.getInput(0));
                        } else {
                            ChatUtil.error(player, "自治体の名前は64文字以内にしてください！");
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
                            ChatUtil.warning(p, "自治体の作成を中断しました。");
                            markerData.remove(p);
                            ServerStore.setMarkerData(markerData);
                        }
                    })
                    .onComplete((p, text) -> {
                        if (text.length() < 64) {
                            complete.set(true);
                            createMunicipal(p, text);
                        } else {
                            ChatUtil.error(p, "自治体の名前は64文字以内にしてください！");
                        }
                        return AnvilGUI.Response.close();
                    })
                    .title("自治体名を入力")
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
        List<String> names = markerSet.getAreaMarkers().stream().map(GenericMarker::getMarkerID).collect(Collectors.toList());
        while (names.contains("m" + i)) {
            i++;
        }
        markerSet.createAreaMarker("m" + i, title, false, markerData.get(p).get(0).getWorld().getName(), X_list, Z_list, true);

        // WG設定
        List<BlockVector2> points = new ArrayList<>();
        for (int j = 0; j < X_list.length; j++) {
            points.add(BlockVector2.at(X_list[j], Z_list[j]));
        }
        ProtectedRegion region = new ProtectedPolygonalRegion("region-m" + i, points, -64, 319);
        for (Municipal.Permission j : Municipal.PERMISSION_NAMES) {
            region.setFlag(j.flag(), StateFlag.State.ALLOW);
        }
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(markerData.get(p).get(0).getWorld()));
        Objects.requireNonNull(regions).addRegion(region);
        markerData.remove(p);
        ServerStore.setMarkerData(markerData);
        String discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(p.getUniqueId());
        ServerListener.getChannel().sendMessage(
                new EmbedBuilder()
                        .setTitle("`" + p.getName() + "`が自治体`" + title + "`を作成しました。")
                        .setDescription("運営は自治体の場所等を確認し、自治体チェックリストに反していないかを確認してください。")
                        .addField("プレイヤー情報", "MCID: `" + p.getName() + "`\nUUID: `" + p.getUniqueId() + "`\ndiscordID: " + discordId + "\ndiscordTag: " + DiscordUtil.getUserById(discordId).getAsTag(), false)
                        .setFooter("接続元サーバー：" + ServerStore.getServerDisplayName())
                        .setImage("https://data.arainu.world/images/checklist.png")
                        .setColor(Color.PINK)
                        .build()
        ).queue();
        sqlUtil.addMunicipal(p.getUniqueId(), "m" + i, List.of());
        ChatUtil.success(p, "自治体を正常に作成しました。");
    }
}
