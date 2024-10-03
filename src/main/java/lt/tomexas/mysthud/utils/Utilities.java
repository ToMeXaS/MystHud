package lt.tomexas.mysthud.utils;

import lt.tomexas.mysthud.Database;
import lt.tomexas.mysthud.Main;
import net.milkbowl.vault.economy.Economy;

import java.util.ArrayList;
import java.util.List;

public class Utilities {

    private final Main plugin;
    private final Database database;
    private final Economy economy;

    public Utilities(Main plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
        this.economy = plugin.getEconomy();
    }

    public String getCurrencySpacer(String type, int balanceLength) {
        if (type.equalsIgnoreCase("vault"))
            return switch (balanceLength) {
                case 1 -> getNegativeSpacer(80);
                case 2 -> getNegativeSpacer(84);
                case 3 -> getNegativeSpacer(88);
                case 4 -> getNegativeSpacer(92);
                default -> "";
            };
        else {
            return switch (balanceLength) {
                case 1 -> getPositiveSpacer(32);
                case 2 -> getPositiveSpacer(28);
                case 3 -> getPositiveSpacer(24);
                case 4 -> getPositiveSpacer(20);
                default -> "";
            };
        }
    }

    public String getStatsSpacer(int numLength) {
        return switch (numLength) {
            case 1 -> getPositiveSpacer(7);
            case 2 -> getPositiveSpacer(4);
            case 3 -> getPositiveSpacer(1);
            default -> "";
        };
    }

    public static String getNegativeSpacer(int pixel) {
        StringBuilder negativeSpacer = new StringBuilder();
        for (int px : divideIntoPowersOfTwo(pixel))
            negativeSpacer.append(convertToNegativeUnicode(px));

        return negativeSpacer.toString();
    }

    public static char convertToNegativeUnicode(int pixel) {
        return switch (pixel) {
            case 1 -> '\uF801';
            case 2 -> '\uF802';
            case 4 -> '\uF804';
            case 8 -> '\uF808';
            case 16 -> '\uF809';
            case 32 -> '\uF80A';
            case 64 -> '\uF80B';
            case 128 -> '\uF80C';
            default -> throw new IllegalStateException("Unexpected value: " + pixel);
        };
    }

    public static String getPositiveSpacer(int pixel) {
        StringBuilder positiveSpacer = new StringBuilder();
        for (int px : divideIntoPowersOfTwo(pixel))
            positiveSpacer.append(convertToPositiveUnicode(px));

        return positiveSpacer.toString();
    }

    public static char convertToPositiveUnicode(int pixel) {
        return switch (pixel) {
            case 1 -> '\uF821';
            case 2 -> '\uF822';
            case 4 -> '\uF824';
            case 8 -> '\uF828';
            case 16 -> '\uF829';
            case 32 -> '\uF82A';
            case 64 -> '\uF82B';
            case 128 -> '\uF82C';
            default -> throw new IllegalStateException("Unexpected value: " + pixel);
        };
    }

    public static List<Integer> divideIntoPowersOfTwo(int totalWidth) {
        List<Integer> result = new ArrayList<>();

        // Start with the largest power of 2 we want to consider (128)
        int[] powers = {128, 64, 32, 16, 8, 4, 2, 1}; // Define powers of 2

        for (int power : powers) {
            while (totalWidth >= power) {
                result.add(power);
                totalWidth -= power; // Subtract the power from totalWidth
            }
        }
        return result;
    }

    public String formatNumber(long number, int start) {
        if (number >= 1_000_000_000) {
            return String.format("%dB", number / 1_000_000_000);
        } else if (number >= 1_000_000) {
            return String.format("%dM", number / 1_000_000);
        } else if (number >= start) {
            return String.format("%dK", number / 1_000);
        } else {
            return String.valueOf(number);
        }
    }

    public String getProgressBar(double progress, double maxProgress, int length) {
        double percentage = progress / maxProgress * 100.0;
        StringBuilder progressBar = new StringBuilder();
        double percentPerChar = 1.0 / (double) length * 100.0;
        String unicodeChar = "\uF801"; // -1px
        String barChar = "\uE118";

        for (int i = 0; i < length; ++i) {
            double progressPassBar = percentPerChar * (double) (i + 1);

            if (percentage >= progressPassBar) {
                if (i + 1 == length) {
                    progressBar.append(unicodeChar).append(barChar);
                } else {
                    progressBar.append(unicodeChar).append(barChar);
                }
            } else if (percentage > progressPassBar - percentPerChar) {
                if (i + 1 == length) {
                    progressBar.append(unicodeChar).append(barChar);
                } else {
                    progressBar.append(unicodeChar).append(barChar);
                }
            } else {
                if (i + 1 == length) {
                    progressBar.append(unicodeChar).append("<dark_gray>" + barChar + "</dark_gray>");
                } else {
                    progressBar.append(unicodeChar).append("<dark_gray>" + barChar + "</dark_gray>");
                }
            }
        }

        return progressBar.toString();
    }

    public String getSkillBar(double progress, double maxProgress) {
        double percentage = progress / maxProgress;

        // Determine the characters to use for dynamic progress
        char[] progressChars = {
                '\uE118',
                '\uE119',
                '\uE120',
                '\uE121',
                '\uE122',
                '\uE123',
                '\uE124',
                '\uE125',
                '\uE126',
                '\uE127',
                '\uE128',
                '\uE129',
                '\uE130',
                '\uE131',
                '\uE133',
                '\uE134',
                '\uE135',
                '\uE136',
                '\uE137',
                '\uE138',
                '\uE139',
                '\uE140',
                '\uE141',
                '\uE142',
                '\uE143',
                '\uE144',
                '\uE145',
                '\uE146',
                '\uE147',
                '\uE148',
                '\uE149',
                '\uE150',
                '\uE151',
                '\uE152',
                '\uE153',
                '\uE154'
        }; // Add more characters if needed

        // Calculate which character to display based on the progress
        int charIndex = (int) (percentage * (progressChars.length - 1));

        return String.valueOf(progressChars[charIndex]);
    }

    public char[] generateUnicodeChars(int startCodePoint, int count) {
        char[] unicodeChars = new char[count];

        for (int i = 0; i < count; i++) {
            // Generate the character by incrementing the code point
            unicodeChars[i] = (char) (startCodePoint + i);
        }

        return unicodeChars;
    }

    public List<String> formatAsParagraph(String input, int maxLineLength) {
        String[] words = input.split(" ");
        List<String> paragraph = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        int lineCount = 0;

        for (String word : words) {
            // Determine the current maximum line length
            int currentMaxLineLength = (lineCount < 2) ? maxLineLength : maxLineLength + 6;

            if (line.length() + word.length() > currentMaxLineLength) {
                paragraph.add(line.toString().trim());
                line = new StringBuilder();
                lineCount++; // Increment the line count after adding a line
            }
            line.append(word).append(" ");
        }

        // Append the last line
        if (!line.isEmpty()) {
            paragraph.add(line.toString().trim());
        }

        return paragraph;
    }
}
