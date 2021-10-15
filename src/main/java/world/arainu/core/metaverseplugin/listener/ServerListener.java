package world.arainu.core.metaverseplugin.listener;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.TimeManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.store.ServerStore;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.awt.Color;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * サーバーの動作に関するイベントリスナーのクラス
 *
 * @author kumitatepazuru
 */
public class ServerListener implements Listener {
    private final JDA jda = DiscordUtil.getJda();
    private final TextChannel channel = Objects
            .requireNonNull(jda.getTextChannelById(MetaversePlugin.getConfiguration().getLong("discord.warn_channel")));

    /**
     * ホワリス以外の人がログインしてきたときにdiscordにログを流す
     * 
     * @param e イベント
     */
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        String name = e.getPlayer().getName();
        if (e.getResult() == PlayerLoginEvent.Result.KICK_WHITELIST) {
            sqlUtil.addkickcount(uuid);
            final int kickcount = Objects.requireNonNull(sqlUtil.getkickcount(uuid));
            MessageEmbed embed = new EmbedBuilder().setTitle("ホワイトリストに登録されていないプレイヤーがログインしようとしました")
                    .setDescription("自動BANまで残り" + (10 * (kickcount / 10 + 1) - kickcount) + "回")
                    .addField("プレイヤー名", "`" + name + "`", false).addField("UUID", String.valueOf(uuid), false)
                    .setThumbnail("https://crafatar.com/avatars/" + uuid + ".png")
                    .setFooter("接続元サーバー：" + ServerStore.getServerDisplayName()).setColor(Color.ORANGE).build();
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
     * AdvancedBANでのBANはこのイベント以降は動かないみたいなのでここで定義
     * 
     * @param e イベント
     */
    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        UUID uuid = e.getPlayerProfile().getId();
        String name = e.getPlayerProfile().getName();
        if (PunishmentManager.get().isBanned(name)) {
            MessageEmbed embed = new EmbedBuilder().setTitle("すでにBANされているプレイヤーがログインしようとしました")
                    .addField("プレイヤー名", "`" + name + "`", false).addField("UUID", String.valueOf(uuid), false)
                    .setThumbnail("https://crafatar.com/avatars/" + uuid + ".png")
                    .setFooter("接続元サーバー：" + ServerStore.getServerDisplayName()).setColor(Color.YELLOW).build();
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
    }

    private final HashMap<UUID, TimeAndMsg> last_log_map = new HashMap<>();

    class TimeAndMsg {
        TimeAndMsg(long time, Message msg) {
            this.time = time;
            this.msg = msg;
        }

        private final long time;
        private final Message msg;
    }
}
