package org.fmenchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.fmenchants.Commands.FMEnchantsCommand;
import org.fmenchants.Enchants.LumberjackEnchant;
import org.fmenchants.Listeners.AnvilListener;
import org.fmenchants.Listeners.LumberjackListener;
import org.fmenchants.TabCompleters.FMEnchantsTabCompleter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class FMEnchants extends JavaPlugin {
    private static FMEnchants instance;

    private static Map<String, Class<? extends Enchantment>> customEnchants = new HashMap<String, Class<? extends Enchantment>>();

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Enchantment.getByKey(LumberjackEnchant.KEY) == null) {
            Enchantment.registerEnchantment(new LumberjackEnchant());
        }
        customEnchants.put("lumberjack", LumberjackEnchant.class);


        getServer().getPluginManager().registerEvents(new LumberjackListener(), this);
        getServer().getPluginManager().registerEvents(new AnvilListener(), this);

        getCommand("fmenchants").setExecutor(new FMEnchantsCommand());
        getCommand("fmenchants").setTabCompleter(new FMEnchantsTabCompleter());
    }

    public static FMEnchants getInstance() {
        return instance;
    }

    public static Map<String, Class<? extends Enchantment>> getCustomEnchants() {
        return customEnchants;
    }
}
