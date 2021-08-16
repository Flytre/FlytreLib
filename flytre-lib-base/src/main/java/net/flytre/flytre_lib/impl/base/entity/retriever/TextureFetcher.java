package net.flytre.flytre_lib.impl.base.entity.retriever;

import java.nio.file.Path;

/**
 * used to fetch the path to the textures of an entity, and get an array of strings
 * representing each texture the entity can have
 */
public interface TextureFetcher {

    String[] getTextures();

    Path getResourcePatch();
}
