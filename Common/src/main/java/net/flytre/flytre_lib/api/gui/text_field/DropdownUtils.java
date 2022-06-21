package net.flytre.flytre_lib.api.gui.text_field;

import com.mojang.blaze3d.systems.RenderSystem;
import net.flytre.flytre_lib.api.base.util.FakeWorld;
import net.flytre.flytre_lib.api.base.util.RenderUtils;
import net.flytre.flytre_lib.impl.base.RenderUtilsImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Used to create standard dropdown menus easily.
 */
public final class DropdownUtils {


    public static final Pattern IDENTIFIER_REGEX = Pattern.compile("^([a-z0-9_.-]+:)?[a-z0-9_.-/]+$", Pattern.CASE_INSENSITIVE);

    private DropdownUtils() {
    }


    public static DropdownMenu createItemDropdown() {
        return createItemDropdown(0, 0);
    }

    public static DropdownMenu createItemDropdown(int xLoc, int yLoc) {
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        DropdownMenu searchField = new DropdownMenu(xLoc, yLoc, Math.min(250, width), 20, Text.translatable("null"), Registry.ITEM.getIds().stream().map(Identifier::toString).sorted().collect(Collectors.toList()));
        searchField.setRenderer(identifierRenderer(Registry.ITEM, i -> Registry.ITEM.get(i).getDefaultStack()));
        searchField.setMatcher(DropdownUtils::identifierMatch);
        searchField.setTextXOffset(25);
        searchField.setOptionRenderer(DropdownUtils::registryRenderer);
        searchField.setEntryWidth(200);
        return searchField;
    }

    public static DropdownMenu createBlockDropdown() {
        return createBlockDropdown(0, 0);
    }

    public static DropdownMenu createBlockDropdown(int xLoc, int yLoc) {
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        DropdownMenu searchField = new DropdownMenu(xLoc, yLoc, Math.min(250, width), 20, Text.translatable("null"), Registry.BLOCK.getIds().stream().map(Identifier::toString).sorted().collect(Collectors.toList()));
        searchField.setRenderer(identifierRenderer(Registry.BLOCK, i -> Registry.BLOCK.get(i).asItem().getDefaultStack()));
        searchField.setMatcher(DropdownUtils::identifierMatch);
        searchField.setTextXOffset(25);
        searchField.setOptionRenderer(DropdownUtils::registryRenderer);
        searchField.setEntryWidth(200);
        return searchField;
    }

    public static DropdownMenu createGenericDropdown(List<String> entries) {
        return createGenericDropdown(0, 0, entries);
    }

    public static DropdownMenu createGenericDropdown(int xLoc, int yLoc, List<String> entries) {
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        DropdownMenu searchField = new DropdownMenu(xLoc, yLoc, Math.min(250, width), 20, Text.translatable("null"), entries);
        searchField.setEntryWidth(200);
        return searchField;
    }


    public static DropdownMenu createEntityDropdown() {
        return createEntityDropdown(0, 0);
    }

    public static DropdownMenu createEntityDropdown(int xLoc, int yLoc) {
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        final Map<EntityType<?>, Entity> entityMap = new HashMap<>();
        for (Identifier id : Registry.ENTITY_TYPE.getIds()) {
            EntityType<?> type = Registry.ENTITY_TYPE.get(id);
            entityMap.put(type, type.create(FakeWorld.getInstance()));
        }

        DropdownMenu searchField = new DropdownMenu(xLoc, yLoc, Math.min(250, width), 20, Text.translatable("null"), Registry.ENTITY_TYPE.getIds().stream().map(Identifier::toString).sorted().collect(Collectors.toList()));
        searchField.setRenderer(identifierRenderer(Registry.ENTITY_TYPE, (textRenderer, matrices, text, fullText, x, y, color, cursor) -> {
            boolean valid = identifierPredicate(fullText, Registry.ENTITY_TYPE);
            if (valid) {
                EntityType<?> type = Registry.ENTITY_TYPE.get(Identifier.tryParse(fullText));
                Entity entity = entityMap.get(type);
                try {
                    if (entityMap.containsKey(type)) {
                        RenderUtils.renderSpinningEntity((int) x - 15, (int) y + 10, 8, 0, 0, entity);
                    } else {
                        drawQuestionMark(matrices, (int) x - 25, (int) y - 4);
                    }
                } catch (Exception e) {
                    entityMap.remove(type);
                    drawQuestionMark(matrices, (int) x - 25, (int) y - 4);
                }
            } else {
                drawQuestionMark(matrices, (int) x - 25, (int) y - 4);
            }
            return 0;
        }));
        searchField.setMatcher(DropdownUtils::identifierMatch);
        searchField.setTextXOffset(25);
        searchField.setOptionRenderer(DropdownUtils::registryRenderer);
        searchField.setEntryWidth(200);
        return searchField;
    }

    public static DropdownMenu createFluidDropdown() {
        return createFluidDropdown(0, 0);
    }

