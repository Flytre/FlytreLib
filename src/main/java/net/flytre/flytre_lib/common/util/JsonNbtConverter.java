package net.flytre.flytre_lib.common.util;

import com.google.gson.*;
import com.google.gson.internal.LazilyParsedNumber;
import net.minecraft.nbt.*;

import java.util.Map;

public class JsonNbtConverter {

    public static NbtElement toNbt(JsonElement json) {

        // JSON Primitive
        if (json instanceof JsonPrimitive) {
            JsonPrimitive primitive = (JsonPrimitive) json;

            if (primitive.isBoolean()) {
                return NbtByte.of(primitive.getAsBoolean());


            } else if (primitive.isNumber()) {
                Number number = primitive.getAsNumber();

                if (number instanceof Byte) {
                    return NbtByte.of(number.byteValue());
                } else if (number instanceof Short) {
                    return NbtShort.of(number.shortValue());
                } else if (number instanceof Integer) {
                    return NbtInt.of(number.intValue());
                } else if (number instanceof Long) {
                    return NbtLong.of(number.longValue());
                } else if (number instanceof Float) {
                    return NbtFloat.of(number.floatValue());
                } else if (number instanceof Double) {
                    return NbtDouble.of(number.doubleValue());
                } else if (number instanceof LazilyParsedNumber) {
                    double val = number.doubleValue();
                    if (Math.abs(val - Math.floor(val)) < 0.001)
                        return NbtInt.of((int) Math.round(val));
                    return NbtDouble.of(val);
                }

            } else if (primitive.isString()) {
                return NbtString.of(primitive.getAsString());
            }

            // JSON Array
        } else if (json instanceof JsonArray) {
            JsonArray jsonArray = (JsonArray) json;
            NbtList list = new NbtList();

            for (JsonElement element : jsonArray) {
                list.add(toNbt(element));
            }

            return list;

            // JSON Object
        } else if (json instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject) json;
            NbtCompound compound = new NbtCompound();

            for (Map.Entry<String, JsonElement> jsonEntry : jsonObject.entrySet()) {
                compound.put(jsonEntry.getKey(), toNbt(jsonEntry.getValue()));
            }

            return compound;

            // Null - Not fully supported
        } else if (json instanceof JsonNull) {
            throw new AssertionError("Null elements not supported.");
        }

        // Something has gone wrong, throw an error.
        throw new AssertionError("JSON to NBT f*cked up.");
    }

    public static JsonElement toJson(NbtElement nbtElement) {
        return toJson(nbtElement, ConversionMode.RAW);
    }


    public static JsonElement toJson(NbtElement element, ConversionMode mode) {

        // Numbers
        if (element instanceof AbstractNbtNumber) {
            AbstractNbtNumber nbtNumber = (AbstractNbtNumber) element;

            switch (mode) {
                case JSON: {
                    if (nbtNumber instanceof NbtByte) {
                        NbtByte nbtByte = (NbtByte) nbtNumber;
                        byte value = nbtByte.byteValue();
                        switch (value) {
                            case 0:
                                return new JsonPrimitive(false);
                            case 1:
                                return new JsonPrimitive(true);
                            default:
                        }
                    }
                }

                case RAW: {
                    return new JsonPrimitive(nbtNumber.numberValue());
                }
            }

        } else if (element instanceof NbtString) {
            return new JsonPrimitive(element.asString());

        } else if (element instanceof NbtList) {
            NbtList list = (NbtList) element;
            JsonArray jsonArray = new JsonArray();

            for (NbtElement tag : list) {
                jsonArray.add(toJson(tag, mode));
            }

            return jsonArray;

            // Compound tag
        } else if (element instanceof NbtCompound) {
            NbtCompound compound = (NbtCompound) element;
            JsonObject jsonObject = new JsonObject();

            for (String key : compound.getKeys()) {
                jsonObject.add(key, toJson(compound.get(key), mode));
            }

            return jsonObject;

            // Nbt termination tag. Should not be encountered.
        } else if (element instanceof NbtNull) {
            throw new AssertionError("Should not encounter end tag");
        }

        // Impossible unless a new NBT class is made.
        throw new UnsupportedOperationException("Unsupported nbt tag type");
    }

    public enum ConversionMode {
        RAW,
        JSON
    }
}
