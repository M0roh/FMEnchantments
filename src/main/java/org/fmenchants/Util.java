package org.fmenchants;

import org.bukkit.Material;
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

    public static String toRoman(int number) {
        if (number <= 0) return "";

        StringBuilder result = new StringBuilder();
        int[] values = {1000, 900, 500, 400, 100, 90,  50, 40, 10, 9, 5, 4, 1};
        String[] numerals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        for (int i = 0; i < values.length; i++) {
            while (number >= values[i]) {
                number -= values[i];
                result.append(numerals[i]);
            }
        }
        return result.toString();
    }

    public static boolean isValidRepairMaterial(ItemStack tool, ItemStack material) {
        if (tool == null || material == null) return false;

        Material toolType = tool.getType();
        Material repairMat = material.getType();

        Material NETHERITE_INGOT = Material.matchMaterial("NETHERITE_INGOT");
        boolean isNeth = NETHERITE_INGOT != null;

        if (isNeth && isNetheriteTool(toolType)) {
            return repairMat == NETHERITE_INGOT;
        }

        switch (toolType) {
            case DIAMOND_SWORD:
            case DIAMOND_AXE:
            case DIAMOND_PICKAXE:
            case DIAMOND_SHOVEL:
            case DIAMOND_HOE:
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
                return repairMat == Material.DIAMOND;

            case IRON_SWORD:
            case IRON_AXE:
            case IRON_PICKAXE:
            case IRON_SHOVEL:
            case IRON_HOE:
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
                return repairMat == Material.IRON_INGOT;

            case GOLDEN_SWORD:
            case GOLDEN_AXE:
            case GOLDEN_PICKAXE:
            case GOLDEN_SHOVEL:
            case GOLDEN_HOE:
            case GOLDEN_HELMET:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_LEGGINGS:
            case GOLDEN_BOOTS:
                return repairMat == Material.GOLD_INGOT;

            case STONE_SWORD:
            case STONE_AXE:
            case STONE_PICKAXE:
            case STONE_SHOVEL:
            case STONE_HOE:
                return repairMat == Material.COBBLESTONE;

            case WOODEN_SWORD:
            case WOODEN_AXE:
            case WOODEN_PICKAXE:
            case WOODEN_SHOVEL:
            case WOODEN_HOE:
                return isWoodenPlank(repairMat);

            default:
                return false;
        }
    }

    private static boolean isNetheriteTool(Material material) {
        String name = material.name();
        return name.startsWith("NETHERITE_");
    }

    private static boolean isWoodenPlank(Material mat) {
        String name = mat.name();
        return name.endsWith("_PLANKS");
    }
}
