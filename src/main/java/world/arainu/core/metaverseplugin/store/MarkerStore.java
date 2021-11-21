package world.arainu.core.metaverseplugin.store;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class MarkerStore {
    @Getter
    @Setter
    private static HashMap<Player, ArrayList<Location>> MarkerData = new HashMap<>();
}
