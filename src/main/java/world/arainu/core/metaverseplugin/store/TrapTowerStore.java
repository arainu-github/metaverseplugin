package world.arainu.core.metaverseplugin.store;

import lombok.Getter;
import lombok.Setter;
import world.arainu.core.metaverseplugin.MetaversePlugin;

import java.util.*;

public class TrapTowerStore {
    @Getter @Setter private static List<UUID> using_player_list = new ArrayList<>(Collections.nCopies(Objects.requireNonNull(MetaversePlugin.getConfiguration().getList("traptower.pos")).size(), null));
}
