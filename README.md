# Flytre Lib


Flytre Lib is a Library mod created and maintained by Flytre that provides common utils for their mods


### Features:

-Basic abstract REI implementations classes to make REI compat easier

-Transparent GUI options (cycling buttons, normal buttons, sliders) to make nicer looking GUIs. Additionally a simple toggle / multistate image button option to make quick and dirty cycling buttons based on images!

-Very simple render utils to make drawing sprites in 3D easier

-A wrench interface to make wrenches cross compatible between my mods

-Lots of inventory aid, such as `OutputSlot`, `EasyInventory`, and `FilterInventory` to make working with inventories and item filters easier

-Recipe Utils to make it easy to add recipes that take quantified ingredients and produce tagged outputs (essentially ore dictionary output on recipes)

-A ton of util classes to make raycasting, packet management, experience calculation, inventory management, item attribute manaagement, json-nbt conversion, and particle math easier

-Automatic eula agreement, server.properties online-mode off and op-granting in development environments

-Config API (below)

### Config API

-Ability to register client or server side configs. The former is reloaded any time resources are reloaded or with `F3 + Y`, while the latter uses `/reloadconfig`

-Config classes are simply any class you make with all the fields you want to store data as, then the config API will automatically turn it into a Json structured config for you to use

-Ability to add comments for each option, customize what name it uses in the Json structure, and set ranges for numerical fields

-An API for specifying blocks, entities, items, fluids, attributes, professions, and more in the config.

-Focus on both appearance while maintaining speed by heavy Set support

[Config](CONFIG_HELP.md)

###### Copyright Flytre © 2021 - All Rights Reserved