package world.arainu.core.metaverseplugin.listener;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
