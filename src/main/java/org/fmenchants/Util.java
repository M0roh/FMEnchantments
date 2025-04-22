package org.fmenchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Util {
    public static String getPluginPrefix() {
        return processColors(FMEnchants.getInstance().getConfig().getString("prefix"));
    }

    public static String getLocaleFormatted(String localePath, boolean needPrefix) {
        return getLocaleFormatted(localePath, Collections.emptyMap(), needPrefix);
    }

    public static String getLocaleFormatted(String localePath, Map<String, String> placeholders, boolean needPrefix) {
        String locale = FMEnchants.getInstance().getConfig().getString(localePath);
        if (locale == null || locale.isEmpty()) {
            locale = "";
            FMEnchants.getInstance().getLogger().severe(processColors("&cError! Unable get localization " + localePath + "!"));
        }
        locale = processColors(processPlaceholders(locale, placeholders));

        if (needPrefix)
            locale = getPluginPrefix() + locale;
        return locale;
    }

    public static String processPlaceholders(String input, Map<String, String> placeholders) {
        Pattern pattern = Pattern.compile("%(\\w+)%");
        Matcher matcher = pattern.matcher(input);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = placeholders.getOrDefault(key, matcher.group(0));
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    public static String processColors(String input) {
        Pattern hexPattern = Pattern.compile("(?i)&?#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(input);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append("§").append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }
        matcher.appendTail(buffer);

        return buffer.toString().replaceAll("&([0-9a-fk-or])", "§$1");
    }
}
