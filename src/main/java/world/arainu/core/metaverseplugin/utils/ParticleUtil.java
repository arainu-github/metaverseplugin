package world.arainu.core.metaverseplugin.utils;

import lombok.Getter;
import org.bukkit.World;
import org.bukkit.block.Block;
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

    public void addBlockLine(Block block, List<Player> show_player){
        final int X = block.getX();
        final int Y = block.getY();
        final int Z = block.getZ();
        final World world = block.getWorld();
        this.addThinLine(new ParticleUtil.Vector3D(X, Y, Z, X + 1, Y, Z, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X, Y, Z + 1, X + 1, Y, Z + 1, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X, Y, Z, X, Y, Z + 1, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z, X + 1, Y, Z + 1, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X, Y, Z, X, Y +1, Z, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X, Y, Z + 1, X, Y +1, Z + 1, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z, X + 1, Y +1, Z, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z + 1, X + 1, Y +1, Z + 1, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X, Y +1, Z, X + 1, Y +1, Z, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X, Y +1, Z + 1, X + 1, Y +1, Z + 1, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X, Y +1, Z, X, Y +1, Z + 1, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X + 1, Y +1, Z, X + 1, Y +1, Z + 1, world, show_player));
    }
}
