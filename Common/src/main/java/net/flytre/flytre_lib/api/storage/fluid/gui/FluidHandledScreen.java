package net.flytre.flytre_lib.api.storage.fluid.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.flytre.flytre_lib.api.base.util.RenderUtils;
import net.flytre.flytre_lib.api.storage.fluid.core.FluidStack;
import net.flytre.flytre_lib.impl.storage.fluid.network.FluidClickSlotC2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;


/**
 * Screens that draw fluids
 */
//FluidHandler::render handled by mixin to prevent messy code
public abstract class FluidHandledScreen<T extends ScreenHandler> extends HandledScreen<T> {

    private static final Identifier BCKG = new Identifier("flytre_lib:textures/gui/container/fluid_cell.png");
    private static final Identifier OVERLAY = new Identifier("flytre_lib:textures/gui/container/fluid_cell_overlay.png");
    private final FluidHandler fluidHandler;
    public FluidSlot focusedFluidSlot; //UNUSED

    public FluidHandledScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.fluidHandler = ((FluidHandler) handler);
    }


    public void drawFluidSlot(MatrixStack matrices, FluidSlot slot) {

        if (!slot.compact) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, BCKG);
            RenderSystem.enableDepthTest();
            drawTexture(matrices, slot.x, slot.y, 0, 0, 30, 60, 30, 60);

            if (slot.getStack().getAmount() > 0 && slot.getCapacity() != 0) {
                double percent = 1.0 - (double) slot.getStack().getAmount() / slot.getCapacity();

                percent = Math.min(0.95, percent);

                Fluid fluid = slot.getStack().getFluid();
                RenderUtils.renderFluidInGui(matrices, fluid, (int) Math.ceil(60 * (1 - percent) - 2), slot.x + 1, slot.y + 1, 30 - 2, 58);
            }

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, OVERLAY);
            drawTexture(matrices, slot.x, slot.y, 0, 0, 30, 60, 30, 60);
        } else {
            if (!slot.getStack().isEmpty()) {
                Fluid fluid = slot.getStack().getFluid();
                RenderUtils.renderFluidInGui(matrices, fluid, 16, slot.x, slot.y, 16, 16);
            }
        }
    }


    @Nullable
    private FluidSlot getFluidSlotAt(double xPosition, double yPosition) {
        for (int i = 0; i < this.fluidHandler.getFluidSlots().size(); ++i) {
            FluidSlot slot = this.fluidHandler.getFluidSlots().get(i);
            if (this.isPointOverFluidSlot(slot, xPosition, yPosition) && slot.doDrawHoveringEffect()) {
                return slot;
            }
        }

        return null;
    }

    public boolean isPointOverFluidSlot(FluidSlot slot, double pointX, double pointY) {
        return this.isPointWithinBounds(slot.x, slot.y, slot.compact ? 16 : 30, slot.compact ? 16 : 60, pointX, pointY);
    }


    @Override
    protected void drawMouseoverTooltip(MatrixStack matrices, int x, int y) {
        super.drawMouseoverTooltip(matrices, x, y);
        FluidSlot hover = getFluidSlotAt(x, y);
        if (hover != null) {
            renderTooltip(matrices, hover.getStack().toTooltip(true), x, y);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean val = super.mouseClicked(mouseX, mouseY, button);
        FluidSlot slot = getFluidSlotAt(mouseX, mouseY);
        boolean shifted = (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 340) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 344));
        if (slot != null) {
            this.onMouseClick(slot, slot.id, button, shifted ? SlotActionType.QUICK_MOVE : SlotActionType.PICKUP);
        }
        return val;
    }


    protected void onMouseClick(FluidSlot slot, int slotId, int button, SlotActionType actionType) {
        if (slot != null) {
            slotId = slot.id;
        }
        assert this.client != null;
        clickSlot(this.handler.syncId, slotId, button, actionType, this.client.player);
    }

    private void clickSlot(int syncId, int slotId, int button, SlotActionType actionType, ClientPlayerEntity player) {
        List<FluidStack> list = Lists.newArrayListWithCapacity(fluidHandler.getFluidSlots().size());
        for (FluidSlot fluidSlot : fluidHandler.getFluidSlots())
            list.add(fluidSlot.getStack().copy());

        fluidHandler.onFluidSlotClick(slotId, button, actionType, player);
        Map<Integer, FluidStack> modifiedStacks = new Int2ObjectOpenHashMap<>();


        for (int j = 0; j < fluidHandler.getFluidSlots().size(); ++j) {
            FluidStack originalStack = list.get(j);
            FluidStack modifiedStack = fluidHandler.getFluidSlots().get(j).getStack();
            if (!FluidStack.areEqual(originalStack, modifiedStack)) {
                modifiedStacks.put(j, modifiedStack.copy());
            }
        }

        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        assert networkHandler != null;
        networkHandler.sendPacket(new FluidClickSlotC2SPacket(syncId, slotId, button, actionType, modifiedStacks, handler.getCursorStack()));
    }
}
