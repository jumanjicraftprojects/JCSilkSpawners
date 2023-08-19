package dev.appeazethecheese.jcsilkspawners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.text.html.parser.Entity;
import java.util.Locale;

public class Events implements Listener {
    private static final String CanBreakSpawnersPermission = "jc.silkspawners.canbreakspawners";

    @EventHandler
    public void onBlockBroken(BlockBreakEvent e){
        var block = e.getBlock();
        var brokeWith = e.getPlayer().getInventory().getItemInMainHand();
        if(block.getType() != Material.SPAWNER
        || !e.getPlayer().hasPermission(CanBreakSpawnersPermission)
        || e.getPlayer().getGameMode() != GameMode.SURVIVAL
        || !block.isPreferredTool(brokeWith)
        || !brokeWith.containsEnchantment(Enchantment.SILK_TOUCH))
            return;

        var originalSpawner = (CreatureSpawner)block.getState();
        var entity = originalSpawner.getSpawnedType();

        var item = new ItemStack(Material.SPAWNER);
        var itemMeta = (BlockStateMeta)item.getItemMeta();
        var newSpawner = (CreatureSpawner) itemMeta.getBlockState();
        e.setExpToDrop(0);
        newSpawner.setSpawnedType(entity);
        itemMeta.setBlockState(newSpawner);
        itemMeta.setDisplayName(getEntityDisplayName(entity) + " Spawner");
        item.setItemMeta(itemMeta);
        block.getWorld().dropItemNaturally(block.getLocation(), item);
    }

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent e){
        var block = e.getBlock();
        if(block.getType() != Material.SPAWNER)
            return;
        if(e.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;
        var item = e.getItemInHand();
        var itemMeta = (BlockStateMeta)item.getItemMeta();
        var spawnerInHand = (CreatureSpawner)itemMeta.getBlockState();
        var entity = spawnerInHand.getSpawnedType();

        JCSilkSpawners.Instance.getLogger().warning(entity.name());

        var placedBlock = e.getBlockPlaced();
        var placedSpawner = (CreatureSpawner) placedBlock.getState();
        Bukkit.getScheduler().runTaskLater(JCSilkSpawners.Instance, () -> {
            placedSpawner.setSpawnedType(entity);
            placedSpawner.update();
        }, 1L);
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent e){
        var slot1Item = e.getInventory().getItem(0);
        var slot2Item = e.getInventory().getItem(1);
        if(slot1Item != null){
            if(slot1Item.getType() == Material.SPAWNER)
                e.setResult(null);
        }
        if(slot2Item != null){
            if(slot2Item.getType() == Material.SPAWNER)
                e.setResult(null);
        }
    }

    @EventHandler
    public void onSpawnerSpawn(SpawnerSpawnEvent event){
        var spawner = event.getSpawner();
        if(spawner.getBlock().getBlockPower() > 0){
            event.setCancelled(true);
        }
    }

    private String getEntityDisplayName(EntityType entityType){
        var split = entityType.name().toLowerCase(Locale.ROOT).split("_");
        StringBuilder str = new StringBuilder();
        for(var word : split){
            var lower = word.toLowerCase(Locale.ROOT);
            var upperChar = Character.toUpperCase(lower.charAt(0));
            str.append(upperChar + lower.substring(1) + " ");
        }
        str.setLength(str.length() - 1);
        return str.toString();
    }
}
