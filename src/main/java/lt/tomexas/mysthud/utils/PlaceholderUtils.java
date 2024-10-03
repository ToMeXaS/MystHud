package lt.tomexas.mysthud.utils;

import com.willfp.ecobits.currencies.Currencies;
import com.willfp.ecobits.currencies.CurrencyUtils;
import com.willfp.ecoskills.api.EcoSkillsAPI;
import com.willfp.ecoskills.stats.Stats;
import lt.tomexas.mysthud.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class PlaceholderUtils {

    public static final String COINS_CURRENCY_PLACEHODLER = "%vault_currency%";
    public static final String MYTHRIL_CURRENCY_PLACEHODLER = "%mythril_currency%";
    public static final String ETHER_CURRENCY_PLACEHODLER = "%ether_currency%";
    public static final String HEALTH_PLACEHODLER = "%health%";
    public static final String HEALTH_PROGRESSBAR_PLACEHODLER = "%health_progressbar%";
    public static final String DEFENSE_PLACEHODLER = "%defense%";
    public static final String STRENGTH_PLACEHODLER = "%strength%";

    private static final Utilities cfg = Main.get().getUtil();
    private static final Economy economy = Main.get().getEconomy();

    public static String translate(Player player, String message) {
        int balance = (int) economy.getBalance(player);
        int mythril = CurrencyUtils.getBalance(player, Currencies.getByID("Mythril")).intValue();
        int ether = CurrencyUtils.getBalance(player, Currencies.getByID("Ether")).intValue();

        int healthLevel = EcoSkillsAPI.getStatLevel(player, Stats.INSTANCE.getByID("health"));
        int defenseLevel = EcoSkillsAPI.getStatLevel(player, Stats.INSTANCE.getByID("defense"));
        int strLevel = EcoSkillsAPI.getStatLevel(player, Stats.INSTANCE.getByID("strength"));

        message = message.replace(COINS_CURRENCY_PLACEHODLER,
                        "<font:hud:currencies>"+ cfg.getCurrencySpacer("vault", cfg.formatNumber(balance, 10_000).length()) +"<#FFFF55>%s</font>".formatted(cfg.formatNumber(balance, 10_000)))
                .replace(MYTHRIL_CURRENCY_PLACEHODLER,
                        "<font:hud:currencies>" + cfg.getCurrencySpacer("mythril", cfg.formatNumber(mythril, 10_000).length()) +"<#00acdf>%s</font>".formatted(cfg.formatNumber(mythril, 10_000)))
                .replace(ETHER_CURRENCY_PLACEHODLER,
                        "<font:hud:currencies>" + cfg.getCurrencySpacer("ether", cfg.formatNumber(ether, 10_000).length()) +"<#FFAA00>%s</font>".formatted(cfg.formatNumber(ether, 10_000)))
                .replace(HEALTH_PLACEHODLER,
                        "<font:hud:stats>" + cfg.getNegativeSpacer(103) + cfg.getStatsSpacer(cfg.formatNumber((int) player.getHealth(), 1_000).length()) + fontImagesWrapper.GLYPH_HP + cfg.getPositiveSpacer(4) + cfg.formatNumber((int) player.getHealth(), 1_000) + cfg.getStatsSpacer(cfg.formatNumber((int) player.getHealth(), 1_000).length()) + "</font>")
                .replace(DEFENSE_PLACEHODLER,
                        "<font:hud:stats>" + cfg.getPositiveSpacer(2) + cfg.getStatsSpacer(cfg.formatNumber(defenseLevel, 1_000).length()) + fontImagesWrapper.GLYPH_DEFENSE + cfg.getPositiveSpacer(4) + cfg.formatNumber(defenseLevel, 1_000) + cfg.getStatsSpacer(cfg.formatNumber(defenseLevel, 1_000).length()) + "</font>")
                .replace(STRENGTH_PLACEHODLER,
                        "<font:hud:stats>" + cfg.getPositiveSpacer(2) + cfg.getStatsSpacer(cfg.formatNumber(strLevel, 1_000).length()) + fontImagesWrapper.GLYPH_STRENGTH + cfg.getPositiveSpacer(4) + cfg.formatNumber(strLevel, 1_000) + cfg.getStatsSpacer(cfg.formatNumber(strLevel, 1_000).length()) + "</font>")
                .replace(HEALTH_PROGRESSBAR_PLACEHODLER,
                        "<font:hud:main>" + cfg.getNegativeSpacer(88) + "<gradient:dark_red:red>" + cfg.getProgressBar((int) player.getHealth(), (healthLevel > (int) player.getHealth()) ? healthLevel : healthLevel + 20, 41) + "</gradient></font>");
        return message;
    }

}
