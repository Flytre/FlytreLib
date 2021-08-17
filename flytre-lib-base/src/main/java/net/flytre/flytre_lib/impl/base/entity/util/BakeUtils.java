package net.flytre.flytre_lib.impl.base.entity.util;

import java.net.URI;
import java.net.URISyntaxException;

public class BakeUtils {
    public static String asNetworkBaked(String model) throws URISyntaxException {

        if(model == null)
            return "Error: Unknown URL";

        URI uri = new URI(model);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
}
