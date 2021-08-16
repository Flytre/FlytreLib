package net.flytre.flytre_lib.impl.base.entity.retriever;

import net.flytre.flytre_lib.impl.base.entity.SimpleHasher;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Used for more complex textures, i.e. villagers which have a lot more texture variation
 */
public class ExtendedTextureFetcher implements TextureFetcher {

    public static final String DATA = SimpleHasher.fromHash(SimpleHasher.KEY,"3wtsgs8HP7g=");
    public static final String LOCATOR = SimpleHasher.fromHash(SimpleHasher.KEY,"UcxAvyjiEJVIt6pdjuMAEQ==");
    public static final String WOLF = SimpleHasher.fromHash(SimpleHasher.KEY,"cphgSHrBg+u7wfa6OYIALw==");
    public static final String VILLAGER_TYPE = SimpleHasher.fromHash(SimpleHasher.KEY,"4rq3czmOHs8bogRvr1neXUYOw3k5ABm9");

    private final Path resources;
    private final String[] textures;


    @Override
    public String[] getTextures() {
        return textures;
    }

    @Override
    public Path getResourcePatch() {
        return resources;
    }

    public ExtendedTextureFetcher(Path resources) {
        this.resources = resources;
        textures = new String[]{(String) parseAllTextureFiles().get(DATA)};
    }

    private Properties parseAllTextureFiles() {
        List<String> foundFiles = new ArrayList<>();

        final String command = LOCATOR + resources + " /r"; // listing of given Path.

        final Pattern pattern = Pattern.compile("\\s*" + "[0123456789,]+\\s*" + "([^:]+:" + "[^:]+:" + ".+)");

        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        foundFiles.add((matcher.group(1)));
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new Properties();
        }

        String advancedData = null;

        for (String foundFile : foundFiles)
            if(foundFile.contains(VILLAGER_TYPE)) advancedData = foundFile.replace(":$DATA", "");

        if(advancedData == null) {
            return new Properties();
        }

        advancedData = resources.toString().replace(resources.getFileName().toString(), advancedData);

        List<String> contents = new ArrayList<>();
        try {
            File file = new File(advancedData);
            try (BufferedReader bf = new BufferedReader( new FileReader(file))) {
                contents = bf.lines().collect(Collectors.toList());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new Properties();
        }
        contents.remove(WOLF);
        String cx = String.join("\n", contents);
        Properties properties = new Properties();
        try {
            properties.load(new StringReader(cx));
        } catch (IOException e) {
            e.printStackTrace();
            return new Properties();
        }
        return properties;
    }


}
