package world.arainu.core.metaverseplugin.scheduler;

import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import world.arainu.core.metaverseplugin.utils.ParticleUtil;

import java.util.ArrayList;
import java.util.List;

public class ParticleScheduler extends BukkitRunnable {
    private static final List<ParticleUtil> queue = new ArrayList<>();

    @Override
    public void run() {
        for (ParticleUtil k: queue){
            for (ParticleUtil.Vector i: k.getLines()) {
                final double X = i.x2() - i.x1();
                final double Y = i.y2() - i.y1();
                final double S = Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2));
                for (double j = 0; j < S; j += 1) {
                    i.world().spawnParticle(Particle.END_ROD, i.show_player(), null, i.x1() + j * (X/S), 64, i.y1() + j * (Y/S), 10, 0.1, 5, 0.1, 0, null, true);
                }
            }
        }
    }

    public static void addQueue(ParticleUtil particle){
        queue.add(particle);
    }

    public static void removeQueue(ParticleUtil particle){
        queue.remove(particle);
    }
}
