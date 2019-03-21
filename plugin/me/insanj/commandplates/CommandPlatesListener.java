package me.insanj.commandplates;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Block;

public class CommandPlatesListener implements Listener {
    private final CommandPlatesPlugin plugin;
    private final CommandPlatesConfig config;

    public CommandPlatesListener(CommandPlatesPlugin plugin, CommandPlatesConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
      if (e == null) { return; }

      Block block =  e.getClickedBlock();
      if (block == null) { return; }

     // List<BlockType> pressurePlateTypes = Arrays.asList(Material.ACACIA_PRESSURE_PLATE, Material.BIRCH_PRESSURE_PLATE, Material.DARK_OAK_PRESSURE_PLATE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, Material.JUNGLE_PRESSURE_PLATE, Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.OAK_PRESSURE_PLATE, Material.SPRUCE_PRESSURE_PLATESTONE_PRESSURE_PLATE);

      if (block.getType() == Material.STONE_PLATE || block.getType() == Material.WOOD_PLATE || block.getType() == Material.GOLD_PLATE || block.getType() == Material.IRON_PLATE) {
        Location location = block.getLocation();
        Location integerLocation = new Location(location.getWorld(), Math.floor(location.getX()), Math.floor(location.getY()), Math.floor(location.getZ()));
        Player player = e.getPlayer();

        plugin.getLogger().info(String.format("Detected %s on pressure plate @ %s, checking if it's command activated...", player.getName(), integerLocation.toString()));
        CommandPlatesListener listener = this;
//Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
        //    @Override
        //    public void run() {
              Map<String, Object> activatedPlate = config.getActivatedPlate(location);
              if (activatedPlate != null) {
                if (listener.config.hasPermissionToRunPlate(player, activatedPlate) == true) {
                    listener.plugin.getLogger().info(String.format("Activating plate %s for player %s!", activatedPlate.toString(), player.toString()));
                    listener.runCommandFromPlate(player, activatedPlate);
                }
              }
      //      }
      //  });
      }
    }

    private void runCommandFromPlate(Player player, Map<String, Object> plate) {
      List<String> commandList = config.getActivatedPlateCommandList(plate);
      for (String commandString : commandList) {
        // Schedules a once off task to occur as soon as possible.
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
          @Override
          public void run() {
            if (config.getConsoleBoolFromPlate(plate) == true) {
              Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandString);
            } else {
              Bukkit.getServer().dispatchCommand(player, commandString);
            }
          }
        });
      }
    }

}
