package lt.tomexas.mysthud.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdventureUtils {

    private AdventureUtils() {
    }
    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final Pattern HEX_PATTERN = Pattern.compile("§x(§[0-9a-fA-F]){6}");

    public static String parseMiniMessage(String message) {
        return MINI_MESSAGE.serialize(MINI_MESSAGE.deserialize(message)).replaceAll("\\\\(?!u)(?!n)(?!\")", "");
    }

    public static String convertLegacyHexToMiniMessage(String legacyText) {
        // Create a matcher for the input string
        Matcher matcher = HEX_PATTERN.matcher(legacyText);
        StringBuffer result = new StringBuffer();

        // Process each match
        while (matcher.find()) {
            // Extract the hex color components (e.g., §2§D§2§0§0§F -> 2D200F)
            String hexCode = matcher.group()
                    .replace("§x", "")    // Remove the §x prefix
                    .replace("§", "");    // Remove all § symbols

            // Replace the match with the MiniMessage <color:#hexCode> format
            matcher.appendReplacement(result, "<color:#" + hexCode + ">");
        }

        // Append the rest of the string after the last match
        matcher.appendTail(result);

        return result.toString();
    }
}
