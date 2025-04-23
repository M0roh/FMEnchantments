package org.fmenchants.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.fmenchants.FMEnchants;
import org.fmenchants.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FMEnchantsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 2 && !(args.length == 1 && sender instanceof Player)) {
            sender.sendMessage(Util.getLocaleFormatted("messages.invalid-usage", true));
            return true;
        }

        if (args[0].equalsIgnoreCase("give"))
            return giveCommand(sender, Arrays.copyOfRange(args, 1, args.length));

        return false;
    }

    public boolean giveCommand(CommandSender sender, String[] args) {
        String enchantName = args[0].toLowerCase();
        Class<? extends Enchantment> enchantmentClass = FMEnchants.getCustomEnchants().get(enchantName);

        if (enchantmentClass == null) {
            sender.sendMessage(Util.getLocaleFormatted("messages.enchant-not-found",
                    Map.of("name", enchantName), true));
            return true;
        }

        Enchantment enchantment = null;
        try {
            enchantment = enchantmentClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Player target;
        if (args.length >= 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Util.getLocaleFormatted("messages.player-not-found", Map.of("player", args[1]), true));
                return true;
            }
        } else {
            if (sender instanceof Player)
                target = (Player) sender;
            else {
                sender.sendMessage(Util.getLocaleFormatted("messages.must-specify-player", true));
                return true;
            }
        }

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.addStoredEnchant(enchantment, 1, true);
        meta.setLore(List.of(enchantment.getName()));
        book.setItemMeta(meta);

        target.getInventory().addItem(book);

        if (target.equals(sender)) {
            sender.sendMessage(Util.getLocaleFormatted("messages.book-received", Map.of("enchant", enchantment.getName()), true));
        } else {
            sender.sendMessage(Util.getLocaleFormatted("messages.book-given",
                    Map.of("enchant", enchantment.getName(), "player", target.getName()), true));
            target.sendMessage(Util.getLocaleFormatted("messages.book-received", Map.of("enchant", enchantment.getName()), true));
        }

        return true;
    }
}
