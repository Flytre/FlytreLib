package net.flytre.flytre_lib.impl.config.init;

import com.google.gson.annotations.SerializedName;
import net.flytre.flytre_lib.api.config.annotation.Description;
import net.flytre.flytre_lib.api.config.annotation.DisplayName;

@DisplayName("config.flytre_lib")
public class FlytreLibConfig {

    @SerializedName("display_title_screen_config_button")
    @Description("Whether the title screen has a button to open the config editor. Recommended on for ease of access as other methods of adjusting configs are more hidden.")
    public boolean displayTitleScreenConfigButton = true;


    @Description("Used to log in with a different account than signed in to the launcher (on game start). Mostly useful in development environments where its hard to log in. Like any 3rd-party log-in tool, don't trust it as passwords are stored unencrypted - Its mostly made for me, Flytre.")
    public Login login = new Login("", "", false);

    public static class Login {

        public String username;
        public String password;

        @SerializedName("should_login")
        public boolean shouldLogin = false;

        public Login(String username, String password, boolean shouldLogin) {
            this.username = username;
            this.password = password;
            this.shouldLogin = shouldLogin;
        }
    }
}
