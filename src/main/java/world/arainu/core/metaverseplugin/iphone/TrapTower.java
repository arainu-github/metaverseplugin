package world.arainu.core.metaverseplugin.iphone;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.scheduler.TrapTowerScheduler;
import world.arainu.core.metaverseplugin.store.TrapTowerStore;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * bungeecordのワールド間テレポートツール。
 * Mod＆Admin専用
 *
 * @author kumitatepazuru
 */
public class TrapTower extends iPhoneBase {
    @Override
    public void executeGui(MenuItem menuItem) {
        Run(menuItem.getClicker());
    }

    /**
     * 主要関数
     *
     * @param player プレイヤー
     */
    private void Run(Player player) {
        if (MetaversePlugin.getCore() != null) {
            FileConfiguration config = MetaversePlugin.getConfiguration();
            String traptower_world_name = Objects.requireNonNull(config.getString("world.traptower"));
            if (!player.getWorld().getName().equals(traptower_world_name)) {
                Bukkit.getLogger().info(player.getName() + "がトラップタワーに行こうとしています。");
                Bukkit.getLogger().info("using_player_list: " + TrapTowerStore.getUsing_player_list());
                if (TrapTowerStore.getUsing_player_list().contains(null)) {
                    Economy econ = MetaversePlugin.getEcon();
                    if(econ.has(player, config.getInt("traptower.money"))) {
                        List<UUID> using_player_list = TrapTowerStore.getUsing_player_list();
                        int i = 0;
                        while (using_player_list.get(i) != null) {
                            i++;
                        }
                        using_player_list.set(i, player.getUniqueId());
                        TrapTowerStore.setUsing_player_list(using_player_list);
                        sqlUtil.setplayerpos(player.getUniqueId(),player.getLocation());
                        final Map<?, ?> pos = config.getMapList("traptower.pos").get(i);
                        final Map<?, ?> spawn_pos = (Map<?, ?>) pos.get("spawn");
                        final Map<?, ?> init_pos = (Map<?, ?>) pos.get("init");
                        Location init_loc = new Location(Bukkit.getWorld(traptower_world_name), (int) init_pos.get("x"), (int) init_pos.get("y"), (int) init_pos.get("z"));
                        init_loc.getBlock().setType(Material.REDSTONE_BLOCK);

                        Bukkit.getScheduler().scheduleSyncDelayedTask(MetaversePlugin.getInstance(), () -> init_loc.getBlock().setType(Material.AIR));
                        Location loc = new Location(Bukkit.getWorld(traptower_world_name), (int) spawn_pos.get("x"), (int) spawn_pos.get("y"), (int) spawn_pos.get("z"), -90, 0);
                        player.teleport(loc);
                        Component component = Component.empty();
                        component = component.append(Component.text(">> 公共施設").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
                        component = component.append(Component.text(" / "));
                        component = component.append(Component.text("トラップタワー").color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD).decorate(TextDecoration.UNDERLINED));
                        component = component.append(Component.text("\n\n概要   ━ ").color(NamedTextColor.RED));
                        component = component.append(Component.text("最新式のトラップタワーを使用できます。"));
                        component = component.append(Component.text("\nヒント ━ ").color(NamedTextColor.RED));
                        component = component.append(Component.text("後ろの待機場行きエレベーターから待機場に行ったほうがスポーン率が高くなります"));
                        component = component.append(Component.text("\n注意   ━ ").color(NamedTextColor.RED));
                        component = component.append(Component.text("トラップタワーからサバイバルサーバーに戻るとチェスト内のアイテムはすべて削除されます。"));
                        component = component.append(Component.text("また、一旦サーバーを抜けると強制的にサバイバルサーバーに転送されます。"));
                        component = component.append(Component.text("\n\n詳しくはこちら: ").color(NamedTextColor.WHITE));
                        component = component.append(Component.text("https://www.arainu.world/minecraft/survival/public/traptower").decorate(TextDecoration.UNDERLINED).decorate(TextDecoration.BOLD).color(NamedTextColor.GREEN).clickEvent(ClickEvent.openUrl("https://www.arainu.world/minecraft/survival/public/traptower")));
                        player.sendMessage(component);
                        new TrapTowerScheduler(player).runTaskTimer(MetaversePlugin.getInstance(), 0, 1200);
                    } else {
                        Gui.error(player, "銀行残高が少なすぎるためトラップタワーにいけません！\n必要料金: 200円/分");
                    }
                } else {
                    Gui.error(player, "トラップタワーがすでに使用中です。時間をおいてもう一度お試しください。");
                }
            } else {
                Gui.error(player, "公共施設から直接公共施設へ行くことはできません！");
            }
        } else {
            Bukkit.getLogger().severe("multiverseを導入してください！");
        }
    }
}
