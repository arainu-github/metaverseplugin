package world.arainu.core.metaverseplugin.utils;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import world.arainu.core.metaverseplugin.MetaversePlugin;

import java.util.UUID;

/**
 * 銀行の入金情報のデータを格納するクラス
 * TODO: mysql化
 */
public record BankNotice(@Getter OfflinePlayer player, @Getter int money) {

    /**
     * プレイヤーのUUIDを取得する関数
     *
     * @return UUID
     */
    public UUID getPlayerUID() {
        return player.getUniqueId();
    }

    /**
     * jecon等で設定されている整形された料金表示を返す関数
     *
     * @return 料金(str)
     */
    public String getFormatedMoney() {
        Economy econ = MetaversePlugin.getEcon();
        return econ.format(money);
    }
}
