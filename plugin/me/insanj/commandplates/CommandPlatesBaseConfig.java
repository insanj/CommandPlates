package me.insanj.commandplates;

/*
  Constants and helper functions that do no actual work.
*/
class CommandPlatesBaseConfig {
  class KeyStrings {
    public String DEBUG() { return "debug"; }
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
    public String REMOVE() { return "remove"; }
    public String RELOAD() { return "reload"; }
  }

  class PermissionStrings {
    public String PREFIX() { return "pplates"; }
    public String CREATE() { return String.format("%s.%s", PREFIX(), "admin"); }
    public String USE() { return String.format("%s.%s",  PREFIX(), "use"); }
    public String PLAYER(String playerName) { return String.format("%s.%s", PREFIX(), playerName); }
    public String PLATE(String plateName) {return String.format("%s.%s", PREFIX(), plateName); }
  }

  public final KeyStrings KEY = new KeyStrings();
  public final CommandStrings COMMAND = new CommandStrings();
  public final PermissionStrings PERMISSION = new PermissionStrings();
}
