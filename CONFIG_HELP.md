# How to use the Config API


### Creation and Registering

1. Make a blank config class. Package it wherever and call the class wherever you want. I typically call it `Config`


2. Determine if the config is client side (rendering) or server side (logic). In some mods you might have two configs, one for each. In your mod initializer, or optionally a mixin if you don't have an initializer,
create a static `ConfigHandler` with a type parameter of your config class. The first constructor parameter is the default config, while the second parameter is the name of the config,
i.e. "flytre_lib" for the file to be named `flytre_lib.json5`. The 3rd parameter, which is optional, is a custom Gson serializer if you don't want to use the comprehensive default one.
Register the config in the `ConfigRegistry` class as a client or server config.



```java
//In Client Mod Initializer:
public static final ConfigHandler<FlytreLibConfig> HANDLER = new ConfigHandler<>(new FlytreLibConfig(), "flytre_lib");

@Override
public void onInitializeClient() {
    ConfigRegistry.registerClientConfig(HANDLER);
}
```

### Adding Config Options

To add an option to the config, simply make a field in the config class. It's recommended for the field to be public rather than using getters, as getters add a lot of extra boilerplate, 
and for the default value to be the initialized value to avoid long constructors. For example, for a primitive data type:
```java
public double zombieMoveSpeed = 1.5d
```

Using the default serializer, aside from primitive types, you can also serialize `String`s, `Identifier`s, `EntityType`s, `Fluid`s, `StatusEffect`s,
`Block`s, `Enchantment`s, `Item`s, `EntityAttribute`s, `SoundEvent`s, and `VillagerProfession`s directly. You can also serialize Sets of these types, which are
usually sorted when serialized, so they look nicer in the config:
```java
   public Set<Block> blocks = Set.of(
            Blocks.DIORITE,
            Blocks.ANCIENT_DEBRIS,
            Blocks.CARROTS,
            Blocks.PUMPKIN,
            Blocks.AIR
        );
```

```json
  "blocks": [
    "minecraft:air",
    "minecraft:ancient_debris",
    "minecraft:carrots",
    "minecraft:diorite",
    "minecraft:pumpkin"
  ]
```

However, this implementation only allows you to use vanilla / your mod's / dependency mods' entities, blocks, etc. in your config. It also doesn't work for values like biomes
that are controlled dyanmically by datapacks. To gain access to these things, you'll need to use the [References api](src/main/java/net/flytre/flytre_lib/config/reference). 

```java
    public Set<BiomeReference> biomes = Set.of(
            new BiomeReference("zeta", "alpha"),
            new BiomeReference("minecraft", "plains"),
            new BiomeReference("minecraft", "jungle"),
            new BiomeReference(BiomeKeys.BADLANDS),
            new BiomeReference("modded", "modded_biome"),
            new BiomeReference("modded", "another_modded_biome")
    );
```


```json
  "biomes": [
    "minecraft:badlands",
    "minecraft:jungle",
    "minecraft:plains",
    "modded:a_modded_biome",
    "modded:modded_biome",
    "zeta:alpha"
  ],
```

In order to access the value of a reference, you'll need a `World` (either client / server) as values can be datapack dependent (i.e. datapacks can add custom dimensions or biomes). Here's how to check if a value is in a references set:

```java
new BiomeReference(biomeObject, worldObject).isIn(setOfReferences);
// - or -
setOfReferences.contains(new BiomeReference(biomeObject, worldObject));
```


Tags (a feature of vanilla Minecraft) are groups of values that are commonly referenced, to make accessing them easier. If we want our config to support tags, i.e. `#minecraft:skeletons` or `#minecraft:carpets`, there are classes that extend `TagReference` that can reference entity tags,
item tags, etc. In order to have a mixed set of say, block tags and blocks, we need to do something like this:

```java
    @SerializedName("block_set")
    public Set<ConfigBlock> blockSet = Set.of(
            new BlockTagReference(BlockTags.CARPETS), // Block TAG
            new BlockTagReference(BlockTags.ANVIL), 
            new BlockReference(Blocks.DIAMOND_BLOCK), // BLOCK
            new BlockReference(Blocks.OBSIDIAN),
            new BlockReference(Blocks.EMERALD_BLOCK),
            new BlockReference(Blocks.NETHERRACK),
            new BlockTagReference(BlockTags.AXE_MINEABLE)
    );
```

Interfaces that start with Config in the Reference API are used for this purpose: `ConfigBlock`, `ConfigEntity`, `ConfigFluid`, and `ConfigItem`. These are the optimal interfaces to use for these data types:

