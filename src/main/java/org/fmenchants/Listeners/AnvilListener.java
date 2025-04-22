package org.fmenchants.Listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.fmenchants.Enchants.LumberjackEnchant;
import org.fmenchants.FMEnchants;
import org.fmenchants.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnvilListener implements Listener {
    private static final String MY_NAMESPACE = "fmenchantments";

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent e) {
        AnvilInventory inv = e.getInventory();
        ItemStack left  = inv.getItem(0);
        ItemStack right = inv.getItem(1);
        if (left == null || right == null) return;

        if (!(right.getItemMeta() instanceof EnchantmentStorageMeta)) {
            if (left.getItemMeta() instanceof EnchantmentStorageMeta) {
                left = inv.getItem(1);
                right = inv.getItem(0);
            }
            else
                return;
        }
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) right.getItemMeta();

        ItemStack result = left.clone();
        ItemMeta im = result.getItemMeta();

        boolean changed = false;

        Map<Enchantment, Integer> combinedEnchants = new HashMap<>();

        if (im instanceof EnchantmentStorageMeta) {
            combinedEnchants.putAll(((EnchantmentStorageMeta) im).getStoredEnchants());
        }

        combinedEnchants.putAll(bookMeta.getStoredEnchants());

        for (Map.Entry<Enchantment, Integer> entry : combinedEnchants.entrySet()) {
            Enchantment ench = entry.getKey();
            int level = entry.getValue();

            if (im.hasEnchant(ench)) {
                int currentLevel = im.getEnchantLevel(ench);
                int maxLevel = ench.getMaxLevel();

                if (currentLevel >= maxLevel) continue;

                level = Math.min(currentLevel + level, maxLevel);
            }

            if (im instanceof EnchantmentStorageMeta)
                ((EnchantmentStorageMeta) im).addStoredEnchant(ench, level, true);
            else
                im.addEnchant(ench, level, true);
            changed = true;
        }

        if (changed) {
            result.setItemMeta(im);
            e.setResult(result);
            e.getInventory().setRepairCost(FMEnchants.getInstance().getConfig().getInt("levelCost"));
        }
    }
}

