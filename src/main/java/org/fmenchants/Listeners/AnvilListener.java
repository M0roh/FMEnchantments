package org.fmenchants.Listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.fmenchants.Enchants.LumberjackEnchant;

import java.util.Map;

public class AnvilListener implements Listener {
    private static final String MY_NAMESPACE = "fmenchantments";

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent e) {
        AnvilInventory inv = e.getInventory();
        ItemStack left  = inv.getItem(0);
        ItemStack right = inv.getItem(1);
        if (left == null || right == null) return;
        if (right.getType() != Material.ENCHANTED_BOOK) return;

        if (!(right.getItemMeta() instanceof EnchantmentStorageMeta)) return;
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) right.getItemMeta();

        ItemStack result = left.clone();
        ItemMeta im = result.getItemMeta();
        boolean changed = false;

        for (Map.Entry<Enchantment,Integer> entry : bookMeta.getStoredEnchants().entrySet()) {
            Enchantment ench = entry.getKey();
            int level = entry.getValue();

            if (im.hasEnchant(ench)) {
                int currentLevel = im.getEnchantLevel(ench);
                int maxLevel = ench.getMaxLevel();

                if (currentLevel >= maxLevel) continue;

                level = Math.min(currentLevel + level, maxLevel);
            }

            im.addEnchant(ench, level, true);
            changed = true;
        }

        if (changed) {
            result.setItemMeta(im);
            e.setResult(result);
        }
    }
}

