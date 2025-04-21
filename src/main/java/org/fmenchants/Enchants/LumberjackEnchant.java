package org.fmenchants.Enchants;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.fmenchants.Util;

public class LumberjackEnchant extends Enchantment {
    public static final NamespacedKey KEY = new NamespacedKey("fmenchantments", "lumberjack");

    public LumberjackEnchant() {
        super(KEY);
    }

    @Override
    public String getName() {
        return Util.getLocaleFormatted("lumberjack.name", false);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return itemStack.getType().name().contains("_AXE");
    }
}
