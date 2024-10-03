package lt.tomexas.mysthud.hud;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Player;

public record Hud(Player player,
                  BossBar bossBar,
                  String displayText,
                  String perm,
                  boolean disableWhilstInWater,
                  boolean enabledByDefault,
                  boolean enableInSpectatorMode)
{

    public Player getPlayer() {
        return player;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public String getDisplayText() {
        return displayText;
    }

    public String getPerm() {
        return perm;
    }

    public boolean isEnabledByDefault() {
        return enabledByDefault;
    }

    public boolean isDisabledWhilistInWater() {
        return disableWhilstInWater;
    }

    public boolean enableInSpectatorMode() {
        return true;
    }
}
