package net.flytre.flytre_lib.common.inventory.filter;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.flytre.flytre_lib.client.gui.CoordinateProvider;
import net.flytre.flytre_lib.client.gui.ToggleButton;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class FilteredScreen<T extends ScreenHandler> extends HandledScreen<T> implements CoordinateProvider {

    protected static final Identifier MODE_BUTTON = new Identifier("flytre_lib:textures/gui/button/check_ex.png");
    protected static final Identifier MOD_BUTTON = new Identifier("flytre_lib:textures/gui/button/mod.png");
    protected static final Identifier NBT_BUTTON = new Identifier("flytre_lib:textures/gui/button/nbt.png");


    public FilteredScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }


    /**
     * @param button  The button to toggle
     * @param channel The packet channel to use
     * @param pos     If this is for a block entity, the pos to pass with the data. If not pass null.
     */
    protected void sendPacket(ToggleButton button, Identifier channel, @Nullable BlockPos pos) {
        button.toggleState();
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        if (pos != null)
            passedData.writeBlockPos(pos);
        passedData.writeInt(button.getState());
        ClientPlayNetworking.send(channel, passedData);
    }


    protected void addButton(int startFrame, int index, Identifier texture, Identifier channel, Supplier<BlockPos> posGetter, Text tooltip1, Text tooltip2) {
        ToggleButton button = new ToggleButton(this.x + 177, this.height / 2 - 80 + 20 * index, 16, 16, startFrame, texture, (buttonWidget) -> {
            sendPacket((ToggleButton) buttonWidget, channel, posGetter.get());

        }, "");
        button.setTooltips(tooltip1, tooltip2);
        button.setTooltipRenderer(this::renderTooltip);

        this.addButton(button);
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
