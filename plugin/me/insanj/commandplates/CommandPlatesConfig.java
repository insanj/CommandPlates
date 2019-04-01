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
import org.bukkit.Material;
import org.bukkit.block.Block;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class CommandPlatesConfig extends CommandPlatesBaseConfig {
    private final CommandPlatesPlugin plugin;
    private Map<String, Map> plates;
    private Map<Location, Map> platesByLocation;
    private Map<Location, String> plateNamesByLocation;

    public CommandPlatesConfig(CommandPlatesPlugin plugin) {
        this.plugin = plugin;
        setup();
    }

    public void debugLog(String message) {
      if (isDebugLogEnabled()) {
        plugin.getLogger().info(message);
      }
    }

    private boolean isDebugLogEnabled() {
      return plugin.getConfig().getBoolean(KEY.DEBUG());
    }

    // basic config management
    private void setup() {
      plugin.saveDefaultConfig();
      reload();
    }

    public void reload() {
      plugin.reloadConfig();
      this.plates = readPlates();
      setupPlatesByLocation(this.plates);
    }

    private void save() {
      plugin.saveConfig();
    }

    // loading of values from config file
    private void setupPlatesByLocation(Map<String, Map> platesToUse) {
        Map<Location, Map> genPlatesByLocation = new HashMap();
        Map<Location, String> genPlateNamesByLocation = new HashMap();

        for (String plateName : platesToUse.keySet()) {
          Map<String, Object> plate = platesToUse.get(plateName);
          Location plateLocation = getLocationForPlate(plateName);
          genPlatesByLocation.put(plateLocation, plate);
          genPlateNamesByLocation.put(plateLocation, plateName);
        }

        this.platesByLocation = genPlatesByLocation;
        this.plateNamesByLocation = genPlateNamesByLocation;
    }

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
              ConfigurationSection locationSection = (ConfigurationSection) plateAttributeValue;
              Map<String, Object> locationAttribute = (Map<String, Object>)  plugin.getConfig().getConfigurationSection(locationSection.getCurrentPath()).getValues(false);
              String worldName = (String)locationAttribute.get(KEY.LOCATION_WORLD());
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
     /* int threshold = 3; // blocks away from pressure plate
      for (String plateName : plates.keySet()) {
        Map<String, Object> plate = (Map<String, Object>) plates.get(plateName);
        Location plateLocation = getLocationForPlate(plateName);
        if (location.distance(plateLocation) <= threshold) {
          return plate;
        }
      }*/

      return platesByLocation.get(location);
    }

    public boolean getConsoleBoolFromPlate(Map plate) {
      return (boolean)plate.get(KEY.CONSOLE());
    }

    public List<String> getPlateCommandList(Map plate) {
      List<String> commandList = (List<String>)plate.get(KEY.COMMANDS());
      return commandList;
    }

    public Location getLocationForPlate(String plateName) {
      Map plate = getPlate(plateName);
      return (Location) plate.get(KEY.LOCATION()); // already parsed in readPlates()
    }

    public String getPlateDisplayString(String plateName) {
      Map<String, Object> plate = getPlate(plateName);
      if (plate == null) {
        return null;
      }
      String author = (String)plate.get(KEY.AUTHOR());
      Boolean console = getConsoleBoolFromPlate(plate);
      Location location = (Location)plate.get(KEY.LOCATION());
      List<String> commandList = getPlateCommandList(plate);

      String locationString = String.format("%s, %s, %s", Double.toString(location.getX()), Double.toString(location.getY()), Double.toString(location.getZ()));

      return String.format("\'%s\' by %s (%s) @ %s > %s", plateName, author, Boolean.toString(console), locationString, commandList.toString());
    }

    public String getPlateDisplayStringWithoutName(Map<String, Object> plate) {
      String author = (String)plate.get(KEY.AUTHOR());
      Boolean console = getConsoleBoolFromPlate(plate);
      Location location = (Location)plate.get(KEY.LOCATION());
      List<String> commandList = getPlateCommandList(plate);

      String locationString = String.format("%s, %s, %s", Double.toString(location.getX()), Double.toString(location.getY()), Double.toString(location.getZ()));

      return String.format("by %s (%s) @ %s > %s", author, Boolean.toString(console), locationString, commandList.toString());
    }

    public void setPlate(String plateName, String author, Location location, boolean console, List<String> commandList) {
        HashMap plate = new HashMap();
        plate.put(KEY.AUTHOR(), author);
        plate.put(KEY.CONSOLE(), console);
        plate.put(KEY.COMMANDS(), commandList);

        Location integerLocation = new Location(location.getWorld(), Math.floor(location.getX()), Math.floor(location.getY()), Math.floor(location.getZ()));

        HashMap plateLocation = new HashMap();
        plateLocation.put(KEY.LOCATION_WORLD(), integerLocation.getWorld().getName());
        plateLocation.put(KEY.LOCATION_X(), integerLocation.getX());
        plateLocation.put(KEY.LOCATION_Y(), integerLocation.getY());
        plateLocation.put(KEY.LOCATION_Z(), integerLocation.getZ());

        plate.put(KEY.LOCATION(), plateLocation);

        String platesConfigSectionPath = KEY.PLATES() + "." + plateName;
        plugin.getConfig().createSection(platesConfigSectionPath, plate);
        plugin.saveConfig();

        plate.put(KEY.LOCATION(), integerLocation); // reset location to normal

        plates.put(plateName, plate);
        setupPlatesByLocation(plates);
    }

    public boolean hasPermissionToRunPlate(Player player, String plateName, Map<String, Object> plate) {
      if (player.isOp() || player.hasPermission(PERMISSION.CREATE())) {
        return true;
      }

      if (player.hasPermission(PERMISSION.USE()) == true) {
        return true;
      }

      if (player.hasPermission(PERMISSION.PLATE(plateName))) {
        return true;
      }

      /*
      String author = (String)plate.get(KEY.AUTHOR());
      if (author.equals(player.getName())) {
        return true;
      }
      */

      return false;
    }

    public void removePlate(String plateName) {
      String platesConfigSectionPath = KEY.PLATES() + "." + plateName;
      plugin.getConfig().set(platesConfigSectionPath, null);
      plugin.saveConfig();

      plates.remove(plateName);
      setupPlatesByLocation(plates);
    }

    public boolean blockIsPressurePlate(Block block) {
      return block.getType() == Material.STONE_PLATE || block.getType() == Material.WOOD_PLATE || block.getType() == Material.GOLD_PLATE || block.getType() == Material.IRON_PLATE;
      // List<BlockType> pressurePlateTypes = Arrays.asList(Material.ACACIA_PRESSURE_PLATE, Material.BIRCH_PRESSURE_PLATE, Material.DARK_OAK_PRESSURE_PLATE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, Material.JUNGLE_PRESSURE_PLATE, Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.OAK_PRESSURE_PLATE, Material.SPRUCE_PRESSURE_PLATESTONE_PRESSURE_PLATE);
    }

    public String getNameForPlateAtLocation(Location location) {
      return this.plateNamesByLocation.get(location);
    }
}
