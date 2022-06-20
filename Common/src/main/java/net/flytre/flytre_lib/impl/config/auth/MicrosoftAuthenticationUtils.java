package net.flytre.flytre_lib.impl.config.auth;

import com.google.gson.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Adapted from Wurst
 */
public final class MicrosoftAuthenticationUtils {

    private static final String CLIENT_ID = "00000000402b5328"; //Use Wurst Client Id
    private static final String SCOPE_ENCODED =
            "service%3A%3Auser.auth.xboxlive.com%3A%3AMBI_SSL";
    private static final String SCOPE_UNENCODED =
            "service::user.auth.xboxlive.com::MBI_SSL";
    private static final String REDIRECT_URI_ENCODED =
            "https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf";
    private static final URL LOGIN_URL =
            createURL("https://login.live.com/oauth20_authorize.srf?client_id="
                    + CLIENT_ID + "&response_type=code&scope=" + SCOPE_ENCODED
                    + "&redirect_uri=" + REDIRECT_URI_ENCODED);
    private static final URL AUTH_TOKEN_URL =
            createURL("https://login.live.com/oauth20_token.srf");
    private static final URL XBL_TOKEN_URL =
            createURL("https://user.auth.xboxlive.com/user/authenticate");
    private static final URL XSTS_TOKEN_URL =
            createURL("https://xsts.auth.xboxlive.com/xsts/authorize");
    private static final URL MC_TOKEN_URL = createURL(
            "https://api.minecraftservices.com/authentication/login_with_xbox");
    private static final URL PROFILE_URL =
            createURL("https://api.minecraftservices.com/minecraft/profile");
    /**
     * Expected data: <code>sFTTag: '&lt;input type="hidden" name="PPFT"
     * id="12345" value="random stuff"/&gt;'</code>
     *
     * <p>
     * This is all inside a long &lt;script&gt; tag on the {@link #LOGIN_URL}
     * webpage.
     */
    private static final Pattern PPFT_REGEX =
            Pattern.compile("sFTTag:[ ]?'.*value=\"(.*)\"/>");
    /**
     * Expected data: <code>urlPost: 'https://login.live.com/...'</code>
     *
     * <p>
     * This appears earlier in the same &lt;script&gt; tag.
     */
    private static final Pattern URLPOST_REGEX =
            Pattern.compile("urlPost:[ ]?'(.+?(?='))");
    private static final Pattern AUTHCODE_REGEX =
            Pattern.compile("[?|&]code=([\\w.-]+)");

    private MicrosoftAuthenticationUtils() {

    }

    public static MinecraftAccountInfo login(String email, String password)
            throws LoginException {

        return getAccount(email, password);
    }

    private static MinecraftAccountInfo getAccount(String email, String password)
            throws LoginException {
        System.out.println("Logging in with Microsoft...");
        long startTime = System.nanoTime();

        try {
            String authCode = getAuthorizationCode(email, password);
            String msftAccessToken = getMicrosoftAccessToken(authCode);

            XBoxLiveToken xblToken = getXBLToken(msftAccessToken);
            String xstsToken = getXSTSToken(xblToken.getToken());

            String mcAccessToken =
                    getMinecraftAccessToken(xblToken.getUHS(), xstsToken);

            MinecraftAccountInfo mcProfile = getMinecraftAccountInfo(mcAccessToken);

            System.out.println("Login successful after "
                    + (System.nanoTime() - startTime) / 1e6D + " ms");

            return mcProfile;

        } catch (LoginException e) {
            System.out.println("Login failed after "
                    + (System.nanoTime() - startTime) / 1e6D + " ms");

            e.printStackTrace();
            throw e;
        }
    }

    private static String getAuthorizationCode(String email, String password)
            throws LoginException {
        String cookie;
        String loginWebpage;

        try {
            URLConnection connection = LOGIN_URL.openConnection();

            System.out.println("Getting login cookie...");
            cookie = connection.getHeaderField("set-cookie");

            System.out.println("Downloading login page...");
            loginWebpage = downloadData(connection);

        } catch (IOException e) {
            throw new LoginException("Connection failed: " + e, e);
        }

        System.out.println("Getting PPFT and urlPost...");

        Matcher matcher = PPFT_REGEX.matcher(loginWebpage);
        if (!matcher.find())
            throw new LoginException("sFTTag / PPFT regex failed.");

        String ppft = matcher.group(1);

        matcher = URLPOST_REGEX.matcher(loginWebpage);
        if (!matcher.find())
            throw new LoginException("urlPost regex failed.");

        String urlPost = matcher.group(1);

        return microsoftLogin(email, password, cookie, ppft, urlPost);
    }

