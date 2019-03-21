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

class CommandPlatesCommandExecutor implements CommandExecutor {

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
    return true;
  }
}
