package lt.tomexas.mysthud.dependencies.skills;

import com.willfp.ecoskills.api.EcoSkillsAPI;
import com.willfp.ecoskills.api.event.PlayerSkillXPGainEvent;
import com.willfp.ecoskills.skills.Skill;
import lt.tomexas.mysthud.Main;
import lt.tomexas.mysthud.hud.Hud;
import lt.tomexas.mysthud.hud.HudManager;
import lt.tomexas.mysthud.utils.Utilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.text.DecimalFormat;

public class PlayerSkillXpGainListener implements Listener {

    private final Main plugin;
    private final Utilities utilities;
    private double xp;
    private double reqXp;

    public PlayerSkillXpGainListener(Main plugin) {
        this.plugin = plugin;
        this.utilities = plugin.getUtil();
    }

    @EventHandler
    public void PlayerSkillsXpGain(PlayerSkillXPGainEvent event) {
        Player player = event.getPlayer();
        HudManager hudManager = Main.get().getHudManager();
        Hud hud = hudManager.getActiveHud(player);
        Skill skill = event.getSkill();
        int level = skill.getActualLevel$core_plugin(player);
        String skillName = skill.getName();

        double gained_xp = event.getGainedXP();
        xp = EcoSkillsAPI.getSkillXP(player, skill) + gained_xp;
        reqXp = EcoSkillsAPI.getRequiredXP(player, skill);

        DecimalFormat df;
        df = (xp % 1 == 0 && reqXp % 1 == 0 && gained_xp % 1 == 0) ? new DecimalFormat("#") : new DecimalFormat("#.00");
        String skillString = "<font:minecraft:default><white>"
                + skillName + " | " + "(" + df.format(xp) + "/" + df.format(reqXp) + ")</font>";

        hudManager.addTempHudDisplay(hud, skillString);


        //final TextComponent textComponent = Component.text("<font:main:hud/skillbar>"
                //+ this.utilities.getNegativeSpacer(130)
                //+ skillString
               // + "</font>");

    }
}
