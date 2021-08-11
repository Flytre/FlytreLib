package net.flytre.flytre_lib.api.base.util;

import com.google.common.collect.Multimap;
import net.flytre.flytre_lib.mixin.base.AbstractBlockAccessor;
import net.flytre.flytre_lib.mixin.base.MiningToolItemAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.property.Properties;

import java.util.*;


/**
 * Get properties of items and block items
 */
public class ItemUtils {

    private ItemUtils() {
        throw new AssertionError();
    }

    public static final Random RANDOM = new Random();


    public static float getHardness(ItemStack stack) {
        assert stack.getItem() instanceof BlockItem;
        return ((BlockItem) stack.getItem()).getBlock().getHardness();
    }

    public static float getResistance(ItemStack stack) {
        assert stack.getItem() instanceof BlockItem;
        return ((AbstractBlockAccessor) ((BlockItem) stack.getItem()).getBlock()).getResistance();
    }

    /**
     * Whether the Block this BlockItem represents is flammable
     */
    public static boolean getFlammable(ItemStack stack) {
        assert stack.getItem() instanceof BlockItem;
        return ((AbstractBlockAccessor) ((BlockItem) stack.getItem()).getBlock()).getMaterial().isBurnable();
    }

    /**
     * Get the value (applied modifiers + player base value) of the given attribute of an ItemStack of an assumed slot (mainhand / armor slot)
     */
    public static double getAttributeValue(ItemStack stack, EntityAttribute attribute) {
        EquipmentSlot slot = stack.getItem() instanceof ArmorItem ? ((ArmorItem) stack.getItem()).getSlotType() : EquipmentSlot.MAINHAND;
        return getAttributeValue(stack, attribute, slot);
    }


    /**
     * Get the value (applied modifiers + player base value) of the given attribute of an ItemStack in the given slot
     */
    public static double getAttributeValue(ItemStack stack, EntityAttribute attribute, EquipmentSlot slot) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        assert player != null;
        return getAttributeValue(stack, attribute, slot, player);

    }


    /**
     * Get the value (applied modifiers + player base value) of the given attribute of an ItemStack in the given slot for the given player
     */
    public static double getAttributeValue(ItemStack stack, EntityAttribute attribute, EquipmentSlot slot, PlayerEntity player) {

        double d = player.getAttributeBaseValue(attribute);

        Multimap<EntityAttribute, EntityAttributeModifier> multimap = stack.getAttributeModifiers(slot);
        var map = genModifierOperationMap(multimap, attribute);


        for (EntityAttributeModifier modifier : map.get(EntityAttributeModifier.Operation.ADDITION))
            d += modifier.getValue();


        double e = d;

        for (EntityAttributeModifier modifier : map.get(EntityAttributeModifier.Operation.MULTIPLY_BASE))
            e += d * modifier.getValue();

        for (EntityAttributeModifier modifier : map.get(EntityAttributeModifier.Operation.MULTIPLY_TOTAL))
            e *= 1.0D + modifier.getValue();


        return attribute.clamp(e);

    }


    private static Map<EntityAttributeModifier.Operation, Collection<EntityAttributeModifier>> genModifierOperationMap(Multimap<EntityAttribute, EntityAttributeModifier> multimap, EntityAttribute attribute) {

        Map<EntityAttributeModifier.Operation, Collection<EntityAttributeModifier>> result = new HashMap<>();
        for (var op : EntityAttributeModifier.Operation.values())
            result.put(op, new HashSet<>());
        for (EntityAttributeModifier modifier : multimap.get(attribute))
            result.get(modifier.getOperation()).add(modifier);
        return result;
    }


    /**
     * Whether a given stack has one or more modifiers for the given attribute of an assumed slot (mainhand / armor slot)
     */
    public static boolean hasAttribute(ItemStack stack, EntityAttribute attribute) {
        EquipmentSlot slot = stack.getItem() instanceof ArmorItem ? ((ArmorItem) stack.getItem()).getSlotType() : EquipmentSlot.MAINHAND;
        return stack.getAttributeModifiers(slot).get(attribute).size() > 0;
    }

    public static int getHarvestLevel(ItemStack stack) {

        Item item = stack.getItem();

        return item instanceof ToolItem ? ((ToolItem) item).getMaterial().getMiningLevel() : -1;
    }


    public static double getHarvestSpeed(ItemStack stack) {
        BlockState state = getBlockForTool(stack);

        if (state == Blocks.AIR.getDefaultState())
            return -1;

        return stack.getItem().getMiningSpeedMultiplier(stack, state);
    }

    /**
     * Get an effective block for the given stack if it is a vanilla tool type
     */
    public static BlockState getBlockForTool(ItemStack stack) {
        if (stack.getItem() instanceof MiningToolItem)
            return ((MiningToolItemAccessor) stack.getItem()).getEffectiveBlocks().getRandom(RANDOM).getDefaultState();
        else if (stack.getItem() instanceof ShearsItem)
            return Blocks.COBWEB.getDefaultState();
        else
            return Blocks.AIR.getDefaultState();
    }

    /**
     * Get the luminance for the default BlockState / lit BlockState of an item, or an empty optional if its 0
     */
    public static Optional<Integer> getLuminance(Item item) {
        if (!(item instanceof BlockItem))
            return Optional.empty();
        Block block = ((BlockItem) item).getBlock();
        BlockState def = block.getDefaultState();
        if (def.getProperties().contains(Properties.LIT))
            def = def.with(Properties.LIT, true);
        return def.getLuminance() > 0 ? Optional.of(def.getLuminance()) : Optional.empty();
    }




}
