package net.flytre.flytre_lib.compat.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.gui.widgets.Arrow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Deprecated
public final class ArrowWidget extends Arrow {
    @NotNull
    private final Rectangle bounds;
    private double animationDuration = -1;

    public ArrowWidget(@NotNull Rectangle bounds) {
        this.bounds = new Rectangle(Objects.requireNonNull(bounds));
    }

    @Override
    public double getAnimationDuration() {
        return animationDuration;
    }

    @Override
    public void setAnimationDuration(double animationDurationMS) {
        this.animationDuration = animationDurationMS;
        if (this.animationDuration <= 0)
            this.animationDuration = -1;
    }

    @NotNull
    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(REIRuntime.getInstance().getDefaultDisplayTexture());
        drawTexture(matrices, getX(), getY(), 82, 60, 24, 17);
        if (getAnimationDuration() > 0) {
            int width = MathHelper.ceil((System.currentTimeMillis() / (animationDuration / 24) % 24d));
            drawTexture(matrices, getX(), getY(), 82, 91, width, 17);
        }
    }

    public List<? extends Element> children() {
        return Collections.emptyList();
    }
}