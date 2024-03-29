package net.flytre.flytre_lib.mixin.storage.upgrade;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.flytre.flytre_lib.api.storage.upgrade.UpgradeHandler;
import net.flytre.flytre_lib.impl.storage.upgrade.network.UpgradeClickSlotC2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

/**
 * Implements upgrade rendering onto the screen
 */
@Mixin(HandledScreen.class)
abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {

    @Unique
    private static final Identifier UPGRADE = new Identifier("flytre_lib:textures/gui/container/upgrade.png");
    @Shadow
    @Final
    protected T handler;
    @Shadow
    protected int backgroundWidth;
    @Shadow
    protected int x;
    @Shadow
    protected int y;
    @Shadow
    @Nullable
    protected Slot focusedSlot;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Shadow
    protected abstract boolean isPointOverSlot(Slot slot, double pointX, double pointY);

    @Shadow
    protected abstract void drawSlot(MatrixStack matrices, Slot slot);

    @Inject(method = "getSlotAt", at = @At("TAIL"), cancellable = true)
    public void flytre_lib$getUpgradeSlotAt(double xPosition, double yPosition, CallbackInfoReturnable<Slot> cir) {
        if (handler instanceof UpgradeHandler) {
            UpgradeHandler handler = (UpgradeHandler) this.handler;
            for (int i = 0; i < handler.getUpgradeSlots().size(); ++i) {
                Slot slot = handler.getUpgradeSlots().get(i);
                if (this.isPointOverSlot(slot, xPosition, yPosition) && slot.isEnabled()) {
                    cir.setReturnValue(slot);
                }
            }
        }
    }


    /**
     * Standard Upgrade Slot rendering
     */
    @Inject(method = "render", at = @At("HEAD"))
    public void flytre_lib$renderQuadUpgradePanel(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (handler instanceof UpgradeHandler && ((UpgradeHandler) handler).getUpgradeSlots().size() == 4) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, UPGRADE);
            this.drawTexture(matrices, x + backgroundWidth, y + 70, 0, 0, 65, 65);
        }
    }


    /**
     * Standard Upgrade Slot rendering
     */
    @Inject(method = "isClickOutsideBounds", at = @At("HEAD"), cancellable = true)
    public void flytre_lib$inBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> cir) {
        if (handler instanceof UpgradeHandler && ((UpgradeHandler) handler).getUpgradeSlots().size() == 4 && mouseX >= (double) left && mouseY >= (double) top + 70 && mouseX < (double) (left + this.backgroundWidth + 65) && mouseY <= (double) (top + 135)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawForeground(Lnet/minecraft/client/util/math/MatrixStack;II)V", shift = At.Shift.BEFORE))
    public void flytre_lib$upgradeHandledScreenRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (handler instanceof UpgradeHandler) {
            UpgradeHandler handler = (UpgradeHandler) this.handler;
            int r;
            for (int m = 0; m < handler.getUpgradeSlots().size(); ++m) {
                Slot slot = handler.getUpgradeSlots().get(m);
                if (slot.isEnabled()) {
                    drawSlot(matrices, slot);
                }

                if (isPointOverSlot(slot, mouseX, mouseY) && slot.isEnabled()) {
                    focusedSlot = slot;
                    RenderSystem.disableDepthTest();
                    int n = slot.x;
                    r = slot.y;
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

                    RenderSystem.colorMask(true, true, true, false);
                    this.fillGradient(matrices, n, r, n + 16, r + 16, -2130706433, -2130706433);
                    RenderSystem.colorMask(true, true, true, true);
                    RenderSystem.enableDepthTest();
                }
            }
        }
    }


    @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", at = @At("HEAD"), cancellable = true)
    public void flytre_lib$onUpgradeSlotClicked(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (handler instanceof UpgradeHandler && slot != null && ((UpgradeHandler) handler).getUpgradeSlots().stream().anyMatch(i -> i == slot)) {
            slotId = slot.id;
            assert this.client != null;
            flytre_lib$clickSlot(this.handler.syncId, slotId, button, actionType, this.client.player);
            ci.cancel();
        }
    }

    @Unique
    private void flytre_lib$clickSlot(int syncId, int slotId, int button, SlotActionType actionType, ClientPlayerEntity player) {
        UpgradeHandler handler = (UpgradeHandler) this.handler;
        List<ItemStack> list = Lists.newArrayListWithCapacity(handler.getUpgradeSlots().size());
        for (var slot : handler.getUpgradeSlots())
            list.add(slot.getStack().copy());

        handler.onUpgradeSlotClick(slotId, button, actionType, player);
        Map<Integer, ItemStack> modifiedStacks = new Int2ObjectOpenHashMap<>();


        for (int j = 0; j < handler.getUpgradeSlots().size(); ++j) {
            ItemStack originalStack = list.get(j);
            ItemStack modifiedStack = handler.getUpgradeSlots().get(j).getStack();
            if (!ItemStack.areEqual(originalStack, modifiedStack))
                modifiedStacks.put(j, modifiedStack.copy());
        }

        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        assert networkHandler != null;
        networkHandler.sendPacket(new UpgradeClickSlotC2SPacket(syncId, slotId, button, actionType, modifiedStacks, this.handler.getCursorStack().copy()));
    }
}
