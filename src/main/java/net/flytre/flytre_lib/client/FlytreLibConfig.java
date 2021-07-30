package net.flytre.flytre_lib.client;

import com.google.gson.annotations.SerializedName;
import net.flytre.flytre_lib.config.Description;
import net.flytre.flytre_lib.config.DisplayName;

@DisplayName("config.flytre_lib")
public class FlytreLibConfig {

    @SerializedName("display_title_screen_config_button")
    @Description("Whether the title screen has a button to open the config editor. Recommended on for ease of access as other methods of adjusting configs are more hidden.")
    public boolean displayTitleScreenConfigButton = true;
}
