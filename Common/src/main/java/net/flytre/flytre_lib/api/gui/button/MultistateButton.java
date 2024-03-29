package net.flytre.flytre_lib.api.gui.button;


import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collections;

/**
 * A multi-state button is similar to a cycling button, but uses images rather than text
 * when its rendered.
 */
public class MultistateButton extends ButtonWidget {

    private final int textureWidth;
    private final int textureHeight;
    private final Identifier texture;
    private final String id;
    private final int states;
    private int state;
    private Text[] tooltips;
    private ButtonTooltipRenderer tooltipRenderer;
    private boolean renderTooltipWithButton;

    /**
     * Instantiates a new Multistate button.
     *
     * @param x       the x
     * @param y       the y
     * @param width   the width
     * @param height  the height
     * @param state   0-indexed current state, ie. 0-2 for a 3 state button
     * @param states  the total number of states, ie 3 for a 3 state button
     * @param texture the texture location
     * @param onPress what to do when the button is pressed
     * @param id      text to overlay on button
     */
    public MultistateButton(int x, int y, int width, int height, int state, int states, Identifier texture, PressAction onPress, char id) {
        this(x, y, width, height, state, states, texture, onPress, id + "");
    }

    /**
     * Instantiates a new Multistate button.
     *
     * @param x       the x
     * @param y       the y
     * @param width   the width
     * @param height  the height
     * @param state   0 or 1 for 1st / 2nd frame
     * @param texture the texture location
     * @param onPress what to do when the button is pressed
     * @param id      text to overlay on button
     */
    public MultistateButton(int x, int y, int width, int height, int state, int states, Identifier texture, PressAction onPress, String id) {
        super(x, y, width, height, Text.empty(), onPress);
        this.textureHeight = height * states * 2;
        this.textureWidth = width;
        this.texture = texture;
        this.state = state;
        this.id = id;
        this.states = states;
        this.renderTooltipWithButton = true;
    }


    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }


    public void setRenderTooltipWithButton(boolean renderTooltipWithButton) {
        this.renderTooltipWithButton = renderTooltipWithButton;
    }

    /**
     * Sets tooltips, make sure you set the renderer. See the ButtonTooltipRenderer for the easiest way
     * to do this
     *
     * @param frames A list of text to display as the tooltip for each frame
     */
    public void setTooltips(Text... frames) {
        this.tooltips = frames;
    }

    /**
     * Gets the tooltip renderer.
     *
     * @return the tooltip renderer
     */
    public ButtonTooltipRenderer getTooltipRenderer() {
        return tooltipRenderer;
    }

    /**
     * Sets the tooltip renderer. See ButtonTooltipRenderer's documentation.
     *
     * @param tooltipRenderer the tooltip renderer
     */
    public void setTooltipRenderer(ButtonTooltipRenderer tooltipRenderer) {
        this.tooltipRenderer = tooltipRenderer;
    }


    /**
     * Cycle through all states
     */
    public void cycleState() {
        this.state++;
        this.state %= states;
    }

    @Override
    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {

        if (tooltips == null)
            return;

        getTooltipRenderer().draw(matrices, Collections.singletonList(tooltips[getState()]), mouseX, mouseY);

    }

    /**
     * Gets the state (0 for frame 1, 1 for frame 2).
     *
     * @return the state
     */
    public int getState() {
        return state;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (MinecraftClient.getInstance() == null)
            return;

        MinecraftClient mc = MinecraftClient.getInstance();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

        this.hovered = isHovering(mouseX, mouseY);
        int y = this.height * state * 2;
        if (hovered)
            y += this.height;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        drawTexture(matrices, x, this.y, 0, y, width, height, textureWidth, textureHeight);

        TextRenderer renderer = mc.textRenderer;
        OrderedText orderedText = Text.of(id + "").asOrderedText();
        renderer.draw(matrices, orderedText, (float) (x + width / 2 - renderer.getWidth(orderedText) / 2) + 0.5f, (float) this.y + 4.5f, 0);

        if (isHovering(mouseX, mouseY) && renderTooltipWithButton) {
            int zOffset = getZOffset();
            setZOffset(zOffset + 100);
            renderTooltip(matrices, mouseX, mouseY);
            setZOffset(zOffset);
        }
    }

}
