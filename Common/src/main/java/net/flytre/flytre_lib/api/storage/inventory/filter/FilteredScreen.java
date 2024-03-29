package net.flytre.flytre_lib.api.storage.inventory.filter;

import net.flytre.flytre_lib.api.gui.CoordinateProvider;
import net.flytre.flytre_lib.api.gui.button.ToggleButton;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.20")
//Use ScreenWithFilter
public abstract class FilteredScreen<T extends ScreenHandler> extends HandledScreen<T> implements CoordinateProvider {

    protected static final Identifier MODE_BUTTON = new Identifier("flytre_lib:textures/gui/button/check_ex.png");
    protected static final Identifier MOD_BUTTON = new Identifier("flytre_lib:textures/gui/button/mod.png");
    protected static final Identifier NBT_BUTTON = new Identifier("flytre_lib:textures/gui/button/nbt.png");


    public FilteredScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }


    /**
     * @param button      The button to toggle
     * @param packetMaker The packet creator
     * @param pos         If this is for a block entity, the pos to pass with the data. If not pass null.
     */
    protected void sendPacket(ToggleButton button, BiFunction<BlockPos, Integer, ? extends Packet<ServerPlayPacketListener>> packetMaker, @Nullable BlockPos pos) {
        button.toggleState();
        Packet<ServerPlayPacketListener> packet = packetMaker.apply(pos, button.getState());
        assert client != null;
        Objects.requireNonNull(client.getNetworkHandler()).sendPacket(packet);
    }


    protected void addButton(int startFrame, int index, Identifier texture, BiFunction<BlockPos, Integer, ? extends Packet<ServerPlayPacketListener>> packetMaker, Supplier<BlockPos> posGetter, Text tooltip1, Text tooltip2) {
        ToggleButton button = new ToggleButton(this.x + 177, this.height / 2 - 80 + 20 * index, 16, 16, startFrame, texture, (buttonWidget) -> {
            sendPacket((ToggleButton) buttonWidget, packetMaker, posGetter.get());

        }, "");
        button.setTooltips(tooltip1, tooltip2);
        button.setTooltipRenderer(this::renderTooltip);

        this.addDrawableChild(button);
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}
