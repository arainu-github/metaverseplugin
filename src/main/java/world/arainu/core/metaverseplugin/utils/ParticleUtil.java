package world.arainu.core.metaverseplugin.utils;

import lombok.Getter;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 独自のパーティクルシステムのクラス
 *
 * @author kumitatepazuru
 */
public class ParticleUtil {
    @Getter
    private final ArrayList<Vector> lines = new ArrayList<>();
    @Getter
    private final ArrayList<Vector3D> thinLines = new ArrayList<>();

    /**
     * 線を追加する関数。
     * addThinLineとは違い、Y座標は固定で幅広に表示される。
     *
     * @param vector 線情報
     */
    public void addLine(Vector vector) {
        lines.add(vector);
    }

    /**
     * 線を追加する関数。
     *
     * @param vector 線情報
     */
    public void addThinLine(Vector3D vector) {
        thinLines.add(vector);
    }

    /**
     * 指定のブロックを囲む線を描画する関数。
     *
     * @param block       対象のブロック
     * @param show_player 表示するプレイヤー。Nullで全員に表示。
     */
    public void addBlockLine(Block block, List<Player> show_player) {
        final int X = block.getX();
        final int Y = block.getY();
        final int Z = block.getZ();
        final World world = block.getWorld();
        this.addThinLine(new ParticleUtil.Vector3D(X, Y, Z, X + 1, Y, Z, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X, Y, Z + 1, X + 1, Y, Z + 1, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X, Y, Z, X, Y, Z + 1, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z, X + 1, Y, Z + 1, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X, Y, Z, X, Y + 1, Z, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X, Y, Z + 1, X, Y + 1, Z + 1, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z, X + 1, Y + 1, Z, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X + 1, Y, Z + 1, X + 1, Y + 1, Z + 1, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X, Y + 1, Z, X + 1, Y + 1, Z, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X, Y + 1, Z + 1, X + 1, Y + 1, Z + 1, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X, Y + 1, Z, X, Y + 1, Z + 1, world, show_player));
        this.addThinLine(new ParticleUtil.Vector3D(X + 1, Y + 1, Z, X + 1, Y + 1, Z + 1, world, show_player));
    }

    /**
     * ParticleUtilで使用する専用のVector型。
     *
     * @param x1          x起点
     * @param y1          y起点
     * @param x2          x終点
     * @param y2          y終点
     * @param world       表示するワールド
     * @param show_player 表示するプレイヤー
     */
    public record Vector(double x1, double y1, double x2, double y2, World world, List<Player> show_player) {
    }

    /**
     * ParticleUtilで使用する専用のVector型。
     *
     * @param x1          x起点
     * @param y1          y起点
     * @param z1          z起点
     * @param x2          x終点
     * @param y2          y終点
     * @param z2          z終点
     * @param world       表示するワールド
     * @param show_player 表示するプレイヤー
     */
    public record Vector3D(double x1, double y1, double z1, double x2, double y2, double z2, World world,
                           List<Player> show_player) {
    }
}
