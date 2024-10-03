package lt.tomexas.mysthud.dependencies.placeholders;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import lt.tomexas.mysthud.Main;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MystHoldersExpansion extends PlaceholderExpansion {

    private final Main plugin;

    public MystHoldersExpansion(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "ToMeXaS";


    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "mh";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0.0";


    }

    @Override
    public boolean persist() {
        return true;


    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.contains("convert_img_")) {
            String[] string = params.split("convert_img_");
            if (string.length > 0) {
                String balance = PlaceholderAPI.setPlaceholders(player, "%" + string[1] + "%");
                balance =  balance
                        .replace("0", ":0:")
                        .replace("1", ":1:")
                        .replace("2", ":2:")
                        .replace("3", ":3:")
                        .replace("4", ":4:")
                        .replace("5", ":5:")
                        .replace("6", ":6:")
                        .replace("7", ":7:")
                        .replace("8", ":8:")
                        .replace("9", ":9:")
                        .replace(",", ":c:")
                        .replace(".", ":d:");
                balance = insert(balance, ":offset_-2:", 3);
                return balance;
            }
            else {
                return "0";
            }

        } else if (params.contains("island_members_exclude_me")) {
            if (player.getPlayer() == null) return "";
            Island island = SuperiorSkyblockAPI.getPlayer(player.getPlayer()).getIsland();

            List<SuperiorPlayer> superiorPlayers = island.getIslandMembers(true);
            List<SuperiorPlayer> updatedPlayers = new ArrayList<>();
            for (SuperiorPlayer superiorPlayer : superiorPlayers)
                if (!(superiorPlayer.getUniqueId().equals(player.getUniqueId())))
                    updatedPlayers.add(superiorPlayer);

            Collections.reverse(updatedPlayers);
            int playerNum = Integer.parseInt(params.substring(26));
            if (playerNum > (superiorPlayers.size()-1)) return "Max num - " + (superiorPlayers.size()-1);
            return updatedPlayers.get(playerNum).getName();

        }

        return null;
    }

    private String insert(String text, String insert, int period) {
        StringBuilder builder = new StringBuilder(
                text.length() + insert.length() * (text.length()/period)+1);

        int index = 0;
        String prefix = "";
        while (index < text.length())
        {
            // Don't put the insert in the very first iteration.
            // This is easier than appending it *after* each substring
            builder.append(prefix);
            prefix = insert;
            builder.append(text.substring(index,
                    Math.min(index + period, text.length())));
            index += period;
        }
        return builder.toString();
    }
}
