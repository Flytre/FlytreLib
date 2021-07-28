package net.flytre.flytre_lib.config.client;

import com.google.gson.annotations.SerializedName;
import net.flytre.flytre_lib.config.Description;
import net.flytre.flytre_lib.config.reference.fluid.ConfigFluid;
import net.flytre.flytre_lib.config.reference.fluid.FluidReference;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO: DELETE
public class TestConfig {

    @Description("Edit this to tweak how and if each creeper tweaks")
    @SerializedName("spawn_data_map")
    public Map<EntityType<?>, CreeperConfig> spawnDataMap;


    public Map<String, ConfigFluid> testMap = new HashMap<>();

    public List<CreeperConfig> creeperConfigList;

    public TestConfig() {
        spawnDataMap = new HashMap<>();
        spawnDataMap.put(EntityType.DONKEY, new CreeperConfig(true, 15, 2, 2));
        spawnDataMap.put(EntityType.BLAZE, new CreeperConfig(true, 15, 2, 2));
        spawnDataMap.put(EntityType.PIG, new CreeperConfig(true, 10, 1, 2));

        testMap.put("ALPHA", new FluidReference(Fluids.WATER));
        testMap.put("BETA", new FluidReference(Fluids.LAVA));
        testMap.put("OMEGA", new FluidReference(Fluids.FLOWING_LAVA));

        creeperConfigList = new ArrayList<>();
        creeperConfigList.add(new CreeperConfig(true, 15, 2, 2));
    }

    public static class CreeperConfig {

        @SerializedName("spawns_naturally")
        public boolean shouldSpawnNaturally;

        @SerializedName("spawn_weight")
        public int spawnWeight;

        @SerializedName("min_spawn_group_size")
        public int minGroupSize;

        @SerializedName("max_spawn_group_size")
        public int maxGroupSize;

        public CreeperConfig(boolean shouldSpawnNaturally, int spawnWeight, int minGroupSize, int maxGroupSize) {
            this.shouldSpawnNaturally = shouldSpawnNaturally;
            this.spawnWeight = spawnWeight;
            this.minGroupSize = minGroupSize;
            this.maxGroupSize = maxGroupSize;
        }
    }
}
