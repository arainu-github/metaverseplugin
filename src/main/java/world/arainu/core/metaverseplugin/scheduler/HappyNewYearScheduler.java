package world.arainu.core.metaverseplugin.scheduler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import world.arainu.core.metaverseplugin.MetaversePlugin;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Objects;

public class HappyNewYearScheduler extends BukkitRunnable {
    long oldTime = 0;
    HashMap<Player, Location> playerpos = new HashMap<>();

    @Override
    public void run() {
        World world = Bukkit.getWorld("world");
        Bukkit.getOnlinePlayers().forEach(e -> {
            long newYear = ChronoUnit.SECONDS.between(LocalDateTime.now(), LocalDateTime.of(2021, 12, 31, 15, 0, 0));
            if (newYear <= 10) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "weather clear");
                e.showTitle(Title.title(Component.text("━━━ " + newYear + " ━━━").decorate(TextDecoration.BOLD).color(NamedTextColor.RED), Component.empty(), Title.Times.of(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)));
                if (oldTime != newYear) {
                    oldTime = newYear;
                    e.playSound(e.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.MASTER, 1, 1f);
                    if (newYear == 7) {
                        Entity entity = Objects.requireNonNull(world).spawnEntity(new Location(world, 73.5, 67, 62.5), EntityType.ARMOR_STAND);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " + entity.getUniqueId() + " run function fireworker:set");
                        entity = Objects.requireNonNull(world).spawnEntity(new Location(world, 49.5, 67, 61.5), EntityType.ARMOR_STAND);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " + entity.getUniqueId() + " run function fireworker:set");
                        entity = Objects.requireNonNull(world).spawnEntity(new Location(world, 104.5, 67, 64.5), EntityType.ARMOR_STAND);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " + entity.getUniqueId() + " run function fireworker:set");
                    } else if (newYear == 0) {
                        e.setGameMode(GameMode.SURVIVAL);
                        e.teleport(playerpos.get(e));
                    }
                }
                if (newYear == 0) {
                    e.showTitle(Title.title(Component.text("Happy New Year!!!").decorate(TextDecoration.BOLD).color(NamedTextColor.GREEN), Component.text("あけましておめでとうございます！").color(NamedTextColor.RED), Title.Times.of(Duration.ZERO, Duration.ofSeconds(5), Duration.ofSeconds(3))));
                    Bukkit.getScheduler().runTaskLater(MetaversePlugin.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[tag=fireworker]"), 20 * 60);
                    cancel();
                } else if (newYear == 5) {
                    playerpos.put(e, e.getLocation());
                } else if (newYear == 4) {
                    e.setGameMode(GameMode.SPECTATOR);
                    e.teleport(new Location(world, 108, 71, 62, 62, 30));
                } else if (newYear == 3) {
                    e.teleport(new Location(world, 73, 71, 54, 5, 30));
                } else if (newYear == 2) {
                    e.teleport(new Location(world, 42, 72, 59, -73, 21));
                } else if (newYear == 1) {
                    e.teleport(new Location(world, 74, 81, 35, 0, 0));
                }
            } else if (newYear <= 60) {
                e.showTitle(Title.title(Component.text(newYear).decorate(TextDecoration.BOLD).color(NamedTextColor.GOLD), Component.empty(), Title.Times.of(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)));
            } else {
                e.showTitle(Title.title(Component.empty(), Component.empty().append(Component.text("のこり").color(NamedTextColor.GREEN)).append(Component.text(newYear).decorate(TextDecoration.BOLD)).append(Component.text("秒").color(NamedTextColor.GOLD)), Title.Times.of(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)));
            }
        });
    }
}
