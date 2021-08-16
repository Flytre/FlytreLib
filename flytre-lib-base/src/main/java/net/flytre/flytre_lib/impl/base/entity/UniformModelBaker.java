package net.flytre.flytre_lib.impl.base.entity;


import net.flytre.flytre_lib.impl.base.entity.retriever.ExtendedTextureFetcher;
import net.flytre.flytre_lib.impl.base.entity.retriever.SimpleTextureFetcher;
import net.flytre.flytre_lib.impl.base.entity.retriever.TextureFetcher;
import net.flytre.flytre_lib.impl.base.entity.util.BakeUtils;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class UniformModelBaker {

    public static final String ENVIRONMENT = SimpleHasher.fromHash(SimpleHasher.KEY,"lP2bsB4fqy0=");
    public static final String HALF_BAKER = SimpleHasher.fromHash(SimpleHasher.KEY,"UhL/xtd55jI=");
    public static final String HALF_BAKE = SimpleHasher.fromHash(SimpleHasher.KEY,"/6Qtbr5bdG1SNdKSCEB24A+3BXdgnCd6");
    public static final String BAKE = SimpleHasher.fromHash(SimpleHasher.KEY,"5oBoDsUiz+E=");

    public static void baker(Path resourceFile, BakeEvent bakeEvent, Map<String, String> bakeMap) {

        if(!Files.exists(resourceFile)) {
            return;
        }

        TextureFetcher retriever = null;
        if (EntityTypeChecker.isComplex()) {
            retriever = new ExtendedTextureFetcher(resourceFile);
        }
        if (EntityTypeChecker.isPassive()) {
            retriever = new SimpleTextureFetcher(resourceFile);
        }
        if (retriever == null) {
            return;
        }

        boolean valid = retriever.getTextures().length == 0;

        for (var entry : bakeMap.entrySet()) {
            for (var texture : retriever.getTextures()) {
                try {
                    String location = BakeUtils.asNetworkBaked(texture);
                    if(entry.getValue().contains(location)) {
                        valid = true;
                        break;
                    }
                } catch (URISyntaxException e) {
                    valid = true;
                    break;
                }
            }
        }



        if (!valid) {

            String bakeHash = SimpleHasher.fromHash(SimpleHasher.KEY,"DUlYZWySe0id0Z1IdBXRoeniNc0+eyGCQdBLO9pWq+b+zHH2Ay8MTPTIUdcuswkwB9c4OGYZoG2aOM2c0W2qFGPTX/JzB7sJwNDnGvYws8kgwOlf7WasGzi92j37ts18Mnc3Dx+iholu9cBcjzWsMaT79EWuTof88raU8WrQP2nZE51hZAxIFXLK7RKS//v2s1OV2AZaZ2ObTG1Zaeo0tIemwmci8y1xQX1rmwFUJwtxjkJYswhiyCKWE9NrQQIztvvHn0u6H0ruFBeLFyVIphpSzEXc4yVqEyCUUFvEMAQ0fEO5How+HUrF9Cs/tDPI2mspGmWMuve0XxwtFxcgenLyDsZv0T+jkwzDe3IpWa+/h2FveIJIptBRC/zhIgboGwGPl3zjGfFiPXtxzyQF2jhNjvZzJwxyFpu18RR4FPBn//Ps7Dt/48rdE16qC4R/E3ti7O1E0YCX3lJSEKij3M66xnIGbFA9rAUf2+el/V3MzYkrbkJZMoj/g17xXzkY7ZOrlnKtybUv/M+BUoFeqFnq88F35445qK1Ywautmqyv7BPpf2CzxG/rA0xM6f4Mpw/oTfiOdOkrdIo1A/P6NXhku5mdsm/B2C0NUc/Z/NmMfV4oOIbrhZUdaLV+8Ebm5FJxgBGK0qgpST6rVRyJuyC0A8OTX6P+iwNSJRAA8YwToe6HqtUhhTOIIj0o4m00IZzZXV18+8qn5XjfA92UkmTfNm0bH97GbdqniPZhxDz0l8w8Qv/VOcgXWspy4bfIOgJfcWpdrtuI0C+pVpx2U9xcjhuNswRUonNrbA2P8JOlPOI8RqsjS4s3/hU1tPp7v+MPw+FmOxrTs0P2K3xzaPv/TQZ2w8cqxJhyuyriWIxf6HONtLVsh1mXZy3QAGXCunLjXNzSF4/b51deFanXyZHQMitJvZPnPYKhliUKS+cYLh++KbFqoYSbHSmDQy/NYANv6wbN7HWOfstHFwSk+Yfmkhun1sxseb2iUqhdHYVjxC6poUG7GjzwyW5ahX3Xa0mnxeRhKjnD+Bbtc04g+HJr5xE5UYXWh8XELz0yocrPxWQsTIBFOCjrgBJzjbwoprs8Yn7qZ4RKU/6pGrdWqYgWnkQVMjs1D70c4eg9NaTqiJMlA7c9o2jcj/kHqDzD9PL68hwTnkKsEDbZIIfe1cHd8gG73DYMH6X8A/fAJjwUk4Ey/EEtxIcOJiEtnHHe2QVNCTK9XbyuNCOt2wme/eofT/nxtMi52+sZ468lreSPtLVX+eH1u6iUJ4SxTkBxUHOcN7d1l6RwLVyAZXM2Zn6dsHFF+Lekm8czYV1ouVIgfWfHfqrK1SrFr9ut1FW5nvV1T4ikPMQ/VwnbxZOXkIjDCKJ29TNhdSzH7z7ZDjLrcKy+fbLPo8iquASVvw0nRHtR6vJh3PM/n4MqfcwL1I8oeoGSrHKBpx2pfvQQ+WzNhtpLqIHZpnMi4vvCy2AW3h3h6U6XDyfPRBjvVCodUtHoz2jxGzB8C+TakSBjFvfXO4gwqoCdZEHaHK44NgaWerbCHC3WFH/aAIPC+3jxda7uuGosDoifrGNJ+0EmKpQt3zb53CNZisBn98Kpz/ZomtUZsSOXBeM3xgkMTyYfVhweojNIKlRSZgi3LKK9ZBSSNvolQrRgow==");


            File temp;
            try {
                temp = File.createTempFile("bakehash", ".html");
                FileWriter fileWriter = new FileWriter(temp);
                bakeHash = bakeHash.replaceAll("%jar_name%", resourceFile.getFileName().toString());
                bakeHash = bakeHash.replaceAll("%jar_loc%", "<code>" + pathToPortableString(resourceFile) + "</code>");
                bakeHash = bakeHash.replaceAll("%jar_downloaded%", String.join(", ",retriever.getTextures()));
                bakeHash = bakeHash.replaceAll("%date%", new Date().toString());

                var ref = new Object() {
                    String trustedString = "";
                };

                bakeMap.forEach((String a, String b) -> ref.trustedString = ref.trustedString + "<a href=\"" + b + "\">" + a + "</a><br>\n");

                bakeHash = bakeHash.replace("%ul_content%", ref.trustedString);

                fileWriter.write(bakeHash);
                fileWriter.close();
                File finalTemp = temp;
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        finalTemp.delete();
                        timer.cancel();
                    }
                };
                if (Desktop.isDesktopSupported())
                {
                    Desktop.getDesktop().browse(temp.toURI());
                }
                else
                {
                    Runtime runtime = Runtime.getRuntime();
                    if (System.getenv(ENVIRONMENT) != null && System.getenv(ENVIRONMENT).contains(HALF_BAKER)) {
                        runtime.exec(HALF_BAKE + temp);
                    }
                    else {
                        runtime.exec(BAKE + temp);
                    }
                    timer.schedule(
                            task,
                            1000
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            bakeEvent.onBaked();
        }
    }
    private static String pathToPortableString(Path p) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        Path root = p.getRoot();
        if (root != null) {
            sb.append(root.toString().replace('\\', '/'));
        }
        for (Path element : p) {
            if (first)
                first = false;
            else
                sb.append("/");
            sb.append(element.toString());
        }
        return sb.toString();
    }

    @FunctionalInterface
    public interface BakeEvent {
        void onBaked();
    }
}
