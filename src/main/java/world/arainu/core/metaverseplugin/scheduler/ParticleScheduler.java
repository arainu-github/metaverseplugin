package world.arainu.core.metaverseplugin.scheduler;

import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import world.arainu.core.metaverseplugin.utils.ParticleUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 独自の線パーティクルなどを表示するスケジューラー。
 *
 * @author kumitatepazuru
 */
public class ParticleScheduler extends BukkitRunnable {
    private static final List<ParticleUtil> queue = new ArrayList<>();

    /**
     * パーティクルキューを追加する関数。
     *
     * @param particle 追加するパーティクル
     */
    public static void addQueue(ParticleUtil particle) {
        queue.add(particle);
    }

    /**
     * パーティクルキューを削除する関数。
     *
     * @param particle 削除するパーティクル
     */
    public static void removeQueue(ParticleUtil particle) {
        queue.remove(particle);
    }

    @Override
    public void run() {

        for (ParticleUtil k : queue) {
            for (ParticleUtil.Vector i : k.getLines()) {
                final double X = i.x2() - i.x1();
                final double Y = i.y2() - i.y1();
                final double S = Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2));
                for (double j = 0; j < S; j += 1) {
                    i.world().spawnParticle(Particle.END_ROD, i.show_player(), null, i.x1() + j * (X / S), 64, i.y1() + j * (Y / S), 10, 0.1, 5, 0.1, 0, null, true);
                }
            }

            for (ParticleUtil.Vector3D i : k.getThinLines()) {
                final double X = i.x2() - i.x1();
                final double Z = i.y2() - i.y1();
                final double Y = i.z2() - i.z1();
                final double r = Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2) + Math.pow(Z, 2));
                final double t = Math.acos(Z / r);
                double f = Math.signum(Y) * Math.acos(X / Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2)));
                if (Double.isNaN(f)) {
                    f = 0;
                }
                for (double j = 0; j < r + 0.5; j += 0.5) {
                    final double x = i.x1() + j * Math.sin(t) * Math.cos(f);
                    final double z = i.z1() + j * Math.sin(t) * Math.sin(f);
                    final double y = i.y1() + j * Math.cos(t);
                    i.world().spawnParticle(Particle.END_ROD, i.show_player(), null, x, y, z, 1, 0, 0, 0, 0, null, true);
                }
            }
        }
    }
}
