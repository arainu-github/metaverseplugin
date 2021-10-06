package world.arainu.core.metaverseplugin.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import world.arainu.core.metaverseplugin.MetaversePlugin;

import java.awt.Color;
import java.util.Objects;

/**
 * サーバーの動作に関するイベントリスナーのクラス
 *
 * @author kumitatepazuru
 */
public class ServerListener implements Listener {
    /**
     * プレイヤーがログインしたときにそのプレイヤーを使用してBungeeCordの名前を取得する
     *
     * @param e イベント
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");
        player.sendPluginMessage(MetaversePlugin.getInstance(), "BungeeCord", out.toByteArray());
        Bukkit.getLogger().info("Bungeecordとの通信中");
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        if(Bukkit.hasWhitelist()){
            if(!e.getPlayer().isWhitelisted()){
                JDA jda = DiscordUtil.getJda();
                Objects.requireNonNull(jda.getTextChannelById(MetaversePlugin.getConfiguration().getLong("discord.warn_channel"))).sendMessage(
                    new EmbedBuilder()
                            .setTitle("ホワイトリストに登録されていないプレイヤーがログインしようとしました")
                            .addField("プレイヤー名",e.getPlayer().getName(),false)
                            .addField("UUID", String.valueOf(e.getPlayer().getUniqueId()),false)
                            .setThumbnail("https://crafatar.com/avatars/"+e.getPlayer().getUniqueId())
                            .setColor(Color.ORANGE)
                            .build()
                ).queue();
            }
        }
    }
}
