package net.flytre.flytre_lib.api.storage.inventory.filter;

import net.flytre.flytre_lib.api.gui.CoordinateProvider;
import net.flytre.flytre_lib.api.gui.button.ToggleButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface ScreenWithFilter extends CoordinateProvider {
    Identifier MODE_BUTTON = new Identifier("flytre_lib:textures/gui/button/check_ex.png");
    Identifier MOD_BUTTON = new Identifier("flytre_lib:textures/gui/button/mod.png");
    Identifier NBT_BUTTON = new Identifier("flytre_lib:textures/gui/button/nbt.png");

    default void sendPacket(ToggleButton button, BiFunction<BlockPos, Integer, ? extends Packet<ServerPlayPacketListener>> packetMaker, @Nullable BlockPos pos) {
        button.toggleState();
        Packet<ServerPlayPacketListener> packet = packetMaker.apply(pos, button.getState());
        Objects.requireNonNull(getClient().getNetworkHandler()).sendPacket(packet);
    }


    default void addButton(int startFrame, int index, Identifier texture, BiFunction<BlockPos, Integer, ? extends Packet<ServerPlayPacketListener>> packetMaker, Supplier<BlockPos> posGetter, Text tooltip1, Text tooltip2) {
        ToggleButton button = new ToggleButton(getX() + 177, getHeight() / 2 - 80 + 20 * index, 16, 16, startFrame, texture, (buttonWidget) -> {
            sendPacket((ToggleButton) buttonWidget, packetMaker, posGetter.get());

        }, "");
        button.setTooltips(tooltip1, tooltip2);
        button.setTooltipRenderer(this::renderTooltipShadow);

        this.addDrawableChildShadow(button);
    }

    MinecraftClient getClient();


    int getHeight();

    void renderTooltipShadow(MatrixStack matrices, List<Text> lines, int x, int y);

    <T extends Element & Drawable & Selectable> T addDrawableChildShadow(T drawableElement);
}
