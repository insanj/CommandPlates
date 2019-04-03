package me.insanj.commandplates;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
    private Map<String, Map<Location, Date>> lastPlayerActivations;
    private Map<String, String> currentlyActivatedPlateName;
    private final long activationBottleneck = 1;

    public CommandPlatesListener(CommandPlatesPlugin plugin, CommandPlatesConfig config) {
        this.plugin = plugin;
        this.config = config;

        this.lastPlayerActivations = new HashMap();
        this.currentlyActivatedPlateName = new HashMap();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
      if (e == null) { return; }

      Block block = e.getClickedBlock();
      if (block == null) {
        Location underneathLocation = e.getPlayer().getLocation().clone().subtract(0, 1, 0);
        block = underneathLocation.getBlock();
      }

      boolean isPressurePlate = config.blockIsPressurePlate(block);
      boolean isWeightedPressurePlate = config.blockIsWeightedPressurePlate(block);
      if (isPressurePlate == false && isWeightedPressurePlate == false) {
        return;
      }

      Player player = e.getPlayer();
      Location location = block.getLocation();
      Location integerLocation = new Location(location.getWorld(), Math.floor(location.getX()), Math.floor(location.getY()), Math.floor(location.getZ()));

      // bottleneck at 1 activation per sec
      Map<Location, Date> playerActivations = lastPlayerActivations.get(player.getName());
      if (playerActivations != null) {
        Date lastTimePlayerActivatedThisPlate = playerActivations.get(integerLocation);
        if (lastTimePlayerActivatedThisPlate != null && getDateDiff(lastTimePlayerActivatedThisPlate, new Date(), TimeUnit.SECONDS) < activationBottleneck) {
          return;
        }
      }

      Map<Location, Date> updatedActivations = new HashMap();
      updatedActivations.put(integerLocation, new Date());
      lastPlayerActivations.put(player.getName(), updatedActivations);


      CommandPlatesListener listener = this;
      Map<String, Object> activatedPlate = config.getActivatedPlate(location);
      String plateName = config.getNameForPlateAtLocation(location);
      if (activatedPlate == null) {
        return; // no plate found at location
      }

      if (listener.config.hasPermissionToRunPlate(player, plateName, activatedPlate) == false) {
        return; // does not have permission to run plate
      }

      // check if this plate is still marked as "active"
      if (isWeightedPressurePlate == true) {
        String alreadyActivatedPlateName = currentlyActivatedPlateName.get(player.getName());
        if (alreadyActivatedPlateName != null && alreadyActivatedPlateName.equals(plateName)) {
          if (block.isBlockIndirectlyPowered() == true) { // 1st thing weighted sends is FALSE, then rest are TRUE
            return; // we are leaving the plate / deactivating
          }
        }
      }

      listener.config.debugLog(String.format("Activating plate %s for player %s!", activatedPlate.toString(), player.toString()));

      currentlyActivatedPlateName.put(player.getName(), plateName); // only needed for weighted plates
      listener.runCommandFromPlate(player, activatedPlate);
    }

    private void runCommandFromPlate(Player player, Map<String, Object> plate) {
      List<String> commandList = config.getPlateCommandList(plate);
      for (String commandString : commandList) {
        // Schedules a once off task to occur as soon as possible.
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
          @Override
          public void run() {
            String commandWithPlayer = commandString.replaceAll("%player%", player.getName());
            if (config.getConsoleBoolFromPlate(plate) == true) {
              Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), commandWithPlayer);
            } else {
              Bukkit.getServer().dispatchCommand(player, commandWithPlayer);
            }
          }
        });
      }
    }

  /**
    https://stackoverflow.com/questions/1555262/calculating-the-difference-between-two-java-date-instances
   * Get a diff between two dates
   * @param date1 the oldest date
   * @param date2 the newest date
   * @param timeUnit the unit in which you want the diff
   * @return the diff value, in the provided unit
 */
  private long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
    long diffInMillies = date2.getTime() - date1.getTime();
    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
  }
}
