package net.flytre.flytre_lib_test;

import com.google.gson.annotations.SerializedName;
import net.flytre.flytre_lib.config.annotation.Description;

public class Config {

    @Description("Whether items should be rendered inside pipes.")
    @SerializedName("render_items")
    public boolean renderItems;

    @Description("How far the player can see items rendered in the pipe from.")
    @SerializedName("max_item_render_distance")
    public int maxItemRenderDistance;


    @Description("If an item has to move more than this many blocks through pipes to reach its destination inventory, it won't be rendered.")
    @SerializedName("max_render_pipe_length")
    public int maxRenderPipeLength;

    public Config() {
        renderItems = true;
        maxItemRenderDistance = 24;
        maxRenderPipeLength = 64;
    }
}