    private static String microsoftLogin(String email, String password,
                                         String cookie, String ppft, String urlPost) throws LoginException {
        Map<String, String> postData = new HashMap<>();
        postData.put("login", email);
        postData.put("loginfmt", email);
        postData.put("passwd", password);
        postData.put("PPFT", ppft);

        byte[] encodedDataBytes =
                urlEncodeMap(postData).getBytes(StandardCharsets.UTF_8);

        try {
            URL url = new URL(urlPost);
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setRequestProperty("Content-Length",
                    "" + encodedDataBytes.length);
            connection.setRequestProperty("Cookie", cookie);

            connection.setDoInput(true);
            connection.setDoOutput(true);

            System.out.println("Getting authorization code...");

            try (OutputStream out = connection.getOutputStream()) {
                out.write(encodedDataBytes);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode >= 500 && responseCode <= 599)
                throw new LoginException(
                        "Servers are down (code " + responseCode + ").");

            if (responseCode != 200)
                throw new LoginException(
                        "Got code " + responseCode + " from urlPost.");

            String decodedUrl = URLDecoder.decode(
                    connection.getURL().toString(), StandardCharsets.UTF_8.name());

            Matcher matcher = AUTHCODE_REGEX.matcher(decodedUrl);
            if (!matcher.find())
                throw new LoginException(
                        "Didn't get authCode. (Wrong email/password?)");

            return matcher.group(1);

        } catch (IOException e) {
            throw new LoginException("Connection failed: " + e, e);
        }
    }

    private static String getMicrosoftAccessToken(String authCode)
            throws LoginException {
        Map<String, String> postData = new HashMap<>();
        postData.put("client_id", CLIENT_ID);
        postData.put("code", authCode);
        postData.put("grant_type", "authorization_code");
        postData.put("redirect_uri",
                "https://login.live.com/oauth20_desktop.srf");
        postData.put("scope", SCOPE_UNENCODED);

        byte[] encodedDataBytes =
                urlEncodeMap(postData).getBytes(StandardCharsets.UTF_8);

        try {
            HttpURLConnection connection =
                    (HttpURLConnection) AUTH_TOKEN_URL.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded; charset=UTF-8");

            connection.setDoOutput(true);

            System.out.println("Getting Microsoft access token...");

            try (OutputStream out = connection.getOutputStream()) {
                out.write(encodedDataBytes);
            }

            JsonElement json = parseConnection(connection);
            if (!json.isJsonObject())
                throw new JsonException();
            if (!json.getAsJsonObject().has("access_token"))
                throw new JsonException();

            return json.getAsJsonObject().get("access_token").getAsString();

        } catch (IOException e) {
            throw new LoginException("Connection failed: " + e, e);

        } catch (JsonException e) {
            throw new LoginException("Server sent invalid JSON.", e);
        }
    }

    private static XBoxLiveToken getXBLToken(String msftAccessToken)
            throws LoginException {
        JsonObject properties = new JsonObject();
        properties.addProperty("AuthMethod", "RPS");
        properties.addProperty("SiteName", "user.auth.xboxlive.com");
        properties.addProperty("RpsTicket", msftAccessToken);

        JsonObject postData = new JsonObject();
        postData.addProperty("RelyingParty", "http://auth.xboxlive.com");
        postData.addProperty("TokenType", "JWT");
        postData.add("Properties", properties);

        String request = postData.toString();

        try {
            URLConnection connection = XBL_TOKEN_URL.openConnection();

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            connection.setDoOutput(true);

            System.out.println("Getting X-Box Live token...");

            try (OutputStream out = connection.getOutputStream()) {
                out.write(request.getBytes(StandardCharsets.US_ASCII));
            }

            JsonElement json = parseConnection(connection);
            if (!json.isJsonObject())
                throw new JsonException();


            String token = json.getAsJsonObject().get("Token").getAsString();
            String uhs = json
                    .getAsJsonObject()
                    .getAsJsonObject("DisplayClaims")
                    .getAsJsonArray("xui")
                    .get(0)
                    .getAsJsonObject()
                    .get("uhs")
                    .getAsString();

            return new XBoxLiveToken(token, uhs);

        } catch (IOException e) {
            throw new LoginException("Connection failed: " + e, e);

        } catch (JsonException e) {
            throw new LoginException("Server sent invalid JSON.", e);
        }
    }

