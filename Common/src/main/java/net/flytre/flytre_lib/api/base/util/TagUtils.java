package net.flytre.flytre_lib.api.base.util;

import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * More just to remember how to get the values of a tag than anything
 */
public class TagUtils {
    public static <T> List<T> getValues(Identifier id, TagGroup<T> group) {
        return getTag(id, group).values();
    }


    public static <T> Tag<T> getTag(Identifier id, TagGroup<T> group) {
        return group.getTagOrEmpty(id);
    }


    private TagUtils() {
        throw new AssertionError();
    }
}