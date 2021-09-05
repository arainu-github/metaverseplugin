package world.arainu.core.metaverseplugin.store;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import world.arainu.core.metaverseplugin.MetaversePlugin;

import java.util.HashMap;
import java.util.UUID;

public class BankStore {
    public BankStore() {
        key = new NamespacedKey(MetaversePlugin.getInstance(), "metaverse-bank__money");
        Instance = this;
    }

    @Getter static NamespacedKey key;
    @Getter static BankStore Instance;
    @Getter @Setter private static HashMap<UUID, Integer> gui_hashmap = new HashMap<>();
}