    public static DropdownMenu createFluidDropdown(int xLoc, int yLoc) {
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        DropdownMenu searchField = new DropdownMenu(xLoc, yLoc, Math.min(250, width), 20, Text.translatable("null"), Registry.FLUID.getIds().stream().map(Identifier::toString).sorted().collect(Collectors.toList()));

        if (RenderUtilsImpl.isFluidRenderingSupported()) {
            searchField.setRenderer(identifierRenderer(Registry.FLUID, (textRenderer, matrices, text, fullText, x, y, color, cursor) -> {
                boolean valid = identifierPredicate(fullText, Registry.FLUID);
                if (valid) {
                    Fluid fluid = Registry.FLUID.get(Identifier.tryParse(fullText));
                    RenderUtils.renderFluidInGui(matrices, fluid, 16, (int) (x - 25), (int) (y - 4), 16, 16);
                } else {
                    drawQuestionMark(matrices, (int) x - 25, (int) y - 4);
                }
                return 0;
            }));
            searchField.setTextXOffset(25);
        }
        searchField.setMatcher(DropdownUtils::identifierMatch);
        searchField.setOptionRenderer(DropdownUtils::registryRenderer);
        searchField.setEntryWidth(200);
        return searchField;
    }

    public static void drawQuestionMark(MatrixStack matrices, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, new Identifier("flytre_lib:textures/gui/config/question_mark.png"));
        DrawableHelper.drawTexture(matrices, x, y, 0, 0, 16, 16, 16, 16);
    }

    public static boolean identifierMatch(String option, String text) {
        return text.length() > 1 && option.contains(text) && !(option.equals(text));
    }

    public static String defaultRenderer(String option, String text, TextRenderer textRenderer, TranslucentTextField.TextFieldRenderer textFieldRenderer, int entryWidth, int entryHeight, int textXOffset) {
        int xScrollInner = text.length();
        int len = textRenderer.trimToWidth(option, entryWidth - textXOffset).length();
        String str;
        if (len < option.length())
            str = textRenderer.trimToWidth("-" + option.substring(xScrollInner), entryWidth - textXOffset);
        else
            str = textRenderer.trimToWidth(option, entryWidth - textXOffset);
        return str;
    }

    public static String registryRenderer(String option, String text, TextRenderer textRenderer, TranslucentTextField.TextFieldRenderer textFieldRenderer, int entryWidth, int entryHeight, int textXOffset) {
        if (option.startsWith("minecraft:") && !text.startsWith("minecraft:"))
            option = option.replace("minecraft:", "");

        return defaultRenderer(option, text, textRenderer, textFieldRenderer, entryWidth, entryHeight, textXOffset);
    }

    public static boolean identifierPredicate(String str, Registry<?> registry) {
        try {
            Identifier id = str.startsWith("#") ? Identifier.tryParse(str.substring(1)) : Identifier.tryParse(str);
            return registry.containsId(id);
        } catch (InvalidIdentifierException e) {
            return false;
        }
    }

    public static TranslucentTextField.TextFieldRenderer identifierRenderer(Registry<?> registry, TranslucentTextField.TextFieldRenderer customRenderer) {
        return (textRenderer, matrices, text, fullText, x, y, color, cursor) -> {
            boolean valid = identifierPredicate(fullText, registry);
            boolean tag = fullText.startsWith("#") && IDENTIFIER_REGEX.matcher(fullText.substring(1)).find();
            OrderedText ordered;
            if (tag) {
                ordered = OrderedText.styledForwardsVisitedString(text, Style.EMPTY.withColor(TextColor.fromRgb(0xFF4287f5)));
            } else if (valid) {
                ordered = OrderedText.styledForwardsVisitedString(text, Style.EMPTY.withColor(TextColor.fromRgb(color)));
            } else {
                ordered = OrderedText.styledForwardsVisitedString(text, Style.EMPTY.withColor(Formatting.RED));
            }
            if (!cursor)
                customRenderer.render(textRenderer, matrices, text, fullText, x, y, color, false);
            return textRenderer.drawWithShadow(matrices, ordered, x, y, color);

        };
    }

    public static int identifierTextFieldRenderer(TextRenderer renderer, MatrixStack matrices, String text, String fullText, float x, float y, int color, boolean cursor) {

        boolean valid = IDENTIFIER_REGEX.matcher(fullText).find();
        return renderer.drawWithShadow(matrices, OrderedText.styledForwardsVisitedString(text, valid ? Style.EMPTY : Style.EMPTY.withColor(Formatting.RED)), x, y, color);
    }

    public static TranslucentTextField.TextFieldRenderer identifierRenderer(Registry<?> registry, Function<Identifier, ItemStack> icon) {

        TranslucentTextField.TextFieldRenderer customRenderer = (textRenderer, matrices, text, fullText, x, y, color, cursor) -> {
            boolean valid = identifierPredicate(fullText, registry);
            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            if (valid) {
                itemRenderer.renderGuiItemIcon(icon.apply(Identifier.tryParse(fullText)), (int) x - 25, (int) y - 4);
            } else {
                drawQuestionMark(matrices, (int) x - 25, (int) y - 4);
            }
            return 0;
        };

        return identifierRenderer(registry, customRenderer);
    }

}
