![Logo](media/logo.png)

# Nether Chest

[![GitHub Build Status](https://img.shields.io/github/actions/workflow/status/Kir-Antipov/nether-chest/build-artifacts.yml?style=flat&logo=github&cacheSeconds=3600)](https://github.com/Kir-Antipov/nether-chest/actions/workflows/build-artifacts.yml)
[![Version](https://img.shields.io/github/v/release/Kir-Antipov/nether-chest?sort=date&style=flat&label=version&cacheSeconds=3600)](https://github.com/Kir-Antipov/nether-chest/releases/latest)
[![Modrinth](https://img.shields.io/badge/dynamic/json?color=00AF5C&label=Modrinth&query=title&url=https://api.modrinth.com/v2/project/nether-chest&style=flat&cacheSeconds=3600&logo=modrinth)](https://modrinth.com/mod/nether-chest)
[![CurseForge](https://img.shields.io/badge/dynamic/json?color=F16436&label=CurseForge&query=title&url=https://api.cfwidget.com/494585&cacheSeconds=3600&logo=curseforge)](https://www.curseforge.com/minecraft/mc-mods/nether-chest-fabric)
[![License](https://img.shields.io/github/license/Kir-Antipov/nether-chest?style=flat&cacheSeconds=36000)](https://github.com/Kir-Antipov/nether-chest/blob/HEAD/LICENSE.md)

I like to think of the storage blocks in Minecraft as analogues to different types of data storage we encounter in everyday life:

 - An ordinary chest is like a hard drive: it's (basically) stationary, and you can access what it stores only through direct contact.
 - A shulker box is similar to a USB flash drive: it's portable, and the things it stores can also be accessed exclusively through direct contact.
 - An ender chest, on the other hand, is just like remote personal storage: upload your belongings to the "cloud," and you'll have access to them from anywhere in the world at any time.

Don't you think something's missing? There's personal cloud storage, but there's no shared one that could act as a kind of file sharing service. And isn't it strange that we have an overworld chest, an ender chest, but no nether chest? Well, behold!

![After all these years, finally, I have them all](media/finally.png)

This mod adds a single block to the game - the Nether Chest. It functions almost the same way as an ender chest, except it has a shared inventory for all players on a server.

### Crafting Recipe

![Crafting recipe: 8 nether bricks + 1 nether star](media/craft.png)

This crafting recipe may seem a bit expensive, but from a balancing perspective, everything is more than justified:

 - The Wither is a pretty weak enemy, even at the maximum difficulty level. Thus, obtaining a couple of extra nether stars shouldn't be too challenging.
 - The Nether Chest is an "endgame" block. If you can afford to spend a nether star on crafting a chest, then a bit of time-saving isn't considered cheating. However, in the early stages of the game, exchanging items between players who are separated by thousands of blocks might be overpowered.

**NOTE:** Nether Chests should be handled with Silk Touch if you don't want to lose your nether stars. :)

If you play on Peaceful *(or just aren't up for battling the Wither)*, you can use [this datapack](media/simplified_nether_chest_recipe_datapack.zip) *(which replaces the nether star with an eye of ender in the crafting recipe)*. Please refer to [this article](https://minecraft.wiki/w/Tutorials/Installing_a_data_pack) if you are unfamiliar with the process of installing a datapack.

### Multichannel Mode

![Multichannel Mode](media/multichannel.png)

When multichannel mode is enabled, each Nether Chest gains an extra slot that can be used to lock the chest to a specific channel with its own unique inventory.

### Redstone Integration

![Hoppers/Comparators actually work](media/redstone.png)

Nether Chests are compatible with hoppers and comparators.

### Config

If you have [Cloth Config](https://www.curseforge.com/minecraft/mc-mods/cloth-config) installed, you can customize the behavior of the mod. A config is usually located at `./config/nether_chest.json` and by default looks like this:

```json
{
  "allowHoppers": true,
  "allowInsertion": true,
  "allowExtraction": true,
  "enableMultichannelMode": true,
  "ignoreNbtInMultichannelMode": false,
  "ignoreCountInMultichannelMode": false,
  "channelBlacklist": [],
  "channelWhitelist": []
}
```

| Name | Description | Default value |
| ---- | ----------- | ------------- |
| `allowHoppers` | When enabled, hoppers will be able to access the Nether Chest's inventory | `true` |
| `allowInsertion` | When enabled, hoppers will be able to insert items into the Nether Chest's inventory | `true` |
| `allowExtraction` | When enabled, hoppers will be able to extract items from the Nether Chest's inventory | `true` |
| `enableMultichannelMode` | When enabled, Nether Chests can be locked to a specific channel | `true` |
| `ignoreNbtInMultichannelMode` | When enabled, channel keys no longer need to have identical NBT data to be considered equal | `false` |
| `ignoreCountInMultichannelMode` | When enabled, channel keys no longer need to have the same stack size to be considered equal | `false` |
| `channelBlacklist` | Items whose IDs are listed in this field are banned from being used as channel keys | `[]` |
| `channelWhitelist` | Only items whose IDs are listed in this field can be used as channel keys | `[]` |

You can edit any of these values directly in the config file or via [ModMenu](https://www.curseforge.com/minecraft/mc-mods/modmenu).

----

## Installation

Requirements:
 - Minecraft `1.20`
 - Fabric Loader `>=0.15.0`
 - Fabric API `>=0.83.0`

You can download the mod from:

 - [GitHub Releases](https://github.com/Kir-Antipov/nether-chest/releases/) <sup><sub>(recommended)</sub></sup>
 - [Modrinth](https://modrinth.com/mod/nether-chest)
 - [CurseForge](https://www.curseforge.com/minecraft/mc-mods/nether-chest-fabric)
 - [GitHub Actions](https://github.com/Kir-Antipov/nether-chest/actions/workflows/build-artifacts.yml) *(these builds may be unstable, but they represent the actual state of the development)*

## Building from sources

Requirements:
 - JDK `17`

### Linux/MacOS

```cmd
git clone https://github.com/Kir-Antipov/nether-chest.git
cd nether-chest

chmod +x ./gradlew
./gradlew build
cd build/libs
```
### Windows

```cmd
git clone https://github.com/Kir-Antipov/nether-chest.git
cd nether-chest

gradlew build
cd build/libs
```