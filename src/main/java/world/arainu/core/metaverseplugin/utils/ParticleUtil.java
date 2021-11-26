package world.arainu.core.metaverseplugin.utils;

import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ParticleUtil {
    @Getter
    private final ArrayList<Vector> lines = new ArrayList<>();

    public record Vector(double x1, double y1, double x2, double y2, World world, List<Player> show_player) {
    }

    public void addLine(Vector vector){
        lines.add(vector);
    }
}
