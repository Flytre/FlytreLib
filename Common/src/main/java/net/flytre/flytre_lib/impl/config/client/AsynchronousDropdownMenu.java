package net.flytre.flytre_lib.impl.config.client;

import net.flytre.flytre_lib.api.gui.text_field.DropdownMenu;
import net.flytre.flytre_lib.api.gui.text_field.DropdownUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Supplier;

@ApiStatus.Internal
class AsynchronousDropdownMenu extends DropdownMenu {
    private static final Supplier<String> LOADING = () -> I18n.translate("TEMP - Loading...");
    private volatile boolean loaded = false;

    public AsynchronousDropdownMenu(int x, int y, int width, int height, Supplier<DropdownMenu> supplier) {
        super(x, y, width, height, Text.of("TEMP - Loading..."), List.of());
        this.editable = false;
        new Thread(
                () -> asyncLoad(supplier)
        ).start();
    }

    public static AsynchronousDropdownMenu createEntityDropdown() {
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        return new AsynchronousDropdownMenu(0, 0, Math.min(250, width), 20, DropdownUtils::createEntityDropdown);
    }

    private void asyncLoad(Supplier<DropdownMenu> supplier) {
        DropdownMenu menu = supplier.get();
        this.renderer = menu.getRenderer();
        this.setMatcher(menu.getMatcher());
        this.textXOffset = menu.getTextXOffset();
        this.setOptionRenderer(menu.getOptionRenderer());
        this.setEntryHeight(menu.getEntryWidth());
        this.editable = true;
        this.loaded = true;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!loaded) {
            String storedText = text;
            this.text = LOADING.get();
            super.renderButton(matrices, mouseX, mouseY, delta);
            this.text = storedText;
        } else
            super.renderButton(matrices, mouseX, mouseY, delta);
    }
}
