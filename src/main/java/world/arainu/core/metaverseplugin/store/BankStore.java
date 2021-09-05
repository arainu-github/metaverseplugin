package world.arainu.core.metaverseplugin.store;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import world.arainu.core.metaverseplugin.MetaversePlugin;

import java.util.HashMap;
import java.util.UUID;

/**
 * 銀行システムに関する情報をまとめているクラス
 * @author kumitatepazuru
 */
public class BankStore {
    /**
     * 銀行システムに関する情報をまとめているクラス
     */
    public BankStore() {
        key = new NamespacedKey(MetaversePlugin.getInstance(), "metaverse-bank__money");
        Instance = this;
    }

    @Getter private static NamespacedKey key;
    @Getter private static BankStore Instance;
    @Getter @Setter private static HashMap<UUID, Integer> gui_hashmap = new HashMap<>();
}
