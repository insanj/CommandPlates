package me.insanj.commandplates;

/*
  Constants and helper functions that do no actual work.
*/
class CommandPlatesBaseConfig {
  class KeyStrings {
    public String PLATES() { return "plates"; };
    public String AUTHOR() { return "author"; }
    public String LOCATION() { return "location"; }
    public String LOCATION_WORLD() { return "world"; }
    public String LOCATION_X() { return "x"; }
    public String LOCATION_Y() { return "y"; }
    public String LOCATION_Z() { return "z"; }
    public String CONSOLE() { return "console"; }
    public String COMMANDS() { return "commands"; }
  }

  class CommandStrings {
    public String CREATE() { return "create"; }
    public String LIST() { return "list"; }
    public String INFO() { return "info"; }
  }

  class PermissionStrings {
    public String PREFIX() { return "pplates"; }
    public String CREATE() { return PREFIX() + "admin"; }
    public String USE() { return PREFIX() + ".use"; }
    public String PLAYER(String playerName) { return PREFIX() + "." + playerName; }
  }

  public final KeyStrings KEY = new KeyStrings();
  public final CommandStrings COMMAND = new CommandStrings();
  public final PermissionStrings PERMISSION = new PermissionStrings();
}
