package lt.tomexas.mysthud;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import lt.tomexas.mysthud.utils.Utilities;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@Command(name = "testbar")
public class TestCommand {

    private final Main plugin;
    private final Utilities utilities;

    private final String sender = "NPC";
    private final String message = "Welcome, adventurer! I am Tadassi, your guide to the wondrous world of Mystaria. Ready to start your journey?";


    public TestCommand(Main plugin) {
        this.plugin = plugin;
        this.utilities = plugin.getUtil();
    }

    @Execute
    void command(@Context CommandSender sender) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        Audience p = this.plugin.getAudience().player(player);
        MiniMessage mm = MiniMessage.miniMessage();

        p.sendMessage(mm.deserialize("<font:main:hud/playerhead>" + this.plugin.getDatabase().getPlayerHead(player) + "</font>"));

        //this.plugin.getDatabase().addNPC(QuestsPlugin.getPlugin().getNpcManager().getById("znpcs#1"));

        /*List<String> paragraph = questUtil.formatAsParagraph(message, 45);
        StringBuilder stringComponent = new StringBuilder();
        String longest = paragraph.stream().max(Comparator.comparing(String::length)).orElse(null);

        for (int i = 0; i < paragraph.size(); i++) {
            int width = questUtil.getWidth(getOrDefault(paragraph, i, ""));

            StringBuilder negativeSpacing = new StringBuilder();
            for (int px : util.divideIntoPowersOfTwo(width))
                negativeSpacing.append(util.convertToNegativeUnicode(px));

            getLogger().info(width + "[dialog_%s]".formatted(i));
                stringComponent
                        .append("<font:main:dialog_%s>".formatted(i))
                        .append(negativeSpacing)
                        .append(questUtil.createBg(questUtil.getWidth(paragraph.get(i))))
                        .append(negativeSpacing)
                        .append(paragraph.get(i))
                        .append("</font>");
        }

        TextComponent textComponent = Component.text(stringComponent.toString());

        getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {

            MiniMessage mm = MiniMessage.miniMessage();
            p.sendActionBar(mm.deserialize(textComponent.content()));
        }, 0L, 0L);*/
    }

    public <T> T getOrDefault(List<T> list, int index, T defaultValue) {
        if (index >= 0 && index < list.size()) {
            return list.get(index);
        } else {
            return defaultValue;
        }
    }

    private int getLongestParagraph(List<String> paragraph) {
        String longest = null;
        for (int i = 0; i < paragraph.size(); i++) {
            if (paragraph.get(i).length() > paragraph.get(i+1).length()) {
                longest = paragraph.get(i);
            }
        }
        return longest.length();
    }
}
