package org.fmenchants.Listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
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
    @EventHandler (
            priority = EventPriority.HIGHEST
    )
    public void onPrepareAnvil(PrepareAnvilEvent e) {
        AnvilInventory inv = e.getInventory();
        ItemStack left  = inv.getItem(0);
        ItemStack right = inv.getItem(1);
        if (left == null)
            return;

        if (right == null) {
            ItemStack result = left.clone();
            ItemMeta meta = left.getItemMeta();

            if (meta == null)
                return;

            String rename = inv.getRenameText();
            if (rename != null && !rename.isEmpty())
                meta.setDisplayName(rename);

            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();

            processItemEnchants(meta, lore, meta.getEnchants());
            meta.setLore(lore);

            result.setItemMeta(meta);
            e.setResult(result);
            e.getInventory().setRepairCost(1);
            return;
        }

        if (!(right.getItemMeta() instanceof EnchantmentStorageMeta)) {
            if (left.getItemMeta() instanceof EnchantmentStorageMeta) {
                left = inv.getItem(1);
                right = inv.getItem(0);
            }
            else if (left.getType() == right.getType() && left.getType().getMaxDurability() > 0) {
                ItemStack result = left.clone();
                ItemMeta meta = result.getItemMeta();
                List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();

                int max = left.getType().getMaxDurability();
                int leftDamage = ((Damageable) left.getItemMeta()).getDamage();
                int rightDamage = ((Damageable) right.getItemMeta()).getDamage();

                int newDamage = max - ((max - leftDamage) + (max - rightDamage) + max) / 2;
                newDamage = Math.max(newDamage, 0);
                newDamage = Math.min(newDamage, max);

                Map<Enchantment, Integer> enchantments = new HashMap<>();
                enchantments.putAll(left.getEnchantments());
                enchantments.putAll(right.getEnchantments());

                processItemEnchants(meta, lore, enchantments);
                meta.setLore(lore);

                if (meta instanceof Damageable)
                    ((Damageable) meta).setDamage(newDamage);

                String rename = inv.getRenameText();
                if (rename != null && !rename.isEmpty())
                    meta.setDisplayName(rename);

                result.setItemMeta(meta);
                e.setResult(result);
                e.getInventory().setRepairCost(6);
                return;
            }

            else
                return;
        }

        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) right.getItemMeta();

        ItemStack result = left.clone();
        ItemMeta im = result.getItemMeta();
        List<String> lore = im.getLore() == null ? new ArrayList<>() : im.getLore();

        boolean changed = false;

        Map<Enchantment, Integer> combinedEnchants = new HashMap<>();

        if (im instanceof EnchantmentStorageMeta) {
            combinedEnchants.putAll(((EnchantmentStorageMeta) im).getStoredEnchants());
        }

        combinedEnchants.putAll(bookMeta.getStoredEnchants());

        changed = processItemEnchants(im, lore, combinedEnchants);

        if (changed) {
            String rename = inv.getRenameText();
            if (rename != null && !rename.isEmpty())
                im.setDisplayName(rename);

            im.setLore(lore);
            result.setItemMeta(im);
            e.setResult(result);
            e.getInventory().setRepairCost(FMEnchants.getInstance().getConfig().getInt("levelCost"));
        }
    }

    private boolean processItemEnchants(ItemMeta im, List<String> baseLore, Map<Enchantment, Integer> combinedEnchants) {
        boolean changed = false;

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

            boolean enchantFound = false;
            for (int i = 0; i < baseLore.size(); i++) {
                String loreLine = baseLore.get(i);
                if (loreLine.contains(ench.getName())) {
                    baseLore.set(i, Util.processColors(ench.getName() + " " +
                            (ench.getMaxLevel() > 1 ? Util.toRoman(level) : "")));
                    enchantFound = true;
                    break;
                }
            }

            if (!enchantFound) {
                if (ench.getKey().getNamespace().equalsIgnoreCase(FMEnchants.MY_NAMESPACE)) {
                    baseLore.add(Util.processColors(ench.getName() + " " +
                            (ench.getMaxLevel() > 1 ? Util.toRoman(level) : "")));
                }
            }
            changed = true;
        }

        return changed;
    }
}

