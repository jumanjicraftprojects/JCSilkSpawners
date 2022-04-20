package dev.appeazethecheese.jcsilkspawners;

import org.bukkit.plugin.java.JavaPlugin;

public final class JCSilkSpawners extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Events(), this);
    }

    @Override
    public void onDisable() {
    }
}