    private static String getXSTSToken(String xblToken) throws LoginException {
        JsonArray tokens = new JsonArray();
        tokens.add(xblToken);

        JsonObject properties = new JsonObject();
        properties.addProperty("SandboxId", "RETAIL");
        properties.add("UserTokens", tokens);

        JsonObject postData = new JsonObject();
        postData.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
        postData.addProperty("TokenType", "JWT");
        postData.add("Properties", properties);

        String request = postData.toString();

        try {
            URLConnection connection = XSTS_TOKEN_URL.openConnection();

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            connection.setDoOutput(true);

            System.out.println("Getting XSTS token...");

            try (OutputStream out = connection.getOutputStream()) {
                out.write(request.getBytes(StandardCharsets.US_ASCII));
            }

            JsonElement json = parseConnection(connection);
            return json.getAsJsonObject().get("Token").getAsString();

        } catch (IOException e) {
            throw new LoginException("Connection failed: " + e, e);

        } catch (JsonException e) {
            throw new LoginException("Server sent invalid JSON.", e);
        }
    }

    private static String getMinecraftAccessToken(String uhs, String xstsToken)
            throws LoginException {
        JsonObject postData = new JsonObject();
        postData.addProperty("identityToken",
                "XBL3.0 x=" + uhs + ";" + xstsToken);

        String request = postData.toString();

        try {
            URLConnection connection = MC_TOKEN_URL.openConnection();

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            connection.setDoOutput(true);

            System.out.println("Getting Minecraft access token...");

            try (OutputStream out = connection.getOutputStream()) {
                out.write(request.getBytes(StandardCharsets.US_ASCII));
            }

            JsonElement json = parseConnection(connection);
            if (!json.isJsonObject())
                throw new JsonException();
            if (!json.getAsJsonObject().has("access_token"))
                throw new JsonException();

            return json.getAsJsonObject().get("access_token").getAsString();

        } catch (IOException e) {
            throw new LoginException("Connection failed: " + e, e);

        } catch (JsonException e) {
            throw new LoginException("Server sent invalid JSON.", e);
        }
    }

    private static MinecraftAccountInfo getMinecraftAccountInfo(String mcAccessToken)
            throws LoginException {
        try {
            URLConnection connection = PROFILE_URL.openConnection();
            connection.setRequestProperty("Authorization",
                    "Bearer " + mcAccessToken);

            System.out.println("Getting UUID and name...");
            JsonElement json = parseConnection(connection);
            JsonObject jsonObject = json.getAsJsonObject();

            if (jsonObject.has("error"))
                throw new LoginException(
                        "Error message from api.minecraftservices.com:\n"
                                + jsonObject.get("error"));

            UUID uuid = uuidFromJson(jsonObject.get("id").getAsString());
            String name = jsonObject.get("name").getAsString();

            return new MinecraftAccountInfo(name,uuid.toString(), mcAccessToken);

        } catch (IOException e) {
            throw new LoginException("Connection failed: " + e, e);

        } catch (JsonException e) {
            throw new LoginException("Server sent invalid JSON.", e);
        }
    }

    private static String urlEncodeMap(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (sb.length() > 0)
                sb.append("&");

            sb.append(
                    URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));

            sb.append("=");

            sb.append(
                    URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        return sb.toString();
    }

    private static UUID uuidFromJson(String jsonUUID) throws JsonException {
        try {
            String withDashes = jsonUUID.replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                    "$1-$2-$3-$4-$5");

            return UUID.fromString(withDashes);

        } catch (IllegalArgumentException e) {
            throw new JsonException("Invalid UUID.", e);
        }
    }

    private static String downloadData(URLConnection connection)
            throws IOException {
        try (InputStream input = connection.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(input);
            BufferedReader bufferedReader = new BufferedReader(reader);
            return bufferedReader.lines().collect(Collectors.joining("\n"));
        }
    }

    private static URL createURL(String url) {
        try {
            return new URL(url);

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static JsonElement parseConnection(URLConnection connection)
            throws IOException, JsonException {
        try (InputStream input = connection.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(input);
            BufferedReader bufferedReader = new BufferedReader(reader);
            return JsonParser.parseReader(bufferedReader);

        } catch (JsonParseException e) {
            throw new JsonException(e);
        }
    }

}
