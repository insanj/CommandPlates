package me.insanj.commandplates;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.Math;
import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandPlatesCommandExecutor implements CommandExecutor {

  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length <= 0) {
      return false;
    }

    if (!(sender instanceof Player) || !sender.isOp()) {
        sender.sendMessage(ChatColor.RED + "");
        return false;
    }

    Player player = (Player) sender;
    String argumentString = args[0];

    if (argumentString.equals(CommandPlatesConfig.COMMAND.CREATE)) {
      return onCommandCreate(player, args);
    } else if (argumentString.equals(CommandPlatesConfig.COMMAND.LIST)) {
      return onCommandList(player, args);
    } else if (argumentString.equals(CommandPlatesConfig.COMMAND.INFO)) {
      return onCommandInfo(player, args);
    }

    return false;
  }

  public boolean onCommandCreate(Player player, String[] args) {
    String createPermissionString = CommandPlatesConfig.PERMISSION.CREATE;
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

    ArrayList<String> commandList = new ArrayList<String>();
    String incompleteCommand = "";
    int i = -1;
    for (String arg : args) {
      i++;
      if (i <= 2) {
        continue;
      }

      String[] split = arg.split(",");
      if (split.length <= 1) {
        incompleteCommand += arg;
      } else {
        for (String splitCmd : split) {
          incompleteCommand += splitCmd;
          commandList.add(new String(incompleteCommand));
          incompleteCommand = "";
        }
      }
    }

    if (incompleteCommand.length() > 0) {
      commandList.add(incompleteCommand);
    }

    String author = player.getName();
    Location location = player.getLocation();
    config.setPlate(plateName, author, location, console, commandList);
    player.sendMessage(ChatColor.GREEN + "Command plate has been created!");
    return true;
  }

  public boolean onCommandList(Player player, String[] args) {
     Map<String, Map> plates = config.getPlates();
     for (String plateName : plates.keySet()) {
       Map<String, Map> plate = plates.get(plateName);
       String message = "";
       for (String plateAttributeName : plate.keySet()) {
         if (plateAttributeName.equals(CommandPlatesConfig.KEY.LOCATION)) {
           Map<String, Object> locationMap = plates
           String locationString = String.format("%s, %s, %s, %s" plates.get(CommandPlatesConfig.KEY.LOCATION_X), plates.get(CommandPlatesConfig.KEY.LOCATION_Y), plates.get(CommandPlatesConfig.KEY.LOCATION.Z), plates.get(CommandPlatesConfig.KEY.LOCATION_WORLD));
           message += String.format("%s: %s", plateAttributeName, locationString);
         } else if (plateAttributeName.equlas(CommandPlatesConfig.KEY.COMMANDS)) {
           String commandsString = "";
           List<String> commandsList = (List<String>)plate.get(plateAttributeName);
           String commandsString = "commands: ";
           Integer i = Integer.parseInt(1);
           for (String cmdString : commandsList) {
             commandsString += String.format("%s %s", i.toString(), cmdString);
             i++;
           }
           message += String.format("%s: %s", plateAttributeName, commandsString);
         } else {
           message += String.format("%s: %s", plateAttributeName, plates.get(plateAttributeName));
         }
       }

       String plateMessage = String.format("%s plate: %s", plateAttributeName, message);
       player.sendMessage(plateMessage);
     }

     return true;
  }

  public boolean onCommandInfo(Player player, String[] args) {
    String plateName = args[1];
    Map<String, Map> plate = (Map<String, Map>)config.getPlate(plateName);
    String message = "";
    for (String plateAttributeName : plate.keySet()) {
      if (plateAttributeName.equals(CommandPlatesConfig.KEY.LOCATION)) {
        Map<String, Object> locationMap = plates
        String locationString = String.format("%s, %s, %s, %s" plates.get(CommandPlatesConfig.KEY.LOCATION_X), plates.get(CommandPlatesConfig.KEY.LOCATION_Y), plates.get(CommandPlatesConfig.KEY.LOCATION.Z), plates.get(CommandPlatesConfig.KEY.LOCATION_WORLD));
        message += String.format("\n%s: %s", plateAttributeName, locationString);
      } else if (plateAttributeName.equlas(CommandPlatesConfig.KEY.COMMANDS)) {
        String commandsString = "";
        List<String> commandsList = (List<String>)plate.get(plateAttributeName);
        String commandsString = "commands: ";
        Integer i = Integer.parseInt(1);
        for (String cmdString : commandsList) {
          commandsString += String.format("%s %s", i.toString(), cmdString);
          i++;
        }
        message += String.format("\n%s: %s", plateAttributeName, commandsString);
      } else {
        message += String.format("\n%s: %s", plateAttributeName, plates.get(plateAttributeName));
      }
    }

    player.sendMessage(String.format("%s%s", plateName, message);
  }
}
