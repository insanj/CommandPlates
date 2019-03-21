package me.insanj.commandplates;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class CommandPlatesConfig extends CommandPlatesBaseConfig {
    private final CommandPlatesPlugin plugin;
    private Map<String, Map> plates;

    public CommandPlatesConfig(CommandPlatesPlugin plugin) {
        this.plugin = plugin;
        setup();
    }

    // basic config management
    private void setup() {
      plugin.saveDefaultConfig();
      reload();
      this.plates = readPlates();
    }

    public void reload() {
      plugin.reloadConfig();
      this.plates = readPlates();
    }

    private void save() {
      plugin.saveConfig();
    }

    // loading of values from config file
    private Map<String, Map> readPlates() {
      final String platesSectionKey = KEY.PLATES();
      ConfigurationSection platesConfigSection = plugin.getConfig().getConfigurationSection(platesSectionKey);
      if (platesConfigSection == null) {
        return new HashMap();
      }

      HashMap parsedPlates = new HashMap();
      Map<String, Object> unparsedPlates = (Map<String, Object>)platesConfigSection.getValues(false);
      for (String plateName : unparsedPlates.keySet()) {
          ConfigurationSection plateSection = (ConfigurationSection) unparsedPlates.get(plateName);
          String plateSectionPath = plateSection.getCurrentPath();

          Map<String, Object> unparsedPlate = (Map<String, Object>) plugin.getConfig().getConfigurationSection(plateSectionPath).getValues(false);
          HashMap parsedPlate = new HashMap();
          for (String plateAttributeName : unparsedPlate.keySet()) {
            Object plateAttributeValue = unparsedPlate.get(plateAttributeName);

            if (plateAttributeName.equals(KEY.LOCATION())) {
              Map<String, Object> locationAttribute = (Map<String, Object>) plateAttributeValue;
              String worldName = (String)locationAttribute.get(KEY.LOCATION_WORLD()); // might need to getConfigSection() again here
              Object xObj = locationAttribute.get(KEY.LOCATION_X());
              Object yObj = locationAttribute.get(KEY.LOCATION_Y());
              Object zObj = locationAttribute.get(KEY.LOCATION_Z());
              Double x, y, z;
              if (xObj instanceof Integer) {
                  x = new Double((Integer)xObj);
              } else {
                  x = (Double)xObj;
              }

              if (yObj instanceof Integer) {
                  y = new Double((Integer)yObj);
              } else {
                  y = (Double)yObj;
              }

              if (zObj instanceof Integer) {
                  z = new Double((Integer)zObj);
              } else {
                  z = (Double)zObj;
              }

              World world = plugin.getServer().getWorld(worldName);
              Location locationFromData = new Location(world, (double)x, (double)y, (double)z);
              parsedPlate.put(plateAttributeName, locationFromData);
            } else {
              parsedPlate.put(plateAttributeName, plateAttributeValue);
            }
          }

          parsedPlates.put(plateName, parsedPlate);
      }

      return parsedPlates;
    }

    // public getters
    public Map<String, Map> getPlates() {
      return plates;
    }

    public Map getPlate(String plateName) {
      return plates.get(plateName);
    }

    public Map getActivatedPlate(Location location) {
      return new HashMap();
    }

    public List<String> getActivatedPlateCommandList(Map plate) {
      return new ArrayList<String>();
    }

    public void setPlate(String plateName, String author, Location location, boolean console, List<String> commandList) {
        HashMap plate = new HashMap();
        plate.put(KEY.AUTHOR(), author);
        plate.put(KEY.CONSOLE(), console);
        plate.put(KEY.COMMANDS(), commandList);

        HashMap plateLocation = new HashMap();
        plateLocation.put(KEY.LOCATION_WORLD(), location.getWorld().getName());
        plateLocation.put(KEY.LOCATION_X(), location.getX());
        plateLocation.put(KEY.LOCATION_Y(), location.getY());
        plateLocation.put(KEY.LOCATION_Z(), location.getZ());

        plate.put(KEY.LOCATION(), plateLocation);

        String platesConfigSectionPath = KEY.PLATES() + "." + plateName;
        plugin.getConfig().createSection(platesConfigSectionPath, plate);
        plugin.saveConfig();
        plates.put(plateName, plate);
    }

    public boolean hasPermissionToRunPlate(Player player, Map<String, Object> plate) {
      if (player.isOp() || player.hasPermission(PERMISSION.CREATE())) {
        return true;
      }

      String author = (String)plate.get(KEY.AUTHOR());
      if (author.equals(player.getName())) {
        return true;
      }

      return false;
    }
}
