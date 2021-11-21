package world.arainu.core.metaverseplugin.utils;

import org.bukkit.entity.Player;

/**
 * アイテムに関する関数(群)
 *
 * @author AreaEffectCloud
 */

public class ItemUtil {

    /**
     * プレイヤーのインベントリが満杯であることを戻り値で返す
     *
     * @param player インベントリが満杯かどうかをチェックするプレイヤー
     * @return 満杯であった場合、戻り値が返される(満杯でなかった場合はスルーされる)
     */
    private boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }
}
