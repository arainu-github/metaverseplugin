package world.arainu.core.metaverseplugin.scheduler;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.listener.ServerListener;
import world.arainu.core.metaverseplugin.store.ServerStore;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class DiscordScheduler extends BukkitRunnable {
    @Override
    public void run() {
        if(Objects.equals(ServerStore.getServerName(), "lobby")) {
            Economy econ = MetaversePlugin.getEcon();
            List<@NotNull OfflinePlayer> offlinePlayers = Arrays.stream(Bukkit.getOfflinePlayers()).toList();
            IntStream.range(0, offlinePlayers.size())
                    .boxed().max(Comparator.comparing(e -> econ.getBalance(offlinePlayers.get(e)))).ifPresent(
                            e -> {
                                String discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(offlinePlayers.get(e).getUniqueId());
                                if (discordId != null) {
                                    final String id = MetaversePlugin.getConfiguration().getString("discord.okanemochi_role");
                                    final Role role = Objects.requireNonNull(ServerListener.getJda().getRoleById(Objects.requireNonNull(id)));
                                    final List<Member> roles = ServerListener.getChannel().getGuild().getMembersWithRoles(role);
                                    final String oldUser;
                                    if (roles.size() != 0) {
                                        oldUser = roles.get(0).getId();
                                    } else {
                                        oldUser = "0";
                                    }
                                    if (!oldUser.equals(discordId)) {
                                        if (roles.contains(ServerListener.getChannel().getGuild().getMemberById(oldUser))) {
                                            ServerListener.getChannel().getGuild().removeRoleFromMember(oldUser, role).queue();
                                        }
                                        ServerListener.getChannel().getGuild().addRoleToMember(discordId, role).queue();
                                    }
                                }
                            }
                    );
        }
    }
}
