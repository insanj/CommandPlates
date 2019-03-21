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
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.FluidCollisionMode;
import org.apache.commons.lang.exception.ExceptionUtils;

public class CommandPlatesPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

      getLogger().info("I'm alive!");

    //  Bukkit.getPluginManager().registerEvents(listener, this);

      CommandPlatesCommandExecutor executor = new CommandPlatesCommandExecutor();
      getCommand("pplates").setExecutor(executor);
    }

    public void logError(Throwable e) {
      getLogger().info(ExceptionUtils.getStackTrace(e));
    }
}
