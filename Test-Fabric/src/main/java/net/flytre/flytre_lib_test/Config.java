package net.flytre.flytre_lib_test;

import com.google.gson.annotations.SerializedName;
import net.flytre.flytre_lib.api.config.ConfigEventAcceptor;
import net.flytre.flytre_lib.api.config.annotation.DisplayName;

@DisplayName("config.aim_plus")
public class Config implements ConfigEventAcceptor {


    @SerializedName("max_distance")
    public double maxDistance = 60;


}
