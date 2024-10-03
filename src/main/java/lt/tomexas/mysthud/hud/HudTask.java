package lt.tomexas.mysthud.hud;

import lt.tomexas.mysthud.Main;
import lt.tomexas.mysthud.utils.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class HudTask extends BukkitRunnable {

    private final HudManager manager = Main.get().getHudManager();

    private List<? extends Player> hudEnabledPlayers() {
        return Bukkit.getOnlinePlayers().stream().filter(manager::getHudState).toList();
    }

    @Override
    public void run() {
        for (Player player : hudEnabledPlayers()) {
            Hud hud = manager.hasActiveHud(player) ? manager.getActiveHud(player) : manager.getDefaultEnabledHuds().get(player);

            if (hud == null || manager.getHudID(hud) == null) continue;
            if (hud.disableWhilstInWater() && EntityUtils.isUnderWater(player)) continue;
            if (!player.hasPermission(hud.getPerm())) return;

            manager.updateHud(player, hud);
        }
    }
}
