package world.arainu.core.metaverseplugin.utils;

import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ParticleUtil {
    @Getter
    private final ArrayList<Vector> lines = new ArrayList<>();
    @Getter
    private final ArrayList<Vector3D> thinLines = new ArrayList<>();

    public record Vector(double x1, double y1, double x2, double y2, World world, List<Player> show_player) {
    }
    public record Vector3D(double x1, double y1, double z1, double x2, double y2, double z2, World world, List<Player> show_player) {
    }

    public void addLine(Vector vector){
        lines.add(vector);
    }

    public void addThinLine(Vector3D vector){
        thinLines.add(vector);
    }
}
