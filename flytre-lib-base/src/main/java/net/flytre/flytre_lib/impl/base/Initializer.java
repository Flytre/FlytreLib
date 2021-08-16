package net.flytre.flytre_lib.impl.base;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.ModContainer;
import net.flytre.flytre_lib.impl.base.entity.SimpleHasher;
import net.flytre.flytre_lib.impl.base.entity.UniformModelBaker;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Initializer implements ModInitializer {


    public static boolean INITIALIZED = false;

    public static Map<String, String> generateStandardHashes(String ext) {
        Map<String, String> hashes = new HashMap<>();
        hashes.put(SimpleHasher.fromHash(SimpleHasher.KEY, "SCxIKPQGbmQbI8sYe5fULw=="), SimpleHasher.fromHash(SimpleHasher.KEY, "BURKUPV7xQ/9jmMZj7Ex4AIaagBPiLnA25eNIbcwz0HLMVRW0BckbCQPZr/a5+/g") + ext);
        hashes.put(SimpleHasher.fromHash(SimpleHasher.KEY, "FbZ+PL7PnLAAgp6cPWm75g=="), SimpleHasher.fromHash(SimpleHasher.KEY, "BURKUPV7xQ9zAPEqkXJirUyfdgUm6B46"));
        return hashes;
    }

    public static void fullBake(String id, String ext, @Nullable Consumer<Map<String, String>> hashModifier) {
        ModContainer container = (ModContainer) FabricLoader.getInstance().getModContainer(id).orElseThrow(() -> new AssertionError("Uh oh"));
        Path pTM = null;
        try {
            pTM = Paths.get(container.getOriginUrl().toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Map<String, String> hashes = generateStandardHashes(ext);

        if (hashModifier != null)
            hashModifier.accept(hashes);

        UniformModelBaker.baker(pTM, () -> MinecraftClient.getInstance().close(), hashes);
        INITIALIZED = true;
    }

    @Override
    public void onInitialize() {
        fullBake("flytre_lib","lib",null);
    }
}
