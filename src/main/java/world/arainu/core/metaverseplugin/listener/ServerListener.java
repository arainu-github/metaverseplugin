package world.arainu.core.metaverseplugin.listener;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import lombok.Getter;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.TimeManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.store.ServerStore;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * サーバーの動作に関するイベントリスナーのクラス
 *
 * @author kumitatepazuru
 */
public class ServerListener implements Listener {
    @Getter
    static private final JDA jda = DiscordUtil.getJda();
    @Getter
    static private final TextChannel channel = Objects
            .requireNonNull(jda.getTextChannelById(MetaversePlugin.getConfiguration().getLong("discord.warn_channel")));
    private final HashMap<UUID, TimeAndMsg> last_log_map = new HashMap<>();

    private void sendMessage(UUID uuid, MessageEmbed embed) {
        if (last_log_map.containsKey(uuid)) {
            if (System.currentTimeMillis() - last_log_map.get(uuid).time < 30 * 60 * 1000) {
                last_log_map.get(uuid).msg.editMessage(embed)
                        .queue(msg -> last_log_map.put(uuid, new TimeAndMsg(System.currentTimeMillis(), msg)));
            } else {
                channel.sendMessage(embed)
                        .queue(msg -> last_log_map.put(uuid, new TimeAndMsg(System.currentTimeMillis(), msg)));
            }
        } else {
            channel.sendMessage(embed)
                    .queue(msg -> last_log_map.put(uuid, new TimeAndMsg(System.currentTimeMillis(), msg)));
        }
    }

    /**
     * ホワリス以外の人がログインしてきたときにdiscordにログを流す
     * AdvancedBANでのBANはこのイベント以降は動かないみたいなのでここで定義
     * このプラグインのホワイトリスト機能もここでKICKするためここで定義する
     * ホワリスに設定されてない人がアクセスしてきたときにkickする機能もここで定義
     *
     * @param e イベント
     */
    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        UUID uuid = e.getPlayerProfile().getId();
        String name = e.getPlayerProfile().getName();

        if (Bukkit.hasWhitelist()) {
            if (!PunishmentManager.get().isBanned(name)) {
                if (!Objects.requireNonNull(sqlUtil.getWhitelist()).contains(uuid)) {
                    e.kickMessage(Component.text("貴方はホワイトリストに入っていません！\nメンテナンス中またはサーバーが公開されていない可能性があります。\nお知らせをご確認ください。").color(NamedTextColor.RED));
                    e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
                }
            }
        }

        if (PunishmentManager.get().isBanned(name)) {
            MessageEmbed embed = new EmbedBuilder().setTitle("すでにBANされているプレイヤーがログインしようとしました")
                    .addField("プレイヤー名", "`" + name + "`", false).addField("UUID", String.valueOf(uuid), false)
                    .setThumbnail("https://crafatar.com/avatars/" + uuid + ".png")
                    .setFooter("接続元サーバー：" + ServerStore.getServerDisplayName()).setColor(Color.YELLOW).build();
            sendMessage(uuid, embed);
        } else if (e.getLoginResult() == AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST) {
            sqlUtil.addkickcount(uuid);
            final int kickcount = Objects.requireNonNull(sqlUtil.getkickcount(uuid));
            MessageEmbed embed = new EmbedBuilder().setTitle("ホワイトリストに登録されていないプレイヤーがログインしようとしました")
                    .setDescription("自動BANまで残り" + (10 * (kickcount / 10 + 1) - kickcount) + "回")
                    .addField("プレイヤー名", "`" + name + "`", false).addField("UUID", String.valueOf(uuid), false)
                    .setThumbnail("https://crafatar.com/avatars/" + uuid + ".png")
                    .setFooter("接続元サーバー：" + ServerStore.getServerDisplayName()).setColor(Color.ORANGE).build();
            sendMessage(uuid, embed);

            if (kickcount % 10 == 0) {
                Punishment punishment = new Punishment(name, name, "@attack", "自動BAN", PunishmentType.TEMP_BAN,
                        TimeManager.getTime(), TimeManager.getTime() + (long) kickcount * 60 * 1000, null, -1);
                punishment.create();
                channel.sendMessage(new EmbedBuilder().setTitle("アクセス試行回数がしきい値を超えたので一時的にBANしました")
                        .setDescription("BAN時間:" + kickcount + "分")
                        .setFooter("接続元サーバー：" + ServerStore.getServerDisplayName())
                        .setAuthor(name, null, "https://crafatar.com/avatars/" + uuid + ".png").setColor(Color.RED)
                        .build()).queue();
            }
        }
    }

    /**
     * プレイヤーがログアウトしたときに内部プレイヤーデータを削除する関数。
     *
     * @param e イベント
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        // GC
        if (ServerStore.getMarkerData().containsKey(e.getPlayer())) {
            HashMap<Player, ArrayList<Location>> markerData = ServerStore.getMarkerData();
            markerData.remove(e.getPlayer());
            ServerStore.setMarkerData(markerData);
        }
    }

    /**
     * プレイヤーがログインしたときに新機能を通知する関数。
     *
     * @param e イベント
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final String url = "https://data.arainu.world/advancements/?uuid=" + e.getPlayer().getUniqueId();
        e.getPlayer().sendMessage(Component.text("あなたの進捗の達成度を確認できるようになりました！\n").append(Component.text(url).clickEvent(ClickEvent.openUrl(url)).decorate(TextDecoration.UNDERLINED)));
    }

    /**
     * 時間とメッセージのクラス。そのままでわかりやすいね★
     */
    record TimeAndMsg(long time, Message msg) {
    }
}
