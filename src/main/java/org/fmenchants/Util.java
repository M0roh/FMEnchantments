package org.fmenchants;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Util {
    public static String getLocaleFormatted(String localePath) {
        String locale = FMEnchants.getInstance().getConfig().getString(localePath);
        return processColors(locale);
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
