package me.insanj.commandplates;

import java.lang.Math;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandPlatesCommandExecutor implements CommandExecutor {
  private final CommandPlatesPlugin plugin;
  private final CommandPlatesConfig config;

  public CommandPlatesCommandExecutor(CommandPlatesPlugin plugin, CommandPlatesConfig config) {
    this.plugin = plugin;
    this.config = config;
  }

  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length <= 0) {
      return false;
    }

    String argumentString = args[0];
    if (argumentString.equals(config.COMMAND.LIST())) {
      return onCommandList(sender, args);
    } else if (argumentString.equals(config.COMMAND.INFO())) {
      return onCommandInfo(sender, args);
    } else if (argumentString.equals(config.COMMAND.CREATE())) {
      if (!(sender instanceof Player)) {
        sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
        return false;
      }

      Player player = (Player) sender;
      return onCommandCreate(player, args);
    }

    return false;
  }

  public boolean onCommandCreate(Player player, String[] args) {
    String createPermissionString = config.PERMISSION.CREATE();
    if (player.hasPermission(createPermissionString) == false || player.isOp() == false) {
      player.sendMessage(ChatColor.RED + "You do not have permission to create new command plates.");
      return true;
    }

    if (args.length < 4) {
      player.sendMessage(ChatColor.RED + "Not enough arguments included in command.");
      return false;
    }

    String plateName = args[1];
    boolean console = Boolean.parseBoolean(args[2]);

    String concatCommand = "";
    int i = 0;
    for (String arg : args) {
      if (i <= 2) {  }
      else if (i == 3) { concatCommand = arg; }
      else { concatCommand += String.format(" %s", arg); }
      i++;
    }

    String[] commandSplit = concatCommand.split(",");
    ArrayList<String> commandList = new ArrayList<>();
    for (String cmd: commandSplit) {
      commandList.add(cmd.trim());
    }

    String author = player.getName();
    Location location = player.getLocation();
    config.setPlate(plateName, author, location, console, commandList);

    plugin.getLogger().info(String.format("Created Plate with metadata:  name=%s, author=%s, location=%s, console=%s, commandList=%s", plateName, author, location.toString(), Boolean.toString(console), commandList.toString()));

    Block block = location.getBlock();
    if (config.blockIsPressurePlate(block) == false) {
      player.sendMessage(ChatColor.BLUE + "Command Plate has been set up, but the pressure plate was not detected where you are standing.");
    } else {
      player.sendMessage(ChatColor.GREEN + "Command Plate has been created!");
    }


    return true;
  }

  public boolean onCommandList(CommandSender sender, String[] args) {
    sender.sendMessage("All Command Plates:");
     Map<String, Map> plates = config.getPlates();
     for (String plateName : plates.keySet()) {
       String plateDisplayString = config.getPlateDisplayString(plateName);
       sender.sendMessage(ChatColor.BLUE + plateDisplayString);
     }

     return true;
  }

  public boolean onCommandInfo(CommandSender sender, String[] args) {
    if (args.length < 2) {
        // where you are standing or looking
        if (!(sender instanceof Player)) {
          sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
          return false;
        }

        Player player = (Player) sender;
        Location playerLoc = player.getLocation();
        Location location = new Location(playerLoc.getWorld(), Math.floor(playerLoc.getX()), Math.floor(playerLoc.getY()), Math.floor(playerLoc.getZ()));
        Map<String, Object> plate = config.getActivatedPlate(location);
        if (plate == null) {
          Location underneathLocation = location.clone().subtract(0, 1, 0);
          plate = config.getActivatedPlate(underneathLocation);
        }

        if (plate == null) {
          Location lookingAtLocation = player.getTargetBlock(null, 100).getLocation();
          plate = config.getActivatedPlate(lookingAtLocation);
        }

        if (plate == null) {
          sender.sendMessage(ChatColor.RED + "Could not find a Command Plate being targeted.");
          return false;
        }

        String plateDisplayString = config.getPlateDisplayStringWithoutName(plate);
        sender.sendMessage(ChatColor.BLUE + plateDisplayString);
        return true;
    }


    String plateName = args[1];
    String plateDisplayString = config.getPlateDisplayString(plateName);
    if (plateDisplayString == null) {
      sender.sendMessage(ChatColor.RED + "No Command Plate found with name: " + plateDisplayString);
      return false;
    }

    sender.sendMessage(ChatColor.BLUE + plateDisplayString);
    return true;
  }
}
