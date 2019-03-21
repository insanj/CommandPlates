# CommandPlates

🏹  bind commands to pressure plates (minecraft 1.12.2)

## Request

Minecraft version: 1.12.2

Suggested name: PressurePlates

What I want: I like to request a plugin to allow players to bind command or commands to pressure plates that will either execute the command(s) by player or console when the pressure plate is stepped on.

Ideas for command/permission:

pressureplate.admin

- /pplates create platename <true:false> <command_one, command_two> - Gives a pressure plate in which you can place down to execute the command. If set to true, the command will be execute by console. If set to false, it will be execute by the player. Use %player% in the command to replace the players name in commands

- /pplates list - Shows all available pressure plates

- /pplates info - While looking at a pressure plate, it will give you information about that plate

- /pplates info platename - Just another way to look up plate info directly by command


/pplates info
platename:
console: true/false
- command_one
- command_two

/pplates list
List of available pressure plates.
- platename
- platename2
- platename3

pressureplate.use
This is default player permission needed to use any of the pressure plates.

pressureplate.platename
Players would also need pressureplate.platename permissions to execute certain pressure plate

config.yml or pressureplates.yml to store all data

Thank you for reading.

## Acknowledgements

- https://bukkit.org/threads/cant-detect-if-player-stands-on-pressure-plate.190533/

## Authors

(c) 2019 Julian Weiss. <a href="https://github.com/insanj">github.com/insanj</a>

## License

```
MIT License

Copyright (c) 2019 Julian Weiss

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
