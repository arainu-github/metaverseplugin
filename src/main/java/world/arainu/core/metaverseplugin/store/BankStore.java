package world.arainu.core.metaverseplugin.store;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.utils.BankNotice;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * 銀行システムに関する情報をまとめているクラス
 * @author kumitatepazuru
 */
public class BankStore {
    @Getter private static final NamespacedKey key = new NamespacedKey(MetaversePlugin.getInstance(), "metaverse-bank__money");
    @Getter @Setter private static HashMap<UUID, Integer> gui_hashmap = new HashMap<>();
    // TODO: Mysqlに移行
    @Getter @Setter private static HashMap<UUID, List<BankNotice>> remittance_map = new HashMap<>();
    @Getter @Setter private static HashMap<UUID, Long> login_money_map = new HashMap<>();
    @Getter @Setter private static HashMap<String, Integer> money_late_two_ago = new HashMap<>();
    @Getter @Setter private static HashMap<String, Integer> money_late_yesterday = new HashMap<>();
    @Getter @Setter private static HashMap<String, Integer> money_late = new HashMap<>();
}
