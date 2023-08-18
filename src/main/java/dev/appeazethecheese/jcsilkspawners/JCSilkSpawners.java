package dev.appeazethecheese.jcsilkspawners;

import org.bukkit.plugin.java.JavaPlugin;

public final class JCSilkSpawners extends JavaPlugin {

    public static JCSilkSpawners Instance;

    public JCSilkSpawners(){
        Instance = this;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Events(), this);
    }

    @Override
    public void onDisable() {
    }
}
