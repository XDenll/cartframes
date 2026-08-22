# CartFrames

CartFrames is a client-side Fabric mod for Minecraft 1.21.11 that tracks the
frame count between configured minecart PvP actions and displays the result in
the HUD.

## Features

- Tracks Crossbow Cart, Overload Cart, and Normal Insta Cart sequences.
- Displays the active frame count while a sequence is in progress.
- Keeps the completed result visible for 10 seconds.
- Supports configurable HUD positions, custom coordinates, background boxes,
  brackets, and plain text.
- Provides a Mod Menu configuration screen.

## Requirements

- Minecraft 1.21.11
- Fabric Loader 0.19.3 or newer
- Fabric API
- Cloth Config
- Mod Menu (optional, for the configuration screen)
- Java 21 or newer

## Installation

1. Install Fabric Loader and Fabric API for Minecraft 1.21.11.
2. Place the CartFrames jar in the instance's `mods` folder.
3. Launch the game. Open the configuration screen through Mod Menu, if
   installed.

## Building

On Windows:

```powershell
.\gradlew.bat clean build
```

On macOS or Linux:

```sh
./gradlew clean build
```

The built jar is written to `build/libs/`.

## License

CartFrames is released under the license included in [LICENSE](LICENSE).
