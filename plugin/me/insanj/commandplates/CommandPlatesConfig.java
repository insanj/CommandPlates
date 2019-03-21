package me.insanj.commandplates;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bukkit.World;
import org.bukkit.Location;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;


/*


plates:
  Example:
    author: insanj
    location:
       - world: world
       - x: 0
       - y: 0
       - z: 0
    console: true
    commands:
      - say Hello
      - say World!


*/

public class CommandPlatesConfig {
    public class KEY {
      public static final String PLATES = "plates";
      public static final String AUTHOR = "author";
      public static final String LOCATION = "location";
      public static final String LOCATION_WORLD = "world";
      public static final String LOCATION_X = "x";
      public static final String LOCATION_Y = "y";
      public static final String LOCATION_Z = "z";
      public static final String CONSOLE = "console";
      public static final String COMMANDS = "commands";
    }

    class COMMAND {
      public static final String CREATE = "create";
      public static final String LIST = "list";
      public static final String INFO = "info";
    }

    class PERMISSION {
      public static final String PREFIX = "pplates";
      public static final String CREATE = PREFIX + ".admin";
      public static final String USE = PREFIX + ".use";
    }

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
      final String platesSectionKey = KEY.PLATES;
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
          for (String plateAttributeName : parsedPlate.keySet()) {
            Object plateAttributeValue = parsedPlate.get(plateAttributeName);
            if (plateAttributeName.equals(KEYS.LOCATION)) {
              String worldName = (String)plateAttributeValue.get(KEYS.LOCATION_WORLD); // might need to getConfigSection() again here
              Object xObj = plateAttributeValue.get(KEYS.LOCATION_X);
              Object yObj = plateAttributeValue.get(KEYS.LOCATION_Y);
              Object zObj = plateAttributeValue.get(KEYS.LOCATION_Z);
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

          parsedPlate.put(plateName, parsedPlate);
      }

      return parsedPlate;
    }

    // public getters
    public Map<String, Map> getPlates() {
      return plates;
    }

    public Map getPlate(String plateName) {
      return plates.get(plateName);
    }

    public Map getActivatedPlate(Location location) {

    }

    public List<String> getActivatedPlateCommandList(Map plate) {

    }

    public void setPlate(String plateName, String author, Location location, boolean console, List<String> commandList) {
        HashMap plate = new HashMap();
        plate.put(KEYS.AUTHOR, author);
        plate.put(KEYS.CONSOLE, console);
        plate.put(KEYS.COMMANDS, commandList);

        HashMap plateLocation = new HashMap();
        plateLocation.put(KEYS.LOCATION_WORLD, location.getWorld().getName());
        plateLocation.put(KEYS.LOCATION_X, location.getX());
        plateLocation.put(KEYS.LOCATION_Y, location.getY());
        plateLocation.put(KEYS.LOCATION_Z, location.getZ());

        plate.put(KEYS.LOCATION, plateLocation);

        String platesConfigSectionPath = KEYS.PLATES + "." + plateName;
        plugin.getConfig().createSection(platesConfigSectionPath, plate);
        plugin.saveConfig();
        plates.put(plateName, plate);
    }

    private boolean hasPermissionToRunPlate(Player player, Map<String, Object> plate) {
      if (player.isOp() || player.hasPermission(PERMISSION.CREATE)) {
        return true;
      }

      String author = (String)plate.get(KEY.AUTHOR);
      if (author.equals(player.getName())) {
        return true;
      }

      return false;
    }
}
