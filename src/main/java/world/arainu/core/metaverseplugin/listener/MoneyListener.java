package world.arainu.core.metaverseplugin.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import world.arainu.core.metaverseplugin.MetaversePlugin;

import java.util.List;
import java.util.Objects;


public class MoneyListener implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        if(e.getEntity().getKiller() != null) {
            if(!e.getEntity().getKiller().getWorld().getName().equals(MetaversePlugin.getInstance().getConfig().getString("world.traptower"))) {
                List.of(
                        new money(EntityType.IRON_GOLEM, 100),
                        new money(EntityType.POLAR_BEAR, 150),
                        new money(EntityType.BEE, 100),
                        new money(EntityType.ENDERMAN, 100),
                        new money(EntityType.SPIDER, 50),
                        new money(EntityType.ZOMBIFIED_PIGLIN, 100),
                        new money(EntityType.CAVE_SPIDER, 50),
                        new money(EntityType.PIGLIN, 100),
                        new money(EntityType.WITHER_SKELETON, 100),
                        new money(EntityType.WITCH, 100),
                        new money(EntityType.VINDICATOR, 200),
                        new money(EntityType.VEX, 150),
                        new money(EntityType.EVOKER, 200),
                        new money(EntityType.ELDER_GUARDIAN, 500),
                        new money(EntityType.ENDERMITE, 150),
                        new money(EntityType.GUARDIAN, 100),
                        new money(EntityType.GHAST, 50),
                        new money(EntityType.CREEPER, 25),
                        new money(EntityType.SHULKER, 75),
                        new money(EntityType.SILVERFISH, 25),
                        new money(EntityType.SKELETON, 25),
                        new money(EntityType.STRAY, 25),
                        new money(EntityType.SLIME, 25),
                        new money(EntityType.ZOGLIN, 75),
                        new money(EntityType.ZOMBIE, 10),
                        new money(EntityType.DROWNED, 25),
                        new money(EntityType.HUSK, 25),
                        new money(EntityType.PIGLIN_BRUTE, 300),
                        new money(EntityType.PILLAGER, 150),
                        new money(EntityType.PHANTOM, 50),
                        new money(EntityType.BLAZE, 50),
                        new money(EntityType.HOGLIN, 75),
                        new money(EntityType.MAGMA_CUBE, 50),
                        new money(EntityType.ZOMBIE_VILLAGER, 10),
                        new money(EntityType.WITHER, 1500),
                        new money(EntityType.ENDER_DRAGON, 1500)
                ).forEach(n -> {
                    if (e.getEntity().getType() == n.type()) addMoney(e.getEntity().getKiller(), n.money());
                });
            }
        }
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent e){
        Advancement advancement = e.getAdvancement();
        if(advancement.getDisplay() != null) {
            switch (Objects.requireNonNull(e.getAdvancement().getDisplay()).frame()){
                case TASK -> addMoney(e.getPlayer(),500);
                case GOAL -> addMoney(e.getPlayer(),1500);
                case CHALLENGE -> addMoney(e.getPlayer(),5000);
            }
        }
    }

    record money(EntityType type,int money){
    }

    private void addMoney(Player player, int money){
        Economy econ = MetaversePlugin.getEcon();
        player.sendActionBar(Component.text("+"+econ.format(money)).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD).decorate(TextDecoration.UNDERLINED));
        econ.depositPlayer(player,money);
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 1, 1f);
    }
}
