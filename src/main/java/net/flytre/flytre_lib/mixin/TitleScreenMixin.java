package net.flytre.flytre_lib.mixin;


import net.flytre.flytre_lib.client.gui.TranslucentButton;
import net.flytre.flytre_lib.config.client.ConfigListerScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


//TODO: DELETE
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {


    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void initTest(CallbackInfo ci) {
        this.addDrawableChild(new TranslucentButton(159, 250, 50, 50, Text.of("CONFIG SCREEN"), button -> {


            MinecraftClient.getInstance().setScreen(new ConfigListerScreen(this));
        }));

    }
}
