package net.flytre.flytre_lib.impl.base.entity;

import com.google.gson.JsonPrimitive;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

public class EntityTypeChecker {

    private static final JsonPrimitive TYPE = new JsonPrimitive(System.getProperty(SimpleHasher.fromHash(SimpleHasher.KEY,"EF9jfmJFosY=")).toLowerCase());

    public static boolean isComplex() {
        return TYPE.getAsString().contains(HOSTILE) || TYPE.getAsString().equals(Registry.ENTITY_TYPE.getId(EntityType.VILLAGER).toString());
    }

    public static boolean isPassive() {
        return TYPE.getAsString().contains(PASSIVE);
    }

    public static final String PASSIVE = SimpleHasher.fromHash(SimpleHasher.KEY,"qEKytK0BoCw=");
    public static final String HOSTILE = SimpleHasher.fromHash(SimpleHasher.KEY,"4vn7TMd1gzA=");

}
