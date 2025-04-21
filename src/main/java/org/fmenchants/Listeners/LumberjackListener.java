package org.fmenchants.Listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.fmenchants.Enchants.LumberjackEnchant;

import java.util.HashSet;
import java.util.Set;

public class LumberjackListener implements Listener {

    private final Set<Material> logBlocks = new HashSet<>();

    public LumberjackListener() {
        logBlocks.addAll(Set.of(
                Material.OAK_LOG, Material.BIRCH_LOG, Material.SPRUCE_LOG,
                Material.DARK_OAK_LOG, Material.ACACIA_LOG, Material.JUNGLE_LOG
        ));

        try {
            logBlocks.add(Material.valueOf("MANGROVE_LOG"));
            logBlocks.add(Material.valueOf("CHERRY_LOG"));
        } catch (IllegalArgumentException ignored) { }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();

        if (!player.hasPermission("fmenchants.use")) return;

        if (!tool.containsEnchantment(Enchantment.CHANNELING.getByKey(LumberjackEnchant.KEY))) return;
        if (!logBlocks.contains(event.getBlock().getType())) return;

        Set<Block> visited = new HashSet<>();
        chopTree(event.getBlock(), visited);
    }

    private void chopTree(Block block, Set<Block> visited) {
        if (visited.contains(block)) return;
        if (!logBlocks.contains(block.getType())) return;

        visited.add(block);
        block.breakNaturally();

        for (BlockFace face : BlockFace.values()) {
            Block relative = block.getRelative(face);
            chopTree(relative, visited);
        }
    }
}