```json
  "block_set": [
    "#minecraft:anvil",
    "#minecraft:carpets",
    "#minecraft:mineable/axe",
    "minecraft:diamond_block",
    "minecraft:emerald_block",
    "minecraft:netherrack",
    "minecraft:obsidian"
  ],
```


#####Summary:

For primitives, Strings, and Identifiers: Use the class directly

For blocks, entities, fluids, and items: If you want just a single value, use the Reference class. If you want a set of values, use the ConfigName
class so users can specify tag groups to make it much easier for them.

For other identifier based data types like sound events, attributes, and villager professions: Use the reference class


### Adding Additional Data to Config Options via Annotations

Enums will automatically have the list of possible values printed as a comment.
They are very useful for having a distinct number of preset options.

Json uses snake_case while Java variables use camelCase. Thus, you'll want to use the `@SerializedName` annotation, which determines 
   the name of the option in the json config: 
   ```java
   @SerializedName("zombie_move_speed)
   public double zombieMoveSpeed = 1.5d
   ```
   

Sometimes, config options aren't clear to the user. You can use the custom `@Description` annotation to add a comment 
to the field in the json file: 
```java
   @Description("How fast zombies should move")
   @SerializedName("zombie_move_speed)
   public double zombieMoveSpeed = 1.5d
```

Additionally, numerical values, like the chance of something happening, often have a range. You can specify a range using the `@Range` annotation:

```java
    @Range(min = 0.0f, max = 1.0f)
    @SerializedName("nether_star_drop_rate")
    public float netherStarDropRate = 0.2f;
```
This will give the user an error if the value is outside the range and cause the default config to be loaded.


### Custom Serialization & Config Events

Configs can implement the `ConfigEventAcceptor` interface to run code on config events, which is currently just `onReload`.
onReload runs whenever the config is loaded or re-loaded, and can be used to do additional config parsing! Very useful when combined with `transient` fields, which are not serialized.


Sometimes, you'll want to have custom serialization. In these cases, you should use the `GsonHelper.GSON_BUILDER` field to 
preserve all the default custom serialization and then add your own adapters to that. `GsonHelper` also has a custom map serializer class to
help you serialize maps with identifier based types as the keys (i.e. blocks can be converted to identifiers so its identifier-based).
Just remember to use `GsonBuilder::enableComplexMapKeySerialization()` when working with maps.





### Example Config:

```java
public class ServerConfig {

    @Range(min = 0, max = 25600)
    @Description("The farthest distance the biome locator will look to try and find the target biome")
    @SerializedName("max_biome_distance")
    public int maxBiomeDistance = 6400;


    @Description("Use the /advancement command for help with advancement ids")
    @SerializedName("required_advancement")
    public AdvancementReference requiredAdvancement = new AdvancementReference("minecraft", "none");

    @Range(min = 0, max = 1f)
    @Description("The chance of the biome locator appearing in loot chests")
    @SerializedName("chance_as_loot")
    public float chanceAsLoot = 0.16f;

    @Description("The list of biomes that are hidden from the biome locator")
    @SerializedName("blacklisted_biomes")
    public Set<BiomeReference> blacklistedBiomes = Set.of(
            new BiomeReference("appliedenergistics2", "spatial_storage"),
            new BiomeReference(BiomeKeys.THE_END)
    );
}
```

```json5
{
  //The farthest distance the biome locator will look to try and find the target biome [min: 0, max: 25.60k]
  "max_biome_distance": 6400,
  //Use the /advancement command for help with advancement ids
  "required_advancement": "minecraft:none",
  //The chance of the biome locator appearing in loot chests [min: 0, max: 1]
  "chance_as_loot": 0.16,
  //The list of biomes that are hidden from the biome locator
  "blacklisted_biomes": [
    "appliedenergistics2:spatial_storage",
    "minecraft:the_end"
  ]
}
```

### Modpack Developer Info:

-Client configs are reloaded when they are registered, or any time resources / resource packs are reloaded.
Additionally, use `F3+Y` to reload it whenever you want.

-Server configs are reloaded whenever server resources are reloaded (think when loot tables are reloaded) or with
the `/reloadconfig` command.

-Helpful error messages for configs will be shown both in the console AND in the config file itself as a comment to make
them easy to find!

Example of an error message in a config file:
```json5
//[18:39:48] ERROR: Value 1.2 for field percentDropRate is not in range [min: 0, max: 1]. Default config was loaded.
{
  "percentDropRate": 1.2
}
```

###### Copyright Flytre © 2021 - All Rights Reserved