package lt.tomexas.mysthud.hud;

import lt.tomexas.mysthud.Database;
import lt.tomexas.mysthud.Main;
import lt.tomexas.mysthud.utils.AdventureUtils;
import lt.tomexas.mysthud.utils.PlaceholderUtils;
import lt.tomexas.mysthud.utils.Utilities;
import lt.tomexas.mysthud.utils.fontImagesWrapper;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HudManager {

    public final NamespacedKey hudToggleKey;
    public final NamespacedKey hudDisplayKey;
    private final HudEvents hudEvents;
    private static HudTask hudTask;
    private static boolean hudTaskEnabled;
    private final Map<Player, Map<String, Hud>> playerHuds;

    public HudManager() {

        hudEvents = new HudEvents();
        hudTaskEnabled = false;
        hudToggleKey = new NamespacedKey(Main.get(), "hud_toggle");
        hudDisplayKey = new NamespacedKey(Main.get(), "hud_display");
        playerHuds = new HashMap<>();
        for (Player player : hudEnabledPlayers()) loadHud(player);

    }

    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(hudEvents, Main.get());
    }

    public void unregisterEvents() {
        HandlerList.unregisterAll(hudEvents);
    }

    public void reregisterEvents() {
        unregisterEvents();
        registerEvents();
    }

    public final Map<Player, Map<String, Hud>> getPlayerHuds() {
        return playerHuds;
    }
    public Hud getHudFromID(Player player, final String id) {
        Map<String, Hud> hud = playerHuds.get(player);
        return hud.get(id);
    }

    public String getHudID(Hud hud) {
        for(Map.Entry<Player, Map<String, Hud>> entry : playerHuds.entrySet()) {
            for (Map.Entry<String, Hud> hudEntry : entry.getValue().entrySet()) {
                if (hudEntry.getValue().equals(hud)) {
                    return hudEntry.getKey();
                }
            }
        }
        return null;
    }

    public boolean hasActiveHud(Player player) {
        return !player.getPersistentDataContainer().getOrDefault(hudDisplayKey, PersistentDataType.STRING, "").isEmpty();
    }

    public Hud getActiveHud(Player player) {
        Map<String, Hud> hudMap = playerHuds.get(player);
        return hudMap.get(player.getPersistentDataContainer().get(hudDisplayKey, PersistentDataType.STRING));
    }

    public void setActiveHud(Player player, Hud hud) {
        player.getPersistentDataContainer().set(hudDisplayKey, PersistentDataType.STRING, getHudID(hud));
    }

    public boolean getHudState(Player player) {
        return player.getPersistentDataContainer().getOrDefault(hudToggleKey, PersistentDataType.BOOLEAN, true);
    }

    public void setHudState(Player player, boolean state) {
        player.getPersistentDataContainer().set(hudToggleKey, PersistentDataType.BOOLEAN, state);
    }

    public Map<Player, Hud> getDefaultEnabledHuds() {
        return playerHuds.entrySet()
                .stream()
                .flatMap(entry -> entry.getValue().values().stream()
                        .filter(Hud::isEnabledByDefault)
                        .map(hud -> Map.entry(entry.getKey(), hud)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void updateHud(final Player player, Hud hud) {
        if (hud == null || hud.getDisplayText() == null || !getHudState(player)) return;

        parsedHudDisplays.putIfAbsent(hud, translateMiniMessageTagsForHud(hud));
        String hudDisplay = parsedHudDisplays.get(hud);
        hudDisplay = translatePlaceholdersForHudDisplay(player, hudDisplay);
        hud.getBossBar().name(AdventureUtils.MINI_MESSAGE.deserialize(hudDisplay));
    }

    public void setTempHudDisplay(final Player player, Hud hud) {
        String defaultDisplay = parsedHudDisplays.get(hud);
        parsedHudDisplays.put(hud, "test");
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.get(), () -> {
            parsedHudDisplays.put(hud, defaultDisplay);
        }, 100);
    }

    public void addTempHudDisplay(Hud hud, String displayText) {
        String defaultDisplay = parsedHudDisplays.get(hud);
        displayText = AdventureUtils.parseMiniMessage(displayText);
        parsedHudDisplays.put(hud, parsedHudDisplays.get(hud) + displayText);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.get(), () -> {
            if (!hud.getPlayer().isOnline()) return;
            parsedHudDisplays.put(hud, defaultDisplay);
        }, 100);
    }

    public void disableHud(final Player player, Hud hud) {
        Main.get().getAudience().player(player).hideBossBar(hud.getBossBar());
    }

    public void enableHud(final Player player, Hud hud) {
        Main.get().getAudience().player(player).showBossBar(hud.getBossBar());
    }

    public void registerTask() {
        if (hudTaskEnabled) return;
        if (hudTask != null) hudTask.cancel();
        if (playerHuds.isEmpty()) return;

        hudTask = new HudTask();
        hudTask.runTaskTimer(Main.get(), 0, 1);
        hudTaskEnabled = true;
    }

    public void unregisterTask() {
        if (!hudTaskEnabled) return;
        if (hudTask != null) hudTask.cancel();
        hudTaskEnabled = false;
    }

    public void restartTask() {
        unregisterTask();
        registerTask();
    }

    private List<? extends Player> hudEnabledPlayers() {
        return Bukkit.getOnlinePlayers().stream().filter(this::getHudState).toList();
    }

    public void loadHud(Player player) {
        final Map<String, Hud> hudMap = new HashMap<>();
        final BossBar bossBarHud = BossBar.bossBar(Component.text(), 0, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
        final String displayText = getBossBarTitle(player);

        Hud hud = new Hud(
                player,
                bossBarHud,
                displayText,
                "hud.playerhud",
                false,
                true,
                false);
        hudMap.put("player_hud", hud);
        playerHuds.put(player, hudMap);

        enableHud(player, hud);
    }

    public Map<Hud, String> parsedHudDisplays;

    public Map<Hud, String> generateHudDisplays() {
        Map<Hud, String> hudDisplays = new HashMap<>();
        for (Map.Entry<Player, Map<String, Hud>> entry : playerHuds.entrySet()) {
            for (Map.Entry<String, Hud> hudEntry : entry.getValue().entrySet()) {
                hudDisplays.put(hudEntry.getValue(), translateMiniMessageTagsForHud(hudEntry.getValue()));
            }
        }

        return hudDisplays;
    }

    private String translateMiniMessageTagsForHud(Hud hud) {
        return AdventureUtils.parseMiniMessage(hud.getDisplayText());
    }

    private String translatePlaceholdersForHudDisplay(final Player player, String message) {
        return PlaceholderUtils.translate(player, message);
    }

    private String getBossBarTitle(Player player) {
        return "<font:hud:main>" + Utilities.getPositiveSpacer(55) + fontImagesWrapper.PLAYERHEAD_FRAME + fontImagesWrapper.HEALTHBAR_EMPTY
                + Utilities.getNegativeSpacer(112) + fontImagesWrapper.COINS + fontImagesWrapper.MYTHRIL + fontImagesWrapper.ETHER + "</font>"
                + PlaceholderUtils.COINS_CURRENCY_PLACEHODLER
                + PlaceholderUtils.MYTHRIL_CURRENCY_PLACEHODLER
                + PlaceholderUtils.ETHER_CURRENCY_PLACEHODLER
                + PlaceholderUtils.HEALTH_PLACEHODLER
                + PlaceholderUtils.DEFENSE_PLACEHODLER
                + PlaceholderUtils.STRENGTH_PLACEHODLER
                + PlaceholderUtils.HEALTH_PROGRESSBAR_PLACEHODLER
                + "<font:hud:main>" + Utilities.getNegativeSpacer(150) + "</font>"
                + "<font:hud:playerhead>" + AdventureUtils.convertLegacyHexToMiniMessage(Database.getPlayerHead(player)) + "</font>";
    }
}
