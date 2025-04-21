package org.fmenchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.fmenchants.Enchants.LumberjackEnchant;
import org.fmenchants.Listeners.LumberjackListener;

import java.lang.reflect.Field;

public final class FMEnchants extends JavaPlugin {
    public static FMEnchants instance;

    @Override
    public void onEnable() {
        instance = this;

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

        getServer().getPluginManager().registerEvents(new LumberjackListener(), this);
    }
    public static FMEnchants getInstance() {
        return instance;
    }
}
