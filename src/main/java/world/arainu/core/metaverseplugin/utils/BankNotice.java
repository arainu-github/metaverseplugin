package world.arainu.core.metaverseplugin.utils;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import world.arainu.core.metaverseplugin.MetaversePlugin;

import java.util.UUID;

public class BankNotice {
    @Getter OfflinePlayer player;
    @Getter int money;

    public BankNotice(OfflinePlayer player, int money){
        this.player = player;
        this.money = money;
    }

    public UUID getPlayerUID() {
        return player.getUniqueId();
    }

    public String getFormatedMoney(){
        Economy econ = MetaversePlugin.getEcon();
        return econ.format(money);
    }
}
