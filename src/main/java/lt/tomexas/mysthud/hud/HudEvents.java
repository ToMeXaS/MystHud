package lt.tomexas.mysthud.hud;

import lt.tomexas.mysthud.Main;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class HudEvents implements Listener {

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        HudManager hudManager = Main.get().getHudManager();
        final Player player = event.getPlayer();
        final PersistentDataContainer pdc = player.getPersistentDataContainer();
        hudManager.loadHud(player);
        Hud hud = hudManager.hasActiveHud(player)
                ? hudManager.getActiveHud(player) : hudManager.getDefaultEnabledHuds().get(player);
        String hudId = hudManager.getHudID(hud);

        if (hud == null || hudId == null) return;
        if (!player.hasPermission(hud.getPerm())) return;
        if (!hudManager.getHudState(player)) return;

        pdc.set(hudManager.hudDisplayKey, PersistentDataType.STRING, hudManager.getHudID(hud));
        pdc.set(hudManager.hudToggleKey, PersistentDataType.BOOLEAN, true);
        Main.get().getDatabase().addPlayer(player);
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        HudManager hudManager = Main.get().getHudManager();
        final Player player = event.getPlayer();

        hudManager.getPlayerHuds().remove(player);
    }

    @EventHandler
    public void onWaterEnter(final EntityAirChangeEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;

        HudManager hudManager = Main.get().getHudManager();
        Player player = (Player) event.getEntity();
        Hud hud = hudManager.getActiveHud(player);

        if (hud == null || !hud.disableWhilstInWater() || !hudManager.getHudState(player)) return;
        if (event.getAmount() < player.getMaximumAir()) {
            hudManager.setHudState(player, true);
            hudManager.enableHud(player, hud);
        } else {
            hudManager.setHudState(player, false);
            hudManager.disableHud(player, hud);
        }
    }

    @EventHandler
    public void onGameModeChange(final PlayerGameModeChangeEvent event) {
        HudManager hudManager = Main.get().getHudManager();
        Player player = event.getPlayer();
        Hud hud = hudManager.getActiveHud(player);
        if (hud == null) return;

        if (player.getGameMode() == GameMode.SPECTATOR && !hud.enableInSpectatorMode()) {
            hudManager.setHudState(player, false);
            hudManager.disableHud(player, hud);
        } else {
            hudManager.setHudState(player, true);
            hudManager.enableHud(player, hud);
        }
    }
}
