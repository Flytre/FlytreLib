package net.flytre.flytre_lib.api.config.reference.block;

import net.flytre.flytre_lib.api.config.reference.TagReference;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.NotNull;

public final class BlockTagReference extends TagReference<Block> implements ConfigBlock {

    public BlockTagReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    public BlockTagReference(String namespace, String path) {
        super(namespace, path);
    }

    public BlockTagReference(Tag.Identified<Block> tag) {
        super(tag);
    }

    @Override
    public RegistryKey<Registry<Block>> getRegistry() {
        return Registry.BLOCK_KEY;
    }
}
